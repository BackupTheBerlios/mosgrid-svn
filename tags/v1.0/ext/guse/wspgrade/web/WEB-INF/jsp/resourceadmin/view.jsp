<%--
List<String> ${sessionScope.midlewares} elerheto midleware-ek listaja
String ${rendertype} megjelenitendo midleware konfiguracios felulet
--%>


	<style type="text/css" media="screen">@import "${pageContext.request.contextPath}/css/tabs.css";</style>

<%@taglib  prefix="p" uri="http://java.sun.com/portlet" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<br />
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
    var popUP_OK="Igen";
    var popUP_NO="Nem";
    var webapp="${pageContext.request.contextPath}";
</script>
<br />
<br />
<br />
 <%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>

<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="resource"  >

<div id="header" style="width:100%">
	<ul id="primary" style="width:100%">




<%-- MENU--%>
<c:forEach var="t" items="${sessionScope.midlewares}">
    <c:choose>
        <c:when test="${rendertype==t}">
            <li><span><img src="${pageContext.request.contextPath}/imgs/midlewares/<c:out value="${t}" escapeXml="true" />.png" /><br/>${t}</span></li>
        </c:when>
        <c:otherwise>
            <p:renderURL var="mURL" >
                <p:param name="guse-render" value="${t}" />
            </p:renderURL>
        <li>
            <a href="${mURL}" >
                <img src="${pageContext.request.contextPath}/imgs/midlewares/<c:out value="${t}" escapeXml="true" />.png" /><br/>
                <f:message key="midleware.${t}" />
            </a>
        </li>
        </c:otherwise>
    </c:choose>
</c:forEach>
	</ul>
	</div>
 </f:bundle>


<%-- tartalom --%>
<div id="main">
    <div id="contents">
        <jsp:include page="/WEB-INF/jsp/resourceadmin/middlewares/${rendertype}.jsp" />
<c:if test="${msg!=null}">
    <f:bundle basename="sysmessage"  >

    <br />
    <br />
    <b><f:message key="text.msg" /></b><i><f:message key="${msg}" /></i>
    </f:bundle>
</c:if>
    </div>
</div>
