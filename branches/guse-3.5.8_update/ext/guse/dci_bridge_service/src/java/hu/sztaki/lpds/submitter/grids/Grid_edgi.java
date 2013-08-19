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
package hu.sztaki.lpds.submitter.grids;

import dci.data.Item;
import dci.data.Item.*;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.service.LB;

import hu.sztaki.lpds.dcibridge.util.BinaryHandler;
import hu.sztaki.lpds.dcibridge.util.InputHandler;
import hu.sztaki.lpds.dcibridge.util.OutputHandler;
import hu.sztaki.lpds.dcibridge.util.XMLHandler;
import hu.sztaki.lpds.submitter.grids.glite.config.GLiteConfig;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.List;
import java.util.Collections;
import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.xml.namespace.QName;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.glite.jdl.JobAd;
import org.glite.wms.wmproxy.AuthenticationFaultException;
import org.glite.wms.wmproxy.AuthorizationFaultException;
import org.glite.wms.wmproxy.JobIdStructType;
import org.glite.wms.wmproxy.ServerOverloadedFaultException;
import org.glite.wms.wmproxy.ServiceException;
import org.glite.wms.wmproxy.StringAndLongList;
import org.glite.wms.wmproxy.StringAndLongType;
import org.glite.wms.wmproxy.WMProxyAPI;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.io.urlcopy.UrlCopy;
import org.globus.util.GlobusURL;
import org.globus.util.Util;
import org.ietf.jgss.GSSCredential;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.SDLType;


/**
  * edgi plugin
  */
public class Grid_edgi extends Middleware {
   	private final boolean DEBUG = false;
    final static String JDKEY="edgi.key";
    final static Object vomsLock = new Object();
    static Hashtable userproxy = new Hashtable(); //user proxy creation time
    private WMProxyAPI client=null;

    /**
     * Constructor
     */
    public Grid_edgi () throws Exception {
        THIS_MIDDLEWARE = Base.MIDDLEWARE_EDGI;
        threadID ++;
        setName("guse/dci-bridge:Middleware handler(edgi) - " + threadID);

        //System.out.println("Plugin " + THIS_MIDDLEWARE + " has been constructed");
    }

    /**
     * Update middleware configuration
     * @throws Configuration/infrastructure exception
     */
    @Override
    public void setConfiguration () throws Exception {

        // for all edgi item
        for(Item itemElement: Conf.getMiddleware(THIS_MIDDLEWARE).getItem()) {
            // item: <middleware type="edgi"><item name="edgiGivenName">...
            Edgi edgiElement = itemElement.getEdgi();
            String urlElement = edgiElement.getUrl();
            List <Item.Edgi.Job> jobList = edgiElement.getJob();
            
            // update (overwrite) job list (EDGI AR applications)
            jobList.clear();
            queryEDGIApplications(urlElement, jobList);
        }

        Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: configuration updated successfully"));
    }

    /**
      * Get application names (along with VOs and CEs) from an EDGI application repository accessible at the given repositoryURL (parameter)
      * Add each application as a "job" to the jobList (parameter), and fill in the related VO and CE substructure
      *
      *  Note: Those applications (implementations) are returned only that have:
      *    (1) VO attribute of the form: VOs.voXXX.name=gLite:VVV (XXX is a VO id number, VVV is the VO site), and
      *    (2) this VO has at least one CE site (VOs.voXXX.site=CCC, where XXX is the VO id and CCC is the CE site)
      *
      * @param repositoryURLPar The URL of the repository at which servlets "/mce/getapps", "/mce/getimps", and "/mce/getfileurls" are avaible
      * @param jobList The list of jobs (initialy empty) into which new jobs to be added. This is the output of the method.
      *
      * @throws MalformedURLException, IOException If any problem occurs at opening/reading from the repositoryURL
      */

	private void queryEDGIApplications (final String repositoryURLPar, List <Item.Edgi.Job> jobList) throws MalformedURLException, IOException {
        final boolean POST = true; // use POST method to the URL to avoid GET's 255 byte limit
        boolean warning; // print warning once if repository line syntax differs from the expected

        final String GET_APPS_SERVLET = "mce/getapps";
        final String GET_APP_RESP_REGEXP = "^\\d+ .+"; // 1001 dsp

        final String GET_IMPS_SERVLET = "mce/getimps";
        final String GET_IMPS_RESP_REGEXP = "^\\d+ \\d+"; // 1001 1101

        final String GET_IMP_ATTRS_SERVLET = "mce/getimpattr";
        final String GET_IMP_ATTRS_RESP_REGEXP = "^\\d+ .+=.*";

        final String GET_IMP_FILE_URL_SERVLET = "mce/getfileurls";
        final String GET_IMP_FILE_URL_RESP_REGEXP = "^\\d+ .+";

		// temp variables
		URL url;
		URLConnection conn;
		BufferedReader rd;
		String line;

		// maintains association: application ID -> application name
		Hashtable <String, String> apps = new Hashtable <String, String> (); // [APP_ID] -> [APP_NAME]

		// maintains association: implementation ID -> application ID
		Hashtable <String, String> imps = new Hashtable <String, String> (); // [IMP_ID] -> [APP_ID]

		// maintains association: implementation ID -> URL of the executable
		Hashtable <String, Vector<String>> fileUrls = new Hashtable <String, Vector<String>> (); // [IMP_ID] -> [URL] +

		// maintains association: implementation ID -> list of VO IDs (vo001, vo002, ...)
		Hashtable <String, Vector<String>> voIds = new Hashtable <String, Vector<String>> (); // [IMP_ID] -> [VO_ID] +

		// maintains association: implementation ID + '_' + VO ID -> VO name (1102_vo002 -> gilda)
		Hashtable <String, String> voNames = new Hashtable <String, String> (); // [IMP_ID]_[VO_ID] -> [VO_NAME]

		// maintains association: implementation ID + '_' + VO ID -> list of CE URLs (1102_vo001 -> ce1.grid.edges-grid.eu:2119/jobmanager-edges-extremadura, ce1.grid.edges-grid.eu:2119/jobmanager-edges-uow, ...)
		Hashtable <String, Vector<String>> ceUrls = new Hashtable <String, Vector<String>> (); // [IMP_ID]_[VO_ID] -> [CE_URL] +

		// maintains association: application name -> URLs of the executable
		Hashtable <String, Vector<String>> appFileUrls = new Hashtable <String, Vector<String>> (); // [APP_NAME] -> [URL] +

		// add '/' to the end of the URL if not present
		String repositoryURL = repositoryURLPar.endsWith("/") ? repositoryURLPar : repositoryURLPar + "/";

		// maintains association: implementation ID -> executable/bundle
		Hashtable <String, String> exeFile = new Hashtable <String, String> (); // [IMP_ID] -> [executable]

		// maintains association: implementation ID -> executable/bundle
		Hashtable <String, String> appExeFile = new Hashtable <String, String> (); // [APP_NAME] -> [executable]
        
		// get all AR applications
		if (DEBUG) System.out.println("Getting APPLICATIONS...");
        warning = false;
		url = new java.net.URL(repositoryURL + GET_APPS_SERVLET);
		conn = url.openConnection ();
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			if (!line.matches(GET_APP_RESP_REGEXP)) {
                if (!warning) {
                    Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: application repository line has invalid syntax: " + line));
                    warning = true;
                }
                continue;
            } // line syntax invalid: skip
			String appId = line.substring(0, line.indexOf(' '));
			String appName = line.substring(line.indexOf(' ') + 1).trim();
			apps.put(appId, appName);
			if (DEBUG) System.out.println("\t" + appName +  " " + appId + " ");
		}
		rd.close();

