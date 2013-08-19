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
 * JobConfig.java
 * Job konfiguracio leito
 */
package hu.sztaki.lpds.wfi.util;
//import dci.extension.Extension;
import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.com.IfBean;
import hu.sztaki.lpds.storage.inf.WFIStorageClient;
import hu.sztaki.lpds.submitter.com.JobIOBean;
import hu.sztaki.lpds.submitter.com.JobRuntime;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfi.service.zen.Runner;
import hu.sztaki.lpds.wfi.service.zen.data.IOInherited;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;
import java.io.File;
import java.io.FileWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.ggf.schemas.jsdl._2005._11.jsdl.ApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.BoundaryType;
import org.ggf.schemas.jsdl._2005._11.jsdl.CandidateHostsType;
import org.ggf.schemas.jsdl._2005._11.jsdl.CreationFlagEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDescriptionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobIdentificationType;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemType;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.OperatingSystemTypeType;
import org.ggf.schemas.jsdl._2005._11.jsdl.RangeValueType;
import org.ggf.schemas.jsdl._2005._11.jsdl.ResourcesType;
import org.ggf.schemas.jsdl._2005._11.jsdl.SourceTargetType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.ArgumentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.EnvironmentType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.FileNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.GroupNameType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.LimitsType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.UserNameType;
import uri.mbschedulingdescriptionlanguage.ConstraintsType;
import uri.mbschedulingdescriptionlanguage.DCINameEnumeration;
import uri.mbschedulingdescriptionlanguage.JobTypeEnumeration;
import uri.mbschedulingdescriptionlanguage.MiddlewareType;
import uri.mbschedulingdescriptionlanguage.MyProxyType;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.RemoteFileAccessEnumeration;
import uri.mbschedulingdescriptionlanguage.SDLType;

public class JobConfig {

    private Hashtable<String, String> desc = new Hashtable<String, String>();
    private Hashtable<String, String> prop = new Hashtable<String, String>();
    private Hashtable<String, Hashtable<String, String>> inputs = new Hashtable<String, Hashtable<String, String>>();
    private Hashtable<String, Hashtable<String, String>> outputs = new Hashtable<String, Hashtable<String, String>>();
    private String id = "";
    private JobRuntime jobData;
    private Hashtable<String, String> proxys = new Hashtable<String, String>();
    private String WFIURL,  StorageURL,  SecureStorageURL;

