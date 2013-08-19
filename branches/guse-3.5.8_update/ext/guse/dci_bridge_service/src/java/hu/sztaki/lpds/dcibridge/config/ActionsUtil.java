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
 * Completion of configuration modifications 
 */

package hu.sztaki.lpds.dcibridge.config;

import dci.data.Configure.*;
import dci.data.Item;
import dci.data.Item.*;
import hu.sztaki.lpds.dcibridge.service.Base;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 * @author krisztian karoczkai
 */
public class ActionsUtil {


/**
 * Existing BOINC grid data modification 
 * @param request
 */
    public static void editBoinc(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_BOINC,grid);
        Boinc tmp=grp.getBoinc();// Configure of selected grid


        if(!"".equals(request.getParameter("pwsdl"))){
                grp.setName(request.getParameter("pboincname"));
        grp.setEnabled("1".equals(request.getParameter("penabled")));
                tmp.setId(request.getParameter("pboincid"));
                tmp.setWsdl(request.getParameter("pwsdl"));
                Enumeration<String> enm=request.getParameterNames();
                String key,jobName;
                while(enm.hasMoreElements()){
                    key=enm.nextElement();
                    if(key.startsWith("pstate_")){
                        jobName=key.substring(7);
                        for(Boinc.Job job:tmp.getJob()){
                            if(jobName.equals(job.getName()))
                                //job.setState(request.getParameter(key));
                                job.setState(true);/*@ToDo ne alapbol true legyen*/
                        }
                    }
                }
            
        }
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

/**
 * Existing GBAC grid data modification
 * @param request
 */
    public static void editGbac(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_GBAC,grid);
        Gbac tmp=grp.getGbac();// Configure of selected grid

        if(!"".equals(request.getParameter("pwsdl"))){
                grp.setName(request.getParameter("pboincname"));
                grp.setEnabled("1".equals(request.getParameter("penabled")));
                tmp.setId(request.getParameter("pboincid"));
                tmp.setWsdl(request.getParameter("pwsdl"));
                tmp.setRundescurl(request.getParameter("pxml"));
        }
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }



/**
 * Existing GT2 grid data modification 
 * @param request
 */
    public static void editGt2(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        String name=request.getParameter("pgt2name");
        Item grp=Conf.getItem(Base.MIDDLEWARE_GT2,grid);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Gt2 tmp=grp.getGt2(); // Configure of selected grid
        

        if(!grid.equals(name))
            if(Conf.isExistItemInMiddleware(Base.MIDDLEWARE_GT2, name)){
                    ((ArrayList<String>)request.getSession().getAttribute("error")).add("error.gt2.name");
                    return ;
            }
            else grp.setName(request.getParameter("pgt2name"));

        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));

        Enumeration<String> enm=request.getParameterNames();
        String key;
        String[] resource;
        List<Gt2.Resource> hosts=new ArrayList<Gt2.Resource>();
        List<String> jobmanagers;
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            if(key.startsWith("pdelete_")){
                resource=request.getParameter(key).split("/");
                for(Gt2.Resource host:tmp.getResource()){
                    if(host.getHost().equals(resource[0])){
                        jobmanagers=new ArrayList<String>();
                            for(String jobmanager:host.getJobmanager())
                                if(jobmanager.equals(resource[1])) jobmanagers.add(jobmanager);
                            for(String l:jobmanagers) host.getJobmanager().remove(l);
                            if(host.getJobmanager().size()==0) hosts.add(host);
                    }
                }
                for(Gt2.Resource l:hosts) tmp.getResource().remove(l);
            }
        }
        for(int i=0;i<5;i++){
            if((request.getParameter("phost_"+i)!=null)&&(request.getParameter("pjobmanager_"+i)!=null))
                if(!"".equals(request.getParameter("phost_"+i)) && !"".equals(request.getParameter("pjobmanager_"+i))) {
                    Gt2.Resource r=new Gt2.Resource();
                    r.setHost(request.getParameter("phost_"+i));
                    r.getJobmanager().add(request.getParameter("pjobmanager_"+i));
                    tmp.getResource().add(r);
                }
        }
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));

    }
    

