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
    <caption><f:message key="caption.arc.edit" /></caption>
    <form id="editForm" method="post" action="conf?editing=${item.name}">
    <tr>
        <th class="ln0"><f:message key="new.arc.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="parcname" size="25" value="${item.name}" /></td>
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
        <th class="ln0"><f:message key="new.arc.accessdata" /></th>
        <td class="ln0"><input class="ln1" type="text" name="paccessdata" size="60" value="${item.arc.accessdata}" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="new.arc.type" /></th>
        <td class="ln1"><input class="ln0" type="text" name="ptype" size="60" value="${item.arc.type}" /></td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.arc.config" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pconfig" size="60" value="${item.arc.configpath}" /></td>
    </tr>
    <jsp:include page="submit_button.jsp" />
</form>
</table>
</f:bundle>