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
package hu.sztaki.lpds.wfs.com;

/**
 * Egy adott volatile output port
 * informacioit tartalmazo bean.
 *
 * A portal a wfs es a storage hasznalja.
 *
 * @author lpds
 */
public class VolatileEntryBean {
    
    // a graph ban megadott
    // job nev, azonosito
    private String jobID;
    
    // a graph ban megadott
    // output file name
    private String outputName1;
    
    // a concret workflow
    // confignal beallitott
    // output file name
    private String outputName2;
    
    /**
     * JavaBeaneknek szukseges konstruktor
     */
    public VolatileEntryBean() {
        setJobID("");
        setOutputName1("");
        setOutputName2("");
    }
    
    /**
     * Egyszerubb hasznalat erdekeben alkalmazott konstruktor
     *
     * @param jName - job neve
     * @param outName1 - output file neve (graphbol)
     * @param outName2 - output file neve (configbol)
     */
    public VolatileEntryBean(String jName, String outName1, String outName2) {
        setJobID(jName);
        setOutputName1(outName1);
        setOutputName2(outName2);
    }
    
    /**
     * Visszaadja a job azonositojat.
     * @return String jobID
     */
    public String getJobID() {
        return jobID;
    }
    
    /**
     * Beallitja a job azonosiotjat.
     * @param  jobID job neve
     */
    public void setJobID(String jobID) {
        this.jobID = jobID;
    }
    
    /**
     * Visszaadja az output file nevet.
     * @return String output file name
     */
    public String getOutputName1() {
        return outputName1;
    }
    
    /**
     * Beallitja az output file nevet.
     * @param outputName1  output file name
     */
    public void setOutputName1(String outputName1) {
        this.outputName1 = outputName1;
    }
    
    /**
     * Visszaadja az output file nevet.
     * @return String output file name
     */
    public String getOutputName2() {
        return outputName2;
    }
    
    /**
     * Beallitja az output file nevet.
     * @param outputName2  output file name
     */
    public void setOutputName2(String outputName2) {
        this.outputName2 = outputName2;
    }
    
}
