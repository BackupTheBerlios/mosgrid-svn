<%--
new service cluster
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata" >
    <caption><f:message key="caption.service.edit" /></caption>
    <form  id="editForm" method="post" action="conf">
    <tr>
        <th><f:message key="new.service.name" /></th>
        <td><input type="text" disabled="true" name="pservicename" size="25" value="${item.name}"/></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
                <c:choose>
                <c:when test="${item.enabled}">
                    <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
                    <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
                </c:when>
                <c:otherwise>
                    <input type="radio" name="penabled" value="1" /><f:message key="general.yes" />
                    <input type="radio" name="penabled" checked="true" value="0" /><f:message key="general.no" />
                </c:otherwise>
            </c:choose>
        </td>
    </tr>

    <tr>
        <td colspan="2"><input type="submit" value="<f:message key="form.ok" />" /></td>
    </tr>
</form>
</table>
</f:bundle>