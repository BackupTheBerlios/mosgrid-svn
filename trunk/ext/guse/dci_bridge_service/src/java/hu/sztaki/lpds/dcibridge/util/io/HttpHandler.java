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
/**
 * Transmission of HTTP-based file
 */

package hu.sztaki.lpds.dcibridge.util.io;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
/**
 * @author krisztian karoczkai
 */
public class HttpHandler {
    private HttpPost httpPost;
    private DefaultHttpClient httpclient;
    private CookieStore cookieStore;
    private HttpContext localContext;

    public void open(String pURL)
    {

        httpclient = new DefaultHttpClient();
        cookieStore =  new BasicCookieStore();
        localContext= new BasicHttpContext();
        httpPost = new HttpPost(pURL.trim());
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
/*
        URL n=new URL(pURL);
        httpclient.getCredentialsProvider().setCredentials(
        new AuthScope(n.getHost(),n.getPort()), 
        new UsernamePasswordCredentials("guse", "guse"));        
 */
    }

    public InputStream getStream(Hashtable<String,String> pValue) throws IOException{

        List <NameValuePair> nvps = new ArrayList<NameValuePair>();
        Enumeration<String> enm=pValue.keys();
        String key;
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            nvps.add(new BasicNameValuePair(key, pValue.get(key)));
        }
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(httpPost);

        HttpEntity entity = response.getEntity();
        return entity.getContent();

    }

    public void write(String pURL, List<File> pValue) throws IOException{
        open(pURL);
        MultipartEntity reqEntity = new MultipartEntity();
        for(File t: pValue){
            FileBody bin = new FileBody(t);
            reqEntity.addPart(t.getName(), bin);
        }
        httpPost.setEntity(reqEntity);
        HttpResponse response = httpclient.execute(httpPost);
    }

    public void close()
    {
//connection shut down
        httpclient.getConnectionManager().shutdown();
    }
    
    
    
    
    public void read(String pURL, String pPath) throws Exception{
        File f=new File(pPath);
        f.createNewFile();
        open(pURL);
        InputStream is=getStream(new Hashtable<String, String>());
        FileOutputStream outFile=new FileOutputStream(f);
        byte[] b=new byte[5120];
        int ln=0;
        while((ln=is.read(b))>0){
            outFile.write(b, 0, ln);
            outFile.flush();
        }

        outFile.close();
        is.close();
        close();
    }
    

}
