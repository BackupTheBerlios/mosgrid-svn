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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm.exceptions.importation;
/**
 *
 * @author Akos Balasko MTA SZTAKI
 */
public class Import_GeneralException extends RuntimeException {
    private String workflow;
    private String user;
    public Import_GeneralException(Throwable cause,String user, String workflow) {
        super(cause);
        this.workflow = workflow;
        this.user = user;
    }

    public Import_GeneralException(String message,String user, String workflow) {
        super(message);
        this.workflow = workflow;
        this.user = user;
    }

    public Import_GeneralException(String message, String user){
        super(message);
        this.user = user;
    }
    public Import_GeneralException(Throwable cause, String user){
        super(cause);
        this.user = user;

    }
    public Import_GeneralException() {
    }

    public String getWorkflow() {
        return workflow;
    }

    public String getUser(){
        return user;
    }
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    public void setUser(String user) {
        this.user = user;
    }
}

