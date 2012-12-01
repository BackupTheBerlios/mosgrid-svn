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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.wfi.zen.pools;

import hu.sztaki.lpds.wfi.service.zen.xml.objects.Input;
import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author krisztian karoczkai
 */
public class JobInstanceXMLParser extends DefaultHandler
{
    private String inputName="";
    private long pid=0;
    private boolean succes=false;
    
    private Vector<InputSuccesBean> pdata=new Vector<InputSuccesBean>(); 

    public JobInstanceXMLParser(){} 
    public JobInstanceXMLParser(String pInputName, long pPID,boolean pSucces) 
    {
        inputName=pInputName;
        pid=pPID;
        succes=pSucces;
    }
    
    
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException 
    {
        if("input".equals(arg2))
        {
            pdata.add(new InputSuccesBean(arg3.getValue("name"), arg3.getValue("pid"), arg3.getValue("succes")));
        }
    }
    
    public Vector<InputSuccesBean> getAllData(){return pdata;}
    
    public String getNewXML()
    {
        boolean exist=false;
        InputSuccesBean tmpdata=new InputSuccesBean();
        for(int i=0;i<pdata.size();i++)
        {
            tmpdata=pdata.get(i);
            if((tmpdata.getName().equals(inputName))&&(tmpdata.getPid()==pid))
            {
                tmpdata.setSucces(succes);
                exist=true;
            }
        }
        if(!exist) pdata.add(new InputSuccesBean(inputName, pid, succes));
        StringBuffer res=new StringBuffer("<b>\n");
        for(int i=0;i<pdata.size();i++) res.append(pdata.get(i).toString());
        res.append("</b>");
        return res.toString();
    }
}
