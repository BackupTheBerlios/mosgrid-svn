<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<portlet:actionURL var="uploadURL" />
<portlet:resourceURL var="ajaxURL" />
<portlet:resourceURL var="uploadStatusURL" >
    <portlet:param name="sid" value="${confID}" />
    <portlet:param name="uploadStatus" value="" />
</portlet:resourceURL>


<div id="rwlist" style="position:relative;"> </div>
<msg:help id="helptext" tkey="help.configure" img="${pageContext.request.contextPath}/img/help.gif" />
<script type="text/javascript">
//    user='root';
    workflow='${wrkdata.workflowID}';
    sjob='';
    var sForm=1;
    var portalID="${portalID}";
    var storageID="<msg:surl dest="storage" url="${storageID}" />";
    var userID="${userID}";
    var workflowID="${wrkdata.workflowID}";
    var confID="${confID}";
    var jobID="";
    var vJob="";
    var callflag=0;
    var fileUploadErrorFlag=0;
    var formid=0;
    var ajaxURL="${ajaxURL}";
    var uploadURL="${uploadURL}";
    var uploadStatusURL="${uploadStatusURL}";

</script>


<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />
<form id="confform" method="post" action="${pURL}">
    <table width="100%">
        <tr>
	    <td>
		    <input type="hidden" name="job" id="job">
		    <input type="hidden" name="workflow" id="workflow" value="${wrkdata.workflowID}" >
		    <input type="hidden" name="confIDparam" id="confIDparam" value="${confID}" >
		    <input type="hidden" name="guse" id="action">
		    <lpds:submit actionID="action" actionValue="doList" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.back" tkey="true"/>		

