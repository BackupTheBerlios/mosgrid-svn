<%-- 
    Document   : WorkflowStats
    Created on : Apr 4, 2011, 1:54:16 PM
    Author     : Alessandra

    Displays Concrete Workflow, Workflow Instance, and Abstract Job metrics
    Connected to test.java, WorkflowStatsBean.java, WorkflowInstanceBean.java, and JobBean.java
--%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<portlet:actionURL var="pURL" portletMode="VIEW" />

<html>
    <head>
     
    </head>
    <body>


<script type="text/javascript">

 	jQuery(function() {
        var length ='${listofStatsvarlength}';
        for (i=1;i<=length;++i){

        var name = "#accordionWorkflowStat_"+i;
        jQuery(name).accordion({ header: "h3", autoHeight: false,navigation: true });
        }

        <c:forEach var="stats" items="${listofStatsvar}" varStatus="statCounter">
          <c:forEach var="inststats" items="${stats.inststats}" varStatus="instCounter">

            jQuery("#accordionWorkflowInstanceStat_"+${statCounter.count}+"_"+${instCounter.count}).accordion({ header: "h3", autoHeight: false,navigation: true});
            $("#instancetimetable_"+${statCounter.count}+"_"+${instCounter.count}).tablesorter();
          </c:forEach>
          <c:forEach var="jobstats" items="${stats.jobstats}" varStatus="jobCounter">
              jQuery("#accordionJobStat_"+${statCounter.count}+"_"+${jobCounter.count}).accordion({ header: "h3", autoHeight: false,navigation: true });
              $("#jobtimetable_"+${statCounter.count}+"_"+${jobCounter.count}).tablesorter();
          </c:forEach>
              $("#workflowdiv_"+"${stats.wfstats.workflowName}").tablesorter();
        </c:forEach>

        
        

            

	});


    function showDiv(select){
        var sj = document.getElementById(select);
    	var value = sj.options[sj.selectedIndex].value.replace(".","_");
        jQuery.facebox({ div: '#div_show_'+value});
    }

  

