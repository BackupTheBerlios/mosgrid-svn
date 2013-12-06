<%--
edit Gae resource
Gae ${item} selected grid config
--%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<table class="newdata" >
    <tr>
        <th class="ln0"><f:message key="new.gae.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
</table>
</f:bundle>
