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
 * Processing of the default configuration file
 */

package hu.sztaki.lpds.information.service.alice.xml;

import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.GuseServiceCommunicationBean;
import hu.sztaki.lpds.information.data.GuseServiceTypeBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import hu.sztaki.lpds.information.net.wsaxis13.ResourceServiceImpl;
import hu.sztaki.lpds.information.service.alice.DH;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author krisztian
 */
public class ConfigXMLSaxParser extends DefaultHandler
{
    private GuseServiceCommunicationBean com=null;
    private GuseServiceBean service=null;
    private String middleware="",grid="";
    private HashMap props;
    private ResourceServiceImpl resourceHandler=new ResourceServiceImpl();
/**
 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
 */
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException{
        if("type".equals(arg2)){
            GuseServiceTypeBean tmp=new GuseServiceTypeBean();
            tmp.setSname(arg3.getValue("name"));
            tmp.setTxt(arg3.getValue("title"));
            DH.getI().newServiceType(tmp);
        }
        else if("communication-type".equals(arg2)){
            com=new GuseServiceCommunicationBean();
            com.setCname(arg3.getValue("name"));
            com.setTxt(arg3.getValue("title"));
            DH.getI().newServiceComm(com);
        }
        else if("service".equals(arg2)){
            service=new GuseServiceBean();
            service.setOwner("root");
            service.setState(true);
            service.setUrl(arg3.getValue("url"));
            if(!"".equals(arg3.getValue("iurl")))
                service.setIurl(arg3.getValue("iurl"));
            if(!"".equals(arg3.getValue("surl")))
                service.setSurl(arg3.getValue("surl"));
            service.setTyp(DH.getI().getGuseServiceType(arg3.getValue("type")));
            DH.getI().newService(service);
        }
        else if("communication".equals(arg2)){
            ServiceResourceBean tmp=new ServiceResourceBean();
            tmp.setCom(com);
            tmp.setCaller(arg3.getValue("comtype"));
            tmp.setRes(arg3.getValue("service"));
            tmp.setSrc(DH.getI().getGuseServiceType(arg3.getValue("from")));
            tmp.setDst(DH.getI().getGuseServiceType(arg3.getValue("stype")));
            DH.getI().persistComResource(com, tmp);
        }
        else if("property".equals(arg2)){
            ServicePropertyBean tmp=new ServicePropertyBean();
            tmp.setPropkey(arg3.getValue("name"));
            tmp.setPropvalue(arg3.getValue("value"));
            DH.getI().persistProperty(service, tmp);
        }
    }
}
