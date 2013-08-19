<%--
    Service adminisztracios menu
--%>
	<style type="text/css" media="screen">@import "${pageContext.request.contextPath}/css/tabs.css";</style>

<%@taglib  prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="serviceadmin"  >


<p:renderURL var="m0URL" >
    <p:param name="guse-render" value="service" />
</p:renderURL>

<p:renderURL var="m1URL" >
    <p:param name="guse-render" value="comm" />
</p:renderURL>

<p:renderURL var="m2URL" >
    <p:param name="guse-render" value="type" />
</p:renderURL>
<p:renderURL var="m3URL" >
    <p:param name="guse-render" value="import" />
</p:renderURL>
<p:renderURL var="m4URL" >
    <p:param name="guse-render" value="export" />
</p:renderURL>
<br /><br /><br />
<style>
    .icolink
    {
        float:left;
        text-align:center;
    }
    .dataform TABLE
    {
        float:left;
        text-align:left;
    }

    .dataform
    {
        float:left;
        width:100%;
        border-bottom-style:solid;
        border-bottom-width:1px;
        margin-bottom:20px;
    }

</style>
<script language="javascript" src="${pageContext.request.contextPath}/designe.js"> </script>
<script type="text/javascript" src="${pageContext.request.contextPath}/tinybox.js"></script>
<style type="text/css" media="screen">@import "${pageContext.request.contextPath}/css/style.css";</style>
<script language="javascript">
    function showhide(pValue)
    {
        var status=document.getElementById(pValue).style.display;
        if(status!="block")
        {
            document.getElementById(pValue).style.display="block";
            document.getElementById(pValue+'_link').style.display="none";
        }
        else
        {
            document.getElementById(pValue).style.display="none";
            try{document.getElementById(pValue+'_link').style.display="block";}
            catch(e){/*csak a bezaras tamogatott*/}
        }
    }
    var popUP_OK="<f:message key="action.yes" />";
    var popUP_NO="<f:message key="action.no" />";
    var webapp="${pageContext.request.contextPath}";
</script>
<div id="header">
	<ul id="primary">

<c:choose>
    <c:when test="${rendertype=='service' }">
		<li><span>
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/service.png" /><br/>
                <f:message key="menu.service" />
        </span></li>
        </c:when>
        <c:otherwise>
		<li><a href="${m0URL}">
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/service.png" /><br/>
                <f:message key="menu.service" />
        </a></li>
        </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${rendertype=='comm' }">
		<li><span>
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/comm.png" /><br/>
                <f:message key="menu.com" />
        </span></li>
    </c:when>
    <c:otherwise>
        <li><a href="${m1URL}" >
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/comm.png" /><br/>
                <f:message key="menu.com" />
        </a></li>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${rendertype=='type'}">
        <li><span>
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/type.png" /><br/>
                <f:message key="menu.type" />
        </span></li>
    </c:when>
    <c:otherwise>
        <li><a href="${m2URL}" >
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/type.png" /><br/>
                <f:message key="menu.type" />
        </a></li>            
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${rendertype=='import'}">
        <li><span>
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/type.png" /><br/>
                <f:message key="menu.import" />
        </span></li>
    </c:when>
    <c:otherwise>
        <li><a href="${m3URL}" >
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/type.png" /><br/>
                <f:message key="menu.import" />
        </a></li>
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${rendertype=='export'}">
        <li><span>
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/export.png" /><br/>
                <f:message key="menu.export" />
        </span></li>
    </c:when>
    <c:otherwise>
        <li><a href="${m4URL}" >
                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/export.png" /><br/>
                <f:message key="menu.export" />
        </a></li>
    </c:otherwise>
</c:choose>

	</ul>
	</div>
<%-- tartalom --%>
<div id="main">
    <div id="contents">
<c:choose>
    <c:when test="${rendertype=='comm' }">
        <jsp:include page="/WEB-INF/jsp/guseserviceadmin/comm/view.jsp" />
    </c:when>
    <c:when test="${rendertype=='type'}">
        <jsp:include page="/WEB-INF/jsp/guseserviceadmin/type/view.jsp" />
    </c:when>
    <c:when test="${rendertype=='import'}">
        <jsp:include page="/WEB-INF/jsp/guseserviceadmin/import/view.jsp" />
    </c:when>
    <c:when test="${rendertype=='export'}">
        <jsp:include page="/WEB-INF/jsp/guseserviceadmin/export/view.jsp" />
    </c:when>
    <c:otherwise>
        <jsp:include page="/WEB-INF/jsp/guseserviceadmin/service/view.jsp" />
    </c:otherwise>
</c:choose>
</f:bundle>
<c:if test="${msg!=null}">
    <f:bundle basename="sysmessage"  >

    <br />
    <br />
    <b><f:message key="text.msg" /></b><i><f:message key="${msg}" /></i>
    </f:bundle>
</c:if>
    </div>
</div>



