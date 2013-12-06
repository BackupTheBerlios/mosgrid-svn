<%--
@call job.jsp
    Job egy szerviz
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<%-- Service panel --%>
<form method="post" action="${sessionScope.ajaxSessionURL}" onsubmit="formsubmit(this,reloadPanel);return false;" id="job_serviceform">
    <input type="hidden" name="smsg" />
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.17" />:</div>
    <div class="jobconfig-data">
	<select title='<msg:getText key="config.job.servicetype" />' ${eservicetype} name="job_servicetype" id="job_servicetype" onChange="getRemoteSelectOptions('j='+document.getElementById('job_servicetype').value+'&m=GetWebServices')">
	    <c:forEach var="sty" items="${servicetypes}">
		<c:choose>
		    <c:when test="${sty==iservicetype}">
    			<option selected="true"><c:out value="${sty}" escapeXml="true" /></option>
		    </c:when>
		    <c:otherwise>
			<option><c:out value="${sty}" escapeXml="true" /></option>
		    </c:otherwise>
		</c:choose>
	    </c:forEach>
	    </select>
	<c:if test="${job.labelServiceType!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelServiceType}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	<c:if test="${job.descServiceType!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descServiceType}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>    
    </div>
</div>    
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.18" />:</div>
    <div class="jobconfig-data">
        <input title='<msg:getText key="config.job.serviceurl" />' type="text" size="50" ${eserviceurl} name="job_serviceurl" id="job_serviceurl" value="${iserviceurl}" />
        <c:if test="${job.labelServiceUrl!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelServiceUrl}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
        <c:if test="${job.descServiceUrl!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descServiceUrl}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>    
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.19" />:</div>
    <div class="jobconfig-data">
        <input title='<msg:getText key="config.job.servicemethod" />' type="text" size="20" ${eservicemethod} name="job_servicemethod" id="job_servicemethod" value="${iservicemethod}" />
        <c:if test="${job.labelServiceMethod!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelServiceMethod}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
        <c:if test="${job.descServiceMethod!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descServiceMethod}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
    </div>
</div>    


<%-- all job panel --%>
<jsp:include page="/jsp/workflow/zen/useall.jsp" />

<%-- mentes gomb--%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div style="display:table-cell;width:900px">
	<center><input type="image" src="${pageContext.request.contextPath}/imgs/accept_64.png" /></center>
    </div>
</div>

</form>
