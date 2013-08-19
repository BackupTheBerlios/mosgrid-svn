<%--
@call job.jsp
    beagyazott workflow konfiguracio
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>

<form method="post" action="${sessionScope.ajaxSessionURL}" onsubmit="formsubmit(this,reloadPanel);return false;" id="job_workflowform">
    <input type="hidden" name="smsg" />

<%-- Workflow panel --%>
<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.22" />:</div>
    <div class="jobconfig-data">
    		<select title='<msg:getText key="config.job.iworkflow" />' ${eiworkflow} name="job_iworkflow" id="job_iworkflow" class="portlet-form-button" >
		<c:forEach var="tmp" items="${workflows}">
		    <c:if test="${tmp.value.template!='--'}">
		    <c:choose>
			<c:when test="${tmp.value.workflowID==iworkflow}">
			    <option selected="true" value="<c:out value="${tmp.value.workflowID}" escapeXml="true" />"><c:out value="${tmp.value.workflowID}" escapeXml="true" />(<c:out value="${fn:substring(tmp.value.template,0,10)}" escapeXml="true" />....) </option>
			</c:when>
			<c:otherwise>
			    <c:if test="${tmp.value.template!=''}" >
				<option value="<c:out value="${tmp.value.workflowID}" escapeXml="true" />"><c:out value="${tmp.value.workflowID}" escapeXml="true" /> (<c:out value="${fn:substring(tmp.value.template,0,10)}" escapeXml="true" />....)</option>
			    </c:if>
			</c:otherwise>
		    </c:choose>
		    </c:if>
		</c:forEach>
		</select>
	    <c:if test="${job.labelIWorkflow!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelIWorkflow}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	    <c:if test="${job.descIWorkflow!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descIWorkflow}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
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

