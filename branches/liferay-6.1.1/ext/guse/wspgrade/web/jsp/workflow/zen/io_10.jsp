<%-- generator --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.14" />:</div>
	<div class="ioconfig-data">
		<input type="hidden" name="output_${tmp.id}_maincount" id="output_${tmp.id}_maincount" value="${tmp.maincount}"/>
		<c:choose>
		    <c:when test="${tmp.maincount=='1'}">
			<input title='<msg:getText key="config.output.maincount" />' ${tmp.enabledMaincount} type="radio" name="output_${tmp.id}_maincount0" id="output_${tmp.id}_maincount0_id1" value="1" checked="true" onclick="document.getElementById('output_${tmp.id}_maincount').value='1'"/><msg:getText key="text.io.14.no" />
			<input title='<msg:getText key="config.output.maincount" />' ${tmp.enabledMaincount} type="radio" name="output_${tmp.id}_maincount0" id="output_${tmp.id}_maincount0_id2" value="2" onclick="document.getElementById('output_${tmp.id}_maincount').value='2'"/><msg:getText key="text.io.14.yes" />
		    </c:when>
		    <c:otherwise>
			<input title='<msg:getText key="config.output.maincount" />' ${tmp.enabledMaincount} type="radio" name="output_${tmp.id}_maincount0" id="output_${tmp.id}_maincount0_id1" value="1" onclick="document.getElementById('output_${tmp.id}_maincount').value='1'"/><msg:getText key="text.io.14.no" />
			<input title='<msg:getText key="config.output.maincount" />' ${tmp.enabledMaincount} type="radio" name="output_${tmp.id}_maincount0" id="output_${tmp.id}_maincount0_id2" value="2" checked="true" onclick="document.getElementById('output_${tmp.id}_maincount').value='2'"/><msg:getText key="text.io.14.yes" />
		    </c:otherwise>
		</c:choose>    
		<c:if test="${tmp.labelMaincount!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelMaincount}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descMaincount!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descMaincount}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>
</div>

