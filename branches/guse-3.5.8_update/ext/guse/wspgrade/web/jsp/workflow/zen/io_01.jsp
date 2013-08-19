<%-- internal file name --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
    <c:choose>	
    <c:when test="${jobgridtype=='gemlca'}">	
	<div class="jobconfig-header"><msg:getText key="text.io.gemlcaint" />:</div>
	<div class="ioconfig-data">
	    <select ${tmp.enabledInternalName} class="portlet-form-button" name="input_${tmp.id}_gout" id="input_${tmp.id}_gout" onClick="javascript:document.getElementById('input_${tmp.id}_intname').value=(this.value);">
        <option value=""> - </option>
	    <c:forEach var="tmp0" items="${gininames}" varStatus="tmpi">
	    <c:choose>
		<c:when test="${tmp0==tmp.internalName}"><option selected="true" value="${tmp0}">${ginfriendlyNames[tmpi.index]}</option></c:when>
		<c:otherwise><option value="${tmp0}">${ginfriendlyNames[tmpi.index]}</option></c:otherwise>
	    </c:choose>    
	    </c:forEach>			
	    </select>
	    <msg:getText key="${gmsg}" />
        <input title='<msg:getText key="config.input.internalfilename" />' ${tmp.enabledInternalName} type="hidden" name="input_${tmp.id}_intname" id="input_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />
	</div>    	
    </c:when>	
    <c:otherwise>
	<div class="jobconfig-header"><msg:getText key="text.io.0" />:</div>
	<div class="ioconfig-data">
	    <input title='<msg:getText key="config.input.internalfilename" />' ${tmp.enabledInternalName} type="text" name="input_${tmp.id}_intname" id="input_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />  
	    <c:if test="${tmp.labelInternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelInternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	    <c:if test="${tmp.descInternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descInternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>
    </c:otherwise>	
    </c:choose>	
    </div>
</div>