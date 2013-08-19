// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import com.google.visualization.datasource.Capabilities;
import com.google.visualization.datasource.DataSourceHelper;
import com.google.visualization.datasource.DataSourceRequest;
import com.google.visualization.datasource.DataSourceServlet;
import com.google.visualization.datasource.QueryPair;
import com.google.visualization.datasource.base.DataSourceException;
import com.google.visualization.datasource.base.ReasonType;
import com.google.visualization.datasource.base.ResponseStatus;
import com.google.visualization.datasource.base.StatusType;
import com.google.visualization.datasource.base.TypeMismatchException;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.query.Query;

import hu.sztaki.lpds.statistics.db.DBBase;
import hu.sztaki.lpds.statistics.db.MenuInformationHarvester;
import hu.sztaki.lpds.statistics.db.MetricInformation;
import hu.sztaki.lpds.statistics.db.MetricInformationHarvester;
import hu.sztaki.lpds.statistics.db.StatisticLevel;
  /**
   * NOTE: By default, this function returns true, which means that cross
   * domain requests are rejected.
   * This check is disabled here so examples can be used directly from the
   * address bar of the browser. Bear in mind that this exposes your
   * data source to xsrf attacks.
   * If the only use of the data source url is from your application,
   * that runs on the same domain, it is better to remain in restricted mode.
   */

import hu.sztaki.lpds.statistics.db.StatistiticsInformationHarvester;
import hu.sztaki.lpds.statistics.portlet.SimpleStats;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A demo servlet for serving a simple, constant data table.
 * This servlet extends DataSourceServlet.
 *
 * @author Nimrod T.
 */
public class PortalStatServlet extends HttpServlet {

@Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    DataSourceRequest dsRequest = null;
    
    try {
      // Extract the request parameters.
      dsRequest = new DataSourceRequest(req);

      // NOTE: If you want to work in restricted mode, which means that only
      // requests from the same domain can access the data source, you should
      // uncomment the following call.
      //
      // DataSourceHelper.verifyAccessApproved(dsRequest);

      // Split the query.
      QueryPair query = DataSourceHelper.splitQuery(dsRequest.getQuery(), Capabilities.SELECT);

      // Generate the data table.

      // Getting input parameters
      String target = req.getParameter("target");
      String aspect = req.getParameter("aspect");
      String userId = req.getParameter("userid");
      String dciname = req.getParameter("dciname");
      String resourceURL = req.getParameter("resourceurl");
      String parameter = "";
      if (userId != null){
          parameter = userId;
      }
      if (dciname != null){
          parameter = dciname;
      }
      if(resourceURL != null){
          parameter = resourceURL;
      }


      
      DataTable data = null;
      if (target.equals(new String("times"))){
        data = generateTimeDataTable(parameter,aspect,query.getDataSourceQuery(), req);
      }
      else if (target.equals(new String("SD"))){
          data = generateSDDataTable(parameter,aspect,query.getDataSourceQuery(), req);
      }


      // Apply the completion query to the data table.
      DataTable newData = DataSourceHelper.applyQuery(query.getCompletionQuery(), data,
          dsRequest.getUserLocale());

      DataSourceHelper.setServletResponse(newData, dsRequest, resp);

    } catch (RuntimeException rte) {
      System.out.println("A runtime exception has occured");
      ResponseStatus status = new ResponseStatus(StatusType.ERROR, ReasonType.INTERNAL_ERROR,
          rte.getMessage());
      if (dsRequest == null) {
        dsRequest = DataSourceRequest.getDefaultDataSourceRequest(req);
      }
      DataSourceHelper.setServletErrorResponse(status, dsRequest, resp);
    } catch (DataSourceException e) {
      if (dsRequest != null) {
        DataSourceHelper.setServletErrorResponse(e, dsRequest, resp);
      } else {
        DataSourceHelper.setServletErrorResponse(e, req, resp);
      }
    }
  }


  public DataTable generateSDDataTable(String parameter,String aspect,Query query, HttpServletRequest request) {
    // Create a data table.

    DataTable data = new DataTable();

        String prettyName = "";

        
        ArrayList<String> titlesForChart = this.getTitlesForChart(parameter,aspect,"4");
        ArrayList<Double> dataForSDChart = this.getDataForChart(parameter,aspect,"4");
        ArrayList<Double> dataForNumberChart = this.getDataForChart(parameter,aspect,"5");
        ArrayList<Double> dataForAverageChart = this.getDataForChart(parameter,aspect,"7");
        ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();

            cd.add(new ColumnDescription("States", ValueType.TEXT, "States"));
            cd.add(new ColumnDescription("Standard Deviation of Time spent in state", ValueType.NUMBER, "Standard Deviation of Time spent in state"));
            cd.add(new ColumnDescription("Number of Times the Job Entered the State", ValueType.NUMBER, "Number of Times the Job Entered the State"));
            cd.add(new ColumnDescription("Average time spent in state", ValueType.NUMBER, "Average time spent in state"));
            data.addColumns(cd);

            for (int i=0; i<titlesForChart.size();++i){
                    try{

                    data.addRowFromValues(titlesForChart.get(i),dataForSDChart.get(i),dataForNumberChart.get(i),dataForAverageChart.get(i));
                    
                        } catch (TypeMismatchException e) {
                    System.out.println("Invalid type!");
                }
            }


    // Fill the data table.
    return data;
  }




  public DataTable generateTimeDataTable(String parameter,String aspect,Query query, HttpServletRequest request) {
    // Create a data table.

    DataTable data = new DataTable();

        String prettyName = "";

        ArrayList<Double> dataForChart = this.getDataForChart(parameter,aspect,"7");
        ArrayList<String> titlesForChart = this.getTitlesForChart(parameter,aspect,"7");


        ArrayList<ColumnDescription> cd = new ArrayList<ColumnDescription>();
        cd.add(new ColumnDescription("States", ValueType.TEXT, "States"));
        cd.add(new ColumnDescription("Average time spent in state", ValueType.NUMBER, "Average time spent in state"));
            data.addColumns(cd);
            for (int i=0; i<titlesForChart.size();++i){
                    try{

                    data.addRowFromValues(titlesForChart.get(i),dataForChart.get(i));
                        } catch (TypeMismatchException e) {
                    System.out.println("Invalid type!");
                }
            }
      

    // Fill the data table.
    return data;
  }

