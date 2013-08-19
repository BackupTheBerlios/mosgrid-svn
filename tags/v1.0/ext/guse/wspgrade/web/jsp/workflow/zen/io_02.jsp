<%-- enbed input --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>




<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.50" />:</div>
	<div class="ioconfig-data">
	    <c:choose>
		<c:when test="${tmp.iinput!=''}">
    		    <input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="yes" checked="true" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='table';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='none';document.getElementById('${tmp.id}_param').style.display='none';document.getElementById('input_${tmp.id}_eparam_id1').checked=false;document.getElementById('input_${tmp.id}_eparam_id2').checked=true;document.getElementById('input_${tmp.id}_waiting').value='one';document.getElementById('input_${tmp.id}_waitingtmp_id1').checked=true;document.getElementById('input_${tmp.id}_waitingtmp_id2').checked=false;" />Yes
		    <input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="no" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='none';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='table';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='table';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='table';document.getElementById('input_${tmp.id}_iinput').value='';" />No
		    <c:set var="flag" value="table" />
		</c:when>
		<c:otherwise>
    		    <input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="yes" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='table';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='none';document.getElementById('${tmp.id}_param').style.display='none';document.getElementById('input_${tmp.id}_eparam_id1').checked=false;document.getElementById('input_${tmp.id}_eparam_id2').checked=true;document.getElementById('input_${tmp.id}_waiting').value='one';document.getElementById('input_${tmp.id}_waitingtmp_id1').checked=true;document.getElementById('input_${tmp.id}_waitingtmp_id2').checked=false;" />Yes
		    <input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="no" checked="true" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='none';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='block';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='block';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='block';document.getElementById('input_${tmp.id}_iinput').value='';" />No
		    <c:set var="flag" value="none" />
		</c:otherwise>
	    </c:choose>
	</div>    	
    </div>
</div>


<div class="ioconfigtable" id="embed_${tmp.id}" style="display:${flag}">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header">&nbsp;&nbsp;<msg:getText key="text.io.enbedinput" />:</div>
	<div class="ioconfig-data">
			    <select title='<msg:getText key="config.input.iinput" />'  ${tmp.enabledIinput} name="input_${tmp.id}_iinput" id="input_${tmp.id}_iinput">
				<option></option>
    				<c:forEach var="tmp0" items="${iinputs}">
				    <c:choose>
					<c:when test="${tmp.iinput==tmp0}"><option selected="true">${tmp0}</option></c:when>
					<c:otherwise><option>${tmp0}</option></c:otherwise>
				    </c:choose>
					
				</c:forEach>
			    </select>
			    <c:if test="${tmp.labelIinput!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelIinput}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
			    <c:if test="${tmp.descIinput!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descIinput}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>
</div>

