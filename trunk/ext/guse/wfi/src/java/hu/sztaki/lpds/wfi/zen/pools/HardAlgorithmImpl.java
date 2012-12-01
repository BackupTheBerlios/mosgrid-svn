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
 * Teljes kepessegu workflow plugin
 */
package hu.sztaki.lpds.wfi.zen.pools;

import hu.sztaki.lpds.logging.Logger;
import hu.sztaki.lpds.wfi.service.zen.Base;
import hu.sztaki.lpds.wfs.utils.Status;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Input;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.Job;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstance;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstanceInput;
import hu.sztaki.lpds.wfi.service.zen.xml.objects.PSInstanceOutput;
import hu.sztaki.lpds.wfi.zen.pools.inf.Algorithm;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author krisztian karoczkai
 */
public class HardAlgorithmImpl implements Algorithm {

    @Override
    public synchronized void action(String pEndJob, long pPID, String pOutput, long pIndex, String pInput, String zenID, String aJob) {
        Logger.getI().workflow(zenID, Logger.INFO, "pre-action end=\"" + pEndJob + "/" + pPID + " " + pOutput + "=" + pIndex + " new=" + aJob + "/" + pInput);
//letrehozando job input portjainak szamossaga
        Hashtable pWorkflowJobs = Base.getZenRunner(zenID).getJobs();
        Job actualJob = (Job) pWorkflowJobs.get(aJob);
        long max = actualJob.getMaxInputCount();
        if (max == 0) {
            return;
        }

        long pstreamIndex = WorkHandler.getI().getInstancePool().getStreamIndex(zenID, pEndJob, pPID, pOutput, pIndex);
        Hashtable<String, Input> inputs = actualJob.getInputs();
//parametrikus ter bejarasa
        if (actualJob.getInput(pInput).getWaiting().equals("one")) {
            Vector<Long> tmpVector = actualJob.getPidsForIndex(pInput, pstreamIndex);
            for (Long t : tmpVector) {
                long i = t.longValue();
                try {
                    if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, aJob, i) == null) {
                        WorkHandler.getI().getInstancePool().createJobInstance(zenID, aJob, i);
                    }
                    if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, aJob, i).getStatus() < 4) {
                        Base.getZenRunner(zenID).addOutputPoolIfNoRescue(WorkHandler.getI().getInstancePool().runnableJob(zenID, aJob, i));
                    }
                } catch (Exception e) {
                }
            }
        } else { //collector
            for (int i = 0; i < max; i++) {
                try {
                    if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, aJob, i) == null) {
                        WorkHandler.getI().getInstancePool().createJobInstance(zenID, aJob, i);
                    }
//                    WorkHandler.getI().getInstancePool().succesfullInput(zenID, actualJob.getName(), i, pInput+"_"+(pPID),0,pIndex);
                    if (WorkHandler.getI().getInstancePool().getPSInstance(zenID, aJob, i).getStatus() < 4) {
                        Base.getZenRunner(zenID).addOutputPoolIfNoRescue(WorkHandler.getI().getInstancePool().runnableJob(zenID, actualJob.getName(), i));
                    }
                } catch (Exception e) {
                    Logger.getI().workflow(zenID, e, "actionerror");/*System.out.println("Collector input:"+i+"/"+max);e.printStackTrace();*/
                }
            }
        }
        Logger.getI().workflow(zenID, Logger.INFO, "post-action end=\"" + pEndJob + "/" + pPID + " " + pOutput + "=" + pIndex + " new=" + aJob + "/" + pInput);
    }

    @Override
    public PSInstance createNewPSInstance(String pZenID, String pJobID, long pPid, boolean pGenInput) throws NullPointerException {
        Logger.getI().workflow(pZenID, Logger.INFO, "pre-create-job job=\"" + pJobID + "\" pid=\"" + pPid + "\" input-gen=\"" + pGenInput + "\"");
        if ((!pGenInput) && WorkHandler.getI().getInstancePool().getPSInstance(pZenID, pJobID, pPid) != null) {
            throw new NullPointerException("letezo peldany");
        }

        PSInstance res;
        if (pGenInput) {
            res = new PSInstanceInput(pJobID, pPid);
        } else {
            res = new PSInstanceOutput(pJobID, pPid);
        }
        Job tmpJob = Base.getZenRunner(pZenID).getJob(pJobID);
        Enumeration enm;
        String key;
        enm = tmpJob.getInputs().keys();
        long tmpInputIndex;
        while (enm.hasMoreElements()) {
            key = "" + enm.nextElement();
            tmpInputIndex = Base.getZenRunner(pZenID).getJob(pJobID).getInputIndexForPID(key, pPid);
            if (tmpJob.getInput(key).getWaiting().equals("one")) {
//normal input                    
                if (tmpJob.getInput(key).getPreJob() == null) {
                    res.addInput(key, "" + tmpInputIndex, -1, true);
                } //csatorna input
                else {
                    String prejob = tmpJob.getInput(key).getPreJob();
                    String preoutput = tmpJob.getInput(key).getPreOutput();

//keresett output leszamolasa a generator streamben
                    Hashtable<Long, Long> ridx = WorkHandler.getI().getInstancePool().getPSInstanceInPreJobInstance(pZenID, prejob, preoutput, tmpInputIndex);

                    Enumeration<Long> enmR = ridx.keys();
                    Long keyR, valueR;
                    while (enmR.hasMoreElements()) {
                        keyR = enmR.nextElement();
                        valueR = ridx.get(keyR);
                        PSInstance tmpPSI = WorkHandler.getI().getInstancePool().getPSInstance(pZenID, tmpJob.getInput(key).getPreJob(), keyR);
                        if (tmpPSI == null) {
                            throw new NullPointerException("INPUT feltetel nem teljesult(" + pZenID + "/" + pJobID + "." + pPid + ")->" + tmpJob.getInput(key).getPreJob() + "." + (tmpInputIndex));
                        }
//                        boolean leavJob = Status.isFinished(tmpPSI.getStatus());//!=null;
                        boolean leavJob = Status.isFinished(tmpPSI.getStatus());//!=null;
                        if (!leavJob) {
                            throw new NullPointerException("INPUT feltetel nem teljesult(" + pZenID + "/" + pJobID + "." + pPid + ")->" + tmpJob.getInput(key).getPreJob() + "." + (tmpInputIndex));
                        }
                        res.addInput(key, "" + keyR, valueR, leavJob);
                    }

                }
            } //collector
            else {
                long preJobCount = Base.getZenRunner(pZenID).getJob(tmpJob.getInput(key).getPreJob()).getCount();
                boolean generatorOutput = Base.getZenRunner(pZenID).getJob(tmpJob.getInput(key).getPreJob()).getOutput(tmpJob.getInput(key).getPreOutput()).getMainCount() > 1;
                int instanceStatus;
                if (generatorOutput) {
                    long psOutputCount, inputIndex = -1;
                    for (int j = 0; j < preJobCount; j++) {
                        if (WorkHandler.getI().getInstancePool().getPSInstance(pZenID, tmpJob.getInput(key).getPreJob(), j) != null) {
                            instanceStatus = WorkHandler.getI().getInstancePool().getPSInstance(pZenID, tmpJob.getInput(key).getPreJob(), j).getStatus();
                            if (Status.isFinished(instanceStatus)) {
                                psOutputCount = WorkHandler.getI().getInstancePool().getPSInstance(pZenID, tmpJob.getInput(key).getPreJob(), j).getOutputCount(tmpJob.getInput(key).getPreOutput());
                                for (int jj = 0; jj < psOutputCount; jj++) {
                                    res.addInput(key + "_" + (++inputIndex), "" + j, jj, false);
                                }
                            }
                            if(!Status.isEndStatus(instanceStatus))
                                throw new NullPointerException("nincs input:");
                            
                        }
                    }
                } else {
                    int coli = 0;
                    int tstatus = 0;
                    for (int j = 0; j < preJobCount; j++) {
                        if (WorkHandler.getI().getInstancePool().getPSInstance(pZenID, tmpJob.getInput(key).getPreJob(), j) != null) {
                            Logger.getI().workflow(pZenID, Logger.INFO, "preinput job=\"" + tmpJob.getInput(key).getPreJob() + "." + j + "\" status=\"" + tstatus + "\"");
                            tstatus = WorkHandler.getI().getInstancePool().getPSInstance(pZenID, tmpJob.getInput(key).getPreJob(), j).getStatus();
                            if ("one".equals(tmpJob.getInput(key).getWaiting())) {
                                if (Status.isFinished(tstatus)) {
                                    res.addInput(key + "_" + coli, "" + j, -1, false);
                                } else if (tstatus < 6) {
                                    throw new NullPointerException("nincs input:");
                                }
                            } else {
                                if (Status.isFinished(tstatus)) {
                                    res.addInput(key + "_" + coli, "" + j, -1, false);
                                } else if (Status.isEndStatus(tstatus)); else {
                                    throw new NullPointerException("nincs input:" + pZenID + "/" + pJobID + "." + pPid + "=>" + tmpJob.getInput(key).getPreJob() + "." + j + "=>" + tstatus);
                                }
                            }
                            coli++;
                        } else {
                            throw new NullPointerException("nincs input:" + pZenID + "/" + pJobID + "." + pPid + "=>" + tmpJob.getInput(key).getPreJob() + "." + j + "=>" + tstatus);
                        }
                    }
                }
            }
        }

//logg        
        Logger.getI().workflow(pZenID, Logger.INFO, "post-create-job job=\"" + pJobID + "\" pid=\"" + pPid + "\" input-gen=\"" + pGenInput + "\"");
        return res;
    }
}
