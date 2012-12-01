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

/**
 * Service interface accessible from portal side
 */
package hu.sztaki.lpds.information.net.wsaxis13;

//~--- non-JDK imports --------------------------------------------------------

import hu.sztaki.lpds.information.data.GroupPropertyBean;
import hu.sztaki.lpds.information.data.GroupsTypeBean;
import hu.sztaki.lpds.information.data.GuseServiceBean;
import hu.sztaki.lpds.information.data.GuseServiceCommunicationBean;
import hu.sztaki.lpds.information.data.GuseServiceTypeBean;
import hu.sztaki.lpds.information.data.ResourceBean;
import hu.sztaki.lpds.information.data.ServicePropertyBean;
import hu.sztaki.lpds.information.data.ServiceResourceBean;
import hu.sztaki.lpds.information.data.ServiceUserBean;
import hu.sztaki.lpds.information.service.alice.DH;
import hu.sztaki.lpds.information.service.alice.ServicesInitCommandImpl;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author krisztian
 */
public class InformationPortalServiceImpl {

    /**
     * Getting the list of all services
     * @return service list
     */
    public List getAllServices() {
        List<GuseServiceBean> tmp = DH.getI().getAllGuseService();

        return tmp;
    }

    /**
     * Getting all types of services
     * @return service type list
     */
    public List getAllServiceTypes() {
        List<GuseServiceTypeBean> tmp = DH.getI().getAllGuseServiceType();

        return tmp;
    }

    /**
     * Getting all the service communication descriptors
     * @return communication list
     */
    public List getAllServiceComs() {
        List<GuseServiceCommunicationBean> tmp = DH.getI().getAllGuseServiceComm();

        return tmp;
    }

