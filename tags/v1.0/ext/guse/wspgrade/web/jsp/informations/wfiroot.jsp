<%--
${data} Vector<WorkflowInformationBean> Workdlow lita
--%>

<%@taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:defineObjects/>
<portlet:actionURL var="aURL" portletMode="VIEW">
    <portlet:param name="guse" value="doAbort" />
</portlet:actionURL>

<portlet:actionURL var="cURL" portletMode="VIEW" >
    <portlet:param name="guse" value="doConfigure" />
</portlet:actionURL>

<c:set var="sum" value="0" />
<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >
<tr>
    <th><msg:getText key="portal.informations.wfi.wfiroot.0" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.1" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.2" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.3" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.4" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.5" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.6" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiroot.7" /></th>
</tr>
<c:forEach var="workflow" items="${data}">
    <tr>
        <td class="portlet-section-body"><c:out value="${workflow.portalid}" escapeXml="true" /></td>
	<td class="portlet-section-body"><c:out value="${workflow.userid}" escapeXml="true" /></td>
	<td class="portlet-section-body"><c:out value="${workflow.workflowid}" escapeXml="true" /></td>
	<td class="portlet-section-body"><c:out value="${workflow.instancename}" escapeXml="true" /></td>
	<td class="portlet-section-body"><c:out value="${workflow.jobNumbers}" escapeXml="true" /></td>
	<c:set var="sum" value="${sum+workflow.jobNumbers}" />

	<td class="<msg:getText key="portal.WorkflowData.status.${workflow.status}" />"><msg:getText key="portal.WorkflowData.status.${workflow.status}" /></td>
	<td class="portlet-section-body" >
	<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0">
	    <c:forEach var="jobstatus" items="${workflow.statuses}">
		<tr>
		    <td class="<msg:getText key="portal.WorkflowData.status.${jobstatus.key}" />" ><msg:getText key="portal.WorkflowData.status.${jobstatus.key}" /></td>
		    <td class="<msg:getText key="portal.WorkflowData.status.${jobstatus.key}" />" >${jobstatus.value}</td>
		</tr>
	    </c:forEach>
	</table>
	</td>
	<td>
	    <c:choose>
		<c:when test="${portalid==workflow.portalid}" >
		    <form method="post" action="${cURL}" >
                <input type="hidden" name="adminuser" value="<c:out value="${workflow.userid}" escapeXml="true" />" />
            <input type="hidden" name="workflow" value="<c:out value="${workflow.workflowid}" escapeXml="true" />" />
			<input type="submit" value="*Configure*" />
		    </form>

		    <form method="post" action="${aURL}" >
                <input type="hidden" name="adminuser" value="<c:out value="${workflow.userid}" escapeXml="true" />" />
            <input type="hidden" name="workflow" value="<c:out value="${workflow.workflowid}" escapeXml="true" />" />
            <input type="hidden" name="rtid" value="<c:out value="${workflow.runtimeid}" escapeXml="true" />" />
			<input type="submit" value="*Abort*" />
		    </form>
		</c:when>
		<c:otherwise>
		    <msg:getText key="portal.informations.wfi.wfiroot.8" />	
		</c:otherwise>	
	    </c:choose>
	</td>
    </tr>
    <tr><td colspan="8" style="border-bottom:2px solid"> </td></tr>
</c:forEach>
    <tr>
	<th colspan="4"> <center>*SUM*</center> </th>
	<th style="float:left">${sum}</th>
	<th colspan="3"> --</th>
    </tr>
</table>