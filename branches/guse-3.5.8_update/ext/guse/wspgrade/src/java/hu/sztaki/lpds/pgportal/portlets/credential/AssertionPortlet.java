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

package hu.sztaki.lpds.pgportal.portlets.credential;

//~--- non-JDK imports --------------------------------------------------------
import com.liferay.portal.util.PortalUtil;
import dci.data.Certificate;
import dci.data.Item;
import dci.data.Item.Unicore;
import dci.data.Middleware;

import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import org.apache.xmlbeans.XmlException;

import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.UUID;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
//import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

/**
 * AssertionPortlet Portlet Class
 */
public class AssertionPortlet extends GenericWSPgradePortlet {
    public class ResourceNotFoundException extends Exception {
        ResourceNotFoundException (String s)  {
            super(s);
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(AssertionPortlet.class);

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
	logger.trace("doView");
        response.setContentType("text/html");

        if (!isInited()) {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);

            return;
        }

        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/saml/SAMLPortlet.jsp");

        if (request.getRemoteUser() == null) {
            request.setAttribute("msg", "You must be logged in to use this function.");
        } else {
            if (request.getParameter("nextJSP") != null) {
                dispatcher = getPortletContext().getRequestDispatcher(request.getParameter("nextJSP"));    // UploadAssertion
            } else {    // main page
                try {
                    Vector<String> resources = getAllGroupsforSAML(request.getPortletSession());

                    request.setAttribute("rlist", resources);

                    Vector uploadedlist = getAssertionList(request.getRemoteUser(), resources);

                    request.setAttribute("uploadedlist", uploadedlist);
                    request.setAttribute("uploadedlistsize", uploadedlist.size());
                    InitializeApplet(request);
                } catch (Exception e) {
                    request.setAttribute("msg", "Could not get resource list from DCI-BRIDGE!");
		    logger.error("Could not get resource list from DCI-BRIDGE!");
		    logger.debug("Got exception",e);
                }
            }
        }

        dispatcher.include(request, response);
	logger.trace("Finished");
    }

    public void doGoGenerate(ActionRequest request, ActionResponse response) {
	logger.trace("doGoGenerate");
        try {
            InitializeApplet((RenderRequest) request);
        } catch (Exception e) {
	    logger.error("Exception occured.",e);
        }
    }

    public void doDelete(ActionRequest request, ActionResponse response) {
        logger.trace("doDelete");
        logger.error("Unsafely implemented!");

        try {
            Vector<String> resources = getAllGroupsforSAML(request.getPortletSession());
            boolean found = false;
            String grid = request.getParameter("setforgrid");
            for (String res:resources) {
                if (res.equals(grid)) {
                    found = true;
                } else {
                    logger.trace("Grid " + res + " != " + grid + ".");
                }
            }

            if (found) {
                String delsaml = this.loadUsersDirPath(request.getRemoteUser())  + "x509up." + grid;
                File filetodel = new File(delsaml);

                filetodel.delete();
                request.setAttribute("msg", "Delete successful for resource: " + request.getParameter("setforgrid"));
            } else {
                    throw new ResourceNotFoundException("Grid "+ grid + " not found.");
            }
        } catch (Exception e) {
            request.setAttribute("msg", "Could not delete: " + request.getParameter("setforgrid"));
            logger.info("Could not delete: " + request.getParameter("setforgrid"));
            logger.debug("Exception",e);
        }

    }

