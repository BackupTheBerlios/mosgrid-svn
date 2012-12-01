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
 * This file is part of P-GRADE Grid Portal.
 *
 * P-GRADE Grid Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * P-GRADE Grid Portal is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * P-GRADE Grid Portal.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2006-2008 MTA SZTAKI
 *
 */
/*
 */

/*
  * QueryGridInfo.java
  *
  * Created on February 10, 2005, 7:28 PM
  */

package hu.sztaki.lpds.pgportal.services.is.lcg2.ldap;

import hu.sztaki.lpds.pgportal.services.utils.timeoutExecutor.TimeoutExecutor;
import hu.sztaki.lpds.pgportal.services.utils.timeoutExecutor.TimeoutExecutorExtension;
import hu.sztaki.lpds.pgportal.services.utils.timeoutExecutor.Function;
import hu.sztaki.lpds.pgportal.services.utils.MiscUtils;
import hu.sztaki.lpds.pgportal.services.pgrade.GridConfiguration;

import hu.sztaki.lpds.pgportal.services.is.lcg2.*;
import hu.sztaki.lpds.pgportal.services.is.lcg2.resource.*;

import javax.naming.*;
import javax.naming.directory.*;

import java.util.Collections;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;

/**
  *
  * @author  Tamas Boczko
  */
public class QueryGridInfo {
    
    protected final static int OBJECT_TYPE_SITE_INFO = 1;
    protected final static int OBJECT_TYPE_GLUE_CE = 2;
    protected final static int OBJECT_TYPE_GLUE_SE = 3;
    protected final static int OBJECT_TYPE_GLUE_SUB_CLUSTER = 4;  
//HG changed 2008.02.04
    protected final static int OBJECT_TYPE_GLUE_SE_WITH_VO = 5;
    
    private final static  long GIGA_PER_KILO_BYTE = 1048576, 
                               CREDITABILITY_LIM  = (long)50000* 1000000, // Giga byte 
                               CREDITABILITY_LIM2 = CREDITABILITY_LIM * GIGA_PER_KILO_BYTE;
//HG changed end    
    
    /** Creates a new instance of QueryGridInfo */
    public QueryGridInfo() {
    }

    public static boolean getGridInfo(LCGGrid lcgG, GridConfiguration aGC) {
        
        NamingEnumeration results = executeLdapQuery(aGC);
        if (results != null) {
             if ( ! processGridInfo(results, lcgG) ) return false;
             else return true;
        }
        else return false;
         
    }    
   
    private static NamingEnumeration executeLdapQuery(GridConfiguration aGridConfiguration){
        
        long timeout = LCGInformationSystem.getInstance().getLdapConnectionTimeout();
        final GridConfiguration  gridConfiguration = aGridConfiguration;
        TimeoutExecutorExtension te = new TimeoutExecutorExtension();
        Function f = new Function() {
            public Object execute() {
                return getGridInfoFromLdap(gridConfiguration);
            }
        };
        
        Object result = null;
        try {
            result = te.executeFunctionWithTimeout(f, timeout);
        }
        catch (Exception ex) {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".executeLdapQuery("+ aGridConfiguration.getName() + ")","WARNING: Timeout was occured. ( TimeoutException was catched.)"+ "timeout is :"+ timeout );
            return null;
        } 
        
