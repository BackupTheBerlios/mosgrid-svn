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
package hu.sztaki.lpds.pgportal.service.workflow;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.workflow.notify.Notify;

/**
 * The notify portlet uses this. (helper class)
 *
 * @author lpds
 */
public class UserQuotaUtils {
    
    private static UserQuotaUtils instance = null;
/**
 * Constructor, creating the singleton instance
 */
    public UserQuotaUtils() {
        if (instance == null) {
            instance = this;
        }
        //
    }
    
    /**
     * Returns the UserQuotaUtils instance.
     * 
     * @return 
     */
    public static UserQuotaUtils getInstance() {
        if (instance == null) {
            instance = new UserQuotaUtils();
        }
        return instance;
    }

    /**
     * Checks whether the user still 
     * has some free quota storage.
     * @param userID user ID
     * @return true if still there is some
     */
    public boolean userQuotaIsNotFull(String userID) {
        return !userQuotaIsFull(userID);
    }   
    
    /**
     * Checks whether the user 
     * overrun its quota.
     * @param userID user ID
     * @return true if overrun
     */
    public boolean userQuotaIsFull(String userID) {
        Long quotaPercentMax = new Long("" + PropertyLoader.getInstance().getProperty(Notify.property_quota_max_percent));
        long quotaSpace = PortalCacheService.getInstance().getUser(userID).getQuotaSpace(userID);
        long usedQuotaSpace = PortalCacheService.getInstance().getUser(userID).getUseQuotaSpace();
        long quotaPercent = usedQuotaSpace * 100 / quotaSpace;
        if (quotaPercent > quotaPercentMax.longValue()) {
            // overrun
            return true;
        }
        // not overrun
        return false;
    }
    
}
