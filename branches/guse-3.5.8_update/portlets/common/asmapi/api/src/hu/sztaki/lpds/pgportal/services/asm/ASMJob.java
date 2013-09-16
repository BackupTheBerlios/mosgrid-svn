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

package hu.sztaki.lpds.pgportal.services.asm;

import java.util.Hashtable;
/**
 * Class to represent simple job contained by ASMworkflows
 * @author akos
 * @version 3.3
 */
public class ASMJob
 {



    private Hashtable<String,String> input_ports;
    private Hashtable<String,String> output_ports;
     private String jobname ="";
     private int status = 0;
/**
 * Returns status of the job
 * @return statuscode
 */
    public int getStatus() {
        return status;
    }
/**
 * Sets status of the job
 * @param status
 */
    public void setStatus(int status) {
        this.status = status;
    }
/**
 * Constructor to create ASMJob object
 * @param jobname - name of the job
 * @param inputports - Hashtable that contains inputports (key: portNumber E (0..15); value: name of the port )
 * @param outputports - Hashtable that contains outputports (key: portNumber E (0..15); value: name of the port )
 */
     public ASMJob(String jobname,Hashtable<String,String> inputports,Hashtable<String,String> outputports){
         input_ports = new Hashtable<String,String>();
         input_ports = inputports;
         output_ports = new Hashtable<String,String>();
         output_ports = outputports;
         this.jobname = jobname;
     }

     public ASMJob(){}
/**
 * Return the inputports
 * @return inputports
 */

      public Hashtable<String, String> getInput_ports() {
        return input_ports;
    }
/**
 * Sets the input ports
 * @param input_ports
 */
    public void setInput_ports(Hashtable<String, String> input_ports) {
        this.input_ports = input_ports;
    }
/**
 * Gets the name of the job
 * @return jobname
 */
    public String getJobname() {
        return jobname;
    }
/**
 * Sets the name of the job
 * @param jobname
 */
    public void setJobname(String jobname) {
        this.jobname = jobname;
    }
/**
 * Gets the output ports
 *
 * @return output_ports
 */
    public Hashtable<String, String> getOutput_ports() {
        return output_ports;
    }
/**
 * Sets the output ports
 * @param output_ports
 */
    public void setOutput_ports(Hashtable<String, String> output_ports) {
        this.output_ports = output_ports;
    }

    


 }