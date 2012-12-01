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

package hu.sztaki.lpds.pgportal.services.asm.exceptions;
import hu.sztaki.lpds.pgportal.services.asm.ASMWorkflow;

/**
 *
 * @author Akos Balasko MTA SZTAKI
 */
public class ASMException extends RuntimeException {
    private ASMWorkflow workflow_ex;
    
    public ASMException(Throwable cause) {
        super(cause);
    }

    public ASMException(Throwable cause, ASMWorkflow workflow_ex) {
        super(cause);       
        this.workflow_ex = workflow_ex;
    }
    
    public ASMException(String message, Throwable cause) {
        super(message, cause);
    }

    public ASMException(String message) {
        super(message);
    }

    public ASMException(String message, Throwable cause, ASMWorkflow workflow_ex) {
        super(message, cause);
        this.workflow_ex = workflow_ex;
    }

    public ASMException(String message, ASMWorkflow workflow_ex) {
        super(message);
        this.workflow_ex = workflow_ex;
    }    

    public ASMException() {
    }

    public ASMWorkflow getExtendedWorkflow() {
        return workflow_ex;
    }

    public void setProject(ASMWorkflow workflow_ex) {
        this.workflow_ex = workflow_ex;
    }
    
    
    
}
