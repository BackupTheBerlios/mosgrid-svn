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
 * Perzistence.java
 * Perzistencia kezel_s deffinici_ja
 */

package hu.sztaki.lpds.wfi.service.zen.util.perzistence.inf;

import java.util.Hashtable;

/**
 * @author krisztian karoczkai
 */
public interface Perzistence 
{
    
/**
 * Uj perzistencia peldany letrehozasa
 * @param pZenID WorkflowRuntimeID
 * @param pParentZenID szulo WorkflowRuntimeID
 * @param pWorkflowDesc adat forras 
 * @param pWorkflowData workflow leiro 
 */    
    public void newInstance(String pZenID, String pParentZenID, String pWorkflowDesc, Hashtable pWorkflowData);
    
/**
 * Perzistencia peldany torlese
 * @param pZenID WorkflowRuntimeID
 */    
    public void deleteInstance(String pZenID);
    
/**
 * Meglevo peldanyok megnyitasa
 */    
    public void openAll();
}
