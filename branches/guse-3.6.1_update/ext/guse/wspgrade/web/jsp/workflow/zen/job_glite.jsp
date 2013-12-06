




<%-- 
    Document   : job_glite
    Created on : 2010.05.19., 16:13:16
    Author     : krisztian
--%>
<%-- gelmajob? --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<msg:help id="helptext" tkey="help.job" img="${pageContext.request.contextPath}/img/help.gif" />




    <tr>
	<td width="30%">
	<c:choose>
	    <c:when test="${sgridtype=='gemlca'}">
		<msg:getText key="text.job.Gemlcarepo" />:
	    </c:when>
	    <c:otherwise>
		<msg:getText key="text.job.5" />:
	    </c:otherwise>
	</c:choose>

	</td>
	<td><select ${egrid} class="portlet-form-button" name="job_grid" id="job_grid" onChange="if(document.getElementById('job_gridtype').value=='gemlca'){gemlcaUI(true,'${jobID}');}else{getRemoteSelectOptions('mw='+document.getElementById('job_gridtype').value+'&j='+document.getElementById('job_grid').value+'&m=GetResource')}">
	    <c:forEach var="tmp0" items="${grids}">
		<c:choose>
		    <c:when test="${tmp0==sgrid}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
		    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
		</c:choose>
	    </c:forEach>
	</select>
    	<c:choose>
		<c:when test="${sgridtype=='gemlca'}">
		    <msg:toolTip id="cicc" tkey="config.job.gemlcarepo" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</c:when>
		<c:otherwise>
		    <msg:toolTip id="cicc" tkey="config.job.grid" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</c:otherwise>
	</c:choose>
	<c:if test="${job.labelGrid!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelGrid}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	<c:if test="${job.descGrid!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descGrid}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>

    <tr>
	<td width="30%">
	<c:choose>
	    <c:when test="${sgridtype=='gemlca'}">
		<msg:getText key="text.job.GemlcaLegacyCode" />:
	    </c:when>
	    <c:otherwise>
		<msg:getText key="text.job.6" />:
	    </c:otherwise>
	</c:choose>
	</td>
	<td><select ${eresource} class="portlet-form-button" name="job_resource" id="job_resource" onChange="javascript:if(document.getElementById('job_gridtype').value=='gemlca'){gemlcaUI(true,'${jobID}');}else{getRemoteSelectOptions('mw='+document.getElementById('job_gridtype').value+'&g='+document.getElementById('job_grid').value+'&j='+document.getElementById('job_resource').value+'&m=GetData')}">
	    <c:forEach var="tmp0" items="${resources}"  >
		<c:choose>
		    <c:when test="${tmp0==sresource}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
		    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
		</c:choose>
	    </c:forEach>
	    </select>
    	    <c:choose>
		<c:when test="${sgridtype=='gemlca'}">
		    <msg:toolTip id="cicc" tkey="config.job.gemlcalc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</c:when>
		<c:otherwise>
		    <msg:toolTip id="cicc" tkey="config.job.resource" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</c:otherwise>
	    </c:choose>
	    <c:if test="${job.labelResource!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelResource}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	    <c:if test="${job.descResource!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descResource}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>
<!--    <tr><td colspan="2"><div class="iodline" /></td></tr> -->
    <tr>
	<td width="30%">
	<c:choose>
	    <c:when test="${sgridtype=='gemlca'}">
		<msg:getText key="text.job.6" />:
	    </c:when>
	    <c:otherwise>
		<msg:getText key="text.job.7" />:
	    </c:otherwise>
	</c:choose>
	</td>
	<td><select ${ejobmanager} class="portlet-form-button" name="job_jobmanager" id="job_jobmanager" >
	    <c:forEach var="tmp0" items="${rdata}">
		<c:choose>
		    <c:when test="${tmp0==sdata}"><option selected="true">${tmp0}</option></c:when>
		    <c:otherwise><option>${tmp0}</option></c:otherwise>
		</c:choose>
	    </c:forEach>
	    </select>
    	    <c:choose>
		<c:when test="${sgridtype=='gemlca'}">
		    <msg:toolTip id="cicc" tkey="config.job.gemlcaresource" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</c:when>
		<c:otherwise>
		    <msg:toolTip id="cicc" tkey="config.job.jobmanager" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		</c:otherwise>
	    </c:choose>
	    <c:if test="${job.labelJobManager!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelJobManager}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
	    <c:if test="${job.descJobManager!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descJobManager}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	</td>
    </tr>
    <tr>
	<td width="30%"><msg:getText key="text.job.20" />:</td>
	<td><input type="checkbox" class="portlet-form-button" name="job_useallthis" id="job_useallthis" value="${jobname}">
	    <msg:toolTip id="cicc" tkey="config.job.usealljob" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	</td>
    </tr>
    </table>
