<%--
edit gbac grid
gbac ${item} kivalasztott boinc grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="editdata" >
    <tr>
        <th><f:message key="new.boinc.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.id" /></th>
        <td class="ln1">${item.gbac.id}</td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.wsdl" /></th>
        <td class="ln0">${item.gbac.wsdl}</td>
    </tr>

    <tr>
        <th><f:message key="new.gbac.xml" /></th>
        <td class="ln0">${item.gbac.rundescurl}</td>
    </tr>
</table>
</f:bundle>