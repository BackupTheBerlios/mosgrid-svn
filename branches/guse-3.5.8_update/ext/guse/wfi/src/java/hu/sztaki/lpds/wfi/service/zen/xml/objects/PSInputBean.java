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
 * Parametrikus peldany inputleiroja
 */

package hu.sztaki.lpds.wfi.service.zen.xml.objects;

/**
 *
 * @author krisztian karoczkai
 */
public class PSInputBean 
{
    private long pid;
    private long index=-1;

    public PSInputBean(long pid, long index) 
    {
        this.pid = pid;
        this.index=index;
    }

    public PSInputBean(long pid) {
        this.pid = pid;
    }

    public PSInputBean() {}

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
    
    
}
