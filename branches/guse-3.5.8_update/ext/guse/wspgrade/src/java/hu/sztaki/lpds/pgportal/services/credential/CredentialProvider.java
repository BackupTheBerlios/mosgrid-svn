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
 * Proxy szolgaltato szerviz
 */

package hu.sztaki.lpds.pgportal.services.credential;


import hu.sztaki.lpds.information.local.PropertyLoader;
import java.io.File;
import java.io.FileInputStream;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @author krisztian karoczkai
 */
@WebService()
public class CredentialProvider {

    /**
     * Web service operation. Proxy lekerdezese byte tomb formajaban
     * @param pGroup user csoport
     * @param pUser user azonosito
     * @param pMiddleware middleware neve
     * @param pVo grid/vo/csoport neve
     * @return user proxy
     * @throws Exception
     */
    @WebMethod(operationName = "get")
    public byte[] get(@WebParam(name = "pGroup")
    String pGroup, @WebParam(name = "pUser")
    String pUser, @WebParam(name = "pMiddleware")
    String pMiddleware, @WebParam(name = "pVo")
    String pVo) {
        String path=PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/"+pUser+"/x509up."+pVo;
        File f=new File(path);
        if(!f.exists()) throw new RuntimeException("credential not exist("+pUser+"/"+pVo+")");
        byte[] res=new byte[(int)f.length()];
        FileInputStream fis = null;
        try {
            fis=new FileInputStream(f);
            fis.read(res);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
