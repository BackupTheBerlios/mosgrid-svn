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
 * Lifearay alapu felhasznalomanagement implementacio
 */

package hu.sztaki.lpds.pgportal.user;

import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import java.util.List;

/**
 * @author krisztian karoczkai
 */
public class LiferayImpl implements UserHandlerFace{

    @Override
    public String getUserName(String pUID) {
        String res="";
        try{
            User user = getUser(pUID);
            res=user.getFullName();
        }
        catch(Exception e){/*Liferay hiba*/}
        return res;
    }

    @Override
    public boolean isUserInRole(String pUID, String pRoleName) {
        try{
            User user = getUser(pUID);
            List<Role> grps1=user.getRoles();
            for(Role t:grps1){
                if(pRoleName.equals(t.getName())) return true;
            }
        }
        catch(Exception e){/*liferay exception*/}
        return false;
    }

/**
 * Liferay felhasznaloi objektum lekerdezese portlet request remoteUser id alapjan
 * @param pUID
 * @return
 * @throws Exception
 */
    private User getUser(String pUID)throws Exception{return UserLocalServiceUtil.getUserById(Long.parseLong(pUID));}
}
