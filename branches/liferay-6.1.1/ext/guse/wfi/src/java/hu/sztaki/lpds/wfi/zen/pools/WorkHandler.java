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
 * Submitalasra varo jobok kezelese
 */

package hu.sztaki.lpds.wfi.zen.pools;



import hu.sztaki.lpds.wfi.zen.pools.inf.Algorithm;
import hu.sztaki.lpds.wfi.zen.pools.inf.IncomingStatusPool;
import hu.sztaki.lpds.wfi.zen.pools.inf.InstancePool;


/**
 * @author krisztian karoczkai
 */
public class WorkHandler 
{
    private static WorkHandler instance=new WorkHandler();
    private RamSubmitPoolImpl out=new RamSubmitPoolImpl();
    private InstancePool inst=new DRamInstancePoolImpl();
    private Algorithm algo=new HardAlgorithmImpl();
    private IncomingStatusPool statuses=new RAMIncomingStatusPollImpl();

    public WorkHandler(){}
            
    public static WorkHandler getI(){return instance;}
        
    public RamSubmitPoolImpl getOutPool(){return out;}
    
    public InstancePool getInstancePool(){return inst;}
    
    public Algorithm getAlgorithm(){return algo;}
    
    public IncomingStatusPool getIncomingStatusPool(){return statuses;}
}
