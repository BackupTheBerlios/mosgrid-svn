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
 * Storage es WFI kozotti kommunikacio megvalositasa
 */

package hu.sztaki.lpds.storage.net.wsaxis13;

import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.storage.com.FileBean;
import hu.sztaki.lpds.storage.com.IfBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


/**
 * @author krisztian
 */
public class StorageWfiServiceImpl {
/**
 * file masolas a storage-on
 * @param pSRC forras file
 * @param pDest cel file
 * @throws java.lang.Exception masolasi hiba
 */
    public void copyFile(FileBean pSRC,FileBean pDest) throws Exception{
//        System.out.println("********storage copy***********");
    }
/**
 * egy adott konyvtarban levo fileok(tartalmazhat prefixet is) szamanak lekerdezese
 * @param pDirectory konyvtar leiro
 * @return specifikacionak megfelelo fileok szama
 * @throws java.lang.Exception file muveleti hiba
 */
    public long getNumberOfFileInDirectory(FileBean pDirectory) throws Exception{
//        System.out.println("*******storage filenumber********");
        long res=0;
        try{
            File dir=new File(getDirectoryPath(pDirectory));
//            System.out.println("PATH:"+dir.getAbsolutePath());
//            System.out.println("PATH:"+pDirectory.getPrefix());
//            System.out.println("PATH:"+pDirectory.getPrefix().isEmpty());
            if(pDirectory.getPrefix()==null || pDirectory.getPrefix().isEmpty()) res=dir.list().length;
            else if(dir.exists()){
                File remoteGenerator=new File(dir.getAbsolutePath()+"/REMOTEGENERATORS_PID");
//                System.out.println("PATH:"+remoteGenerator.getAbsolutePath());
                if(remoteGenerator.exists()){
                    BufferedReader br=new BufferedReader(new FileReader(remoteGenerator));
                    String ln;
                    String[] data;
                    while((ln=br.readLine())!=null){
                        data=ln.split("#");
                        data[0]=data[0]+"_";
//                        System.out.println("remote generator:"+data[0]+"=?="+pDirectory.getPrefix());
                        if(data[0].equals(pDirectory.getPrefix())) res=Long.parseLong(data[1]);
                    }
                }
//                else
//                    System.out.println("NOT EXIST:"+remoteGenerator.getAbsolutePath());
                String[] filesInDirectory=dir.list();
                for(String f:filesInDirectory)
                    if(f.startsWith(pDirectory.getPrefix())) res++; /*@ToDo pontosabb szures prefixre*/
            }
        }
        catch(Exception e){e.printStackTrace();}
        System.out.println("*******"+res+"********");
        return res;
    }
/**
 * File tartalom vizsgalat
 * @param pValue Vizsgalati leiro
 * @return Vizsgalat eredmenye
 * @throws java.lang.Exception file kezelesi hiba
 */
    public boolean ifTest(IfBean pValue) throws Exception {
        boolean res=false;
        String path=PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"storage/";
        
        String t0="",t1="";
        File srcFile=new File(path+pValue.getSrcpath());
        BufferedReader br=new BufferedReader(new FileReader(srcFile));
        t0=br.readLine();
        br.close();
        if(pValue.getDstpath()!=null){
            File dstFile=new File(path+pValue.getDstpath());
            br=new BufferedReader(new FileReader(dstFile));
            t1=br.readLine();
            br.close();
        }
        else t1=pValue.getDstvalue();
        
        if(pValue.getOperation().equals("1")) res=t1.equals(t0);
        else if(pValue.getOperation().equals("2")) res= !t1.equals(t0);
        else if(pValue.getOperation().equals("3")) res= t0.contains(t1);

        return res;
    }

    private String getDirectoryPath(FileBean pDirectory){
        String path=PropertyLoader.getInstance().getProperty("portal.prefix.dir")+"storage/";
        path=path+pDirectory.getPortalid().replaceAll("/", "_")+"/";
        path=path+pDirectory.getUserid()+"/";
        path=path+pDirectory.getWorkflowname()+"/";
        path=path+pDirectory.getJobname()+"/";
        if(pDirectory.getRuntimeid()==null || "".equals(pDirectory.getRuntimeid()))
            path=path+"inputs/"+pDirectory.getPortnumber();
        else path=path+"outputs/"+pDirectory.getRuntimeid()+"/"+pDirectory.getPid()+"/";

        return path;
    }
}
