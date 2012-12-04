<%--
${servicelist} Vector<ServiceType>
--%>

<%@taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:defineObjects/>

<c:forEach var="wfi" items="${servicelist}">
    <portlet:renderURL var="pURL">
	<portlet:param name="service" value="${wfi.service.URL}" />
    </portlet:renderURL>

    <a href="${pURL}">${wfi.service.URL}</a><br/>
</c:forEach>