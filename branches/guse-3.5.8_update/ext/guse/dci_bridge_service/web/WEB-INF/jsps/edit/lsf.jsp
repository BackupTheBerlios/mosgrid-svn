<%--
edit lsf cluster
Configure.Lsf ${item} configuration data of selected LSF cluster
String ${param['t']} selected submenu item
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >
<form id="editForm" method="post" action="conf?editing=${item.name}">
<table>
<tr><td>
<table class="newdata" >
    <caption><f:message key="caption.edit.lsf" /></caption>
    <tr>
        <th class="ln0"><f:message key="new.pbs.name" /></th>
        <td class="ln0"><input type="text" name="plsfname" value="${item.name}" size="20"/></td>
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
        <th colspan="2" class="ln0"><center><f:message key="new.cluster.queues" /><center></th>
    </tr>

    <c:forEach var="t" begin="0" end="4" >
    	<c:choose>
    	    <c:when test="${t%2==0}"><c:set var="css" value="ln1" /></c:when>
    	    <c:otherwise><c:set var="css" value="ln0" /></c:otherwise>
    	</c:choose>
    	<tr>
	    <th class="${css}"><f:message key="new.pbs.queue" /></th>
    	    <td class="${css}"> <input type="text" name="pnew_queue_${t}" size="20" /></td>
	</tr>
    </c:forEach>

    <tr>
        <th colspan="2" class="ln0"><center><f:message key="delete.cluster.queue" /><center></th>
    </tr>

    <c:forEach var="t" items="${item.lsf.queue}" varStatus="ln">
	<c:choose>
    	    <c:when test="${ln.index%2==0}"><c:set var="css" value="ln1" /></c:when>
    	    <c:otherwise><c:set var="css" value="ln0" /></c:otherwise>
    	</c:choose>
	<tr>
    	    <td class="${css}">${t}</td>
    	    <td class="${css}"><input type="checkbox" name="pdelete_queue_${ln.index}" value="${t}" /></td>
	</tr>
    </c:forEach>
</table>    
</td>
</tr>
<jsp:include page="submit_button.jsp" />
</table>
</form>
</f:bundle>