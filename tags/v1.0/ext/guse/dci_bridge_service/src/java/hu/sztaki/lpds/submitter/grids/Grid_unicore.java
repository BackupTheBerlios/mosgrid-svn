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
 * Unicore submitter
 */
package hu.sztaki.lpds.submitter.grids;


import dci.data.Item;
import de.fzj.unicore.uas.StorageManagement;
import de.fzj.unicore.uas.TargetSystemFactory;
import de.fzj.unicore.uas.client.*;
import de.fzj.unicore.uas.security.ClientProperties;
import de.fzj.unicore.wsrflite.security.ISecurityProperties;
import de.fzj.unicore.wsrflite.xmlbeans.client.RegistryClient;
import eu.unicore.jsdl.extensions.ExecutionEnvironmentDescriptionDocument;
import eu.unicore.security.etd.TrustDelegation;
import hu.sztaki.lpds.dcibridge.config.Conf;
import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.dcibridge.util.*;
import hu.sztaki.lpds.submitter.grids.unicore.JsdlDocBuilder;
import hu.sztaki.lpds.submitter.grids.unicore.UnicoreMiddleWareObject;
import hu.sztaki.lpds.submitter.grids.unicore.UnicoreUtils;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import org.ggf.schemas.bes._2006._08.bes_factory.ActivityStateEnumeration;
import org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType;
import org.ggf.schemas.jsdl._2005._11.jsdl.JobDefinitionType;
import org.ggf.schemas.jsdl._2005._11.jsdl.RangeValueType;
import org.ggf.schemas.jsdl._2005._11.jsdl_posix.POSIXApplicationType;
import org.unigrids.services.atomic.types.GridFileType;
import org.unigrids.services.atomic.types.StatusType;
import org.unigrids.x2006.x04.services.tss.ApplicationResourceType;
import org.unigrids.x2006.x04.services.tss.SubmitDocument;
import org.unigrids.x2006.x04.services.tss.TargetSystemPropertiesDocument;
import org.w3.x2005.x08.addressing.EndpointReferenceType;
import uri.mbschedulingdescriptionlanguage.OtherType;
import uri.mbschedulingdescriptionlanguage.SDLType;
import xmlbeans.org.oasis.saml2.assertion.AssertionDocument;

public class Grid_unicore extends Middleware {

    protected static final String Unicore_STDOUT_FILENAME = "stdout";
    protected static final String Unicore_STDERR_FILENAME = "stderr";
    protected static String LOCALOUTPUTS = "localoutputs.tgz";
    public final static String WRAPPER = "wrapper.sh";
    public final static String LOCALINPUTS = "localinputs.tgz";

    public final static String GUSE_JSDL = "guse.jsdl";

    public Grid_unicore() throws Exception {
        System.out.println("Start unicore");
        THIS_MIDDLEWARE = Base.MIDDLEWARE_UNICORE;
        threadID++;
        setName("guse/dci-bridge:Middleware handler(unicore) - " + threadID);
    }

    @Override
    public void setConfiguration() throws Exception {
        for (Item t : Conf.getMiddleware(Base.MIDDLEWARE_UNICORE).getItem()) {
            t.getUnicore().setSubjectdn(getSubjectDN(t.getUnicore()));
        }
    }


