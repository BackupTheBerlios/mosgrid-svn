<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<div  style="margin:0px 10px; width:95%; height:400px;overflow:auto;">
<c:set var="tdat" value="" />
<table  width="100%">
    <c:forEach var="one" items="${jobchis}" varStatus="ln">
	<c:if test="${tdat!=one.tim}" >
	    <c:set var="tdat" value="${one.tim}" />
	    <tr> <td colspan="5" class="jobconfig-header"><center>${one.tim}</center></td> </tr>	
	    <tr> 
		<td class="jobconfig-data"><msg:getText key="text.history.0" /></td> 
		<td class="jobconfig-data"><msg:getText key="text.history.1" /></td> 
		<td class="jobconfig-data"><msg:getText key="text.history.2" /></td> 
		<td class="jobconfig-data"><msg:getText key="text.history.3" /></td> 
		<td class="jobconfig-data"><msg:getText key="text.history.4" /></td> 
	    </tr>	
	</c:if>
	<c:if test="${fn:indexOf(one.mdyid,'*')<0}">    
	<c:choose>
	    <c:when test="${(ln.index%2)==1}">
	        <c:set var="color" value="kline1" />
	    </c:when>
	    <c:otherwise>
	        <c:set var="color" value="kline0" />
	    </c:otherwise>
	</c:choose>		
	<tr> 
	    <td class="${color}">${one.user}</td> 
	    <td class="${color}">${one.port}</td> 
	    <td class="${color}"><msg:getText key="${one.mdyid}" /></td> 
	    <c:choose>
		<c:when test="${fn:indexOf(one.mdyid,'input.sqlpass')>0}">
		    <td class="${color}">
			<c:if test="${one.ovalue!=''}">*****</c:if>
		    </td> 
		    <td class="${color}">*****</td> 
		</c:when>
		<c:otherwise>
		    <td class="${color}"><c:out value="${one.ovalue}" escapeXml="true"  /></td> 
		    <td class="${color}"><c:out value="${one.nvalue}" escapeXml="true"  /></td> 
		</c:otherwise>
	    </c:choose>	    
	</tr>	
	</c:if>	
	<tr><td colspan="5"><div class="iodline" /></td></tr>
    </c:forEach>
</table>    
</div>    

