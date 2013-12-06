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

package hu.sztaki.lpds.submitter.grids;

import hu.sztaki.lpds.dcibridge.service.Base;
import hu.sztaki.lpds.dcibridge.service.Job;
import hu.sztaki.lpds.submitter.grids.gt.gtStatusListener;
import hu.sztaki.lpds.submitter.grids.gt.work_dci;
import java.util.HashMap;


public class Grid_gt2 extends Middleware{
    work_dci gthandler;

/**
 * constructor
 * @param pCount index of thread handling the middleware
 */
    public Grid_gt2() throws Exception{
        THIS_MIDDLEWARE=Base.MIDDLEWARE_GT2;
        threadID++;
        setName("guse/dci-bridge:Middleware handler(gt2) - "+threadID);
        gthandler = new work_dci("gt2");
    }

    @Override
    protected void abort(Job pJob) throws Exception {
        gthandler.abort(pJob);
    }

    @Override
    protected void submit(Job pJob) throws Exception {
        gthandler.submit(pJob);
    }

    @Override
    protected void getOutputs(Job pJob) throws Exception {
        gthandler.getOutput(pJob);

    }

    @Override
    protected void getStatus(Job pJob) throws Exception {
        ((gtStatusListener)  (((HashMap)pJob.getMiddlewareObj()).get(work_dci.STATUSLISTENER))).getStatus(pJob);
    }

//    @Override
//    protected void abort(Job pJob) throws Exception {
//        gtHandler.getI().abort(pJob);
//    }
//
//    @Override
//    protected void submit(Job pJob) throws Exception {
//        gtHandler.getI().submit(pJob,"gt2");
//    }
//
//    @Override
//    protected void getOutputs(Job pJob) throws Exception {
//
//    }
//
//    @Override
//    protected void getStatus(Job pJob) throws Exception {
//        gtHandler.getI().getStatus(pJob);
//    }

}
