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
 * Copyright (C) 2009-2010 MTA SZTAKI LPDS
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * In addition, as a special exception, MTA SZTAKI gives you permission to link the
 * code of its release of the 3G Bridge with the OpenSSL project's "OpenSSL"
 * library (or with modified versions of it that use the same license as the
 * "OpenSSL" library), and distribute the linked executables. You must obey the
 * GNU General Public License in all respects for all of the code used other than
 * "OpenSSL". If you modify this file, you may extend this exception to your
 * version of the file, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version.
 */
package hu.sztaki.lpds.submitter.grids.unicore;

import de.fzj.unicore.wsrflite.xmlbeans.WSUtilities;
import eu.unicore.jsdl.extensions.ArgumentDocument;
import eu.unicore.jsdl.extensions.ExecutionEnvironmentDocument;
import hu.sztaki.lpds.submitter.grids.Grid_unicore;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.ggf.schemas.jsdl.x2005.x11.jsdl.*;
import org.ggf.schemas.jsdl.x2005.x11.jsdlPosix.EnvironmentType;
import org.ggf.schemas.jsdl.x2005.x11.jsdlPosix.POSIXApplicationDocument;
import org.ggf.schemas.jsdl.x2005.x11.jsdlPosix.POSIXApplicationType;
import org.unigrids.x2006.x04.services.tss.SubmitDocument;

/**
 * This class is responsible to generate correct JSDL descriptions and wrapper
 * scripts of jobs for a BES plugin instance.
 */
public class JsdlDocBuilder {

    private JobDefinitionType jobDef;
    private SubmitDocument submitDoc;

    private String path;
    private String exeparams;
    private String uuid;
    private String binaryName;
    private String idbTool;
    private String walltime;
    private String coreNumber;
    private String nodeNumber;
    private String memory;
    private boolean mpiParallelExecution;
    private boolean parserExecution;
    private boolean hasXtreemFSStorage;
    private String certificateDN;

    List<org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType> remoteInputs;
    List<org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType> remoteOutputs;

    List<org.ggf.schemas.jsdl._2005._11.jsdl_posix.EnvironmentType> envType;

    private SimpleDateFormat format;

    /**
     * Initializes the JsdlDocBuilder object for further usage according to the
     * parameters.
     */
    public JsdlDocBuilder(
            String path,
            String exeparams,
            String uuid,
            String binaryName,
            String idbTool,
            String walltime,
            String coreNumber,
            String nodeNumber,
            String memory,
            boolean mpiParallelExecution,
            boolean parserExecution,
            List<org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType> rinputs,
            List<org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType> routputs,
            List<org.ggf.schemas.jsdl._2005._11.jsdl_posix.EnvironmentType> eType,
            String certificateDN,
            boolean hasXtreemFSStorage) {
        this.submitDoc = SubmitDocument.Factory.newInstance();
        this.jobDef = submitDoc.addNewSubmit().addNewJobDefinition();
        this.jobDef.addNewJobDescription();

        this.path = path;
        this.exeparams = exeparams;
        this.uuid = uuid;
        this.binaryName = binaryName;
        this.idbTool = idbTool;
        this.walltime = walltime;
        this.coreNumber = coreNumber;
        this.nodeNumber = nodeNumber;
        this.memory = memory;

        this.mpiParallelExecution = mpiParallelExecution;
        this.parserExecution = parserExecution;

        this.hasXtreemFSStorage = hasXtreemFSStorage;
        this.certificateDN = certificateDN;

        this.remoteInputs = rinputs;
        this.remoteOutputs = routputs;

        this.envType = eType;
        
        this.format = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
    }

    /**
     * Generates and returns a JobDefinitionDocument object what contains the
     * JSDL description of the parameter job.
     *
     * @param job
     * @return JobDefinitionDocument object
     * @throws Exception contains file information which is not accessable under
     * ftp root dir
     */
    public SubmitDocument generateJSDLDoc() throws Exception {
        buildApplication();
        buildDataStaging();

        return submitDoc;
    }

