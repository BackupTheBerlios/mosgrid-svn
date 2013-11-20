<%--
@call job.jsp
    metabroker job
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

    <p:resourceURL var="ajaxURL">
    <p:param name="m" value="SaveAllData" />
    </p:resourceURL>
<%-- eroforras lista  --%>

    <input type="hidden" name="job_mbt" value="metabroker" />

    <c:set var="mbtidx" value="0" />
    <c:forEach var="tMiddleware" items="${sessionScope['pub_resources']}">
    <c:if test="${tMiddleware.type=='gt2' || tMiddleware.type=='gt4' || tMiddleware.type=='glite'}">
	<b style="font-size:13px; font-weight:bold; color:blue;padding-top:10px" >${tMiddleware.type}</b><br />
	<div id="gm_${tMiddleware.type}" style="border-top:1px solid #000000">
	<c:forEach var="tGroup" items="${tMiddleware.item}">
    	    <c:set var="mbtidx" value="${mbtidx+1}" />
    	    <c:set  var="tmp" value="${tMiddleware.type}_${tGroup.name}" />
    	    <c:set var="flag" value="false" />
    	    <c:forEach var="t" items="${resources}">
            	<c:if test="${t==tmp}"><c:set var="flag" value="true" /></c:if>
    	    </c:forEach>
    	    <c:choose>
        	<c:when test="${flag=='true'}">
            	    <input type="checkbox" checked name="job_mbt${mbtidx}" value="${tMiddleware.type}_${tGroup.name}"> ${tGroup.name}<br />
        	</c:when>
        	<c:otherwise>
            	    <input type="checkbox" name="job_mbt${mbtidx}" value="${tMiddleware.type}_${tGroup.name}"> ${tGroup.name}<br />
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


