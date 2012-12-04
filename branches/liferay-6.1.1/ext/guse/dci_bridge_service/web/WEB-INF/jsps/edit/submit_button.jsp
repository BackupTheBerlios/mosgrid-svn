<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<tr>
    <td><f:message key="resource.submit.usethis" /></td>
    <td>${item.forward.usethis=='true'}
        <c:choose>
            <c:when test="${item.forward.usethis=='true'}">
                <input type="checkbox" name="pusethis" checked="true" />
            </c:when>
            <c:otherwise>
                <input type="checkbox" name="pusethis" />
            </c:otherwise>
        </c:choose>
    </td>
</tr>

<tr>
    <td><f:message key="resource.submit.wsdl" /></td>
    <td>
        <c:set var="flag" value="0" />
        <c:forEach var="t" items="${item.forward.wsdl}">
            <c:set var="flag" value="1" />
            <input name="pwsdl" value="${t}" size="50" />
        </c:forEach>
        <c:if test="${flag=='0'}">
            <input name="pwsdl" value="${t}" size="50" />
        </c:if>
    </td>
</tr>

<tr>
        <td colspan="2" align="center">
            <a href="#" style="display:block" onclick="TINY.box.show(popUPForm('editForm','<f:message key="msg.really" />'),0,300,300,1);">
                <img src="imgs/save.png" /> <br />
                <f:message key="form.ok" />
            </a>
        </td>
</tr>
</f:bundle>