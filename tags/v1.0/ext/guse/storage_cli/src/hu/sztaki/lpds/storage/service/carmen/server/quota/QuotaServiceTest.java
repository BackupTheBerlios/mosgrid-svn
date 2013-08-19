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
package hu.sztaki.lpds.storage.service.carmen.server.quota;

/**
 * Calls the QuotaService class for testing purposes.
 * 
 * @author lpds
 */
public class QuotaServiceTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // System.out.println("test run...");
        try {

            // speed test start
            long start = System.currentTimeMillis();
            // System.out.println("start: " + start);

            // System.out.println("stepp_0: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().addPlussRtIDQuotaSize("portalA", "userA", "workflowA", "rtIDA", new Long(100));
            // System.out.println("stepp_1: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));            
            QuotaService.getInstance().addPlussRtIDQuotaSize("portalA", "userA", "workflowA", "rtIDB", new Long(200));
            // System.out.println("stepp_2: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().addPlussOthersQuotaSize("portalA", "userA", "workflowA", new Long(300));
            // System.out.println("stepp_3: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().deleteWorkflowRtIDQuotaItem("portalA", "userA", "workflowA", "rtIDA");
            // System.out.println("stepp_4: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().deleteWorkflowAllOutputsQuotaItem("portalA", "userA", "workflowA");            
            // System.out.println("stepp_5: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().deleteWorkflowAllQuotaItem("portalA", "userA", "workflowA");
            // System.out.println("stepp_6: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().addPlussOthersQuotaSize("portalA", "userA", "workflowA", new Long(400));
            // System.out.println("stepp_7: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().addPlussRtIDQuotaSize("portalA", "userA", "workflowA", "rtIDC", new Long(500));
            // System.out.println("stepp_8: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().addPlussRtIDQuotaSize("portalA", "userA", "workflowA", "rtIDC", new Long(50));
            // System.out.println("stepp_9: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            QuotaService.getInstance().deleteWorkflowAllQuotaItem("portalA", "userA", "workflowA");
            // System.out.println("stepp_A: " + QuotaService.getInstance().getUserQuotaItems("portalA", "userA"));
            // System.out.println("");
            // System.out.println("init...:");
            
            // init quota service
            QuotaService.getInstance().initQuotaService();
 
            // get user quotaItems hash
            // System.out.println("stepp_Z: " + QuotaService.getInstance().getUserQuotaItems("delportal", "deluser"));
            
            // test end
            long stop = System.currentTimeMillis();
            // System.out.println("stop: " + stop);
            // System.out.println("stop-start: " + (stop - start) + " msec");
        } catch (Exception e) {
            // e.printStackTrace();
        }
        // System.out.println("test end...");
    }

}
