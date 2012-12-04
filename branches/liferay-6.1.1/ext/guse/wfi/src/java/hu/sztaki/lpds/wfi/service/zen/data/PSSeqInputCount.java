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
 * PSSeqInputCount.java
 * Inputokra szamolt parametrikus darab szam
 */

package hu.sztaki.lpds.wfi.service.zen.data;

import java.util.Hashtable;

public class PSSeqInputCount 
{
    private Hashtable data=new Hashtable();

/**
 * Class constructor
 */    
    public PSSeqInputCount(){}

/**
 * Class constructor
 * @param pName Input neve
 * @param pSeq Input sorszama
 * @param pCount Darabszam
 */    
    public PSSeqInputCount(String pName,String pSeq,String pCount) 
    {
        setName(pName);
        setSeq(pSeq);
        setCount(pCount);
    }

/**
 * Input nev lekeredezese
 * @return Input neve
 */   
    public String getName(){return (String)data.get("name");}

/**
 * Input port sorrendjenek lekerdezese
 * @return sorrend
 */   
    public int getSeq(){return Integer.parseInt((String)data.get("seq"));}

/**
 * Input Parametrikus darabszama lekeredezese
 * @return darabszam
 */   
    public long getCount(){return Integer.parseInt((String)data.get("count"));}

/**
 * Input nev beallitasa
 * @param pValue Input neve
 */   
    public void setName(String pValue){data.put("name",pValue);}
    
/**
 * Input port sorrendjenek beallitasa
 * @param pValue sorrend
 */   
    public void setSeq(String pValue){data.put("seq",pValue);}
    
/**
 * Input Parametrikus darabszama beallitasa
 * @param pValue darabszam
 */   
    public void setCount(String pValue){data.put("count",pValue);}

}
