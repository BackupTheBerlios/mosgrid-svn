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
 * JobPropertyBean.java
 * Job tulajdonsag leiro Bean
 */

package hu.sztaki.lpds.wfs.com;

import java.util.HashMap;
import java.util.Vector;

/**
 * @author krisztian
 */
public class JobPropertyBean 
{
    private String name="";
    private String txt="";
    private long id;
    private long x;
    private long y;
    private HashMap exe=new HashMap();
    private HashMap exeDisabled=new HashMap();
    private Vector outputs=new Vector();
    private Vector inputs=new Vector();
    private HashMap desc=new HashMap();
    private Vector history= new Vector();
    private HashMap desc0=new HashMap(); 
    private HashMap label=new HashMap(); 
    private HashMap inh=new HashMap(); 
    
    
/** 
 * Class constructor 
 */   
    public JobPropertyBean() {}
    
/** 
 * A job binaris tulajdonsagok lekerdezese
 * @return tulajdonsagok
 * @see HashMap
 */   
    public HashMap getExe(){ return exe;}
    
/** 
 * binaris tulajdonsagok lekerdezese
 * @return tulajdonsagok Hash(key=exe property, value=ertek)
 * @see HashMap
 */   
    public HashMap getExeDisabled(){ return exeDisabled;}
    
/** 
 * A job outputjainak lekerdezese
 * @return Outputok
 * @see Vector
 */   
    public Vector getOutputs(){return outputs;}
    
/** 
 * A job inputjainak lekerdezese
 * @return Inputok
 * @see Vector
 */   
    public Vector getInputs(){return inputs;}
    
/** 
 * A jobhoz tartozo leiro(JDL,RSL) lekerdezese
 * @return leiro
 * @see HashMap
 */   
    public HashMap getDesc(){return desc;}
/** 
 * A jobhoz tartozo cimkek lekerdezese
 * @return leiro
 * @see HashMap
 */   
    public HashMap getLabel(){return label;}
/** 
 * A jobhoz konfiguracios szoveges leirasok lekerdezese
 * @return leiro
 * @see HashMap
 */   
    public HashMap getDesc0(){return desc0;}
/** 
 * A jobhoz tartozo orokolt tulajdonsagok lekerdezese
 * @return leiro
 * @see HashMap
 */   
    public HashMap getInherited(){return inh;}
    
/** 
 * A job eddigi konfiguracioinak listaja
 * @return History adatok
 * @see Vector
 */   
    public Vector getHistory(){return history;}
    
/** 
 * Job leiro text lekerdezese
 * @return leiro
 * @see String
 */       
    public String getTxt(){return txt;}
    
/** 
 * Job nev lekerdezese
 * @return nev
 * @see String
 */       
    public String getName(){return name;}
    
/** 
 * Job X koordinata lekerdezese
 * @return X koordinata erteke
 */       
    public long getX(){return x;}
    
/** 
 * Job Y koordinata lekerdezese
 * @return Y koordinata erteke
 */       
    public long getY(){return y;}
    
/** 
 * Job ID lekerdezese
 * @return ID erteke
 */       
    public long getId(){return id;}    
    
/** 
 * Adott Id-u Input lekerdezese
 * @param pId Input Id
 * @return PortDataBean
 * @see PortDataBean
 * @see String
 */   
    public PortDataBean getInput(String pId)
    {
        for(int i=0;i<inputs.size();i++)
            if(pId.equals(""+((PortDataBean)inputs.get(i)).getId())) return (PortDataBean)inputs.get(i);
        return null;
    }
    
/** 
 * Adott Id-u Output lekerdezese
 * @param pId Output Id
 * @return PortDataBean
 * @see PortDataBean
 * @see String
 */   
    public PortDataBean getOutput(String pId)
    {
        for(int i=0;i<outputs.size();i++)
            if(pId.equals(""+((PortDataBean)outputs.get(i)).getId())) return (PortDataBean)outputs.get(i);
        return null;
    }
    
/** 
 * A job binaris tulajdonsagainak beallitasa
 * @param value tulajdonsag Hash
 * @see HashMap
 */   
    public void setExe(HashMap value){exe=value;}
    
/** 
 * A job binaris orokolt tulajdonsagainak beallitasa
 * @param value tulajdonsag Hash
 * @see HashMap
 */   
    public void setExeDisabled(HashMap value){exeDisabled=value;}
    
/** 
 * A job inputok beallitasa
 * @param value Input Vector
 * @see Vector
 */   
    public void setInputs(Vector value){inputs=value;}
    
/** 
 * A job outputok beallitasa
 * @param value Output Vector
 * @see Vector
 */   
    public void setOutputs(Vector value){outputs=value;}
    
/** 
 * A job leirosok beallitasa
 * @param value leiro hash
 * @see HashMap
 */   
    public void setDesc(HashMap value){desc=value;}
/** 
 * A job tulajdonsg leiro beallitasa
 * @param value leiro hash
 * @see HashMap
 */   
    public void setDesc0(HashMap value){desc0=value;}
/** 
 * A job cimke beallitasa
 * @param value leiro hash
 * @see HashMap
 */   
    public void setLabel(HashMap value){label=value;}
/** 
 * A job tulajdonsag oroklesenek beallitasa
 * @param value leiro hash
 * @see HashMap
 */   
    public void setInherited(HashMap value){inh=value;}
    
/** 
 * A job history beallitasa
 * @param value History Vector
 * @see Vector
 */   
    public void setHistory(Vector value){history=value;}
    
/** 
 * A job nev beallitas
 * @param pValue Job nev
 * @see String
 */   
    public void setName(String pValue){name=pValue;}
    
/** 
 * A job leiros beallitas
 * @param pValue Job leiros
 * @see String
 */   
    public void setTxt(String pValue){txt=pValue;}
    
/** 
 * A job X koordinata beallitasa
 * @param pValue X erek
 */   
    public void setX(long pValue){x=pValue;} 
    
/** 
 * A job Y koordinata beallitasa
 * @param pValue Y erek
 */   
    public void setY(long pValue){y=pValue;}
    
/** 
 * A job ID  beallitasa
 * @param pValue ID
 */   
    public void setId(long pValue){id=pValue;}
    
/** 
 * Uj binaris parameter hozzaadasa
 * @param pKey kulcs
 * @param pValue ertek
 * @see String
 */       
    public void addExe(String pKey, String pValue){exe.put(pKey,pValue);}
/** 
 * Uj Tulajdonsagleiro hozzaadasa
 * @param pKey kulcs
 * @param pValue ertek
 * @see String
 */       
    public void addDesc0(String pKey, String pValue){desc0.put(pKey,pValue);}
/** 
 * Uj cimke hozzaadasa
 * @param pKey kulcs
 * @param pValue ertek
 * @see String
 */       
    public void addLabel(String pKey, String pValue){label.put(pKey,pValue);}
/** 
 * Uj orokolt ertek hozzaadasa
 * @param pKey kulcs
 * @param pValue ertek
 * @see String
 */       
    public void addInherited(String pKey, String pValue){inh.put(pKey,pValue);}
    
/** 
 * Uj binaris parameter hozzaadasa templatebol
 * @param pKey kulcs
 * @param pValue ertek
 * @see String
 */       
    public void addExeLock(String pKey, String pValue)
    {
        exe.put(pKey,pValue);
        exeDisabled.put(pKey, "");
    }
    
/** 
 * Uj Input hozzaadasa
 * @param pValue Input leiro
 * @see PortDataBean
 */       
    public void addInput(PortDataBean pValue){inputs.add(pValue);}
    
/** 
 * Uj Output hozzaadasa
 * @param pValue Output leiro
 * @see PortDataBean
 */       
    public void addOutput(PortDataBean pValue){outputs.add(pValue);}
    
/** 
 * Uj History bejegyzes hozzaadasa
 * @param value History leiro
 * @see HistoryBean
 */       
    public void addHistory(HistoryBean value){history.add(value);}
    
/** 
 * Uj Job leiro tulajdonsag hozzaadasa
 * @param pKey kulcs
 * @param pValue ertek
 * @see String
 */       
    public void addDesc(String pKey, String pValue){desc.put(pKey,pValue);}

}
