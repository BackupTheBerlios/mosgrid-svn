<%--
Manager view
boolean ${status} service status
--%>
<%@taglib  prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<form method="post" action="conf">
<table class="newdata">
    <caption> <f:message key="caption.manager" /> </caption>
    <tr>
        <th><f:message key="manager.status" /></th>
        <td>
            <c:choose>
                <c:when test="${status}">
                    <input type="radio" name="status" value="0"> <f:message key="manager.inactive" />
                    <input type="radio" name="status" value="1" checked> <f:message key="manager.active" />
                </c:when>
                <c:otherwise>
                    <input type="radio" name="status" value="0" checked> <f:message key="manager.inactive" />
                    <input type="radio" name="status" value="1" > <f:message key="manager.active" />
                </c:otherwise>
            </c:choose>

        </td>
    </tr>
    <tr><td colspan="2"><input type="submit" value="<f:message key="form.ok" />" /></td></tr>
</table>
</form>
</f:bundle>