    /**
     * Fills the jobDescription object with the necessary HPC Profile
     * Application description information according to the data stored in the
     * job object.
     *
     * @param jobDescription
     * @param job
     */
    private void buildApplication() {
        POSIXApplicationDocument posixDoc = POSIXApplicationDocument.Factory.newInstance();
        POSIXApplicationType posixType = posixDoc.addNewPOSIXApplication();

        // TODO get the value input fields
        for (org.ggf.schemas.jsdl._2005._11.jsdl_posix.EnvironmentType property : envType) {
            EnvironmentType eType = posixType.addNewEnvironment();
            String[] parts = property.getValue().split(":");
            eType.setName(parts[0]);
            eType.setStringValue(parts[1]);
            Grid_unicore.sysLog(path, ":::Unicore:::buildAddParameters Value: "+parts[0]+":"+parts[1]);
        }

        String jobName = "";

        ApplicationType appType = jobDef.getJobDescription().addNewApplication();

        // add parameters
        if (UnicoreUtils.stringIsNotEmpty(exeparams)) {
            Grid_unicore.sysLog(path, ":::Unicore:::buildApplication using parameters " + exeparams);
            posixType.addNewArgument().setStringValue(exeparams);
        }

        // insert Posix executable
        if (UnicoreUtils.stringIsEmpty(idbTool)
            && UnicoreUtils.stringIsNotEmpty(binaryName)) {
            Grid_unicore.sysLog(path, ":::Unicore:::buildApplication using wrapper.sh on " + binaryName);
//            posixType.addNewExecutable().setStringValue("./execute.bin");
            posixType.addNewExecutable().setStringValue("./wrapper.sh");
            jobName = binaryName;
        }
        // insert IDB tool if filled
        else if (UnicoreUtils.stringIsNotEmpty(idbTool)) {
            Grid_unicore.sysLog(path, ":::Unicore:::buildApplication binary:" + binaryName);
            String applicationName = idbTool;
            String[] subs = idbTool.split(" ");
            if (subs.length > 1) {
                String applicationVersion = subs[subs.length - 1];
                int pos = applicationName.length() - applicationVersion.length() - 1;
                applicationName = applicationName.substring(0, pos);
                appType.setApplicationName(applicationName);
                appType.setApplicationVersion(applicationVersion);
                Grid_unicore.sysLog(path,
                        ":::Unicore:::buildApplication using idbtool:"
                        + applicationName + " v. "+ applicationVersion);

                jobName = applicationName + " v. " + applicationVersion;
            }
            else {
                Grid_unicore.sysLog(path, ":::Unicore:::buildApplication using idbtool:" + applicationName);
                appType.setApplicationName(subs[0]);
            }
        }
        WSUtilities.insertAny(posixDoc, appType);

        // add resource requirements
        ResourcesType type = jobDef.getJobDescription().addNewResources();

        // set MPI parallel job
        if (mpiParallelExecution) {
            ExecutionEnvironmentDocument eed = ExecutionEnvironmentDocument.Factory.newInstance();
            ExecutionEnvironmentDocument.ExecutionEnvironment ee = eed.addNewExecutionEnvironment();
            ee.setName("Parallel Execution"); // Parallel Execution

            // number of processes
            long processes =
                UnicoreUtils.parseLongSafe(coreNumber,1)
                * UnicoreUtils.parseLongSafe(nodeNumber, 1);
            Grid_unicore.sysLog(path, ":::Unicore:::buildAddParameters Processes:" + processes);

            ArgumentDocument.Argument arg = ee.addNewArgument();
            arg.setName("Number of Cores");
            arg.setValue(""+(processes));

            // the type.set-call deletes all previously set values in JobDescrition
            // therefor we have to insert the appType again
            WSUtilities.insertAny(eed, type);
        }
        else if (parserExecution) {
            System.out.println("Execute parser environment");
            
            ExecutionEnvironmentDocument eed = ExecutionEnvironmentDocument.Factory.newInstance();
            ExecutionEnvironmentDocument.ExecutionEnvironment ee = eed.addNewExecutionEnvironment();
            ee.setName("Parser"); // Parallel Execution

            // the type.set-call deletes all previously set values in JobDescrition
            // therefor we have to insert the appType again
            WSUtilities.insertAny(eed, type);
        }

        Grid_unicore.sysLog(path, ":::Unicore::: buildAddParameters individualCPUTime:" + walltime);
        Grid_unicore.sysLog(path, ":::Unicore::: buildAddParameters individualCPUCount:" + coreNumber);
        Grid_unicore.sysLog(path, ":::Unicore::: buildAddParameters totalResourceCount:" + nodeNumber);
        Grid_unicore.sysLog(path, ":::Unicore::: buildAddParameters individualPhysicalMemory:" + memory);
        Grid_unicore.sysLog(path, ":::Unicore::: buildAddParameters mpiParallel:" + mpiParallelExecution);

        // walltime
        long time = 60*60; // 60 minutes default
        if (UnicoreUtils.stringIsNotEmpty(walltime)) {
            time = UnicoreUtils.parseLongSafe(walltime, 0l);
            // parse minutes to seconds
            time = time * 60;
        }
        type.addNewIndividualCPUTime().addNewExact().setStringValue(String.valueOf(time));

        // cores
        String cores = UnicoreUtils.stringIsNotEmpty(coreNumber)?
                coreNumber : "1";  // 1 core default
        type.addNewIndividualCPUCount().addNewExact().setStringValue(cores);

        // nodes
        String nodes = mpiParallelExecution && UnicoreUtils.stringIsNotEmpty(nodeNumber)?
                nodeNumber : "1"; // 1 node default
        type.addNewTotalResourceCount().addNewExact().setStringValue(nodes);

        // memory
        long memoryDouble = 256*1024*1024; // 256 MB default
        if (UnicoreUtils.stringIsNotEmpty(memory)) {
            memoryDouble = UnicoreUtils.parseLongSafe(memory, 0l);
            // the memory requirement is in MB. so we have to transform it to bytes
            memoryDouble = memoryDouble * 1024 * 1024;
        }
        type.addNewIndividualPhysicalMemory().addNewExact().setStringValue(String.valueOf(memoryDouble));

        jobDef.getJobDescription().addNewJobIdentification().setJobName(
                jobName + " submitted at " + format.format(new Date()));
    }


