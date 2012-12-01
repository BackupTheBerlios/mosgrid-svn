<%-- 
    Document   : general.jsp
    Created on : 2011.12.08., 14:54:05
    Author     : akos
--%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type="text/javascript" >

    //Load the Visualization API and the ready-made Google table visualization
  google.load('visualization', '1', {'packages':['corechart']});
  google.load('visualization', '1', {'packages':['table']});
  // Set a callback to run when the API is loaded.
  google.setOnLoadCallback(init);


  // Send the query to the data source.
  function init() {


    // Specify the data source URL.
    var timequery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=times&aspect=portal');
    //var sdquery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=SD&aspect=portal');
    //++ userID as parameter

    // Send the query with a callback function.
    timequery.send(timeHandleQueryResponse);
    //sdquery.send(sdHandleQueryResponse);

    // Specify the data source URL.
    var usertimequery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=times&aspect=user&userid=${userid}');
    //var usersdquery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=SD&aspect=user&userid=${userid}');
    //++ userID as parameter

// dci table
    var dcitimequery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=times&aspect=dci&dciname=${dcibean.DCIName}');
    //var dcisdquery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=SD&aspect=dci&dciname=${dcibean.DCIName}');

// resource table
    var resourcetimequery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=times&aspect=resource&resourceurl=${resourceURL.resourceName}');
    //var resourcesdquery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=SD&aspect=resource&resourceurl=${resourceURL.resourceName}');
    

    // Send the query with a callback function.
    usertimequery.send(usertimeHandleQueryResponse);
    
    //usersdquery.send(usersdHandleQueryResponse);

    dcitimequery.send(dcitimeHandleQueryResponse);
    //dcisdquery.send(dcisdHandleQueryResponse);

    resourcetimequery.send(resourcetimeHandleQueryResponse);
    //resourcesdquery.send(resourcesdHandleQueryResponse);

  }

  // Handle the query response.
  function usertimeHandleQueryResponse(response) {
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var data = response.getDataTable();
    var chart = new google.visualization.PieChart(document.getElementById('time_chart_user_div'));
    //var table = new google.visualization.Table(document.getElementById('time_table_div'));
      //  table.draw(data, {showRowNumber: true});
    chart.draw(data, {title: 'Total Time',width: 600, height: 150});
  }


 // Handle the query response.
  function timeHandleQueryResponse(response) {
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var data = response.getDataTable();
    var chart = new google.visualization.PieChart(document.getElementById('time_chart_div'));
    //var table = new google.visualization.Table(document.getElementById('time_table_div'));
      //  table.draw(data, {showRowNumber: true});
    chart.draw(data, {title: 'Total Time',width: 600, height: 150});
  }


// Handle the query response.
  function dcitimeHandleQueryResponse(response) {
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var data = response.getDataTable();
    var chart = new google.visualization.PieChart(document.getElementById('time_chart_dci_div'));
    //var table = new google.visualization.Table(document.getElementById('time_table_div'));
      //  table.draw(data, {showRowNumber: true});
    chart.draw(data, {title: 'Total Time',width: 600, height: 150});
  }


// Handle the query response.
  function resourcetimeHandleQueryResponse(response) {
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var data = response.getDataTable();
    var chart = new google.visualization.PieChart(document.getElementById('time_chart_resource_div'));
    //var table = new google.visualization.Table(document.getElementById('time_table_div'));
      //  table.draw(data, {showRowNumber: true});
    chart.draw(data, {title: 'Total Time',width: 600, height: 150});
  }


</script>
<script type="text/javascript">
			$(function(){
                $('#tabs').tabs();
                if (${selectedtab} != -1)
                   $('#tabs').tabs('select','${selectedtab}');
                
			});
		</script>





          <div id="tabs">
			<ul>
				<li><a href="#tabs-1">Portal Statistics</a></li>
				<li><a href="#tabs-2">DCI Statistics</a></li>
				<li><a href="#tabs-3">User Statistics</a></li>
                <li><a href="#tabs-4">Workflow Statistics</a></li>
			</ul>
			<div id="tabs-1">

                
                <jsp:include page="PortalStats.jsp" /> 
            </div>
			<div id="tabs-2">
                
                <c:set var="dcibean" scope="request" value="${dcibean}" />
                <c:set var="resourceURL" scope="request" value="${resourceURL}" />

                <jsp:include page="DCIStats.jsp" />
            </div>
			<div id="tabs-3">
                <c:set var="tguser" scope="request" value="${tguser}" />
                <jsp:include page="UserStats.jsp" />
            </div>
            <div id="tabs-4">
               
                <c:set var="workflowsvar" scope="request" value="${workflows}" />
                <c:set var="listofStatsvar" scope="request" value="${listofStats}" />

                <jsp:include page="WorkflowStats.jsp" />
                
            </div>
		</div>
    </body>
</html>
