<%--
edit gbac grid
Gbac ${item} kivalasztott gbac grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<form  id="editForm" method="post" action="conf?editing=${item.name}">
<table class="editdata" >
    <caption><f:message key="caption.boinc.edit" /></caption>
    <tr>
        <th><f:message key="new.boinc.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pboincname" size="25" value="${item.name}" /></td>
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
        <th><f:message key="new.boinc.id" /></th>
        <td class="ln1"><input class="ln0" type="text" name="pboincid" size="25"  value="${item.gbac.id}"/></td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.wsdl" /></th>
        <td class="ln0"><input class="ln1" type="text" name="pwsdl" size="60"  value="${item.gbac.wsdl}" /></td>
    </tr>
    <tr>
        <th><f:message key="new.gbac.xml" /></th>
        <td class="ln1"><input class="ln0" type="text" name="pxml" size="60"  value="${item.gbac.rundescurl}" /></td>
    </tr>

    <jsp:include page="submit_button.jsp" />
</table>
</form>
</f:bundle>