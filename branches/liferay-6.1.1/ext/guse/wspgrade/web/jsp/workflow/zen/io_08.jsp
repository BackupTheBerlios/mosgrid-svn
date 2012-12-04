<%-- embed output --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.51" />:</div>
	<div class="ioconfig-data">
	    <c:choose>
		<c:when test="${tmp.ioutput!=''}">	    
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" checked="true" value="yes" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='block';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='none';document.getElementById('output_${tmp.id}_maincount').value='1';document.getElementById('output_${tmp.id}_maincount0_id1').checked=true;document.getElementById('output_${tmp.id}_maincount0_id2').checked=false;" />Yes
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" value="no" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='none';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='block';document.getElementById('output_${tmp.id}_ioutput').value='';" />No
		    <c:set var="flag" value="table-row" />
		</c:when>
		<c:otherwise>
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" value="yes" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='block';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='none';document.getElementById('output_${tmp.id}_maincount').value='1';document.getElementById('output_${tmp.id}_maincount0_id1').checked=true;document.getElementById('output_${tmp.id}_maincount0_id2').checked=false;" />Yes
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" checked="true" value="no" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='none';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='block';document.getElementById('output_${tmp.id}_ioutput').value='';" />No
		    <c:set var="flag" value="none" />
		</c:otherwise>
	    </c:choose>
	    <msg:toolTip id="cicc" tkey="config.output.embedparam" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</div>    	
    </div>

<div id="outputembed_${tmp.id}"  style="display:${flag};margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.enbedoutput" />:</div>
	<div class="ioconfig-data">
			    <select title='<msg:getText key="config.output.ioutput" />' ${tmp.enabledIoutput} name="output_${tmp.id}_ioutput" id="output_${tmp.id}_ioutput">
				<option></option>
    				<c:forEach var="tmp0" items="${ioutputs}">
				    <c:choose>
					<c:when test="${tmp.ioutput==tmp0}"><option selected="true">${tmp0}</option></c:when>
					<c:otherwise><option>${tmp0}</option></c:otherwise>
				    </c:choose>
				</c:forEach>
			    </select>
			    <msg:toolTip id="cicc" tkey="config.output.ioutput" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			    <c:if test="${tmp.labelIoutput!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelIoutput}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
			    <c:if test="${tmp.descIoutput!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descIoutput}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>

</div>

