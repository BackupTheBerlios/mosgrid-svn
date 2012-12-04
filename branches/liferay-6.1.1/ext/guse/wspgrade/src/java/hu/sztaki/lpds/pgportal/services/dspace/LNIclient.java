/* Copyright 2007-2011 MTA SZTAKI LPDS, Budapest

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */
/*
 * Copyright (c) 2008, Hewlett-Packard Company and Massachusetts
 * Institute of Technology.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * - Neither the name of the Hewlett-Packard Company nor the name of the
 * Massachusetts Institute of Technology nor the names of their
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */
//
package hu.sztaki.lpds.pgportal.services.dspace;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

/**
 * Minimal LNI client for remote submission.
 * ONLY implements PUT and maybe GET of Items.
 * <p>
 * Requires Sun Java JRE 5 and these libraries:
 * <ul>
 * <li> Apache Jakarta Commons HTTPClient
 *  <br><tt>   http://hc.apache.org/httpclient-3.x/ </tt>
 *
 * <li> Apache Commons Codec (required by HTTPClient)
 *
 * <li>  Apache Commons Logging (required by HTTPClient)
 *  <br> <tt>  http://commons.apache.org/logging/ </tt>
 *
 * <li> Apache Commons CLI
 * </ul>
 * <p>Usage:
 * <p>
 * Here is a sample of how it is called to ingest an Item into a collection:
 * <pre>
 *   String collection = "some-handle";
 *   LNIclient lni = new LNIclient("http://mydspace:8080/lni/dav/",
 *                                 "eperson@my.edu", "password");
 *   OutputStream os = lni.startPut(collection, "METS", null);
 *   your-code-to-write-METS-package-to-stream(os);
 *   os.close();
 *   String newItemhandle = lni.finishPut();
 * </pre>
 * @author Larry Stone
 * Version: $Revision: 1.1 $
 * Date: $Date: 2009/05/27 12:48:26 $
 */