    /**
     * Uploading SAML file(s).
     * The parameters are filled in by the user on the saml/UploadAssertion.jsp page.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    @Override
    public synchronized void doUpload(ActionRequest request, ActionResponse response) {
	logger.trace("");

        // logger.info("Assertion-doUpload");
        FileItem samlFile  = null;
        String   sresource = null;    // selected resource
    	String action = request.getParameter("guse");
	logger.info("Action:" + action);

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload   pfu     = new PortletFileUpload(factory);

            pfu.setSizeMax(1048576);         // Maximum upload size 1Mb

            Iterator iter = pfu.parseRequest(request).iterator();

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();

                if (item.isFormField()) {    // retrieve parameters if item is a form field
                    if ("sresource".equals(item.getFieldName())) {
                        sresource = item.getString();
                    }
                } else {
                    if ("samlFile".equals(item.getFieldName())) {
                        samlFile = item;
                    }
                }
            }

	    List<String> result = uploadSaml(request.getPortletSession(),
					       samlFile, 
					       sresource, 
					       request.getRemoteUser());

	    StringBuilder sb = new StringBuilder();
	    boolean flag = false;
	    for (String msg: result) {
		if (flag) {
		    sb.append("<br />");
		} else {
		    flag = true;
		}
		sb.append(msg);
	    }
	    request.setAttribute("msg",sb.toString());
        } catch (Exception e) {
            request.setAttribute("msg", "Upload failed. Reason: " + e.getMessage());
	    logger.info("Upload of asseriont failed. Reason: " + e.getMessage());
	    logger.debug("Exception:",e);
        }
    }

    
    /**
     * Uploading SAML file(s) for interaction with the Java Applet.
     * The parameters are filled in by the SAML assiertion applet.
     * This function contains a fallback to the help provider of gUSE.
     * @param request ResourceRequest
     * @param response ResouceResponse
     * @throws PortletException
     * @throws IOException
     */   
    @Override
    public void serveResource(ResourceRequest request, ResourceResponse response) 
	throws PortletException, IOException {
	logger.debug("serveResource: guse=" + request.getParameter("guse"));
	if(!"doAppletUpload".equals(request.getParameter("guse"))) {
	    super.serveResource(request,response);
	    return;
	}

	String msg;

	try {
	    logger.debug("Assertion-doUpload");
	    FileItem samlFile  = null;
	    String   sresource = null;    // selected resource

	    try {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		PortletFileUpload   pfu     = new PortletFileUpload(factory);
		

		pfu.setSizeMax(1048576);         // Maximum upload size 1Mb

		Iterator iter = pfu.parseRequest(PortalUtil.getHttpServletRequest(request)).iterator();

		while (iter.hasNext()) {
		    FileItem item = (FileItem) iter.next();

		    if (item.isFormField()) {    // retrieve parameters if item is a form field
			if ("sresource".equals(item.getFieldName())) {
			    sresource = item.getString();
			}
		    } else {
			if ("samlFile".equals(item.getFieldName())) {
			    samlFile = item;
			}
		    }
		}

		List<String> result = uploadSaml(request.getPortletSession(), samlFile, sresource, request.getRemoteUser());

		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		for (String m: result) {
		    if (flag) {
			sb.append("\n");
		    } else {
			flag = true;
		    }
		    sb.append(m);
		}


		msg = sb.toString();
	    } catch (Exception e) {
		msg = "Upload failed. Reason: " + e.getMessage();
		logger.info("Upload of assertion failed. Reason: " + e.getMessage());
		logger.debug("Exception:",e);
	    }

	    response.setContentType("text/plain");
	    response.addProperty("Cache-Control", "no-cache");

	    response.getWriter().write(msg);

	    logger.info("Returning: " + msg);

	} catch (Exception e2) {
	    logger.error("Error in " + getClass().getName() + "\n",e2);
	}
	return;
    }	

    /**
     * Checks if the SAML file exists in the user's directory. Proper messages
     * are sent to the GUI.
     * @param session PortletSession a portlet session object
     * @param samlfile FileItem the SAML Asertion
     * @param usrId String containing the user id
     * @throws Exception
     */
    private synchronized List<String> uploadSaml(PortletSession session,
					   FileItem samlfile, 
					   String sresource, 
					   String usrId) throws Exception {
        logger.debug("samlfile:" + samlfile.getName() + " sresource:" + sresource);
        FileItem sf     = samlfile;
        String   userId = usrId;
	String retval = null;


	UUID uuid = UUID.randomUUID();
	String tmpName = loadUsersDirPath(userId);
	File   saveTotmp = File.createTempFile("tmp","x509up",new File(tmpName));
	List<String> result = new Vector<String>();

        try {
	    checkFile("SAML", samlfile);
	    sf.write(saveTotmp);
	    HashMap samldetails = getSAMLDetail(saveTotmp);    // check, if it is a valid assertion, or not

	    List<String> resources = getResources(session,sresource,(String)samldetails.get("subject"));

	    for (String sres:resources) {
		try {
		    String sdestfile = tmpName + "/" + "x509up." + sres;
	    

		    File destfile = new File(sdestfile);

		    if (destfile.exists()) {
			destfile.delete();
		    }

		    sf.write(destfile);
		    result.add("Upload successful for resource: " + sres);
		} catch (Exception e) {
		    result.add("Upload for " + sres + " failed. Reason: " + e.getMessage());
		    logger.warn("Upload for " + sres + " failed. Reason: " + e.getMessage());
		    logger.debug("Exception: ",e);
		}
            }

	} catch (Exception e) {
	    if (saveTotmp.exists()) {
                saveTotmp.delete();
            }
	    throw e;
	}
	if (saveTotmp.exists()) {
	    saveTotmp.delete();
	}
	return result;
    }

