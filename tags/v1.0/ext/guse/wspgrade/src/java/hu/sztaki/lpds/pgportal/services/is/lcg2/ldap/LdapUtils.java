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

package hu.sztaki.lpds.pgportal.services.is.lcg2.ldap;

import java.util.regex.*;

import hu.sztaki.lpds.pgportal.services.utils.MiscUtils;

/**
  *
  * @author  Tamas Boczko
  */
public class LdapUtils {
    
    /** Creates a new instance of LdapUtils */
    public LdapUtils() {
    }

    public static String removeQuotes(String input) {
        Pattern pattern = Pattern.compile("^\"?(.*?)\"?$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) return matcher.group(1);
        else return input;
    }
    
    
    /** Get the site identifier from the relative DN (objectName).
     * The relative DN ends with {...,mds-vo-name=siteId}
     *
     */
    protected static String getSiteIdFromRelativeDn(String relativeDn){
        String siteId = "";
        relativeDn = relativeDn.toLowerCase();
        Pattern pattern = Pattern.compile("^.*mds\\-vo\\-name\\=(([a-zA-Z0-9._-])*)");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   siteId = matcher.group(1);     }
//        System.out.println("LdapUtils.getSiteIdFromRelativeDn("+relativeDn+")-siteId:" + siteId );     
        return siteId;
    }    
 
    protected static int getObjectType(String relativeDn){
        if (isObjectTypeSiteInfo(relativeDn))      return QueryGridInfo.OBJECT_TYPE_SITE_INFO;
        else if (isObjectTypeGlueCE(relativeDn)) return QueryGridInfo.OBJECT_TYPE_GLUE_CE;
//HG changed 2008.02.04  begin  
        else if (isObjectTypeGlueSE_with_VO(relativeDn)) return QueryGridInfo.OBJECT_TYPE_GLUE_SE_WITH_VO;
//HG changed end        
        else if (isObjectTypeGlueSE(relativeDn)) return QueryGridInfo.OBJECT_TYPE_GLUE_SE;
        else if (isObjectTypeGlueSubCluster(relativeDn)) return QueryGridInfo.OBJECT_TYPE_GLUE_SUB_CLUSTER;
        return 0;
    }
 
    
    private static boolean isObjectTypeSiteInfo(String relativeDn) {
        Pattern pattern = Pattern.compile("^siteName=.*$");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        
        pattern = Pattern.compile("^in=.*$");
        matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        
        return false;
    }
    
    private static boolean isObjectTypeGlueCE(String relativeDn) {
        Pattern pattern = Pattern.compile("^GlueCEUniqueID=.*$");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        return false;
    }
    
//HG change 2008.02.04     
    private static boolean isObjectTypeGlueSE_with_VO(String relativeDn) {
        Pattern pattern = Pattern.compile("^GlueSALocalID=.*GlueSEUniqueID=.*$");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        return false;        
    }
//HG change end    
//HG change 2008.01.30     
    private static boolean isObjectTypeGlueSE(String relativeDn) {
        Pattern pattern = Pattern.compile("^GlueSEUniqueID=.*$");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        return false;        
    }
    //old
/*    
  private static boolean isObjectTypeGlueSE_old(String relativeDn) {
        Pattern pattern = Pattern.compile("GlueSEUniqueID=.*$");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        return false;        
    }
 */ 
 //HG change end 
    private static boolean isObjectTypeGlueSubCluster(String relativeDn) {
        Pattern pattern = Pattern.compile("^GlueSubClusterUniqueID=.*$");
        Matcher matcher = pattern.matcher(relativeDn);
        if (matcher.find()) {   return true;     }
        return false;        

    }
    /*
    public static boolean isBDIIHostValid(String host){
        if ( host == null ) return false;
        if (host.equals("")) return false;
        return true;
    }
    
    public static boolean isBDIIPortValid(String port){
        if ( port == null ) return false;
        
        Pattern pattern = Pattern.compile("^(\\d){4}$");
        Matcher matcher = pattern.matcher(port);
        if (! matcher.find())  return false;
        
        return true;
    }
    
    public static boolean isBDIIBaseDnValid(String baseDn){
        if ( baseDn == null ) return false;
        
        Pattern pattern = Pattern.compile("^(mds\\-vo\\-name\\=[a-z]+\\,o\\=grid)$");
        Matcher matcher = pattern.matcher(baseDn.toLowerCase());
        if (! matcher.find())  return false;
        return true;
    }    
    */
}