    @SuppressWarnings("unchecked")
    private void buildDataStaging() {
        // Handle remote files as Job Export and Import definitions.
        // compare: http://guse.svn.sourceforge.net/viewvc/guse/guse/trunk/
        //            submitter/src/java/hu/sztaki/lpds/submitter/grids/gt/work.java?annotate=2

        // Job-Import from XtreemFS to the USpace:
        for (org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType stagingType : remoteInputs) {
            String key = stagingType.getFileName();
            String value = stagingType.getSource().getURI();
            if (value == null || !(value.toLowerCase().startsWith("xtreemfs://"))) {
                throw new RuntimeException("Invalid protokoll, check remote input: " + key + " " + value);
            } else {
                Grid_unicore.sysLog(path, ":::Unicore::: Remote Input File: " + key + " " + value);
            }
            if (!hasXtreemFSStorage) {
                Grid_unicore.sysLog(path, ":::Unicore::: No XtreemFS-Storage found on Target-System for remote file " + key + " " + value);
            }

            DataStagingType import1 = jobDef.getJobDescription().addNewDataStaging();
            import1.setFileName(key);
            import1.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
            import1.addNewSource().setURI(value.replaceAll(" ", "%20"));
        }

        // Job-Export from the USpace to XtreemFS:
        for (org.ggf.schemas.jsdl._2005._11.jsdl.DataStagingType stagingType : remoteOutputs) {
            String key = stagingType.getFileName();
            String value = stagingType.getSource().getURI();
            if (value == null || !(value.toLowerCase().startsWith("xtreemfs://"))) {
                throw new RuntimeException("Invalid protokoll, check remote input: " + key + " " + value);
            } else {
                Grid_unicore.sysLog(path, ":::Unicore::: Remote Output File: " + key + " " + value);
            }
            if (!hasXtreemFSStorage) {
                Grid_unicore.sysLog(path, ":::Unicore::: No XtreemFS-Storage found on Target-System for remote file " + key + " " + value);
            }

            DataStagingType export1 = jobDef.getJobDescription().addNewDataStaging();
            export1.setFileName(key);
            export1.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
            export1.addNewTarget().setURI(value.replaceAll(" ", "%20"));
        }

        // all export
        if (certificateDN != null) {

            String dn_path = "xtreemfs://" + certificateDN
                    + File.separator + "results"
                    + File.separator + uuid
                    //jobConfig.getJobData().getWorkflowID()
                    //+ File.separator + jobConfig.getJobData().getJobID()
                    + File.separator;

            dn_path = dn_path.replaceAll(" ", "%20");
            Grid_unicore.sysLog(path, ":::Unicore::: Remote File Directory: '" + dn_path + "'");
            if (hasXtreemFSStorage) {
                // Stage out everything
                DataStagingType export_all = jobDef.getJobDescription().addNewDataStaging();
                export_all.setFileName("/");
                export_all.setCreationFlag(CreationFlagEnumeration.OVERWRITE);
                export_all.addNewTarget().setURI(dn_path);
            } else {
                Grid_unicore.sysLog(path, ":::Unicore::: Could not locate XtreemFS-Storage on Target-System! Ignoring stage out to XtreemFS");
            }

        }

    }
}