/**
 * Existing GT4 grid data modification 
 * @param request
 */
    public static void editGt4(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_GT4,grid);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Gt4 tmp=grp.getGt4(); // Configure of selected grid


        grp.setName(request.getParameter("pgt4name"));
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        Enumeration<String> enm=request.getParameterNames();
        String key;
        String[] resource;
        List<Gt4.Resource> hosts=new ArrayList<Gt4.Resource>();
        List<String> jobmanagers;
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            if(key.startsWith("pdelete_")){
                resource=request.getParameter(key).split("/");
                for(Gt4.Resource host:tmp.getResource()){
                    if(host.getHost().equals(resource[0])){
                        jobmanagers=new ArrayList<String>();
                        for(String jobmanager:host.getJobmanager())
                            if(jobmanager.equals(resource[1])) jobmanagers.add(jobmanager);
                        for(String l:jobmanagers) host.getJobmanager().remove(l);
                            if(host.getJobmanager().size()==0) hosts.add(host);
                    }
                }
                for(Gt4.Resource l:hosts)
                    tmp.getResource().remove(l);
            }
        }
        for(int i=0;i<5;i++){
            if((request.getParameter("phost_"+i)!=null)&&(request.getParameter("pjobmanager_"+i)!=null))
            if(!"".equals(request.getParameter("phost_"+i)) && !"".equals(request.getParameter("pjobmanager_"+i))) {
                Gt4.Resource r=new Gt4.Resource();
                r.setHost(request.getParameter("phost_"+i));
                r.getJobmanager().add(request.getParameter("pjobmanager_"+i));
                tmp.getResource().add(r);
            }
        }
    }

    public static void editGlite(HttpServletRequest request){
        String vo=(String)request.getParameter("editing"); //Name of selected vo
        Item grp=Conf.getItem(Base.MIDDLEWARE_GLITE,vo);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Glite tmp=grp.getGlite(); // Configure of selected vo

        grp.setName(request.getParameter("pglitename"));
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        tmp.setLfc(request.getParameter("plfc"));
        tmp.setWms(request.getParameter("pwms"));
        tmp.setBdii(request.getParameter("pbdii"));
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

    public static void editArc(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_ARC,grid);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Arc tmp=grp.getArc(); // Configure of selected grid

        grp.setName(request.getParameter("parcname"));
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        tmp.setConfigpath(request.getParameter("pconfig"));
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

    public static boolean editService(HttpServletRequest request){
        String name=request.getParameter("pservicename");//name of service type
        Item grp=Conf.getItem(Base.MIDDLEWARE_SERVICE, name);
        Service tmp=new Service();
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        tmp.setUrl("");
        grp.setService(tmp);
        return true;
    }

    public static void editUnicore(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_UNICORE,grid);
        Unicore tmp=grp.getUnicore();

//        java.lang.System.out.println("unicore grid("+grid+")"+grp);
//        java.lang.System.out.println("unicore grid("+grid+")"+tmp);

        grp.setName(request.getParameter("punicorename"));
        grp.setEnabled("1".equals(request.getParameter("penabled")));


        tmp.setKeystore(request.getParameter("pkeystore"));
        tmp.setKeypass(request.getParameter("pkeypass"));
        tmp.setKeyalias(request.getParameter("pkeyalias"));
        tmp.setSubjectdn(request.getParameter("psubjectdn"));
        tmp.setTruststore(request.getParameter("ptruststore"));
        tmp.setTrustpass(request.getParameter("ptrustpass"));

        grp.setUnicore(tmp);
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

    public static void editGelmca(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_GEMLCA,grid);

        grp.setName(request.getParameter("pgelmcaname"));
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

    public static void editLocal(HttpServletRequest request){
        String grid=(String)request.getParameter("editing"); //Name of selected grid
        Item grp=Conf.getItem(Base.MIDDLEWARE_LOCAL,grid);

        grp.setName(request.getParameter("plocalname"));
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        if(grp.getForward()==null)grp.setForward(new Forward());
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }


/**
 * Editing configure data of a Pbs cluster
 * cluster name in request.getParameter("list")
 * @param request http request
 */
    public static void editPbs(HttpServletRequest request){
        String cluster=(String)request.getParameter("editing"); //Name of selected cluster
        Item grp=Conf.getItem(Base.MIDDLEWARE_PBS,cluster);
        grp.setName(request.getParameter("ppbsname"));
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Pbs tmp=grp.getPbs(); // Configure of selected cluster
        List<String> tmpQueues=new ArrayList<String>(); // temporary list for deleting, adding queue
        Enumeration<String> enm;
        String key,value;
/*new queues*/
        enm=request.getParameterNames();
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            if(key.startsWith("pnew_queue_")){
                value=request.getParameter(key);
                if(!"".equals(value))
                if(!Conf.isQueueInPbs(tmp, value))
                    tmp.getQueue().add(value);
            }
        }
/*delete queues*/
        for(String t:tmp.getQueue()){
            enm=request.getParameterNames();
            while(enm.hasMoreElements()){
                key=enm.nextElement();
                if(key.startsWith("pdelete_queue_"))
                if(request.getParameter(key).equals(t))
                    tmpQueues.add(t);
            }
        }
        for(String t:tmpQueues) tmp.getQueue().remove(t);
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

/**
 * Editing configure data of a Lsf cluster
 * cluster name in request.getParameter("list")
 * @param request http request
 */
    public static void editLsf(HttpServletRequest request){
        String cluster=(String)request.getParameter("editing"); //Name of selected cluster
        Item grp=Conf.getItem(Base.MIDDLEWARE_LSF,cluster);
        grp.setName(request.getParameter("plsfname"));
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Lsf tmp=grp.getLsf(); // Configure of selected cluster
        List<String> tmpQueues=new ArrayList<String>(); // temporary list for deleting, adding queue
        Enumeration<String> enm;
        String key,value;
/*new queues*/
        enm=request.getParameterNames();
        while(enm.hasMoreElements()){
            key=enm.nextElement();
            if(key.startsWith("pnew_queue_")){
                value=request.getParameter(key);
                if(!"".equals(value))
                if(!Conf.isQueueInLsf(tmp, value))
                    tmp.getQueue().add(value);
            }
        }
/*delete queues*/
        for(String t:tmp.getQueue()){
            enm=request.getParameterNames();
            while(enm.hasMoreElements()){
                key=enm.nextElement();
                if(key.startsWith("pdelete_queue_"))
                if(request.getParameter(key).equals(t))
                    tmpQueues.add(t);
            }
        }
        for(String t:tmpQueues) tmp.getQueue().remove(t);
        grp.getForward().setUsethis((request.getParameter("pusethis")!=null)?"true":"false");
        grp.getForward().getWsdl().clear();
        grp.getForward().getWsdl().add(request.getParameter("pwsdl"));
    }

    public static boolean editEdgi (HttpServletRequest request) {
        String editedEdgiName = (String) request.getParameter("editing");
        String newName = request.getParameter("pedginame");
        String newUrl = request.getParameter("pedgiurl");

        //java.lang.System.out.println("EDGI: name of the edited edgi: " + editedEdgiName);

        Item edgiElement = Conf.getItem (Base.MIDDLEWARE_EDGI, editedEdgiName);

        edgiElement.setName(newName);
        edgiElement.getEdgi().setUrl(newUrl);

        edgiElement.setEnabled("1".equals(request.getParameter("penabled")));

        edgiElement.getForward().getWsdl().clear();
        edgiElement.getForward().getWsdl().add(request.getParameter("pwsdl"));

        //java.lang.System.out.println("EDGI: new name: " + newName + ", new URL: " + newUrl);

        return true;
    }
	
	public static boolean newService(HttpServletRequest request){
        String name=request.getParameter("pservicename");//name of service type
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_SERVICE, name);
        Service tmp=new Service();
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        tmp.setUrl("");
        grp.setService(tmp);
        return true;
    }