public class LNIclient
    implements Runnable
{
    private String serverURL = null;

    private HttpClient client = null;

    // state to save between startPUT() and finishPUT()
    private PutMethod lastPut = null;
    private GetMethod lastGet = null;
    private Thread lastPutThread = null;

    // status of last PUT operation (or GET if that is implemented)
    private int lastStatus = -1;

    // HTTP Request body for PROPFIND
    private static final String propfindBody =
        "<propfind xmlns=\"DAV:\">\n"+
        " <prop xmlns:dspace=\"http://www.dspace.org/xmlns/dspace\">\n"+
        "  <displayname/>\n"+
        "  <dspace:handle/>\n"+
        " </prop>\n"+
        "</propfind>";

    /**
     * Adds a PROPFIND method to Apache Commons HTTPClient library.
     * It's logically just like a POST request, so we can just extend
     * that class and change its protocol verb (getName()).
     */
    private static class PropfindMethod extends PostMethod
    {
        private String body;

        /**
         * @param uri target WebDAV resource
         * @param body  WebDAV XML request body
         */
        public PropfindMethod(String uri, String body)
        {
            super(uri);
            this.body = body;
        }

        public String getName()
        {
            return "PROPFIND";
        }

        protected RequestEntity generateRequestEntity()
        {
            try
            {
                return new StringRequestEntity(body, "text/xml", null);
            }
            catch (UnsupportedEncodingException e)
            {
                return null;
            }
        }
    }

    // SAX handler to get DSpace "Handle" value out of PROPFIND results
    private static class PropfindHandler
        extends DefaultHandler
    {
        String handle = null;

        protected String textValue = null;

        // NOTE:  text value MAY be presented in multiple calls, even if
        // it all one word, so be ready to splice it together.
        // BEWARE:  subclass's startElement method should call super()
        // to null out 'value'.  (Don't you miss the method combination
        // options of a real object system like CLOS?)
        public void characters(char[] ch, int start, int length)
            throws SAXException
        {
            String newValue = new String(ch, start, length);
            if (newValue.length() > 0)
            {
                if (textValue == null)
                    textValue = newValue;
                else
                    textValue += newValue;
            }
        }

        // save contents of FormatID and PUID to put them in a map
        // entry when FileType is closed.
        public void endElement(String namespaceURI, String localName,
                                 String qName)
            throws SAXException
        {
            if (localName.equals("handle"))
                handle = textValue.trim();
        }

        // subclass overriding this MUST call it with super()
        public void startElement(String namespaceURI, String localName,
                                 String qName, Attributes atts)
            throws SAXException
        {
            // XXX NOTE: this only works because we only care
            // about the simple text element dspace:handle
            textValue = null;
        }

        public void error(SAXParseException exception)
            throws SAXException
        {
            throw new SAXException(exception);
        }

        public void fatalError(SAXParseException exception)
            throws SAXException
        {
            throw new SAXException(exception);
        }
    }

    /**
     * Constructs a logical "connection" to an a specific DSpace LNI
     * server with given credentials.
     * <p>
     * This constructor assumes HTTP Basic Authentication, although it
     * should also work just as well for HTTPS with X.509 client
     * certificates -- since credentials are handled by the Java
     * Security infrastructure, accessed directly by HTTPClient.
     *
     * @param url the DSpace LNI root resource
     * @param eperson email address of DSpace user, or null if unused.
     * @param password password of DSpace user, or null if unused.
     */
    public LNIclient(String url, String eperson, String password)
    {

        super();
//HG test
        //System.out.println(">> LNIclient.Constructor url=" + url + " <<");
//HG test end
        this.serverURL = url;
        this.client = new HttpClient();
        if (eperson != null && password != null)
            client.getState().setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(eperson, password));
    }

    // Utility to emit a warning message.
    // change this to connect to a logger if necessary.
    private static void warn(String msg)
    {
        System.err.println("WARNING: "+msg);
    }

    /**
     * Utility - perform LNI lookup of a DSpace Handle and return
     * the fully-qualified LNI URL of the corresponding resource.
     */
    private String lookupHandle(String handle)
        throws IOException, HttpException
    {
//HG test

//        String HGstring = serverURL + "/lookup/handle/"+handle;
//        System.out.println("LNIClient.java:lookupHandle: - get arg=" + HGstring);
//HG test end

        GetMethod get = new GetMethod(serverURL+"/lookup/handle/"+handle);

        //System.out.println(">> ++1 <<");
        get.setDoAuthentication(true);

        //System.out.println(">> ++2 <<");
        get.setFollowRedirects(false);
        //System.out.println(">> ++3 <<");
        try
        {
            //System.out.println(">> ++4 <<");
            int status = client.executeMethod(get);
            //System.out.println(">> ++5 <<");
            if (status >= 300 && status < 400)
            {
                //System.out.println(">> ++6 <<");
                Header loc = get.getResponseHeader("Location");
                if (loc != null)
                {
                    //System.out.println(">> ++7 <<");
                    return loc.getValue();
                }
                else
                {
                    //System.out.println(">> ++8 <<");
                    throw new IOException("lookupHandle returns success but Location header was missing.");
                }
            }
            else
            {
                //System.out.println(">> ++9 <<");
                throw new IOException("lookupHandle request status="+status+", msg="+
                     get.getStatusText());
            }
        }
        finally
        {
            //System.out.println(">> ++10 <<");
            get.releaseConnection();
        }
    }

    /**
     * Starts a two-stage WebDAV PUT operation, which gives the caller
     * an OutputStream on which to write the body.  The expected
     * sequence is:  call startPut(), write the body, close the stream,
     * and then call finishPut() to obtain the Handle of the
     * newly-created resource.
     * <p>
     * The actual PUT method is executed in a separate thread since it
     * has to read data from the pipe attached to the returned
     * OutputStream, and this thread must write to that OutputStream.
     * <p>
     * Since the LNI only submits Items, the target must be a
     * collection.
     *
     * @param collection Handle of the target, i.e. collection into which Item is submitted
     * @param type Package type, actually the name of package ingester plugin on the server.
     * @param options other HTTP options which are passed to package ingester plugin
     * @return an OutputStream on which the request body is written, it then MUST be closed.
     */
    public OutputStream startPut(String collection, String type, NameValuePair options[])
        throws IOException, HttpException
    {
        PipedOutputStream out = new PipedOutputStream();
        PipedInputStream in = new PipedInputStream(out);
        startPutInternal(collection, type, options, in);
        lastPutThread = new Thread(this);
        lastPutThread.start();
        return out;
    }

    /**
     * Set up and initiate the PUT method, but leave the actual
     * technique of writing the request body to the caller.
     */
    private void startPutInternal(String collection, String type,
                         NameValuePair options[], InputStream is)
        throws IOException, HttpException
    {
        if (lastPut != null)
            throw new IOException("Bad state: startPUT called twice without finishPUT.");

        String url = lookupHandle(collection);

        NameValuePair args[] = new NameValuePair[1+(options == null ? 0:options.length)];
        args[0] = new NameValuePair("package", type);
        if (options != null)
        {
            for (int i = 0; i < options.length; ++i)
                args[i+1] = options[i];
        }
        lastPut = new PutMethod(url);
        lastPut.setDoAuthentication(true);
        lastPut.setQueryString(args);
        lastPut.setRequestEntity(new InputStreamRequestEntity(is, -1));
    }

    // do the HTTP PUT, and ensure the method closes its request body stream.
    private void executePut()
    {
        try
        {
            client.executeMethod(lastPut);
        }
        catch (IOException e)
        {
            warn("Exception in PUT: "+e);
        }
        finally
        {
            lastPut.releaseConnection();
        }
    }

    /**
     * Completes the two-part operation started by startPut(), by
     * collecting status of the PUT operation and converting the
     * WebDAV URI of the newly-created resource back to a Handle,
     * which it returns.
     * <p>
     * Any failure results in an exception.
     *
     * @return Handle of the newly-created DSpace resource.
     */
    public String finishPut()
        throws InterruptedException, IOException,
        SAXException, SAXNotRecognizedException, ParserConfigurationException
    {
        if (lastPutThread != null)
        {
            lastPutThread.join();
            lastPutThread = null;
        }

        Header loc = lastPut.getResponseHeader("Location");
        lastStatus = lastPut.getStatusCode();
        if (lastStatus < 100 || lastStatus >= 400)
            throw new IOException("PUT returned status = "+lastStatus+"; text="+lastPut.getStatusText());

        lastPut = null;
        if (loc != null)
        {
            String newURL = loc.getValue();

            // do a quick PROPFIND to get the handle
            PropfindMethod pf = new PropfindMethod(newURL, propfindBody);
            pf.setDoAuthentication(true);
            client.executeMethod(pf);

            int pfStatus = pf.getStatusCode();
            if (pfStatus < 200 || pfStatus >= 300)
                throw new IOException("finishPut.propfind got status = "+pfStatus+"; text="+pf.getStatusText());

            // Maybe move all this crap to within Propfind class??
            // so it can get the inputstream directly?
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            PropfindHandler handler = new PropfindHandler();

            // XXX FIXME: should turn off validation here explicitly, but
            //  it seems to be off by default.
            xr.setFeature("http://xml.org/sax/features/namespaces", true);
            xr.setContentHandler(handler);
            xr.setErrorHandler(handler);
            xr.parse(new InputSource(pf.getResponseBodyAsStream()));

            return handler.handle;
        }
        else
            throw new IOException("PUT response was missing a Location: header.");
    }

    /**
     * Thread body that executes the PUT operation.
     */
    public void run()
    {
        executePut();
    }

    /**
     * Conducts complete synchronous WebDAV PUT operation, sending the data from the
     * given InputStream to the indicated DSpace collection.  The stream
     * must contain a package of the indicated type
     * <p>
     * Any failure results in an exception.
     *
     * @param collection Handle of the target, i.e. collection into which Item is submitted
     * @param type Package type, actually the name of package ingester plugin on the server.
     * @param options other HTTP options which are passed to package ingester plugin
     * @return Handle of the newly-created DSpace resource.
     */
    public String put(String collection, String type,
                         NameValuePair options[], InputStream is)
        throws InterruptedException, IOException, HttpException,
               SAXException, SAXNotRecognizedException, ParserConfigurationException
    {
        startPutInternal(collection, type, options, is);
        executePut();
        return finishPut();
    }

    /**
     * Returns the HTTP status of last GET or PUT operation.
     * @return Numeric HTTP status code
     */
    public int getLastStatus()
    {
        return lastStatus;
    }

    /**
     * Starts a synchronous WebDAV GET operation, returning an
     * InputStream carrying the contents of the resource (e.g. an Item
     * as a DSpace DIP).
     * <p>
     * This MUST be followed by a call to finishGet() to close the
     * client connection.
     * <p>
     * Any failure results in an exception.
     *
     * @param collection Handle of the target, i.e. collection into which Item is submitted
     * @param type Package type, actually the name of package ingester plugin on the server.
     * @param options other HTTP options which are passed to package ingester plugin
     * @return Handle of the newly-created DSpace resource.
     */
    public InputStream startGet(String handle, String type,
                         NameValuePair options[])
        throws IOException, HttpException
    {
 //HG Test
        //System.out.println("LNIclient.startGet: -init:");
        String lh=lookupHandle(handle);
        lastGet = new GetMethod(lh);
        //System.out.println(">>> +1 << lh:"+lh);
        NameValuePair args[] = new NameValuePair[1+(options == null ? 0:options.length)];
        //System.out.println(">>> +2 <<");
        args[0] = new NameValuePair("package", type);
        //System.out.println(">>> +3 <<");
        if (options != null)
        {
            for (int i = 0; i < options.length; ++i){
                args[i+1] = options[i];
                //System.out.println(">>> +3 args << "+i+": "+options[i]);
            }
        }

        //System.out.println(">>> +4 <<");
        lastGet.setDoAuthentication(true);
        //System.out.println(">>> +5 <<");
        lastGet.setQueryString(args);
        //System.out.println(">>> +6 <<");
        lastGet.setFollowRedirects(false);
        //System.out.println(">>> +7 <<");
        int status = client.executeMethod(lastGet);
        //System.out.println(">>> +8 <<");
        if (status < 200 || status >= 300)
        {
            //System.out.println(">>> +9 <<");
            throw new IOException("GET failed, status = "+status+"; text="+lastGet.getStatusText());
        }
        return lastGet.getResponseBodyAsStream();
    }

    /**
     * Releases resources associated with a GET method initiated by
     * startGet().  Must be called exactly once after client is done
     * reading from the stream returned by startGet().
     */
    public void finishGet()
        throws IOException, HttpException
    {
        if (lastGet != null)
        {
            lastGet.releaseConnection();
            lastGet = null;
        }
    }

    /**
     * Optional main for standalone testing, demonstrate usage.
     *
     */
    public static void main(String[] argv)
    {
        // Args:  -G | -P  [ -i file ] [ -o file ] URL handle
        //         -e eperson -p password [ -t type ]
        Options options = new Options();
        options.addOption("h", "help", false, "show help message");
        options.addOption("o", "output", true, "output file for GET");
        options.addOption("i", "input", true, "input file for PUT");
        options.addOption("G", "get", false, "GET contents of Handle");
        options.addOption("P", "put", false, "PUT package into Handle");
        options.addOption("e", "eperson", true, "eperson to authenticate as (required)");
        options.addOption("p", "password", true, "password for eperson (required)");
        options.addOption("t", "type", true, "package type for GET/PUT");

        try
        {
            CommandLine line = (new PosixParser()).parse(options, argv);
            String eperson = line.getOptionValue("e");
            String password = line.getOptionValue("p");
            String type = line.getOptionValue("t");
            String rest[] = line.getArgs();
            if (eperson == null || password == null || rest.length < 2)
                Usage(options, 1, "Missing a required option or argument");

            String url = rest[0];
            String handle = rest[1];

            LNIclient lni = new LNIclient(url, eperson, password);
            if (type == null)
                type = "METS";

            if (line.hasOption("G"))
            {
                OutputStream pkg = System.out;
                if (line.hasOption("o"))
                    pkg = new FileOutputStream(line.getOptionValue("o"));

                // TODO: add option to convey packager options here.
                try
                {
                    InputStream g = lni.startGet(handle, type, null);
                    copy(g, pkg);
                    pkg.close();
                }
                finally
                {
                    lni.finishGet();
                }
            }
            else if (line.hasOption("P"))
            {
                InputStream pkg = System.in;
                if (line.hasOption("i"))
                    pkg = new FileInputStream(line.getOptionValue("i"));
                String result = lni.put(handle, type, null, pkg);
                System.err.println("LNI PUT created Handle: "+result);
            }
            else
                Usage(options, 1, "Missing required 'G' or 'P' option.");
        }
        catch (org.apache.commons.cli.ParseException pe)
        {
            Usage(options, 1, "Error in arguments: "+pe.toString());
        }
        catch (Throwable e)
        {
            System.err.println("Got exception: "+e.toString());
            e.printStackTrace();
        }
        finally
        {
            System.exit(0);
        }
    }

    // prints usage info to System.out & dies.
    private static void Usage(Options options, int status, String msg)
    {
        HelpFormatter hf = new HelpFormatter();
        if (msg != null)
            System.out.println(msg+"\n");
        hf.printHelp(LNIclient.class.getName()+" [options]  LNI-DAV-URL  Handle\n",
                       options, false);
        System.exit(status);
    }

    /**
     * Copy stream-data from source to destination. This method does not buffer,
     * flush or close the streams, as to do so would require making non-portable
     * assumptions about the streams' origin and further use. If you wish to
     * perform a buffered copy, use {@link #bufferedCopy}.
     *
     * @param input
     *            The InputStream to obtain data from.
     * @param output
     *            The OutputStream to copy data to.
     */
    private static void copy(final InputStream input, final OutputStream output)
            throws IOException
    {
        final int BUFFER_SIZE = 1024 * 4;
        final byte[] buffer = new byte[BUFFER_SIZE];

        while (true)
        {
            final int count = input.read(buffer, 0, BUFFER_SIZE);

            if (-1 == count)
            {
                break;
            }

            // write out those same bytes
            output.write(buffer, 0, count);
        }

        // needed to flush cache
        // output.flush();
    }
}
