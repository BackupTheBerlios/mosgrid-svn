<%-- html escape--%>
<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.cabs" img="${pageContext.request.contextPath}/img/help.gif" />
<jsp:include page="/jsp/core.jsp" />





<script>
function labelValueChecker(pID,pTyp)
{
    if(pTyp=='0')document.getElementById(pID).value="";
    if(pTyp=='1')
    if(document.getElementById(pID).value=='')
    document.getElementById(pID).value="Default Value";
    
}
</script>	


<portlet:defineObjects/>
<portlet:actionURL var="pURL" />

<form method="post" action="${pURL}">
    <input type="hidden" name="guse" id="doList">
    <input type="submit" class="portlet-form-button" value="<msg:getText key="button.back" />" />
</form>

<form method="post" action="${pURL}"> 
    <input type="hidden" name="workflow" value="${workflow}">
    <input type="hidden" name="guse" value="doCreateAWorkflow">
    <input type="hidden" name="psize" value="${size}">
    <input type="hidden" name="pfrom" value="${pfrom}">
    <input type="hidden" name="pdesc" value="${pdesc}">    

    <table width="100%" class="darktable">
	<tr>
	    <td width="25%" class="kback"><div class="bold"><msg:getText key="text.ctemplate.newtempname" /></div></td>
	    <td width="75%" class="kback"><c:out value="${newWorkflow}" escapeXml="true" /></td>
	</tr>
	<tr>
	    <td width="25%" class="kback"><div class="bold"><msg:getText key="text.ctemplate.parentname" /></div></td>
	    <td width="75%" class="kback"><c:out value="${workflow}" escapeXml="true" /></td>
	</tr>
    </table>

    <input type="hidden" name="newaworkflow" value="${newWorkflow}">
    <input type="submit" class="portlet-form-button" value="<msg:getText key="button.create" />" />
   

<table width="100%" class="kback">

