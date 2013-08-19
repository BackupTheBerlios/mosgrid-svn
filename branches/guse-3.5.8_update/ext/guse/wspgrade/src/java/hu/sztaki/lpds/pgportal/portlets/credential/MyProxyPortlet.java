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

import dci.data.Certificate;
import dci.data.Middleware;
import hu.sztaki.lpds.dcibridge.client.ResourceConfigurationFace;
import hu.sztaki.lpds.dcibridge.util.ConfigHandler;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import hu.sztaki.lpds.pgportal.services.credential.SZGStoreKey;
import hu.sztaki.lpds.pgportal.services.credential.SZGCredential;
import hu.sztaki.lpds.pgportal.services.credential.SZGCredentialManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequestDispatcher;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.portlet.PortletSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.portlet.PortletFileUpload;
import java.util.Date;
import java.util.List;
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;

/**
 * CredentialManager Portlet Class
 * hu.sztaki.lpds.pgportal.portlets.credential.MyProxyPortlet
 * com.test.CredentialManager
 * This class represents the MyProxy portlet, which implements methods for
 * creating, deleting and modifying grid proxy certificates.
 */
public class MyProxyPortlet extends GenericWSPgradePortlet {

    private String mainjsp = "credentialList.jsp";
    
    private SZGCredentialManager cm = null;

    public MyProxyPortlet() {
        cm = SZGCredentialManager.getInstance();
    }

