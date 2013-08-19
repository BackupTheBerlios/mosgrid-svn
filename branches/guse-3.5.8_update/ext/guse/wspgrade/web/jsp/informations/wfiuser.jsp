<%--
${data} Vector<WorkflowInformationBean> Workflow lista
--%>

<%@taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:defineObjects/>
<portlet:renderURL var="pURL" portletMode="VIEW" />


<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >
<tr>
    <th><msg:getText key="portal.informations.wfi.wfiuser.0" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiuser.1" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiuser.2" /></th>
    <th><msg:getText key="portal.informations.wfi.wfiuser.3" /></th>
</tr>
<c:forEach var="workflow" items="${data}">
    <tr>
	<td class="portlet-section-body">
	<c:choose>
	    <c:when test="${workflow.userid==userid}">
            <bold> <c:out value="${workflow.workflowid}[${workflow.instancename}]" escapeXml="true" /></bold>
	    </c:when>
	    <c:otherwise>
		<italic><msg:getText key="portal.informations.wfi.wfiuser.4" /></italic>
	    </c:otherwise>
	</c:choose>    
	</td>
	<td class="portlet-section-body">${workflow.jobNumbers}</td>
	<td class="<msg:getText key="portal.WorkflowData.status.${workflow.status}" />"><msg:getText key="portal.WorkflowData.status.${workflow.status}" /></td>
	<td class="portlet-section-body" >
	<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0">
	    <c:forEach var="jobstatus" items="${workflow.statuses}">
		<c:if test="${jobstatus.key!=99}">
		<tr>
		    <td class="<msg:getText key="portal.WorkflowData.status.${jobstatus.key}" />" ><msg:getText key="portal.WorkflowData.status.${jobstatus.key}" /></td>
		    <td class="<msg:getText key="portal.WorkflowData.status.${jobstatus.key}" />" >${jobstatus.value}</td>
		</tr>
		</c:if>	
	    </c:forEach>
	</table>
	</td>
    </tr>
    <tr><td colspan="4" style="border-bottom:2px solid"> </td></tr>
</c:forEach>
</table>