<%-- 
    Document   : DCIStats
    Created on : Apr 4, 2011, 1:52:29 PM
    Author     : Alessandra

    Displays DCI and Resource Metrics
    Connected to test.java, DCIStatsBean.java, and ResourceBean.java
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
   <portlet:resourceURL var="resURL" />
    <portlet:renderURL var="rURL" />
    <portlet:actionURL var="uploadURL" />
    <portlet:resourceURL var="ajaxURL" />
    <portlet:defineObjects/>

    <portlet:actionURL var="pURL" portletMode="VIEW" />
   
<html>
    <head>
    </head>
    <body>
        
       

<script type="text/javascript">

 	jQuery(function() {


	  	 $("#accordionDCIStat").accordion({ header: "h3", autoHeight: false,navigation: true });
          $("#accordionResourceStat").accordion({ header: "h3", autoHeight: false,navigation: true });

          $("#dcitimetable").tablesorter();
          $("#resourcetimetable").tablesorter();

	});

</script>

        <form id="chooseDCI" method="post" action="${pURL}">



        
            <hr/>
            <input type="hidden" name="action" id="action" value="getResources">
            <select id="selectDCI" name="selectDCI">
               <c:forEach var="dci" items="${dcilist}">
                 <option value="${dci}"> ${dci} </option>
             </c:forEach>
              <%--  <%= listDCIs%> --%>
            </select>
            <input type="submit" value="DCI" class="portlet-form-button">
            <hr/>
            <br>
        </form>

  <c:if test="${dcibean != null}">

            <h4>Resources: </h4>
            <form method="post" action="${pURL}">
                <input type ="hidden" name="DCIURL" id="DCIURL" value="${dcibean.DCIName}">
                <input type="hidden" name="action" id="action" value="getWorkflowsforResource">
                <select id="selectResource" name="selectResource">
                    <c:forEach var="resource" items="${dcibean.resources}">
                        <option value="${resource}"> ${resource} </option>
                    </c:forEach>


                 <%--  ${DCIName.resourceList} --%>
                    <input type="submit" value="Resource" class="portlet-form-button">
                </select>
            </form>
            <hr/>





<h2 class="demoHeaders">DCI Statistics</h2>
		<div id="accordionDCIStat">
			<div>
				<h3><a href="#">Overall DCI Statistics</a></h3>

                    
                <table border="0" cellspacing="20" cellpadding="50" ALIGN="top">
                    <tbody ALIGN="top">
                        <tr>
                            <td>
                                <h4>
                                    DCI Name: ${dcibean.DCIName}<br>
                                    <b>${dcibean.cat1Stats}<br>
                                    ${dcibean.cat3Stats}</b>
                                </h4>
                            </td>
                            <td>
                                <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${dcibean.failrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                         
                            </td>
                        </tr>
                    </tbody>
                </table>
            


			</div>

            <div>
				<h3><a href="#">Details</a></h3>
				
            <table border="0" cellspacing="20" cellpadding="50" ALIGN="top" width="100%">
                <tbody ALIGN="top">
                    <tr>
                        <td width="70%">
                                <div id="dcitimetablediv">
                                    <table id="dcitimetable" class="tablesorter" width="70%">
                                    <thead>
                                    <tr>
                                        <c:forEach items="${tgdci.titlesForChart}" var="title">
                                            <th>${title}</th>
                                        </c:forEach>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${tgdci.table}" var="row" >
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
                                

                        </td>
                        </tr>
                        <tr>
                        <td width="30%">
                            <div id="time_chart_dci_div">

                            </div>
                        </td>
                    </tr>

                </tbody>
            </table><br>
			</div>


		</div>

    

        <c:if test="${!empty resourceURL}">

<h2 class="demoHeaders">Resource Statistics</h2>
		<div id="accordionResourceStat">
			<div>
				<h3><a href="#">Overall Resource Statistics</a></h3>

                    
                <table border="0" cellspacing="20" cellpadding="50" ALIGN="top">
                    <tbody ALIGN="top">
                        <tr>
                            <td>
                                <h4>
                                    Resource Name: ${resourceURL.resourceName}<br>
                                    ${resourceURL.cat1Stats}${resourceURL.cat3Stats}
                                </h4>
                            </td>
                            <td>
                                <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${resourceURL.failrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                               
                            </td>
                        </tr>
                    </tbody>
                </table>
            

			</div>
            <div>
				<h3><a href="#">Details</a></h3>

            <table border="0" cellspacing="20" cellpadding="50" ALIGN="top" width="100%">
                <tbody ALIGN="top">
                    <tr>
                        <td width="70%">
                                <div id="resourcetimetablediv">
                                    <table id="resourcetimetable" class="tablesorter" width="70%">
                                    <thead>
                                    <tr>
                                        <c:forEach items="${tgresource.titlesForChart}" var="title">
                                            <th>${title}</th>
                                        </c:forEach>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${tgresource.table}" var="row" >
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
                                

                        </td>
                        </tr>
                        <tr>
                        <td>
                            
                           <div id="time_chart_resource_div">

                            </div>


                        </td>
                    </tr>

                </tbody>
            </table><br>
                 
			</div>

			

		</div>
   
        </c:if>
        </c:if>
    </body>
</html>
