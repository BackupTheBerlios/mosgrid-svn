/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.pgportal.services.asm.beans;

/**
 *
 * @author akos
 */
public class ASMResourceBean {

    private String type;


    private String grid;
    private String queue;
    private String resource;

    
    public String getGrid() {
        return grid;
    }

    public void setGrid(String grid) {
        this.grid = grid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public ASMResourceBean(String type, String grid, String resource, String queue) {
        this.type = type;
        this.grid = grid;
        this.queue = queue;
        this.resource = resource;
    }

   
    
}
