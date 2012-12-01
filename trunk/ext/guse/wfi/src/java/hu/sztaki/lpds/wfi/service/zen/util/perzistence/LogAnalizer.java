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

package hu.sztaki.lpds.wfi.service.zen.util.perzistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author krisztian karoczkai
 */
public class LogAnalizer 
{
    public static void main(String[] args)
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try
        {
            SAXParser saxParser = factory.newSAXParser();
//            File f=new File("/home/krisztian/lpds/r31/pgportal/tomcat/temp/guselogg/zen/__");
            File f=new File("/home/krisztian/lpds/logg");
                    getMaxUsedMemory(saxParser, f.getPath());
                    objectMonitor(saxParser, f.getPath());
//                    noDeleting(saxParser, f.getPath());
                    memoryMonitor(saxParser, f.getPath());
                    allMemoryMonitor(saxParser, f.getPath());
/*            String[] s=f.list();
            System.out.println(s);
            for(String t:s)
            {
                if(t.endsWith("5000x10.logg"))
                {
                    System.out.println(f.getPath()+"/"+t);
//                    objectMonitor(saxParser, f.getPath()+"/"+t);
//                    noDeleting(saxParser, f.getPath()+"/"+t);
                    getMaxUsedMemory(saxParser, f.getPath()+"/"+t);
//                    memoryMonitor(saxParser, f.getPath()+"/"+t);
//                    allMemoryMonitor(saxParser, f.getPath()+"/"+t);
                }
            }
 */
        }
        catch(Exception e){e.printStackTrace();}
    }
    
    private static void objectMonitor(SAXParser pValue, String pPath) throws Exception
    {
            ObjectMonitor handler=new ObjectMonitor("/home/krisztian/out01.html");
            pValue.parse(pPath, handler);
            handler.writeMax();
    }
    
    private static void allMemoryMonitor(SAXParser pValue, String pPath) throws Exception
    {
            AllMemoryMonitor handler=new AllMemoryMonitor(pPath+"allmemory.html");
            pValue.parse(pPath, handler);
    }
    
    private static void memoryMonitor(SAXParser pValue, String pPath) throws Exception
    {
            MemoryMonitor handler=new MemoryMonitor("/home/krisztian/m01.html");
            pValue.parse(pPath, handler);
//            handler.writeMax();
    }
    
   private static void noDeleting(SAXParser pValue, String pPath) throws Exception
   {
            XMLHandler handler=new XMLHandler();
            pValue.parse(pPath, handler);
            Enumeration<String> enm=handler.getData().keys();
            while(enm.hasMoreElements())
            System.out.println("NO DELETE:"+enm.nextElement());
            System.out.println("all:"+handler.getData().size());
   }
   
   private static void getMaxUsedMemory(SAXParser pValue, String pPath) throws Exception
   {
        MaxMemory handler=new MaxMemory();
        pValue.parse(pPath, handler);
        System.out.println("MAX use memory:"+handler.getMaxUseMemory());
   }
}


class XMLHandler extends DefaultHandler
{
    private Hashtable<String,Boolean> datas=new Hashtable<String, Boolean>();
    
    
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException 
    {
        if(arg2.equals("newrunnablejob"))
            datas.put(arg3.getValue("job")+"."+arg3.getValue("pid"), new Boolean(true));
        if(arg2.equals("cleaning-delete")||arg2.equals("cleaning-deletegen"))
        {
//            System.out.println(arg3.getValue("name")+"."+arg3.getValue("pid"));
            datas.remove(arg3.getValue("job")+"."+arg3.getValue("pid"));
            datas.remove(arg3.getValue("name")+"."+arg3.getValue("pid"));
        }
        
    }
    
    
    public Hashtable<String,Boolean> getData(){return datas;}
}


class ObjectMonitor extends DefaultHandler
{
    private FileWriter fw=null;
    private long max=0,tmp=0;
    public ObjectMonitor(String p) 
    {
        try{fw=new FileWriter(p);}
        catch(IOException e){}
    }
    
    private Hashtable<String,Boolean> datas=new Hashtable<String, Boolean>();
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException 
    {

        if(arg2.equals("newrunnablejob")||arg2.equals("cleaning-delete"))
        {
            try 
            {
                tmp=Long.parseLong(arg3.getValue("poolsize"));
                if(tmp>max) max=tmp;
                fw.write("<div style=\"display:block;float:left;width:1px;min-height:" + arg3.getValue("poolsize") + "px;background-color:#FF0000;\">&nbsp;</div>\n");
                fw.flush();
            } 
            catch (IOException ex) {Logger.getLogger(ObjectMonitor.class.getName()).log(Level.SEVERE, null, ex);}
        }
    }
    
    public void writeMax()
    {
            try 
            {
                fw.write("<br /><font size=\"30\">"+max+"</font>\n");
                fw.flush();
            } 
            catch (IOException ex) {Logger.getLogger(ObjectMonitor.class.getName()).log(Level.SEVERE, null, ex);}
    }
}