/**
 * New BPS cluster getting up
 * @param request
 */
    public static boolean newPbs(HttpServletRequest request){
        String name=request.getParameter("ppbsname");//name of cluster
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_PBS, name);
        Pbs tmp=new Pbs();
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        for(int i=0;i<10;i++){
            if((request.getParameter("pqueue_"+i)!=null))
            if(!"".equals(request.getParameter("pqueue_"+i)))
                tmp.getQueue().add(request.getParameter("pqueue_"+i));
        }
        grp.setPbs(tmp);
        return true;
    }

/**
 * New LSF cluster getting up
 * @param request
 */
    public static boolean newLsf(HttpServletRequest request){
        String name=request.getParameter("plsfname");//name of cluster
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_LSF, name);
        Lsf tmp=new Lsf();
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        for(int i=0;i<10;i++){
            if((request.getParameter("pqueue_"+i)!=null))
            if(!"".equals(request.getParameter("pqueue_"+i)))
                tmp.getQueue().add(request.getParameter("pqueue_"+i));
        }
        grp.setLsf(tmp);
        return true;
    }

/**
 * New local resource getting up
 * @param request
 */
    public static boolean newLocal(HttpServletRequest request){
        String name=request.getParameter("plocalname");
        Item tmp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_LOCAL, name);
        tmp.setName(name);
        tmp.setEnabled("1".equals(request.getParameter("penabled")));
        return true;
    }

