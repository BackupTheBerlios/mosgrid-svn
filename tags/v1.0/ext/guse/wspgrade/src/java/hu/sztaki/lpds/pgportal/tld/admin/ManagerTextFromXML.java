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

package hu.sztaki.lpds.pgportal.tld.admin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

public class ManagerTextFromXML 
{
    public static void main(String[] args)
    {
        File dir=new File("/home/krisztian/lpds/jsp/");
        String[] files=dir.list();
        for(int i=0;i<files.length;i++)
        {
            if(!files[i].equals("."))
            if(!files[i].equals(".."))
            {
//file beolvasas
                StringBuffer jspString=new StringBuffer("");
                try
                {
                    BufferedReader br=new BufferedReader(new FileReader("/home/krisztian/lpds/jsp/"+files[i]));
                    String s="";
                    while((s=br.readLine())!=null)
                    {
                        jspString.append(s+"\n");
                    }
                    br.close();
                }
                catch(IOException e){}
//szoveg cserek                
                String t=jspString.toString();
                Vector tmp=get(files[i]);
                MessageDataBean ttmp;
                for(int ii=0;ii<tmp.size();ii++)
                {
                    ttmp=(MessageDataBean)tmp.get(ii);
                   
                    t=t.replace(ttmp.getText(),"<msg:getText key=\""+ttmp.getKey()+"\" />");
                }
//file iras
                try
                {
                    File ff=new File("/home/krisztian/lpds/njsp/"+files[i]);
                    ff.createNewFile();
                    FileWriter fw=new FileWriter(ff);
                    fw.write(t);
                    fw.close();
                }
                catch(IOException e){e.printStackTrace();}
            }
        }
    }

    private static Vector get(String pValue)
    {
        MessageXMLParser handler = new MessageXMLParser();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try 
        {
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse( new File("/home/krisztian/messages.xml"), handler);
//            System.out.println("--"+pValue.substring(0,pValue.length()-4));
            return handler.getDatas(pValue.substring(0,pValue.length()-4));
        } 
        catch (Throwable t) {System.out.println("Hib�s message.xml\n");t.printStackTrace();}
        return new Vector();
    }
}



class MessageXMLParser extends DefaultHandler
{
    Vector datas=new Vector();
    
    @Override
    public void startElement(String namespaceURI,String lName,String qName,Attributes attrs) throws SAXException
    {
        try
        {
            if(qName.equals("text"))
            datas.add(new MessageDataBean(attrs.getValue("value"),attrs.getValue("key"),attrs.getValue("jsp"),attrs.getValue("desc")));
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    public Vector getDatas(String pValue)
    {
        Vector res=new Vector();
        MessageDataBean tmp;
        for(int i=0;i<datas.size();i++)
        {
            tmp=(MessageDataBean)datas.get(i);
            if(tmp.getJsp().equals("*")||tmp.getJsp().equals(pValue)) res.add(tmp);
        }
        
        return res;
    }
    
}


class MessageDataBean
{
    Hashtable datas=new Hashtable();
    public MessageDataBean(){}
    public MessageDataBean(String p0,String p1,String p2,String p3)
    {
        setText(p0);
        setKey(p1);
        setJsp(p2);
        setDesc(p3);
    }
    
    public void setText(String pValue){datas.put("text",pValue);}
    public void setKey(String pValue){datas.put("key",pValue);}
    public void setJsp(String pValue){datas.put("jsp",pValue);}
    public void setDesc(String pValue){datas.put("desc",pValue);}
    
    public String getText(){return (String)datas.get("text");}
    public String getKey(){return (String)datas.get("key");}
    public String getJsp(){return (String)datas.get("jsp");}
    public String getDesc(){return (String)datas.get("desc");}
    
}
