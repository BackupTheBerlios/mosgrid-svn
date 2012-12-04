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
 * GetJobInstance.java
 * Retrival of the properties of a job instance
 */
package hu.sztaki.lpds.pgportal.servlet.ajaxactions;

import hu.sztaki.lpds.information.com.ServiceType;
import hu.sztaki.lpds.information.local.InformationBase;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.service.base.PortalCacheService;
import hu.sztaki.lpds.pgportal.service.base.data.JobStatusData;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Retrival of the properties of a job instance
 *
 * @author krisztian karoczkai
 */
public class GetJobInstance  extends BASEActions
{

    public GetJobInstance() {
    }

    public String getDispacher(Hashtable pParams) {
        return "/jsp/workflow/jobinstances.jsp";
    }

    public String getOutput(Hashtable pParams) {
        return null;
    }

    /*
     * pParams values(key=value):
     *  j(job)=name of the job
     *  s(status)=0..25,all : selected status
     *  f(fromindex)=list from pid index f
     *  r(range)=list from f to f+r
     *  t(sorttype)= 0:by pid in slices (0-20)ret: 0,1,5,10,11
     *               1:close each oder  (10-)r pieces(5):10,11,14,19,21   
     */
    @Override
    public Hashtable getParameters(Hashtable pParams) {
        Hashtable res = new Hashtable();
        String user = "" + pParams.get("user");
        String detailsworkflow = "" + pParams.get("workflow");
        String detailsruntime = "" + pParams.get("detailsruntime");

        //check paging parameters
        Vector from = new Vector();//from legordulo menu - tol-indexek
        int f = 0;
        int r = 20;
        int jobnum = 0;
        int t = 1; //default sort type
        if (pParams.get("f") != null) {
            try {
                f = Integer.parseInt("" + pParams.get("f"));
            } catch (Exception e) {
            }
        }
        if (pParams.get("r") != null) {
            try {
                r = Integer.parseInt("" + pParams.get("r"));
            } catch (Exception e) {
            }
        }
        if (pParams.get("t") != null) {
            try {
                t = Integer.parseInt("" + pParams.get("t"));
            } catch (Exception e) {
            }
        }
        //System.out.println("fromindex="+f+" range="+r+" type="+t);

        //long starttime=System.currentTimeMillis();
        if (t == 0) {//sort type 0
            if (f % r != 0) {
                f = f - (f % r);
                //System.out.println("corrected fromindex=" + f + " range=" + r);
            }

            ConcurrentHashMap jobs = PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime).getJobStatus("" + pParams.get("j"));
            if (pParams.get("s") == null || "all".equals(pParams.get("s"))) {
                res.put("jobs", sortFromValuesNum0(jobs, f, r));
                //System.out.println("sortedjobs_size:" + ((Vector) res.get("jobs")).size());
                jobnum = jobs.size();
            } else {
                ConcurrentHashMap filteredjobs = new ConcurrentHashMap();
                Enumeration jkeys = jobs.keys();
                while (jkeys.hasMoreElements()) {//paramjobok
                    String jobk = (String) jkeys.nextElement();
                    if (((JobStatusData) jobs.get(jobk)).getStatus()==Integer.parseInt(""+pParams.get("s"))) {
                        filteredjobs.put(jobk, jobs.get(jobk));
                    }
                    //jobnum-nak max pidnek kellene lennie!!
                    int jobki = Integer.parseInt(jobk);
                    if (jobki > jobnum) {
                        jobnum = jobki;
                    }
                }
                res.put("jobs", sortFromValuesNum0(filteredjobs, f, r));
            }
            //settings
            int frpiece = jobnum / r;
            for (int i = 0; i < frpiece; i++) {
                from.add("" + i * r);
            }
            if (jobnum % r > 0) {
                from.add("" + (frpiece * r));
            }
            //System.out.println("fpiece" + frpiece + " from=" + from);

        } else {//sort type 1
            // System.out.println("sort type 1 status:" + user+":"+PortalCacheService.getInstance().getUser(user));
//            System.out.println("sort type 1 status:" + detailsworkflow+":"+PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow));
//            System.out.println("sort type 1 status:" + detailsruntime+PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime));
//            System.out.println("sort type 1 status:" + pParams.get("j")+":"+PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime).getJobStatus("" + pParams.get("j")));
            Vector filteredjobs = new Vector();
            Vector sortvector = new Vector(PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime).getJobStatus("" + pParams.get("j")).keySet());
            //parseint & sort
            for (int i = 0; i < sortvector.size(); i++) {
                int vi = Integer.parseInt("" + sortvector.get(i));
                sortvector.set(i, vi);
            }
            Collections.sort(sortvector);

