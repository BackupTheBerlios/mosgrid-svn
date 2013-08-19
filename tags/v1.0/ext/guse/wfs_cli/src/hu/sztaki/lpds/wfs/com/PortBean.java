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
 * PortBean.java
 * Egy portot leiro bean
 */

package hu.sztaki.lpds.wfs.com;

import java.util.Hashtable;

/**
 * @author krisztian
 */
public class PortBean 
{
    private Hashtable data=new Hashtable();
    private Hashtable dataDisabled=new Hashtable();
/**
 * Class constructor
 */    
    public PortBean() {}
/**
 * Class constructor
 * @param pid portID
 * @param pX X koordinata erteke
 * @param pY Y koordinata erteke
 * @param pType port tipusa
 */    
    public PortBean(String pid,String pX,String pY,String pType) 
    {
        setId(pid);
        setX(pX);
        setY(pY);
        setType(pType);
    }

/**
 * Class constructor
 * @param pid portID
 * @param pX X koordinata erteke
 * @param pY Y koordinata erteke
 * @param pType port tipusa
 * @param pPreJob  Csatorna port eseten a megelozo job
 * @param pPreOutpt  Csatorna port eseten a megelozo output
 */    
  public PortBean(String pid,String pX,String pY,String pType,String pPreJob,String pPreOutpt) 
    {
        setId(pid);
        setX(pX);
        setY(pY);
        setType(pType);
        setPreJob(pPreJob);
        setPreOutput(pPreOutpt);
    }

/**
 * Port ID beallitasa
 * @param value port azonosito
 */
    public void setId(String value){data.put("id", value);}
/**
 * Csatorna output eseteben a Feltetel job outputjanak nevenek beallitasa
 * @param value output name
 */    
    public void setPreOutput(String value){data.put("poutput", value);}
/**
 * Csatorna output eseteben a Feltetel job nevenek beallitasa
 * @param value job name
 */    
    public void setPreJob(String value){data.put("pjob", value);}
/**
 * Port tipusanak beallitasa
 * @param value port typusa
 */    
    public void setType(String value){data.put("typ", value);}
/**
 * Port X koordinatajanak beallitasa
 * @param value port X koorinataja
 */    
    public void setX(String value){data.put("x", value);}
/**
 * Port Y koordinatajanak beallitasa
 * @param value port X koorinataja
 */    
    public void setY(String value){data.put("y", value);}
/**
 * Port sorszamanak beallitasa
 * @param value port sorszama
 */    
    public void setSeq(String value){data.put("seq", value);}
/**
 * Port nevenek beallitasa
 * @param value port neve
 */    
    public void setName(String value){data.put("name", value);}
/**
 * Porthoz tatozo tajekoztato szoveg beallitasa
 * @param value port text
 */    
    public void setTxt(String value){data.put("txt", value);}
/**
 * Port belso nevenek beallitasa
 * @param value port belso neve
 * @deprected
 */    
    public void setInternalName(String value){data.put("internalName", value);}
/**
 * Port kulso nevenek beallitasa
 * @param value port kulso neve
 * @deprected
 */    
    public void setExternalName(String value){data.put("enternalName", value);}
/**
 * Porthoz tartozo remote file nevenek beallitasa
 * @param value port remote file neve
 * @deprected
 */    
    public void setRemoteName(String value){data.put("remoteName", value);}
    
/** 
 * Port ID lekerdezese
 * @return port ID
 */    
    public String getId(){return (String)data.get("id");}
/** 
 * Csatorna output felteteljob output nevenek lekerdezese
 * @return output neve
 */    
    public String getPreOutput(){return (String)data.get("poutput");}
/** 
 * Csatorna output felteteljob nevenek lekerdezese
 * @return job neve
 */    
    public String getPreJob(){return (String)data.get("pjob");}
/** 
 * Port type lekerdezese
 * @return port type
 */    
    public String getType(){return (String)data.get("typ");}
/** 
 * Port X kkordinata ertekenek lekerdezese
 * @return koordinata ertek
 */    
    public String getX(){return (String)data.get("x");}
/** 
 * Port Y kkordinata ertekenek lekerdezese
 * @return koordinata ertek
 */    
    public String getY(){return (String)data.get("y");}
/** 
 * Port sorszamanak lekerdezese
 * @return port sorszam
 */    
    public String getSeq(){return (String)data.get("seq");}
/** 
 * Port nevenek lekerdezese
 * @return port neve
 */    
    public String getName(){return (String)data.get("name");}
/** 
 * Porthoz tartozo magyarazo szoveg lekerdezese
 * @return port text
 */    
    public String getTxt(){return (String)data.get("txt");}
/** 
 * Porthoz tartozo belso nev lekerdezese
 * @return port nev
 */    
    public String getInternalName(){return (data.get("internalName")==null)?getName():(String)data.get("internalName");}
/** 
 * Porthoz tartozo kulso nev lekerdezese
 * @return port nev
 */    
    public String getExternalName(){return (String)data.get("enternalName");}
/** 
 * Porthoz tartozo remote file nev lekerdezese
 * @return remote file
 */    
    public String getRemoteName(){return (String)data.get("remoteName");}
    
    
/** 
 * Port adat hash lekerdezese
 * @return port adat hash(key=port property value=ertek)
 */    
    public Hashtable getData(){return data;}
/** 
 * Port nem modosithato tulajdonsagainak adat hash lekerdezese
 * @return port adat hash(key=port property value="")
 */    
    public Hashtable getDataDisabled(){return dataDisabled;}
/** 
 * Port adat hash beallitasa
 * @param  value port adat hash(key=port property value=ertek)
 */    
    public void setData(Hashtable value){data=value;}
/** 
 * Port nem modosithato tulajdonsagainak adat hash beallitasa
 * @param value  port adat hash(key=port property value="")
 */    
    public void setDataDisabled(Hashtable value){dataDisabled=value;}

/**
 * Alltalanos tulajdonsag ertek lekerdezes
 * @param pkey tulajdonsag neve 
 * @return tulajdonsag ertek
 */   
    public String get(String pkey){return (String)data.get(pkey);}
/**
 * Alltalanos tulajdonsag ertek beallitasa
 * @param pkey tulajdonsag neve 
 * @param pValue  tulajdonsag erteke
 */   
    public void set(String pkey,String pValue){data.put(pkey,pValue);}
/**
 * Nem valtoztathato tulajdonsag ertek beallitasa
 * @param pkey tulajdonsag neve 
 * @param pValue  tulajdonsag erteke
 */   
    public void setDisabled(String pkey,String pValue)
    {
        data.put(pkey,pValue);
        dataDisabled.put(pkey,"");
    }
    
/** 
 * Port adat hash lekerdezese
 * @return port adat hash(key=port property value=ertek)
 */    
    public Hashtable getAllData(){return data;}
}
