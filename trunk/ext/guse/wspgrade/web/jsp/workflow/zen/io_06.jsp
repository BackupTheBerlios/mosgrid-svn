<%-- output file neve --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<div class="ioconfigtable">
    <c:if test="${jobgridtype=='gemlca'}">	
	<div style="display:table-row;margin-top:1px;float:left;">
	    <div class="jobconfig-header"><msg:getText key="text.io.gemlcaint" />:</div>
        <div class="ioconfig-data">
		    <select ${tmp.enabledInternalName} class="portlet-form-button" name="output_${tmp.id}_gout" id="output_${tmp.id}_gout" onClick="javascript:document.getElementById('output_${tmp.id}_intname').value=(this.value);javascript:document.getElementById('gout_${tmp.id}_intfile').innerHTML=(this.value);">
            <option value=""> - </option>
			<c:forEach var="tmp0" items="${goutinames}" varStatus="tmpi">
			<c:choose>
			    <c:when test="${tmp0==tmp.internalName}">
                    <c:set var="goutfilename" value="${tmp0}" />
                    <option selected="true" value="${tmp0}">${goutfriendlyNames[tmpi.index]}</option>
                </c:when>
			    <c:otherwise><option value="${tmp0}">${goutfriendlyNames[tmpi.index]}</option></c:otherwise>
			</c:choose>    
			</c:forEach>			
		    </select>
		    <msg:getText key="${gmsg}" />
	    </div>    	
	</div>
    </c:if>

    <div style="display:table-row;margin-top:1px;float:left;">
        <c:choose>
            <c:when test="${jobgridtype=='gemlca'}">
                <div>
                    <input title="internalfilename" type="hidden" ${tmp.enabledInternalName} name="output_${tmp.id}_intname" id="output_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />
                </div>
                <div class="jobconfig-header"><msg:getText key="text.io.9" />:</div>
                <div class="ioconfig-data" id ="gout_${tmp.id}_intfile">
                    <c:out value="${goutfilename}" escapeXml="true"  />
                </div>
            </c:when>
            <c:otherwise>
                <div class="jobconfig-header"><msg:getText key="text.io.9" />:</div>
                <div class="ioconfig-data">
                    <input title="<msg:getText key="config.output.intname" />" type="text" ${tmp.enabledInternalName} name="output_${tmp.id}_intname" id="output_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />
                    <c:if test="${tmp.labelInternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelInternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
                    <c:if test="${tmp.descInternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descInternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
                </div>
            </c:otherwise>
        </c:choose>
    </div>


</div>


