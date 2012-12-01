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
    <caption><f:message key="caption.unicore.edit" /></caption>
    <form id="editForm" method="post" action="conf?editing=${item.name}">
    <tr>
        <th class="ln0"><f:message key="new.unicore.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="punicorename" size="25" value="${item.name}" /></td>
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
        <th class="ln0"><f:message key="unicore.keystore" /></th>
        <td class="ln0">
            <input type="text" name="pkeystore" value="${item.unicore.keystore}" />
        </td>
    </tr>

    <tr>
        <th class="ln1"><f:message key="unicore.keypass" /></th>
        <td class="ln1">
            <input type="password" name="pkeypass" value="${item.unicore.keypass}" />
        </td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="unicore.keyalias" /></th>
        <td class="ln0">
            <input type="text" name="pkeyalias"  value="${item.unicore.keyalias}" />
        </td>
    </tr>

    <tr>
        <th class="ln1"><f:message key="unicore.subjectdn" /></th>
        <td class="ln1">
            <input type="text" name="psubjectdn" value="${item.unicore.subjectdn}" />
        </td>
    </tr>

    <tr>
        <th class="ln0"><f:message key="unicore.truststore" /></th>
        <td class="ln0">
            <input type="text" name="ptruststore" value="${item.unicore.truststore}" />
        </td>
    </tr>

    <tr>
        <th class="ln1"><f:message key="unicore.trustpass" /></th>
        <td class="ln1">
            <input type="password" name="ptrustpass" value="${item.unicore.trustpass}" />
        </td>
    </tr>
    <jsp:include page="submit_button.jsp" />
</form>
</table>
</f:bundle>
