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
 * MessageBean.java
 * portal text leiro
 */

package hu.sztaki.lpds.pgportal.portlets.admin;

import java.util.Hashtable;

/**
 * Egy portal feluleten megjeleno kulcs text ertek
 * parost leiro bean osztaly.
 *
 * @author krisztian karoczkai
 */
public class MessageBean {
    
    Hashtable datas=new Hashtable();
    
    /** Creates a new instance of MessageBean */
    public MessageBean() {
    }
    
    /**
     * Letrehoz egy message-bean osztalyt.
     *
     * @param pTkey - a szoveg kulcsa (pl: button.exit)
     * @param pDesc - magyarazo szoveg, csoportositas celjabol
     * @param pTxt - a feluleten tenylegesen megjeleno szoveg (pl: Exit)
     */
    public MessageBean(String pTkey,String pDesc, String pTxt) {
        setTkey(pTkey);
        setTxt(pTxt);
        setDesc(pDesc);
    }
    
    /**
     * Beallitja a message kulcsat
     *
     * @param pvalue - a szoveg kulcsa (pl: button.exit)
     */
    public void setTkey(String pvalue){datas.put("tkey",pvalue);}
    
    /**
     * Beallitja a kiirando, megjelenitendo szoveget
     *
     * @param pvalue - a feluleten tenylegesen megjeleno szoveg (pl: Exit)
     */
    public void setTxt(String pvalue){datas.put("txt",pvalue);}
    
    /**
     * Beallitja a message leirojat
     *
     * @param pvalue - magyarazo szoveg, csoportositas celjabol
     */
    public void setDesc(String pvalue){datas.put("desc",pvalue);}
    
    /**
     * Visszaadja a message kulcsat
     *
     * @return String - a szoveg kulcsa (pl: button.exit)
     */
    public String getTkey(){return (String)datas.get("tkey");}
    
    /**
     * Visszaadja a kiirando, megjelenitendo szoveget
     *
     * @return String - a feluleten tenylegesen megjeleno szoveg (pl: Exit)
     */
    public String getTxt(){return (String)datas.get("txt");}
    
    /**
     * Visszaadja a message leirojat
     *
     * @return String - magyarazo szoveg, csoportositas celjabol
     */
    public String getDesc(){return (String)datas.get("desc");}
    
}
