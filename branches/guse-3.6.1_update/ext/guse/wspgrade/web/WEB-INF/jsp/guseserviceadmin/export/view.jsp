
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >

<p:defineObjects />
<p:resourceURL var="pURL" />
<a href="${pURL}"><f:message key="export.download" /></a>

</f:bundle>
