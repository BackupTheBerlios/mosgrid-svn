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
 * Examines if the default data has been already loaded to the database, if not, it loads it
 */

package hu.sztaki.lpds.information.service.alice;

import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.OptionBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.inf.InitCommand;
import hu.sztaki.lpds.information.service.alice.xml.ConfigXMLSaxParser;
import java.io.File;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author krisztian
 */
public class DataBaseInitCommandImpl implements InitCommand
{
    private static final String[] propertyKeys={"guse.system.database.driver","guse.system.database.url","guse.system.database.user","guse.system.database.password"};

@Override
    public void run(ServletContext cx,HttpServletRequest request)
    {
        try
        {
            String inited=DH.getI().getOptionValue("service.init");
        }
        catch(Exception e)
        {
            try
            {
                ConfigXMLSaxParser handler = new ConfigXMLSaxParser();
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser.parse( new File(cx.getRealPath("/WEB-INF/config/service.xml")), handler);
                OptionBean tmp=new OptionBean();
                tmp.setId("service.init");
                tmp.setTxt("succesfull");
                DH.getI().setOptionValue(tmp);
            }
            catch(Exception e0){e0.printStackTrace();}
        }
        if(isValidDatabaseparameters(request))
            initDatabasePropertyes(request);    
    }
/**
 * Checks the parameters for the database connection
 * @param request http request descriptor
 * @return true = all data are set as requested
 */
    private boolean isValidDatabaseparameters(HttpServletRequest request){
        for(String t:propertyKeys){
            System.out.println(t+"="+request.getParameter(t));
            if(request.getParameter(t)==null) return false;
            else if("".equals(request.getParameter(t))) return false;
        }
        return true;
    }
/**
 * Modifying or creating service properties based on the received database parameters
 * @param request http request
 */
    private void initDatabasePropertyes(HttpServletRequest request){
        List<GuseServiceBean> services=DH.getI().getAllGuseService();
        for(GuseServiceBean t: services){
            System.out.println("======="+t.getUrl()+"========");
            for(String p:propertyKeys){
                manageDataBaseProperty(t, p, request.getParameter(p));
                System.out.println(t.getUrl()+":"+p+"="+request.getParameter(p));
            }
        }
    }
/**
 * Creating or modifying a service parameter
 * @param service service descriptor
 * @param pKey property key
 * @param pValue property value
 */
    private void manageDataBaseProperty(GuseServiceBean service, String pKey, String pValue){
        ServicePropertyBean prop;
        try{prop=DH.getI().getProperty(service, pKey);}
        catch(NullPointerException e){prop=new ServicePropertyBean();}
        prop.setPropkey(pKey);
        prop.setPropvalue(pValue);
        DH.getI().persistProperty(service, prop);
    }

}