/*
 *  paremeter can represent user and resourceURL.
 */


 private ArrayList<Double> getDataForChart(String parameter, String aspect, String category){
      ArrayList<Double> dataForChart = new ArrayList<Double>();
        DBBase db = new DBBase();
        MetricInformationHarvester mif = new MetricInformationHarvester(db);
        Map<String, List<MetricInformation>> ms = null;
        StatistiticsInformationHarvester statsFac = new StatistiticsInformationHarvester(db);
        MenuInformationHarvester mp = new MenuInformationHarvester(db);
        if (aspect.equals("portal")){
            ms =  mif.getMetric(StatisticLevel.PORTAL);
            String portalURL = mp.getPortal();
            statsFac.getPortal(ms, portalURL);
        }
        else if (aspect.equals("user")){
            ms =  mif.getMetric(StatisticLevel.USER);
            statsFac.getUser(ms, parameter);
        }
        else if (aspect.equals("dci")){
            ms =  mif.getMetric(StatisticLevel.DCI);
            statsFac.getDCI(ms, parameter);
        }
        else if (aspect.equals("resource")){
            ms =  mif.getMetric(StatisticLevel.RESOURCE);
            statsFac.getResource(ms, parameter);
        }
        else if (aspect.equals("job")){

        }
        else if (aspect.equals("instance")){
            ms = mif.getMetric(StatisticLevel.WORKFLOWINSTANCE);
            // parameter = workflowInstance Id
            statsFac.getWorkflowInstance(ms, parameter);
        }
        else if (aspect.equals("workflow")){
            ms = mif.getMetric(StatisticLevel.CONCRETEWORKFLOW);
            // parameter = workflow Id
            statsFac.getConcreteWorkflow(ms, parameter);
        }
                //Outputs
        SimpleStats stat = new SimpleStats(ms);

        java.util.List<hu.sztaki.lpds.statistics.db.MetricInformation> getCat = stat.retrieveCategoryStat(category);
        if (getCat == null) {
            System.out.println("No Info FAIL!!!");
        } else {

            for (hu.sztaki.lpds.statistics.db.MetricInformation info : getCat) {
                try {
                    String prettyName = info.getPretty_name();

                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number = format.parse(info.getData());
                    double ddata = number.doubleValue();

                    
                    //    maxSize = (int) (maxSize + data);

                    dataForChart.add(ddata);
                } catch (ParseException ex) {
                    Logger.getLogger(PortalStatServlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return dataForChart;
  }


  private ArrayList<String> getTitlesForChart(String parameter,String aspect, String category){
      ArrayList<String> titlesForChart = new ArrayList<String>();
        DBBase db = new DBBase();
        MetricInformationHarvester mif = new MetricInformationHarvester(db);
        Map<String, List<MetricInformation>> ms = null;
        StatistiticsInformationHarvester statsFac = new StatistiticsInformationHarvester(db);
        MenuInformationHarvester mp = new MenuInformationHarvester(db);
        if (aspect.equals("portal")){
            ms =  mif.getMetric(StatisticLevel.PORTAL);
            String portalURL = mp.getPortal();
            statsFac.getPortal(ms, portalURL);
        }
        else if (aspect.equals("user")){
            ms =  mif.getMetric(StatisticLevel.USER);
            statsFac.getUser(ms, parameter);
        }
        else if (aspect.equals("dci")){
            ms =  mif.getMetric(StatisticLevel.DCI);
            statsFac.getDCI(ms, parameter);
        }
        else if (aspect.equals("resource")){
            ms =  mif.getMetric(StatisticLevel.RESOURCE);
            statsFac.getResource(ms, parameter);
        }
                //Outputs
        SimpleStats stat = new SimpleStats(ms);

        java.util.List<hu.sztaki.lpds.statistics.db.MetricInformation> getCat = stat.retrieveCategoryStat(category);
        if (getCat == null) {
            System.out.println("No Info FAIL!!!");
        } else {

            for (hu.sztaki.lpds.statistics.db.MetricInformation info : getCat) {
                try {
                    String prettyName = info.getPretty_name();
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number = format.parse(info.getData());
                    titlesForChart.add(prettyName);

                } catch (ParseException ex) {
                    Logger.getLogger(PortalStatServlet.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        return titlesForChart;
  }

}