    private Vector<String> getAllGroupsforSAML(PortletSession ps) throws Exception {

        logger.trace("loadUsersDirPath");

        // elerheto eroforras konfiguracio lekerdezese
        if (ps.getAttribute("resources", ps.APPLICATION_SCOPE) == null) {
            ResourceConfigurationFace rc =
                (ResourceConfigurationFace) InformationBase.getI().getServiceClient("resourceconfigure", "portal");
            List<Middleware> tmp_r = rc.get();

            ps.setAttribute("resources", tmp_r, ps.APPLICATION_SCOPE);
            ps.setAttribute("pub_resources", tmp_r, ps.APPLICATION_SCOPE);
        }

        @SuppressWarnings("unchecked")
        Vector<String> gridNames = ConfigHandler.getAllGroupsforProxy((List<Middleware>) ps.getAttribute("resources",
                                       ps.APPLICATION_SCOPE), new Certificate[] { Certificate.SAML });

        return gridNames;
    }

    private List<String> getResourceList(PortletSession session, String DN)
	throws InvalidNameException
    {
        logger.trace("getResourceList");

        List<Middleware> pResources =
            (List<Middleware>) session.getAttribute("resources",
						    session.APPLICATION_SCOPE);
        if (pResources == null) {
            return null;
        }

        List<String> Names = new Vector<String>();
        boolean flag      = false;
	
        LdapName name = new LdapName(DN);
        logger.info("Comparing LDAP name " + name.toString());
	
        for (Middleware t : pResources) {
    	    flag = false;

            if (t.isEnabled()) {
                for (Certificate c : t.getCertificate()) {
                    if (Certificate.SAML.equals(c)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                for (Item i : t.getItem()) {
                    Unicore uni = i.getUnicore();
                    if (t.isEnabled()) {
                        try {
                            LdapName subject = new LdapName (uni.getSubjectdn());
			    
                            logger.info("Checking DN: “" + uni.getSubjectdn() + "”");
                            logger.info("Subject: " + subject.toString());
                            if(name.equals(subject)) {
                                Names.add(i.getName());
                            }
                        } catch (InvalidNameException e) {
                            logger.warn("Internal error: Reported certificate from service invalid" + uni);
                            logger.warn("Reported DN: " + uni.getSubjectdn());
                            logger.debug("Stack trace:",e);
                        }
			
                        logger.debug("Alias" + uni.getKeyalias());
                    }
                }
    	    }
        }
	return Names;
    }


    private List<String> getResources(PortletSession session,String sresource, String DN) 
	throws ResourceNotFoundException, InvalidNameException
    {
        logger.trace("getResources");
        logger.info("Searching for resource “" + sresource + "” and DN “" + DN + "”");

    	List<String> resources = getResourceList(session,DN);
        if (resources == null) {
            throw new ResourceNotFoundException("No DCI found (getResourceList returned an empty list), searching for " + sresource + ".");
        }
    
        if (sresource == null) {
            return resources;
        }
        if (sresource.length() == 0 ) {
            return resources;
        }
        List<String> ret = new Vector<String>();
        for (String res:resources) {
            logger.debug("Checking resource “" + res + "”");
	    
            if (sresource.equals(res)) {
                ret.add(res);
                return ret;
            }
        }

        throw new ResourceNotFoundException("Resource " + sresource + " could not be found.");
    }


    public Vector getAssertionList(String userId, Vector<String> resources) {
	logger.trace("getAssertionList");
        Vector re = new Vector();

        try {
            for (String resource : resources) {
                File samlfile = new File(loadUsersDirPath(userId) + "x509up." + resource);

                if (samlfile.exists()) {
                    HashMap samldetails = getSAMLDetail(samlfile);

                    samldetails.put("set", resource);    // set for grid
                    re.add(samldetails);
                }
            }
        } catch (Exception cex) {                        // no creds set
	    logger.info("No assertions for user " + userId + " found.");
	    logger.debug("Exception was", cex);
        }

        return re;
    }

    /**
     * Creates a HashMap containing the details of the SAML assertion
     * @param sf File
     * @return HashMap
     * @throws Exception
     */
    private HashMap getSAMLDetail(File samlf) throws Exception {
	logger.trace("getSAMLDetail");
        HashMap ch = new HashMap();

        try {
            if (!samlf.equals(null) && samlf.exists() && (samlf.length() > 0)) {
                String            id      = "";
                String            issuer  = "";
                String            subject = "";
                Long              endTime = 0L;
                AssertionDocument td      = AssertionDocument.Factory.parse(samlf);

                if (!td.isNil()) {
                    id      = td.getAssertion().getID();
                    issuer  = td.getAssertion().getIssuer().getStringValue();
                    endTime = td.getAssertion().getConditions().getNotOnOrAfter().getTimeInMillis();
                    subject = td.getAssertion().getSubject().getNameID().getStringValue();
                } else {
                    logger.info("trust delegation is null");
                }

                ch.put("id", id);
                ch.put("issuer", issuer);
                ch.put("tleft", timeConvert(endTime));
                ch.put("subject", subject);

                return ch;
            }
        } catch (XmlException xmle) {
            logger.info("An XML exception has been caught.",xmle);

            try {
                samlf.delete();
            } catch (Exception e) {
		logger.trace("deleting samlf failed",e);
	    }

            throw new Exception("It seems to be not a valid assertion file.");
        }

        return ch;
    }

    /**
     * Initialize the applet parameters.
     * @param request
     */
    public void InitializeApplet(PortletRequest request) {
	logger.trace("InitializeApplet");
        try {
            String           aliases    = "";
            String           subjectdns = "";
            List<Middleware> pResources = (List<Middleware>) request.getPortletSession().getAttribute("resources",
                                              request.getPortletSession().APPLICATION_SCOPE);
            HashMap<String,String> aliassubj = new HashMap<String,String>();
            boolean flag      = false;

            for (Middleware t : pResources) {
                flag = false;

                if (t.isEnabled()) {
                    for (Certificate c : t.getCertificate()) {
                        if (Certificate.SAML.equals(c)) {
                            flag = true;
                        }
                    }
                }

                if (flag) {
                    for (Item i : t.getItem()) {
                        if (t.isEnabled()) {
                            Unicore uni = i.getUnicore();

                            aliassubj.put(uni.getKeyalias(), uni.getSubjectdn());

                            logger.debug("uni-alias" + uni.getKeyalias());
                        }
                    }
                }
            }

            Iterator it = aliassubj.keySet().iterator();

            while (it.hasNext()) {
                String alias = it.next().toString();
                String dn    = aliassubj.get(alias).toString();

                logger.debug("alias:" + alias + " dn:" + dn);
                aliases    += alias + "##";
                subjectdns += dn + "##";
            }

            aliases    = aliases.substring(0, aliases.length() - 2);
            subjectdns = subjectdns.substring(0, subjectdns.length() - 2);
            request.setAttribute("aliases", aliases);
            request.setAttribute("subjectdns", subjectdns);
        } catch (Exception e) {
	    logger.error("Exception occured.",e);
        }
    }

    /**
     * Calculates the validity of the SAML Assertion.
     * @param endt Long
     * @return String
     */
    protected String timeConvert(Long endt) {
	logger.trace("timeConvert");
        Long endTime  = endt;
        Long currTime = new Date().getTime();
        Long timeLeft = 0L;

	String d;

        if (endTime > currTime) {
            timeLeft = endTime - currTime;
	    timeLeft /= 1000;
	    int minutes = (int) ((timeLeft) / 60);
	    timeLeft = timeLeft - (minutes * 60);
	    int hours = ((minutes) / 60);
	    minutes = minutes - (hours * 60);
	    int days = (hours / (24));
	    hours = hours - (days * 24);
	    d        = days + " days    " + hours + "h " + minutes + "min " + timeLeft + "s";
        } else {
            d = "<div class=\"portlet-msg-alert\">Your assertion is expired. Please generate a new assertion.</div>";
        }
	

        return d;
    }

    /**
     * Checks if the file input field was selected, if the file exists and
     * if the file is not null. Proper error messages are set, showed on the GUI.
     * @param type String contains the selected type (p12, pem or saml)
     * @param f FileItem containing the uploaded file
     */
    private void  checkFile(String type, 
			    FileItem f) throws ResourceNotFoundException, IOException {
	logger.trace("checkFile");

	if ((f == null) || "".equals(f.getName())) {
	    throw new ResourceNotFoundException(type + " file must be selected.");
	}

	if (f.getInputStream() == null) {
	    throw new ResourceNotFoundException("Selected " + type + " file does not exist.");
	}

	if (f.getInputStream().available() == 0) {
	    throw new ResourceNotFoundException("Selected " + type + " file cannot be accessed.");
	}

    }

    /**
     * Gets the user directory's path.
     * @return String
     * @throws Exception
     */
    private String loadUsersDirPath(String userId) throws Exception {
        String path = PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/"+userId+"/";

        // check if the user's directory exists and create it if not
        File userDirectory = new File(path);
        if (!userDirectory.exists()) {
            if (!userDirectory.mkdir()) {
                throw new Exception("Could not create user dir for " + userDirectory.getPath() + ".");
            }
        }

        return path;
    }





    private List<String> getResourceList(ActionRequest request, String DN)
            throws InvalidNameException {
        @SuppressWarnings("unchecked")
        List<Middleware> pResources =
                (List<Middleware>) request.getPortletSession().getAttribute("resources",
                request.getPortletSession().APPLICATION_SCOPE);

        List<String> Names = new Vector<String>();
        boolean flag = false;

        LdapName name = new LdapName(DN);
        System.out.println("Comparing LDAP name " + name.toString());

        for (Middleware t : pResources) {
            flag = false;

            if (t.isEnabled()) {
                for (Certificate c : t.getCertificate()) {
                    if (Certificate.SAML.equals(c)) {
                        flag = true;
                    }
                }
            }

            if (flag) {
                for (Item i : t.getItem()) {
                    Unicore uni = i.getUnicore();
                    if (t.isEnabled()) {
                        try {
                            LdapName subject = new LdapName(uni.getSubjectdn());

                            System.out.println("Checking DN: “" + uni.getSubjectdn() + "”");
                            System.out.println("Subject: " + subject.toString());
                            if (name.equals(subject)) {
                                Names.add(i.getName());
                            }
                        } catch (InvalidNameException e) {
                            logger.warn("Internal error: Reported certificate from service invalid", uni);
                            logger.warn("Reported DN: " + uni.getSubjectdn());
                            logger.trace("Stack trace:", e);
                        }

                    // System.out.println("uni-alias" + uni.getKeyalias());
                    }
                }
            }
        }
        return Names;
    }

    private List<String> getResources(ActionRequest request, String sresource, String DN)
            throws ResourceNotFoundException, InvalidNameException {
        System.out.println(logger.toString());
        logger.info("Searching for resource “" + sresource + "” and DN “" + DN + "”");
        System.out.println("Searching for resource “" + sresource + "” and DN “" + DN + "”");

        List<String> resources = getResourceList(request, DN);
        if (sresource == null) {
            return resources;
        }
        if (sresource.length() == 0) {
            return resources;
        }
        List<String> ret = new Vector<String>();
        for (String res : resources) {
            logger.trace("Checking resource “" + res + "”");
            System.out.println("Checking resource “" + res + "”");

            if (sresource.equals(res)) {
                ret.add(res);
                return ret;
            }
        }

        throw new ResourceNotFoundException("Resource " + sresource + " could not be found.");
    }
}