/**
 * New GAE provider getting up
 * @param request
 */
    public static boolean newGae(HttpServletRequest request){
        String name=request.getParameter("pgaename");
        Item tmp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_GAE, name);
        tmp.setName(name);
        tmp.setEnabled("1".equals(request.getParameter("penabled")));
        return true;
    }


/**
 * New ARC server getting up
 * @param request
 */
    public static boolean newArc(HttpServletRequest request){
        String name=request.getParameter("parcname");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_ARC, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Arc tmp=new Arc();
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        tmp.setConfigpath(request.getParameter("pconfig"));
        grp.setArc(tmp);
        return true;
    }

/**
 * New UNICORE server getting up
 * @param request
 */
    public static boolean newUnicore(HttpServletRequest request){
        String name=request.getParameter("punicorename");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_UNICORE, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Unicore tmp=new Unicore();
       
        tmp.setKeystore(request.getParameter("pkeystore"));
        tmp.setKeypass(request.getParameter("pkeypass"));
        tmp.setKeyalias(request.getParameter("pkeyalias"));
        tmp.setSubjectdn(request.getParameter("psubjectdn"));
        tmp.setTruststore(request.getParameter("ptruststore"));
        tmp.setTrustpass(request.getParameter("ptrustpass"));
        
        grp.setUnicore(tmp);
        return true;
}

/**
 * New GEMLCA server getting up
 * @param request
 */
    public static boolean newGelmca(HttpServletRequest request){
        String name=request.getParameter("pgelmcaname");
        Item tmp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_GEMLCA, name);
        tmp.setName(name);
        tmp.setEnabled("1".equals(request.getParameter("penabled")));
        return true;
    }

/**
 * New GT2 VO getting up
 * @param request
 */
    public static boolean newGt2(HttpServletRequest request){
        String name=request.getParameter("pgt2name");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_GT2, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Gt2 tmp=new Gt2();
        grp.setName(name);
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        for(int i=0;i<10;i++){
            if((request.getParameter("phost_"+i)!=null)&&(request.getParameter("pjobmanager_"+i)!=null))
            if(!"".equals(request.getParameter("phost_"+i)) && !"".equals(request.getParameter("pjobmanager_"+i))) {
                Gt2.Resource r=new Gt2.Resource();
                r.setHost(request.getParameter("phost_"+i));
                r.getJobmanager().add(request.getParameter("pjobmanager_"+i));
                tmp.getResource().add(r);
            }
        }
        grp.setGt2(tmp);
        return true;
    }

