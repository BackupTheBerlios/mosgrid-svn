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
<script>
    function validateEditForm(){
        var x=document.forms["edit"]["list"].selectedIndex;
        if (x==null || x==0){
            alert("?????????");
            return false;
        }
        return true;
    }

    function submitEditForm(){
        if(validateEditForm())
            document.forms["edit"].submit();
    }

</script>
</head>

<script>
    var webapp="${pageContext.request.contextPath}";
    var popUP_OK="";
    var popUP_NO="";
</script>

<body>
<img src="imgs/banner.png" />
<div id="header">
    <ul id="primary">
        <jsp:include page="menu.jsp" />
    </ul>
</div>

<div id="main">
    <div id="contents">
    <c:choose>
        <c:when test="${param['t']=='new'}">
            <center>
            <jsp:include page="${param['t']}/${sessionScope['menu']}.jsp" />
            </center>
        </c:when>
        <c:when test="${param['t']=='edit' }">
            <jsp:include page="edit.jsp" />
        </c:when>
        <c:when test="${param['t']=='general' }">
            <jsp:include page="general.jsp" />
        </c:when>
        <c:when test="${param['t']=='info' }">
            <jsp:include page="monitor.jsp" />
        </c:when>
        <c:when test="${param['t']=='logg' }">
            <jsp:include page="logg.jsp" />
        </c:when>
        <c:when test="${param['t']=='manager' }">
            <jsp:include page="system/manager.jsp" />
        </c:when>
        <c:when test="${param['t']=='properties' }">
            <jsp:include page="system/properties.jsp" />
        </c:when>
        <c:otherwise>
            <f:message key="index.choose" />
        </c:otherwise>
    </c:choose>
            <c:forEach var="t" items="${sessionScope['notset']}">
                ${t}
            </c:forEach>
            <c:forEach var="t" items="${sessionScope['error']}">
                ${t}
            </c:forEach>
    </div>
</div>
</body>
</html>
</f:bundle>