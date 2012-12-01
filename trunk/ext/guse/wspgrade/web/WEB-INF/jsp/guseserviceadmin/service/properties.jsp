<%-- 
GuseServiceBean ${sessionScope.propertyitem} kivalasztott service
HashMap ${props} service property hash
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >
<%-- uj elem felvitele/szerkesztese --%>
<script language="JavaScript">
    function editProprety(pKey, pValue)
    {
        document.getElementById("insertpropertyform").style.display="block";
        document.getElementById("ppropkey").value=pKey;
        document.getElementById("ppropvalue").value=pValue;
    }
</script>
<a href="#" id="insertform_link" onclick="javascript:showhide('insertpropertyform');" class="icolink">
    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/add.png" /><br />
    <div>[<f:message key="action.new" />]</div>
</a>

<div id="insertpropertyform"  class="dataform" style="display:none">
    <p:actionURL var="npURL">
        <p:param name="guse" value="newproperty" />
    </p:actionURL>
    <form method="post" action="${npURL}">
    <table border="1">
        <caption><f:message key="caption.newservice" /></caption>
        <tr>
            <th><f:message key="head.propkey" /></th>
            <td><input type="text" id="ppropkey" name="ppropkey" value="<c:out value="${propkey}" escapeXml="true" />" /></td>
        </tr>
        <tr>
            <th><f:message key="head.propvalue" /></th>
            <td><input type="text" id="ppropvalue" name="ppropvalue" value="<c:out value="${propvalue}" escapeXml="true" />" /></td>
        </tr>
        <tr> <td colspan="2"><input type="submit" value="<f:message key="action.save" />"></tr>
    </table>
    </form>
    <a href="#" onclick="javascript:showhide('insertpropertyform');" class="icolink">
    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
    <div>[<f:message key="action.cancel" />]</div>
    </a>
</div>
<br /><br />

<%--property lista--%>
<table border="1">
    <caption><f:message key="caption.serviceproperties" /></caption>
    <tr>
        <th><f:message key="head.propkey" /></th>
        <th><f:message key="head.propvalue" /></th>
        <th><f:message key="head.actions" /></th>
    </tr>
<c:forEach var="t" items="${props}">
    <tr>
        <td><c:out value="${t.key}" escapeXml="true" /></td>
        <td><c:out value="${t.value}" escapeXml="true" /></td>
        <td>
            <p:actionURL var="dsURL">
                <p:param name="dppid" value="${t.key}" />
            </p:actionURL>
                <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${dsURL}','<f:message key="msg.delete" />'),0,300,300,1);">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
                [<f:message key="action.delete" />]
            </a>
            <a href="#"  class="icolink" onclick="editProprety('${t.key}', '${t.value}')">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/edit.png" /><br />
                [<f:message key="action.edit" />]
            </a>
        </td>
    </tr>
</c:forEach>
</table>
</f:bundle>