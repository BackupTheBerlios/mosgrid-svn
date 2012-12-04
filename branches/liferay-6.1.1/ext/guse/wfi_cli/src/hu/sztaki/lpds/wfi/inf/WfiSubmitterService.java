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
 * Definition of the services offered from the WFI to the submitter
 */

package hu.sztaki.lpds.wfi.inf;

import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;

/**
 *
 * @author krisztian
 */
public interface WfiSubmitterService 
{
/**
 * Current job status processing
 * @param pData job status descriptor
 * @deprecated 
 */
    public void setStatus(JobStatusBean pData);
/**
 * Batch job status processing
 * @param pData job status descriptor list
 * @return is the processing successful or not
 */    
    public Boolean setCollectionStatus(Vector pData);
    
/**
 * Submitter asks for jobs
 * @param pSubmitterID grid name
 * @param pCount number of jobs
 * @param pTimeOut delay
 * @return the number of jobs which are achievable from all asked
 */   
    public int callJob(String pGridID, int pCount,int pTimeout);
    
/**
 * Service overload termination
 * @param pServiceType service descriptor
 * @param pServiceID service ID
 * @deprecated 
 */    
    public void unForbidden(String pServiceType, String pServiceID);
}
