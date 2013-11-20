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
 * WfsPortalService.java
 * A workflowtar szervice alltal megvalositando metodusok
 */

package hu.sztaki.lpds.wfs.inf;

import hu.sztaki.lpds.wfs.com.*;
import java.util.Vector;

/**
 * @author krisztian
 */

public interface WfsPortalService 
{
    /**
     * Visszaadja a template workflowkat
     * @param value A portal es felhasznaloi adatok
     * @return Az adott portal adott felhasznalojanak template workflowinak neveit AbstractWorkflow classkent tartalmazo lista
     */
    public Vector getAbstractWorkflows(ComDataBean value);
    
    /**
     * Visszaadja a konkret workflowkat
     * @param value A portal es felhasznaloi adatok
     * @return Az adott portal adott felhasznalojanak workflowi RealWorkflow classkent tartalmazo lista
     */
    public Vector getRealWorkflows(ComDataBean value);
    
    /**
     * Visszaadja a konkret workflow Jobjait
     * @param value A portal, felhasznalo es workflow adatok
     * @return Az adott portal adott felhasznalojanak adott workflowjanak Jobjait JobBean classkent tartalmazo lista
     */
    public Vector getWorkflowJobs(ComDataBean value);
    
    /**
     * Uj konkret workflow felvitele
     * @param value Az uj workflow adatai
     * @return Az adott portal adott felhasznalojanak adott workflowjonak Jobjait JobBean classkent tartalmazo lista
     */
    public Boolean saveNewWorkflow(ComDataBean value);
    
    /**
     * Workflow nem csatrona input inputjainak lekerdezese
     * @param value Kommunikacios leiro
     * @return Job/input lista
     */
    public Vector getNormalInputs(ComDataBean value);
    
    /**
     * Workflow nem csatrona output inputjainak lekerdezese
     * @param value Kommunikacios leiro
     * @return Job/output lista
     */
    public Vector getNormalOutputs(ComDataBean value);
    
    /**
     * Visszaadja a konkret workflow peldany Jobjait
     * @param value A portal, felhasznalo es workflow adatok
     * @return Az adott portal adott felhasznalojanak adott workflowjanak Jobjait JobStatusBean classkent tartalmazo lista
     */
    public Vector getWorkflowInstanceJobs(ComDataBean value);
    
    /**
     * Visszaadja a workflow configuracioja kozben elofordult hibak listajat.
     * 
     * (minden hiba bejegyzes egy bean a vectorban)
     *
     * @param value ComDataBean A portal, felhasznalo es workflow adatok
     * @return Vector - WorkflowConfigErrorBean -eket tartalmaz
     */
    public Vector getWorkflowConfigDataError(ComDataBean value);
    
    /**
     * Visszaadja a repository-ban talalhato
     * megadott tipusu workflow-k listajat. 
     *
     * (Az eredmeny egy vektor melyben
     * RepositoryWorkflowBean-ek vannak.)
     * 
     * @param  bean - workflowType (pl: appl, proj, real, abst, graf)
     * @return workflowList - workflowk listaja
     */
    public Vector getRepositoryItems(RepositoryWorkflowBean bean);

    /**
     * Egy workflow volatile tipusu output
     * portjainak listajat kerdezi le.
     *
     * @param  value workflow leiro bean
     * @return VolatileBean tartalmaz
     * egy workflow leiro comdatabeant es
     * egy volatile vectort ami a job volatile
     * output filejairol ad informaciot.
     * A visszaadott volatile vector
     * volatileentrybeaneket tartalmaz:
     */
    public VolatileBean getVolatileOutputs(ComDataBean value);
/**
 * Workflow parameterek lekerdezese
 * @param value kommunikacios leiro
 * @return parameterlista
 */
    public Vector getWorkflowProps(ComDataBean value);
    
/**
 * Workflow parameterek beallitasa
 * @param value kommunikacios leiro
 * @param wfprop  parameterlista
 */
    public void setWorkflowProps(ComDataBean value, Vector wfprop);
    
}
