<%--
edit unicore resource
Unicore ${item} selected grid config
--%>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<table class="newdata" >
    <tr>
        <th class="ln0"><f:message key="new.unicore.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="unicore.keyalias" /></th>
        <td class="ln1">${item.unicore.keyalias}</td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="unicore.subjectdn" /></th>
        <td class="ln0">${item.unicore.subjectdn}</td>
    </tr>

</table>
</f:bundle>
