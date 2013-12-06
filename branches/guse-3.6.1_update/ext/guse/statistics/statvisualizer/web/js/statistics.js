/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


<script type="text/javascript" src="http://www.google.com/jsapi"></script>
    //Load the Visualization API and the ready-made Google table visualization
  google.load('visualization', '1', {'packages':['corechart']});
  google.load('visualization', '1', {'packages':['table']});
  // Set a callback to run when the API is loaded.
  google.setOnLoadCallback(init);


  // Send the query to the data source.
  function init() {


    // Specify the data source URL.
    var timequery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=times&aspect=portal');
    var sdquery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=SD&aspect=portal');
    //++ userID as parameter

    // Send the query with a callback function.
    timequery.send(timeHandleQueryResponse);
    sdquery.send(sdHandleQueryResponse);

    // Specify the data source URL.
    var usertimequery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=times&aspect=user');
    var usersdquery = new google.visualization.Query('/statvisualizer/PortalStatServlet?target=SD&aspect=user');
    //++ userID as parameter

    // Send the query with a callback function.
    usertimequery.send(usertimeHandleQueryResponse);
    usersdquery.send(usersdHandleQueryResponse);

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
    chart.draw(data, {width: 600, height: 150});
  }

  function usersdHandleQueryResponse(response) {
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var sd_data = response.getDataTable();
        var sd_table = new google.visualization.Table(document.getElementById('sd_table_user_div'));
        sd_table.draw(sd_data, {showRowNumber: true});
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
    chart.draw(data, {width: 600, height: 150});
  }

  function sdHandleQueryResponse(response) {
    if (response.isError()) {
      alert('Error in query: ' + response.getMessage() + ' ' + response.getDetailedMessage());
      return;
    }

    // Draw the visualization.
    var sd_data = response.getDataTable();
        var sd_table = new google.visualization.Table(document.getElementById('sd_table_div'));
        sd_table.draw(sd_data, {showRowNumber: true});
  }
