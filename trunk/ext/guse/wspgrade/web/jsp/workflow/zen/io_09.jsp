<%-- permament/voletile output --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.12" />:</div>
	<div class="ioconfig-data">
		<input type="hidden" name="output_${tmp.id}_type" id="output_${tmp.id}_type" value="${tmp.type}"/>
		<c:choose>
		<c:when test="${tmp.type=='volatile'}">
		    <input title='<msg:getText key="config.output.type" />' ${tmp.enabledType} type="radio" name="output_${tmp.id}_type0" value="permament" onclick="document.getElementById('output_${tmp.id}_type').value='permamen'"/><msg:getText key="text.io.13" />
		    <input title='<msg:getText key="config.output.type" />' ${tmp.enabledType} type="radio" name="output_${tmp.id}_type0" value="volatile"  checked="true"onclick="document.getElementById('output_${tmp.id}_type').value='volatile'"/><msg:getText key="text.io.33" />
		</c:when>
		<c:otherwise>
		    <input title='<msg:getText key="config.output.type" />' ${tmp.enabledType} type="radio" name="output_${tmp.id}_type0" value="permament" checked="true" onclick="document.getElementById('output_${tmp.id}_type').value='permamen'"/><msg:getText key="text.io.13" />
		    <input title='<msg:getText key="config.output.type" />' ${tmp.enabledType} type="radio" name="output_${tmp.id}_type0" value="volatile"  onclick="document.getElementById('output_${tmp.id}_type').value='volatile'" /><msg:getText key="text.io.33" />
		</c:otherwise>
		</c:choose>
		<c:if test="${tmp.labelType!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelType}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descType!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descType}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>
</div>