<%--
Rendelkezesre allo service kommunikacios lehetosegek  megjelenitese
List<GuseServiceTypeBean> ${coms} rendelkezesre allo service tipusok
GuseServiceTypeBean ${sessionScope.comcom} beallitando configuracio
GuseServiceTypeBean ${sessionScope.comitem} szerkesztendo configuracio

--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >
<p:defineObjects />

<%-- settings --%>
<c:if test="${sessionScope.comcom!=null}">
        <div id="setform"  class="dataform" style="display:block;">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/comm/set.jsp" />
            <a href="#" onclick="javascript:showhide('setform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
        </div>
       <br /><br />
    </c:if>
<%-- edit --%>
    <c:if test="${sessionScope.comitem!=null}">
        <div id="editform" class="dataform" style="display:block;">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/comm/edit.jsp" />
            <a href="#" onclick="javascript:showhide('editform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
        </div>
       <br /><br />
    </c:if>
<%-- uj elem felvitele --%>
        <a href="#" id="insertform_link" onclick="javascript:showhide('insertform');" class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/add.png" /> <br/>
            <div>[<f:message key="action.new" />]</div>
        </a>
        <div id="insertform"  class="dataform" style="display:none">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/comm/new.jsp" />
            <a href="#" onclick="javascript:showhide('insertform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
       </div>

<%-- lista --%>
<table border="1" width="100%" >
    <caption><f:message key="caption.com" /></caption>
    <tr>
        <th width="25%"><f:message key="head.name" /></th>
        <th width="50%"><f:message key="head.desc" /></th>
        <th width="25%"><f:message key="head.actions" /></th>
    </tr>
    <c:forEach var="t" items="${coms}" >
    <tr>
        <td><c:out value="${t.cname}" escapeXml="true" /></td>
        <td><c:out value="${t.txt}" escapeXml="true" /></td>
        <td>
            <p:actionURL var="dstURL">
                <p:param name="dcid" value="${t.id}" />
            </p:actionURL>
            <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${dstURL}','<f:message key="msg.delete" />'),0,300,300,1);">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
                [<f:message key="action.delete" />]
             </a>
            <p:renderURL var="dsURL">
                <p:param name="ecid" value="${t.id}" />
                <p:param name="guse-render" value="comm" />
            </p:renderURL>
            <a href="${dsURL}" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/edit.png" /><br />
                [<f:message key="action.edit" />]
            </a>
            <p:renderURL var="dsURL" >
                <p:param name="scid" value="${t.id}" />
                <p:param name="guse-render" value="comm" />
            </p:renderURL>
            <a href="${dsURL}"  class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/set.png" /><br />
                [<f:message key="action.set" />]
            </a>
        </td>
    </tr>
    </c:forEach>
</table>
</f:bundle>