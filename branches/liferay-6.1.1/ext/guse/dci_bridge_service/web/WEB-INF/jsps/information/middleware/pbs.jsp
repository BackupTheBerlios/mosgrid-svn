<%--
edit pbs cluster
Configure.Lsf ${item} configuration data of selected LSF cluster
String ${param['t']} selected submenu item
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<table class="newdata" >
    <tr>
        <th class="ln0"><f:message key="new.pbs.name" /></th>
        <td class="ln0">${item.name}</td>
    </tr>

    <tr>
        <th colspan="2" class="ln1"><center><f:message key="new.pbs.queue" /></center></th>
    </tr>

    <c:forEach var="t" items="${item.pbs.queue}" varStatus="ln">
	<c:choose>
    	    <c:when test="${ln.index%2==0}"><c:set var="css" value="ln1" /></c:when>
    	    <c:otherwise><c:set var="css" value="ln0" /></c:otherwise>
    	</c:choose>
	<tr>
    	    <td colspan="2" class="${css}">${t}</td>
	</tr>
    </c:forEach>
</table>    
</f:bundle>


