<jsp:include page="../core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<msg:help id="helptext" tkey="help.details" img="${pageContext.request.contextPath}/img/help.gif" />

<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />
<portlet:resourceURL var="ajaxURL" />
<script>
<!--
    var ajaxURL="${ajaxURL}";
-->
</script>

    <table width="100%">
        <tr>
	    <td colspan="8">
		<form method="post" action="${pURL}"> 
		    <input type="hidden" name="job" id="job">
		    <input type="hidden" name="workflow" id="workflow">
		    <input type="hidden" name="action" id="action">
		    <lpds:submit actionID="action" actionValue="doList" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.back" tkey="true" />		
		</form>
	    </td>
        </tr>
        <tr>
	    <td colspan="8">
		<table width="100%">
        <tr>
			<td width="20%">Workflow's name: </td>
			<td width="80%"><c:out value="${wrkdata.workflowID}" escapeXml="true" /></td>
        </tr>
    	<tr>
			<td width="20%">Workflow's description: </td>
			<td width="80%"><c:out value="${wrkdata.txt}" escapeXml="true" /></td>
        </tr>
		</table>
	</td>
	</tr>
	    <td>----${rtid}</td>
        <tr>
	</tr>
        <tr>
	    <td>Job</td>
	    <td>PID</td>
	    <td>Hostname</td>
	    <td>Status</td>
	    <td>[ Logs ]</td>
	    <td>[ Output ]</td>
	    <td>[ Visualization ]</td>
	    <td>[ Action ]</td>
	</tr>
	<c:forEach var="ajob" items="${aJobList}">
    	    <tr>
		<td>${ajob.value.name}</td>
		<td>${ajob.value.pid}</td>
		<td>${ajob.value.resource}</td>
		<td>${ajob.value.status}</td>
		<td>[ Logs ]</td>
		<td>[ Output ]</td>
		<td> -- </td>
		<td>[ Action ]</td>
    	    </tr>
	</c:forEach>    
    </table>
