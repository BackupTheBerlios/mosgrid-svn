<%-- 
edit boinc grid
Boinc ${item} kivalasztott boinc grid
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="editdata" >
    <caption><f:message key="caption.boinc.edit" /></caption>
    <form  id="editForm" method="post" action="conf?editing=${item.name}">
    <tr>
        <th><f:message key="new.boinc.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pboincname" size="25" value="${item.name}" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
                <c:choose>
                <c:when test="${item.enabled}">
                    <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
                    <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
                </c:when>
                <c:otherwise>
                    <input type="radio" name="penabled" value="1" /><f:message key="general.yes" />
                    <input type="radio" name="penabled" checked="true" value="0" /><f:message key="general.no" />
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.id" /></th>
        <td class="ln1"><input class="ln0" type="text" name="pboincid" size="25"  value="${item.boinc.id}"/></td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.wsdl" /></th>
        <td class="ln0"><input class="ln1" type="text" name="pwsdl" size="60"  value="${item.boinc.wsdl}" /></td>
    </tr>
    <tr>
        <th><f:message key="new.boinc.job.name" /></th>
        <th><f:message key="new.boinc.job.state" /></th>
    </tr>
    <c:forEach var="t" items="${item.boinc.job}" varStatus="ln">
        <c:choose>
            <c:when test="${ln.index%2==0}">
                <c:set var="css" value="ln0" />
            </c:when>
            <c:otherwise>
                <c:set var="css" value="ln1" />
            </c:otherwise>
        </c:choose>
    <tr>
        <td class="${css}">${t.name}</td>
        <td class="${css}">
            <c:set var="selected" value="" />
            <select name="pstate_${t.name}">
                <c:choose>
                    <c:when test="${t.state}">
                        <option selected="true" value="1">true</option>
                        <option value="0">false</option>
                    </c:when>
                    <c:when test="${t.state=='not verify'}">
                        <option value="1">true</option>
                        <option selected="true" value="0">false</option>
                    </c:when>
                 </c:choose>
           </select>
        </td>
    </tr>
    </c:forEach>
    <jsp:include page="submit_button.jsp" />
</form>
</table>
</f:bundle>