class MemoryMonitor extends DefaultHandler
{
    private FileWriter fw=null;
    private long max=0,tmp=0;
    public MemoryMonitor(String p) 
    {
        try{fw=new FileWriter(p);}
        catch(IOException e){}
    }
    
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException 
    {

        if(arg3.getValue("memory")!=null)
        {
            tmp=Long.parseLong(arg3.getValue("memory"));
            tmp=tmp/102400;
            if(tmp>max) max=tmp;
            try 
            {
                fw.write("<div style=\"display:block;float:left;width:1px;min-height:" + tmp + "px;background-color:#FF0000;\">&nbsp;</div>\n");
                fw.flush();
            } 
            catch (IOException ex) {Logger.getLogger(ObjectMonitor.class.getName()).log(Level.SEVERE, null, ex);}
        }
    }
    public long getMaxUseMemory(){return max;}

}

class MaxMemory extends DefaultHandler
{
    private long max=0,tmp=0;
    
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException 
    {
        if(arg3.getValue("memory")!=null)
        {
            tmp=Long.parseLong(arg3.getValue("memory"));
            if(tmp>max) max=tmp;
        }
    }
    
    public long getMaxUseMemory(){return max;}
}


class AllMemoryMonitor extends DefaultHandler
{
    private FileWriter fw=null;
    private long instances=0,outpool=0,usememory=0,usememorytmp=0;
    private long maxinstances=0,maxoutpool=0,maxusememory=0;
    public AllMemoryMonitor(String p) 
    {
        try
        {
            fw=new FileWriter(p);
            fw.write("<table border=\"1\">\n");
            fw.write("\t<tr><td>Event</td><td>Instances</td><td>Out Pool</td><td>Use Memory(byte)</td><td>Diferent Memory(byte)</td></tr>\n");
            fw.flush();
        }
        catch(IOException e){}
    }
    
    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException 
    {

        if(arg3.getValue("memory")!=null)
        {
            if(arg3.getValue("poolsize")!=null) 
                instances=Long.parseLong(arg3.getValue("poolsize"));
            if(arg3.getValue("all")!=null) outpool=Long.parseLong(arg3.getValue("all"));
    //        if(arg2.equals("submit")) outpool--;
            usememorytmp=Long.parseLong(arg3.getValue("memory"));
            try
            {

                fw.write("\t<tr>\n");
                fw.write("\t\t<td>"+arg2+"("+arg3.getValue("job")+"."+arg3.getValue("pid")+")</td>\n");//event
                if(instances>maxinstances)
                {
                    fw.write("\t\t<td style=\"background-color:#ff0000\">"+instances+"</td>\n");//managelt peldanyok
                    maxinstances=instances;
                }
                else
                    fw.write("\t\t<td>"+instances+"</td>\n");//managelt peldanyok
                if(outpool>maxoutpool)
                {
                    fw.write("\t\t<td style=\"background-color:#ff0000\">"+outpool+"</td>\n");//kimeneti pool
                    maxoutpool=outpool;
                }
                else
                    fw.write("\t\t<td>"+outpool+"</td>\n");//kimeneti pool
                if(usememorytmp>maxusememory)
                {
                    fw.write("\t\t<td style=\"background-color:#ff0000\">"+usememorytmp+"</td>\n");//aktualisan hasznalt memoria
                    maxusememory=usememorytmp;
                }
                else
                    fw.write("\t\t<td>"+usememorytmp+"</td>\n");//aktualisan hasznalt memoria
                if(usememorytmp>usememory)
                    fw.write("\t\t<td style=\"background-color:#ff0000\">"+(usememorytmp-usememory)+"</td>\n");//hasznalt memoriavaltozas
                else if(usememorytmp<usememory)
                    fw.write("\t\t<td style=\"background-color:#00ff00\">"+(usememorytmp-usememory)+"</td>\n");//hasznalt memoriavaltozas
                else
                    fw.write("\t\t<td>"+(usememorytmp-usememory)+"</td>\n");//hasznalt memoriavaltozas
                usememory=usememorytmp;
                fw.write("\t</tr>\n");
                fw.flush();
            }
            catch(IOException e){}
/*            tmp=Long.parseLong(arg3.getValue("memory"));
            tmp=tmp/102400;
            if(tmp>max) max=tmp;
            try 
            {
                fw.write("<div style=\"display:block;float:left;width:1px;min-height:" + tmp + "px;background-color:#FF0000;\">&nbsp;</div>\n");
                fw.flush();
            } 
            catch (IOException ex) {Logger.getLogger(ObjectMonitor.class.getName()).log(Level.SEVERE, null, ex);}
*/
        }

    }

    @Override
    protected void finalize() throws Throwable 
    {
            fw.write("</table>\n");
            fw.close();
    }
    
}
