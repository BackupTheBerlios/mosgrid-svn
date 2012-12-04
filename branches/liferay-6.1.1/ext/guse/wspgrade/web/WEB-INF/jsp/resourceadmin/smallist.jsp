<%--
Eroforraslistak
String ${sessionScope.midleware} kivalasztott midleware
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="resource"  >

<script> var ajaxURL="${rURL}"; </script>

<table border="1" width="100%">
    <tr>
        <th><f:message key="head.group.${sessionScope.midleware}" /></th>
    </tr>
<c:forEach var="t" items="${sessionScope.grids}" >
    <tr>
        <td><c:out value="${t}" escapeXml="true" /></td>
     </tr>
</c:forEach>

</table>
</f:bundle>
