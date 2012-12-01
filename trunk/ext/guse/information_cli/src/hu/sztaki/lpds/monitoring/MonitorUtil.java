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
 * Default Monitoring services
 */

package hu.sztaki.lpds.monitoring;

import hu.sztaki.lpds.service.CommandValueBean;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author krisztian karoczkai
 */
public class MonitorUtil {

/**
 * Default monitoring commands(use all gUSE application)
 */
/*    private static final String[][] MONITORINGCOMMANDS=new String[][]{
        {"totalmemory","getToatalMemory"},
        {"freememory","getFreeMemory"},
        {"maxmemory","getMaxMemory"},
        {"usememory","getUseMemory"},
        {"live","isLive"}};
*/
/**
 * Base Application system information tool
 * @param pCommands  pCommand list of monitoring commands
 * @param pMonitoringCommands available commands and functions
 * @return monitoring value
 * @throws java.lang.NullPointerException if command not definited
 */
    public List<CommandValueBean> getMonitoringValues(List<java.lang.String> pCommands, String[][] pMonitoringCommands) throws NullPointerException{
        ArrayList<CommandValueBean> res =new ArrayList<CommandValueBean>();
        CommandValueBean newItem;
        Method m;
        try{
            Class c = Class.forName(this.getClass().getName());
            for(String[] t:pMonitoringCommands){
                if((!"".equals(t[1])) && pCommands.contains(t[0])) {
                    newItem=new CommandValueBean();
                    try{
                        newItem.setCommand(t[0]);
                        m = c.getDeclaredMethod(t[1]);
                        newItem.setValue(""+(((Long)m.invoke(this)).longValue()));
                        res.add(newItem);
                    }
                    catch(NoSuchMethodException e){
// find method in super class
                        try{
                            c = MonitorUtil.class;
                            newItem.setCommand(t[0]);
                            m = c.getDeclaredMethod(t[1]);
                            newItem.setValue(""+(((Long)m.invoke(this)).longValue()));
                            res.add(newItem);
                        }
                        catch(Exception e0){/*not suported method or calling error*/}
                    }
                    catch(Exception e){/*calling error*/}
                }
            }
        }
        catch(ClassNotFoundException e){/*invalid class*/}
        return res;
    }


/**
 * get Total Memory
 * @return value of Total Memory
 */
        public Long getTotalMemory(){ return Runtime.getRuntime().totalMemory();}

/**
 * get Maximum usable Memory
 * @return value of maximum usable Memory
 */
        public Long getMaxMemory(){ return Runtime.getRuntime().maxMemory();}
/**
 * get Free JVM Memory
 * @return value of Free JVM Memory
 */
        public Long getFreeMemory(){ return Runtime.getRuntime().freeMemory();}
/**
 * get Using Memory
 * @return value of Total Memory
 */
        public Long getUseMemory(){ return (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());}
/**
 * get Total Memory
 * @return value of Total Memory
 */
        public Long isLive(){
            try{String[] test=new String[1024];return (long)1;}
            catch(Exception e){return (long)0;}
        }

}
