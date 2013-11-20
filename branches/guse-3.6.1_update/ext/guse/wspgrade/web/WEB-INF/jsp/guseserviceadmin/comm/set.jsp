<%-- Communikacios tipus beallitasa
GuseServiceCommunicationBean ${sessionScope.comcom} beallitasra kivalsztaott peldany
List<GuseServiceTypeBean> ${styps} gUSE sezrviz tipusok
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >
<p:defineObjects />
<p:actionURL var="ncURL">
    <p:param name="scid" value="${com.id}" />
</p:actionURL>
<%-- uj elem felvitele --%>
        <a href="#" id="insertrightform_link" onclick="javascript:showhide('insertrightform');" class="icolink">
            <img id="insertrightform_icon" src="${pageContext.request.contextPath}/imgs/serviceadmin/user_add.png" /><br />
            <div id="insertrightform_button">[<f:message key="action.newchanel" />]</div>
        </a>

<div id="insertrightform" class="dataform" style="display:none">
<form method="post" action="${ncURL}">
<table border="1">
    <tr>
        <th><f:message key="head.srcservice" /></th>
        <td>
            <select name="pdservice">
                <c:forEach var="t" items="${typs}">
                    <option value="${t.id}"><c:out value="${t.sname}" /></option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <th><f:message key="head.dstservice" /></th>
        <td>
            <select name="psservice">
                <c:forEach var="t" items="${typs}">
                    <option value="${t.id}"><c:out value="${t.sname}" /></option>
                </c:forEach>
            </select>
        </td>
    </tr>
    <tr>
        <th><f:message key="head.serviceresource" /></th>
        <td><input type="text" size="50" name="pres"/></td>
    </tr>
    <tr>
        <th><f:message key="head.client" /></th>
        <td><input type="text" size="50" name="pcls"/></td>
    </tr>
</table>
    <input type="submit" value="<f:message key="action.save" />">
</form>
</div>


<table border="1">
    <caption><f:message key="caption.setcom" /></caption>
    <tr>
        <th><f:message key="head.name" /></th>
        <td><c:out value="${sessionScope.comcom.cname}" escapeXml="true" /></td>
    </tr>
    <tr>
        <th><f:message key="head.desc" /></th>
        <td><c:out value="${sessionScope.comcom.txt}" escapeXml="true" /></td>
    </tr>
    <tr>
        <th colspan="2"><f:message key="head.settings" /></th>
    </tr>
    <tr>
        <td colspan="2">
            <table border="1" width="100%">
                <tr>
                    <th><f:message key="head.dstservice" /></th>
                    <th><f:message key="head.srcservice" /></th>
                    <th><f:message key="head.serviceresource" /></th>
                    <th><f:message key="head.client" /></th>
                    <th><f:message key="head.actions" /></th>
                 </tr>
            <c:forEach var="t" items="${sessionScope.comcom.resources}" >
                <tr>
                    <td><c:out value="${t.src.sname}" escapeXml="true" /></td>
                    <td><c:out value="${t.dst.sname}" escapeXml="true" /></td>
                    <td><c:out value="${t.res}" escapeXml="true" /></td>
                    <td><c:out value="${t.caller}" escapeXml="true" /></td>
                    <td>
            <p:actionURL var="dsURL">
                <p:param name="dsrid" value="${t.id}" />
                <p:param name="scid" value="${com.id}" />
            </p:actionURL>
            <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${dsURL}','<f:message key="msg.delete" />'),0,300,300,1);">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
            [<f:message key="action.delete" />]
        </a>
                    </td>
                </tr>
            </c:forEach>
            </table>
        </td>
    </tr>

</table>
</f:bundle>