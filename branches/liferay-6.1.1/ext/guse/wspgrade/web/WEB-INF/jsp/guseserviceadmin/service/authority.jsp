<%-- service-hez kapcsolodo jogosultsagkezeles
GuseServiceBean ${sessionScope.auth} konfiguralando szerviz
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<p:defineObjects />
<%-- uj elem felvitele --%>
        <a href="#" id="insertrightform_link" onclick="javascript:showhide('insertrightform');" class="icolink">
            <img id="insertrightform_icon" src="${pageContext.request.contextPath}/imgs/serviceadmin/user_add.png" /><br />
            <div id="insertrightform_button">[felhasznalo hozzaadasa]</div>
        </a>
        <div id="insertrightform" class="dataform" style="display:none">
            <p:actionURL var="aURL" >
            </p:actionURL>
            <form method="post" action="${aURL}">
            <table border="1">
                <caption>uj felhasznalo felvitele</caption>
                <tr>
                    <th>Felhasznalo login neve</th>
                    <td><input type="text" name="pnlu" /></td>
                </tr>
                <tr><td colspan="2"><input type="submit" value="felvesz"></td></tr>
            </table>
            </form>
            <a href="#" onclick="javascript:showhide('insertrightform');" class="icolink">
                <img  src="${pageContext.request.contextPath}/imgs/serviceadmin/close.png" /><br />
                <div float:left>[megse]</div>
            </a>
        </div>

       <br /><br />
<table border="1" >
    <caption>Jogosultsagok</caption>
    <tr>
        <th>Service type</th>
        <td>${sessionScope.auth.typ.sname}</td>
    </tr>
    <tr>
        <th>Service felület</th>
        <td>
            <c:forEach var="t" items="${sessionScope.auth.com}" >
                ${t.cname} <br />
            </c:forEach>
        </td>
    </tr>
    <tr>
        <th>Service url(gUSE)</th>
        <td>${sessionScope.auth.url}</td>
    </tr>
    <tr>
        <th>Init url(gUSE)</th>
        <td>${sessionScope.auth.surl}</td>
    </tr>
    <tr>
        <th>Service publikus url(user)</th>
        <td>${sessionScope.auth.surl}</td>
    </tr>
    <tr>
        <th>Status</th>
        <td>
        <c:choose>
            <c:when test="${sessionScope.auth.state}">
                <div style="display:block;text-align:center">
                    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/active.png" /><br />
                    (aktiv)
                </div>
            </c:when>
            <c:otherwise>
                <div style="display:block;text-align:center">
                    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/inactive.png" /><br />
                    (inaktiv)
                </div>
            </c:otherwise>
        </c:choose>
        </td>
    </tr>
    <tr>
        <td>
            konfiguralt felhasznalok <br />
            (ures lista eseten mindenki hasznalhatja)
        </td>
        <td>
            <table>
            <c:forEach var="t" items="${sessionScope.auth.users}" >
                <tr><td >${t.lname}</td>
                <td>
            <p:actionURL var="dsURL">
                <p:param name="pdlu" value="${t.id}" />
            </p:actionURL>
            <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${dsURL}','Valoban torolni akarod?'),0,300,300,1);">
            <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
            [torol]
        </a>
                </td></tr>

            </c:forEach>
            </table>
        </td>
    </tr>
</table>

