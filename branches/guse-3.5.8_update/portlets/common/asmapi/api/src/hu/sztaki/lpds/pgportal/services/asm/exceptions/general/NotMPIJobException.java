/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm.exceptions.general;

/**
 *
 * @author akos
 */
public class NotMPIJobException extends RuntimeException {
    private String workflow;
    private String user;
    private String job;

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
    public NotMPIJobException(Throwable cause,String user, String workflow, String job) {
        super(cause);
        this.workflow = workflow;
        this.user = user;
        this.job = job;
    }

    public NotMPIJobException(String message,String user, String workflow,String job) {
        super(message);
        this.workflow = workflow;
        this.user = user;
        this.job = job;
    }

    public NotMPIJobException(String message, String user,String job){
        super(message);
        this.user = user;
        this.job = job;
    }
    public NotMPIJobException(Throwable cause, String user,String job){
        super(cause);
        this.user = user;
        this.job = job;

    }
    public NotMPIJobException() {
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

