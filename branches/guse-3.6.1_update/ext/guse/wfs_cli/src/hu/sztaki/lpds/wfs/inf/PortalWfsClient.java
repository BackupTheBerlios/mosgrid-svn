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
 * PortalClient.java
 * Portal szolgaltatasok elereset deffinialo interface
 */

package hu.sztaki.lpds.wfs.inf;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import java.util.Vector;
import hu.sztaki.lpds.wfs.com.*;

/**
 * @author krisztian
 */
public interface PortalWfsClient extends BaseCommunicationFace
{
    
    /**
     * Visszaadja a template workflowkat
     * @param value A Portal es felhasznaloi adatok
     * @return Az adott Portal adott felhasznalojanak absztraktworkflowinak neveit AbstractWorkflow classkent tartalmazo lista
     */
    public Vector getAbstractWorkflows(ComDataBean value);
    
    /**
     * Visszaadja a konkret workflowkat
     * @param value A Portal es felhasznaloi adatok
     * @return Az adott Portal adott felhasznalojanak workflowi RealWorkflow classkent tartalmazo lista
     */
    public Vector getRealWorkflows(ComDataBean value);
    
    /**
     * Visszaadja a konkret workflow Jobjait
     * @param value A Portal, felhasznalo es workflow adatok
     * @return Az adott Portal adott felhasznalojanak adott workflowjanak Jobjait JobBean classkent tartalmazo lista
     */
    public Vector getWorkflowJobs(ComDataBean value);
    
    /**
     * Visszaadja a konkret workflow peldany Jobjait
     * @param value A Portal, felhasznalo _s workflow adatok
     * @return Az adott Portal adott felhasznalojanak adott workflowjanak Jobjait JobStatusBean classkent tartalmazo lista
     */
    public Vector getWorkflowInstanceJobs(ComDataBean value);
    
    /**
     * Elmenti az uj workflowt
     * @param value Az uj workflow adatai
     * @return Mentesi muvelet sikeressege
     * @see ComDataBean
     */
    public boolean saveNewWorkflow(ComDataBean value);    
    
    /**
     * Torli a workflowt
     * @param value Az uj workflow adatai
     * @return Mentesi muvelet sikeressege
     * @see ComDataBean
     */
    public boolean deleteWorkflow(ComDataBean value);    
    
    /**
     * Torli a workflow grafot es a hozza tartozo workflowkat
     * @param value Az uj workflow adatai
     * @return Mentesi muvelet sikeressege
     * @see ComDataBean
     */
    public boolean deleteWorkflowGraf(ComDataBean value);    
    
/**
 * Workflow submit
 * @param value Kommunikacios leiro
 * @see ComDataBean
 */    
    public void submitWorkflow(ComDataBean value);
    
/**
 * Job tulajdons_g lek_rdez_se
 * @deprected
 * @param value Kommunikacios leiro
 * @see ComDataBean
 * @return Job tulajdonsagainal leirasa
 */    
    public JobPropertyBean getJobProperty(ComDataBean value);
    
/**
 * Workflow konfiguracio mentes
 * @param pID Kommunikacios leiro
 * @param pData Konfiguracios adat vector
 * @see ComDataBean
 * @see Vector
 */    
    public void setWorkflowConfigData(ComDataBean pID,Vector pData);  
    
/**
 * Workflow nem csatrona inputok lekerdezese
 * @param value Kommunikacios leiro
 * @return job/input lista
 * @see ComDataBean
 */    
    public Vector getNormalInputs(ComDataBean value);
    
/**
 * Workflow nem csatrona outputok lek_rdez_se
 * @param value Workflow kommunikacios leiro
 * @return job/output lista
 * @see ComDataBean
 */    
    public Vector getNormalOutputs(ComDataBean value);
    
/**
 * Workflow konfiguracios adatok lekerdezese
 * @param value Kommunikacios leiro
 * @return workflow Job-jai
 * @see ComDataBean
 * @see Vector
 */    
    public Vector getWorkflowConfigData(ComDataBean value);
    
/**
 * Workflow property adatok lekerdezese
 * @param value Kommunikacios leiro
 * @return WF property-k
 * @see ComDataBean
 * @see Vector
 */    
    public Vector getWorkflowProps(ComDataBean value);    
    
/**
 * Workflow property adatok lementese
 * @param pID Kommunikacios leiro
 * @param pData Wf tulajdonsagok
 * @see ComDataBean
 * @see Vector
 */    
    public void setWorkflowProps(ComDataBean pID,Vector pData);
    
/**
 * Workflow peldanylekerdezese
 * @param pValue workflow runtime iD
 * @return Kommunikacios leiro
 * @see ComDataBean
 * @see String
 */    
    public ComDataBean getWorkflowInstanceDesc(String pValue);
    
/**
 * Workflow peldanylekerdezese
 * @param value Kommunikacios leiro
 * @return Template Workflow tulajdons_gai
 * @see ComDataBean
 * @see Vector
 */    
    public Vector getTemplateWorkflows(ComDataBean value);

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
     * @param bean - workflowType (pl: appl, proj, real, abst, graf)
     * @return workflowList - workflowk listaja
     */
    public Vector getRepositoryItems(RepositoryWorkflowBean bean);

    /**
     * Atirja a wf grafjat
     * @param value Az uj workflow adatai
     * @return Mentesi muvelet sikeressege
     * @see ComDataBean
     */
    public boolean setNewGraf(ComDataBean value);    
    
    /**
     * Atirja a wf templatejet
     * @param value Az uj workflow adatai
     * @return uj graf
     * @see ComDataBean
     */
    public String setNewTemplate(ComDataBean value);     

    /**
     * Egy workflow volatile tipusu output
     * portjainak listajat kerdezi le.
     *
     * @param value workflow leiro bean
     * @return VolatileBean tartalmaz
     * egy workflow leiro comdatabeant es
     * egy volatile vectort ami a job volatile
     * output filejairol ad informaciot.
     * A visszaadott volatile vector
     * volatileentrybeaneket tartalmaz:
     */
    public VolatileBean getVolatileOutputs(ComDataBean value);
    
}
