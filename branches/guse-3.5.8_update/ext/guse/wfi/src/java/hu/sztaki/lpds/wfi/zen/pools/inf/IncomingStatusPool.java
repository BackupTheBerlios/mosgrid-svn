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
 * Submitterektol erkezo status informaciok kezelese
 */

package hu.sztaki.lpds.wfi.zen.pools.inf;

import hu.sztaki.lpds.wfs.com.JobStatusBean;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public interface IncomingStatusPool
{
    public void addNewStatusInformation(JobStatusBean pValue);
    public void addNewStatusInformations(Vector<JobStatusBean> pValue);
    public long getPoolSize();
    public JobStatusBean removeFirstElementFromPool();
    public JobStatusBean getFirstElement(String pZenID);
    public int removeJobs(String pZenID);
    public void setNoRuning(JobStatusBean pStatus);
}
