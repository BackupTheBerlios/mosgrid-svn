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
 * PortDataBean.java
 * Input/Output tulajdonsag leiro Bean
 */

package hu.sztaki.lpds.wfs.com;

import java.util.HashMap;

/**
 * @author krisztian
 */
public class PortDataBean 
{
    private long id=0;
    private String name="";
    private String txt="";
    private String prejob="";
    private String preoutput="";
    private long seq=0;
    private long x;
    private long y;
    private HashMap data=new HashMap(); 
    private HashMap labelp=new HashMap(); 
    private HashMap descp=new HashMap(); 
    private HashMap inhp=new HashMap(); 
    private HashMap dataDisabled=new HashMap(); 

/**
 * Class constructor
 */    
    public PortDataBean() {}
    
/**
 * Class constructor
 * @param pId Port ID
 * @param pSeq Port sorrend
 * @param pName Port neve
 * @param pTxt Port leiro
 * @param pPrejob Port csatorna output jobja
 * @param pPreoutput Port csatorna outputja
 * @param pX Port X koordinataja
 * @param pY Port X koordinataja
 */    
    public PortDataBean(long pId, long pSeq, String pName,String pTxt,String pPrejob,String pPreoutput, long pX, long pY) 
    {
        id=pId;
        seq=pSeq;
        name=pName;
        txt=pTxt;
        prejob=pPrejob;
        preoutput=pPreoutput;
        x=pX;
        y=pY;
    }
    
/**
 * Port tulajdonsag lekerdezese
 * @param pkey tulajdonsag
 * @return tulajdonsaghoz kapcsolodo ertek
 */    
    public String get(String pkey){return (String)data.get(pkey);}
    
/**
 * Port nev tulajdonsag lekerdezese
 * @return port neve
 */    
    public String getName(){return name;}
    
/**
 * Port leiros tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 * @see String
 */    
    public String getTxt(){return txt;}
    
/**
 * Port csatorna Job tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 * @see String
 */    
    public String getPrejob(){return prejob;}
    
/**
 * Port csatorna output tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 * @see String
 */    
    public String getPreoutput(){return preoutput;}
    
/**
 * Port ID tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 */    
    public long getId(){return id;}
    
/**
 * Port X korin_ta tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 */    
    public long getX(){return x;}
    
/**
 * Port Y koordin_ta tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 */    
    public long getY(){return y;}
    
/**
 * Port Sorrend tulajdonsag lekerdezese
 * @return tulajdonsag erteke
 */    
    public long getSeq(){return seq;}
    
/**
 * Port tulajdonsag adatok lekerdezese
 * @return tulajdonsag ertek Hash
 * @see HashMap
 */    
    public HashMap getData(){return (data==null)?new HashMap():data;}
/**
 * Port tulajdonsag cimkek lekerdezese
 * @return tulajdonsag ertek Hash
 * @see HashMap
 */    
    public HashMap getLabel(){return labelp;}
/**
 * Port tulajdonsag konfiguracios leirasok lekerdezese
 * @return tulajdonsag ertek Hash
 * @see HashMap
 */    
    public HashMap getDesc(){return descp;}
/**
 * Port tulajdonsag orokolt adatok lekerdezese
 * @return tulajdonsag ertek Hash
 * @see HashMap
 */    
    public HashMap getInherited(){return inhp;}
    
/**
 * Port orokolt tulajdonsag adatok lekerdezese
 * @return tulajdonsag ertek Hash
 * @see HashMap
 */    
    public HashMap getDataDisabled(){return dataDisabled;}
    
/**
 * Port nev tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 * @see String
 */    
    public void setName(String pValue){name=pValue;}
    
/**
 * Port leiros tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 * @see String
 */    
    public void setTxt(String pValue){txt=pValue;}
    
/**
 * Port csatorna output job tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 * @see String
 */    
    public void setPrejob(String pValue){prejob=pValue;}
    
/**
 * Port csatorna output tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 * @see String
 */    
    public void setPreoutput(String pValue){preoutput=pValue;}
    
/**
 * Port ID beallitasa
 * @param pValue tulajdonsag ertek
 */    
    public void setId(long pValue){id=pValue;}
    
/**
 * Port X koordinata tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 */    
    public void setX(long pValue){x=pValue;}
    
/**
 * Port Y koordinata tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 */    
    public void setY(long pValue){y=pValue;}
    
/**
 * Port sorrend tulajdonsag beallitasa
 * @param pValue tulajdonsag ertek
 */    
    public void setSeq(long pValue){seq=pValue;}
    
/**
 * Port adatok beallitasa
 * @param pValue tulajdonsag ertek Hash
 * @HashMap
 */    
    public void setData(HashMap pValue){data=pValue;}
/**
 * Port adatokhoz tartozo cimkek beallitasa
 * @param pValue tulajdonsag ertek Hash
 * @HashMap
 */    
    public void setLabel(HashMap pValue){labelp=pValue;}
/**
 * Port adatokhoz tartozo konfiguracios leirasok beallitasa
 * @param pValue tulajdonsag ertek Hash
 * @HashMap
 */    
    public void setDesc(HashMap pValue){descp=pValue;}
/**
 * Orokolt Port adatok beallitasa
 * @param pValue tulajdonsag ertek Hash
 * @HashMap
 */    
    public void setInherited(HashMap pValue){inhp=pValue;}
    
/**
 * Port orokolt adatok beallitasa
 * @param value tulajdonsag ertek Hash
 */    
    public void setDataDisabled(HashMap value){dataDisabled=value;}
    
/**
 * Port tulajdonsag beallitasa
 * @param pkey kulcs
 * @param pValue ertek
 * @String
 */    
    public void set(String pkey,String pValue){data.put(pkey,pValue);}
    
/**
 * Port orokolt tulajdonsag beallitasa
 * @param pkey kulcs
 * @param pValue ertek
 * @String
 */    
    public void setDisabled(String pkey,String pValue)
    {
        data.put(pkey,pValue);
        dataDisabled.put(pkey,"");
    }
    
}
