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
 * JobBean.java
 *
 * Egy Job lista adatait tartalmazó Bean
 */

package hu.sztaki.lpds.wfs.com;


import java.util.Vector;

/**
 * @author krisztian
 */
public class JobBean 
{
    private String jobID="";
    private int status=0;
    private String resource="";
    private String txt="";
    private int x,y; 
    private Vector ports=new Vector();
/**
 * JavaBeaneknen szukseges konstruktor
 */    
    public JobBean() {}

/**
 * Egyszerubb hasznalat erdekeben alkalmazott konstruktor
 * @param pJobID   A Job azonositoja
 * @param pStatus   A job Statusza
 * @param pResource   A job eroforrasa
 * @param pTxt  A workflow leiroja
 * @param pX  X koordinata
 * @param pY Y koordinata
 */    
    public JobBean(String pJobID, int pStatus, String pResource,String pTxt, int pX, int pY) 
    {
        jobID=pJobID;
        status=pStatus;
        resource=pResource;
        txt=pTxt;
        x=pX;
        y=pY;
    }

/**
 * Visszaadja a JobID-t
 * @return a jobID-ja
 */    
    public String getJobID(){return jobID;}

/**
 * Visszaadja a Job statuszat-t
 * @return a job stutusza
 */    
    public int getStatus(){return status;}
    
/**
 * Visszaadja a Jobot futtato eroforrast
 * @return a joot futtato eroforras
 */    
    public String getResource(){return resource;}
    
/**
 * Visszaadja a Job szoveges leirasat
 * @return a jooB leirasa
 */    
    public String getTxt(){return txt;}
    
/**
 * Visszaadja a Job x koordinatajat
 * @return a jooB x koordinataja a grafban
 */    
    public int getX(){return x;}
    
/**
 * Visszaadja a Job y koordinatajat
 * @return a jooB y koordinataja a grafban
 */    
    public int getY(){return y;}

/**
 * portok lekerdezese
 * @return port Vector
 */    
    public Vector getPorts(){return ports;}
    
/**
 * portok beallitasa
 * @param pValue port Vector
 */    
    public void setPorts(Vector pValue){ports=pValue;}
    
/**
 * Beallitja a JobID-t
 * @param value a jobID-ja
 */    
    public void setJobID(String value){jobID=value;}
    
/**
 * Beallitja a Job statuszat
 * @param value a job statusza
 */    
    public void setStatus(int value){status=value;}
    
/**
 * Beallitja a Jobot futtato eroforros erteket
 * @param value a Jobot futtato eroforras erteke
 */    
    public void setResource(String value){resource=value;}
    
/**
 * Beallitja a Jobot szoveges leirasat
 * @param value a Jobot leirasa
 */    
    public void setTxt(String value){txt=value;}
    
/**
 * Beallitja a Job grafikus x koordinatakat.
 * @param value a Job x koordinataja
 */    
    public void setX(int value){x=value;}
    
/**
 * Beallitja a Job grafikus y koordinatakat.
 * @param value a Job y koordinataja
 */    
    public void setY(int value){y=value;}
    
}
