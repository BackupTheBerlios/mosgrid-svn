<%-- konfiguralt gUSE szervizek
List<GuseServiceBean> ${sessionScope.services} elerheto gUSE serrvize-ek
GuseServiceBean ${sessionScope.serviceitem} modositando szerviz
GuseServiceBean ${sessionScope.auth} jogosultsag allitas ezen a szervizen
--%>

<%@taglib prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >

    <p:defineObjects />

<%-- modositas --%>
    <c:if test="${sessionScope.serviceitem!=null}">
        <div id="editform" class="dataform" style="display:block;">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/service/edit.jsp" />
            <a href="#" onclick="javascript:showhide('editform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
        </div>
       <br /><br />
    </c:if>
<%-- property-k --%>
    <c:if test="${sessionScope.propertyitem!=null}">
        <div id="propertyform" class="dataform" style="display:block;">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/service/properties.jsp" />
            <a href="#" onclick="javascript:showhide('propertyform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
        </div>
       <br /><br />
    </c:if>
<%-- jogosultsag
    <c:if test="${sessionScope.auth!=null}">
        <div id="authform"  class="dataform" style="display:block;">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/service/authority.jsp" />
            <a href="#" onclick="javascript:showhide('authform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
        </div>
       <br /><br />
    </c:if>
 --%>
<%-- uj elem felvitele --%>
        <a href="#" id="insertform_link" onclick="javascript:showhide('insertform');" class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/add.png" /><br />
            <div>[<f:message key="action.new" />]</div>
        </a>
        <div id="insertform"  class="dataform" style="display:none">
            <jsp:include page="/WEB-INF/jsp/guseserviceadmin/service/new.jsp" />
            <a href="#" onclick="javascript:showhide('insertform');" class="icolink">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div>[<f:message key="action.cancel" />]</div>
            </a>
        </div>
       <br /><br />

<table border="1" width="100%">
    <caption><f:message key="caption.service" /></caption>
<tr>
    <th  width="30%"><f:message key="head.url" />(<f:message key="head.surl" />)</th>
    <th  width="10%"><f:message key="head.type" /></th>
    <th  width="15%"><f:message key="head.priv" /></th>
    <th  width="10%"><f:message key="head.status" /></th>
    <th  width="35%"><f:message key="head.actions" /></th>
</tr>
<c:forEach var="t" items="${sessionScope.services}">
<tr>
    <td>
        <c:out value="${t.url}" escapeXml="true"/>
        <c:if test="${t.surl!=null}"><c:out value="(${t.surl})" escapeXml="true"/></c:if>
    </td>
    <td style="text-align:center"><c:out value="${t.typ.sname}" escapeXml="true"/></td>
    <td>
        <c:set var="flag" value="0" />
        <c:forEach var="t0" items="${t.users}" >
            <c:set var="flag" value="1" />
            <c:out value="${t0.lname}" escapeXml="true"/> <br />
        </c:forEach>   
        <c:if test="${flag=='0'}"><f:message key="text.allusers" /></c:if>

    </td>
    <td style="text-align:center" align="center">
        <c:choose>
            <c:when test="${t.state}">
                <div style="display:block;text-align:center">
                    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/active.png" /><br />
                    (<f:message key="text.active" />)
                </div>
            </c:when>
            <c:otherwise>
                <div style="display:block;text-align:center">
                    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/inactive.png" /><br />
                    (<f:message key="text.inactive" />)
                </div>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <p:actionURL var="dsURL">
            <p:param name="dsid" value="${t.id}" />
        </p:actionURL>
            <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${dsURL}','<f:message key="msg.delete" />'),0,300,300,1);">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
            [<f:message key="action.delete" />]
        </a>
        <p:renderURL var="dsURL" >
            <p:param name="esid" value="${t.id}" />
        </p:renderURL>
        <a href="${dsURL}"  class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/edit.png" /><br />
            [<f:message key="action.edit" />]
        </a>
        <p:renderURL var="dsURL" >
            <p:param name="prsid" value="${t.id}" />
        </p:renderURL>
        <a href="${dsURL}"  class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/property.png" /><br />
            [<f:message key="action.property" />]
        </a>
        <p:actionURL var="dsURL" >
            <p:param name="prefreshid" value="${t.id}" />
        </p:actionURL>
        <a href="${dsURL}"  class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/refresh.png" /><br />
            [<f:message key="action.refreshservice" />]
        </a>
<%--
        <p:renderURL var="dsURL" >
            <p:param name="guse-render" value="service" />
            <p:param name="ssid" value="${t.id}" />
        </p:renderURL>
        <a href="${dsURL}"  class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/rights.png" /><br />
            [<f:message key="action.auth" />]
        </a>
        <p:renderURL var="dsURL" >
            <p:param name="psid" value="${t.id}" />
        </p:renderURL>
        <a href="${dsURL}"  class="icolink">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/ping.png" /><br />
            [<f:message key="action.ping" />]
        </a>
--%>
    </td>
</tr>
</c:forEach>
</table>
</f:bundle>