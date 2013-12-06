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
 * Log Entry
 */

package hu.sztaki.lpds.dcibridge.service;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * @author krisztian karoczkai 
 */
public class LB {
    public static short INFO=0;
    public static short DEBUG=1;
    public static short WARNING=2;
    public static short EXCEPTION=3;

    private long ts0,ts1;
    private short level;
    private String info,params;

    public LB() {setTs0(System.currentTimeMillis());}

    public LB(String pInfo) {
        setTs0(System.currentTimeMillis());
        setInfo(pInfo);
    }


    public LB(Exception e) {
        setTs0(System.currentTimeMillis());
        setLevel(EXCEPTION);
        String pInfo=e.getMessage()+"\n";
        for(StackTraceElement t:e.getStackTrace())
            pInfo=pInfo.concat(t.getClassName()+"."+t.getMethodName()+"("+t.getFileName()+":"+t.getLineNumber()+")\n");
        setInfo(pInfo);
    }

    public String getInfo() {return info;}

    public void setInfo(String info) {this.info = info;}

    public short getLevel() {return level;}

    public void setLevel(short level) {this.level = level;}

    public long getTs0() {return ts0;}

    public void setTs0(long ts0) {this.ts0 = ts0;}

    public long getTs1() {return ts1;}

    public void setTs1(long ts1) {this.ts1 = ts1;}

    public String getParams() {return params;}

    public void setParams(String params) {this.params = params;}

    public String getTimestamp0(){
        if(ts0>0 &&ts1>0)
            return (new Timestamp(ts0)).toString()+"("+(ts1-ts0)+")";
        else
            return (new Timestamp(ts0)).toString();
    }
}
