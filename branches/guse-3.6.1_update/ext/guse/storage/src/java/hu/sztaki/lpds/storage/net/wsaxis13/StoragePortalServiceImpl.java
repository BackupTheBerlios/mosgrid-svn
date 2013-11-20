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
package hu.sztaki.lpds.storage.net.wsaxis13;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.inf.StoragePortalService;
import hu.sztaki.lpds.storage.service.carmen.commons.WorkflowFileUtils;
import hu.sztaki.lpds.storage.service.carmen.server.delete.DeleteUtils;
import hu.sztaki.lpds.storage.service.carmen.server.delete.DeleteWorkflowFiles;
import hu.sztaki.lpds.storage.service.carmen.server.quota.QuotaService;
import hu.sztaki.lpds.storage.service.carmen.server.upload.UploadUtils;
import hu.sztaki.lpds.wfs.com.VolatileBean;
import hu.sztaki.lpds.wfs.com.ComDataBean;
import hu.sztaki.lpds.storage.com.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author lpds
 */
public class StoragePortalServiceImpl implements StoragePortalService {

    private DeleteWorkflowFiles deleteWorkflowFiles;
/**
 * Constructor, data initialization
 */
    public StoragePortalServiceImpl() {
        try {
            deleteWorkflowFiles = new DeleteWorkflowFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @see StoragePortalService#getAllWorkflowSize(hu.sztaki.lpds.storage.com.StoragePortalUserBean)
     */
    @Override
    public Hashtable getAllWorkflowSize(StoragePortalUserBean pUser) {
        Hashtable res = new Hashtable();
        if ("true".equals(PropertyLoader.getInstance().getProperty("guse.storageclient.localmode.sendquota"))) {
            try {
                QuotaService.getInstance().initQuotaService();
                Hashtable tmp=QuotaService.getInstance().getUserQuotaItems(pUser.getPortalID(), pUser.getUserID());
                // System.out.println("------"+pUser.getPortalID()+":"+pUser.getUserID()+":"+tmp);
                return tmp;
            } catch(Exception e) {
                return res;
            }
        }
        return res;
    }
    
    /**
     * @see StoragePortalService#getWorkflowAllJobSize(hu.sztaki.lpds.storage.com.StoragePortalUserBean)
     */
    @Override
    public Hashtable getWorkflowAllJobSize(StoragePortalUserWorkflowBean pUser) {
        Hashtable res=new Hashtable();
        return res;
    }
    
    /**
     * @see StoragePortalService#deleteWorkflow(hu.sztaki.lpds.wfs.com.ComDataBean)
     */
    @Override
    public Boolean deleteWorkflow(ComDataBean value) {
        try {
            deleteWorkflowFiles.deleteWorkflow_all(value);
            return new Boolean(true);
        } catch(Exception e){e.printStackTrace();return new Boolean(false);}
    }
    
    /**
     * @see StoragePortalService#deleteWorkflowInstance(hu.sztaki.lpds.wfs.com.ComDataBean)
     */
    @Override
    public Boolean deleteWorkflowInstance(ComDataBean value) {
        try {
            deleteWorkflowFiles.deleteWorkflow_outputs_rtID(value);
            return new Boolean(true);
        } catch(Exception e){e.printStackTrace();return new Boolean(false);}
    }
    
    /**
     * @see StoragePortalService#deleteWorkflowOutputs(hu.sztaki.lpds.wfs.com.ComDataBean)
     */
    @Override
    public Boolean deleteWorkflowOutputs(ComDataBean value) {
        try {
            deleteWorkflowFiles.deleteWorkflow_outputs_all(value);
            return new Boolean(true);
        } catch(Exception e){e.printStackTrace();return new Boolean(false);}
    }
    
    /**
     * @see StoragePortalService#deleteWorkflowLogOutputs(hu.sztaki.lpds.wfs.com.ComDataBean, java.util.Vector)
     */
    @Override
    public Boolean deleteWorkflowLogOutputs(ComDataBean idBean, Vector value) {
        try {
            for (int vPos = 0; vPos < value.size(); vPos++) {
                ComDataBean tmpBean = (ComDataBean) value.elementAt(vPos);
                idBean.setJobID(tmpBean.getJobID());
                idBean.setJobPID(tmpBean.getJobPID());
                deleteWorkflowFiles.deleteWorkflow_log_outputs(idBean);
            }
            return new Boolean(true);
        } catch(Exception e){e.printStackTrace();return new Boolean(false);}
    }
    
    /**
     * @see StoragePortalService#copyWorkflowFiles(hu.sztaki.lpds.storage.com.StoragePortalCopyWorkflowBean)
     */
    @Override
    public boolean copyWorkflowFiles(StoragePortalCopyWorkflowBean value) {
        try {
            // System.out.println("copyWorkflowFiles...:");
            // System.out.println("portalID         : " + value.getPortalID());
            // System.out.println("userID           : " + value.getUserID());
            // System.out.println("sourceWorkflowID : " + value.getSourceWorkflowID());
            // System.out.println("destinWorkflowID : " + value.getDestinWorkflowID());
            // System.out.println("copyHash         : " + value.getCopyHash());
            // parse copyHash (copy workflow files)...
            return WorkflowFileUtils.getInstance().copyWorkflowFilesParseCopyHash(value);
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }
    
    /**
     * @see StoragePortalService#uploadWorkflowFiles(hu.sztaki.lpds.storage.com.UploadWorkflowBean)
     */
    @Override
    public boolean uploadWorkflowFiles(UploadWorkflowBean value) {
        try {
            UploadUtils.getInstance().uploadTemporaryFiles(value);
            //
            DeleteUtils.getInstance().deleteOldFiles(value);
            return true;
        } catch (Exception e) {
            // e.printStackTrace();
            return false;
        }
    }
    
    /**
     * @see StoragePortalService#deleteWorkflowVolatileOutputs(hu.sztaki.lpds.wfs.com.VolatileBean)
     */
    @Override
    public boolean deleteWorkflowVolatileOutputs(VolatileBean value) {
        try {
            // System.out.println("VolatileBean in storage side...");
            deleteWorkflowFiles.deleteWorkflow_volatile_outputs(value);
            return new Boolean(true);
        } catch(Exception e){e.printStackTrace();return new Boolean(false);}
    }


    /**
     * @see StoragePortalService#getUploadingFilePercent(java.lang.String, java.lang.String)
     */
    @Override
    public int getUploadingFilePercent(String sid,String filename){
        try{
            return UploadUtils.getInstance().getUploadPercentforSingleFile(sid, filename);
          } catch(Exception e){e.printStackTrace();return -1;}
    }
    
}
