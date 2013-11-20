<%--
edit arc grid
Arc ${item} selected grid config
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<table class="newdata" >
    <tr>
        <th class="ln0"><f:message key="new.arc.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="new.arc.accessdata" /></th>
        <td class="ln1">${item.arc.accessdata}</td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.arc.type" /></th>
        <td class="ln0">${item.arc.type}</td>
    </tr>
</table>
</f:bundle>