        if (result != TimeoutExecutor.RESULT_AT_TIMEOUT) {
            return (NamingEnumeration)result;
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".executeLdapQuery("+ aGridConfiguration.getName() +")","WARNING: Timeout was occured.");
            return null;
        }        
    }    
 
    private static NamingEnumeration getGridInfoFromLdap(GridConfiguration aGridConfiguration) {
        java.util.Hashtable env = new java.util.Hashtable();
       
        try {
            env.put(Context.PROVIDER_URL, "ldap://" + aGridConfiguration.getISHost() + ":" + aGridConfiguration.getISPort());
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(LCGInformationSystem.getInstance().getLdapConnectionTimeout()) );
//            env.put("java.naming.ldap.version","2");

//            System.out.println("hu.sztaki.lpds.pgportal.services.is.mds2.getMDSResourceList()-InitialDirContext() calling.");
            DirContext ctx = new InitialDirContext(env);
       
              
                    
            // Search recursivly
//            String[] retAttr = new String [] {"siteName", "GlueCEName"};
            SearchControls controls = new SearchControls();
//            controls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
//            controls.setSearchScope(SearchControls.OBJECT_SCOPE);
//            controls.setReturningAttributes(retAttr);

            // Search...
//            String filterExpr = "(objectClass=SiteInfo)";
//            String filterExpr = "(GlueCEUniqueID=*)";
            // HG change 2008.01.31 Start
            // new
            String filterExpr = "(|(siteName=*)(GlueCEUniqueID=*)(GlueSEUniqueID=*)(GlueSAStateUsedSpace=*)(GlueSubClusterName=*))";
            // old
            //  String filterExpr = "(|(siteName=*)(GlueCEUniqueID=*)(GlueSAStateUsedSpace=*)(GlueSubClusterName=*))";
            // HG change 2008.01.31 End
            
//            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".getGridInfoFromLdap("+aGridConfiguration.getName()+")","search() STARTING.");
            NamingEnumeration results = ctx.search(aGridConfiguration.getISBaseDn(), filterExpr, controls);
            
//            Attributes matchAttrs = new BasicAttributes(true); // ignore attribute name case
//            matchAttrs.put(new BasicAttribute("siteName"));
//            NamingEnumeration results = ctx.search(aGridConfiguration.getBdiiBaseDn(), matchAttrs, retAttr);
            
            ctx.close();
 
//            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".getGridInfoFromLdap("+aGridConfiguration.getName()+")","search() FINISHED.");

            return results;
            
            
        }
        catch (javax.naming.CommunicationException e) {
            // MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".getGridInfoFromLdap("+aGridConfiguration.getName()+")","CommunicationException was occured."+e.getMessage());
            // e.printStackTrace();
            return null;
        }
        catch (javax.naming.NamingException e) {
            // MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".getGridInfoFromLdap("+aGridConfiguration.getName()+")","-NamingException was occured: "+e.getMessage());
            // e.printStackTrace();
            return null;
        }
    }    
    
    private static boolean processGridInfo( NamingEnumeration results, LCGGrid aGrid) {
        SearchResult sr;
        Attributes attributes;
        String relativeDn;
        int objectType;
        String siteId;
        


//        MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processGridInfo("+aGrid.getGridName()+")","STARTING.");
// HG change 2005.05.25
// NEW!
// Important change !        
        aGrid.init();
// HG change end
        try {
            while ( results.hasMoreElements()) {
                sr = (SearchResult)results.next();
                attributes = sr.getAttributes();
                relativeDn = LdapUtils.removeQuotes(sr.getName());
 //MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processGridInfo("+aGrid.getGridName()+")","relativeDn=" + relativeDn + "STARTING");                
                
                if (attributes != null) {            
                    siteId = LdapUtils.getSiteIdFromRelativeDn(relativeDn);
                    aGrid.addResource(siteId);

                    objectType = LdapUtils.getObjectType(relativeDn);
                    switch (objectType) {
                        case OBJECT_TYPE_SITE_INFO: processSiteInfo(siteId, aGrid, attributes); break;
                        case OBJECT_TYPE_GLUE_CE: processCE(siteId, aGrid, attributes); break;
                        case OBJECT_TYPE_GLUE_SE: processSE(siteId, aGrid, attributes); break;
//HG changed 2008.02.04 begin
                        case OBJECT_TYPE_GLUE_SE_WITH_VO: processSE_with_VO(siteId, aGrid, attributes); break;
//HG changed end                        
                        case OBJECT_TYPE_GLUE_SUB_CLUSTER: processSubCluster(siteId, aGrid, attributes); break;
                    }
 
                }
//                MiscUtils.printlnLog(QueryResourceList.class.getName() + ".processGridInfo("+aGrid.getGridName()+")","relativeDn=" + relativeDn  + "FINISHED --------------------");
            }
//            aGrid.printData();
//            aGrid.removeNotUsedComponents();
//            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processGridInfo("+aGrid.getGridName()+")","FINISHED.");            
            return true;
        }
        catch (javax.naming.CommunicationException e) {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processGridInfo("+aGrid.getGridName()+")","CommunicationException was occured."+e.getMessage());
            e.printStackTrace();
            return false;
        }
        catch (javax.naming.NamingException e) {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processResourceList("+aGrid.getGridName()+")","-NamingException was occured: "+e.getMessage());
            e.printStackTrace();
            return false;
        }
    } 
    

    private static void processSiteInfo(String siteId, LCGGrid aGrid, Attributes aAttributes) 
        throws CommunicationException, NamingException {
        Attribute a;
        
        if ( (a = aAttributes.get("siteName")) != null  ) {
            aGrid.getResource( siteId ).setSiteName( a.get().toString() );
        }
        else {
           aGrid.getResource( siteId ).setSiteName( siteId );
        }
            
//        MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSiteInfo(" + siteId + ")","siteName:" + a.get().toString());
    }

    private static void processCE(String siteId, LCGGrid aGrid, Attributes attributes) 
        throws CommunicationException, NamingException {
        Attribute a,aa;            
        String lrmsType, ceName, hostName;
//        MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processCE()","siteId:" + siteId);
        
        // process lrmsType
        if ( (a = attributes.get("GlueCEInfoLRMSType")) != null  ) {
            lrmsType = a.get().toString(); 
//            System.out.println("QueryGridInfo.processCE()-GlueCEInfoLRMSType: " + lrmsType);
        }
        else {
//            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processCE(" + siteId+ ")","Error: Missing attributes (GlueCEInfoLRMSType) in ldap.Cannot process this CE.");
            return;
        }
        // process CEName
        if ( (a = attributes.get("GlueCEName")) != null  ) {
            ceName = a.get().toString(); 
//            System.out.println("QueryGridInfo.processCE()-GlueCEName: " + ceName);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processCE(" + siteId+ ")","Error: Missing attributes (GlueCEName) in ldap.Cannot process this CE.");
            return;
        }
        
        if ( (a = attributes.get("GlueCEInfoHostName")) != null  ) {
            hostName = a.get().toString(); 
//            System.out.println("QueryGridInfo.processCE()-GlueCEInfoHostName: " + hostName);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processCE(" + siteId+ ")","Error: Missing attributes (GlueCEInfoHostName) in ldap.Cannot process this CE.");
            return;
        }
        
        LCGResource lcgR = aGrid.getResource(siteId);
        LCGComputingElement ce = new LCGComputingElement(hostName, ceName , lrmsType);
//        lcgR.addCE(hostName, ceName, lrmsType);
//        LCGComputingElement ce = lcgR.getCE(hostName, ceName, lrmsType );
        
        if ( (a = attributes.get("GlueCEInfoLRMSVersion")) != null  ) {
            ce.setLrmsVersion( a.get().toString()) ; 
//            System.out.println("QueryResourceList.processCE()-GlueCEInfoLRMSVersion: " + lrmsVersion);
        }
        if ( (a = attributes.get("GlueCEInfoTotalCPUs")) != null  ) {
            try {
                ce.setTotalCpu( Integer.parseInt( a.get().toString()) ) ; 
            }
            catch (NumberFormatException e) { e.printStackTrace();}
//            System.out.println("QueryResourceList.processCE()-GlueCEInfoTotalCPUs: " + totalCpu);
        }
        if ( (a = attributes.get("GlueCEStateFreeCPUs")) != null  ) {
            try {
                ce.setFreeCpu( Integer.parseInt( a.get().toString()) ); 
            }
            catch (NumberFormatException e) { e.printStackTrace();}
//            System.out.println("QueryResourceList.processCE()-GlueCEStateFreeCPUs: " + freeCpu);
        }
        if ( (a = attributes.get("GlueCEStateRunningJobs")) != null  ) {
            try {
                ce.setRunningJob( Integer.parseInt( a.get().toString()) ); 
            }
            catch (NumberFormatException e) { e.printStackTrace();}
//            System.out.println("QueryResourceList.processCE()-GlueCEStateRunningJobs: " + runningJob);
        }
        if ( (a = attributes.get("GlueCEStateWaitingJobs")) != null  ) {
            try {
                ce.setWaitingJob( Integer.parseInt( a.get().toString()) ); 
            }
            catch (NumberFormatException e) { e.printStackTrace();}
//            System.out.println("QueryResourceList.processCE()-GlueCEStateWaitingJobs: " + waitingJob);
        }
        
        // process voNames...
        if ( (a = attributes.get("GlueCEAccessControlBaseRule")) != null ){
            NamingEnumeration ne;
            String voName;
            for ( ne = a.getAll(); ne.hasMore();  ) {
                voName = (String)ne.next();
                if (voName.startsWith("VO:")) {
                    voName = voName.substring(3);
                }
                aGrid.addVo(voName, siteId);
//                System.out.println("QueryGridInfo.processCE("+lcgR.getName()+")- Adding VO: " + voName);
                ce.addVO(voName);
//                System.out.println("QueryGridInfo.processCE()-voName[]:" + voName);
            }
        }
        lcgR.addCE(ce);
//        lcgR.updateSumsForCE(ce);
        
    }
    
    private static void processSE(String siteId, LCGGrid aGrid, Attributes aAttributes) 
        throws CommunicationException, NamingException {
        Attribute a,aa;             
        String seUId = "";
        String seVO = "";
        boolean tapeFlag=false;
// MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE()","siteId:" + siteId);
        
        if ( (a = aAttributes.get("GlueSEUniqueID")) != null  ) {
            seUId = a.get().toString();

// MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE()","siteId:" + siteId + " SE:"+seUId); 
                
       
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE(" + siteId+ ")","Error: Missing attributes (GlueSEUniqueID) in ldap.Cannot process this SE.");
            return;
        }

        seVO="--";
        LCGResource lcgR = aGrid.getResource(siteId);
        LCGStorageElement se = new LCGStorageElement( seUId, seVO);
//        System.out.println("QueryGridInfo.processSE("+lcgR.getName()+")- Adding VO: " + seVO);
        aGrid.addVo(seVO, siteId);
   

        if ( (a = aAttributes.get("GlueSEArchitecture")) != null  ) {
           String kind = a.get().toString().trim();
           se.setKind(kind);
// System.out.println("QueryResourceList.processSE()-Architecture : " + kind );
        }
        int seState =0;
        Long totalSpace=(long)0,availableSpace=(long)0;
        if ( (a = aAttributes.get("GlueSESizeTotal")) != null  ) {
            try {
                totalSpace = Long.parseLong( a.get().toString() );
                if ( totalSpace < CREDITABILITY_LIM)
                {    
                     totalSpace = totalSpace  * GIGA_PER_KILO_BYTE;
                     seState++;
                     
                }     
                else {se.setState(LCGStorageElement.STATE_UNRELIABLE); }
                
            }
            catch (NumberFormatException e) { e.printStackTrace();}
        }        
        
        if ( (a = aAttributes.get("GlueSESizeFree")) != null  ) {
            try {
                
                availableSpace = Long.parseLong( a.get().toString() );
                if ( availableSpace < CREDITABILITY_LIM)
                {    
                     availableSpace = availableSpace  * GIGA_PER_KILO_BYTE;
                     seState++;
                }     
                else { se.setState(LCGStorageElement.STATE_UNRELIABLE); };
                
            }
            catch (NumberFormatException e) { e.printStackTrace();}
         } 
        if (seState == 2) se.setTotalAndAvalable(totalSpace,availableSpace);
// System.out.println("QueryResourceList.processSE()-sE state=" +seState + " totalSpace="+ totalSpace+ " avalialbeSpace=" + availableSpace  );          
        lcgR.addSE(se);

    }
