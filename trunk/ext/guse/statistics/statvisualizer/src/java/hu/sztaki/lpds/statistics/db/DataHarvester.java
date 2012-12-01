/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hu.sztaki.lpds.statistics.db;

import hu.sztaki.lpds.statistics.portlet.SimpleStats;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author akos
 */
public class DataHarvester {

    private ArrayList<String> titlesForChart = null;
    private ArrayList<ArrayList<String>> table = null;
    private StringBuilder cat1Stats;
    private double chartdata;

    public double getChartdata() {
        return chartdata;
    }

    public void setChartdata(double chartdata) {
        this.chartdata = chartdata;
    }

    public StringBuilder getCat1Stats() {
        return cat1Stats;
    }

    public void setCat1Stats(StringBuilder cat1Stats) {
        this.cat1Stats = cat1Stats;
    }

    public StringBuilder getCat2Stats() {
        return cat2Stats;
    }

    public void setCat2Stats(StringBuilder cat2Stats) {
        this.cat2Stats = cat2Stats;
    }

    public StringBuilder getCat3Stats() {
        return cat3Stats;
    }

    public void setCat3Stats(StringBuilder cat3Stats) {
        this.cat3Stats = cat3Stats;
    }

    public StringBuilder getCat4Stats() {
        return cat4Stats;
    }

    public void setCat4Stats(StringBuilder cat4Stats) {
        this.cat4Stats = cat4Stats;
    }

    public StringBuilder getCat5Stats() {
        return cat5Stats;
    }

    public void setCat5Stats(StringBuilder cat5Stats) {
        this.cat5Stats = cat5Stats;
    }
    private StringBuilder cat2Stats;
    private StringBuilder cat3Stats;
    private StringBuilder cat4Stats;
    private StringBuilder cat5Stats;
    public ArrayList<ArrayList<String>> getTable() {
        return table;
    }

    public void setTable(ArrayList<ArrayList<String>> table) {
        this.table = table;
    }
    

    
    private String parameter;
    private String aspect;


    public DataHarvester(String parameter,String aspect){
        this.parameter = parameter;
        this.aspect = aspect;
        table = new ArrayList<ArrayList<String>>();
        titlesForChart = new ArrayList<String>();
        cat1Stats = new StringBuilder();
        cat2Stats = new StringBuilder();
        cat3Stats = new StringBuilder();
        cat4Stats = new StringBuilder();
        cat5Stats = new StringBuilder();

    

        generateSDDataTable();

    }

    public ArrayList<String> getTitlesForChart() {
        return titlesForChart;
    }

    public void setTitlesForChart(ArrayList<String> titlesForChart) {
        this.titlesForChart = titlesForChart;
    }

    private void generateSDDataTable() {
    // Create a data table.

        String prettyName = "";


        ArrayList<String>statesForChart = this.getTitlesForChart(parameter,aspect,"4");

        ArrayList<String> categories = new ArrayList<String>();
        /*categories.add("4");
        categories.add("5");
        categories.add("2");
         */
        categories.add("1");
        categories.add("2");
        categories.add("3");
        categories.add("4");
        categories.add("5");
        
        ArrayList<ArrayList<String>> data = this.getDataForChart(parameter, aspect, categories);
        for (String s : data.get(0)){
            this.cat1Stats.append(s);
        }
        for (String s : data.get(1)){
            this.cat2Stats.append(s);
        }
        for (String s : data.get(2)){
            this.cat3Stats.append(s);
        }
        for (String s : data.get(3)){
            this.cat4Stats.append(s);
        }
        for (String s : data.get(4)){
            this.cat5Stats.append(s);
        }
        
        titlesForChart.add("States");
        titlesForChart.add("Number of Jobs Entered the State");
        titlesForChart.add("Average time");
        titlesForChart.add("Standard Deviation of Time");
        
        

        

        for (int i=0; i<statesForChart.size();++i){

                    ArrayList<String> row = new ArrayList<String>();
                    //row.add(statesForChart.get(i));row.add(dataForNumberChart.get(i));row.add(dataForAverageChart.get(i));row.add(dataForSDChart.get(i));
                    row.add(statesForChart.get(i));
                    row.add(data.get(4).get(i));

                    row.add(data.get(1).get(i));
                    row.add(data.get(3).get(i));
                    
                    table.add(row);

                }




    }


    



/*
 *  paremeter can represent user and resourceURL.
 */


 private ArrayList<ArrayList<String>> getDataForChart(String parameter, String aspect, ArrayList<String> listOfCategories){

      //ArrayList<String> dataForChart = new ArrayList<String>();
      ArrayList<ArrayList<String>> returndata=new ArrayList<ArrayList<String>>();
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
            ms = mif.getMetric(StatisticLevel.ABSTRACTJOB);         //Gets Metric Information for an Abstract Job
            String wfId = parameter.split("@")[0];
            String abjobName = parameter.split("@")[1];
            statsFac.getAbstractJob(ms, abjobName, wfId);           //Gets Statistics for an Abstract Job
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
        for (String category: listOfCategories){
            java.util.List<hu.sztaki.lpds.statistics.db.MetricInformation> getCat = stat.retrieveCategoryStat(category);
            ArrayList<String> dataForChart = new ArrayList<String>();
        if (getCat == null) {
            System.out.println("No Info FAIL!!!");
        } else {

            for (hu.sztaki.lpds.statistics.db.MetricInformation info : getCat) {


                try {
                    String prettyName = info.getPretty_name();
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
                    Number number = format.parse(info.getData());
                    double ddata = number.doubleValue();
                   
                    
                    String units = info.getUnits();

                    //    maxSize = (int) (maxSize + data);
                    if (category.equals("1") || category.equals("3")){
                    String stringData = prettyName + ":" + info.getData() + units + "<br>";
                    dataForChart.add(stringData);
                   }
                   else{
                    dataForChart.add(Double.toString(ddata));
                    }
                } catch (ParseException ex) {

                }

            }
        }
         returndata.add(dataForChart);
        }
        return returndata;
  }


  private ArrayList<String> getTitlesForChart(String parameter,String aspect, String category){
      ArrayList<String> returnTitles = new ArrayList<String>();
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
            ms = mif.getMetric(StatisticLevel.ABSTRACTJOB);         //Gets Metric Information for an Abstract Job
            String wfId = parameter.split("@")[0];
            String abjobName = parameter.split("@")[1];
            statsFac.getAbstractJob(ms, abjobName, wfId);           //Gets Statistics for an Abstract Job
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
        this.chartdata = stat.getChartData();

        java.util.List<hu.sztaki.lpds.statistics.db.MetricInformation> getCat = stat.retrieveCategoryStat(category);
        if (getCat == null) {
            System.out.println("No Info FAIL!!!");
        } else {

            for (hu.sztaki.lpds.statistics.db.MetricInformation info : getCat) {
                try {
                    
                    String prettyName = info.getPretty_name();
                    
                    NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);

                    
                    Number number = format.parse(info.getData());
                    returnTitles.add(info.getType().toString());

                } catch (ParseException ex) {

                }

            }
        }
        return returnTitles;
  }





}