    /**
     * Class constructor
     */
    public JobConfig() {
        try {
            ServiceType wfi = InformationBase.getI().getService("wfi", "wfi", new Hashtable(), new Vector());
            WFIURL = wfi.getServiceUrl();
            ServiceType storage = InformationBase.getI().getService("storage", "wfi", new Hashtable(), new Vector());
            StorageURL = storage.getServiceUrl();
            try {
                SecureStorageURL = storage.getSecureServiceUrl();
                //System.out.println("ssURL, SecureStorageURL:"+SecureStorageURL);
                if ("".equals(SecureStorageURL.trim())) {
                    SecureStorageURL = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println("STORAGEURL("+storage.getServiceUrl()+"):"+StorageURL);
//            System.out.println("WFIURL("+wfi.getServiceUrl()+"):"+WFIURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Hashtable<String, String> getProxys() {
        return proxys;
    }

    public void setProxys(Hashtable<String, String> proxys) {
        this.proxys = proxys;
    }

    public JobRuntime getJobData() {
        return jobData;
    }

    public void setJobData(JobRuntime jobData) {
        this.jobData = jobData;
        if (this.jobData.getStorageID() == null || "".equals(this.jobData.getStorageID())) {
            this.jobData.setStorageID(StorageURL);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Job Leiro hozzaadasa
     * @param pKey kulcs
     * @param pvalue ertek
     */
    public void addDesc(String pKey, String pvalue) {
        desc.put(pKey, pvalue);
    }

    /**
     * Job proprerty hozzaadasa
     * @param pKey kulcs
     * @param pvalue ertek
     */
    public void addProp(String pKey, String pvalue) {
        prop.put(pKey, pvalue);
    }

    /**
     * Input tulajdonsag hozzaadasa
     * @param pName input neve
     * @param pKey kulcs
     * @param pvalue ertek
     */
    public void addInput(String pName, String pKey, String pvalue) {
        if (inputs.get(pName) == null) {
            inputs.put(pName, new Hashtable());
        }
        ((Hashtable) inputs.get(pName)).put(pKey, pvalue);
    }

    /**
     * Output tulajdonsag hozzaadasa
     * @param pName ouput neve
     * @param pKey kulcs
     * @param pvalue ertek
     */
    public void addOutput(String pName, String pKey, String pvalue) {
        if (outputs.get(pName) == null) {
            outputs.put(pName, new Hashtable());
        }
        ((Hashtable) outputs.get(pName)).put(pKey, pvalue);
    }

    /**
     * Job tulajdonsag lekerdezese
     * @param pValue tulajdonsag kulcs
     * @return ertek
     */
    public String getJobPropertyValue(String pValue) {
        try {
            return (String) prop.get(pValue);
        } catch (NullPointerException e) {
            System.out.println("--JOB propery error:" + pValue);
            return "";
        }
    }

    /**
     * Job leiro lekerdezese
     * @param pValue leiro kulcs
     * @return ertek
     */
    public String getJobDescriptionValue(String pValue) {
        try {
            return (String) desc.get(pValue);
        } catch (NullPointerException e) {
            System.out.println("--JOB description error:" + pValue);
            return "";
        }
    }

    /**
     * Input tulajdonsag lekerdezese
     * @param pInput Input neve
     * @param pValue tulajdonsag kulcs
     * @return ertek
     */
    public String getInputPropertyValue(String pInput, String pValue) {
        try {
            return (String) ((Hashtable) inputs.get(pInput)).get(pValue);
        } catch (NullPointerException e) {
            System.out.println("--Input propery error:" + pInput + "/" + pValue);
            return "";
        }

    }

    /**
     * Output tulajdonsag lekerdezese
     * @param pInput pOutput neve
     * @param pValue tulajdonsag kulcs
     * @return ertek
     */
    public String getOutputPropertyValue(String pInput, String pValue) {
        try {
            return (String) ((Hashtable) outputs.get(pInput)).get(pValue);
        } catch (NullPointerException e) {
            if ("intname".equals(pValue)) {
                return pInput;
            } else if ("maincount".equals(pValue)) {
                return "1";
            } else {
                System.out.println("--Output propery error:" + pInput + "/" + pValue);
                e.printStackTrace();
                return "";
            }
        }
    }

    /**
     * Job tulajdonsag lista lekerdezese
     * @return ertek
     */
    public Enumeration getJobPropertys() {
        return prop.keys();
    }

    /**
     * Job leiro lista lekerdezese
     * @return ertek
     */
    public Enumeration getJobDescriptions() {
        return desc.keys();
    }

    /**
     * Input property lista lekerdezese
     * @param pInput input neve
     * @return ertek
     */
    public Enumeration getInputPropertys(String pInput) {
        return ((Hashtable) inputs.get(pInput)).keys();
    }

    /**
     * Output property lista lekerdezese
     * @param pInput output neve
     * @return ertek
     */
    public Enumeration getOutputPropertys(String pInput) {
        return ((Hashtable) outputs.get(pInput)).keys();
    }

    /**
     * Job property hash lekerdezese
     * @return tualjdonsag hash
     */
    public Hashtable getJobPropertyHash() {
        return prop;
    }

    /**
     * Job leiro hash lekerdezese
     * @return leiro hash
     */
    public Hashtable getJobDescriptionHash() {
        return desc;
    }

    /**
     * Input property hash lekerdezese
     * @param pInput input neve
     * @return tualjdonsag hash
     */
    public Hashtable getInputPropertyHash(String pInput) {
        return (Hashtable) inputs.get(pInput);
    }

    /**
     * Output property hash lekerdezese
     * @param pInput output neve
     * @return tualjdonsag hash
     */
    public Hashtable getOutputPropertyHash(String pInput) {
        return (Hashtable) outputs.get(pInput);
    }

    /**
     * Input lista lekerdezese
     * @return input nev lista
     */
    public Enumeration getInputs() {
        return inputs.keys();
    }

    /**
     * Output lista lekerdezese
     * @return output nev lista
     */
    public Enumeration getOutputs() {
        return outputs.keys();
    }

    /**
     * Lokalis inputok listajanak lekerdezese
     * @return input nev lista
     */
    public Vector getLocalInputFiles() {
        Vector res = new Vector();
        Object key;
        Enumeration enm = getInputs();
        while (enm.hasMoreElements()) {
            key = enm.nextElement();
            if ((getInputPropertyValue((String) key, "file") != null) || ((getInputPropertyValue((String) key, "remote") == null) && (getInputPropertyValue((String) key, "sqlselect") == null))) {
                res.add(key);
            }
        }
        return res;
    }

    /**
     * SQL inputok listajanak lekerdezese
     * @return input nev lista
     */
    public Vector getSQLInputFiles() {
        Vector res = new Vector();
        Object key;
        Enumeration enm = getInputs();
        while (enm.hasMoreElements()) {
            key = enm.nextElement();
            if (getInputPropertyValue((String) key, "sqlselect") != null) {
                res.add(key);
            }
        }
        return res;
    }

    /**
     * Elelnorzes
     */
    public void init() {
        Enumeration enm = getInputs();
        Object key;
        while (enm.hasMoreElements()) {
            key = enm.nextElement();
            if (getInputPropertyValue((String) key, "intname") == null) {
                addInput((String) key, "intname", (String) key);
            }
        }

        enm = getOutputs();
        while (enm.hasMoreElements()) {
            key = enm.nextElement();
            if (getOutputPropertyValue((String) key, "intname") == null) {
                addOutput((String) key, "intname", (String) key);
            }
        }
        setId(UUID.randomUUID().toString());
    }

    public List<String> getIfInputs() {
        List<String> res = new ArrayList<String>();
        String key;
        Enumeration<String> enm = getInputs();
        while (enm.hasMoreElements()) {
            key = enm.nextElement();
            if (!"0".equals(getInputPropertyValue(key, "pequaltype"))) {
//                System.out.println(jobData.getJobID()+"."+key+"=>"+getInputPropertyValue(key, "pequaltype"));
                res.add(key);
            }
        }

        return res;
    }

    /**
     * Input file helye a storage-on
     * @param value Input port leiro a wfi tol
     * @param key0 port neve
     * @return path;
     */
    public String getLocalInputPath(String key0) {
        String path = "";
//        System.out.println(jobData.getJobID()+"."+jobData.getPID()+"/"+key0);
        JobIOBean value = (JobIOBean) jobData.getInputsCount().get(key0);


        if (value.getRuntimeID() == null) {//input
            path = jobData.getJobID() + "/inputs/" + inputs.get(key0).get("seq") + "/" + value.getPID();
        } else {//csatorna
            if ("-1".equals(value.getIndex())) {
                path = value.getJobID() + "/outputs/" + value.getRuntimeID() + "/" + value.getPID() + "/" + value.getName();
            } else {
                path = value.getJobID() + "/outputs/" + value.getRuntimeID() + "/" + value.getPID() + "/" + value.getName() + "_" + value.getIndex();
            }
        }
        return path;

    }

    public String getRemoteInputPath(String key0, String postfix) {

        String path = "";
        JobIOBean value;
        value = (JobIOBean) jobData.getInputsCount().get(key0 + postfix);


        if (value.getRuntimeID() == null) {//input
            if (getInputPropertyHash(key0).get("max") == null) {
                path = (String) getInputPropertyHash(key0).get("remote");
            } else {
                long max = Long.parseLong("" + getInputPropertyHash(key0).get("max"));
                if (max < 2) {
                    path = (String) getInputPropertyHash(key0).get("remote");//+"_"+value.getPID();
                } else {
                    path = (String) getInputPropertyHash(key0).get("remote") + "_" + value.getPID();
                }
            }
        } else {//csatorna
            if ("-1".equals(value.getIndex())) {
                path = getInputPropertyHash(key0).get("remote") + "_" + value.getPID();
            } else {
                path = getInputPropertyHash(key0).get("remote") + "_" + value.getPID() + "_" + value.getIndex();
            }
//                    System.out.println("remote input:"+key0+"="+path);
        }
        return path;

    }

    public String getEmbedInputPath(String portName) {
        String path = "";
        Runner parentWF = Base.getZenRunner(Base.getZenRunner(jobData.getWorkflowRuntimeID()).getParentZenID());

        JobIOBean value = (JobIOBean) jobData.getInputsCount().get(portName);
        Runner wf = Base.getZenRunner(jobData.getWorkflowRuntimeID());
        IOInherited internalPort = wf.getParentInputPort(jobData.getJobID(), portName);

        if (value.getRuntimeID() == null)//input
        {
            path = internalPort.getWorkflow() + "/" + internalPort.getJob() + "/inputs/" + internalPort.getSeq() + "/" + internalPort.getPID();
        } else {//csatorna
            Job job = Base.getZenRunner(internalPort.getWorkflowRID()).getJob(internalPort.getJob());

            if ("-1".equals(value.getIndex())) {
                path = internalPort.getWorkflow() + "/" + job.getInput(internalPort.getPort()).getPreJob() + "/outputs/" + internalPort.getWorkflowRID() + "/" + value.getPID() + "/" + job.getInput(internalPort.getPort()).getPreOutput();
            } else {
                path = internalPort.getWorkflow() + "/" + job.getInput(internalPort.getPort()).getPreJob() + "/outputs/" + value.getRuntimeID() + "/" + value.getPID() + "/" + value.getName() + "_" + value.getIndex();
            }
        }

        return path;
    }

    public boolean isEmbedInput(String portName) {
        boolean res = false;
        try {
            Runner wf = Base.getZenRunner(jobData.getWorkflowRuntimeID());
            wf.getParentInputPort(jobData.getJobID(), portName);
            res = true;
        } catch (Exception e) {/*e.printStackTrace();*/

        }
        return res;
    }

    public String getBasePath() {
        return jobData.getPortalID().replaceAll("/", "_") + "/" + jobData.getUserID() + "/";
    }

    public JobDefinitionType toXML() throws Exception {

        String submitterURL = WFIURL + "/getFile?path=" + jobData.getWorkflowRuntimeID() + "&job=" + jobData.getJobID() + "&file=";
        String baseURL = jobData.getStorageID() + "/getFile?path=" + getBasePath();
        String uplURL = jobData.getStorageID() + "/FileUploadServlet?path=" + getBasePath() + jobData.getWorkflowID() + "/" + jobData.getJobID() + "/";
        String path;

        String key0;
        Enumeration<String> enm;

        JobDefinitionType jsdl = new JobDefinitionType();

        if (("boinc".equals(prop.get("gridtype")) || "gbac".equals(prop.get("gridtype"))) && SecureStorageURL != null) {
            //System.out.println("boinc job, SecureStorageURL:"+SecureStorageURL);
            baseURL = SecureStorageURL + "/getFile?path=" + getBasePath();
            uplURL = SecureStorageURL + "/FileUploadServlet?path=" + getBasePath() + jobData.getWorkflowID() + "/" + jobData.getJobID() + "/";
        }

        String[] jobName = new String[]{""};
        if (prop.get("binary") != null && !prop.get("binary").trim().equals("")) {
            jobName = prop.get("binary").split("/");
        } else if (prop.get("gridtype") != null && "unicore".equals(prop.get("gridtype").trim().toLowerCase())
                && prop.get("jobmanager") != null && !prop.get("jobmanager").equals("")) {
            // for UNICORE the jobmanager represents the selected IDB-tool
            jobName = new String[]{prop.get("jobmanager")};
        } else if (prop.get("arapp") != null && "edgi".equals(prop.get("gridtype"))) {
            jobName = new String[]{prop.get("arapp")};
        } else if (prop.get("servicetype") != null && !prop.get("servicetype").equals("")) {
            jobName = new String[]{prop.get("servicemethod")};
        } else if (prop.get("resource") != null && !prop.get("resource").equals("")) {
            jobName = new String[]{prop.get("resource")};
        }

        jsdl.setJobDescription(new JobDescriptionType());
        jsdl.getJobDescription().setJobIdentification(new JobIdentificationType());

        jsdl.getJobDescription().getJobIdentification().setJobName(jobName[jobName.length - 1]);
        jsdl.getJobDescription().setApplication(new ApplicationType());
        jsdl.getJobDescription().getApplication().setApplicationName("guse://" + jobData.getWorkflowRuntimeID() + "/" + jobData.getWorkflowSubmitID() + "/" + jobData.getJobID() + "/" + jobData.getPID());

        POSIXApplicationType posixApplication = new POSIXApplicationType();

// PosixApplication/ProcessCountlimit
        if (desc.get("gt2.keyhostCount") != null) {
            try {
                LimitsType posix01 = new LimitsType();
                posix01.setValue(new BigInteger(desc.get("gt2.keyhostCount")));
                posixApplication.setProcessCountLimit(posix01);
            } catch (Exception e) {
            }
            desc.remove("gt2.keyhostCount");
        }
// PosixApplication/CPUTimeLimit
        if (desc.get("gt2.keymaxTime") != null) {
            try {
                LimitsType posix01 = new LimitsType();
                posix01.setValue(new BigInteger(desc.get("gt2.keymaxTime")));
                posixApplication.setCPUTimeLimit(posix01);
            } catch (Exception e) {
            }
            desc.remove("gt2.keymaxTime");
        }

//PosixApplication/user name
        UserNameType posixUserName = new UserNameType();
        posixUserName.setValue(jobData.getUserID());
        posixApplication.setUserName(posixUserName);
//PosixApplication/group name
        GroupNameType posixGroupName = new GroupNameType();
        posixGroupName.setValue(jobData.getPortalID());
        posixApplication.setGroupName(posixGroupName);

//PosixApplication/error
        FileNameType posixError = new FileNameType();
//        posixError.setFilesystemName("HOME");
        posixError.setValue("stderr.log");
        posixApplication.setError(posixError);

//PosixApplication/std output
        FileNameType posixOutput = new FileNameType();
//        posixOutput.setFilesystemName("HOME");
        posixOutput.setValue("stdout.log");
        posixApplication.setOutput(posixOutput);
//PosixApplication/executable
        if (prop.get("binary") != null) {
            FileNameType posixExecutable = new FileNameType();
//            posixExecutable.setFilesystemName("HOME");
            posixExecutable.setValue("execute.bin");
            posixApplication.setExecutable(posixExecutable);
        }
//PosixApplication/parameter
        if (prop.get("params") != null) {
            ArgumentType posixArgument;
            String[] args = prop.get("params").split(" ");
            for (String t : args) {
                posixArgument = new ArgumentType();
                posixArgument.setValue(t);
                posixApplication.getArgument().add(posixArgument);
            }
        }

//JSDL binary input
        if (prop.get("binary") != null) {
            DataStagingType binary = new DataStagingType();
            binary.setFileName("execute.bin");
            SourceTargetType binaryURL = new SourceTargetType();
            binaryURL.setURI(baseURL + jobData.getWorkflowID() + "/" + jobData.getJobID() + "/execute.bin");
            binary.setSource(binaryURL);
            binary.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
            binary.setDeleteOnTermination(new Boolean(true));
            jsdl.getJobDescription().getDataStaging().add(binary);
        }
//JSDL inputs

        HashMap<String, String> inputValues = new HashMap<String, String>();
        if (jobData.getInputsCount() != null && jobData.getInputsCount().size() > 0) {
            enm = jobData.getInputsCount().keys();
            String key00;
            String postfix = "";
            while (enm.hasMoreElements()) {
                key00 = enm.nextElement();
                if (inputs.get(key00) != null) {
                    key0 = key00;
                    postfix = "";
                } else {
                    String[] tmp = key00.split("_");
                    int ends = tmp[tmp.length - 1].length() + 1;
                    key0 = key00.substring(0, key00.length() - ends);
                    postfix = "_" + tmp[tmp.length - 1];
                }


                DataStagingType input = new DataStagingType();
                input.setFileName(inputs.get(key0).get("intname") + postfix);
                SourceTargetType inputURL = new SourceTargetType();
                input.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
                input.setDeleteOnTermination(new Boolean(true));

                path = getLocalInputPath(key00);
                if (isEmbedInput(key0)) {
                    inputURL.setURI(baseURL + getEmbedInputPath(key00));
                } else if (getInputPropertyHash(key0).get("value") != null) { //konstans
                    inputURL.setURI(submitterURL + key0);
                    String pathValue = PropertyLoader.getInstance().getProperty("prefix.dir") + jobData.getWorkflowRuntimeID() + "/" + jobData.getJobID();
                    File tmpValue = new File(pathValue);
                    tmpValue.mkdirs();
                    pathValue = pathValue + "/" + key0;
                    tmpValue = new File(pathValue);
                    try {
                        tmpValue.createNewFile();
                        FileWriter fw = new FileWriter(tmpValue);
                        String value = (String) getInputPropertyHash(key0).get("value");
                        if ("${user}".equals(value)) {
                            value = jobData.getUserID();
                        } else if ("${version}".equals(value)) {
                            value = "" + PropertyLoader.getInstance().getProperty("system.version");
                        } else if ("${workflow}".equals(value)) {
                            value = jobData.getWorkflowID() + " - " + Base.getZenRunner(jobData.getWorkflowRuntimeID()).getWorkflowData().getInstanceText() + " (" + jobData.getWorkflowRuntimeID() + ")";
                        } else if (value.startsWith("${") && value.endsWith("}")) {
                            String property = value.substring(2);
                            property = property.substring(0, property.length() - 1);
                            value = PropertyLoader.getInstance().getProperty(property);
                        }

                        // also add the input values as environment variables
                        EnvironmentType eType = new EnvironmentType();
                        String internalName = (String) getInputPropertyHash(key0).get("intname");
                        eType.setValue(internalName + ":" + value);
                        posixApplication.getEnvironment().add(eType);

                        System.out.println("adding " + key0 + ":" + value);

                        fw.write(value);
                        fw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (getInputPropertyHash(key0).get("sqlurl") != null) { //sql
                    try {
                        Class.forName("com.mysql.Driver");
                        String jdbc = (String) getInputPropertyHash(key0).get("sqlurl");
                        String user = (String) getInputPropertyHash(key0).get("sqluser");
                        String pass = (String) getInputPropertyHash(key0).get("sqlpass");
                        String select = "select " + getInputPropertyHash(key0).get("sqlselect") + " like " + jobData.getPID() + ",1";
                        Connection con = DriverManager.getConnection(jdbc, user, pass);
                        PreparedStatement ps = con.prepareStatement(select);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            String pathValue = PropertyLoader.getInstance().getProperty("prefix.dir") + jobData.getWorkflowRuntimeID();
                            File tmpValue = new File(pathValue);
                            tmpValue.mkdirs();
                            pathValue = pathValue + "/" + key0;
                            tmpValue = new File(pathValue);
                            tmpValue.createNewFile();
                            FileWriter fw = new FileWriter(tmpValue);
                            fw.write(rs.getString(1));
                            fw.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    inputURL.setURI(submitterURL + key0);
                } else if (getInputPropertyHash(key0).get("remote") != null) {
                    inputURL.setURI(getRemoteInputPath(key0, postfix));
                } //                    inputURL.setURI((String)getInputPropertyHash(key0).get("remote")+"_"+jobData.getPID());
                else {
                    if (ifTest(key0, postfix)) {
                        inputURL.setURI(baseURL + jobData.getWorkflowID() + "/" + path);
                    } else {
                        throw new IfException("false input(" + key0 + ")");
                    }
                }

                input.setSource(inputURL);
                jsdl.getJobDescription().getDataStaging().add(input);
            }
        }

        jsdl.getJobDescription().getApplication().getAny().add(
                new JAXBElement(new QName("http://schemas.ggf.org/jsdl/2005/11/jsdl-posix", "POSIXApplication_Type"), POSIXApplicationType.class, posixApplication));

        jsdl.getJobDescription().setResources(new ResourcesType());
        jsdl.getJobDescription().getResources().setOperatingSystem(new OperatingSystemType());
        jsdl.getJobDescription().getResources().getOperatingSystem().setOperatingSystemType(new OperatingSystemTypeType());
        if ("Java".equals(prop.get("type"))) {
            jsdl.getJobDescription().getResources().getOperatingSystem().getOperatingSystemType().setOperatingSystemName(OperatingSystemTypeEnumeration.JAVA_VM);
        } else {
            jsdl.getJobDescription().getResources().getOperatingSystem().getOperatingSystemType().setOperatingSystemName(OperatingSystemTypeEnumeration.LINUX);
        }

        if (prop.get("nodenumber") != null) {
            jsdl.getJobDescription().getResources().setTotalCPUCount(new RangeValueType());
            jsdl.getJobDescription().getResources().getTotalCPUCount().setUpperBoundedRange(new BoundaryType());
            jsdl.getJobDescription().getResources().getTotalCPUCount().getUpperBoundedRange().setValue(Double.parseDouble(prop.get("nodenumber")));
        }

        if (desc.get("gt2.keyminMemory") != null) {
            try {
                float t = Float.parseFloat("" + desc.get("gt2.keyminMemory"));
                jsdl.getJobDescription().getResources().setTotalPhysicalMemory(new RangeValueType());
                jsdl.getJobDescription().getResources().getTotalPhysicalMemory().setLowerBoundedRange(new BoundaryType());
                jsdl.getJobDescription().getResources().getTotalPhysicalMemory().getLowerBoundedRange().setValue(t);
            } catch (Exception e) {
            }
            desc.remove("gt2.keyminMemory");
        }

        CandidateHostsType jsdlCandHost = new CandidateHostsType();

        if (prop.get("resource") != null && prop.get("jobmanager") != null) {
            if (!"".equals(prop.get("resource")) && !"".equals(prop.get("jobmanager"))) {
                String t = prop.get("resource") + "/" + prop.get("jobmanager");
                jsdlCandHost.getHostName().add(t);
            } else if (prop.get("resource") != null) {
                String t = prop.get("resource");
                jsdlCandHost.getHostName().add(t);
            } else if (prop.get("servicemethod") != null) {
                String t = prop.get("servicemethod");
                jsdlCandHost.getHostName().add(t);
            }
        }

        if ("edgi".equals(prop.get("gridtype"))) {
            //System.out.println("WFI-JobConfig-edgi-setCandidateHosts:-resource=" + prop.get("resource")+ " role=" + prop.get("gliterole"));
            String t = prop.get("resource");
            String role = "";
            if (prop.get("gliterole") != null) {
                /**@todo Remove role, if it can get fom AR! **/
                role = prop.get("gliterole").trim();
            }
            jsdlCandHost.getHostName().add(t);
            jsdl.getJobDescription().getResources().getOtherAttributes().put(QName.valueOf("ar"), prop.get("ar").trim());
            jsdl.getJobDescription().getResources().getOtherAttributes().put(QName.valueOf("gliterole"), role);
        }

        jsdl.getJobDescription().getResources().setCandidateHosts(jsdlCandHost);

        boolean remoteGeneratorOutput = false;
        enm = outputs.keys();
        while (enm.hasMoreElements()) {
            key0 = enm.nextElement();
            path = "outputs/" + jobData.getWorkflowRuntimeID() + "/" + jobData.getPID() + "/";

            DataStagingType output = new DataStagingType();
            output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
            output.setDeleteOnTermination(new Boolean(true));
            output.setFileName(outputs.get(key0).get("intname"));
            SourceTargetType outURL = new SourceTargetType();
            if (outputs.get(key0).get("remote") == null) {
                outURL.setURI(uplURL + path + "&link=" + key0);
            } else {
                if ("2".endsWith(outputs.get(key0).get("maincount"))) {
                    remoteGeneratorOutput = true;
                }
                outURL.setURI(outputs.get(key0).get("remote") + "_" + jobData.getPID());
                if (outputs.get(key0).get("remotehost") != null) {
                    output.setFilesystemName(outputs.get(key0).get("remotehost"));
                }
            }
            output.setName(key0);
            output.setTarget(outURL);
            jsdl.getJobDescription().getDataStaging().add(output);
        }

        path = "outputs/" + jobData.getWorkflowRuntimeID() + "/" + jobData.getPID();
        DataStagingType output = new DataStagingType();
        if (remoteGeneratorOutput) {
            output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
            output.setDeleteOnTermination(new Boolean(true));
            output.setFileName("REMOTEGENERATORS_PID");
            SourceTargetType outURL = new SourceTargetType();
            outURL.setURI(uplURL + path);
            output.setTarget(outURL);
            jsdl.getJobDescription().getDataStaging().add(output);
        }

        output = new DataStagingType();
        output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
        output.setDeleteOnTermination(new Boolean(true));
        output.setFileName("stderr.log");
        SourceTargetType outURL = new SourceTargetType();
        outURL.setURI(uplURL + path);
        output.setTarget(outURL);
        jsdl.getJobDescription().getDataStaging().add(output);


        output = new DataStagingType();
        output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
        output.setDeleteOnTermination(new Boolean(true));
        output.setFileName("stdout.log");
        outURL = new SourceTargetType();
        outURL.setURI(uplURL + path);
        output.setTarget(outURL);
        jsdl.getJobDescription().getDataStaging().add(output);

        output = new DataStagingType();
        output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
        output.setDeleteOnTermination(new Boolean(true));
        output.setFileName("gridnfo.log");
        outURL = new SourceTargetType();
        outURL.setURI(uplURL + path);
        output.setTarget(outURL);
        jsdl.getJobDescription().getDataStaging().add(output);

        output = new DataStagingType();
        output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
        output.setDeleteOnTermination(new Boolean(true));
        output.setFileName("guse.jsdl");
        outURL = new SourceTargetType();
        outURL.setURI(uplURL + path);
        output.setTarget(outURL);
        jsdl.getJobDescription().getDataStaging().add(output);

        output = new DataStagingType();
        output.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
        output.setDeleteOnTermination(new Boolean(true));
        output.setFileName("guse.logg");
        outURL = new SourceTargetType();
        outURL.setURI(uplURL + path);
        output.setTarget(outURL);
        jsdl.getJobDescription().getDataStaging().add(output);

        SDLType metabroker = new SDLType();
        metabroker.setConstraints(new ConstraintsType());


        /*binaris*/
        if ("metabroker".equals(prop.get("gridtype"))) {
            enm = prop.keys();
            String[] mid = new String[2];
            while (enm.hasMoreElements()) {
                key0 = enm.nextElement();
                if (key0.startsWith("mbt") && !key0.equals("mbt")) {
                    mid = prop.get(key0).split("_");
                }
                metabroker.getConstraints().getMiddleware().add(mbsdlMiddleware(mid[0], mid[1], desc.get("glite.keyMyProxyServer")));
            }
        } else if (prop.get("gridtype") != null) {
            metabroker.getConstraints().getMiddleware().add(mbsdlMiddleware(prop.get("gridtype"), prop.get("grid"), desc.get("glite.keyMyProxyServer")));
        }


        /*service*/
        if (prop.get("servicetype") != null) {
            metabroker.getConstraints().getMiddleware().add(mbsdlMiddleware(prop.get("servicetype"), prop.get("serviceurl"), null));
        }

        /*GAE*/
        if (prop.get("cloudtype") != null) {
            metabroker.getConstraints().getMiddleware().add(mbsdlMiddleware(prop.get("cloudtype"), prop.get("gaeurl"), null));
        }
        /* JobType        */
        if ("MPI".equals(prop.get("type"))) {
            metabroker.getConstraints().getJobType().add(JobTypeEnumeration.MPI);
        }
//        else  metabroker.getConstraints().getJobType().add(JobTypeEnumeration.MPI);
/*remote type*/
        if (desc.get("glite.keyDataAccessProtocol") != null) {
            metabroker.getConstraints().getRemoteFileAccess().add(RemoteFileAccessEnumeration.EGEE_LFN);
        }

        enm = desc.keys();

        desc.remove("glite.keyMyProxyServer");

        while (enm.hasMoreElements()) {
            key0 = enm.nextElement();
            OtherType other = new OtherType();
            other.setName(key0);
            other.setValue(desc.get(key0));
            metabroker.getConstraints().getOtherConstraint().add(other);
        }

        if (prop.get("jobparser") != null) {
            // add a JDL/RSL parameter for this
            OtherType other = new OtherType();
            other.setName("unicore.keyParserExecution");
            other.setValue("true");
            metabroker.getConstraints().getOtherConstraint().add(other);
        }

        dci.extension.ExtensionType guse = new dci.extension.ExtensionType();
        Hashtable conn = new Hashtable();
        conn.put("url", Base.getZenRunner(jobData.getWorkflowRuntimeID()).getWorkflowData().getPortalID());
        ServiceType st = InformationBase.getI().getService("portal", "submitter", conn, new Vector());
        guse.setProxyservice(st.getServiceUrl() + st.getServiceID());
//        guse.setProxyservice("http://localhost:8080/wspgrade/CredentialProvider?wsdl");
        conn.put("url", PropertyLoader.getInstance().getProperty("service.url"));
        st = InformationBase.getI().getService("wfi", "submitter", conn, new Vector());
//        guse.setWfiservice("http://localhost:8080/wfi/JobStatusService?wsdl");

        guse.setWfiservice(st.getServiceUrl() + st.getServiceID());
        jsdl.getAny().add(new JAXBElement(new QName("uri:MBSchedulingDescriptionLanguage", "SDL_Type"), metabroker.getClass(), metabroker));
        jsdl.getAny().add(new JAXBElement(new QName("extension.dci", "extension_type"), guse.getClass(), guse));

        return jsdl;
    }

    private MiddlewareType mbsdlMiddleware(String pType, String pVO, String pMyProxy) {
        MiddlewareType middleware = new MiddlewareType();
        if ("arc".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.ARC);
        } else if ("boinc".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.BOINC);
        } else if ("gae".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GAE);
        } else if ("gemlca".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GEMLCA);
        } else if ("glite".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GLITE);
        } else if ("gt2".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GT_2);
        } else if ("gt4".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GT_4);
        } else if ("local".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.LOCAL);
        } else if ("lsf".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.LSF);
        } else if ("pbs".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.PBS);
        } else if ("service".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.SERVICE);
        } else if ("unicore".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.UNICORE);
        } else if ("service".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.SERVICE);
        } else if ("axis".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.SERVICE);
        } else if ("gbac".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GBAC);
        } else if ("cloudbroker".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.CLOUDBROKER);
        } else if ("gt5".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.GT_5);
        } else if ("edgi".equals(pType)) {
            middleware.setDCIName(DCINameEnumeration.EDGI);
        }

        middleware.setManagedResource(pVO);
        MyProxyType myProxyServer = new MyProxyType();
        myProxyServer.setServerName(pMyProxy);
        middleware.setMyProxy(myProxyServer);
        return middleware;
    }

    private boolean ifTest(String pPortName, String pIndex) {
        String srcPath = getBasePath() + jobData.getWorkflowID() + "/" + getLocalInputPath(pPortName + pIndex);
        Hashtable<String, String> pValue = getInputPropertyHash(pPortName);
        boolean res = true;
        if (pValue.get("pequaltype") != null && !pValue.get("pequaltype").equals("0")) { //if
            try {
                WFIStorageClient cl = (WFIStorageClient) InformationBase.getI().getServiceClient("storage", "wfi");
                IfBean data = new IfBean();
                data.setOperation(pValue.get("pequaltype"));
                if (pValue.get("pequalvalue") != null) {
                    data.setDstvalue(pValue.get("pequalvalue"));
                }
                if (pValue.get("pequalinput") != null) {
                    String dstPath = getBasePath() + jobData.getWorkflowID() + "/" + getLocalInputPath(pValue.get("pequalinput") + pIndex);
                    data.setDstpath(dstPath);
                }
                data.setSrcpath(srcPath);
                res = cl.ifTest(data);

            } catch (Exception e0) {
                e0.printStackTrace();
                res = false;
            }
        }
        return res;

    }
}

