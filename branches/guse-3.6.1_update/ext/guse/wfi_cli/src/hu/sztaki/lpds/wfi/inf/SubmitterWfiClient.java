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
 * SubmitterWfiClient.java
 * Interface of the communication between the submitter and the WFI
 */

package hu.sztaki.lpds.wfi.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;

public interface SubmitterWfiClient extends BaseCommunicationFace
{
/**
 * Job status modification
 * @param pData job descriptor
 */
   public boolean setStatus(JobStatusBean pData) throws Exception;
   
/**
 * Modification of more job statuses 
 * @param pData Vector of the job descriptors (inside: JobStatusBean)
 */
   public boolean setCollectionStatus(Vector pData) throws Exception;

/**
 * The submitter asked for jobs
 * @param pSubmitterID grid name
 * @param pCount number of jobs
 * @param pTimeOut delay
 * @return the number of jobs which are achievable from all asked
 */   
    public int callJob(String pGridID, int pCount,int pTimeOut) throws Exception;
   
}