            jobnum = (Integer) sortvector.lastElement();
            int vi = 0;//vector index//kert statuszhoz tartozik
            int listi = 0;//list index//megjelenitett listahoz tartozik

            Enumeration jkeys = sortvector.elements();
            if (pParams.get("s") == null || pParams.get("s").equals("all")) {
                while (jkeys.hasMoreElements()) {
                    int jobk = (Integer) jkeys.nextElement();
                    //System.out.println(" |jobk=" + jobk + " SELECTEDSTATUS");
                    if (vi % r == 0) {//add page
                        from.add("" + jobk);
                        //System.out.println(" |vi=" + vi + " listi=" + listi);
                    }
                    if (jobk >= f && listi < r) {//add jobinstance to list
                        filteredjobs.add(PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime).getJobStatus("" + pParams.get("j")).get("" + jobk));
                        //System.out.println(" |jobk=" + jobk + " listaba---------");
                        listi++;
                    }
                    vi++;
                }
            } else {//filter status
                while (jkeys.hasMoreElements()) {
                    int jobk = (Integer) jkeys.nextElement();
                    //System.out.println(" |jobk=" + jobk);
                    if (((JobStatusData) PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime).getJobStatus("" + pParams.get("j")).get("" + jobk)).getStatus()==Integer.parseInt(""+pParams.get("s"))) {
                        //System.out.println(" |jobk=" + jobk + " SELECTEDSTATUS");
                        if (vi % r == 0) {//add page
                            from.add("" + jobk);
                            //System.out.println(" |vi=" + vi + " listi=" + listi);
                        }
                        if (jobk >= f && listi < r) {//add jobinstance to list
                            filteredjobs.add(PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getRuntime(detailsruntime).getJobStatus("" + pParams.get("j")).get("" + jobk));
                            //System.out.println(" |jobk=" + jobk + " listaba---------");
                            listi++;
                        }
                        vi++;
                    }
                }
            }
            res.put("jobs", filteredjobs);
        }
        //System.out.println("getWorkflowInstanceJobs sort time ms:"+(System.currentTimeMillis()-starttime));

        res.put("jobnum", jobnum);
        res.put("from", from);
        res.put("fromindex", f);
        res.put("range", r);
        res.put("sorttype", t);
        res.put("status", pParams.get("s"));
        res.put("job", pParams.get("j"));
        res.put("rtid", detailsruntime);
        res.put("user", user);
        res.put("workflow", detailsworkflow);
        res.put("portal", PropertyLoader.getInstance().getProperty("service.url"));
        if(PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getStorageID()==null){
            ServiceType stTMP=InformationBase.getI().getService("storage","portal",new Hashtable(),new Vector());
            PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).setStorageID(stTMP.getServiceUrl());
        }
        res.put("storageurl", PortalCacheService.getInstance().getUser(user).getWorkflow(detailsworkflow).getStorageID());
        return res;
    }

    private Vector sortFromValuesNum0(ConcurrentHashMap jobi, int fromindex, int range) {
        Vector ret = new Vector();
        try {
            Hashtable h = new Hashtable();
            Enumeration pids = jobi.keys();
            while (pids.hasMoreElements()) {
                try {
                    h.put("" + pids.nextElement(), "");
                } catch (Exception e) {
                }
            }
            for (int i = fromindex; i < fromindex + range; i++) {
                if (h.containsKey("" + i)) {
                    ret.add(jobi.get("" + i));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}
