package hu.sztaki.lpds.statistics.portlet;

import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableCell;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.ValueType;

import hu.sztaki.lpds.statistics.db.DBBase;

import hu.sztaki.lpds.statistics.db.MenuInformationHarvester;
import hu.sztaki.lpds.statistics.db.MetricInformation;
import hu.sztaki.lpds.statistics.db.MetricInformationHarvester;
import hu.sztaki.lpds.statistics.db.StatisticLevel;
import hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester;
import hu.sztaki.lpds.statistics.db.DataHarvester;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.portlet.PortletRequestDispatcher;
//import java.sql.;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * StatVisualizerPortlet Portlet Class
 * this class provides the rendering function of the portlet
 */
public class StatVisualizerPortlet extends GenericPortlet {
    /*private final static String PORTALVIEW = "PortalStats.jsp";
    private final static String USERVIEW = "UserStats.jsp";
    private final static String DCIVIEW = "DCIStats.jsp";
    private final static String WORKFLOWVIEW = "WorkflowStats.jsp";*/
    private final static Integer  PORTALTAB = 1;
    private final static Integer  DCITAB =2;
    private final static Integer  USERTAB = 3;
    private final static Integer  WORKFLOWTAB=4;

    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException {
        System.out.println("processaction called...");
        String action = "";
        if ((request.getParameter("action") != null) && (!request.getParameter("action").equals(""))) {
            action = request.getParameter("action");
        }
        System.out.println("*************" + action + "::" + request.getParameter("action"));
        if (action != null) {
            try {
                Method method = this.getClass().getMethod(action, new Class[]{ActionRequest.class, ActionResponse.class});
                method.invoke(this, new Object[]{request, response});
            } catch (IllegalAccessException ex) {
                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
    /*
     * Handles the rendering for the portlet
     */
    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
    

        /* Sending Workflow list
         */
         StringBuilder outsWFName = new StringBuilder();
                            MenuInformationHarvester mp = new MenuInformationHarvester(new DBBase());
                           String userId = request.getRemoteUser();


        Map<String, String> workflowList = mp.getWFIDs(userId);
        
        request.setAttribute("workflows", workflowList);
        request.setAttribute("listofStatsvarlength", "0");
        request.setAttribute("userid", userId);
        
        // Getting used DCIs:


        request.setAttribute("dcilist",mp.getDCIs());




        // key = workflowID value = workflowname
        Integer nexttab = -1;
        String tempnexttab = request.getParameter("nexttab");

        if (tempnexttab != null){
        nexttab = Integer.parseInt(tempnexttab);
        }
        


       
            String portalURL = "";
            
            Map<String, List<MetricInformation>> ms = null;
            MetricInformationHarvester mif = new MetricInformationHarvester(new DBBase());
            mp = new MenuInformationHarvester(new DBBase());
            StatistiticsInformationHarvester statsFac;
            statsFac = new StatistiticsInformationHarvester(new DBBase());
            //Methods
             portalURL = mp.getPortal();
             
            if (portalURL.isEmpty()) {
                  portalURL = "";
            } else {

                 ms = mif.getMetric(StatisticLevel.PORTAL);
                 statsFac.getPortal(ms, portalURL);
                 DataHarvester tg = new DataHarvester("","portal");
                 request.setAttribute("tg",tg);

                 SimpleStats s = new SimpleStats(ms);
                StringBuilder outs = s.getCategoryStat("1");
                StringBuilder outs2 =s.getCategoryStat("2");
                StringBuilder outs3 =s.getCategoryStat("3");
                StringBuilder outs4 =s.getCategoryStat("4");
                StringBuilder outs5 =s.getCategoryStat("5");
                String portalfailrate = Double.toString(s.getChartData());
                request.setAttribute("statCategory1", outs);
                request.setAttribute("statCategory2", outs2);
                request.setAttribute("statCategory3", outs3);
                request.setAttribute("statCategory4", outs4);
                request.setAttribute("statCategory5", outs5);
                request.setAttribute("portalfailrate", portalfailrate);

                 ms = mif.getMetric(StatisticLevel.USER);
                 statsFac.getUser(ms, userId);
                 request.setAttribute("numofWorkflows",mp.getWFIDs(userId).size());
                 DataHarvester tguser = new DataHarvester(userId,"user");
                
                 request.setAttribute("tguser",tguser);
               
                  s = new SimpleStats(ms);
                outs = s.getCategoryStat("1");
                outs2 =s.getCategoryStat("2");
                outs3 =s.getCategoryStat("3");
                outs4 =s.getCategoryStat("4");
                outs5 =s.getCategoryStat("5");
                String userfailrate = Double.toString(s.getChartData());
                request.setAttribute("userstatCategory1", outs);
                request.setAttribute("userstatCategory2", outs2);
                request.setAttribute("userstatCategory3", outs3);
                request.setAttribute("userstatCategory4", outs4);
                request.setAttribute("userstatCategory5", outs5);
                request.setAttribute("userfailrate", userfailrate);
                //Outputs
                
                
                
            }
             


         if (nexttab == WORKFLOWTAB ){
            
            //for concrete workflow
        String wfs = request.getParameter("wfs2pass");
      
        if (wfs != null) {
            ArrayList<DetailedWorkflowStats> listofStats = new ArrayList<DetailedWorkflowStats>();
            String[] splitted_wfs = wfs.split("/@/");
            for (int i = 1; i < splitted_wfs.length; ++i) {
                    DetailedWorkflowStats dwbean = new DetailedWorkflowStats(userId, splitted_wfs[i]);
                    listofStats.add(dwbean);
            }
            request.setAttribute("listofStats", listofStats);
            request.setAttribute("listofStatsvarlength", listofStats.size());




        }
        //for job

        }
        
        else if (nexttab == DCITAB){
            //nextJSP = this.DCIVIEW;

            if (request.getParameter("dci") != null) {              //check to see if DCIName has been selected
            String dciname = request.getParameter("dci");       //gets parameter
            if (!dciname.equals("")) {                              //check to see if DCI is empty
                DCIStatsBean DCIbean;                               //New DCIbean
                try {
                    DCIbean = new DCIStatsBean(dciname);
                    request.setAttribute("dcibean", DCIbean);       //Sets the DCIBean to Attribute on DCIStats.jsp
                    DataHarvester tgdci = new DataHarvester(dciname,"dci");
        
                 request.setAttribute("tgdci",tgdci);
                     String resourcename = request.getParameter("resourceURL");
                    if (resourcename != null) {

                        if (!resourcename.equals("")) {                             //Checks to see if resourcename is empty
                            ResourceBean resbean;                                   //New ResourceBean
                            try {
                                resbean = new ResourceBean(resourcename, dciname);

                                DataHarvester tgresource = new DataHarvester(resourcename,"resource");
                             
                                request.setAttribute("tgresource",tgresource);
                                request.setAttribute("resourceURL", resbean);       //Sets the resbean to Attribute on DCIStats.jsp
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (SQLException ex) {
                                Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }



                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(StatVisualizerPortlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }




        }

        // generating JSP page

         request.setAttribute("selectedtab", nexttab);
        PortletRequestDispatcher dispatcher;
        dispatcher = getPortletContext().getRequestDispatcher("/WEB-INF/jsp/general.jsp" );
        dispatcher.include(request, response);

    }

    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException {
        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/test_edit.jsp");
        dispatcher.include(request, response);
    }

    public void doHelp(RenderRequest request, RenderResponse response) throws PortletException, IOException {

        response.setContentType("text/html");
        PortletRequestDispatcher dispatcher =
                getPortletContext().getRequestDispatcher("/WEB-INF/jsp/test_help.jsp");
        dispatcher.include(request, response);
    }

    /*
     * requests parameter selectDCI(from selection list on DCIStats.jsp)
     * and sets proper render Parameters
     */
    public void getResources(ActionRequest request, ActionResponse response) throws PortletException, SQLException, ClassNotFoundException {
        String dci = request.getParameter("selectDCI");
      
        response.setRenderParameter("dci", dci);
        response.setRenderParameter("nexttab", DCITAB.toString());
    }
    /*
     * requests parameter selectResource(from selection list on DCIStats.jsp)
     * requests parameter DCIURL(from DCIStats.jsp)
     * and sets proper render parameters
     */
    public void getWorkflowsforResource(ActionRequest request, ActionResponse response) throws PortletException, SQLException, ClassNotFoundException {

        String resourceURL = request.getParameter("selectResource");
        String dci = request.getParameter("DCIURL");

        response.setRenderParameter("resourceURL", resourceURL);
        response.setRenderParameter("dci", dci);
        response.setRenderParameter("nexttab", DCITAB.toString());
    }
/*For the three following funcitons
 * requests parameters from WorkflowStats.jsp
 * sets neccesary render parameters
 */
    public void getWFStats(ActionRequest request, ActionResponse response) throws PortletException, SQLException, ClassNotFoundException {
        java.lang.String[] workflowNames = request.getParameterValues("selectWorkFlow");

        response.setRenderParameter("nexttab", WORKFLOWTAB.toString());
        if (workflowNames != null) {
            
        
            String wfs2pass = "";
            for (String wfname : workflowNames) {
                wfs2pass = wfs2pass + "/@/" + wfname;
            }
            response.setRenderParameter("wfs2pass", wfs2pass);
            
        }
    }


   

}