//HG changed 2008.02.04 Begin
 private static void processSE_with_VO(String siteId, LCGGrid aGrid, Attributes aAttributes) 
        throws CommunicationException, NamingException {
        Attribute a,aa;             
        String seUId = "";
        String seVO = "";
//        MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE_with_VO()","siteId:" + siteId);
        
        if ( (a = aAttributes.get("GlueChunkKey")) != null  ) {
            seUId = a.get().toString();
            if (seUId.startsWith("GlueSEUniqueID=")) {
                seUId = seUId.substring(15);
//HG temp 
//            System.out.println("QueryGridInfo.processSE_with_VO()-GlueSEUniqueID: " + seUId);                
            }
//HG temp             
//            System.out.println("QueryGridInfo.processSE_wit_VO()-GlueChunkKey: " + seUId);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE_with_VO(" + siteId+ ")","Error: Missing attributes (GlueChunkKey) in ldap.Cannot process this SE.");
            return;
        }

        if ( (a = aAttributes.get("GlueSAAccessControlBaseRule")) != null  ) {
            seVO = a.get().toString(); 
//            System.out.println("QueryGridInfo.processSE()-GlueSEAccessControlBaseRule: " + seVO);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE_with_VO(" + siteId+ ")","Error: Missing attributes (GlueSAAccessControlBaseRule) in ldap.Cannot process this SE.");
            return;
        }
        
        LCGResource lcgR = aGrid.getResource(siteId);
        LCGStorageElement se = new LCGStorageElement( seUId, seVO);
//        System.out.println("QueryGridInfo.processSE_with_VO("+lcgR.getName()+")- Adding VO: " + seVO);
        aGrid.addVo(seVO, siteId);
//        lcgR.addSE(seUId, seVO);
//        LCGStorageElement se = lcgR.getSE(seUId, seVO);        
//-----------------------------------------------------------------------------   
        int seState =0;
        Long usedSpace=(long)0,availableSpace=(long)0;
        int schemaVersionMinor=2;
        if ( (a = aAttributes.get("GlueSchemaVersionMinor")) != null  ) {
            try {
                
                schemaVersionMinor = Integer.parseInt( a.get().toString() );
  
            }
            catch (NumberFormatException e) { e.printStackTrace();}
         } 
        
        if ( (a = aAttributes.get("GlueSAStateAvailableSpace")) != null  ) {
            try {
                
                availableSpace = Long.parseLong( a.get().toString() );
                /*
                if(schemaVersionMinor == 3 )
                {
                    // In this version the usage is defined in Bytes 
                    availableSpace = availableSpace >> 10;
                }
                */
                if ( availableSpace < CREDITABILITY_LIM2)
                {    
                     seState++;
                }     
                else { se.setState(LCGStorageElement.STATE_UNRELIABLE); };
                
            }
            catch (NumberFormatException e) { e.printStackTrace();}
         } 
        if ( (a = aAttributes.get("GlueSAStateUsedSpace")) != null  ) {
            try {
                
                usedSpace = Long.parseLong( a.get().toString() );
                /*
                if(schemaVersionMinor == 3 )
                {
                    // In this version the usage is defined in Bytes 
                    usedSpace = usedSpace >> 10;
                }    
                */
                if ( usedSpace < CREDITABILITY_LIM2)
                {    
                     seState++;
                }     
                else { se.setState(LCGStorageElement.STATE_UNRELIABLE); };
                
            }
            catch (NumberFormatException e) { e.printStackTrace();}
         } 
        if (seState == 2) se.setAvalableAndUsed(availableSpace,usedSpace);
// System.out.println("QueryResourceList.processSE_with_VO() seVO= "+ seVO + " seState=" +seState + " usedSpace="+ usedSpace+ " avalialbeSpace=" + availableSpace  );          
        lcgR.addSE(se);
  

    }
    

 /*
    private static void processSE_old(String siteId, LCGGrid aGrid, Attributes aAttributes) 
        throws CommunicationException, NamingException {
        Attribute a,aa;             
        String seUId = "";
        String seVO = "";
//        MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE()","siteId:" + siteId);
        
        if ( (a = aAttributes.get("GlueChunkKey")) != null  ) {
            seUId = a.get().toString();
            if (seUId.startsWith("GlueSEUniqueID=")) {
                seUId = seUId.substring(15);
//HG temp 
//            System.out.println("QueryGridInfo.processSE()-GlueSEUniqueID: " + seUId);                
            }
//HG temp             
//            System.out.println("QueryGridInfo.processSE()-GlueChunkKey: " + seUId);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE(" + siteId+ ")","Error: Missing attributes (GlueChunkKey) in ldap.Cannot process this SE.");
            return;
        }

        if ( (a = aAttributes.get("GlueSAAccessControlBaseRule")) != null  ) {
            seVO = a.get().toString(); 
//            System.out.println("QueryGridInfo.processSE()-GlueSEAccessControlBaseRule: " + seVO);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSE(" + siteId+ ")","Error: Missing attributes (GlueSAAccessControlBaseRule) in ldap.Cannot process this SE.");
            return;
        }
        
        LCGResource lcgR = aGrid.getResource(siteId);
        LCGStorageElement se = new LCGStorageElement( seUId, seVO);
//        System.out.println("QueryGridInfo.processSE("+lcgR.getName()+")- Adding VO: " + seVO);
        aGrid.addVo(seVO, siteId);
//        lcgR.addSE(seUId, seVO);
//        LCGStorageElement se = lcgR.getSE(seUId, seVO);        
        
        if ( (a = aAttributes.get("GlueSAStateAvailableSpace")) != null  ) {
            try {
                se.setAvailableSpace( Long.parseLong( a.get().toString() ) );
            }
            catch (NumberFormatException e) { e.printStackTrace();}
//            System.out.println("QueryResourceList.processSE()-GlueSAStateAvailableSpace: " + a.get().toString() );
        }        
        
        if ( (a = aAttributes.get("GlueSAStateUsedSpace")) != null  ) {
            try {
                se.setUsedSpace( Long.parseLong( a.get().toString() ) );
            }
            catch (NumberFormatException e) { e.printStackTrace();}
//            System.out.println("QueryResourceList.processSE()-GlueSAStateUsedSpace: " + a.get().toString());
        } 
// HG changed 2008.01.30 Begin        
// delete        
//        se.setTotalSpace();
// HG change end        
        lcgR.addSE(se);
//        lcgR.updateSumsForSE(se);
    }
*/   
 //HG changed end  
    private static void processSubCluster(String siteId, LCGGrid aGrid, Attributes aAttributes) 
        throws CommunicationException, NamingException {
            
//        MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSubCluster()","siteId:" + siteId);
        Attribute a,aa;             
        String subClusterName = "";
        
        if ( (a = aAttributes.get("GlueSubClusterName")) != null  ) {
            subClusterName = a.get().toString(); 
//            System.out.println("QueryGridInfo.processSubCluster()-GlueSubClusterName: " + subClusterName);
        }
        else {
            MiscUtils.printlnLog(QueryGridInfo.class.getName() + ".processSubCluster(" + siteId+ ")","Error: Missing attributes (GlueSubClusterName) in ldap.Cannot process this Sub Cluster.");
            return;
        }        
        
        LCGResource lcgR = aGrid.getResource(siteId);
        lcgR.addSubCluster(subClusterName);
        LCGSubCluster subCluster = lcgR.getSubCluster(subClusterName);        
  
        if ( (a = aAttributes.get("GlueHostOperatingSystemName")) != null  ) {
            subCluster.setOsName( a.get().toString() );
//            System.out.println("QueryResourceList.processSubCluster()-osName: " + a.get().toString());
        }

        if ( (a = aAttributes.get("GlueHostOperatingSystemRelease")) != null  ) {
            subCluster.setOsRelease( a.get().toString() ); 
//            System.out.println("QueryResourceList.processSubCluster()-osRelease: " + a.get().toString());
        }
        
        if ( (a = aAttributes.get("GlueHostProcessorModel")) != null  ) {
            subCluster.setCpuModel( a.get().toString() ); 
//            System.out.println("QueryResourceList.processSubCluster()-cpuModel: " + a.get().toString());
        }

        if ( (a = aAttributes.get("GlueHostProcessorClockSpeed")) != null  ) {
            subCluster.setCpuSpeed( a.get().toString() ); 
//            System.out.println("QueryResourceList.processSubCluster()-cpuSpeed: " + a.get().toString());
        }
        
        if ( (a = aAttributes.get("GlueHostMainMemoryRAMSize")) != null  ) {
            subCluster.setRamSize( a.get().toString() ); 
//            System.out.println("QueryResourceList.processSubCluster()-ramSize: " + a.get().toString());
        }

        if ( (a = aAttributes.get("GlueHostMainMemoryVirtualSize")) != null  ) {
            subCluster.setVirtualSize( a.get().toString() ); 
//            System.out.println("QueryResourceList.processSubCluster()-virtualSize: " + a.get().toString());
        }
        
        if ( (a = aAttributes.get("GlueHostApplicationSoftwareRunTimeEnvironment")) != null ){
            NamingEnumeration ne;
//            String sE;
            for ( ne = a.getAll(); ne.hasMore();  ) {
//                sRTE = (String)ne.next();
                subCluster.addSoftwareEnv( (String)ne.next() );
//                System.out.println("QueryResourceList.processSubCluster()-softwareEnvironment[]:" + sRTE);
            }
        }
        
        
    }
   
    
    
}