    /**
     * Loads existing user certificates, if they exist.
     * @param userId String containing user id.
     */
    private void doInit(String userId) {
        try {
            loadUsrCert(userId);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets user data if available sets the view to the credentialList.jsp page
     * @param request RenderRequest
     * @param response RenderResponse
     * @throws PortletException
     * @throws IOException
     */
    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        if (!isInited()) {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }
        
        String userId = request.getRemoteUser();
        String nextjsp = request.getParameter("nextJSP");
        if (userId != null) {//user logged in

            if(nextjsp == null || nextjsp.equals(new String("")) ) nextjsp=mainjsp;

            if (mainjsp.equals(nextjsp)) {//credentialList
                Vector cr = new Vector();
                Vector<HashMap> orig_creds = (Vector<HashMap>) cm.getCredentialsList(userId);
                if (orig_creds == null) {//load certs!
                   
                    doInit(userId);
                    orig_creds = cm.getCredentialsList(userId);
                }

                if (orig_creds == null) {
                    request.setAttribute("credssize", 0);
                } else {
                    for (HashMap hs : orig_creds)
                     cr.add(hs);
                    
                    request.setAttribute("creds", cr);
                    request.setAttribute("credssize", cr.size());
                }

                if (!getSAML(userId).equals(null) && getSAML(userId).length() > 0) {
                    try {
                        cr.add(getSAMLDetail(userId, getSAML(userId)));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    request.setAttribute("creds",cr);
                    request.setAttribute("credssize", cr.size());
                } else {
                    //request.setAttribute("msg", "Missing SAML file!");
                }
            }
        } else {
            request.setAttribute("msg", "Sign in!");
            request.setAttribute("credssize", 0);
            //jsp = mainjsp;
        }
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/jsp/credential/" + nextjsp);
        dispatcher.include(request, response);
    }

    /**
     * Return the file containing the SAML Assertion.
     * @param userId String
     * @return
     */
    File getSAML(String userId) {
        String saml = "";
        try {
            saml = this.loadUsersDirPath() + userId + "/" + "x509up.assertion";
        } catch (Exception e) {
            e.printStackTrace();
        }
        File samlf = new File(saml);
        return samlf;
    }

    /**
     * Creates a HashMap containing the details of the user credential
     * @param cred SZGCredential containing proxy.
     * @param userId String containing user id.
     * @return HashMap
     * @throws Exception
     */
    private HashMap getCredentialDetail(SZGCredential cred, String userId) throws Exception {
        HashMap ch = new HashMap();
        ch.put("id", cred.getId());
        ch.put("dfrom", cred.getDownloadedFrom());
        ch.put("issuer", cred.getIssuer());
        ch.put("subject", cred.getSubject());
        ch.put("tleft", cred.getTimeLeft());
        ch.put("ptype", cred.getProxyType());
        ch.put("strenght", "" + cred.getStrength());
        ch.put("desc", cred.getDescription());
        ch.put("set", SZGCredentialManager.getInstance().getSetGridsForCredential(userId, cred.getId()));

        return ch;
    }

    /**
     * Creates a HashMap containing the details of the SAML assertion
     * @param usrId String
     * @param sf File
     * @return HashMap
     * @throws Exception
     */
    private HashMap getSAMLDetail(String usrId, File sf) throws Exception {
        HashMap ch = new HashMap();

        File samlf = getSAML(usrId);
        if (!samlf.equals(null) && samlf.exists() && samlf.length() > 0) {
            String id = "";
            String issuer = "";
            String subject = "";
            Long endTime = 0L;

            AssertionDocument td = AssertionDocument.Factory.parse(samlf);

            if (!td.isNil()) {
                //List<TrustDelegation> tdList = new ArrayList<TrustDelegation>();

                id = td.getAssertion().getID();
                issuer = td.getAssertion().getIssuer().getStringValue();
                endTime = td.getAssertion().getConditions().getNotOnOrAfter().getTimeInMillis();
                subject = td.getAssertion().getSubject().toString();

            } else {
                System.out.println("MyPorxyPortlet::getSamlDetail:trust delegation is null");
            }
            ch.put("id", id);
            ch.put("issuer", issuer);
            ch.put("tleft", timeConvert(endTime));
            ch.put("subject", subject);
            ch.put("ptype", "SAML");

            return ch;
        }

        return ch;
    }

    /**
     * Calculates the validity of the SAML Assertion.
     * @param endt Long
     * @return String
     */
    String timeConvert(Long endt) {
        Long endTime = endt;
        Long currTime = new Date().getTime();
        Long timeLeft = 0L;

        if (endTime > currTime) {
            timeLeft = endTime - currTime;
        }

        String d;
        timeLeft /= 1000;

        int days = (int) (timeLeft / (3600 * 24));
        timeLeft = timeLeft - (days * 3600 * 24);
        int hours = (int) ((timeLeft) / 3600);
        timeLeft = timeLeft - (hours * 3600);
        int minutes = (int) ((timeLeft) / 60);
        timeLeft = timeLeft - (minutes * 60);

        d = days + " days    " + hours + ":" + minutes + ":" + timeLeft;
        return d;
    }

    /**
     * Navigates to the details.jsp page and
     * shows the details of a given proxy certificate and
     * @param request ActionRequest
     * @param response ActionRequest
     */
    public void doGoDetails(ActionRequest request, ActionResponse response) {
        String userId = request.getRemoteUser();
        String detailCredId = "-";

        try {
            detailCredId = "" + request.getParameter("selCredId");
            if (detailCredId.contains("SAML")) {
                request.setAttribute("cred", getSAMLDetail(userId, getSAML(userId)));
            } else {
                SZGStoreKey key = new SZGStoreKey(userId, detailCredId);
                SZGCredential cred = this.cm.getCredential(key);

                request.setAttribute("cred", getCredentialDetail(cred, userId));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        request.setAttribute("msg", "detailCredId=" + detailCredId);
        response.setRenderParameter("nextJSP","details.jsp");
    }

    /**
     * Navigates to the mapProxy.jsp page where a given proxy can be maped to a
     * proxy certificate.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doGoMapProxy(ActionRequest request, ActionResponse response) {
        try {
            String mapCredId = request.getParameter("selCredId");
            String userId = request.getRemoteUser();
            SZGStoreKey key = new SZGStoreKey(userId, mapCredId);
            SZGCredential cred = this.cm.getCredential(key);

            PortletSession ps = request.getPortletSession();

            // elerheto eroforras konfiguracio lekerdezese
            try{
                if(ps.getAttribute("resources",ps.APPLICATION_SCOPE)==null){
                    ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                    List<Middleware> tmp_r=rc.get();
                    ps.setAttribute("resources", tmp_r,ps.APPLICATION_SCOPE);
                    ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
                }
            }
            catch(Exception ex) {ex.printStackTrace();}
            Vector<String> gridNames = ConfigHandler.getAllGroupsforProxy((List<Middleware>)ps.getAttribute("resources",ps.APPLICATION_SCOPE),new Certificate[]{Certificate.X_509_GSI,Certificate.X_509_RFC});
            if (gridNames == null || gridNames.size() == 0) {
                request.getPortletSession().setAttribute("msg", "There are no defined grids, report this to the administrator of the portal!");
                //"credentialList.jsp";
                this.saveUsrCert(userId);
                return;
            } else {
                request.setAttribute("msg", "Map proxy with one of the Grids.");
                response.setRenderParameter("nextJSP","mapProxy.jsp");

                request.getPortletSession().setAttribute("gridNames", gridNames,ps.APPLICATION_SCOPE);
                request.getPortletSession().setAttribute("cred", getCredentialDetail(cred, userId),ps.APPLICATION_SCOPE);
                request.getPortletSession().setAttribute("selCredId", cred.getId(),ps.APPLICATION_SCOPE);
            }
            this.saveUsrCert(userId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Maps the proxy to a certain grid and saves the .
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doMapProxy(ActionRequest request, ActionResponse response) {
        String currentCertId = request.getParameter("selCredId");
        String gridName = request.getParameter("gridName");
        String userId = request.getRemoteUser();
        SZGCredential mappedCred = this.cm.getCredentialForGrid(userId, gridName);
        if (mappedCred != null) {
            request.getPortletSession().setAttribute("gridName", gridName);
            request.setAttribute("gridName", gridName);
            request.getPortletSession().setAttribute("selCredId", currentCertId);
            request.getPortletSession().setAttribute("oldCredId", this.cm.getCredentialForGrid(userId, gridName).getId());
            request.setAttribute("msg", "Confirm mapping certificate for " + gridName + "!");
            response.setRenderParameter("nextJSP","mapProxyConfirm.jsp");
        } else {
            this.cm.setCredentialForGrid(userId, currentCertId, gridName);
            request.setAttribute("msg", "Certificate successfully set for " + gridName + ".");
            response.setRenderParameter("nextJSP","credentialList.jsp");
        }
        try {
            this.saveUsrCert(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigates to the mapProxyConfirmDetailed.jsp page and creates a .cred file.
     * lists the details of a given proxy .
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doMapProxyShowDetails(ActionRequest request, ActionResponse response) {
        String gridName = request.getParameter("gridName");
        String newCredId = request.getParameter("selCredId");
        String userId = request.getRemoteUser();
        try {
            SZGCredential mappedCred = this.cm.getCredentialForGrid(userId, gridName);

            SZGStoreKey key = new SZGStoreKey(userId, newCredId);
            SZGCredential cred = this.cm.getCredential(key);

            request.setAttribute("gridName", gridName);
            request.setAttribute("newcred", getCredentialDetail(cred, userId));
            request.setAttribute("oldcred", getCredentialDetail(mappedCred, userId));
            request.setAttribute("msg", "Confirm mapping for " + gridName + " Grid!");
            response.setRenderParameter("nextJSP","mapProxyConfirmDetailed.jsp");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("msg", "An ERROR occured!");
            //response.setRenderParameter("nextJSP",""credentialList.jsp";
        }
        return;
    }

    /**
     * Maps a proxy certificate to a grid.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doMapReallyProxy(ActionRequest request, ActionResponse response) {
        String gridName = request.getParameter("gridName");
        String currentCertId = request.getParameter("selCredId");
        String userId = request.getRemoteUser();
        this.cm.setCredentialForGrid(userId, currentCertId, gridName);
        request.setAttribute("msg", "Certificate successfully set for " + gridName + " Grid.");
        //response.setRenderParameter("nextJSP",""credentialList.jsp";

        try {
            this.saveUsrCert(userId);
        } catch (Exception e) {
        }
        return;
    }

    /**
     * Navigates to the credentialList.jsp page.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doGoCredentialList(ActionRequest request, ActionResponse response) {
        request.setAttribute("msg", "[Press a button.]");
        //jsp = mainjsp;
    }

    /**
     * Deletes the selected proxy certificate.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doDelete(ActionRequest request, ActionResponse response) {
        String userId = request.getRemoteUser();
        try {
            if (request.getParameter("selCredId").contains("SAML")) {
                deleteSAML(userId);
            } else {
                String deleteCredId = request.getParameter("selCredId");
                SZGStoreKey key = new SZGStoreKey(userId, deleteCredId);
                deleteCertFile(userId, key);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } catch (java.lang.Throwable t) {
            t.printStackTrace();
        }
        request.setAttribute("msg", "Delete successfull.");
        //jsp = "credentialList.jsp";

        try {
            saveUsrCert(userId);
        } catch (Exception e) {
        }
    }

    /**
     * Navigates to the download.jsp page.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doGoDownload(ActionRequest request, ActionResponse response) {
        request.setAttribute("msg", "Fill in the fields for download!");
        response.setRenderParameter("nextJSP","download.jsp");
    }

    /**
     * Downloads a proxy certificate. The proxy certificate is generated on
     * the MyProxy server specified by user parameters.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doDownload(ActionRequest request, ActionResponse response) {
        String userId = request.getRemoteUser();
        response.setRenderParameter("nextJSP","download.jsp");
        SZGStoreKey key = new SZGStoreKey(userId, new Long(System.currentTimeMillis()).toString());
        try {
            String host = request.getParameter("host");
            String portS = request.getParameter("port");
            String login = request.getParameter("login");
            String pass = request.getParameter("pass");
            String lifetimeS = request.getParameter("lifetime");
            String desc = request.getParameter("desc");
            if (!this.checkFieldsFilled(new String[]{host, portS, login, pass, lifetimeS})) {
                request.setAttribute("msg", "The fields marked with * cannot be empty!");
                return;
            }
            int port;
            int lifetime;
            try {
                port = Integer.parseInt(portS);
                lifetime = Integer.parseInt(lifetimeS);
                lifetime = lifetime * 3600;
                lifetime = this.correctLifetime(lifetime);
            } catch (Exception ex) {
                request.setAttribute("msg", "Port and lifetime must be numbers!");
                return;
            }
            this.cm.downloadFromServer(login, pass, lifetime, key, desc, host, port);

            PortletSession ps = request.getPortletSession();

  // elerheto eroforras konfiguracio lekerdezese
        try{
            if(ps.getAttribute("resources",ps.APPLICATION_SCOPE)==null){
                ResourceConfigurationFace rc=(ResourceConfigurationFace)InformationBase.getI().getServiceClient("resourceconfigure", "portal");
                List<Middleware> tmp_r=rc.get();
                ps.setAttribute("resources", tmp_r,ps.APPLICATION_SCOPE);
                ps.setAttribute("pub_resources", tmp_r,ps.APPLICATION_SCOPE);
            }
        }
        catch(Exception ex) {ex.printStackTrace();}
            Vector<String> gridNames = ConfigHandler.getAllGroupsforProxy((List<Middleware>)ps.getAttribute("resources",ps.APPLICATION_SCOPE),new Certificate[]{Certificate.X_509_GSI,Certificate.X_509_RFC});
            if (gridNames.size() == 0) {
                request.setAttribute("msg", "Download successfull.");
                //jsp = "credentialList.jsp";
                saveUsrCert(userId);
                return;
            } else {
                request.setAttribute("msg", "Download successfull, you can set the certificate for any GRID.");
                request.setAttribute("selCredId", key.getCredId());
                response.setRenderParameter("nextJSP","whetherToMapProxy.jsp");
                saveUsrCert(userId);
                return;
            }
        } catch (Exception ex) {
            request.setAttribute("msg", "Download with this parameters failed! Reason: " + ex.getMessage());
            return;
        }
    }

    /*
     * Navigates to the upload page.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doGoUpload(ActionRequest request, ActionResponse response) {
        request.setAttribute("msg", "Fill in the fields for upload!");
        response.setRenderParameter("nextJSP","upload.jsp");
    }

    /**
     * Depending on user's choice calls proper functions for uploading PEM
     * or SAML file(s).
     * The parameters are filled in by the user on the upload.jsp page.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public synchronized void doUpload(ActionRequest request, ActionResponse response) {
        response.setRenderParameter("nextJSP","upload.jsp");
        FileItem samlFile = null;
        FileItem p12File = null;
        String p12FilePass = null;
        String host = null;
        String portS = null;
        String login = null;
        String pass = null;
        String lifetimeS = null;
        String keyPass = null;
        boolean RFCEnabled = false;
        FileItem kf = null, cf = null;
        boolean useDN = false;
        Boolean samlUsed = false, p12Used = false, pemUsed = false;

        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            PortletFileUpload pfu = new PortletFileUpload(factory);
            pfu.setSizeMax(1048576); // Maximum upload size 1Mb
            Iterator iter = pfu.parseRequest(request).iterator();
            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {// retrieve parameters if item is a form field
                    if ("auth".equals(item.getFieldName())) {
                        if ("SAML".equals(item.getString())) {
                            samlUsed = true;
                        } else if ("P12".equals(item.getString())) {
                            p12Used = true;
                        } else if ("PEM".equals(item.getString())) {
                            pemUsed = true;
                        }
                    } else if ("P12".equals(item.getFieldName())) {
                        p12Used = true;
                    } else if ("P12".equals(item.getFieldName())) {
                        pemUsed = true;
                    } else if ("host".equals(item.getFieldName())) {
                        host = item.getString();
                    } else if ("port".equals(item.getFieldName())) {
                        portS = item.getString();
                    } else if ("login".equals(item.getFieldName())) {
                        login = item.getString();
                    } else if ("pass".equals(item.getFieldName())) {
                        pass = item.getString();
                    } else if ("lifetime".equals(item.getFieldName())) {
                        lifetimeS = item.getString();
                    } else if ("keyFilePass".equals(item.getFieldName())) {
                        keyPass = item.getString();
                    } else if ("dnlogin".equals(item.getFieldName())) {
                        useDN = true;
                    } else if ("p12FilePass".equals(item.getFieldName())) {
                        p12FilePass = item.getString();
                    }
                    else if ("RFCEnabled".equals(item.getFieldName())) {
                        RFCEnabled = true;
                    }
                } else {
                    if ("samlFile".equals(item.getFieldName())) {
                        samlFile = item;
                    } else if ("p12File".equals(item.getFieldName())) {
                        p12File = item;
                    }
                    if ("keyFile".equals(item.getFieldName())) {
                        kf = item;
                    } else if ("certFile".equals(item.getFieldName())) {
                        cf = item;
                    }
                }
            }

            if (samlUsed) {
                uploadSaml(request, samlFile, request.getRemoteUser());
            } else if (p12Used) {
                uploadPem(request, p12File, p12FilePass, cf, host, portS, login, pass, lifetimeS, useDN, "p12",RFCEnabled);
            } else if (pemUsed) {
                uploadPem(request, kf, keyPass, cf, host, portS, login, pass, lifetimeS, useDN, "pem",RFCEnabled);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if the file input field was selected, if the file exists and
     * if the file is not null. Proper error messages are set, showed on the GUI.
     * @param req ActionRequest
     * @param type String contains the selected type (p12, pem or saml)
     * @param f FileItem containing the uploaded file
     * @return Boolean
     */
    private Boolean checkFile(ActionRequest req, String type, FileItem f) {
        ActionRequest request = req;

        try {
            if (f == null || "".equals(f.getName())) {
                request.setAttribute("msg", type + " file must be selected!");
                return false;
            }
            if (f.getInputStream() == null) {
                request.setAttribute("msg", "Selected " + type + " file does not exists!");
                return false;
            }
            if (f.getInputStream().available() == 0) {
                request.setAttribute("msg", "Selected" + type + " file does not exists!");
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks all the required fields needed for the MyProxy server.
     * @param req ActionRequest
     * @param host String host for MyProxy
     * @param portS String port for MyProxy
     * @param login String login for MyProxy
     * @param pass String password for MyProxy
     * @param lifetimeS String lifetime to store the credentials
     * @param uDN Boolean: DN was used or not
     * @return Boolean
     */
    private Boolean checkMyproxy(ActionRequest req, String host, String portS,
            String login, String pass, String lifetimeS, Boolean uDN) {

        Boolean useDN = uDN;
        ActionRequest request = req;

        if (!useDN
                && !this.checkFieldsFilled(new String[]{host, portS, login, pass, lifetimeS})) {
            request.setAttribute("msg", "The fields marked with ** cannot be empty when DN login isn't selected!");
            return false;
        }
        if (useDN
                && !this.checkFieldsFilled(new String[]{host, portS, lifetimeS})) {
            request.setAttribute("msg", "The fields marked with * cannot be empty when DN login is selected!");
            return false;
        }

        return true;

    }

    /**
     * Checks if the SAML file exists in the user's directory. Proper messages
     * are sent to the GUI.
     * @param ActionRequest
     * @param samlfile FileItem the SAML Asertion
     * @param usrId String containing the user id
     * @throws Exception
     */
    private void uploadSaml(ActionRequest request, FileItem samlfile, String usrId) throws Exception {
        FileItem sf = samlfile;
        String userId = usrId;

        try {
            if (checkFile(request, "SAML", samlfile)) {
                String usrDir = this.loadUsersDirPath() + "/" + userId + "/" + "x509up.assertion";
                File saveTo = new File(usrDir);
                sf.write(saveTo);
                request.setAttribute("msg", "Upload successful.");
                //response.setRenderParameter("nextJSP","credentialList.jsp");
            }
        } catch (Exception e) {
            request.setAttribute("msg", "Upload failed! Reason: " + e.getMessage());
            //response.setRenderParameter("nextJSP",""credentialList.jsp";
            e.printStackTrace();
        }
    }

    /**
     * Checks if the correct files exist and uploads the files to the MyProxy server.
     * @param req
     * @param keyFile File containing the key
     * @param keyPass Key password
     * @param certFile File containing the cert
     * @param host String containing the MyProxy host
     * @param port String containing the MyProxy port
     * @param login String containing the login for MyProxy
     * @param pass String containing the password for MyProxy
     * @param lifetime String containing the proxy lifetiem
     * @param uDN Boolean: use DN or not
     * @param type String containing pem or p12.
     */
    private void uploadPem(ActionRequest req, FileItem keyFile, String keyPass, FileItem certFile, String host, String port,
            String login, String pass, String lifetime, Boolean uDN, String type,boolean RFCEnabled) {

        ActionRequest request = req;
        FileItem kf = keyFile;
        FileItem cf = certFile;
        Boolean useDN = uDN;
        int iport, ilifetime;
        try {
            iport = Integer.parseInt(port);
            ilifetime = Integer.parseInt(lifetime);
            ilifetime = ilifetime * 3600;
            ilifetime = this.correctLifetime(ilifetime);
        } catch (Exception ex) {
            request.setAttribute("msg", "Port and lifetime must be numbers!");
            return;
        }

        if (type.equals("pem") && checkFile(req, "Key", keyFile) && checkFile(req, "Cert", certFile) && checkMyproxy(req, host, port, login, pass, lifetime, uDN)) {
            try {

                this.cm.uploadToServerInputStream(cf.getInputStream(), kf.getInputStream(), keyPass, login, pass, ilifetime, host, iport, useDN,RFCEnabled);
                request.setAttribute("msg", "Upload successful.");
                //response.setRenderParameter("nextJSP",""credentialList.jsp";
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("msg", "Upload failed! Reason: " + e.getMessage());
                //response.setRenderParameter("nextJSP",""credentialList.jsp";
            }
        } else if (type.equals("p12") && checkFile(req, "P12", keyFile) && checkMyproxy(req, host, port, login, pass, lifetime, uDN)) {

            InputStream kIS = p12ToPem("k", keyFile, req.getRemoteUser(), keyPass);
            InputStream cIS = p12ToPem("c", keyFile, req.getRemoteUser(), keyPass);

            try {
                this.cm.uploadToServerInputStream(cIS, kIS, keyPass, login, pass, ilifetime, host, iport, useDN,RFCEnabled);
                request.setAttribute("msg", "Upload successful.");
                //response.setRenderParameter("nextJSP",""credentialList.jsp";
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("msg", "Upload failed! Reason: " + e.getMessage());
                //jsp = "credentialList.jsp";
            }
        }
    }

    /**
     * Converts the p12 file to pem (MyProxy cannot process p12).
     * @param t String containing type pem or p12.
     * @param p12 FileItem containing the p12 cert
     * @param usrId String holds user id
     * @param pass String holds password
     * @return InputStream
     */
    public InputStream p12ToPem(String t, FileItem p12, String usrId, String pass) {
        String type = t;
        String p12FN = p12.getName();
        String userId = usrId;
        String password = pass;
        String opensslCmd = null;

        try {
            String path = this.loadUsersDirPath() + userId + "/";
            File p12File = new File(path + p12FN);
            p12.write(p12File);
            Process child;
            if ("k".equals(type)) {
                opensslCmd = "/usr/bin/openssl pkcs12 -in " + p12File
                        + " -passin pass:" + password + " -passout pass:"
                        + password + " -nocerts -out " + path + "userkey.pem";
                child = Runtime.getRuntime().exec(opensslCmd);
                File keyF = new File(path + "userkey.pem");

                java.io.InputStream inStream = new java.io.FileInputStream(keyF);
                if (!p12File.delete() || !keyF.delete()) {
                    System.out.println("MyProxyPortlet::p12topem::p12 or key delete failed");
                }
                return inStream;
            } else if ("c".equals(type)) {
                opensslCmd = "/usr/bin/openssl pkcs12 -in " + p12File
                        + " -passin pass:" + password + " -clcerts -nokeys -out "
                        + path + "usercert.pem";
                child = Runtime.getRuntime().exec(opensslCmd);
                File certF = new File(path + "usercert.pem");
                InputStream inStream = new java.io.FileInputStream(certF);

                if (!p12File.delete() || !certF.delete()) {
                    System.out.println("p12topem::p12 & cert delete failed");
                }
                return inStream;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* Shows other operations:
     * proxy destroy, information, change password.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doGoOtherOps(ActionRequest request, ActionResponse response) {
        request.setAttribute("msg", "Fill in the fields for an operation!");
        response.setRenderParameter("nextJSP","otherMyProxyOps.jsp");
    }

    /**
     * Gives information about a MyProxy credential.
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doInformation(ActionRequest request, ActionResponse response) {
        response.setRenderParameter("nextJSP","otherMyProxyOps.jsp");
        String host;
        String login;
        String pass;
        String portS;
        int port;
        try {
            host = request.getParameter("host");
            portS = request.getParameter("port");
            login = request.getParameter("login");
            pass = request.getParameter("pass");
            request.setAttribute("host", host);
            request.setAttribute("port", portS);
            request.setAttribute("login", login);
            if (!checkFieldsFilled(new String[]{host, portS, login, pass})) {
                request.setAttribute("msg", "The fields marked with * cannot be empty!");
                return;
            }
            try {
                port = Integer.parseInt(portS);
            } catch (Exception ex) {
                request.setAttribute("msg", "Port must be a number!");
                return;
            }
            request.setAttribute("nfo", this.cm.MyProxyInfo(host, port, login, pass));

            

        } catch (Exception ex) {
            request.setAttribute("msg", ex.getMessage());
            return;
        }
    }

    /**
     * Destroys a MyProxy credential
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doDestroy(ActionRequest request, ActionResponse response) {
        response.setRenderParameter("nextJSP","otherMyProxyOps.jsp");
        String host;
        String login;
        String pass;
        String portS;
        int port;
        try {
            host = request.getParameter("host");
            portS = request.getParameter("port");
            login = request.getParameter("login");
            pass = request.getParameter("pass");
            if (!checkFieldsFilled(new String[]{host, portS, login, pass})) {
                request.setAttribute("msg", "The fields marked with * cannot be empty!");
                return;
            }
            try {
                port = Integer.parseInt(portS);
            } catch (Exception ex) {
                request.setAttribute("msg", "Port must be a number!");
                return;
            }
            this.cm.MyProxyDestroy(host, port, login, pass);
        } catch (Exception ex) {
            //log.error(ex.getMessage());
            request.setAttribute("msg", ex.getMessage());
            return;
        }
        request.setAttribute("msg", "Credentials deleted");
    }

    /**
     * Changes the password of a MyProxy credential
     * @param request ActionRequest
     * @param response ActionResponse
     */
    public void doChangePass(ActionRequest request, ActionResponse response) {
        response.setRenderParameter("nextJSP","otherMyProxyOps.jsp");
        String host;
        String login;
        String pass;
        String newPass;
        String confNewPass;
        String portS;
        int port;
        try {
            host = request.getParameter("host");
            portS = request.getParameter("port");
            login = request.getParameter("login");
            pass = request.getParameter("pass");
            if (!checkFieldsFilled(new String[]{host, portS, login, pass})) {
                request.setAttribute("msg", "The fields marked with * cannot be empty!");
                return;
            }
            newPass = request.getParameter("newPass");
            confNewPass = request.getParameter("confNewPass");
            try {
                port = Integer.parseInt(portS);
            } catch (Exception ex) {
                request.setAttribute("msg", "Port must be a number!");
                return;
            }
            if (!newPass.equals(confNewPass)) {
                request.setAttribute("msg", "Password must match confirmation");
                return;
            }
            this.cm.MyProxyChangePassword(host, port, login, pass, newPass);
        } catch (Exception ex) {
            //log.error(ex.getMessage());
            request.setAttribute("msg", ex.getMessage());
            return;
        }
        request.setAttribute("msg", "Password changed");
    }

    /**
     * Saves the proxy certificate under the user's directory.
     * @param usr String containing user id
     * @return String
     * @throws Exception
     */
    private String saveUsrCert(String usr) throws Exception {
        String usrDir = this.loadUsersDirPath() + usr + "/";
        File uDir = new File(usrDir);
        if (!uDir.exists()) {
            if (!uDir.mkdirs()) {
                return null;
            }
        }
        FileWriter tmp = new FileWriter(usrDir + "/.creds");
        BufferedWriter out = new BufferedWriter(tmp);
        SZGCredential[] creds = this.cm.getCredentials(usr);
        if (creds == null) {
        }
        try {
            if (creds != null) {
                int credNum = creds.length;

                for (int j = 0; j < credNum; j++) {
                    SZGCredential cred = (SZGCredential) creds[j];
                    String credId = cred.getId();
                    String[] gs = SZGCredentialManager.getInstance().getSetGridsForCredential(usr, credId, true);
                    StringBuffer gsVal = new StringBuffer("");
                    if (gs != null && gs.length != 0) {
                        for (int i = 0; i < gs.length; i++) {
                            gsVal.append(gs[i]);
                            if (i != (gs.length - 1)) {
                                gsVal.append(" ;");
                            }
                        }
                    }
                    if (!" ;".equals(gsVal.toString())) {
                        out.write(cred.getId() + ";" + cred.getDownloadedFrom()
                                + ";" + cred.getTimeLeftInSeconds() + ";"
                                + cred.getDescription() + " ;#" + gsVal.toString()
                                + " ;");
                    }
                    if (j != credNum) {
                        out.newLine();
                    }
                }
            }
            out.close();
        } catch (Exception e) {
        }
        return " ";
    }

    /**
     * Loads the proxy certificate and displays details about it (Issuer,
     * Set for grids, Time left, Actions)
     * @param usr String containing user id
     * @return String
     * @throws Exception
     */
    private String loadUsrCert(String usr) throws Exception {
        String usrDir = this.loadUsersDirPath() + usr + "/";
        File uDir = new File(usrDir);
        if (!uDir.exists()) {
            if (!uDir.mkdirs()) {
                return null;
            }
        }
        File uCreds = new File(usrDir + "/.creds");
        if (uCreds.exists()) {
            FileReader fin = new FileReader(uCreds);
            BufferedReader in = new BufferedReader(fin);
            SZGCredential[] creds = this.cm.getCredentials(usr);
            if (creds == null) {
                try {
                    String sor = new String(" ");
                    while ((sor = in.readLine()) != null) {
                        int indx = 0;
                        int indv = sor.indexOf(";", indx);
                        String Id = new String(sor.substring(indx, indv));
                        indx = indv + 1;
                        indv = sor.indexOf(";", indx);
                        String DownloadedFrom = new String(sor.substring(indx, indv));
                        indx = indv + 1;
                        indv = sor.indexOf(";", indx);
                        String TimeLeft = new String(sor.substring(indx, indv));
                        indx = indv + 1;
                        indv = sor.indexOf(";#", indx);
                        String Description = new String(sor.substring(indx, indv));
                        indx = indv + 2;
                        indv = sor.indexOf(";", indx);
                        String gsVal = new String(sor.substring(indx, indv));

                        SZGStoreKey key = new SZGStoreKey(usr, Id);
                        String cfp;
                        if (0 == gsVal.compareTo(" ")) {
                            System.out.println("loadUsrCert: not set for any grid.");
                            cfp = new String(usrDir + "x509up");
                            InputStream crinstr = new FileInputStream(cfp);
                            this.cm.loadFromFile(crinstr, DownloadedFrom, Integer.parseInt(TimeLeft), key, Description);
                        } else {
                            cfp = new String(usrDir + "x509up." + gsVal.trim());
                            InputStream crinstr = new FileInputStream(cfp);
                            this.cm.loadFromFile(crinstr, DownloadedFrom, Integer.parseInt(TimeLeft), key, Description);
                            while (indv != -1) {
                                this.cm.setCredentialForGrid(usr, Id, gsVal.trim());
                                indx = indv + 1;
                                indv = sor.indexOf(";", indx);
                                if (indv != -1) {
                                    gsVal = new String(sor.substring(indx, indv));
                                }
                            }
                        }
                    }
                    in.close();
                } catch (Exception e) {
                    System.out.println("loadUsrCert ERROR:" + e);
                }
            } else {
                System.out.println("loadUsrCert: - already loaded");
            }
        } else {
            System.out.println("loadUsrCert: - no saved certs");
        }
        return " ";
    }

    /**
     * Deletes the SAML file.
     * @param usrid String
     */
    private void deleteSAML(String usrid) {
        File samlf = getSAML(usrid);
        if (samlf.exists()) {
            samlf.delete();
        } else {
            System.out.println("MyProxyPortlet::deleteSAML::file is missing");
        }
    }

    /**
     * Delete proxy certificate
     * @param usr String containing user id
     * @param key SZGStoreKey containing proxy
     */
    private void deleteCertFile(String usr, SZGStoreKey key) {
        try {
            String usrCert = this.loadUsersDirPath() + usr + "/x509up";
            SZGCredential[] creds = this.cm.getCredentials(usr);
            if (creds != null) {
                int credNum = creds.length;
                for (int j = 0; j < credNum; j++) {
                    SZGCredential cred = (SZGCredential) creds[j];
                    String credId = cred.getId();
                    if (credId.equals(key.getCredId().toString())) {
                        String[] gs = SZGCredentialManager.getInstance().getSetGridsForCredential(usr, credId, true);
                        this.cm.removeCredential(key);
                        if (gs != null && gs.length != 0) {
                            for (int i = 0; i < gs.length; i++) {
                                String filep = new String(usrCert + "." + gs[i]);
                                System.out.println("delete cert:"+filep);
                                File f = new File(filep);
                                if (f.exists()) {
                                    f.delete();
                                }
                            }
                        }
                    }
                }

            } else {
//                System.out.println("deleteCertFile() creds==null");
            }
        } catch (Exception e) {
            System.out.println("deleteCertFile ERROR: " + e);
        }
    }

    /**
     * Checks if the required fields are filled in.
     * @param fields Array containing the fieldsfrom the GUI
     * @return Boolean
     */
    private boolean checkFieldsFilled(String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (fields[i] == null) {
                return false;
            } else if (fields[i].equals("")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the user directory's path.
     * @return String
     * @throws Exception
     */
    private String loadUsersDirPath() throws Exception {
        return PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/";
    }

    /**
     * Checks if the lifetime is correct ( lifetime is in seconds.)
     * @param lifetime Integer containing lifetime
     * @return
     */
    private int correctLifetime(int lifetime) {
        if (lifetime < 3600) {
            lifetime = 3600;
        }
        if (lifetime > 360000) {
            lifetime = 360000;
        }
        return lifetime;
    }
}