</script>


        <h3>Concrete Workflow Statistics</h3>
        <hr/>

        <form id="chooseWf" method="post" action="${pURL}">

            <input type="hidden" name="action" id="action" value="getWFStats">

            <select id="selectWorkFlow" name="selectWorkFlow" MULTIPLE size ="3" >
             <c:forEach var="wf" items="${workflowsvar}">
                 <option value="${wf.key}"> ${wf.value} </option>
             </c:forEach>

             
            </select>
            <input type="submit" value="Select Workflow(s)" class="portlet-form-button">
            <hr/>
        </form>
        
        <c:forEach var="stats" items="${listofStatsvar}" varStatus="rowCounter">

            <!-- Accordion -->
		
		<h3>Statistics of ${stats.wfstats.workflowName}</h3>
           <div>
			<div id="accordionWorkflowStat_${rowCounter.count}">
				<h3><a href="#">Overall Workflow Statistics</a></h3>
                <div>
                    <table>
                        <tr>
                            <td>
				    <b>${stats.wfstats.cat1Stats}<br>
                    ${stats.wfstats.cat3Stats}<br>
                    ${stats.wfstats.cat6Stats}</b>
                            </td>
                            <td>
                               <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${stats.wfstats.failrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                            </td>
                        </tr>
                        <tr>
                            <td>

                           <select id="selectJob_${stats.wfstats.workflowName}" name="selectJob">
                                        <c:forEach var="jobname" items="${stats.wfstats.jobNames}" varStatus="rowCounter">
                                        <option value="${stats.wfstats.workflowName}_${jobname}">
                                            ${jobname}
                                        </option>
                                        </c:forEach>
                                    </select>
                           <input type="button" value="Show Job Statistics" onclick="javascript:showDiv('selectJob_${stats.wfstats.workflowName}');">

                           <select id="selectWfI_${stats.wfstats.workflowName}" name="selectWfI">
                                      <c:forEach var="instname" items="${stats.wfstats.workflowInstances}" varStatus="rowCounter">
                                        <option value="${stats.wfstats.workflowName}_${instname.key}">
                                            ${instname.key}
                                        </option>
                                        </c:forEach>
                           </select>
                           <input type="button" value="Show Workflow Instance Statistics" onclick="javascript:showDiv('selectWfI_${stats.wfstats.workflowName}');">
                           </td>
                         </tr>
                           
                           

                           </table>
                        </div>
                        
                        
                         


				<h3><a href="#">Details</a></h3>


                    <div id="workflowdiv">
                        <table id="workflowdiv_${stats.wfstats.workflowName}" class="tablesorter" width="70%">
                        <thead>
                        <tr>
                            <c:forEach items="${stats.wfstats.tgworkflow.titlesForChart}" var="title">
                                <th>${title}</th>
                            </c:forEach>
                        </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${stats.wfstats.tgworkflow.table}" var="row" >
                                <tr>
                                    <c:forEach items="${row}" var="data">
                                    <td>
                                        ${data}
                                    </td>
                                    </c:forEach>
                                </tr>

                            </c:forEach>

                        </tbody>
                        </table>
                     </div>

            </div>
           </div>

        </c:forEach>
        
        <c:forEach var="stats" items="${listofStatsvar}" varStatus="statCounter">
        <c:forEach var="inststats" items="${stats.inststats}" varStatus="instCounter">
        <div id="div_show_${fn:replace(stats.wfstats.workflowName,".","_")}_${fn:replace(inststats.workflowInstId,".","_")}" style="display:none;">
                
                <h2 class="demoHeaders">Workflow Instance Statistics</h2>
		<div id="accordionWorkflowInstanceStat_${statCounter.count}_${instCounter.count}" style="overflow-x: auto; overflow-y: auto;">
			<div>
				<h3><a href="#">Overall Workflow Instance Statistics</a></h3>
				<div>
                    
                    <table border="0" cellspacing="20" cellpadding="50" ALIGN="top">
                        <tbody ALIGN="top">
                            <tr>
                                <td>
                                    <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${inststats.failrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                                    
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <h4>
                                        <b>${inststats.cat1Stats}<br>${inststats.cat3Stats}<br>${inststats.cat6Stats}</b>
                                    </h4>
                                </td>

                            </tr>
                        </tbody>
                    </table>

                
                </div>
			</div>
			<div>
				<h3><a href="#">Details</a></h3>
				<div>
                  
                    <div id="instancetimetablediv">
                        <table id="instancetimetable_${statCounter.count}_${instCounter.count}" class="tablesorter" width="70%">
                        <thead>
                        <tr>
                            <c:forEach items="${inststats.tginstance.titlesForChart}" var="title">
                                <th>${title}</th>
                            </c:forEach>
                        </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${inststats.tginstance.table}" var="row" >
                                <tr>
                                    <c:forEach items="${row}" var="data">
                                    <td>
                                        ${data}
                                    </td>
                                    </c:forEach>
                                </tr>

                            </c:forEach>

                        </tbody>
                        </table>
                     </div>

                
                </div>

		</div>
        </div>
        </div>
           </c:forEach>
           <c:forEach var="jobstats" items="${stats.jobstats}" varStatus="jobCounter">
              
            <div class="draggable" id="div_show_${fn:replace(stats.wfstats.workflowName,".","_")}_${fn:replace(jobstats.jobName,".","_")}" style="display:none;" width="100%">
                                <h2 class="demoHeaders">${jobstats.jobName} Statistics</h2>
		<div id="accordionJobStat_${statCounter.count}_${jobCounter.count}">
			<div>
				<h3><a href="#">Overall ${jobstats.jobName} Statistics</a></h3>
				<div>
                    
                    <table border="0" cellspacing="20" cellpadding="50" ALIGN="top">
                        <tbody ALIGN="top">
                            <tr>
                                
                                <td>
                                    <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${jobstats.failrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                                    
                                </td> 
                            </tr>
                            <tr>
                                <td>
                                    <h4>
                                        <br>
                                        <b>${jobstats.cat1Stats}<br>
                                        ${jobstats.cat3Stats}</b>
                                    </h4>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                

                </div>
			</div>
			<div>
				<h3><a href="#">Details</a></h3>

                <div id="jobtimetablediv">
                                    <table id="jobtimetable_${statCounter.count}_${jobCounter.count}" class="tablesorter" width="70%">
                                    <thead>
                                    <tr>
                                        <c:forEach items="${jobstats.tgjob.titlesForChart}" var="title">
                                            <th>${title}</th>
                                        </c:forEach>
                                        <!-- -->
                                    </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${jobstats.tgjob.table}" var="row" >
                                            <tr>
                                                <c:forEach items="${row}" var="data">
                                                <td>
                                                    ${data}
                                                </td>
                                                </c:forEach>
                                            </tr>

                                        </c:forEach>

                                    </tbody>
                                    </table>
                                </div>

                
                    
			</div>

		</div>
                
            </div>
            </c:forEach>
        </c:forEach>
    </body>
</html>


