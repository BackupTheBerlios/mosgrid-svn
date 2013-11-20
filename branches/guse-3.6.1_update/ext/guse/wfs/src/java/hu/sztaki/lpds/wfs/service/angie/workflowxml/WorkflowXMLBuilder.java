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
package hu.sztaki.lpds.wfs.service.angie.workflowxml;

import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.wfs.com.JobPropertyBean;
import hu.sztaki.lpds.wfs.com.StorageWorkflowNamesBean;
import hu.sztaki.lpds.wfs.com.WorkflowConfigErrorBean;
import hu.sztaki.lpds.wfs.net.wsaxis13.WfsPortalServiceImpl;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Összeallit es visszaad egy adatbazisban letarolt workflow leiro xml-t.
 *
 * (es ha kell a beagyazott workflow-kat is osszegyujti)
 *
 * @author lpds
 */
public class WorkflowXMLBuilder {

    private WorkflowXMLService workflowXMLService;
    private WfsPortalServiceImpl wfsPortalService;
    private BuilderUtils builderUtils;
    // Az embed workflow felderites miatt kellenek
    private Hashtable grafList;
    private Hashtable abstList;
    private Hashtable realList;

    public WorkflowXMLBuilder() throws Exception {
        this.workflowXMLService = new WorkflowXMLServiceImpl();
        this.wfsPortalService = new WfsPortalServiceImpl();
        this.builderUtils = new BuilderUtils(workflowXMLService, wfsPortalService);
        // Az embed workflow felderites miatt kellenek
        grafList = new Hashtable();
        abstList = new Hashtable();
        realList = new Hashtable();
    }

