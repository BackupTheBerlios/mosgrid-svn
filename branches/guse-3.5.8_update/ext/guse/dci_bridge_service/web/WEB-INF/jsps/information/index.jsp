<%--
DCI BRIDGE main config page
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

<head>
    <title>DCI BRIDGE</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <c:choose>
        <c:when test="${sessionScope['css']==null}">
            <style type="text/css" media="screen">@import "tabs.css";</style>
        </c:when>
        <c:otherwise>
            <link rel="stylesheet" type="text/css" href="${sessionScope['css']}" />
        </c:otherwise>
    </c:choose>

    <style type="text/css" media="screen">@import "style.css";</style>
    <script language="javascript" src="designe.js"> </script>
    <script type="text/javascript" src="tinybox.js"></script>
</head>

<script>
    var webapp="${pageContext.request.contextPath}";
    var popUP_OK="";
    var popUP_NO="";
</script>

<body>
<img src="imgs/banner.png" />
<a href="conf"><img align="right"src="imgs/login.png" /></a>
<div id="header">
    <ul id="primary">
        <jsp:include page="menu.jsp" />
    </ul>
</div>

<div id="main">
    <div id="contents">
        <c:choose>
            <c:when test="${param.t!=null}">
                <jsp:include page="middleware/${sessionScope['menu']}.jsp" />
            </c:when>
            <c:otherwise><f:message key="index.choose" /></c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
</f:bundle>