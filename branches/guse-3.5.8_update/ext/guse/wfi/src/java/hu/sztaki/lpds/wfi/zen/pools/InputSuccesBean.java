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

package hu.sztaki.lpds.wfi.zen.pools;

/**
 *
 * @author krisztian karoczkai
 */
public class InputSuccesBean 
{
    private String name;
    private long pid;
    boolean succes;

    public InputSuccesBean() {}
    public InputSuccesBean(String name, long pid, boolean succes) {
        this.name = name;
        this.pid = pid;
        this.succes = succes;
    }

public InputSuccesBean(String name, String pid, String succes) {
        this.name = name;
        this.pid = Long.parseLong(pid);
        this.succes = Boolean.parseBoolean(succes);
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public boolean isSucces() {
        return succes;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    @Override
    public String toString() 
    {
        return "\t<input name=\""+name+"\" pid=\""+pid+"\" succes=\""+succes+"\"/>\n";
    }
    
    
    
}