    /**
     * A parameterek-ben (bean) megadott azonositok
     * alapjan összeallitja a workflow xml-t.
     *
     * @param StorageWorkflowNamesBean bean
     *         - portalid portal azonosito
     *         - userid felhasznalo azonosito
     *         - workflowid main workflow neve
     *         - downloadType letoltes tipusa
     *         - instanceType instance tipusa
     *         - exportType letoltes tipusa
     * @return String workflow xml
     */
    public String buildXMLStr(StorageWorkflowNamesBean bean) throws Exception {
        try {
            String retStr = realBuildXMLStr(bean);
            // mindig meg kell hivni...
            workflowXMLService.closeConnection();
            return retStr;
        } catch (Exception e) {
            // mindig meg kell hivni...
            workflowXMLService.closeConnection();
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * A parameterek-ben (bean) megadott azonositok
     * alapjan összeallitja a workflow xml-t.
     *
     * @param StorageWorkflowNamesBean bean
     *         - portalid portal azonosito
     *         - userid felhasznalo azonosito
     *         - workflowid main workflow neve
     *         - downloadType letoltes tipusa
     *         - instanceType instance tipusa
     *         - exportType letoltes tipusa
     * @return String workflow xml
     */
    private String realBuildXMLStr(StorageWorkflowNamesBean bean) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        Document doc = builder.newDocument();
        Element eworkflowlist = doc.createElement("workflow");
        eworkflowlist.setAttribute("name", bean.getWorkflowID());
        eworkflowlist.setAttribute("download", bean.getDownloadType());
        eworkflowlist.setAttribute("export", bean.getExportType());
        //
        String grafName = new String("");
        String abstName = new String("");
        String realName = new String("");
        String mainGrafName = new String("");
        String mainAbstName = new String("");
        String mainRealName = new String("");
        //
        if (bean.isWork()) {
            if (bean.isGraf()) {
                grafName = bean.getWorkflowID();
                addGraf(grafName);
                builderUtils.buildGraf(eworkflowlist, doc, bean, grafName);
                mainGrafName = grafName;
            }
            if (bean.isAbst()) {
                abstName = bean.getWorkflowID();
                addAbst(abstName);
                // get aworkflow name (grafName) from abst workflow name
                grafName = workflowXMLService.getAWorkflowName(bean.getPortalID(), bean.getUserID(), abstName);
                addGraf(grafName);
                builderUtils.buildGraf(eworkflowlist, doc, bean, grafName);
                builderUtils.buildAbst(eworkflowlist, doc, bean, abstName, grafName);
                mainGrafName = grafName;
                mainAbstName = abstName;
            }
            if ((bean.isReal()) || (bean.isAll()) || (bean.getDownloadType().startsWith(bean.downloadType_inputs_rtID))) {
                realName = bean.getWorkflowID();
                addReal(realName);
                // get aworkflow name (grafName) from real workflow name
                grafName = workflowXMLService.getAWorkflowName(bean.getPortalID(), bean.getUserID(), realName);
                addGraf(grafName);
                // get abstract workflow name from realName
                abstName = workflowXMLService.getAbstractWorkflowName(bean.getPortalID(), bean.getUserID(), realName);
                if (!"".equals(abstName)) {
                    addAbst(abstName);
                }
                builderUtils.buildGraf(eworkflowlist, doc, bean, grafName);
                if (!"".equals(abstName)) {
                    builderUtils.buildAbst(eworkflowlist, doc, bean, abstName, grafName);
                    mainAbstName = abstName;
                }
                builderUtils.buildReal(eworkflowlist, doc, bean, realName, grafName, abstName);
                mainGrafName = grafName;
                mainRealName = realName;
            }
        }
        if ((bean.isAppl()) || (bean.isProj())) {
            addReal(bean.getWorkflowID()); // add main workflow
            mainRealName = bean.getWorkflowID();
            // parse embed workflows begin
            //
            // create com data bean
            ComDataBean comDataBean = new ComDataBean();
            comDataBean.setPortalID(bean.getPortalID());
            comDataBean.setUserID(bean.getUserID());
            //
            Hashtable tempList = new Hashtable();
            boolean working = true;
            // main ciklus start
            while (working) {
                // newWf true ha van meg parsolni valo workflow
                boolean newWf = false;
                // refresh lists
                tempList.clear();
                tempList.putAll(abstList);
                tempList.putAll(realList);
                // System.out.println("tempList : " + tempList);
                // parse tempList
                Enumeration tempenum = tempList.keys();
                while (tempenum.hasMoreElements()) {
                    String wfname = (String) tempenum.nextElement(); // real or abst workflow name
                    String wfvalue = (String) tempList.get(wfname); // "R", "T", "R_parsed", "T_parsed"
                    // System.out.println("wfname  : " + wfname);
                    // System.out.println("wfvalue : " + wfvalue);
                    if (wfvalue.length() <= 1) {
                        // first parsing begin
                        if (("R".equals(wfvalue)) || ("T".equals(wfvalue))) {
                            // get aworkflow name (grafName) from wfname
                            grafName = workflowXMLService.getAWorkflowName(bean.getPortalID(), bean.getUserID(), wfname);
                            if (!isOldGraf(grafName)) {
                                newWf = true;// nem feltetlen kell
                                addGraf(grafName);
                                // System.out.println("new graf : " + grafName);
                                builderUtils.buildGraf(eworkflowlist, doc, bean, grafName);
                                if ("".equals(mainGrafName)) {
                                    if (wfname.equals(mainRealName)) {
                                        mainGrafName = grafName;
                                    }
                                }
                            }
                            // parsing embeds
                            comDataBean.setWorkflowID(wfname);
                            Vector jobList = wfsPortalService.getEmbedWorkflowConfigDataReal(comDataBean);
                            // System.out.println("for start, size : " + jobList.size());
                            // parse jobList
                            for (int jobPos = 0; jobPos < jobList.size(); jobPos++) {
                                JobPropertyBean tmpjob = (JobPropertyBean) jobList.get(jobPos);
                                // System.out.println("tmpjob.getName() : " + tmpjob.getName() + ", tmpjob.getExe() : " + tmpjob.getExe());
                                if (tmpjob.getExe().containsKey("iworkflow")) {
                                    String newwfname = (String) tmpjob.getExe().get("iworkflow");
                                    if (!isOldReal(newwfname)) {
                                        newWf = true;
                                        addReal(newwfname);
                                        // System.out.println("new real : " + newwfname);
                                    }
                                }
                            }
                            // System.out.println("for end...");
                            if ("R".equals(wfvalue)) {
                                // get abstract workflow name from real wfname
                                abstName = workflowXMLService.getAbstractWorkflowName(bean.getPortalID(), bean.getUserID(), wfname);
                                if (!"".equals(abstName)) {
                                    if (!isOldAbst(abstName)) {
                                        newWf = true;
                                        addAbst(abstName);
                                        // System.out.println("new abst : " + abstName);
                                        if ("".equals(mainAbstName)) {
                                            if (wfname.equals(mainRealName)) {
                                                mainAbstName = abstName;
                                            }
                                        }
                                    }
                                }
                                if (!isParsedReal(wfname)) {
                                    builderUtils.buildReal(eworkflowlist, doc, bean, wfname, grafName, abstName);
                                    // set parsed real
                                    addReal(wfname);
                                }
                            }
                            if ("T".equals(wfvalue)) {
                                if (!isParsedAbst(wfname)) {
                                    builderUtils.buildAbst(eworkflowlist, doc, bean, wfname, grafName);
                                    // set parsed abst
                                    addAbst(wfname);
                                }
                            }
                        }
                        // first parsing end
                    }
                }
                if (!newWf) {
                    // nincs tobb parsolni valo
                    // main ciklus vege
                    working = false;
                }
            }
            // System.out.println("working end");
            // System.out.println("grafList : " + grafList);
            // System.out.println("abstList : " + abstList);
            // System.out.println("realList : " + realList);
            //
            // Modulsag leellenorzese
            if (bean.isAppl()) {
                isModuleWorkflowList(bean, realList);
            }
        }
        // beallitja az xml ben a main tag-ben a main workflow tulajdonsagokat
        eworkflowlist.setAttribute("maingraf", mainGrafName);
        eworkflowlist.setAttribute("mainabst", mainAbstName);
        eworkflowlist.setAttribute("mainreal", mainRealName);
        // System.out.println("mainGrafName : " + mainGrafName);
        // System.out.println("mainAbstName : " + mainAbstName);
        // System.out.println("mainRealName : " + mainRealName);
        // parse embed workflows end
        return builderUtils.transformWorkflowListToString(doc, eworkflowlist);
    }

    private void addGraf(String name) throws Exception {
        addList(grafList, name, "G");
        // 2x hogy "G_parsed" legyen,
        // bar nem funkcionalitasa van.
        addList(grafList, name, "G");
    }

    private void addAbst(String name) throws Exception {
        addList(abstList, name, "T");
    }

    private void addReal(String name) throws Exception {
        addList(realList, name, "R");
    }

    private void addList(Hashtable hash, String name, String value) throws Exception {
        if (!"".equals(name)) {
            if (!hash.containsKey(name)) {
                hash.put(name, value);
            } else {
                hash.put(name, value + "_parsed");
            }
        }
    }

    private boolean isOldGraf(String name) throws Exception {
        return isOld(grafList, name);
    }

    private boolean isOldReal(String name) throws Exception {
        return isOld(realList, name);
    }

    private boolean isOldAbst(String name) throws Exception {
        return isOld(abstList, name);
    }

    private boolean isOld(Hashtable hash, String name) throws Exception {
        if (!"".equals(name)) {
            if (hash.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean isParsedGraf(String name) throws Exception {
        return isParsed(grafList, name);
    }

    private boolean isParsedReal(String name) throws Exception {
        return isParsed(realList, name);
    }

    private boolean isParsedAbst(String name) throws Exception {
        return isParsed(abstList, name);
    }

    private boolean isParsed(Hashtable hash, String name) throws Exception {
        if (!"".equals(name)) {
            if (hash.containsKey(name)) {
                if ("parsed".equals((String) hash.get(name))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void isModuleWorkflowList(StorageWorkflowNamesBean bean, Hashtable tempList) throws Exception {
        // Modulsag leellenorzese
        // csak application tipusnal kell ellenorizni a modulsagot...
        if (bean.isAppl()) {
            // System.out.println("check workflow list is module...");
            // System.out.println("tempList : " + tempList);
            //
            ComDataBean comBean = new ComDataBean();
            comBean.setPortalID(bean.getPortalID());
            comBean.setUserID(bean.getUserID());
            // parse tempList
            Enumeration tempenum = tempList.keys();
            while (tempenum.hasMoreElements()) {
                String wfname = (String) tempenum.nextElement(); // real or abst workflow name
                // String wfvalue = (String) tempList.get(wfname); // "R", "T", "R_parsed", "T_parsed"
                comBean.setWorkflowID(wfname);
                Vector errorVector = wfsPortalService.getWorkflowConfigDataErrorReal(comBean);
                // parse error vector
                for (int vPos = 0; vPos < errorVector.size(); vPos++) {
                    WorkflowConfigErrorBean eBean = (WorkflowConfigErrorBean) errorVector.get(vPos);
                    if (!"certtype".equals(eBean.getErrorID())) {
                        // simply error mess
                        // ha van error uzenet akkor nem modul az aktualis workflow
                        throw new Exception(bean.getWorkflowID() + " workflow is not module !");
                    }
                }
            }
        }
    }
}