<c:forEach var="tmp" items="${jobs}" varStatus="p">
    <tr><td class="khead"><msg:getText key="text.ctemplate.check" /></td><td class="khead"><c:out value="${tmp.name}" escapeXml="true" /></td><td class="khead"><c:out value="${tmp.txt}" escapeXml="true" /></td></tr>
    <c:forEach var="tmp0" items="${tmp.exe}" varStatus="ln">

	<c:if test="${tmp0.key!='useallthis'}">        
	<c:if test="${tmp0.key!='module'}">        
        
        <c:set var="exe" value="${exe},job${p.index}_${tmp0.key}" scope="page"/>
	<c:choose>
	    <c:when test="${(ln.index%2)==1}">
	        <c:set var="color" value="kline1" />
	    </c:when>
	    <c:otherwise>
	        <c:set var="color" value="kline0" />
	    </c:otherwise>
	</c:choose>		
    <c:if test="${fn:indexOf(tmp0.key,'*')<0}">        
    <c:if test="${tmp0.value!='null'}">        
    <tr>
	<c:set var="pp" value="2" />
	<c:forEach var="ptmpl" items="${tmp.exeDisabled}">
	    <c:if test="${ptmpl.key==tmp0.key}">
		<c:set var="pp" value="1" />
	    </c:if>	
	</c:forEach>
	<c:choose>
	    <c:when test="${pp=='2'||open=='true'}">
		<td class="${color}">
		    <div style="display:none"><input type="checkbox" disabled="true" checked="true" name="job${p.index}_${tmp0.key}" id="job${p.index}_${tmp0.key}" value="<c:out value="${tmp0.value}" escapeXml="true" />"></div>
		    <input type="radio" checked="true" name="job${p.index}_${tmp0.key}" value="${tmp0.value}" onclick="document.getElementById('job${p.index}_${tmp0.key}').checked=true;document.getElementById('tmpdivjob${p.index}_${tmp0.key}').style.display='none';labelValueChecker('label_job${p.index}_${tmp0.key}',0);"><msg:getText key="text.ctemplate.close" />
		    <input type="radio" name="job${p.index}_${tmp0.key}" value="${tmp0.value}" onclick="document.getElementById('job${p.index}_${tmp0.key}').checked=false;document.getElementById('tmpdivjob${p.index}_${tmp0.key}').style.display='block';labelValueChecker('label_job${p.index}_${tmp0.key}',1);"><msg:getText key="text.ctemplate.free" />
		</td>    		    
	    </c:when>
	    <c:otherwise>
		<td class="${color}"><input type="radio" disabled="true" checked="true" name="job${p.index}_${tmp0.key}" value="<c:out value="${tmp0.value}" escapeXml="true" />"><msg:getText key="text.ctemplate.closed" /></td>
	    </c:otherwise>
	</c:choose>
	<td class="${color}"><msg:getText key="text.ctemplate.key.${tmp0.key}" /></td>    
	<td class="${color}"><c:out value="${tmp0.value}" escapeXml="true" /></td>    
    </tr>
    <tr>
	<td colspan="3">
	<c:if test="${pp=='2'||open=='true'}">
    	    <div style="padding-left:20px;display:none" id="tmpdivjob${p.index}_${tmp0.key}">
		<table>
		    <tr>
			<td><msg:getText key="text.ctemplate.inherit" /></td>
			<td>
			    <select name="inh_job${p.index}_${tmp0.key}">
				<option>---</option>
				<c:forEach var="tmpj" items="${jobs}">
    				    <option>${tmpj.name}</option>
				</c:forEach>
			    </select>
			    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.binhjob" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
		    <tr>
			<td><msg:getText key="text.ctemplate.label" /></td>
			<td><textarea cols="50" id="label_job${p.index}_${tmp0.key}" name="label_job${p.index}_${tmp0.key}"></textarea></td>
			<td>
			    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.blabel" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
		    <tr>
			<td><msg:getText key="text.ctemplate.description" /></td>
			<td><textarea cols="50" rows="5" name="desc_job${p.index}_${tmp0.key}"></textarea></td>
			<td>
			    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.bdesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
		    <tr>
			<td> <%-- <msg:getText key="text.ctemplate.defaultvalue" /> --%> </td>
			<td>
			    <input type="hidden" value="${tmp0.value}" disabled="true" />
			    <%-- <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.bdefaultvalue" img="${pageContext.request.contextPath}/img/tooltip.gif" /> --%>
			</td>
		    </tr>
		</table>
	    </div>
	</c:if>    
	</td>
    </tr>
    </c:if>
    </c:if>
    
    </c:if><!-- module szurese lezaras-->
    </c:if><!-- useallthis szurese lezaras-->
    
    </c:forEach>

    <c:forEach var="tmp0" items="${tmp.inputs}" varStatus="i">
	<tr><td class="khead"><msg:getText key="text.ctemplate.input" /></td><td class="khead">${tmp0.name}</td><td class="khead"><c:out value="${tmp0.txt}" escapeXml="true" /></td></tr>
	<c:forEach var="tmp1" items="${tmp0.data}" varStatus="ln">
    	    <c:set var="exe" value="${exe},job${p.index}_input${i.index}_${tmp1.key}" scope="page"/>
	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
		    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
		    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>		

	    <c:if test="${fn:indexOf(tmp1.key,'*')<0}">        
	    <c:if test="${tmp1.value!='null'}">        

	    <c:choose>
		<c:when test="${tmp1.key=='remote'}">
	    <tr>
		<td class="${color}">
		    <input type="checkbox" name="job${p.index}_input${tmp0.id}_${tmp1.key}" value="${tmp1.value}" disabled="true">
		    <msg:toolTip id="cicc" tkey="template.remote" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</td>
		<td class="${color}"><msg:getText key="text.ctemplate.key.${tmp1.key}" /></td>
		<td class="${color}"><c:out value="${tmp1.value}" escapeXml="true" /></td>
	    </tr>	
		
		</c:when>
		<c:otherwise>
	    <tr>

		<c:set var="pp" value="0" />
		<c:forEach var="ptmpl" items="${tmp0.dataDisabled}">
		    <c:if test="${(ptmpl.key==tmp1.key)&&(tmp1.value!='null')}">
			<c:set var="pp" value="1" />
		    </c:if>			    
		</c:forEach>
		
		<c:choose>
		    <c:when test="${pp=='0'||open=='true'}">
			<td class="${color}">																	                                          	
			    <input type="radio" value="${tmp1.value}" checked="true" name="job${p.index}_input${tmp0.id}_${tmp1.key}" value="${tmp1.value}" onclick="javascript:document.getElementById('tmpdivinput${p.index}_${tmp0.name}_${tmp1.key}').style.display='none';labelValueChecker('label_job${p.index}_input${tmp0.id}_${tmp1.key}',0);"><msg:getText key="text.ctemplate.close" />
			    <input type="radio" value="${tmp1.value}" name="job${p.index}_input${tmp0.id}_${tmp1.key}" onclick="document.getElementById('tmpdivinput${p.index}_${tmp0.name}_${tmp1.key}').style.display='block';labelValueChecker('label_job${p.index}_input${tmp0.id}_${tmp1.key}',1);"><msg:getText key="text.ctemplate.free" />
			</td>
		    </c:when>
		    <c:otherwise>
			<td class="${color}">
			    <input type="radio" value="${tmp1.value}" checked="true" disabled="true" name="job${p.index}_input${tmp0.id}_${tmp1.key}" value="${tmp1.value}" onclick="document.getElementById('job${p.index}_${tmp0.name}').checked=true;document.getElementById('tmpdivinput${p.index}__${tmp1.key}').style.display='none';"><msg:getText key="text.ctemplate.closed" />
			</td>
		    </c:otherwise>
		</c:choose>
	
		<td class="${color}"><msg:getText key="text.ctemplate.key.${tmp1.key}" /></td>
		<td class="${color}">
		    <c:choose>
			<c:when test="${tmp1.key=='sqlpass'}">***</c:when>
			<c:otherwise>${tmp1.value}</c:otherwise>
		    </c:choose>
		</td>
	    </tr>	
	    <tr>
		<td colspan="3">
		    <div id="tmpdivinput${p.index}_${tmp0.name}_${tmp1.key}" style="display:none">
		    <!--tmpdivinput${p.index}_${tmp0.name}_${tmp1.key}-->
			<table>
			    <tr>
				<td><msg:getText key="text.ctemplate.inherit" /></td>
				<td>
				    <select name="inh_job${p.index}_input${tmp0.id}_${tmp1.key}">
					<option>---</option>
					<c:forEach var="tmpj" items="${jobs}">
					    <c:forEach var="tmpj0" items="${tmpj.inputs}" varStatus="i">
    						<option>${tmpj.name}/${tmpj0.name}</option>
					    </c:forEach>
					</c:forEach>
				    </select>
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.iinhjob" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
			    <tr>
				<td><msg:getText key="text.ctemplate.label" /></td>
				<td><textarea cols="50"  id="label_job${p.index}_input${tmp0.id}_${tmp1.key}" name="label_job${p.index}_input${tmp0.id}_${tmp1.key}"></textarea></td>
				<td>
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.ilabel" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
			    <tr>
				<td><msg:getText key="text.ctemplate.description" /></td>
				<td><textarea cols="50" name="desc_job${p.index}_input${tmp0.id}_${tmp1.key}" rows="5"></textarea></td>
				<td>
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.idesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
			    <tr>
				<td>
				    <%-- <msg:getText key="text.ctemplate.defaultvalue" /> --%>
				 </td>
				<td>
				    <input type="hidden" value="${tmp1.value}" disabled="true" />
				    <%-- <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.idefaultvalue" img="${pageContext.request.contextPath}/img/tooltip.gif" /> --%>
				</td>
			    </tr>
			</table>
		    </div>
		</td>
	    </tr>	    
		</c:otherwise>
	    </c:choose>		
	    </c:if>	
	    </c:if>	
	</c:forEach>
    </c:forEach>
    
    
    <c:forEach var="tmp0" items="${tmp.outputs}" varStatus="o">
	<tr><td class="khead"><msg:getText key="text.ctemplate.output" /></td><td class="khead">${tmp0.name}</td><td class="khead"><c:out value="${tmp0.txt}" escapeXml="true" /></td></tr>
	<c:forEach var="tmp1" items="${tmp0.data}" varStatus="ln">
	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
		    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
		    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>		

	    <c:if test="${fn:indexOf(tmp1.key,'*')<0}">        
	    <c:if test="${tmp1.value!='null'}">        
	    <c:choose>
		<c:when test="${tmp1.key=='remote'}">
	    <tr>
		<td class="${color}">
		    <input type="checkbox" disabled="true" name="job${p.index}_output${tmp0.id}_${tmp1.key}" value="${tmp1.value}">
		    <msg:toolTip id="cicc" tkey="template.remote" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</td>
		<td class="${color}"><msg:getText key="text.ctemplate.key.${tmp1.key}" /></td>
		<td class="${color}"><c:out value="${tmp1.value}" escapeXml="true" /></td>
	    </tr>			
		</c:when>

		<c:when test="${tmp1.key=='remotehost'}">
	    <tr>
	    
		<td class="${color}">
		    <input type="checkbox" disabled="true" name="job${p.index}_output${tmp0.id}_${tmp1.key}" value="${tmp1.value}">
		    <msg:toolTip id="cicc" tkey="template.remote" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</td>
		<td class="${color}"><msg:getText key="text.ctemplate.key.${tmp1.key}" /></td>
		<td class="${color}"><c:out value="${tmp1.value}" escapeXml="true" /></td>
	    </tr>			
		</c:when>
		<c:otherwise>		    
	    <tr>
		<c:set var="pp" value="0" />
		<c:forEach var="ptmpl" items="${tmp0.dataDisabled}">
		    <c:if test="${(ptmpl.key==tmp1.key)&&(tmp1.value!='null')}">
			<c:set var="pp" value="1" />
		    </c:if>			    
		</c:forEach>

		<c:choose>
		    <c:when test="${pp=='0'||open=='true'}">
			<td class="${color}">
			    <input type="radio" value="${tmp1.value}" checked="true" name="job${p.index}_output${tmp0.id}_${tmp1.key}" value="${tmp1.value}" onclick="javascript:document.getElementById('tmpdivoutput${p.index}_${tmp0.name}_${tmp1.key}').style.display='none';labelValueChecker('label_job${p.index}_output${tmp0.id}_${tmp1.key}',0);"><msg:getText key="text.ctemplate.close" />
			    <input type="radio" value="${tmp1.value}" name="job${p.index}_output${tmp0.id}_${tmp1.key}" onclick="document.getElementById('tmpdivoutput${p.index}_${tmp0.name}_${tmp1.key}').style.display='block';labelValueChecker('label_job${p.index}_output${tmp0.id}_${tmp1.key}',1);"><msg:getText key="text.ctemplate.free" />
			</td>
		    </c:when>
		    <c:otherwise>
			<td class="${color}">
			    <input type="radio" value="${tmp1.value}" checked="true" disabled="true" name="job${p.index}_output${tmp0.id}_${tmp1.key}" value="${tmp1.value}" onclick="document.getElementById('job${p.index}_${tmp0.name}_${tmp1.key}').checked=true;document.getElementById('tmpdivoutput${p.index}_${tmp0.name}_${tmp1.key}').style.display='none';"><msg:getText key="text.ctemplate.closed" />
			</td>
		    </c:otherwise>
		</c:choose>

		<td class="${color}"><msg:getText key="text.ctemplate.key.${tmp1.key}" /></td>
		<td class="${color}">${tmp1.value}</td>
	    </tr>
	    <tr>
		<td colspan="3">
		    <div id="tmpdivoutput${p.index}_${tmp0.name}_${tmp1.key}" style="display:none">
		    <!--tmpdivoutput${p.index}_${tmp0.name}_${tmp1.key}-->
			<table>
			    <tr>
				<td><msg:getText key="text.ctemplate.inherit" /></td>
				<td>
				    <select name="inh_job${p.index}_output${tmp0.id}_${tmp1.key}">
					<option>---</option>
					<c:forEach var="tmpj" items="${jobs}">
					    <c:forEach var="tmpj0" items="${tmpj.outputs}" varStatus="i">
    						<option><c:out value="${tmpj.name}/${tmpj0.name}" escapeXml="true" /></option>
					    </c:forEach>
					</c:forEach>
				    </select>
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.oinhjob" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
			    <tr>
				<td><msg:getText key="text.ctemplate.label" /></td>
				<td><textarea cols="50" id="label_job${p.index}_output${tmp0.id}_${tmp1.key}" name="label_job${p.index}_output${tmp0.id}_${tmp1.key}"></textarea></td>
				<td>
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.olabel" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
			    <tr>
				<td><msg:getText key="text.ctemplate.description" /></td>
				<td><textarea cols="50" rows="5" name="desc_job${p.index}_output${tmp0.id}_${tmp1.key}"></textarea></td>
				<td>
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.odesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			    <tr><td colspan="2" style="border-bottom:#000000 solid 1px"></td></tr>
			    <tr>
				<td><msg:getText key="text.ctemplate.defaultvalue" /></td>
				<td>
				    <input type="text" value="<c:out value="${tmp1.value}" escapeXml="true" />" disabled="true" />
				    <msg:toolTip id="ctmpdiv" tkey="portal.ctemplate.odefaultvalue" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>
			</table>
		    </div>
		</td>
	    </tr>	    
	    	
		</c:otherwise>
	    </c:choose>		
	    </c:if>
	    </c:if>
	</c:forEach>
    </c:forEach>

</c:forEach>

</table>
</form>
