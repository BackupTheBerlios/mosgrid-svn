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
 * Proxy Provider service
 */

package hu.sztaki.lpds.pgportal.services.asm;


import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.File;
import java.io.FileInputStream;


public class ASMCredentialProvider {

    public ASMCredentialProvider() {
    }

    
     /**
     * Web service operation. Getting proxy as a byte array
     * @param pGroup user group
     * @param pUser user id
     * @param pMiddleware middleware name
     * @param pVo grid/vo/group name
     * @return user proxy
     * @throws Proxy does not exsists, file i/o error
     */

    public byte[] get(String pUser,String pVo)throws Exception{
        String path=PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/"+pUser+"/x509up."+pVo;
        File f=new File(path);
        if(!f.exists()) throw new Exception("credential not exist("+pUser+"/"+pVo+")");
        byte[] res=new byte[(int)f.length()];
        FileInputStream fis=new FileInputStream(f);
        fis.read(res);
        fis.close();
        return res;
    }


}
