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
package hu.sztaki.lpds.pgportal.portlets.admin;

import java.sql.Connection;
import java.sql.SQLException;
import hu.sztaki.lpds.information.local.PropertyLoader;
import hu.sztaki.lpds.pgportal.portlet.GenericWSPgradePortlet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import javax.portlet.PortletRequestDispatcher;

/**
 * QuotaSettingsPortlet Portlet Class
 */
public class QuotaSettingsPortlet extends GenericWSPgradePortlet {

    @Override
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        if(!isInited())
        {
            getPortletContext().getRequestDispatcher("/WEB-INF/jsp/error/init.jsp").include(request, response);
            return;
        }

        request.setAttribute("defquota", getDefaultQuota());
        try {
            request.setAttribute("users", getUsersQuota());
        } catch (Exception e) {
            request.setAttribute("msgerr", " " + e.getMessage());
        }
        PortletRequestDispatcher dispatcher = getPortletContext().getRequestDispatcher("/jsp/admin/quotalist.jsp");
        dispatcher.include(request, response);
    }

    public void doSaveDef(ActionRequest request, ActionResponse response) throws PortletException {


        //System.out.println("doSaveDef QUOTA");
        try {
            int sqota = Integer.parseInt(request.getParameter("defquota"));
            saveQuota("", sqota);//save default quota
            request.setAttribute("msg", "Quota for users successfully set.");
        } catch (NumberFormatException ne) {
            request.setAttribute("msg", "Quota must be integer number!");
        } catch (Exception e) {
            request.setAttribute("msg", "Save failed!");
        }

    }

    public void doSaveUsers(ActionRequest request, ActionResponse response) throws PortletException {
        //System.out.println("doSaveUsers QUOTA");
        String msg = "";
        Iterator u = getUserIDs().iterator();
        while (u.hasNext()) {
            String usr = u.next().toString();
            String quota = request.getParameter("q" + usr);
            if (quota != null) {
                try {
                    saveQuota(usr, Integer.parseInt(quota));
                } catch (Exception e) {
                    msg = msg.concat(usr + " ");
                }
            }
        }
        if (msg.length() != 0) {
            request.setAttribute("msg", "Quota must be integer number! Not saved userids: " + msg);
        } else {
            request.setAttribute("msg", "Saved successfully. ");
        }
    }

    private String getDefaultQuota() {
        try {
            String q = readQuota("");
            return q;
        } catch (Exception e) {
            try {
                saveQuota("", 5000);//save default quota
                return "5000";
            } catch (Exception es) {
                es.printStackTrace();
            }
        }
        return "Error";
    }

    private void saveQuota(String user, int quota) throws IOException {
        File qdir = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user);
        if (!qdir.exists()) {
//            System.out.println("mkdirs for user:" + user);
            qdir.mkdirs();
        }
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user + "/.quota")));
        pw.print("" + quota);
        pw.close();

    }

    private String readQuota(String user) throws IOException {
        StringBuffer s = new StringBuffer();
        BufferedReader br = null;
        br = new BufferedReader(new FileReader(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "users/" + user + "/.quota"));
        String line;
        while ((line = br.readLine()) != null) {
            s.append(line);
        }
        br.close();
        return s.toString();
    }

    private Vector getUsersQuota() throws Exception {
        Vector ret = new Vector();
        Connection con = null;
        long sumquota = 0;
        con = getDBConn();
        try {
            Statement stmt = con.createStatement();
            ResultSet users = stmt.executeQuery("SELECT userId, screenName, firstName, lastName FROM User_ ORDER BY screenName");
            while (users.next()) {
                String uid = "" + users.getString("userId");
                String squota = "";
                try {
                    squota = readQuota(uid);
                } catch (Exception e) {
//                    System.out.println("save default quota for user:" + uid);
                    squota = getDefaultQuota();
                    saveQuota(uid, Integer.parseInt(squota));
                }
                try {
                    sumquota += Integer.parseInt(squota);
                } catch (Exception e) {
                }
                ret.add(toUserQuota(users.getString("firstName") + " " + users.getString("lastName"), users.getString("screenName"), uid, squota));
            }
            ret.add(toUserQuota("Summa: ", "", "", "" + sumquota));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return ret;
    }

    private Vector getUserIDs() {
        Vector ret = new Vector();
        Connection con = null;
        try {
            con = getDBConn();
            Statement stmt = con.createStatement();
            ResultSet users = stmt.executeQuery("SELECT userId FROM User_ ");
            while (users.next()) {
                ret.add(users.getString("userId"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
        return ret;
    }

    private Connection getDBConn() throws Exception {
        StringBuffer s = new StringBuffer();
        BufferedReader br = null;
        File portalext = new File(PropertyLoader.getInstance().getProperty("portal.prefix.dir") + "../../portal-ext.properties");
        if (!portalext.exists()){
            throw new Exception("portal-ext.properties not found! See the installation manual!");
        }
        br = new BufferedReader(new FileReader(portalext));
        String line;
        String url = "";
        String usr = "";
        String pass = "";
        String driver = "";
        while ((line = br.readLine()) != null) {
            if (line.startsWith("jdbc.default.driverClassName=")) {
                driver = line.substring(29).trim();
            } else if (line.startsWith("jdbc.default.url=")) {
                url = line.substring(17).trim();
            } else if (line.startsWith("jdbc.default.username=")) {
                usr = line.substring(22).trim();
            } else if (line.startsWith("jdbc.default.password=")) {
                pass = line.substring(22).trim();
            }
        }
        br.close();
        //System.out.println("QUOTA DB:driver,url,usr,pass:" + driver + "," + url + "," + usr + "," + pass);
        Class.forName(driver).newInstance();
        return DriverManager.getConnection(url, usr, pass);
    }

    private HashMap toUserQuota(String pusername, String pscreenname, String puserid, String pquota) {
        HashMap h = new HashMap();
        h.put("name", pusername);
        h.put("sname", pscreenname);
        h.put("id", puserid);
        h.put("quota", pquota);
        return h;
    }
}