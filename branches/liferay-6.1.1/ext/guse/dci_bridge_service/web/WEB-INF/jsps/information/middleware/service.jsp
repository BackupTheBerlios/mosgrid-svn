<%--
new service cluster
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata" >
    <tr>
        <th><f:message key="new.service.name" /></th>
        <td>${item.name}</td>
    </tr>

</table>
</f:bundle>