/**
 * New GT4 VO getting up 
 * @param request
 */
    public static boolean newGt4(HttpServletRequest request){
        String name=request.getParameter("pgt4name");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_GT4, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Gt4 tmp=new Gt4();
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        for(int i=0;i<10;i++){
            if((request.getParameter("phost_"+i)!=null)&&(request.getParameter("pjobmanager_"+i)!=null))
            if(!"".equals(request.getParameter("phost_"+i)) && !"".equals(request.getParameter("pjobmanager_"+i))) {
                Gt4.Resource r=new Gt4.Resource();
                r.setHost(request.getParameter("phost_"+i));
                r.getJobmanager().add(request.getParameter("pjobmanager_"+i));
                tmp.getResource().add(r);
            }
        }
        grp.setGt4(tmp);
        return true;
    }


/**
 * New gLite VO getting up
 * @param request
 */
    public static boolean newGlite(HttpServletRequest request){
        String name=request.getParameter("pglitename");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_GLITE, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Glite tmp=new Glite();
        tmp.setAccessdata(request.getParameter("paccessdata"));
        tmp.setType(request.getParameter("ptype"));
        tmp.setLfc(request.getParameter("plfc"));
        grp.setGlite(tmp);
        return true;
    }


/** @ToDo
 * New BOINC grid getting up 
 * @param request
 */
    public static boolean newBoinc(HttpServletRequest request){
        String name=request.getParameter("pboincname");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_BOINC, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Boinc tmp=new Boinc();

        tmp.setId(request.getParameter("pboincid"));
        tmp.setWsdl(request.getParameter("pwsdl"));
        grp.setBoinc(tmp);
        return true;
    }

/**
 * New GBAC grid getting up 
 * @param request
 */
    public static boolean newGbac(HttpServletRequest request){
        String name=request.getParameter("pboincname");
        Item grp=Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_GBAC, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));
        Gbac tmp=new Gbac();

        tmp.setId(request.getParameter("pboincid"));
        tmp.setWsdl(request.getParameter("pwsdl"));
        tmp.setRundescurl(request.getParameter("pxml"));
        grp.setGbac(tmp);
        return true;
    }

/**
 * Adding new EDGI provider EDGI
 * @param request
 */
    public static boolean newEdgi(HttpServletRequest request){
        String name = request.getParameter("pedginame");
        Item grp = Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_EDGI, name);
        grp.setName(name);
        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Edgi tmp = new Edgi();
        String url = request.getParameter("pedgiurl");
        tmp.setUrl(url);
        grp.setEdgi(tmp);

//        java.lang.System.out.println("New EDGI instance " + name + " " + url);

        return true;
    }


/**
 * Add a new CloudBroker
 * @param request
 */
    public static boolean newCloudBroker (HttpServletRequest request){
        String name = request.getParameter("pcloudbrokername");
        Item grp = Conf.getItemNewInstanceIfNotExist(Base.MIDDLEWARE_CLOUDBROKER, name);

        grp.setName(name);

        grp.setEnabled("1".equals(request.getParameter("penabled")));

        Cloudbroker tmp = new Cloudbroker();
        tmp.setUrl(request.getParameter("pcloudbrokerurl"));
        tmp.setUser(request.getParameter("pcloudbrokeruser"));
        tmp.setPassword(request.getParameter("pcloudbrokerpassword"));

        grp.setCloudbroker(tmp);

//        java.lang.System.out.println("New CloudBroker instance " + name + " " + request.getParameter("pcloudbrokerurl"));

        return true;
    }
	
/**
 * DCI-Bridge system settings
 * @param request
 */
    public static void setSystem(HttpServletRequest request){
        if(!"".equals(request.getParameter("metabroker"))){
                Conf.getS().setMetabroker(request.getParameter("metabroker"));
                try{Base.getI().getDWLThread().createMBClient();}
                catch(Exception e){e.printStackTrace();/*ToDO*/}
        }
        Conf.getS().setPath(request.getParameter("workdir"));
        Base.getI().setStatus("1".equals(request.getParameter("status")));
    }


}
