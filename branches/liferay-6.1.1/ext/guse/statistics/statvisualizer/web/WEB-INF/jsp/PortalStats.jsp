



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%--
This page is the opening page for the statistics portlet.
It retreives portal metrics.
Connected to test.java
--%>
<html>
    <portlet:resourceURL var="resURL" />
    <portlet:renderURL var="rURL" />
    <portlet:actionURL var="uploadURL" />
    <portlet:resourceURL var="ajaxURL" />
    <portlet:defineObjects/>

    <portlet:actionURL var="pURL" portletMode="VIEW" />
    <head></head>
    <body>


 

<script type="text/javascript">

 	jQuery(function() {


	  	//jQuery("#accordion").accordion({ header: "h3" });
       $("#accordionPortalStat").accordion({ header: "h3",autoHeight: false,navigation: true });

       $("#portaltimetable").tablesorter();
	});

</script>



<h2 class="demoHeaders">Portal Statistics</h2>
		<div id="accordionPortalStat">
			<div>
				<h3><a href="#">Overall Portal Statistics</a></h3>
				<div>

                 <div id=tbl style="overflow:hidden;display:block">
            <table border="0" cellspacing="20" cellpadding="50" ALIGN="top" width ="100%">
                <tbody ALIGN="top">
                    <tr>
                        <td>
                            <h4>
                                <b>${statCategory1}
                                <br>${statCategory3}</b>

                            </h4><br>
                        </td>
                        <td>
                         <img src="https://chart.googleapis.com/chart?chs=300x150&chxt=x,y&chf=bg,s,EEF0F2&chco=00FF00,FFFF00,FF0000&chd=t:${portalfailrate}&cht=gom&chtt=FailureRate[%]&chxt=y&chxl=0:|0%|100%\" alt=\"\"/>
                          
                        </td>
                    </tr>
                </tbody>
            </table><br></div>




                </div>
			</div>
			<div>
				<h3><a href="#">Details</a></h3>
				<div id=tbl2 style="overflow:hidden;display:block">
            <table border="0" cellspacing="20" cellpadding="50" ALIGN="top" width="100%">
                <tbody ALIGN="top">
                    <tr>
                        <td width="70%">
                                <div id="portaltimetablediv">
                                    <table id="portaltimetable" class="tablesorter" width="70%">
                                    <thead>
                                    <tr>
                                        <c:forEach items="${tg.titlesForChart}" var="title">
                                            <th>${title}</th>
                                        </c:forEach>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${tg.table}" var="row" >
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
                            <div id="time_chart_div">

                            </div>


                        </td>
                    </tr>

                </tbody>
            </table><br></div>
			</div>

		</div>


            
        
    </body>
</html>