<!-- 	    <lpds:submit actionID="action" actionValue="doSaveEWorkflowParams" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.saveewfparams" tkey="true"/>		-->
	    <input type="button" onclick="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);" value="<msg:getText key="button.saveandupload" />" class="portlet-form-button">
	    </td>
	</tr>
        <tr>
	    <td>
	    
		<table width="100%" class="darktable">
    		    <tr>
			<td width="20%" class="kback"><div class="bold"><msg:getText key="text.configure.workflowname"/> </div></td>
			<td width="80%" class="kback"><c:out value="${wrkdata.workflowID}" escapeXml="true" /></td>
    		    </tr>
    		    <tr>
			<td width="20%" class="kback"><div class="bold"><msg:getText key="text.configure.0" />: </div></td>
			<td width="80%" class="kback"><c:out value="${wrkdata.txt}" escapeXml="true" /></td>
    		    </tr>
		</table>
	    <br/>
	    <input type="hidden" value="${easyParamssize}" id="easyParamssize"> 
	    <table width="100%" class="darktable">
		<c:forEach var="tmp" items="${easyParams}" varStatus="ln">
    		    <tr>
			<c:choose>
			    <c:when test="${(ln.index%2)==1}">
    				<c:set var="color" value="kline1" />
			    </c:when>
			    <c:otherwise>
    				<c:set var="color" value="kline0" />
			    </c:otherwise>
			</c:choose>

			<td width="30%" class="${color}"><div class="bold"><c:out value="${tmp.label}" escapeXml="true" /></div></td>
			<td width="20" class="${color}"><c:if test="${tmp.desc!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.desc}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if></td>
			<td class="${color}">
			    <c:choose>
				<c:when test="${tmp.name=='file'}">				  
				    <input type="hidden" value="${tmp.value}" name="dparam_${ln.index}" id="dparam_${ln.index}"> 
				    <div name="vparam_${ln.index}" id="vparam_${ln.index}"><c:out value="${tmp.value}" escapeXml="true" /></div> 
				    <input type="hidden" value="${tmp.value}" name="eparam_${ln.index}" id="eparam_${ln.index}"> 
				    <div id="upload_${ln.index}" style="float:left;">
				    	<input class="portlet-form-button" type="file" name="feparam_${ln.index}" id="feparam_${ln.index}" onchange="javascript:addEitem('${ln.index}','input_${tmp.inputID}','${tmp.jobName}','feparam_${ln.index}','<msg:surl dest="storage" url="${tmp.storageID}" />','${tmp.portalID}','${tmp.userID}','${tmp.wfID}');" >
				    </div>
				    <div id="eupload_${ln.index}" style="float:left;">				    
				    </div>
				</c:when>
				<c:when test="${tmp.name=='binary'}">
				    binary:
				    <input type="hidden" value="${tmp.value}" name="dparam_${ln.index}" id="dparam_${ln.index}"> 
				    <div name="vparam_${ln.index}" id="vparam_${ln.index}"><c:out value="${tmp.value}" escapeXml="true" /></div> 
				    <input type="hidden" value="${tmp.value}" name="eparam_${ln.index}" id="eparam_${ln.index}">
				    <div id="upload_${ln.index}" style="float:left;">
					<input class="portlet-form-button" type="file" name="feparam_${ln.index}" id="feparam_${ln.index}" onchange="javascript:addEitem('${ln.index}','binary','${tmp.jobName}','feparam_${ln.index}','<msg:surl dest="storage" url="${tmp.storageID}" />','${tmp.portalID}','${tmp.userID}','${tmp.wfID}');" >									
				    </div>
				    <div id="eupload_${ln.index}" style="float:left;">
				    </div>				    
				</c:when>
				
				<c:when test="${tmp.name=='gridtype'}">
				    <input type="hidden" id="eparam_${ln.index}" value="${tmp.value}">
					<c:choose>
					<c:when test="${(tmp.gridtype!='gemlca')}">
					<c:forEach var="gridt" items="${tmp.data}" varStatus="ls">
						<c:choose>
						<c:when test="${tmp.value==gridt.key}"> <!--    document.getElementById('jobisworkflow').style.display='block';   -->
							<input type="radio" class="portlet-form-button" name="eparam_${ln.index}" id="eparam_${ln.index}_${ls.index}" value="${gridt.key}" onclick="javascript:document.getElementById('eparam_${ln.index}').value='${gridt.key}';if(${tmp.i!=null}){getRemoteSelectOptionsE('m=GetEGridnfo&e=${ln.index}&v='+document.getElementById('eparam_${ln.index}_${ls.index}').value);}" checked="true" >${gridt.key}<br>
						</c:when>
						<c:otherwise>
							<input type="radio" class="portlet-form-button" name="eparam_${ln.index}" id="eparam_${ln.index}_${ls.index}" value="${gridt.key}" onclick="javascript:document.getElementById('eparam_${ln.index}').value='${gridt.key}';if(${tmp.i!=null}){getRemoteSelectOptionsE('m=GetEGridnfo&e=${ln.index}&v='+document.getElementById('eparam_${ln.index}_${ls.index}').value);}" >${gridt.key}<br>
						</c:otherwise>
						</c:choose>
					</c:forEach>
					</c:when>
					<c:otherwise>
							<input type="radio" class="portlet-form-button" name="eparam_${ln.index}" id="eparam_${ln.index}_0" value="gemlca" onclick="javascript:document.getElementById('eparam_${ln.index}').value='gemlca';if(${tmp.i!=null}){getRemoteSelectOptionsE('m=GetEGridnfo&e=${ln.index}&v='+document.getElementById('eparam_${ln.index}_${ls.index}').value);}" checked="true" >gemlca<br>
					</c:otherwise>
					</c:choose>

				</c:when>
				<c:when test="${tmp.name=='grid'}">
	    			    <!--grid ${tmp.value}-->
				    <select class="portlet-form-button" name="eparam_${ln.index}" id="eparam_${ln.index}" onChange="if(${tmp.i!=null}){getRemoteSelectOptionsE('m=GetEGridnfo&e=${ln.index}&v='+document.getElementById('eparam_${ln.index}').value);}">
					<c:forEach var="tmp0" items="${tmp.data}">
					    <c:choose>
						<c:when test="${tmp0==tmp.value}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
						<c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
					    </c:choose>    
					</c:forEach>	
				    </select>
				    
				    
				</c:when>
				<c:when test="${tmp.name=='resource'}">
	    			    <!--resource ${tmp.value}-->
				    <select class="portlet-form-button" name="eparam_${ln.index}" id="eparam_${ln.index}" onChange="if(${tmp.i!=null}){getRemoteSelectOptionsE('m=GetEGridnfo&e=${ln.index}&v='+document.getElementById('eparam_${ln.index}').value);}">
					<c:forEach var="tmp0" items="${tmp.data}">
					    <c:choose>
						<c:when test="${tmp0==tmp.value}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
						<c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
					    </c:choose>    
					</c:forEach>	
				    </select>
				    
				</c:when>
				<c:when test="${tmp.name=='jobmanager'}">
	    			    <!--jobmanager ${tmp.value}-->
				    <select class="portlet-form-button" name="eparam_${ln.index}" id="eparam_${ln.index}">
					<c:forEach var="tmp0" items="${tmp.data}">
					    <c:choose>
						<c:when test="${tmp0==tmp.value}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
						<c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
					    </c:choose>    
					</c:forEach>	
				    </select>

				</c:when>


				<c:when test="${tmp.name=='maincount'}">
	    			    <input type="text" name="eparam_${ln.index}" value=${tmp.value} onkeyup="this.value = this.value.replace(/[^0-9]/g, '')">
				</c:when>
			
				<c:when test="${tmp.name=='params'}">
					<c:choose>
					<c:when test="${tmp.gridtype!='gemlca'}">
						<input type="text" name="eparam_${ln.index}" value="<c:out value="${tmp.value}" escapeXml="true" />">
					</c:when>
					<c:otherwise>
					<input type="hidden" id="eparam_${ln.index}" name="eparam_${ln.index}" value="${tmp.value}">
					<table width="100%"   >
					<tr><td colspan="2"><div class="iodline" /></td></tr> 					
						<c:forEach var="tmp0" items="${tmp.gparams}">
						<tr>	
							<c:choose>
							<c:when test="${tmp0['file']=='true'}">
							<td width="30%"></td><td><input type="hidden" name="job_gparam${ln.index}${tmp0['nbr']}" id="job_gparam${ln.index}${tmp0['nbr']}" value="<c:out value="${tmp0['svalue']}" escapeXml="true" />" onkeyup="this.value = this.value.replace(' ', '')" onblur="javascript:egpcheck('${ln.index}','${tmp0['nbr']}','${tmp0['value']}');"></td>
							</c:when>
							<c:otherwise>
							<td width="30%">${tmp0['friendlyName']} (${tmp0['value']}):</td><td><input type="text" name="job_gparam${ln.index}${tmp0['nbr']}" id="job_gparam${ln.index}${tmp0['nbr']}" value="<c:out value="${tmp0['svalue']}" escapeXml="true" />" onkeyup="this.value = this.value.replace(' ', '')" onblur="javascript:egpcheck('${ln.index}','${tmp0['nbr']}','${tmp0['value']}');" ></td>
							</c:otherwise>
							</c:choose>
							
						</tr>
						</c:forEach>	
					
					</table>
					</c:otherwise>
					</c:choose>
				</c:when>

				<c:when test="${(tmp.name=='intname') && (tmp.gridtype=='gemlca')}">
					<input type="hidden" name="eparam_${ln.index}" value="${tmp.value}"><c:out value="${tmp.value}" escapeXml="true" />
				</c:when>

				<c:when test="${tmp.name=='cprop'}">
					<c:choose>
					<c:when test="${tmp.value==''}">
						<input type="button" disabled="true" name="eparam_${ln.index}button" id="eparam_${ln.index}button" onclick="javascript:ecleardivs();getConfForm('m=GetPropertyWindow&job=${tmp.jobName}&pdiv=eparam_${ln.index}div&pvl=eparam_${ln.index}');loadForm('eparam_${ln.index}')" value="view property window">
						<input type="hidden" name="eparam_${ln.index}" id="eparam_${ln.index}" value="${tmp.value}">
					</c:when>
					<c:otherwise>
						<input type="button" name="eparam_${ln.index}button" id="eparam_${ln.index}button" onclick="javascript:ecleardivs();getConfForm('m=GetPropertyWindow&job=${tmp.jobName}&pdiv=eparam_${ln.index}div&pvl=eparam_${ln.index}');loadForm('eparam_${ln.index}')" value="view property window">
						<input type="hidden" name="eparam_${ln.index}" id="eparam_${ln.index}" value="${tmp.value}">
					</c:otherwise>
					</c:choose>
					<div id="eparam_${ln.index}div"></div>
				</c:when>

				<c:otherwise>
				    <input type="text" name="eparam_${ln.index}" value="<c:out value="${tmp.value}" escapeXml="true" />">
				</c:otherwise>
			    </c:choose>

			    
			</td>
			
    		    </tr>
		</c:forEach>
    	    </table>	
		
	<!--	</form> -->
	    </td>
	</tr>

    </table>
    <div id="disbl" class="shape" style="z-index:100;display: none; position: absolute; width: 1280px; height: 1000px; top: 0; left: 0; color: rgb(255, 255, 255); filter: 'alpha(opacity=80)';">

        <div class="hdn_txt" style="top:200px">
            <div id="jsmsg" ><msg:getText key="text.configure.uploadinp"/></div>
            <div id="jsmsg0" style="display:none" ><msg:getText key="text.configure.uploadsuc"/></div>
            <div><msg:getText key="text.configure.uploadstat"/> <div id="statusforms" style="float:left;display:block;"> </div> </div>
            <div id="jmsgreload" style="display:none;width:490px;">
             <input  type="button" value="OK" onclick="javascript:callDoSaveEWorkflowParams();" />
            </div>
        </div>


    </div> 
    
<div style="position:absolute">

    <table width="100%">
        <tr>
	    <td>
		<table>
		<tr>
		<td>
		    <div style="height:300px;overflow:auto;">
    			<div id="hideforms" style="display:none;"></div>
			<div id="uploadstatus" style="float:left;display:block"></div>
			<IFRAME src="" width="0" height="0" name="none" scrolling="none" frameborder="0"></IFRAME>
		    </div>	
		</td>
		</tr>
		</table>			
		
	    </td>
	    <td>
	    </td>
	    	
	</tr>
    </table>		    
</div>
</form>
<div id="cdata" class="shape" style="display:none;position:absolute;left:5px;min-height:600px;" ></div>
            
<table>
    <tr>
	<td><div class="bold"><msg:getText key="text.global.0" />: </div></td>
	<td>${msg}</td>
    </tr>
</table>
<script>
    document.getElementById("cdata").style.top=(document.getElementById("rwlist").offsetTop);
//    document.getElementById("cdata").style.min-height=document.getElementById("rwend").offsetTop; 
</script>