</div>
<%-- Nem gemlca job-- --%>



    <tr><%-- file upload --%>
    	<td width="30%"><msg:getText key="text.job.14" />: </td>
        <td align="left">
            <msg:getText key="text.job.15" />: ${binary}<br />

    	    <c:choose>
                <c:when test="${sgridtype=='DesktopGrid'}">
                    <div id="upload_upload0" style="display:none">
        		</c:when>
            	<c:otherwise>
                    <div id="upload_upload0" style="display:block">
                </c:otherwise>
            </c:choose>
            <input ${ebinary}  type="file" name="job_${jobID}_binary" id="job_${jobID}_binary" />

            <msg:toolTip id="cicc" tkey="config.job.binary" img="${pageContext.request.contextPath}/img/tooltip.gif" />
            <c:if test="${job.labelBinary!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelBinary}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
            <c:if test="${job.descBinary!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descBinary}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </div>

	    <c:choose>
		<c:when test="${sgridtype=='DesktopGrid'}">
		    <div id="cg_bin" style="display:block">
		</c:when>
		<c:otherwise>
		    <div id="cg_bin" style="display:none">
		</c:otherwise>
	    </c:choose>
		<input type="hidden" id="job_cgbinaryy" value="${binary}">
		<select ${ebinary} name="job_cgbinary" id="job_cgbinary" onChange="">
		    <option value=""><msg:getText key="text.job.cg.selectcode" /></option>
		    <c:forEach var="tmp0" items="${CGbinary}"  >
			    <c:choose>
				<c:when test="${tmp0.binary==binary}"><option selected="true" value="${tmp0.binary}">${tmp0.desc}</option></c:when>
				<c:otherwise><option value="${tmp0.binary}">${tmp0.desc}</option></c:otherwise>
			    </c:choose>
		    </c:forEach>
		</select>
		<msg:toolTip id="cicc" tkey="config.job.cgbinary" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		<c:if test="${job.labelBinary!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelBinary}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${job.descBinary!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descBinary}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </div>


	    <c:choose>
		<c:when test="${sgridtype=='gUSE'}">
		    <div id="er_bin" style="display:block">
		</c:when>
		<c:otherwise>
		    <div id="er_bin" style="display:none">
		</c:otherwise>
	    </c:choose>

		<select ${ebinary} name="job_erbinary" id="job_erbinary" onChange="">
		    <option value=""><msg:getText key="text.job.cg.selectcode" /></option>
		    <c:forEach var="tmp0" items="${edgesbinary}"  >
			    <c:choose>
				<c:when test="${tmp0.binary==erbinary}"><option selected="true" value="${tmp0.binary}">${tmp0.desc}</option></c:when>
				<c:otherwise><option value="${tmp0.binary}">${tmp0.desc}</option></c:otherwise>
			    </c:choose>
		    </c:forEach>
		</select>
		<msg:toolTip id="cicc" tkey="config.job.edggesbinary" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		<c:if test="${job.labelBinary!=''}"><msg:toolTip id="tmp_label" tkey="${job.labelBinary}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${job.descBinary!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descBinary}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </div>

    	</td>
    </tr>
    <tr><td colspan="2"><div class="iodline"/></td></tr>
    </table>
  </div>


<!--  gemlca job-- -->

<c:choose>
    <c:when test="${sgridtype!='gemlca'}">
<div id="jobisbinaryg" style="display:none">
    </c:when>
    <c:otherwise>
<div id="jobisbinaryg" style="display:block">
    </c:otherwise>
</c:choose>

    <table width="100%" >
<!--    <tr><td colspan="2"><div class="iodline" /></td></tr> -->
    <tr>
	<td width="30%">GEMLCA parameters: ${inpnmb} input ${oupnmb} output port(s)</td>
	<td width="30%"><msg:getText key="${gmsg}" /></td>
    </tr>

	<c:forEach var="tmp0" items="${gparams}">
		  <!--  <c:choose>
			<c:when test="${tmp0['input']=='true'}">
			    Input
			</c:when>
			<c:otherwise>
			    Output
		        </c:otherwise>
		    </c:choose>	-->
		<c:choose>
			<c:when test="${tmp0['file']=='true'}">
			    <input type="hidden" name="job_gparam${tmp0['nbr']}" id="job_gparam${tmp0['nbr']}" value="<c:out value="${tmp0['svalue']}" />"  ><!--File-->
			</c:when>
			<c:otherwise>
	    		<tr>
			<td width="30%">${tmp0['friendlyName']} (${tmp0['value']}):</td>
			<td>
			<input type="text" ${eparams} name="job_gparam${tmp0['nbr']}" id="job_gparam${tmp0['nbr']}" value="<c:out value="${tmp0['svalue']}" />" onkeyup="this.value = this.value.replace(' ', '')" onblur="javascript:gpcheck('${tmp0['nbr']}','${tmp0['value']}');" >
			</td>
			</tr>
		        </c:otherwise>
		</c:choose>
	</c:forEach>

    </table>
    <br><c:if test="${gmsg==null}"><H2><msg:getText key="please.configure.jobio" /></H2><br></c:if>
  </div>

