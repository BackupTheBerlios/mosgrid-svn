<%--
@call job.jsp
    metabroker job
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<form method="post" action="${sessionScope.ajaxSessionURL}" onsubmit="saveGridJobData(this,'${jobID}');return false;" id="job_gridform">
    <input type="hidden" name="smsg" />

<%-- eroforras lista  --%>

    <input type="hidden" name="job_mbt" value="metabroker" />

    <c:set var="mbtidx" value="0" />
    <c:forEach var="tMiddleware" items="${sessionScope['pub_resources']}">
    <c:if test="${tMiddleware.key=='gt2' || tMiddleware.key=='gt4' || tMiddleware.key=='glite'}">
	<b style="font-size:13px; font-weight:bold; color:blue;padding-top:10px">${tMiddleware.key}</b><br />
	<div id="gm_${tMiddleware.key}" style="border-top:1px solid #000000">
	<c:forEach var="tGroup" items="${tMiddleware.value}">
    	    <c:set var="mbtidx" value="${mbtidx+1}" />
    	    <c:set  var="tmp" value="${tMiddleware.key}_${tGroup.key}" />
    	    <c:set var="flag" value="false" />
    	    <c:forEach var="t" items="${resources}">
            	<c:if test="${t==tmp}"><c:set var="flag" value="true" /></c:if>
    	    </c:forEach>
    	    <c:choose>
        	<c:when test="${flag=='true'}">
            	    <input type="checkbox" checked name="job_mbt${mbtidx}" value="${tMiddleware.key}_${tGroup.key}"> ${tGroup.key}<br />
        	</c:when>
        	<c:otherwise>
            	    <input type="checkbox" name="job_mbt${mbtidx}" value="${tMiddleware.key}_${tGroup.key}"> ${tGroup.key}<br />
        	</c:otherwise>
            </c:choose>
	</c:forEach>
	</div>
    </c:if>	
    </c:forEach>
    <div style="border-top:1px solid #000000">&nbsp;</div>
    
<%-- middleware specifikus konfiguracios adatok--%>
<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />
<jsp:include page="/jsp/workflow/zen/myjob.jsp" />  
<%-- mentes gomb--%>

<table width="100%">
    <tr><td colspan="2"><div class="iodline" /></td></tr>
    <tr>
        <td colspan="2">
            <input type="submit" class="portlet-form-button" value="Save.." />
        </td>
    </tr>
 </table>
</form>

