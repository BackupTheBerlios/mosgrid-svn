<%-- nem csatorna input --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<div class="ioconfigtable">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.7" />:</div>
	<div class="ioconfig-data">
    <c:choose>
    <c:when test="${tmp.max!=''}">
	<input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id1" name="input_${tmp.id}_eparam" value="1" checked="true" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='table';"><msg:getText key="text.io.2" />
	<input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id2" name="input_${tmp.id}_eparam" value="0" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='none';"><msg:getText key="text.io.3" />
    </c:when>
    <c:otherwise>
	<input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id1" name="input_${tmp.id}_eparam" value="1" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='table';"><msg:getText key="text.io.2" />
	<input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id2" name="input_${tmp.id}_eparam" value="0" checked="true" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='none';"><msg:getText key="text.io.3" />
    </c:otherwise>
    </c:choose>
	
	</div>    	
    </div>
</div>



<c:choose>
    <c:when test="${tmp.max!=''}">
	<c:set var="flag" value="block" />
    </c:when>
    <c:otherwise>
	<c:set var="flag" value="none" />
    </c:otherwise>
</c:choose>

<div  id="${tmp.id}_param" style="display:${flag}"  class="ioconfigtable">

    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.15" />:</div>
	<div class="ioconfig-data">
		<select title='<msg:getText key="config.input.dpid" />' ${tmp.enabledDpid} name="input_${tmp.id}_dpid" id="input_${tmp.id}_dpid">
		<c:forEach var="tmp0" begin="0" end="16">

		    <c:choose>
			<c:when test="${tmp.dpid!=''}">
			    <c:choose>
				<c:when test="${tmp0==tmp.dpid}">
				    <option selected="true" value="${tmp0}">${tmp0}</option>
				</c:when>
				<c:otherwise><option>${tmp0}</option></c:otherwise>
			    </c:choose>		
			</c:when>
			<c:otherwise>
    		    	    <c:choose>
		    		<c:when test="${tmp0==tmp.seq}">
			    	    <option selected="true" value="${tmp0}">${tmp0}</option>
				</c:when>
				<c:otherwise><option>${tmp0}</option></c:otherwise>
			    </c:choose>
			</c:otherwise>
		    </c:choose>
		</c:forEach>
		</select>
		<c:if test="${tmp.labelDpid!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelDpid}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descDpid!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descDpid}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>

<c:if test="${tmp.preJob==''}">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.19" />:</div>
	<div class="ioconfig-data">
		<input type="text" title='<msg:getText key="config.input.max" />' ${tmp.enabledMax} name="input_${tmp.id}_max" value="${tmp.max}" id="input_${tmp.id}_max" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')">
		<c:if test="${tmp.labelMax!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelMax}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descMax!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descMax}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>	
	</div>    	
    </div>
</c:if>

<c:if test="${tmp.preJob!=''}">
    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.16" />:</div>
	<div class="ioconfig-data">
		<c:choose>
		    <c:when test="${tmp.waiting=='all'}">
			<input type="hidden" name="input_${tmp.id}_waiting" id="input_${tmp.id}_waiting" value="all">
			<input title='<msg:getText key="config.input.waiting" />' ${tmp.enabledWaiting} type="radio" id="input_${tmp.id}_waitingtmp_id1" name="input_${tmp.id}_waitingtmp" value="one" onclick="document.getElementById('input_${tmp.id}_waiting').value='one';document.getElementById('dependr_${tmp.id}_id1').disabled=false;document.getElementById('dependr_${tmp.id}_id2').disabled=false;"><msg:getText key="text.io.17" />&nbsp;&nbsp;
			<input title='<msg:getText key="config.input.waiting" />' ${tmp.enabledWaiting} type="radio" id="input_${tmp.id}_waitingtmp_id2" name="input_${tmp.id}_waitingtmp" value="all" checked="true" onclick="document.getElementById('input_${tmp.id}_waiting').value='all';document.getElementById('depend_${tmp.id}').style.display='none';document.getElementById('dependr_${tmp.id}_id1').checked=false;document.getElementById('dependr_${tmp.id}_id1').disabled=true;document.getElementById('dependr_${tmp.id}_id2').checked=true;document.getElementById('dependr_${tmp.id}_id2').disabled=true;document.getElementById('input_${tmp.id}_pequalvalue').value='';document.getElementById('input_${tmp.id}_pequalinput').selectedIndex='0';document.getElementById('input_${tmp.id}_pequaltype').selectedIndex='0';"><msg:getText key="text.io.18" />&nbsp;&nbsp;
		    </c:when>
		    <c:otherwise>
			<input type="hidden" name="input_${tmp.id}_waiting" id="input_${tmp.id}_waiting" value="one">
			<input title='<msg:getText key="config.input.waiting" />' ${tmp.enabledWaiting} type="radio" id="input_${tmp.id}_waitingtmp_id1" name="input_${tmp.id}_waitingtmp" value="one" checked="true" onclick="document.getElementById('input_${tmp.id}_waiting').value='one';document.getElementById('dependr_${tmp.id}_id1').disabled=false;document.getElementById('dependr_${tmp.id}_id2').disabled=false;"><msg:getText key="text.io.17" />&nbsp;&nbsp;
			<input title='<msg:getText key="config.input.waiting" />' ${tmp.enabledWaiting} type="radio" id="input_${tmp.id}_waitingtmp_id2" name="input_${tmp.id}_waitingtmp" value="all" onclick="document.getElementById('input_${tmp.id}_waiting').value='all';document.getElementById('depend_${tmp.id}').style.display='none';document.getElementById('dependr_${tmp.id}_id1').checked=false;document.getElementById('dependr_${tmp.id}_id1').disabled=true;document.getElementById('dependr_${tmp.id}_id2').checked=true;document.getElementById('dependr_${tmp.id}_id2').disabled=true;document.getElementById('input_${tmp.id}_pequalvalue').value='';document.getElementById('input_${tmp.id}_pequalinput').selectedIndex='0';document.getElementById('input_${tmp.id}_pequaltype').selectedIndex='0';"><msg:getText key="text.io.18" />&nbsp;&nbsp;
		    </c:otherwise>
		</c:choose>
		<c:if test="${tmp.labelWaiting!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelWaiting}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descWaiting!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descWaiting}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</div>    	
    </div>
</c:if>


</div>
