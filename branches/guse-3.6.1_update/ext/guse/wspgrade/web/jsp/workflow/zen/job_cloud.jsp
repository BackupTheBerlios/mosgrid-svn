<%--
@call job.jsp
    Job egy cloud resource
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>

<form method="post" action="${sessionScope.ajaxSessionURL}" onsubmit="formsubmit(this,fakeSave);;return false;" id="job_cloudform">
    <input type="hidden" name="smsg" />

<%-- Cloud panel --%>
<div style="display:table-row;margin-top:1px;float:left">
    <div class="jobconfig-header"><msg:getText key="text.job.i7" />:</div>
    <div class="jobconfig-data">
        <select title="<msg:getText key="config.job.servicetype" />" ${ecloudtypes} name="job_cloudtype" id="job_cloudtype">
	    <c:forEach var="sty" items="${cloudtypes}">
		<c:choose>
		    <c:when test="${sty==icloud}">
    			<option selected="true"><c:out value="${sty}" escapeXml="true" /></option>
		    </c:when>
		    <c:otherwise>
			<option><c:out value="${sty}" escapeXml="true" /></option>
		    </c:otherwise>
		</c:choose>
	    </c:forEach>
	    </select>
    </div>

    <div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.gaeurl" />:</div>
    <div class="jobconfig-data">
        <input type="hidden" name="job_defaultgaeservice" value="${defaultGAEservice}" />
        <input type="text" size="100" name="job_gaeurl" value="${gaeurl}"/>
        <msg:toolTip id="cicc" tkey="config.job.serviceurl" img="${pageContext.request.contextPath}/img/tooltip.gif" />
    </div>    
    </div>
</div>



<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />

<%-- mentes gomb--%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div style="display:table-cell;width:900px">
	<center><input type="image" src="${pageContext.request.contextPath}/imgs/accept_64.png" onclick=""/></center>
    </div>
</div>
</form>
