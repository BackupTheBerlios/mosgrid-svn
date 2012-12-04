<%--
edit glite vo
Glite ${item} selected VO config
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<table class="newdata" >
    <tr>
        <th class="ln0"><f:message key="new.glite.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="new.glite.accessdata" /></th>
        <td class="ln1">${item.glite.accessdata}</td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.glite.type" /></th>
        <td class="ln0">${item.glite.type}</td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="new.glite.bdii" /></th>
        <td class="ln1">${item.glite.bdii}</td>
    </tr>
    <tr>
        <th class="ln0"><f:message key="new.glite.wms" /></th>
        <td class="ln0">${item.glite.wms}</td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="new.glite.lfc" /></th>
        <td class="ln1">${item.glite.lfc}</td>
    </tr>
</table>
</f:bundle>