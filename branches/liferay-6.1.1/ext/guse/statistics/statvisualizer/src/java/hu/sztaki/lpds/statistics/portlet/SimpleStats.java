/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.sztaki.lpds.statistics.portlet;

import hu.sztaki.lpds.statistics.db.MetricInformation;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
 *Class for retrieving statistics by category
 * Each return is for a  different category in the
 * stat_metric_description table
 * 
 * @author Alessandra
 */
public class SimpleStats {

    private HashMap<String,List<MetricInformation>> metricMap;

    public SimpleStats(Map<String,List<MetricInformation>> ms){
       metricMap = new HashMap(ms);
    }
    /*
     * It can return null!
     */
    public List<MetricInformation> retrieveCategoryStat(String category){
      return metricMap.get(category);
    }

   public StringBuilder getCategoryStat(String category){
       StringBuilder stat = new StringBuilder();
       List<MetricInformation> statlist = retrieveCategoryStat(category);
      if (statlist == null){
                  stat = stat.append("No Information");
      }
      else{
         for (MetricInformation info : statlist) {
                stat.append(info.getPretty_name());
                stat.append(": &nbsp;");
                stat.append(info.getData());
                stat.append("&nbsp;");
                stat.append(info.getUnits());
                stat.append("<br>");
            }
        }
        return stat;
      }



    public double getChartData(){

        List<MetricInformation> statlist = retrieveCategoryStat("8");
        double data = 0;
        if (statlist == null){
            return data;
        }
        else{
            //Getting the last... don't know why
            data = statlist.get(statlist.size()-1).getUglyData();
            return data;
        }
    }
   

    public List<MetricInformation> getTimeData(){
        return retrieveCategoryStat("7");
    }

  
}