   		if (apps.size() == 0) {
            Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: no application found in the repository"));
            return;
        } // no app at all, return

		// create '+' concatenated list of app ids (in order to get all imps in one HTTP request)
		StringBuffer appIdListBuffer = new StringBuffer();
		for (String appId: apps.keySet()) appIdListBuffer.append("+" + appId);
		String appIdList = appIdListBuffer.toString();
		appIdList = appIdList.substring(1); // cut leading '+'

		// get application implementations
		if (DEBUG) System.out.println("\nGetting application IMPLEMENTATIONS...");
        warning = false;
        if (POST) {
            if (DEBUG) System.out.println("Querying application implementations using POST method");
            url = new java.net.URL(repositoryURL + GET_IMPS_SERVLET + "");
            conn = url.openConnection ();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            String pars = "appids=" + appIdList;
            wr.write(pars);
            wr.flush();
        } else {
            url = new java.net.URL(repositoryURL + GET_IMPS_SERVLET + "?appids=" + appIdList); // TODO: use POST if possible (GET paramter limit is 255 bytes, usually)
            conn = url.openConnection ();
        }
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			if (!line.matches(GET_IMPS_RESP_REGEXP)) {
                if (!warning) {
                    Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: application implementation repository line has invalid syntax: " + line));
                    warning = true;
                }
                continue;
            } // line syntax invalid: skip
			String appId = line.substring(0, line.indexOf(' '));
			String impId = line.substring(line.indexOf(' ') + 1).trim();
			if (DEBUG) if (imps.containsKey(impId) && !appId.equals(imps.get(impId))) if (DEBUG) System.out.println("WARNING: same implemenation but different apps");
			imps.put(impId, appId);
			if (DEBUG) System.out.println("\t" + apps.get(appId) + " " + impId + "");
		}
		rd.close();

        if (imps.size() == 0) {
            Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: no implementation found in the repository"));
            return;
        } // no imp at all, return

		// create '+' concatenated list of imp ids (in order to get all imp attributes in one HTTP request)
		StringBuffer impIdListBuffer = new StringBuffer();
		for (String impId: imps.keySet()) impIdListBuffer.append("+" + impId);
		String impIdList = impIdListBuffer.toString();
		impIdList = impIdList.substring(1); // cut leading '+'

		// NOT USED: get implementation (executable) file URLs
		if (DEBUG) System.out.println("\nGetting implementation URLS...");
        warning = false;
        if (POST) {
            url = new java.net.URL(repositoryURL + GET_IMP_FILE_URL_SERVLET + "");
            conn = url.openConnection ();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            String pars = "impids=" + impIdList;
            wr.write(pars);
            wr.flush();
        } else {
    		url = new java.net.URL(repositoryURL + GET_IMP_FILE_URL_SERVLET + "?impids=" + impIdList);
    		conn = url.openConnection ();
        }
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			if (!line.matches(GET_IMP_FILE_URL_RESP_REGEXP)) {
                if (!warning) {
                    Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: application executable repository line has invalid syntax: " + line));
                    warning = true;
                }
                continue;
            } // line syntax invalid: skip
			String impId = line.substring(0, line.indexOf(' '));
			String fileUrl = line.substring(line.indexOf(' ') + 1).trim();

			if (!fileUrls.containsKey(impId)) {
				Vector <String> tempFileUrls = new Vector <String> ();
				tempFileUrls.add(fileUrl);
				fileUrls.put(impId, tempFileUrls);
			} else {
				fileUrls.get(impId).add(fileUrl);
			}

			if (DEBUG) System.out.println("\t" + impId + " " + fileUrl + "");
		}
		rd.close();

		// get implementation attributes
		if (DEBUG) System.out.println("\nGetting implementation ATTRIBUTES...");
        warning = false;
        if (POST) {
            url = new java.net.URL(repositoryURL + GET_IMP_ATTRS_SERVLET + "");
            conn = url.openConnection ();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            String pars = "impids=" + impIdList;
            wr.write(pars);
            wr.flush();
        } else {
    		url = new java.net.URL(repositoryURL + GET_IMP_ATTRS_SERVLET + "?impids=" + impIdList); // TODO: use POST
    		conn = url.openConnection ();
        }
		rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = rd.readLine()) != null) {
			if (!line.matches(GET_IMP_ATTRS_RESP_REGEXP)) continue; // line syntax invalid: skip
			String impId = line.substring(0, line.indexOf(' '));

			line = line.substring(line.indexOf(' ') + 1);
			String attrName = line.substring(0, line.indexOf('='));
			String attrValue = line.substring(line.indexOf('=') + 1).trim();

            if (attrName.startsWith("executable/bundle")){
                if (DEBUG) System.out.println("\t" + impId + " " + attrName + " (" + attrValue + ")");
                exeFile.put(impId, attrValue);
			} else if (attrName.matches("^VOs[.]vo\\d+[.]name") && attrValue.matches("^gLite:.+")) { // e.g. VOs.vo0001.name=gLite:desktopgrid.vo.edges-grid.eu
				String voId = attrName.substring(attrName.indexOf('.') + 1, attrName.lastIndexOf('.')); // e.g. vo0001
				String voName = attrValue.substring(attrValue.indexOf("gLite:") + 6); // e.g. desktopgrid.vo.edges-grid.eu
				if (DEBUG) System.out.println("\t" + impId + " " + voName + " (" + voId + ")");
				// add the new VO ID to the implementation ID
				if (voIds.containsKey(impId)) {
					voIds.get(impId).add(voId);
				} else {
					Vector <String> tempVoIds = new Vector <String> ();
					tempVoIds.add(voId);
					voIds.put(impId, tempVoIds);
				}
				// a unique key is constructed: impId + '_' + voId
				String key = impId + "_" + voId;
				// store VO name associated with impId_voId
				voNames.put(key, voName);

			} else if (attrName.matches("^VOs[.]vo\\d+[.]site\\d+") && attrValue.matches(".+")) { // e.g. VOs.vo0001.site0001=ce1.grid.edges-grid.eu:2119/jobmanager-edges-extremadura
				String voId = attrName.substring(attrName.indexOf('.') + 1, attrName.lastIndexOf('.')); // e.g. vo0001
				String ceUrl = attrValue;
				if (DEBUG) System.out.println("\t" + impId + " " + ceUrl + " (" + voId + ")");
				// a unique key is constructed: impId + '_' + voId
				String key = impId + "_" + voId;
				// store CE URLs associated with impId_voId
				if (ceUrls.containsKey(key)) {
					ceUrls.get(key).add(ceUrl);
				} else {
					Vector <String> tempCeUrls = new Vector <String> ();
					tempCeUrls.add(ceUrl);
					ceUrls.put(key, tempCeUrls);
				}
			}
		}
		rd.close();

		// create the final structure of apps having VO(s) and CE(s)
		Hashtable <String, Hashtable<String, Vector<String>>> finalApps = new Hashtable <String, Hashtable<String, Vector<String>>> ();
		for (String impId: imps.keySet()) {
            
			if (voIds.containsKey(impId)) {

				for (String voId: voIds.get(impId)) {
					String key = impId + "_" + voId;

					final String appName = apps.get(imps.get(impId));
					final String voName = voNames.get(key);

					Hashtable <String, Vector <String>> tempVoHash = null; // vo name -> ce list
					Vector <String> tempCeList = null; // ce list

					if (ceUrls.containsKey(key)) {

						for (String ceUrl: ceUrls.get(key)) {
							// if this app is not yet stored
							if (!finalApps.containsKey(appName)) {
								tempCeList = new Vector <String> ();
								tempCeList.add(ceUrl);
								tempVoHash = new Hashtable <String, Vector <String>> ();
								tempVoHash.put(voName, tempCeList);
								finalApps.put(appName, tempVoHash);
							} else {
								tempVoHash = finalApps.get(appName);
								if (!tempVoHash.containsKey(voName)) {
									tempCeList = new Vector <String> ();
									tempCeList.add(ceUrl);
									tempVoHash.put(voName, tempCeList);
								} else {
									tempCeList = tempVoHash.get(voName);
									if (!tempCeList.contains(ceUrl)) tempCeList.add(ceUrl);
									else if (DEBUG) System.out.println("WARNING: Duplicate CE URL: " + ceUrl + " " + appName + "(" + impId + ") VO:" + voName + "(" +  voId + ") " );
								}
							}
                            //System.out.println("-----impId:" + impId + "appName:" + appName + "");
                            if (fileUrls.containsKey(impId)) {
                                //System.out.println("-----impId:" + impId + "appName:" + appName + "--CONTAINS!!!!");
                                Vector<String> fs = fileUrls.get(impId);
                                if (DEBUG) for (String file : fs) {
                                    System.out.println("-----impId:" + impId + "appName:" + appName + "--file:" + file);
                                }
                                appFileUrls.put(appName, fs);
                            }//no exe files
                            if (exeFile.containsKey(impId)){
                                String pexe = exeFile.get(impId).trim();
                                if (DEBUG) System.out.println("-----exeFile-impId:"+impId+" pexe:"+pexe);
                                if (!"".equals(pexe)){
                                    appExeFile.put(appName, pexe);
                                }
                            }
						}
					} // no CE for VO
				}                
			} // no VO ID of imp
		}

       if (finalApps.size() == 0) {
            Base.writeLogg(THIS_MIDDLEWARE, new LB(THIS_MIDDLEWARE + " plugin: no application found with VO.name (gLite) and CE.site attributes"));
            return;
        } // no gLite apps at all, return

        if (DEBUG) {
            System.out.println("Listing APPs, VOs, and CEs...");
            for (String app: finalApps.keySet()) {
                System.out.println("\n\"" + app + "\"");
                Hashtable<String, Vector<String>> vos = finalApps.get(app);
                for (String vo: vos.keySet()) {
                    System.out.println("\tVO: " + vo);
                    Vector <String> ces = vos.get(vo);
                    for (String ce: ces) {
                        System.out.println("\t\tCE: " + ce);
                    }
                }
            }
        }

        // order by application name
        Vector <String> sortedApps =  new Vector <String> ();
        for (String appName: finalApps.keySet()) sortedApps.add(appName);
        Collections.sort(sortedApps, new AppNameComparator());

        // fill in job structure
        //for (String appName: finalApps.keySet()) {
        for (String appName: sortedApps) {

            Item.Edgi.Job job = new Item.Edgi.Job();
            job.setName(appName);
            if (appExeFile.containsKey(appName)) {
                job.setExecutable(appExeFile.get(appName));
            } else {
                job.setExecutable("");
            }
            try {
                Vector<String> exeurls = appFileUrls.get(appName);
                for (String eurl : exeurls) {
                    //System.out.println("EXEurl for " + appName + " url:" + eurl);
                    Edgi.Job.Exeurl exeurl = new Edgi.Job.Exeurl();
                    exeurl.setUrl(eurl);
                    job.getExeurl().add(exeurl);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            jobList.add(job);

            Hashtable<String, Vector<String>> vos = finalApps.get(appName);
            for (String voName: vos.keySet()) {

                Item.Edgi.Job.Vo vo = new Item.Edgi.Job.Vo();
                vo.setName(voName);
                job.getVo().add(vo);

                Vector <String> ces = vos.get(voName);
                for (String ceName: ces) {

                    vo.getCe().add(ceName);

                }
            }
       }
	}

    class AppNameComparator implements Comparator <String> {
        private Collator collator = null;
        AppNameComparator () { collator = Collator.getInstance(new Locale("en","US")); } // TODO: use acutal locale
        public int compare(String o1, String o2) {
            return collator.compare(o1.toUpperCase(), o2.toUpperCase());
        }
    }

    /**
     * Aborts the job
     * @param pJob
     */
    public void abort(Job pJob) {
        //GStatusHandler.getI().removeJob(pJob.getId());
        String path=Base.getI().getJobDirectory(pJob.getId());
        cleanupJob(pJob);
        errorLog(path+"outputs/", "- - - - - - - - \nABORTED by user");
        pJob.setStatus(ActivityStateEnumeration.CANCELLED);
    }

    /**
     * Abort job, and cleanup.
     * @param pJob
     */
    private void cleanupJob(Job pJob) {
        String path=Base.getI().getJobDirectory(pJob.getId());
        if (pJob.getMiddlewareId() != null) {
            synchronized (GLiteConfig.getI().getLock()) {
                try {
                    client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(pJob.getConfiguredResource().getVo()), path + "/x509up");
                    sysLog(path + "outputs/", " ABORT - jobcancel");
                    client.jobCancel(pJob.getMiddlewareId());
                } catch (Exception e) {
                    try {
                        sysLog(path + "outputs/", "ABORT - jobcancel failed -> jobPurge ...   reason:" + e.getMessage());
                        client.jobPurge(pJob.getMiddlewareId());
                    } catch (Exception ee) {
                        //ee.printStackTrace();
                    }
                } finally {
                    client = null;
                }
            }
        }
    }
    /**
     * Submits the job
     * @param pJob
     * @throws java.lang.Exception
     */
    protected void submit(Job pJob) throws Exception {
        String path = Base.getI().getJobDirectory(pJob.getId());
        JobDefinitionType jsdl = pJob.getJSDL();
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);
        
        //sysLog(path + "outputs/", "mv: " + pJob.getConfiguredResource().getMiddleware() + " vo: " + pJob.getConfiguredResource().getVo() + " host:" + pJob.getConfiguredResource().getResource() + " jobmanager:" + pJob.getConfiguredResource().getJobmanager());
        //sysLog(path + "outputs/", "SubmitTo: " +pJob.getJSDL().getJobDescription().getJobIdentification().getJobName());

        try {
            createJobad(pJob);

            if (!getProxy(path, pJob, userid, false)) {
                System.out.println("failed creating proxy");
                pJob.setStatus(ActivityStateEnumeration.FAILED);
                //Thread.sleep(5000);
                return;
            }
            doSubmit(pJob);


        } catch (Exception e) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            errorLog(path + "outputs/", "Job submit failed. ", e);
            e.printStackTrace();
        }



        //pJob.setStatus(ActivityStateEnumeration.RUNNING);

    // TODO:
    }

    /**
     * Is not used, the job output is get after getstatus or send by callback
     * @param pJob
     * @throws java.lang.Exception
     */
    protected void getOutputs (Job pJob) throws Exception {
        // TODO:
    }

    /**
     * Get job status
     * @param pJob
     */
    protected void getStatus (Job pJob) {
        gstatus(pJob);
    }



    private boolean cp(String inf, String outf) {
        try {
            File inputFile = new File(inf);
            File outputFile = new File(outf);
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int c;
            while ((c = in.read()) != -1) {
                out.write(c);
            }
            in.close();
            out.close();
            Util.setFilePermissions(outf, 600);
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

/**
 * Try to get proxy from cache, in failed create it
 * @param path
 * @param resource
 * @param userid
 * @param renew if TRUE, the proxy already exists, do not save it again as x509up_o
 * @return
 */
    private boolean getProxy(String path, Job pJob, String userid, Boolean renew) {
        String grid = pJob.getConfiguredResource().getVo();
        String role = "";
        try {
            role = pJob.getJSDL().getJobDescription().getResources().getOtherAttributes().get(QName.valueOf("gliterole"));
        } catch (Exception e) {
        }

        String proxy = path+"x509up";
        boolean success = true;
        synchronized (vomsLock) {
            try {
                if ((System.currentTimeMillis() - Long.parseLong("" + userproxy.get(userid + grid + role))) < 1800000) {
                    sysLog(path + "outputs/", "+--------- getProxy - use cache --------");
                    if (!renew) {
                        try {
                            new File(proxy).renameTo(new File(proxy + "_o"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            new File(proxy).delete();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    cp(getProxyCachedir() + userid + "/x509up." + grid+role, proxy);
                } else {
                    if (!renew) {
                        cp(proxy, proxy+"_o");
                    } else {
                        try {
                            new File(proxy).delete();
                            cp(proxy+"_o",proxy);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    success = createProxy(path, grid, userid, role);
                }
            } catch (Exception e) {
                sysLog(path + "outputs/", "+--------- getProxy - not cached --------");
                try {
                    cp(proxy, proxy+"_o");
                    //new File(proxy + "_o").renameTo(new File(proxy));
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                success = createProxy(path, grid, userid, role);
            }
        }
        return success;
    }

    /**
     * Adds voms extension to the jobs proxy
     * @param localDir
     * @param voname
     * @return boolean
     */
    private boolean addVomsC(String localDir, String voname, String role){
            try{
                //String addvomssext = PropertyLoader.getInstance().getProperty("prefix.dir")+"addvomsext/addvomsext";
                //String cmd =new String(addvomssext+" "+voname+" "+localDir+"/x509up"+ " "+localDir+"/x509upvoms");//
                String cmd; //="voms-proxy-init -voms "+voname+":/edgiprod.vo.edgi-grid.eu/Role=edgidemo -noregen -out x509up";// /**@todo Delete role!**/

                if ("".equals(role)){//role is not set
                    cmd ="voms-proxy-init -voms "+voname+": -noregen -out x509up";
                } else {//add role
                    cmd = "voms-proxy-init -voms " + voname + ":/" + voname + "/Role=" + role + " -noregen -out x509up";
                }

                sysLog(localDir + "outputs/", "localDir:"+localDir+" cmd:"+cmd);
                sysLog(localDir + "outputs/", cmd);
                Process p;
                p = Runtime.getRuntime().exec(cmd,null,new File(localDir));
                BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream() ));
                int exitv =p.waitFor();
                if (exitv==0){
                    sin.close();
                    return true;
                }
                else{
                    String sor="";
                    errorLog(localDir + "outputs/", cmd + " \n failed.");
                    while ((sor = sin.readLine()) != null) {
                      sysLog(localDir + "outputs/", sor);
                      errorLog(localDir + "outputs/", sor);
                    }
                    errorLog(localDir + "outputs/", "\n");
                    sin.close();
                    return false;
                }
            }catch(Exception e){
                //e.printStackTrace();
                return false;
            }
    }

    /*
     * adds voms extension and delegates the proxy
     */
    private boolean createProxy(String path, String grid, String userid, String role) {
        String OutputDir = path+"outputs/";
        String proxy = path+"x509up";
        sysLog(OutputDir, "+--------- getProxy - start - proxy:" + proxy + "-------");
        try {
            GlobusCredential agcred = new GlobusCredential(proxy);
            GlobusGSSCredentialImpl agssproxy = new GlobusGSSCredentialImpl(agcred, GSSCredential.INITIATE_AND_ACCEPT);
            sysLog(OutputDir, "rem.life: " + agssproxy.getRemainingLifetime());
            if (agssproxy.getRemainingLifetime() < 600) {
                throw new Exception("Certificate expired. ");
            }

            if (addVomsC(path, grid, role)) {
                sysLog(OutputDir, "VOMS ext. succesfully added");
            } else {
                throw new Exception("Add VOMS - error. The remaining lifetime of the proxy in the submission time: "+calculateHMS(agssproxy.getRemainingLifetime()));
            }

//            try {
//                String p = PropertyLoader.getInstance().getProperty("glite." + grid + ".sleep");
//                int is = Integer.parseInt(p);
//                sysLog(OutputDir, " sleep (s): " + is);
//                try {
//                    Thread.sleep(is * 1000);
//                } catch (Exception e2) {
//                    e2.printStackTrace();
//                }
//            } catch (Exception e) {
//                sysLog(OutputDir, "glite." + grid + ".sleep DISABLED ");
//            }

            synchronized (GLiteConfig.getI().getLock()) {
                client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                sysLog(OutputDir, "getProxyReq .....");
                String rproxy = "";
                int ntry = 0;
                boolean btry = true;
                while (btry) {//error handling, retry if service call fails
                    try {
                        rproxy = client.getProxyReq(userid+grid+role);//delegationId
                        btry = false;
                    } catch (AuthenticationFaultException e1){
                        //System.out.println("***                       !!! W A R N I N G !!!      Syncronise system clock!                ***");
                        retryOrThrowException(OutputDir, ntry++, 1, "Authentication Failed: " + e1.getMessage());
                    } catch (AuthorizationFaultException e2){
                        //System.out.println("***                       !!! W A R N I N G !!!      Syncronise system clock!                ***");
                        retryOrThrowException(OutputDir, ntry++, 1, "Authorization Failed: " + e2.getMessage());
                    } catch (ServerOverloadedFaultException e3){
                        retryOrThrowException(OutputDir, ntry++, 6, "Server is Overloaded: " + e3.getMessage());
                    } catch (ServiceException e4) {
                        if (e4.getMessage().contains("unsupported_certificate")){
                            throw new Exception("Your proxy is not valid! Please upload your user cert and key from command line to the myproxy server and download the new proxy. " +
                                    "You can use the following command:\n\n" +
                                    "myproxy-init -s myproxy.server.hostname -l MyProxyAccount -c 0 -t 100" +
                                    " \n\n\n WMS Service Error: " + e4.getMessage());//throw new Exception(excmsg);
                        }
                        retryOrThrowException(OutputDir, ntry++, 2, "WMS Service Error: " + e4.getMessage());
                    }
                }
                sysLog(OutputDir, "getProxyReq result [" + rproxy + "]");
                sysLog(OutputDir, "grstPutProxy .....");
                client.grstPutProxy(userid+grid+role, rproxy);//delegationId=userid
            }
        } catch (Exception exc) {
            errorLog(OutputDir, "Preparation of the proxy for VO "+ grid + " failed. " , exc);
            sysLog(OutputDir, exc.toString());
            //exc.printStackTrace();
            //status = 7;// "Aborted";
            client = null;
            return false;
        } finally {
            client = null;
        }
        sysLog(OutputDir, "+-----------------------------------getProxy - succes-----------------------------------+");
        if (!new File(getProxyCachedir() + userid).exists()){
            new File(getProxyCachedir() + userid).mkdirs();
        }
        new File(getProxyCachedir() + userid + "/x509up."+grid+role).delete();
        cp(proxy,getProxyCachedir() + userid + "/x509up."+grid+role);
        userproxy.put("" + userid+grid+role, System.currentTimeMillis());
        sysLog(OutputDir, "userproxy creation times:"+userproxy);
        return true;
    }

    private void retryOrThrowException(String logdir, int numoftry, int maxnumoftry, String excmsg) throws Exception {
        if (numoftry < maxnumoftry) {
            sysLog(logdir, numoftry + "/" + maxnumoftry + " sleep 10sec msg:" + excmsg);
            try {
                Thread.sleep(10000);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else {
            throw new Exception(excmsg);
        }
    }

    /*
     * Converts seconds to hour:min:sec format string
     */
    private String calculateHMS(int timeInSeconds) {
        int hours, minutes, seconds;
        hours = timeInSeconds / 3600;
        timeInSeconds = timeInSeconds - (hours * 3600);
        minutes = timeInSeconds / 60;
        timeInSeconds = timeInSeconds - (minutes * 60);
        seconds = timeInSeconds;
        return (hours + ":" + minutes + ":" + seconds);
    }

    private String getProxyCachedir(){
        return Base.getI().getPath()+"proxycache/";
    }

    /**
     * Upload local inputs, and submit.
     * @param pJob
     * @return
     * @throws java.lang.Exception
     */
    private boolean doSubmit(Job pJob) throws Exception {
        POSIXApplicationType pType = XMLHandler.getData(pJob.getJSDL().getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
        String userid = BinaryHandler.getUserName(pType);
        String path = Base.getI().getJobDirectory(pJob.getId());
        String grid = pJob.getConfiguredResource().getVo();
        String proxy = path + "/x509up";
        String role = "";
        try {
            role = pJob.getJSDL().getJobDescription().getResources().getOtherAttributes().get(QName.valueOf("gliterole"));
        } catch (Exception e) {
        }
        String OutputDir = path + "outputs/";

        String[] reduced_path = null;

        sysLog(OutputDir, "+--------- doSubmit Contacting:" + GLiteConfig.getI().getWMProxyUrl(grid) + " -------");
        try {
            List<DataStagingType> inputs = InputHandler.getlocalInputs(pJob);
            // Reads JDL
            JobAd jad = new JobAd();
            jad.fromFile(path + "outputs/job.jdl");
            String jdlString = jad.toString();

            int ntry = 0;
            boolean btry = true;
            while (btry) {// error handling, retry if service fails
                try {
                    sysLog(OutputDir, "jobRegister... try:" + ntry);
                    synchronized (GLiteConfig.getI().getLock()) {
                        if (pJob.getMiddlewareId() == null || "".equals(pJob.getMiddlewareId())) { //if the jobregister was not successfull
                            client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                            JobIdStructType jobIDurl = client.jobRegister(jdlString, userid+grid+role);//delegationId

                            if (jobIDurl != null) {
                                pJob.setMiddlewareId(jobIDurl.getId());
                            } else {
                                sysLog(OutputDir, "SUBMIT E R R O R ! - Job submission failed. ");
                                errorLog(OutputDir, "Job submission failed. ");
                                client = null;
                                return false;
                            }
                        }

                        if (inputs.size() > 0) {
                            org.glite.wms.wmproxy.StringList InputSandboxURI = client.getSandboxDestURI(pJob.getMiddlewareId(), "gsiftp");
                            //Convert listURI into a String.
                            reduced_path = InputSandboxURI.getItem();
                        }
                        btry = false;
                    }
                } catch (ServerOverloadedFaultException e3) {
                    retryOrThrowException(OutputDir, ntry++, 6, "Server is Overloaded: " + e3.getMessage());
                } catch (ServiceException e4) {
                    retryOrThrowException(OutputDir, ntry++, 2, "WMS Service Error: " + e4.getMessage());
                }
            }
            
            sysLog(OutputDir, " submit successfull jobID:" + pJob.getMiddlewareId());
            FileWriter furl = new FileWriter(path + "outputs/job.url");
            BufferedWriter out = new BufferedWriter(furl);
            out.write("" + pJob.getMiddlewareId());
            out.flush();
            out.close();
                    
            if (inputs.size() > 0) {
                // Creation of the "toURL" link to copy the file(s).
                int pos = (reduced_path[0]).indexOf("2811");
                int length = (reduced_path[0]).length();
                String front = (reduced_path[0]).substring(0, pos);
                String rear = (reduced_path[0]).substring(pos + 4, length);
                String TURL = front + "2811/" + rear;
                GlobusCredential gcred = new GlobusCredential(proxy);
                GSSCredential gssproxy = new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);


                //uploads local inputs
                for (DataStagingType inp : inputs) {
                    String toURL = TURL + "/" + inp.getFileName();
                    String fromURL = "file:///" + path + "/" + inp.getFileName();
                    sysLog(OutputDir, "fromURL:" + fromURL + " toURL:" + toURL);
                    try {
                        GlobusURL from = new GlobusURL(fromURL);
                        GlobusURL to = new GlobusURL(toURL);
                        UrlCopy uCopy = new UrlCopy();
                        uCopy.setCredentials(gssproxy);
                        uCopy.setDestinationUrl(to);
                        uCopy.setSourceUrl(from);
                        uCopy.setUseThirdPartyCopy(true);
                        uCopy.copy();
                    } catch (Exception e) {
                        sysLog(OutputDir, "Can not copy the Input files:" + inp.getFileName() + " - " + e.toString());
                        errorLog(OutputDir, "Can not copy the Input files:" + inp.getFileName() + " - " + e.getMessage());
                        //e.printStackTrace();
                        client = null;
                        return false;
                    }
                }
            }
            
            ntry = 0;
            btry = true;
            while (btry) {//error handling, retry if service call fails
                try {
                    synchronized (GLiteConfig.getI().getLock()) {
                        client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                        client.jobStart(pJob.getMiddlewareId());
                        btry = false;
                    }
                } catch (ServerOverloadedFaultException e3) {
                    retryOrThrowException(OutputDir, ntry++, 6, "Server is Overloaded: " + e3.getMessage());
                } catch (ServiceException e4) {
                    retryOrThrowException(OutputDir, ntry++, 2, "WMS Service Error: " + e4.getMessage());
                }
            }

            //GStatusHandler.getI().initJobStatus(userid, pJob.getId(), 0);//init  //DISABLED
            pJob.setStatus(ActivityStateEnumeration.PENDING);
            pJob.setResource(grid + " WMS");

            //delete local input 
            for (DataStagingType inp : inputs) {
                //jobad.addAttribute("InputSandbox", inp.getFileName());
                try {
                    File delfile = new File(path + "/" + inp.getFileName());
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception exc) {
            errorLog(OutputDir, "Job submit ERROR. \n " + exc.getMessage());
            sysLog(OutputDir, exc.toString());
            exc.printStackTrace();
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            client = null;
            return false;
        } finally {
            client = null;
        }
        sysLog(OutputDir, "+-----------------------------------doSubmit - succes-----------------------------------+");
        return true;
    }

    /**
     * Generates the JDL
     * @param pJob
     * @return
     */
    private JobAd createJobad(Job pJob){
        JobDefinitionType jsdl=pJob.getJSDL();
        String path=Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType=XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
//        String stdErr=BinaryHandler.getStdErrorFileName(pType);
//        String stdOut=BinaryHandler.getStdOutFileName(pType);
        String OutputDir = path + "outputs/";        

        JobAd jobad = new JobAd() ;
        try{

            //jobad.addAttribute("VirtualOrganisation", pJob.getConfiguredResource().getVo());            
            //jobad.addAttribute("JobType", "Normal");            
            String params = "";
            for (String t : BinaryHandler.getCommandLineParameter(pType)) {
                params = params.concat(" " + t);
            }
            if (!"".equals(params)) {
                jobad.addAttribute("Arguments", params);
            }

//            jobad.addAttribute("StdOutput", stdOut);
//            jobad.addAttribute("StdError", stdErr);
//            jobad.addAttribute("StdOutput", "test.out");
//            jobad.addAttribute("StdError", "test.err");

            HashMap<String,String> inputsandbox = new HashMap<String,String>();//avoid inputsandbox duplications, otherwise jdl submission will fail
            //add executable
            String ar = jsdl.getJobDescription().getResources().getOtherAttributes().get(QName.valueOf("ar"));
            Item.Edgi config = Conf.getItem(Base.MIDDLEWARE_EDGI, ar).getEdgi();
            String firstexe = null;     
            String exebundle = null;//executable/bundle from AR
            List<dci.data.Item.Edgi.Job> jobsinar = config.getJob();
            for (dci.data.Item.Edgi.Job arjob : jobsinar){
                if (arjob.getName().equals(jsdl.getJobDescription().getJobIdentification().getJobName())){
                    exebundle = arjob.getExecutable();
                    List<Edgi.Job.Exeurl> exeurls = arjob.getExeurl();
                    for (Edgi.Job.Exeurl eurl : exeurls){
                        if (firstexe == null){
                            firstexe = eurl.getUrl();
                        }
                        inputsandbox.put(eurl.getUrl(), "");                        
                    }
                    break;
                }
            }
            
            try {
                String exetostart;
                if (!"".equals(exebundle)){//executable/bundele is set in ar
                    exetostart = exebundle;
                }else{
                    exetostart = firstexe.substring(firstexe.lastIndexOf("/") + 1);
                    errorLog(OutputDir, "Remote executable/bundele not found or an error occurred, set it to: " + jsdl.getJobDescription().getJobIdentification().getJobName());
                }                 
                jobad.addAttribute("Executable", exetostart);
            } catch (Exception e) {
                errorLog(OutputDir, "Remote exe not found or an error occurred, set it to: " + jsdl.getJobDescription().getJobIdentification().getJobName());
                jobad.addAttribute("Executable", jsdl.getJobDescription().getJobIdentification().getJobName());
            }         

            // add remote inputs
            List<DataStagingType> rinputs = InputHandler.getRemoteInputs(pJob);
            for (DataStagingType t : rinputs) {
                inputsandbox.put(t.getSource().getURI(), "");
            }
            //add local inputs
            List<DataStagingType> inputs = InputHandler.getlocalInputs(pJob);
            for (DataStagingType inp : inputs) {
                inputsandbox.put(inp.getFileName(),"");
            }
           
            for (String input : inputsandbox.keySet()){
                jobad.addAttribute("InputSandbox", input);
            }
            //add local outputs
            for (String outpname : getOutputSandboxFileNames(pJob)) {
                    jobad.addAttribute("OutputSandbox", outpname);
            }
            //add remote output
            List<DataStagingType> routputs = OutputHandler.getRemoteOutputs(pJob.getJSDL());
            for (DataStagingType t : routputs) {
                //w.writeln("lcgdownload " + grid + " \"" + t.getSource().getURI() + "\" \"" + t.getFileName() + "\" ");
                jobad.addAttribute("OutputSandbox", t.getSource().getURI());
            }

            //jobad.addAttribute("OutputSandbox", "" + stdErr);//error.log is in outputs
            //jobad.addAttribute("OutputSandbox", "" + stdOut);//stdout.log is in outputs

            /*add user specific jdl attributes */
            SDLType sdlType = XMLHandler.getData(jsdl.getAny(), SDLType.class);
            List ls = sdlType.getConstraints().getOtherConstraint();
            Iterator it = ls.iterator();

            String userreq="";

            String envVars = null;
            while (it.hasNext()) {
                OtherType value = (OtherType) it.next();
                //System.out.println("Value :" + value.getName()+" = "+value.getValue());

                if(value.getName().indexOf(JDKEY)>-1){
                    String sKey = value.getName().replaceAll(JDKEY,"");
                    String sValue = value.getValue();
                    sysLog(OutputDir, "KEY: "+sKey);
                    sysLog(OutputDir, " Value: "+sValue);
                    if (!(sValue).trim().equals("")){
                        try{
                            if (sKey.equals("requirements")){
                                if (sValue.contains("other.GlueCEStateStatus") ){
                                    userreq=sValue;
                                }else{
                                    userreq="(other.GlueCEStateStatus==\"Production\") && ("+sValue+")";
                                }
                            }else if (sKey.equals("Rank")){
                                jobad.setAttributeExpr(sKey,sValue);
                            }else if (sKey.equals("ShallowRetryCount") ){
                                jobad.addAttribute(sKey,Integer.parseInt( sValue ));
                            }else if (sKey.equals("RetryCount") ){
                                jobad.addAttribute(sKey,Integer.parseInt( sValue ));
                            }else if (sKey.equals("Environment") ){
                                sysLog(OutputDir,"Environment:"+sValue);
                                envVars = sValue;
                                //jobad.setAttribute("Environment",""+sValue );
                            }else{
                                jobad.addAttribute(sKey, sValue);
                            }
                        }catch (Exception e){
                            //e.printStackTrace();
                                //sysLog(e.toString());errorLog(e.getMessage());
                        }
                    }
                }
            }

            try {
                String myproxy = sdlType.getConstraints().getMiddleware().get(0).getMyProxy().getServerName();
                jobad.addAttribute("MyProxyServer", myproxy);
            } catch (Exception e) {
                //e.printStackTrace();
            }

            //mandatory..
            //hibas siteok tiltasa
            String ds = "";
            String req = "";
//            Object[] sites = DisabledHost.getI().getAll().keySet().toArray();
//            for (int s = 0; s < sites.length; s++) {
//                long disabled = System.currentTimeMillis() - DisabledHost.getI().get((String)sites[s]);//Long.parseLong("" + disablehost.get(sites[s]));
//                if (disabled > maxdisablehost) {//engedelyezni
//                    DisabledHost.getI().remove((String)sites[s]);
//                } else {//tilt, ha userreq -ben nincs benne a host
//                    if (!userreq.contains(""+sites[s])){
//                        ds = ds.concat("other.GlueCEInfoHostname!=\"" + sites[s] + "\" ");
//                    }
//                }
//            }
            ds = ds.trim().replaceAll(" ", " && ");
            if (!"".equals(ds) && !"".equals(userreq)) {
                req = userreq + " && ( " + ds + " )";
            } else if ("".equals(ds) && !"".equals(userreq)) {
                req = userreq;
            } else if (!"".equals(ds) && "".equals(userreq)) {
                req = "(other.GlueCEStateStatus==\"Production\") && ( " + ds + " )";
            } else {
                req = "other.GlueCEStateStatus==\"Production\"";
            }
            //sysLog(OutputDir, "disablehost:" + DisabledHost.getI().getAll());
            sysLog(OutputDir, "requirements:" + req);
            try {jobad.setAttributeExpr("requirements", req);
            } catch (Exception e) {sysLog(OutputDir, "requirements: " + e.toString());errorLog(OutputDir, "Error in JDL attribute requirements: \""+req+"\" ", e);return null;}

            try{jobad.setAttributeExpr("Rank","-other.GlueCEStateEstimatedResponseTime");
            }catch (Exception e){sysLog(OutputDir, "Rank: "+e.toString());}
            if (!jobad.hasAttribute("ShallowRetryCount")) {
                jobad.addAttribute("ShallowRetryCount", 0);
            }
            if (!jobad.hasAttribute("RetryCount")) {
                jobad.addAttribute("RetryCount", 0);
            }

            jobad.addAttribute("SubmitTo", pJob.getConfiguredResource().getResource() + "/" + pJob.getConfiguredResource().getJobmanager());
            //   sysLog(jobad.toSubmissionString());
            sysLog(OutputDir, jobad.toLines());
            try{
                FileWriter tmp = new FileWriter(path+"outputs/job.jdl", false);
                BufferedWriter   out = new BufferedWriter(tmp);
                if (envVars != null){
                    String originaljdl = jobad.toLines();
                    String njdl = originaljdl.substring(0, originaljdl.lastIndexOf("]"));

                    //System.out.println("n1:"+njdl);
                    njdl+=";\nEnvironment = {\""+envVars.replace("\"", "\\\"")+"\"};\n]";
                    sysLog(OutputDir,"new jdl:"+njdl);
                    out.write(njdl);
                }else{
                    out.write(jobad.toLines());
                }
                out.flush();
                out.close();
            }catch (Exception e){sysLog(OutputDir, e.toString());}
        }catch (Exception e){e.printStackTrace();return null;}
        return jobad;
    }



    /** Converts status string to ActivityStateEnumeration status code
     *  @return status code
     */
    private ActivityStateEnumeration toActivityStatusCode(String st){
        if (st.equals("Submitted")) return ActivityStateEnumeration.PENDING;//2;
        else if (st.equals("Waiting")) return ActivityStateEnumeration.PENDING;//3;
        else if (st.equals("Ready")) return ActivityStateEnumeration.PENDING;//10;
        else if (st.equals("Scheduled")) return ActivityStateEnumeration.PENDING;//4;
        else if (st.equals("Running")) return ActivityStateEnumeration.RUNNING;//5;
        else if (st.equals("Cancelled")) return ActivityStateEnumeration.FAILED;//7;
        else if (st.equals("Aborted")) return ActivityStateEnumeration.FAILED;//7;
        else if (st.equals("Done")) return ActivityStateEnumeration.FINISHED;//6;
        else if (st.equals("Done error")) return ActivityStateEnumeration.PENDING;//9;
        else if (st.equals("Cleared")) return ActivityStateEnumeration.FAILED;//7;
        else if (st.equals("getOutput")) return ActivityStateEnumeration.RUNNING;//5;//disable getstatus
        else if (st.equals("submitting")) return ActivityStateEnumeration.PENDING;//3;//disable getstatus
        else if (st.equals("Done (Failed)")) return ActivityStateEnumeration.FAILED;//7;
        return ActivityStateEnumeration.PENDING;//20;
    }

    /**
     * Get job status from grid.
     * Resubmit/ get output if necessary.
     * @param pJob
     */
    private void gstatus(Job pJob) {
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
        String stat="";
        BufferedReader sin = null;
        BufferedReader sinerr = null;
        try{
            String cmd ="glite-wms-job-status "+pJob.getMiddlewareId();

            Process p;
            p = Runtime.getRuntime().exec(cmd,null,new File(Base.getI().getJobDirectory(pJob.getId())));//localDir
            sin = new BufferedReader(new InputStreamReader(p.getInputStream()));
            sinerr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv =p.waitFor();
            String sor;
            String msg="";
            //boolean resubmit=false;
            sysLog(OutputDir, cmd);
            if (exitv == 0) {
                while ((sor = sin.readLine()) != null) {
                    if (sor.contains("Current Status:")) {
                        stat = sor.substring(16, sor.length()).trim();
                    } else if (sor.contains("Destination:")) {
                        String resource = sor.substring(13, sor.length()).trim();
                        pJob.setResource(resource);
                    }
//                    else if (sor.contains("hit job shallow retry count") || sor.contains("failed (LB query failed)")) {//Status Reason:
//                        sysLog(OutputDir, sor);
//                        resubmit = true;
//                    }
                    msg += sor + "\n";
                //  sysLog(sor);
                }
                sin.close();

               // if (pJob.getMiddlewareStatus() != toStatusCode(stat)) {//statuszvaltozas figyeles
/////******                if (!pJob.getStatus().equals(toActivityStatusCode(stat))) {//statuszvaltozas
                    sysLog(OutputDir, "gstat.exit:" + exitv + " Current Status:" + stat + " Destination:" + pJob.getResource());
//                    statuschanged = System.currentTimeMillis();
                    if (stat.equals("Done (Success)")) {
                        if (getOutputFilesAndPurge(pJob)){//;//getOutput();
                            pJob.setStatus(ActivityStateEnumeration.FINISHED);
                        }else{
                            pJob.setStatus(ActivityStateEnumeration.FAILED);
                        }
                        //sysLog(OutputDir, "end sleep 5000");
                        //Thread.sleep(5000);
                    } else if (stat.equals("Done (Exit Code !=0)") || stat.equals("Done (Failed)")) {
                        sysLog(OutputDir, msg);
                        getOutputFilesAndPurge(pJob);//getOutput();
                        errorLog(OutputDir, msg);
                        //sysLog(OutputDir, "end sleep 5000");
                        //Thread.sleep(5000);
                        pJob.setStatus(ActivityStateEnumeration.FAILED);//status = 7;// "Aborted";
                    } else if (!stat.equals("")) {
                        //status = toStatusCode(stat);
                        pJob.setStatus(toActivityStatusCode(stat));
                        //pJob.setMiddlewareStatus(toStatusCode(stat));
                    }
                    if (stat.equals("Aborted") || stat.equals("Cancelled")) {
                        errorLog(OutputDir, msg);
                        sysLog(OutputDir, msg);
                        pJob.setStatus(ActivityStateEnumeration.FAILED);
                    }



            }else{//hiba a status lekerdezes soran
                sysLog(OutputDir, "gstat.exit:" + exitv + " Current Status:" + stat + " Destination:" + pJob.getResource());
                while ((sor = sinerr.readLine()) != null) {
                    msg += sor + "\n";
                }
                sinerr.close();
                if (msg.contains("PROXY_EXPIRED")) {//renew voms
                    sysLog(OutputDir, "PROXY_EXPIRED -> renew voms");

                    boolean success=false;

                    String path = Base.getI().getJobDirectory(pJob.getId());
                    JobDefinitionType jsdl = pJob.getJSDL();
                    POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);
                    String userid = BinaryHandler.getUserName(pType);
                    success = getProxy(path, pJob, userid, true);
                    
                    if (success){
                        return;
                    }
                } else if (msg.contains("server closed the connection, probably due to overload") ||
                        msg.contains("Connection timed out")) {
                    synchronized (GLiteConfig.getI().getLock()) {
                        try {sysLog(OutputDir, msg+" SLEEP 60 sec");Thread.sleep(60000);}catch(Exception err){err.printStackTrace();}
                    }
                    //return status;
                    return;
                }
                errorLog(OutputDir, "It was not possible to query the status. \n "+msg);
                sysLog(OutputDir, msg);
                synchronized (GLiteConfig.getI().getLock()) {
                    try {sysLog(OutputDir, "SLEEP 60 sec");Thread.sleep(60000);}catch(Exception err){err.printStackTrace();}
                }
                pJob.setStatus(ActivityStateEnumeration.FAILED);//status = 7;// "Aborted";

            }
            //return status;
        }catch (Exception ex) {
            sysLog(OutputDir, "ERROR! gstatus"+ex.getMessage() );
            //ex.printStackTrace();
            pJob.setStatus(ActivityStateEnumeration.FAILED);//status = 7;// "Aborted";
        } finally {
            try {
                sin.close();
            } catch (Exception e) {
            }
            try {
                sinerr.close();
            } catch (Exception e) {
            }
        }
    }







    /**
     * Get jobs local outputs.
     * @param pJob
     * @return
     */
    private boolean getOutputFilesAndPurge(Job pJob) {
        String grid = pJob.getConfiguredResource().getVo();
        String OutputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";
        String proxy = Base.getI().getJobDirectory(pJob.getId()) + "x509up";

        StringAndLongList result = null;
        StringAndLongType[] list = null;
        int size = 0;
        boolean succes = true;

        try {
            Vector outputf = getOutputSandboxFileNames(pJob);
            size = outputf.size();
            sysLog(OutputDir, "+--------------------------getoutput:" + size + "-------------------------------------------+");
            if (size > 0) {                                
                int ntry = 0;
                boolean btry = true;
                while (btry) {//error handling, retry if service call fails
                    try {
                        sysLog(OutputDir, "getOutputFileList ...try:" + ntry);
                        synchronized (GLiteConfig.getI().getLock()) {
                            client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);
                            result = client.getOutputFileList(pJob.getMiddlewareId(), "gsiftp");
                            btry = false;
                        }
                    } catch (ServerOverloadedFaultException e3) {
                        retryOrThrowException(OutputDir, ntry++, 6, "Server is Overloaded: " + e3.getMessage());
                    } catch (ServiceException e4) {
                        retryOrThrowException(OutputDir, ntry++, 2, "WMS Service Error: " + e4.getMessage());
                    }
                }
                
                if (result != null) {

                    // list of files+their size
                    list = (StringAndLongType[]) result.getFile();
                    // size = list.length;

                    if (list != null) {
                        if (list.length != size) {
                            sysLog(OutputDir, "Some file(s) listed in the output sandbox were not available..");
                            errorLog(OutputDir, "Some file(s) listed in the output sandbox were not available..");
                        }
                        sysLog(OutputDir, "Downloading output files ...");
                        GlobusCredential gcred = new GlobusCredential(proxy);
                        GSSCredential gssproxy = new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);
                        for (int i = 0; i < size; i++) {
                            try {
                                // Creation of the "fromURL" link from where download the file(s).
                                int pos = (list[i].getName()).indexOf("2811");
                                int length = (list[i].getName()).length();
                                String front = (list[i].getName()).substring(0, pos);
                                String rear = (list[i].getName()).substring(pos + 4, length);
                                String fromURL = front + "2811/" + rear;

                                // Creation of the "toURL" link from where download the file(s).
                                String toURL = "file:///" + OutputDir + rear.substring(rear.lastIndexOf("/"));
                                sysLog(OutputDir, i + " get fromURL:" + fromURL + " toURL:" + toURL);
                                GlobusURL from = new GlobusURL(fromURL);
                                GlobusURL to = new GlobusURL(toURL);
                                UrlCopy uCopy = new UrlCopy();
                                uCopy.setCredentials(gssproxy);
                                uCopy.setDestinationUrl(to);
                                uCopy.setSourceUrl(from);
                                uCopy.setUseThirdPartyCopy(true);
                                uCopy.copy();
                            } catch (ArrayIndexOutOfBoundsException ae) {
                                succes = false;
                                sysLog(OutputDir, i + "Can not copy the Output file:" + outputf.get(i) + " - the file does not exist." + ae.getMessage());
                                //ae.printStackTrace();
                                errorLog(OutputDir, "Can not copy the Output file:" + outputf.get(i) + " - the file does not exist.");
                            } catch (Exception e) {
                                succes = false;
                                sysLog(OutputDir, i + " Can not copy the Output file:" + outputf.get(i) + " - " + e);
                                //e.printStackTrace();
                                errorLog(OutputDir, "Can not copy the Output file:" + outputf.get(i) + " - " + e.getMessage());
                            }
                        }
                    } else {
                        sysLog(OutputDir, "No output files for this job!");
                    }
                } else {
                    sysLog(OutputDir, "An empty list has been received");
                }
            }
            sysLog(OutputDir, "+--------------------------getoutput-Success---------------------------------------------+");
        } catch (Exception exc) {
            errorLog(OutputDir, exc.getMessage());
            sysLog(OutputDir, exc.toString());
            sysLog(OutputDir, "+--------------------------getoutput-FAILED-----------------------------------------------+");
            //exc.printStackTrace();
            succes = false;
        } finally {
            try {
                sysLog(OutputDir, "jobPurge ...");
                synchronized (GLiteConfig.getI().getLock()) {
                    client = new WMProxyAPI(GLiteConfig.getI().getWMProxyUrl(grid), proxy);                    
                    client.jobPurge(pJob.getMiddlewareId());
                }
            } catch (Exception exc) {
                sysLog(OutputDir, "jobPurge ERROR:" + exc.toString());
            //exc.printStackTrace();
            }
            client = null;
        }

        return succes;
    }

    private Vector<String> getOutputSandboxFileNames(Job pJob) {
        Vector files = new Vector();

        //add local outputs
        List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        for (DataStagingType outp : outputs) {
                if (!("gridnfo.log".equals(outp.getFileName()) ||
                        "stdout.log".equals(outp.getFileName()) ||
                        "stderr.log".equals(outp.getFileName()) ||
                        "guse.jsdl".equals(outp.getFileName()) ||
                        "guse.logg".equals(outp.getFileName()))) {
                files.add(outp.getFileName());
            }
        }
        return files;
    }

    @Override
    public void run() {
        Base.writeLogg(THIS_MIDDLEWARE, new LB("starting thread - EDGI"));
        Job tmp=null;
        while(true){
            try{
                tmp=jobs.take();
                switch(tmp.getFlag()){
                    case SUBMIT:
                        Base.initLogg(tmp.getId(), "logg.job.submit");
                        submit(tmp);
                        Base.endJobLogg(tmp, LB.INFO,"");
                        tmp.setFlag(GETSTATUS);
                        tmp.setTimestamp(System.currentTimeMillis());
                        tmp.setPubStatus(ActivityStateEnumeration.RUNNING);
                        break;
                    case GETSTATUS:
                        if((System.currentTimeMillis()-tmp.getTimestamp())<LAST_ACTIVATE_TIMESTAMP){
                            try{sleep(1000);/*System.out.println(getName()+" sleep 1000");*/}
                            catch(InterruptedException ei){Base.writeLogg(THIS_MIDDLEWARE, new LB(ei));}
                        } else {
                            Base.initLogg(tmp.getId(), "logg.job.getstatus");
                            getStatus(tmp);
                            Base.endJobLogg(tmp, LB.INFO, "");
                            tmp.setTimestamp(System.currentTimeMillis());
                        }
                        break;
                    case ABORT:
                        tmp.setStatus(ActivityStateEnumeration.CANCELLED);
                        Base.initLogg(tmp.getId(), "logg.job.abort");
                        abort(tmp);
                        Base.endJobLogg(tmp, LB.INFO,"");
                        break;
                }
                if(isEndStatus(tmp)){
                    Base.initLogg(tmp.getId(), "logg.job.getoutput");
                    getOutputs(tmp);
                    Base.endJobLogg(tmp, LB.INFO,"");
                  Base.getI().finishJob(tmp);
                }
                else if(isAbortStatus(tmp))
                    Base.getI().finishJob(tmp);
                else addJob(tmp);

            }
            catch(Exception e){
                if(tmp!=null) Base.writeJobLogg(tmp, e, "error.job."+tmp.getFlag());
            }
        }
    }



    /** stderr.log -ba logol
     */
    private void errorLog(String OutputDir, String txt){
        try{
            FileWriter tmp = new FileWriter(OutputDir+"/stderr.log",true);
            BufferedWriter   out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        }catch (Exception e){sysLog(OutputDir, e.toString());}
    }

    /** stderr.log -ba logol
     */
    private void errorLog(String OutputDir, String pMsg, Exception pEx) {
        try {
            File f = new File(OutputDir + "/stderr.log");
            f.createNewFile();
            FileWriter fw = new FileWriter(f, true);
            fw.write(pMsg + "\n");
            fw.write(pEx.getMessage() + "\n");
            if (pEx.getCause()!=null){
                fw.write(pEx.getCause().getMessage()+ "\n");
            }
            fw.write("\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sysLog(String logdir, String txt) {
        try {
            if (Conf.getP().getDebug() > 0) {
                System.out.println("-" + txt);
                FileWriter tmp = new FileWriter(logdir + "/plugin.log", true);
                BufferedWriter out = new BufferedWriter(tmp);
                out.newLine();
                out.write(txt);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            }
    }
}