    /**
     * Service deletion
     * @param pID service unique internal ID
     * @return descriptor of the success of the operation (servicecall.deleteservice=successful)
     */
    public String deleteService(String pID) {
        String res = "servicecall.deleteservice";

        try {
            long tmp = Long.parseLong(pID);

            DH.getI().deleteService(tmp);
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (NullPointerException e0) {
            res = "error.valid";
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Service type deletion
     * @param pID type unique internal ID
     * @return descriptor of the success of the operation(servicecall.deleteservicetype=successful)
     */
    public String deleteServiceType(String pID) {
        String res = "servicecall.deleteservicetype";

        try {
            long tmp = Long.parseLong(pID);

            DH.getI().deleteServiceType(tmp);
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (NullPointerException e0) {
            res = "error.valid";
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Service communication deletion
     * @param pID internal communication unique ID
     * @return descriptor of the success of the operation (servicecall.deleteservicecommunicaion=successful)
     */
    public String deleteServiceCom(String pID) {
        String res = "servicecall.deleteservicecommunicaion";

        try {
            long tmp = Long.parseLong(pID);

            DH.getI().deleteServiceComm(tmp);
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (NullPointerException e0) {
            res = "error.valid";
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Service communication deletion
     * @param pValue service descriptor bean
     * @return descriptor of the success of the operation (servicecall.modservice=successful edit,servicecall.newservice=this is a new service)
     */
    public String dataService(GuseServiceBean pValue) {
        String res = "";

//      edit
        try {
            if (pValue.getId() > 0) {
                res = "servicecall.modservice";

                GuseServiceBean tmp = DH.getI().getGuseService(pValue.getId());

                if (tmp != null) {
                    tmp.setIurl(pValue.getIurl());
                    tmp.setSurl(pValue.getSurl());
                    tmp.setUrl(pValue.getUrl());
                    tmp.setOwner(pValue.getOwner());
                    tmp.setState(pValue.isState());

                    GuseServiceTypeBean ty = DH.getI().getGuseServiceType(pValue.getTyp().getId());

                    if (ty != null) {
                        tmp.setTyp(ty);
                    } else {
                        tmp.setTyp(null);
                    }

                    DH.getI().newService(tmp);
                } else {
                    res = "error.valid";
                }
            }

//          new service
            else {
                res = "servicecall.newservice";
                DH.getI().newService(pValue);
            }
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }

    /**
     * Changing the service communication descriptor
     * @param pValue communication descriptor
     * @return descriptor of the success of the operation (servicecall.modservicecom=successful)
     */
    public String dataServiceCom(GuseServiceCommunicationBean pValue) {
        String res = "";

        try {
            if (pValue.getId() > 0) {
                GuseServiceCommunicationBean tmp = DH.getI().getGuseServiceComm(pValue.getId());

                if (tmp != null) {
                    res = "servicecall.modservicecom";
                    tmp.setCname(pValue.getCname());
                    tmp.setTxt(pValue.getTxt());
                    DH.getI().newServiceComm(tmp);
                } else {
                    res = "error.valid";
                }
            } else {
                res = "servicecall.newservicecom";
                DH.getI().newServiceComm(pValue);
            }
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }
/**
 * Changing the service type descriptor, checking equality based on id
 * @param pValue modified service type descriptor
 * @return
 */
    public String dataServiceType(GuseServiceTypeBean pValue) {
        String res = "";

        try {
            if (pValue.getId() > 0) {
                GuseServiceTypeBean tmp = DH.getI().getGuseServiceType(pValue.getId());

                if (tmp != null) {
                    res = "servicecall.modservicetype";
                    tmp.setSname(pValue.getSname());
                    tmp.setTxt(pValue.getTxt());
                    DH.getI().newServiceType(tmp);
                } else {
                    res = "error.valid";
                }
            } else {
                res = "servicecall.newservicetype";
                DH.getI().newServiceType(pValue);
            }
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }
/**
 * Defining new service user
 * @param pServiceID service ID
 * @param pUserLogin service user descriptor
 * @return
 */
    public String addServiceUser(String pServiceID, ServiceUserBean pUserLogin) {
        String res = "servicecall.newuserpriority";

        try {
            long            sid = Long.parseLong(pServiceID);
            GuseServiceBean tmp = DH.getI().getGuseService(sid);

            if (tmp != null) {
                DH.getI().persistServiceUser(tmp, pUserLogin);
            } else {
                res = "error.valid";
            }
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (Exception e1) {
            res = "error.datahandler";
            e1.printStackTrace();
        }

        return res;
    }
/**
 * Taking away service usage rights from the user
 * @param pServiceID service ID
 * @param pUserLoginID user ID
 * @return
 */
    public String deleteServiceUser(String pServiceID, String pUserLoginID) {
        String res = "servicecall.deleteuserpriority";

        try {
            long            sid  = Long.parseLong(pServiceID);
            long            uid  = Long.parseLong(pUserLoginID);
            GuseServiceBean tmp  = DH.getI().getGuseService(sid);
            ServiceUserBean user = DH.getI().getServiceUserBean(uid);

            if ((tmp != null) && (user != null)) {
                DH.getI().removeAuth(tmp, user);
            } else {
                res = "error.valid";
            }
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (Exception e1) {
            res = "error.datahandler";
            e1.printStackTrace();
        }

        return res;
    }
/**
 * Creating communication channel
 * @param pComID communication ID
 * @param pResource resource descriptor
 * @return
 */
    public String addCommChanel(String pComID, ServiceResourceBean pResource) {
        String res = "servicecall.newcomchanell";

        try {
            long                         cid = Long.parseLong(pComID);
            GuseServiceCommunicationBean tmp = DH.getI().getGuseServiceComm(cid);

            if (tmp != null) {
                DH.getI().persistComResource(tmp, pResource);
            } else {
                res = "error.valid";
            }
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (Exception e1) {
            res = "error.datahandler";
            e1.printStackTrace();
        }

        return res;
    }
/**
 * Deleting communication
 * @param pComID communication ID
 * @param pResourceID resource ID
 * @return
 */
    public String deleteCommChanel(String pComID, String pResourceID) {
        String res = "servicecall.deletecomchanell";

        try {
            long                         cid      = Long.parseLong(pComID);
            long                         rid      = Long.parseLong(pResourceID);
            GuseServiceCommunicationBean tmp      = DH.getI().getGuseServiceComm(cid);
            ServiceResourceBean          resource = DH.getI().getServiceResourceBean(rid);

            if ((tmp != null) && (resource != null)) {
                DH.getI().removeComResource(tmp, resource);
            } else {
                res = "error.valid";
            }
        } catch (NullPointerException e2) {
            res = "error.input";
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (Exception e1) {
            res = "error.datahandler";
            e1.printStackTrace();
        }

        return res;
    }
/**
 * Service refresh
 * @param pServiceID service ID
 * @return
 */
    public String refreshService(String pServiceID) {
        String res = "servicecall.refreshservice";

        try {
            long            sid = Long.parseLong(pServiceID);
            GuseServiceBean gsb = DH.getI().getGuseService(sid);

            if ((gsb.getIurl() != null) && (!"".equals(gsb.getIurl()))) {
                ServicesInitCommandImpl sic = new ServicesInitCommandImpl();

                sic.open(gsb.getIurl());
                sic.get(gsb);
            }
        } catch (NullPointerException e2) {
            res = "error.input";
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }
/**
 * Defining new property for a service
 * @param pServiceID service ID
 * @param pPropertyID property ID
 * @param pKey property key
 * @param pValue property value
 * @return
 */
    public String addServiceProperty(String pServiceID, String pPropertyID, String pKey, String pValue) {
        String res = "";

        try {
            long            sid = Long.parseLong(pServiceID);
            long            pid = Long.parseLong(pPropertyID);
            GuseServiceBean gsb = DH.getI().getGuseService(sid);

            try {
                ServicePropertyBean sbp = DH.getI().getProperty(gsb, pKey);

                DH.getI().deleteServiceProperty(gsb, sbp);
                res = "servicecall.propertyedit";
            } catch (NullPointerException ex) {

                /* new property */
                res = "servicecall.propertyadd";
            }

            if (!"".equals(pValue)) {
                ServicePropertyBean sbp = new ServicePropertyBean();

                sbp.setPropkey(pKey);
                sbp.setPropvalue(pValue);
                DH.getI().persistProperty(gsb, sbp);
            } else {
                res = "servicecall.propertydelete";
            }
        } catch (NullPointerException e2) {
            res = "error.input";
        } catch (NumberFormatException e0) {
            res = "error.input";
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }
/**
 * Importing service properties
 * @param pDSTServiceID target service
 * @param pSRCServiceID source service (at the moment properties can be found here)
 * @return
 */
    public String importServiceProperties(String pDSTServiceID, String pSRCServiceID) {
        String res = "servicecall.propertyimport";

        try {
            Collection<ServicePropertyBean> tmp = DH.getI().getGuseService(pSRCServiceID).get(0).getProperties();
            List<GuseServiceBean>           dst = DH.getI().getGuseService(pDSTServiceID);
            Iterator<ServicePropertyBean>   it  = tmp.iterator();
            ServicePropertyBean             key;

            while (it.hasNext()) {
                key = it.next();
                addServiceProperty("" + dst.get(0).getId(), "0", key.getPropkey(), key.getPropvalue());
            }
        } catch (NullPointerException e2) {
            res = "error.input";
        } catch (Exception e) {
            res = "error.datahandler";
            e.printStackTrace();
        }

        return res;
    }
/**
 * Exporting the configuration to XML string
 * @return xml data
 */
    public String export() {
        StringBuffer res = new StringBuffer();
        List<GuseServiceTypeBean> stypes = getAllServiceTypes();
// service types
        for (GuseServiceTypeBean t : stypes) {
            res.append("\t<type name=\"" + t.getSname() + "\" title=\"" + t.getTxt() + "\"/>\n");
        }

        List<GuseServiceBean>         services = getAllServices();
        Iterator<ServicePropertyBean> it;
        ServicePropertyBean           tmp;

        for (GuseServiceBean t : services) {
            res.append("\t<service type=\"" + t.getTyp().getSname() + "\" url=\"" + t.getUrl() + "\" iurl=\""
                       + t.getIurl() + "\" surl=\"" + ((t.getSurl() == null)
                    ? ""
                    : t.getSurl()) + "\"  >\n");
            it = t.getProperties().iterator();

            while (it.hasNext()) {
                tmp = it.next();
                res.append("\t\t<property name=\"" + tmp.getPropkey() + "\" value=\"" + tmp.getPropvalue()
                           + "\" /> \n");
            }

            res.append("\t</service>\n");
        }

//communication types
        List<GuseServiceCommunicationBean> coms = getAllServiceComs();
        Iterator<ServiceResourceBean>      itsr;
        ServiceResourceBean                tmpsr;

        for (GuseServiceCommunicationBean t : coms) {
            res.append("\t<communication-type  name=\"" + t.getCname() + "\" title=\"" + t.getTxt() + "\">\n");
            itsr = t.getResources().iterator();

            while (itsr.hasNext()) {
                tmpsr = itsr.next();
                res.append("\t\t<communication stype=\"" + tmpsr.getDst().getSname() + "\" from=\""
                           + tmpsr.getSrc().getSname() + "\" comtype=\"" + tmpsr.getCaller() + "\" service=\""
                           + tmpsr.getRes() + "\" />\n");
            }

            res.append("\t</communication-type>\n");
        }

//      resource groups
        List<GroupsTypeBean> gprs = DH.getI().getAllRunnableResourceGroups();

        for (GroupsTypeBean t : gprs) {
            res.append("\t<resource-group name=\"" + t.getName() + "\" type=\"" + t.getType() + "\">\n");

            ArrayList<GroupPropertyBean> props = new ArrayList(t.getProperties().values());

            for (GroupPropertyBean p : props) {
                res.append("\t\t<resource-property name=\"" + p.getName() + "\" value=\"" + p.getValue() + "\" />\n");
            }

            ArrayList<ResourceBean> resources = new ArrayList(t.getResources());

            for (ResourceBean r : resources) {
                res.append("\t\t<resource-runnable site=\"" + r.getSite() + "\" value=\"" + r.getJobmanager()
                           + "\" />\n");
            }

            res.append("\t</resource-group>\n");
        }

        return "<root>\n" + res.toString() + "\n</root>";
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