    /**
     * Get DN from service-certificate
     * @param configitem
     * @return
     * @throws CertificateException
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    private static String getSubjectDN(Item.Unicore configitem)
            throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, Exception {
        String subjectDn = null;
        File keystoreFile = new File(configitem.getKeystore());
        KeyStore keystore = loadKeyStore(keystoreFile, configitem.getKeypass(), "PKCS12");
        Enumeration<String> aliases = keystore.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            //System.out.println("alias:" + alias);
            if (alias.equals(configitem.getKeyalias())) {
                System.out.println("Alias found in keystore:" + alias);
                Certificate cert = keystore.getCertificate(alias);
                //System.out.println("cert:" + cert.toString());
                if (cert instanceof X509Certificate) {
                    X509Certificate x509cert = (X509Certificate) cert;
                    // Get subject
                    Principal principal = x509cert.getSubjectDN();
                    subjectDn = principal.getName();
                    // Get issuer
                    //principal = x509cert.getIssuerDN();
                    //String issuerDn = principal.getName();
                    System.out.println("subjectDn:[" + subjectDn + "]");
                }
            }
        }
        if (subjectDn == null) {
            throw new Exception("Alias (" + configitem.getKeyalias() + ") not found in:" + configitem.getKeystore());
        }
        return subjectDn;
    }

    /**
     * Tries to load the keystore in the specified format.
     *
     * @param file keystore file
     * @param passwd password of the keystore
     * @param type type of the keystore
     * @return keystore object
     * @throws IOException
     * @throws KeyStoreException
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     */
    public static KeyStore loadKeyStore(File file, String passwd, String type)
            throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException {

        KeyStore keystore = null;
        FileInputStream inStream = null;
        if (!file.exists()) {
            throw new FileNotFoundException("Keystore " + file.getCanonicalPath() + " not found");
        }
        try {
            System.out.println("Trying loading keystore " + file.getCanonicalPath() + "  in " + type + " format ...");

            keystore = KeyStore.getInstance(type);
            inStream = new FileInputStream(file);
            keystore.load(inStream, passwd.toCharArray());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return keystore;
    }

    @Override
    protected void abort(Job pJob) throws Exception {
        try {
            EndpointReferenceType jobEPR = EndpointReferenceType.Factory.newInstance();
            jobEPR.addNewAddress().setStringValue(pJob.getMiddlewareId());

            JobClient jobClient = new JobClient(
                    pJob.getMiddlewareId(), jobEPR, getSecurityProperties(pJob));
            if (jobClient != null) {
                jobClient.abort();
                System.out.println(":::Unicore:::Activity succesfully terminated");
            }
        } catch (Exception e) {
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            e.printStackTrace();
        }
    }

    @Override
    protected void submit(Job pJob) throws Exception {
        TSSClient client = null; // client object for the communication with the remote WS
        JobClient jobClient = null; // client object for submitting jobs
        String path = Base.getI().getJobDirectory(pJob.getId());

        // bean for storing user realted information
        pJob.setMiddlewareObj(new UnicoreMiddleWareObject());

        //System.out.println(":::Unicore submitter::: path = " + path);
        //sysLog(path, ":::Unicore::: "
        //        + " vo: " + pJob.getConfiguredResource().getVo()
        //        + " registry: " + pJob.getConfiguredResource().getResource() 
        //        + " jobmanager:" + pJob.getConfiguredResource().getJobmanager()); // this is the IDB-tool

        try {
            if (useWrapperSkript(pJob)) {
                compressInputs(pJob);
                createWrapper(pJob);
            }

            // get the security properties
            ClientProperties p = getSecurityProperties(pJob);

            RegistryClient registryClient = initRegistryClient(pJob.getConfiguredResource().getVo(), p);//resourceUrl
            //sysLog(path ,":::Unicore::: registryClient: xmlText:"+registryClient.getResourcePropertiesDocument().xmlText());
            //sysLog(path, ":::Unicore::: init registry");

            // create TSSClient
            client = findAndCreateTSS(registryClient, pJob);

            //sysLog(path, ":::Unicore::: " +client.getTargetSystemName() + " is ready for usage.");
        } catch (Exception e) {
            sysLog(path, ":::Unicore::: runtine exception occured: " + e.getMessage());
            errorLog(path + "outputs", "Runtine exception occured while submitting the job: " + e.getMessage());
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            return;
        }

        // Submits the list of jobs to the target Unicore system.
        try {
            POSIXApplicationType pType
                    = XMLHandler.getData(pJob.getJSDL().getJobDescription().getApplication().getAny(), POSIXApplicationType.class);

            HashMap<String, StorageClient> xtreemFSStorage = ((UnicoreMiddleWareObject) pJob.getMiddlewareObj()).getXtreemFSStorage();
            String dn = ((UnicoreMiddleWareObject)pJob.getMiddlewareObj()).getDn();

            String uuid = getUUID(pJob);

            // create object for generating the JSDL description of jobs
            JsdlDocBuilder jsdlDocBuider = new JsdlDocBuilder(
                    path,
                    getCommandLineParams(pType),
                    //pJob.getId(),
                    uuid,
                    BinaryHandler.getBinaryFileName(pType),
                    getIDBTool(pJob),
                    getIndividualCPUTime(pJob),
                    getIndividualCPUCount(pJob),
                    getTotalResourceCount(pJob),
                    getIndividualPhysicalMemory(pJob),
                    BinaryHandler.isMPI(pJob.getJSDL()),
                    isParserExecution(pJob),
                    InputHandler.getRemoteInputs(pJob),
                    OutputHandler.getRemoteOutputs(pJob.getJSDL()),
                    pType.getEnvironment(),
                    dn,
                    xtreemFSStorage != null && !xtreemFSStorage.isEmpty()
                    );

            SubmitDocument submitDoc = jsdlDocBuider.generateJSDLDoc();
            sysLog(path, ":::SubmitDoc::: " + submitDoc.toString());
            jobClient = client.submit(submitDoc);
            uploadData(pJob, jobClient);
            pJob.setMiddlewareId(jobClient.getEPR().getAddress().getStringValue());//store joburl/jobid

            jobClient.start();
            sysLog(path, ":::Unicore::: started " + UnicoreUtils.getFriendlyUrl(pJob.getMiddlewareId()));
            //sysLog(path, ":::Unicore::: jobClient.getStatus() = n" + jobClient.getStatus().toString());

            pJob.setResource(pJob.getConfiguredResource().getVo());

        } catch (Exception e) {
            sysLog(path, ":::Unicore::: failed to submit job: " + e.getMessage());
            errorLog(path + "outputs", "Failed to submit job: " + e.getMessage());
            pJob.setStatus(ActivityStateEnumeration.FAILED);
            e.printStackTrace();
        }

    }

    private String getUUID(Job pJob) {
        String uuid = pJob.getJSDL().getJobDescription().getApplication().getApplicationName();
        if (uuid!= null && uuid.indexOf("guse://")>=0) {
            uuid = uuid.substring(7);
        }
//        if (uuid!= null && uuid.indexOf("/") >= 0) {
//            uuid = uuid.substring(0, uuid.indexOf("/"));
//        }
        return uuid;
    }

    @Override
    protected void getOutputs(Job pJob) throws Exception {
    }

    @Override
    protected void getStatus(Job pJob) throws Exception {
        String path = Base.getI().getJobDirectory(pJob.getId());

        JobClient jobClient = null;

        sysLog(path, ":::Unicore::: manager.getActivityStatus, current status:" + pJob.getStatus());
        StatusType.Enum status = null;


        try {
            //sysLog(path, ":::Unicore::: try get status");
            EndpointReferenceType jobEPR = EndpointReferenceType.Factory.newInstance();
            jobEPR.addNewAddress().setStringValue(pJob.getMiddlewareId());
            jobClient = new JobClient(
                    pJob.getMiddlewareId(),
                    jobEPR,
                    getSecurityProperties(pJob));

            status = jobClient.getStatus();
            sysLog(path, ":::Unicore::: "
                    + UnicoreUtils.getFriendlyUrl(jobClient.getUrl())
                    + " jobClient.getStatus() = " + status.toString()
                    + " jobClient.getExitCode() = " + jobClient.getExitCode());
        } catch (Exception e) {
            sysLog(path, ":::Unicore:::" + e.getMessage());
            e.printStackTrace();
        }

        Integer exitCode = getExitCode(path, jobClient);

        //int portalStatus = 0;
        if (status == StatusType.READY) {
            //portalStatus = 10;
        } else if (status == StatusType.RUNNING || status == StatusType.STAGINGIN || status == StatusType.STAGINGOUT) {
            //portalStatus = 5;
            pJob.setStatus(ActivityStateEnumeration.RUNNING);
        } else if (status == StatusType.UNDEFINED) {
            //portalStatus = 7;
            pJob.setStatus(ActivityStateEnumeration.FAILED);
        } else if (status == StatusType.QUEUED) {
            //portalStatus = 4;
        } else if (status == StatusType.SUCCESSFUL && isExitCodeSuccess(exitCode)) {
            //portalStatus = 6;
            pJob.setStatus(ActivityStateEnumeration.FINISHED);
        } else if (status == StatusType.FAILED || !isExitCodeSuccess(exitCode)) {
            //portalStatus = 7;
            pJob.setStatus(ActivityStateEnumeration.FAILED);
        }

        if (pJob.getStatus() != null &&
                (pJob.getStatus() == ActivityStateEnumeration.FINISHED
                 || pJob.getStatus() == ActivityStateEnumeration.FAILED)) {

            // download data
            if (!downloadData(pJob, jobClient)) {
                pJob.setStatus(ActivityStateEnumeration.FAILED);
            }

            HashMap<String, StorageClient> metadataStorage = ((UnicoreMiddleWareObject) pJob.getMiddlewareObj()).getMetadataStorage();
            String dn = ((UnicoreMiddleWareObject) pJob.getMiddlewareObj()).getDn();

            // process metadata
            processMetaData(pJob, metadataStorage, dn);
        }

        //sysLog(path, ":::Unicore::: portalStatus pjob.getStatus " + pJob.getStatus().value());
    }

    private Integer getExitCode(final String loggingPath, final JobClient jobClient) {
        Integer exitCode = null;
        try {
            exitCode = jobClient.getExitCode();
        } catch (Exception e) {
            sysLog(loggingPath, ":::Unicore::: could not retrieve exitCode - " + e.getMessage());
            e.printStackTrace();
        }
        return exitCode;
    }

    private boolean isExitCodeSuccess(final Integer exitCode) {
        // exitCode is success iff equal to 0
        return exitCode != null && exitCode == 0;
    }


    private static ClientProperties getSecurityProperties(Job pJob) {
        ClientProperties p = ((UnicoreMiddleWareObject)pJob.getMiddlewareObj()).getClientProperties();
        if (p == null) {
            // init client object for the registry
            p = makeSecurityPropertiesSAML(pJob);
            ((UnicoreMiddleWareObject)pJob.getMiddlewareObj()).setClientProperties(p);
        }
        return p;
    }


    /**
     * Creates and returns an IUASSecurityProperties object what contains the
     * security description of the WS connection.
     *
     * @param jobdir jobs working dir
     * @param resource jobs resource
     * @return IUASSecurityProperties object
     */
    private static ClientProperties makeSecurityPropertiesSAML(Job pJob) {
        String jobdir = Base.getI().getJobDirectory(pJob.getId());
        String resource = pJob.getConfiguredResource().getVo();

        Item.Unicore config = Conf.getItem(Base.MIDDLEWARE_UNICORE, resource).getUnicore();

        ClientProperties p = new ClientProperties();

        p.setProperty(ISecurityProperties.WSRF_SSL, "true");
        p.setProperty(ISecurityProperties.WSRF_SSL_CLIENTAUTH, "true");

        p.setProperty(ISecurityProperties.WSRF_SSL_KEYSTORE, config.getKeystore());
        p.setProperty(ISecurityProperties.WSRF_SSL_KEYTYPE, "pkcs12");
        p.setProperty(ISecurityProperties.WSRF_SSL_KEYPASS, config.getKeypass());
        p.setProperty(ISecurityProperties.WSRF_SSL_KEYALIAS, config.getKeyalias());

        p.setProperty(ISecurityProperties.WSRF_SSL_TRUSTPASS, config.getTrustpass());
        p.setProperty(ISecurityProperties.WSRF_SSL_TRUSTSTORE, config.getTruststore());
        p.setProperty(ISecurityProperties.WSRF_SSL_TRUSTTYPE, "JKS");

        p.setSignMessage(true);
        p.getETDSettings().setExtendTrustDelegation(true);

        List<TrustDelegation> trustDelegations = new ArrayList<TrustDelegation>();

        //load the assertion file
        File tdFile = new File(jobdir + "x509up");
        try {
            AssertionDocument td = AssertionDocument.Factory.parse(tdFile);

            String xmlText = td.getAssertion().getIssuer().xmlText();
            String dn = xmlText.substring(xmlText.indexOf('>')+1, xmlText.lastIndexOf('<'));
            ((UnicoreMiddleWareObject)pJob.getMiddlewareObj()).setDn(dn);

            sysLog(jobdir, ":::Unicore::: Subject DN: " + dn);

            trustDelegations.add(new TrustDelegation(td));
        } catch (Exception e) {
            sysLog(jobdir, ":::Unicore:::" + e.getMessage());
            e.printStackTrace();
        }

        if(trustDelegations!=null && !trustDelegations.isEmpty()) {
            p.getETDSettings().setTrustDelegationTokens(trustDelegations);
        }

        return p;
    }

    private static RegistryClient initRegistryClient(String resourceUrl, ClientProperties p) throws Exception {
        String url = "https://" + resourceUrl + "/REGISTRY/services/Registry?res=default_registry";
        EndpointReferenceType epr = EndpointReferenceType.Factory.newInstance();
        epr.addNewAddress().setStringValue(url);
        return new RegistryClient(url, epr, p);
    }

    private static TSSClient findAndCreateTSS(
            RegistryClient registry,
            Job pJob) throws Exception {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 90);
        List<EndpointReferenceType> tsfEPRs = registry.listAccessibleServices(TargetSystemFactory.TSF_PORT);

        String idbTool = getIDBTool(pJob);

        // shuffe, thus balance load over available TSS
        if (tsfEPRs.size() > 0) {
            Collections.shuffle(tsfEPRs, new Random(System.currentTimeMillis()));
        }

        // Iterate the available installations
        for (EndpointReferenceType epr : tsfEPRs) {
            String serverUrl = epr.getAddress().getStringValue().trim();

            try {
                //System.out.println(":::Unicore::: findAndCreateTSS::: serverUrl: " + serverUrl + " epr:" + epr);
                TSFClient tsf = new TSFClient(serverUrl, epr, registry.getSecurityProperties());
                List<EndpointReferenceType> tssEPR = tsf.getAccessibleTargetSystems();

                if (tssEPR.size() > 0) {
                    Collections.shuffle(tssEPR, new Random(System.currentTimeMillis()));
                }

                // Check for available TargetSystems
                if (tssEPR == null || tssEPR.isEmpty()) {
                    try {
                        TSSClient tss = tsf.createTSS(c);
                        if (checkMatch(tss, registry, idbTool, epr, pJob)) {
                            return tss;
                        }
                    } catch (Exception e) {
                        // TSSClient Exception
                        sysLog(pJob, ":::UNICORE::: TSS: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Create a new TargetSystem
                for (EndpointReferenceType epr2 : tssEPR) {
                    try {
                        TSSClient tss = new TSSClient(epr2.getAddress().getStringValue().trim(), epr2, registry.getSecurityProperties());
                        if (checkMatch(tss, registry, idbTool, epr2, pJob)) {
                            return tss;
                        }
                    } catch (Exception e) {
                        // TSSClient Exception
                        sysLog(pJob, ":::UNICORE::: TSS: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                sysLog(pJob, ":::UNICORE::: TSS: " + e.getMessage());
                e.printStackTrace();
            }
        }

        throw new Exception("No target system available");
    }

    /**
     * Further checks for the application in the IDB
     *
     * @param tss
     * @param idbtool
     * @param epr
     * @return
     */
    public static boolean checkMatch(
            TSSClient tss,
            RegistryClient registry,
            String idbtool,
            EndpointReferenceType epr,
            Job pJob) {
        boolean matches = false;
        try {
            // requirements check
            matches = matches(tss, registry, pJob);
        } catch (Exception e) {
            e.printStackTrace();
            matches = false;
        }

        if (!matches) {
            sysLog(pJob, ":::UNICORE::: TSS: "
                    + UnicoreUtils.getFriendlyUrl(epr.getAddress().getStringValue()) + " requirements check failed. No matching resources!");
            return false;
        } else {
            // check if no IDB tool is selected
            if (useWrapperSkript(pJob)) {
                sysLog(pJob, ":::UNICORE::: TSS: "
                        + UnicoreUtils.getFriendlyUrl(epr.getAddress().getStringValue()) + " requirements check successful.");
                return true;
            }
            // check for the IDB tool
            else {
                for (ApplicationResourceType app : tss.getApplications()) {
                    String out = app.getApplicationName() + " " + app.getApplicationVersion();
                    if (idbtool.equals(out)) {
                        sysLog(pJob, ":::UNICORE::: TSS: "
                                + UnicoreUtils.getFriendlyUrl(epr.getAddress().getStringValue())
                                + " requirements check successful.");
                        return true;
                    }
                }
                sysLog(pJob, ":::UNICORE::: TSS: "
                        + UnicoreUtils.getFriendlyUrl(epr.getAddress().getStringValue())
                        + " requirements check failed for idbtool: " + idbtool);
                return false;
            }
        }
    }

    /**
     * Checks each Resource for requirements including: - memory - cores - nodes
     * - walltime - xtreemfs storage - map parallel execution
     *
     * @param tss
     * @return
     * @throws Exception
     */
    private static boolean matches(
            TSSClient tss,
            RegistryClient registry,
            Job jobConfig
            ) throws Exception {

        sysLog(jobConfig, ":::UNICORE::: TSS: checking + " + tss.getTargetSystemName());

        // see http://unicore.svn.sourceforge.net/viewvc/unicore/ucc/trunk/ucc-core/src/main/java/de/fzj/unicore/ucc/helpers/Runner.java?revision=HEAD&view=markup
        TargetSystemPropertiesDocument.TargetSystemProperties props
                = tss.getResourcePropertiesDocument().getTargetSystemProperties();

        // check for MPI: parallel execution environment
        if (BinaryHandler.isMPI(jobConfig.getJSDL())) {
            boolean match = false;
            if (props.getExecutionEnvironmentDescriptionArray() != null) {
                for (ExecutionEnvironmentDescriptionDocument.ExecutionEnvironmentDescription descs : props.getExecutionEnvironmentDescriptionArray()) {
                    if (descs.getName().equals("Parallel Execution")) {
                        match = true;
                        break;
                    }
                }
            }
            if (!match) {
                sysLog(jobConfig, " \tTSS requirements check failed for Parallel Execution Environment.");
                return false;
            }
        }

        if (props == null) {
            sysLog(jobConfig, " \tTSS requirements check failed due to NULL-properties.");
        }

        // memory
        double individualPhysicalMemoryAvail = props.getIndividualPhysicalMemory().getRangeArray()[0].getUpperBound().getDoubleValue();
        double individualPhysicalMemoryReq = UnicoreUtils.parseDoubleSafe(getIndividualPhysicalMemory(jobConfig), 0);
        if (individualPhysicalMemoryAvail < individualPhysicalMemoryReq) {
            sysLog(jobConfig, " \tTSS requirements check failed for individualCPUCount." + individualPhysicalMemoryAvail + " < " + individualPhysicalMemoryReq);
            return false;
        }

        // total number of CPUs
        double totalResourceCountAvail = props.getTotalResourceCount().getRangeArray()[0].getUpperBound().getDoubleValue();
        double totalResourceCountReq = UnicoreUtils.parseDoubleSafe(getTotalResourceCount(jobConfig), 0);
        if (totalResourceCountAvail < totalResourceCountReq) {
            sysLog(jobConfig, " \tTSS requirements check failed for individualCPUCount." + totalResourceCountAvail + " < " + totalResourceCountReq);
            return false;
        }

        // walltime
        double individualCPUTimeAvail = props.getIndividualCPUTime().getRangeArray()[0].getUpperBound().getDoubleValue();
        double individualCPUTimeReq = UnicoreUtils.parseDoubleSafe(getIndividualCPUTime(jobConfig), 0);
        if (individualCPUTimeAvail < individualCPUTimeReq) {
            sysLog(jobConfig, " \tTSS requirements check failed for individualCPUCount." + individualCPUTimeAvail + " < " + individualCPUTimeReq);
            return false;
        }

        // number of cores (CPUs) per node
        double individualCPUAvail = props.getIndividualCPUCount().getRangeArray()[0].getUpperBound().getDoubleValue();
        double individualCPUReq = UnicoreUtils.parseDoubleSafe(getIndividualCPUCount(jobConfig), 0);
        if (individualCPUAvail < individualCPUReq) {
            sysLog(jobConfig, " \tTSS requirements check failed for individualCPUCount." + individualCPUAvail + " < " + individualCPUReq);
            return false;
        }

        HashMap<String, StorageClient> xtreemFSStorage = ((UnicoreMiddleWareObject) jobConfig.getMiddlewareObj()).getXtreemFSStorage();
        HashMap<String, StorageClient> metadataStorage = ((UnicoreMiddleWareObject) jobConfig.getMiddlewareObj()).getMetadataStorage();
        xtreemFSStorage.clear();

        // check for XtreemFS as storage        
        String targetSystemName = tss.getTargetSystemName();
        sysLog(jobConfig, " \tchecking for XtreemFS...");

        boolean hasXtreemFSStorage = false;
        List<EndpointReferenceType> eprs = getStorageEPRs(tss, registry);
        for (EndpointReferenceType epr : eprs) {
            try {
                StorageClient client = new StorageClient(epr.getAddress().getStringValue(),epr, getSecurityProperties(jobConfig));

                // check for XtreemFS as storage
                if (client.getStorageName() != null
                    && client.getStorageName().trim().toLowerCase().contains("xtreemfs")
                    && epr.getAddress().getStringValue().contains(targetSystemName)
                    && !epr.getAddress().getStringValue().contains("bisgrid") // XtreemFS on bisgrid currently not working
                    ) {
                    sysLog(jobConfig, " \tnew XtreemFS Storage found: " +
                            client.getStorageName() + " " + UnicoreUtils.getFriendlyUrl(epr.getAddress().getStringValue()));

                    hasXtreemFSStorage = true;
                    xtreemFSStorage.put(epr.getAddress().getStringValue(), client);
                }

                // check for metadata
                if (client.supportsMetadata()
                    && !metadataStorage.containsKey(epr.getAddress().getStringValue())) {
                    sysLog(jobConfig, " \tnew Metadata Storage found: " +
                         client.getStorageName() + " " + UnicoreUtils.getFriendlyUrl(epr.getAddress().getStringValue()));

                    metadataStorage.put(epr.getAddress().getStringValue(), client);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!hasXtreemFSStorage) {
            sysLog(jobConfig, " \tchecking for XtreemFS FAILED on server: " + targetSystemName);
        }

        List<DataStagingType> remoteInputs = InputHandler.getRemoteInputs(jobConfig);
        List<DataStagingType> remoteOutputs = OutputHandler.getRemoteOutputs(jobConfig.getJSDL());

        // check if xtreemfs is required
        if (!hasXtreemFSStorage    // xtreemfs was not found on any server
            && (remoteInputs != null && !remoteInputs.isEmpty()
                || remoteOutputs != null && !remoteOutputs.isEmpty())) {
            for (DataStagingType type : remoteInputs) {
                if (type.getFileName() != null && type.getFileName().toLowerCase().contains("xtreemfs")) {
                    sysLog(jobConfig, " \tTSS requirements check failed for XtreemFS Storage.");
                    return false;
                }
            }
            for (DataStagingType type : remoteOutputs) {
                if (type.getFileName() != null && type.getFileName().toLowerCase().contains("xtreemfs")) {
                    sysLog(jobConfig, " \tTSS requirements check failed for XtreemFS Storage.");
                    return false;
                }
            }
        }

        // all checks passed
        return true;
    }

    /**
    * get a list of storage addresses, either from the TSSClient supplied to the constructor, or
    * from the registry
    */
    protected static List<EndpointReferenceType> getStorageEPRs(
            TSSClient tss, RegistryClient registryClient) {
        //else check storages in the registry
        List<EndpointReferenceType> result = new ArrayList<EndpointReferenceType>();
        try {
            result.addAll(registryClient.listAccessibleServices(StorageManagement.SMS_PORT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.addAll(tss.getStorages());
        return result;
    }

    /**
     * add user specific jdl attributes
     * @param jobConfig
     * @param JDKEY
     * @return
     */
    private static String getSDLType(Job jobConfig, String JDKEY) {
        SDLType sdlType = XMLHandler.getData(jobConfig.getJSDL().getAny(), SDLType.class);
        List<OtherType> ls = sdlType.getConstraints().getOtherConstraint();
        for (OtherType value : ls) {
            if (value.getName().contains(JDKEY)) {
                return value.getValue();
            }
        }
        return null;
    }

    protected static boolean isParserExecution(Job jobConfig) {
        String JDKEY="unicore.keyParserExecution";
        String value = getSDLType(jobConfig, JDKEY);
        return value != null && !value.trim().equals("");
    }


    /**
     * walltime
     *
     * @param jobConfig
     * @return
     */
    protected static String getIndividualCPUTime(Job jobConfig) {
        String JDKEY="unicore.keyWalltime";
        return getSDLType(jobConfig, JDKEY);
    }


    /**
     * IDB tool
     *
     * @param jobConfig
     * @return
     */
    protected static String getIDBTool(Job jobConfig) {
        return jobConfig.getConfiguredResource().getJobmanager();
    }

    /**
     * MPI Cores Number
     *
     * @param jobConfig
     * @return
     */
    protected static String getIndividualCPUCount(Job jobConfig) {
        String JDKEY="unicore.keyCores";
        return getSDLType(jobConfig, JDKEY);
    }

    /**
     * MPI Node Number
     *
     * @param jobConfig
     * @return
     */
    protected static String getTotalResourceCount(Job jobConfig) {
        RangeValueType type = jobConfig.getJSDL().getJobDescription().getResources().getTotalCPUCount();
        if (type != null) {
            return "" + ((type.getUpperBoundedRange() != null)? type.getUpperBoundedRange().getValue() : "");
        }
        return null;
    }

    /**
     * Physical Memory
     *
     * @param jobConfig
     * @return
     */
    protected static String getIndividualPhysicalMemory(Job jobConfig) {
        String JDKEY="unicore.keyMemory";
        return getSDLType(jobConfig, JDKEY);
    }


    /**
     * Start Metadata Extraction for users home directory
     * @param jobConfig
     */
    private static void processMetaData(Job pJob,  HashMap<String, StorageClient> metadataStorage, String dn) {
        try {
            sysLog(pJob, ":::Unicore::: processing metadata");
            if (metadataStorage != null && !metadataStorage.isEmpty()) {
                HashSet<String> metaDataServers = new HashSet<String>();
                for (StorageClient storageClient : metadataStorage.values()) {
                    MetadataClient meta = storageClient.getMetadataClient();

                    // avoid processing a metadata several times
                    if (!metaDataServers.contains(meta.getUrl())) {
                        sysLog(pJob, ":::Unicore::: found metadata server " + UnicoreUtils.getFriendlyUrl(meta.getUrl()));

                        if (dn != null && !dn.trim().equals("")) {
                            sysLog(pJob,
                                    ":::Unicore::: metadata extraction started for "
                                    + "xtreemfs://"+dn.replaceAll(" ", "%20"));
                            meta.startMetadataExctraction("/"+dn.replaceAll(" ", "%20"), 10);
                        }
                        metaDataServers.add(meta.getUrl());
                    }
                }
            }
            else {
                sysLog(pJob, ":::Unicore::: metadata not supported!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void uploadData(
            String fileName, String path, StorageClient storageClient)
                throws FileNotFoundException, IOException, Exception {
        FileInputStream in = null;
        BufferedInputStream buf = null;
        FileTransferClient fileTransferClient = null;
        try {
            sysLog(path, ":::Unicore::: Upload input " + path + "/" + fileName);

            in = new FileInputStream(path + "/" + fileName);
            buf = new BufferedInputStream(in);
            buf = new BufferedInputStream(in);

            fileTransferClient = storageClient.getImport(fileName);
            fileTransferClient.writeAllData(buf);

            //sysLog(path, ":::Unicore::: Upload input successful " + fileName);
        } catch (Exception e) {
            sysLog(path, ":::Unicore:::Upload input failed for " + fileName);
            e.printStackTrace();
            // do not throw the exception so we can download the other files.
        } finally {
            UnicoreUtils.saveCloseStream(in);
        }
    }

    private static void uploadData(Job pJob, JobClient jobClient) throws Exception {
        String path = Base.getI().getJobDirectory(pJob.getId());

        // client object for storage
        StorageClient storageClient = jobClient.getUspaceClient();

        // upload inputs if UNICORE IDB-tool is set
        if (UnicoreUtils.stringIsNotEmpty(getIDBTool(pJob))) {
            List<DataStagingType> inputs = InputHandler.getlocalInputs(pJob);
            for (DataStagingType inp : inputs) {
                String fileName = inp.getFileName();
                if (!"execute.bin".equals(fileName)) {
                    uploadData(fileName, path, storageClient);
                }
            }

            // upload gUSE jsdl
            uploadData(GUSE_JSDL, path+"outputs/", storageClient);
        }
        // upload wrapper skript and tar-gz inputs
        else {
            uploadData(WRAPPER, path, storageClient);
            storageClient.changePermissions(WRAPPER, true, true, true);

            uploadData(LOCALINPUTS, path, storageClient);
        }

        sysLog(path, ":::Unicore::: Upload input finished");
    }
    
    private static void downloadData(
            String fileName, 
            String path,
            StorageClient storageClient,
            boolean appendLogExtension,
            boolean generatorCheck)
                throws FileNotFoundException, IOException, Exception {
        FileOutputStream out = null;
        BufferedOutputStream buf = null;
        FileTransferClient fileTransferClient = null;
        try {
            sysLog(path, ":::Unicore::: getExport " + fileName + " from " + UnicoreUtils.getFriendlyUrl(storageClient.getUrl()));

            out = new FileOutputStream(path + "outputs/" + fileName + (appendLogExtension?".log":""));
            buf = new BufferedOutputStream(out);

            fileTransferClient = storageClient.getExport("/" + fileName);
            fileTransferClient.readAllData(buf);          
        } catch (Exception e) {
            sysLog(path, ":::Unicore::: FAILED getExport for "
                    + fileName+ " from "
                    + UnicoreUtils.getFriendlyUrl(storageClient.getUrl()) + "\n"
                    + e.getLocalizedMessage());
            // do not throw the exception so we can download the other files.

            try {
                // consider generators, so we have to check for a postfix like _0, _1, ...
                if (generatorCheck) {
                    GridFileType[] listing = storageClient.listDirectory("/");
                    for(GridFileType file : listing) {
                        String newFilePath = file.getPath();
                        if (newFilePath.contains("/")) {
                            String newFileName = newFilePath.substring(newFilePath.lastIndexOf("/"));
                            if (newFileName.contains(fileName+"_")) {
                                //sysLog(path, ":::Unicore:::getExport found generator file for " + fileName + ": " +newFileName );
                                downloadData(newFileName, path, storageClient, appendLogExtension, false);
                            }
                        }
                    }
                }

                //e.printStackTrace();
            } catch (Exception ex2) {
                ex2.printStackTrace();
            }
        } finally {
            UnicoreUtils.saveCloseStream(out);
        }
    }

    private static boolean useWrapperSkript(Job pJob) {
        return UnicoreUtils.stringIsEmpty(getIDBTool(pJob));
    }

    private static boolean downloadData(Job pJob, JobClient jobClient) throws Exception {
        String path = Base.getI().getJobDirectory(pJob.getId());
        try {
            StorageClient storageClient = jobClient.getUspaceClient();

            // transfer STDOUT
            downloadData(Unicore_STDOUT_FILENAME, path, storageClient, true, false);

            // transfer STDERR
            downloadData(Unicore_STDERR_FILENAME, path, storageClient, true, false);

            // a wrapper skript is used if the IDB tool is not set
            if (useWrapperSkript(pJob)) {
                downloadData(LOCALOUTPUTS, path, storageClient, false, false);

                // extract the outputs
                if (!extractFile(path + "outputs/", LOCALOUTPUTS)) {
                    sysLog(path, "Could not extract outputsandbox: " + LOCALOUTPUTS);
                    errorLog(path + "outputs/", "Could not extract outputsandbox: " + LOCALOUTPUTS);
                }
            }
            // download files specified in the JSDL, if no wrapper skript is used
            else {
                List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
                for (DataStagingType inp : outputs) {
                    String fileName = inp.getFileName();
                    
                    // these files are generated by the wrapper script. thus
                    // we can skip downloading them when not using the wrapper.
                    if (!"guse.jsdl".equals(fileName)
                        && !"guse.logg".equals(fileName)
                        && !"gridnfo.log".equals(fileName)
                        && !"stderr.log".equals(fileName)
                        && !"stdout.log".equals(fileName)) {
                        downloadData(fileName, path, storageClient, false, true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return checkOutputFiles(pJob, jobClient);
    }

    private static boolean checkOutputFiles(Job pJob, JobClient jobClient) {
        String outputDir = Base.getI().getJobDirectory(pJob.getId()) + "outputs/";

        // append UNICORE execution log to the gridnfo.log:
        appendExecutionLogToGridnfo(outputDir, jobClient);

        List<DataStagingType> outputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        boolean success = true;
        try {
            for (DataStagingType t : outputs) {
                File of = new File(outputDir + "/" + t.getFileName());
                if (!of.exists()) {
                    //if not exists, it can be a generator, check the first element:
                    of = new File(outputDir + "/" + t.getFileName() + "_0");
                    if (!of.exists()) {
                        success = false;
                        sysLog(outputDir, "Can not copy the Output file:" + t.getFileName());
                        errorLog(outputDir, "Can not copy the Output file:" + t.getFileName());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            success = false;
        }

        if (success) {
            success = checkGridnfo(pJob, outputDir, jobClient);
        }

        return success;
    }

    private static boolean checkGridnfo(Job pJob, String outputDir, JobClient jobClient) {
        boolean success = true;

        if (useWrapperSkript(pJob)) {
            success = false;
            BufferedReader input = null;
            try {
                input = new BufferedReader(new FileReader(outputDir + "/gridnfo.log"));
                String line = null;
                while ((line = input.readLine()) != null) {
                    if (line.contains("Wrapper script finished succesfully")) {
                        success = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                UnicoreUtils.saveCloseStream(input);
            }
        }
        return success;
    }


    private static void appendExecutionLogToGridnfo(String outputDir, JobClient jobClient) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outputDir + "/gridnfo.log", true); // append to the stream
            out.write(getExecutionLog(jobClient).getBytes());
            if (jobClient.getStatus() == StatusType.SUCCESSFUL) {
                out.write("\n\nWrapper script finished succesfully\n".getBytes());
            }
        } catch (Exception e) {
            sysLog(outputDir, ":::Unicore:::getExport failed for gridnfo.log");
            e.printStackTrace();
        } finally {
            UnicoreUtils.saveCloseStream(out);
        }
    }
    
    /**
     * Get the UNICORE execution log
     */
    public static String getExecutionLog(JobClient jobClient) {
        try {
            return jobClient.getJobLog();
        } catch (Exception e) {
            System.out.println(":::Unicore:::"+ e.getMessage());
            return e.getMessage();
        }
    }


    /**
     * Extracts a file in a specified dir, and deletes it.
     *
     * @return boolean
     */
    private static boolean extractFile(String dir, String file) {
        try {
            String cmd = "tar -xzf " + file;
            //sysLog(cmd);
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(dir));
            BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv = p.waitFor();
            if (exitv == 0) {
                UnicoreUtils.saveCloseStream(sin);
                new File(dir + "/" + file).delete();
                return true;
            } else {
                String sor = "";
                while ((sor = sin.readLine()) != null) {
                    //sysLog(sor);
                    System.out.println(sor);
                }
                UnicoreUtils.saveCloseStream(sin);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates LOCALINPUTS in the jobs directory, if it do not exist or is
     * empty.
     *
     * @return
     */
    private static void compressInputs(Job pJob) throws IOException, InterruptedException {
        String path = Base.getI().getJobDirectory(pJob.getId());

        //List<String> fileList = new ArrayList<String>();
        File f = new File(path + "/" + LOCALINPUTS);
        if (!f.exists()) {
            String compressfiles = "";
            List<DataStagingType> inputs = InputHandler.getlocalInputs(pJob);
            for (DataStagingType inp : inputs) {
                //fileList.add(inp.getFileName() );
                compressfiles += inp.getFileName() + " ";
            }

            // new ZipUtil().compressFiles(path.substring(0,path.length()-1) , fileList, path + LOCALINPUTS);

            String cmd = "tar -czf " + LOCALINPUTS + " " + compressfiles + " ";
            sysLog(path, cmd);
            Process p;
            p = Runtime.getRuntime().exec(cmd, null, new File(path));
            BufferedReader sin = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            int exitv = p.waitFor();
            if (exitv == 0) {
                UnicoreUtils.saveCloseStream(sin);
                //return true;
            } else {
                String sor = "";
                while ((sor = sin.readLine()) != null) {
                    sysLog(path, sor);
                }
                UnicoreUtils.saveCloseStream(sin);
                //return false;
            }
        } else {
            sysLog(path, LOCALINPUTS + " exists");
        }
    }

    /**
     * Legeneralja a WRAPPER scriptet
     */
    private static void createWrapper(Job pJob) throws Exception {
        JobDefinitionType jsdl = pJob.getJSDL();
        //String userid = BinaryHandler.getUserName(jsdl);
        String path = Base.getI().getJobDirectory(pJob.getId());
        POSIXApplicationType pType = XMLHandler.getData(jsdl.getJobDescription().getApplication().getAny(), POSIXApplicationType.class);

        String binname = BinaryHandler.getBinaryFileName(pType);
        String params = getCommandLineParams(pType);
        String stdOut = BinaryHandler.getStdOutFileName(pType);
        String stdErr = BinaryHandler.getStdErrorFileName(pType);

        //compress the output files
        Stack localoutfiles = new Stack();
        List<DataStagingType> loutputs = OutputHandler.getLocalOutputs(pJob.getJSDL());
        for (DataStagingType t : loutputs) {
            if (!("stderr.log".equals(t.getFileName())
                    || "stdout.log".equals(t.getFileName())
                    || "guse.jsdl".equals(t.getFileName())
                    || "guse.logg".equals(t.getFileName()))) {
                localoutfiles.add(t.getFileName());
            }
        }

        LinuxWrapperForGrids w = null;
        try {
            w = new LinuxWrapperForGrids(path);
            //w.setLocalOutsAndCallback(localoutfiles, LOCALOUTPUTS, CALLBACK_STATUS_SERVLET_URL, userid, pJob.getId());
            w.setLocalOutputs(localoutfiles, LOCALOUTPUTS);
            w.addFunctionsAndStartScript(null, LinuxWrapperForGrids.FUNCTION_GSIFTPREMOTEIO);
            w.export_LD_LIBRARY_PATH();

            w.extractAndDelete(LOCALINPUTS);
            //app.tgz eseten
            if (BinaryHandler.isAppTgzExtension(jsdl)) {
                w.extractAndDelete(binname);
                binname = BinaryHandler.getAppTgzBase(jsdl);
            }
            /**
             * remote inputs
             */
            List<DataStagingType> rinputs = InputHandler.getRemoteInputs(pJob);
            for (DataStagingType t : rinputs) {
                //w.writeln("if (! globus-url-copy " + t.getSource().getURI() + " file:`pwd`/" + t.getFileName() + " ) then \n echo Can not copy the Input file " + t.getFileName() + " from:" + t.getSource().getURI() + " >> stderr.log \n ext 1 \n fi ");
            }
            if (BinaryHandler.isJavaJob(jsdl)) {
                w.setJavaEnviroments(Conf.getP().getJava());
                w.runJava(binname, params, stdOut, stdErr);
            } else {
                w.runBinary(binname, params, stdOut, stdErr);
            }

            List<DataStagingType> routputs = OutputHandler.getRemoteOutputs(pJob.getJSDL());
            for (DataStagingType t : routputs) {
                String remotefile = t.getTarget().getURI();
                //..value22/0/       pert levág          -> ..value22_0
                if (remotefile.endsWith("/")) {
                    remotefile = remotefile.substring(0, remotefile.lastIndexOf("/"));
                }
                //w.writeln("if (! globus-url-copy file:`pwd`/"+t.getFileName()+" "+t.getTarget().getURI()+" ) then \n echo Can not copy the Output file "+t.getFileName()+" to "+t.getTarget().getURI()+" >> stderr.log \n  ext 1 \n fi ");
                // w.writeln(" gsiftpupload " + remotefile + " " + t.getFileName());
            }
        } finally {
            w.close();
        }
    }

    private static String getCommandLineParams(POSIXApplicationType pType) {
        String params = "";
        for (String s : BinaryHandler.getCommandLineParameter(pType)) {
            params = params.concat(" " + s);
        }
        return params;
    }

    /**
     * logs to stderr.log
     */
    private static void errorLog(String OutputDir, String txt) {
        try {
            FileWriter tmp = new FileWriter(OutputDir + "/stderr.log", true);
            BufferedWriter out = new BufferedWriter(tmp);
            out.newLine();
            out.write(txt);
            out.flush();
            out.close();
        } catch (Exception e) {
            sysLog(OutputDir, e.toString());
        }
    }

    /**
     * Write log into sys.log & stdout.
     */
    public static void sysLog(Job job, String txt) {
        String logdir = Base.getI().getJobDirectory(job.getId());
        sysLog(logdir, txt);
    }


    /**
     * Write log into sys.log & stdout.
     */
    public static void sysLog(String logdir, String txt) {
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
            e.printStackTrace();
        }
    }
        
}
