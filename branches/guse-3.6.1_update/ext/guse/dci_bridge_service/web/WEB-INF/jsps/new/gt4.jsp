<%--
new gt4 grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata" >
    <caption><f:message key="caption.gt4.new" /></caption>
    <form method="post" action="conf">
    <tr>
        <th class="ln0"><f:message key="new.gt4.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pgt4name" size="25" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
            <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
            <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
        </td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.gt4.accessdata" /></th>
        <td class="ln0"><input class="ln0"type="text" name="paccessdata" size="60" /></td>
    </tr>
    <tr>
        <th><f:message key="new.gt4.type" /></th>
        <td class="ln1"><input class="ln1" type="text" name="ptype" size="60" /></td>
    </tr>
    <c:forEach var="t" begin="0" end="10" varStatus="ln">
        <c:choose>
            <c:when test="${ln.index%2==0}">
                <c:set var="css" value="ln0" />
            </c:when>
            <c:otherwise>
                <c:set var="css" value="ln1" />
            </c:otherwise>
        </c:choose>
    <tr>
        <th class="${css}"><f:message key="new.gt4.site"  /></th>
        <td class="${css}"><input  class="${css}" type="text" name="phost_${t}" size="30" />
                <input  class="${css}" type="text" name="pjobmanager_${t}" size="30" /></td>
    </tr>
    </c:forEach>

    <tr>
        <td colspan="2"><input type="image" src="imgs/save.png" value="<f:message key="form.ok" />" /><br />
            (<f:message key="form.ok" />)
        </td>
    </tr>
</form>
</table>
</f:bundle>
