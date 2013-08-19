<%--
new lsf cluster
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata" >
    <caption><f:message key="caption.lsf.new" /></caption>
    <form method="post" action="conf">
    <tr>
        <th class="ln0"><f:message key="new.lsf.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="plsfname" size="25" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
            <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
            <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
        </td>
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
        <th class="${css}"><f:message key="new.lsf.queue" /></th>
        <td class="${css}"><input class="${css}" type="text" name="pqueue_${t}" size="50" /></td>
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