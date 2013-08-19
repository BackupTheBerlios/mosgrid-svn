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
 * ssh service
 */
package hu.sztaki.lpds.submitter.service.ssh;

import com.jcraft.jsch.*;
import hu.sztaki.lpds.dcibridge.service.Base;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class sshService {

    private static sshService instance = null;
    private static ConcurrentHashMap<String, Session> session = new ConcurrentHashMap();

    private static String keycachepath = null;

    public static sshService getI() {
        if (instance == null) {
            if (keycachepath == null) {
                keycachepath = Base.getI().getPath() + "keycache/";
                File kp = new File(keycachepath);
                if (!kp.exists()) {
                    kp.mkdir();
                }
            }
            instance = new sshService();
        }
        return instance;
    }

    /**
     * Reads ssh User Name from keyfile and puts the key into keycache dir.
     * @param inf key file from jobs dir
     * @param guseuserid
     * @param sshhost
     * @return sshUserName from the first line of the key (user=sshUserName)
     * @throws java.io.FileNotFoundException
     */
    public String setSshKey(String inf, String guseuserid, String sshhost) throws FileNotFoundException, IOException {
        BufferedReader input = new BufferedReader(new FileReader(inf));
        BufferedWriter output = null;
        String sshuser = null;
        try {
            String line = null;
            while ((line = input.readLine()) != null) {
                //System.out.println(line);
                if (sshuser == null && line.contains("user=")) {
                    String[] u = line.split("=");
                    sshuser = u[1].trim();
                    String outf = keycachepath + guseuserid + sshuser + sshhost;
                    output = new BufferedWriter(new FileWriter(outf));
                    //System.out.println("SSHKEY user:" + sshuser);
                } else if (sshuser != null) {
                    output.write(line + "\n");
                }
            }
        } finally {
            try {
                input.close();
            } catch (Exception e) {
            }
            try {
                output.close();
            } catch (Exception e) {
            }
        }
        return sshuser;
    }  
    
    public String sshExec(String guseuser, String user, String host, String cmd) throws IOException, JSchException, Exception {
        String ret = "";
        int exitst = -1;
        authUser(guseuser, user, host);
        Channel channel = session.get(guseuser + user + host).openChannel("exec");
        ((ChannelExec) channel).setCommand(cmd);
        //channel.setInputStream(null);
        //((ChannelExec) channel).setErrStream(System.err);
        InputStream in = channel.getInputStream();
        channel.connect();
        byte[] tmp = new byte[1024];
        while (true) {
            while (in.available() > 0) {
                int i = in.read(tmp, 0, 1024);
                if (i < 0) {
                    break;
                }
                ret = ret.concat(new String(tmp, 0, i));
            }
            if (channel.isClosed()) {
                exitst = channel.getExitStatus();
                //System.out.println("exit-status: " + channel.getExitStatus());
                break;
            }
            try {
                Thread.sleep(10);
            } catch (Exception ee) {
            }
        }
        channel.disconnect();
        if (exitst != 0) {
            throw new Exception("exit-status: " + exitst);
        }
        return ret;
    }

    public boolean scpTo(String guseuser, String user, String host, String lfile, String rfile) throws Exception {
        FileInputStream fis = null;
        try {
            authUser(guseuser, user, host);
            Channel channel = session.get(guseuser + user + host).openChannel("exec");
            // exec 'scp -t rfile' remotely
            String command = "scp -p -t " + rfile;
            ((ChannelExec) channel).setCommand(command);
            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            channel.connect();
            if (checkAck(in) != 0) {
                return false;//System.exit(0);
            }
            // send "C0644 filesize filename", where filename should not include '/'
            long filesize = (new File(lfile)).length();
            command = "C0644 " + filesize + " ";
            if (lfile.lastIndexOf('/') > 0) {
                command += lfile.substring(lfile.lastIndexOf('/') + 1);
            } else {
                command += lfile;
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
            if (checkAck(in) != 0) {
                return false;//System.exit(0);
            }
            // send a content of lfile
            fis = new FileInputStream(lfile);
            byte[] buf = new byte[1024];
            while (true) {
                int len = fis.read(buf, 0, buf.length);
                if (len <= 0) {
                    break;
                }
                out.write(buf, 0, len); //out.flush();
            }
            fis.close();
            fis = null;
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            if (checkAck(in) != 0) {
                return false;//System.exit(0);
            }
            out.close();
            channel.disconnect();
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception ee) {
            }
            throw new Exception(e);
        }
        return true;
    }

    public boolean scpFrom(String guseuser, String user, String host, String rfile, String lfile) throws Exception {
        FileOutputStream fos = null;
        try {
            String prefix = null;
            if (new File(lfile).isDirectory()) {
                prefix = lfile + File.separator;
            }
            authUser(guseuser, user, host);
            Channel channel = session.get(guseuser + user + host).openChannel("exec");
            // exec 'scp -f rfile' remotely
            String command = "scp -f " + rfile;
            ((ChannelExec) channel).setCommand(command);
            // get I/O streams for remote scp
            OutputStream out = channel.getOutputStream();
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] buf = new byte[1024];
            // send '\0'
            buf[0] = 0;
            out.write(buf, 0, 1);
            out.flush();
            while (true) {
                int c = checkAck(in);
                if (c != 'C') {
                    break;
                }
                // read '0644 '
                in.read(buf, 0, 5);
                long filesize = 0L;
                while (true) {
                    if (in.read(buf, 0, 1) < 0) {
                        // error
                        break;
                    }
                    if (buf[0] == ' ') {
                        break;
                    }
                    filesize = filesize * 10L + (long) (buf[0] - '0');
                }
                String file = null;
                for (int i = 0;; i++) {
                    in.read(buf, i, 1);
                    if (buf[i] == (byte) 0x0a) {
                        file = new String(buf, 0, i);
                        break;
                    }
                }
                //System.out.println("filesize="+filesize+", file="+file);
                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                // read a content of lfile
                fos = new FileOutputStream(prefix == null ? lfile : prefix + file);
                int foo;
                while (true) {
                    if (buf.length < filesize) {
                        foo = buf.length;
                    } else {
                        foo = (int) filesize;
                    }
                    foo = in.read(buf, 0, foo);
                    if (foo < 0) {
                        // error
                        break;
                    }
                    fos.write(buf, 0, foo);
                    filesize -= foo;
                    if (filesize == 0L) {
                        break;
                    }
                }
                fos.close();
                fos = null;
                if (checkAck(in) != 0) {
                    return false;//System.exit(0);
                }
                // send '\0'
                buf[0] = 0;
                out.write(buf, 0, 1);
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            System.out.println(e);
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ee) {
            }
            throw new Exception(e);
        }
        return true;
    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

    private void authUser(String guseuser, String user, String host) throws JSchException, Exception {
        JSch jsch = new JSch();
        String gUserHost = guseuser + user + host;
        if (!session.containsKey(gUserHost)) {//user auth
            try {
                jsch.addIdentity(keycachepath + gUserHost);//("/home/user/pbs/.ssh.id_rsa");
                session.put(gUserHost, jsch.getSession(user, host, 22));
                UserInfo ui = new MyUserInfo();
                session.get(gUserHost).setUserInfo(ui);
                session.get(gUserHost).connect();
                System.out.println("first auth success:" + user + " " + host);
            } catch (Exception e) {// if first auth fails, delete the key file from keycache
                session.remove(gUserHost);
                File keyf = new File(keycachepath + gUserHost);
                if (keyf.exists()) {
                    keyf.delete();
                }
                throw new JSchException("Could not authenticate to server " + host + " with user " + user + ". ", e);
            }
        }
        if (!session.get(gUserHost).isConnected()) {
            try {
                jsch.addIdentity(keycachepath + gUserHost);
                session.put(gUserHost, jsch.getSession(user, host, 22));
                UserInfo ui = new MyUserInfo();
                session.get(gUserHost).setUserInfo(ui);
                session.get(gUserHost).connect();
            } catch (JSchException je) {
                throw new JSchException("Could not authenticate to server " + host + " with user " + user + ". The connection is broken. ", je);
            }
        }
    }

    public static class MyUserInfo implements UserInfo {

        public String getPassword() {
            return null;
        }

        public String getPassphrase() {
            //System.out.println("getPassphrase");
            return "";
        }

        public boolean promptPassword(String string) {
            //System.out.println("promptPassword:" + string);
            return false;
        }

        public boolean promptPassphrase(String string) {
            //System.out.println("promptPassphrase:" + string);
            return false;
        }

        public boolean promptYesNo(String string) {
            //System.out.println("promptYesNo:" + string);
            return true;
        }

        public void showMessage(String string) {
            //System.out.println("showMessage:" + string);
        }
    }
}
