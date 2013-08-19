<%@taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:defineObjects/>

<c:forEach var="job" items="${jobs}">
    <portlet:renderURL var="pURL">
	<portlet:param name="sjobid" value="${job.jobRuntimeID}" />
    </portlet:renderURL>

    <a href="${pURL}">${job.workflowid}-${job.jobID}/${job.pid}</a><br/>
    <c:forEach var="input" items="${job.inputs}">
	${input.name}(${input.intname})<br/>
    </c:forEach>
</c:forEach>