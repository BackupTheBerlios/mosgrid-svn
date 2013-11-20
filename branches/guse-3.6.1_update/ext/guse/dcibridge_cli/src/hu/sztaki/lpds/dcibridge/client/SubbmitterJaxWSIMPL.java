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
 * JaxWS alapu bes szerviz hivas
 */

package hu.sztaki.lpds.dcibridge.client;

import hu.sztaki.lpds.information.inf.BaseCommunicationFace;
import java.net.URL;
import javax.xml.namespace.QName;
import org.ggf.schemas.bes._2006._08.bes_factory.BESFactoryService;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentType;
import org.ggf.schemas.bes._2006._08.bes_factory.InvalidRequestMessageFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAcceptingNewActivitiesFault;
import org.ggf.schemas.bes._2006._08.bes_factory.NotAuthorizedFault;
import org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesResponseType;
import org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesType;
import org.ggf.schemas.bes._2006._08.bes_factory.UnsupportedFeatureFault;

/**
 * @author krisztian karoczkai
 */
public class SubbmitterJaxWSIMPL implements SubmitterFace{
    private BESFactoryService client=null;
    private String webApplication;
    private String wsdl;

/**
 *@see SubmitterFace#createActivity(org.ggf.schemas.bes._2006._08.bes_factory.CreateActivityType)
 */
@Override
    public CreateActivityResponseType createActivity(CreateActivityType parameters) throws NotAuthorizedFault, NotAcceptingNewActivitiesFault, InvalidRequestMessageFault, UnsupportedFeatureFault,Exception {
        createClientIfNotExist();
        return client.getBESFactoryPort().createActivity(parameters);
    }

/**
 *@see SubmitterFace#getActivityStatuses(org.ggf.schemas.bes._2006._08.bes_factory.GetActivityStatusesType)
 */
@Override
    public GetActivityStatusesResponseType getActivityStatuses(GetActivityStatusesType parameters) throws InvalidRequestMessageFault,Exception {
        createClientIfNotExist();
        return client.getBESFactoryPort().getActivityStatuses(parameters);
    }
/**
 *@see SubmitterFace#terminateActivities(org.ggf.schemas.bes._2006._08.bes_factory.TerminateActivitiesType)
 */
@Override
    public TerminateActivitiesResponseType terminateActivities(TerminateActivitiesType parameters) throws InvalidRequestMessageFault,Exception {
        createClientIfNotExist();
        return client.getBESFactoryPort().terminateActivities(parameters);
    }

/**
 *@see SubmitterFace#getActivityDocuments(org.ggf.schemas.bes._2006._08.bes_factory.GetActivityDocumentsType)
 */
@Override
    public GetActivityDocumentsResponseType getActivityDocuments(GetActivityDocumentsType parameters) throws InvalidRequestMessageFault,Exception {
        createClientIfNotExist();
        return client.getBESFactoryPort().getActivityDocuments(parameters);
    }

/**
 *@see SubmitterFace#getFactoryAttributesDocument(org.ggf.schemas.bes._2006._08.bes_factory.GetFactoryAttributesDocumentType)
 */
@Override
    public GetFactoryAttributesDocumentResponseType getFactoryAttributesDocument(GetFactoryAttributesDocumentType parameters) throws InvalidRequestMessageFault,Exception {
        createClientIfNotExist();
        return client.getBESFactoryPort().getFactoryAttributesDocument(parameters);
    }

/**
 *@see BaseCommunicationFace#setServiceURL(java.lang.String)
 */
@Override
    public void setServiceURL(String value) {webApplication=value;}


/**
 *@see BaseCommunicationFace#setServiceID(java.lang.String)
 */
@Override
    public void setServiceID(String value) {wsdl=value;}

    private void createClientIfNotExist(){
        if(client==null){
            try{
                client=new BESFactoryService(new URL(webApplication+wsdl), new QName("http://schemas.ggf.org/bes/2006/08/bes-factory", "BESFactoryService"));

            }
            catch(Exception e){e.printStackTrace();}
        }

    }

}
