<%-- 
    Document   : UserStats
    Created on : Apr 4, 2011, 1:53:41 PM
    Author     : Alessandra

    Displays User Metrics
    Connected to test.java
--%>



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%--"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"--%>

        
        <h1>User Statistics</h1>
   

<script type="text/javascript">

 	jQuery(function() {


	  	//jQuery("#accordion").accordion({ header: "h3" });
        $("#accordionUserStat").accordion({ header: "h3", autoHeight: false,navigation: true });

        $("#usertimetable").tablesorter();
	});

</script>

<h2 class="demoHeaders">User Statistics</h2>
		<div id="accordionUserStat">
			<div>
				<h3><a href="#">Overall User Statistics</a></h3>
				<div>
                    <table border="0" cellspacing="20" cellpadding="50" ALIGN="top">
                <tbody ALIGN="top">
                    <tr>
                        <td>
                            <h4>
                                <b>Number of Workflows: ${numofWorkflows}<br>
                                 ${userstatCategory1}<br>${userstatCategory3}</b>
                                
                            </h4>
                        </td>
                        <td>
                            <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${userfailrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                            <%--${userstatCategory8}--%>
                            
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
			</div>
			<div>
				<h3><a href="#">Details</a></h3>
                <div>
              
				<%--outs2 + outs7 --%> 
        
            <table border="0" cellspacing="20" cellpadding="50" ALIGN="top" width="100%">
                <tbody ALIGN="top">
                   <tr>
                        <td width="70%">
                            <div id="usertimetablediv">
                                    <table id="usertimetable" class="tablesorter" width="70%">
                                    <thead>
                                    <tr>
                                        <c:forEach items="${tguser.titlesForChart}" var="title">
                                            <th>${title}</th>
                                        </c:forEach>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${tguser.table}" var="row" >
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
                            <div id="time_chart_user_div">

                            </div>


                        </td>
                    </tr>
                </tbody>
            </table>
            </div>
   
			</div>

		</div>
