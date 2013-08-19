<!-- JOB IO szekesztesenek UIja -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<msg:help id="helptext" tkey="help.io" img="${pageContext.request.contextPath}/img/help.gif" />
<link type="text/css" href="/gridsphere/themes/default/css/default.css" rel="stylesheet">
<div  style="position:relative;border:1px solid #ad2e27; width:100%; height:400px;overflow:auto;">

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easing.1.3.js"></script>
<ul class="container">
    <c:forEach var="tmp" items="${inputs}">
    <li class="toogle_menu">
	<ul>
	    <li class="button">
		<a href="#" class="green">
		    <c:out value="${tmp.name}" escapeXml="true"  /> 
		    <c:if test="${tmp.preJob!=''}">(channel)</c:if>
		    <c:out value="${tmp.txt}" escapeXml="true"  />
		</a>
	    </li>
            <li class="dropdown">
                <ul class="toggle_data">
                    <li class="toggle_data">alma</li>
                    <li class="toggle_data">korte</li>
                </ul>
	    </li>
          </ul>
    </li>
    </c:forEach>
</ul>
<div class="clear"></div>



<table width="100%" class="window-main">
    <c:if test="${inputsnum>0}">
	<tr><th colspan="2" class="khead">Inputs</th></tr>    	
    </c:if>	
    <c:forEach var="tmp" items="${inputs}">
    <tr><td>
	<table width="100%" >
    	    <tr><td colspan="2" style="padding-top:5px;"></td></tr>    	
	    <tr><td colspan="2" class="ksline"></td></tr>
	    <tr>
		<th class="kahead">
		     ${tmp.seq}: <c:out value="${tmp.name}" escapeXml="true"  />
		     <c:if test="${tmp.preJob!=''}">(channel)</c:if>
		</th>
		<th class="kahead"><c:out value="${tmp.txt}" escapeXml="true"  /></th>
	    </tr>  
	    <c:choose>	
	    <c:when test="${jobgridtype=='gemlca'}">	
    	    <tr> 
		<th class="prop-text" width="30%"><msg:getText key="text.io.gemlcaint" />:</th>
		<td>
		    <select ${tmp.enabledInternalName} class="portlet-form-button" name="input_${tmp.id}_gout" id="input_${tmp.id}_gout" onClick="javascript:document.getElementById('input_${tmp.id}_intname').value=(this.value);">
			<c:forEach var="tmp0" items="${gininames}" varStatus="tmpi">
			<c:choose>
			    <c:when test="${tmp0==tmp.internalName}"><option selected="true" value="${tmp0}">${ginfriendlyNames[tmpi.index]}</option></c:when>
			    <c:otherwise><option value="${tmp0}">${ginfriendlyNames[tmpi.index]}</option></c:otherwise>
			</c:choose>    
			</c:forEach>			
		    </select>
		    <msg:getText key="${gmsg}" />
		</td>    	
	    </tr>
	    <tr><td colspan="2"><div class="iodline"></td></tr>	
	    </c:when>	
	    <c:otherwise></c:otherwise>	
	    </c:choose>	
	
	
	    <c:choose>	
	    <c:when test="${sworkflow=='null'}">	
    	    <tr>
		<th class="prop-text" width="30%"><div <c:if test="${jobgridtype=='gemlca'}">style="display:none"</c:if>><msg:getText key="text.io.0" />:</div></th>
	        <td><div <c:if test="${jobgridtype=='gemlca'}">style="display:none"</c:if>>
		    <input title='<msg:getText key="config.input.internalfilename" />' ${tmp.enabledInternalName} type="text" name="input_${tmp.id}_intname" id="input_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />  
		    <c:if test="${tmp.labelInternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelInternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		    <c:if test="${tmp.descInternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descInternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
		</div>	
		</td>
	    </tr>
	    <tr><td colspan="2"><div <c:if test="${jobgridtype=='gemlca'}">style="display:none"</c:if>><div class="iodline"></div></td></tr>
	    </c:when>	
	    <c:otherwise>	
    	    <tr>
		<th class="prop-text" width="30%"><msg:getText key="text.io.0" />:</th>
	        <td><input title='<msg:getText key="config.input.internalfilename" />' disabled="true" ${tmp.enabledInternalName} type="text" name="input_${tmp.id}_intname" id="input_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />
		    <c:if test="${tmp.labelInternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelInternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		    <c:if test="${tmp.descInternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descInternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </tr>
	    <tr><td colspan="2"><div class="iodline"></td></tr>
	    </c:otherwise>	
	    </c:choose>	

	    <c:if test="${iworkflow!='null'}">	
	    <tr>
	    <th class="prop-text" width="30%"><msg:getText key="text.io.50" />: </th>
	    <td> 
		<c:choose>
		    <c:when test="${tmp.iinput!=''}">
    			<input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="yes" checked="true" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='block';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='none';document.getElementById('${tmp.id}_param').style.display='none';document.getElementById('input_${tmp.id}_eparam_id1').checked=false;document.getElementById('input_${tmp.id}_eparam_id2').checked=true;document.getElementById('input_${tmp.id}_waiting').value='one';document.getElementById('input_${tmp.id}_waitingtmp_id1').checked=true;document.getElementById('input_${tmp.id}_waitingtmp_id2').checked=false;" />Yes
			<input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="no" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='none';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='block';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='block';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='block';document.getElementById('input_${tmp.id}_iinput').value='';" />No
		    </c:when>
		    <c:otherwise>
    			<input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="yes" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='block';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='none';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='none';document.getElementById('${tmp.id}_param').style.display='none';document.getElementById('input_${tmp.id}_eparam_id1').checked=false;document.getElementById('input_${tmp.id}_eparam_id2').checked=true;document.getElementById('input_${tmp.id}_waiting').value='one';document.getElementById('input_${tmp.id}_waitingtmp_id1').checked=true;document.getElementById('input_${tmp.id}_waitingtmp_id2').checked=false;" />Yes
			<input title='<msg:getText key="config.input.embed" />' ${tmp.enabledIinput} type="radio" name="input_${tmp.id}_embedparam" value="no" checked="true" onclick="javascript:document.getElementById('embed_${tmp.id}').style.display='none';document.getElementById('inp_hid_div1_${tmp.id}_param').style.display='block';document.getElementById('inp_hid_div2_${tmp.id}_param').style.display='block';document.getElementById('inp_hid_div3_${tmp.id}_param').style.display='block';document.getElementById('input_${tmp.id}_iinput').value='';" />No
		    </c:otherwise>
		</c:choose>
	    </td>
	    </tr>
    	    <tr>
    		<td colspan="2">
		<c:choose>
		<c:when test="${tmp.iinput!=''}">
		    <div id="embed_${tmp.id}" style="display:block">
		</c:when>
		<c:otherwise>
		    <div id="embed_${tmp.id}" style="display:none">
		</c:otherwise>
		</c:choose>    
	    	    <table style="text-align:left;" width="100%" >
	    	    <tr>
			<th class="prop-text" width="30%">&nbsp;&nbsp;<msg:getText key="text.io.enbedinput" /></th>
			<td>
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
			</td>
		    </tr>
		    </table>
		    </div>
		</td>
	    </tr>
	    <tr><td colspan="2"><div class="iodline"></td></tr>
	    </c:if>

    <c:set var="inputCondition" value="none" />
    <c:if test="${iworkflow=='null' || iworkflow==''}"><c:set var="inputCondition" value="block" /> </c:if>
    
	    <tr>
	    <th class="prop-text" width="30%"><div style="display:${inputCondition}"><msg:getText key="text.io.1" />:</div></th>
	    <td> 
        	<div style="display:${inputCondition}">
		<c:choose>
		<c:when test="${tmp.equalType>0}">
		    <input title='<msg:getText key="config.input.depend" />' type="radio" id="dependr_${tmp.id}_id1" name="dependr_${tmp.id}" onclick="javascript:document.getElementById('depend_${tmp.id}').style.display='block';" checked="true" <c:if test="${tmp.waiting=='all'}">disabled=true</c:if> ><msg:getText key="text.io.2" />
		    <input title='<msg:getText key="config.input.depend" />' type="radio" id="dependr_${tmp.id}_id2" name="dependr_${tmp.id}" onclick="javascript:document.getElementById('depend_${tmp.id}').style.display='none';" <c:if test="${tmp.waiting=='all'}">disabled=true</c:if> ><msg:getText key="text.io.3" />
        	    <c:set var="dependCondition" value="block" />
		</c:when>
		<c:otherwise>
		    <input title='<msg:getText key="config.input.depend" />' type="radio" id="dependr_${tmp.id}_id1" name="dependr_${tmp.id}" onclick="javascript:document.getElementById('depend_${tmp.id}').style.display='block';" <c:if test="${tmp.waiting=='all'}">disabled=true</c:if> ><msg:getText key="text.io.2" />
		    <input title='<msg:getText key="config.input.depend" />' type="radio" id="dependr_${tmp.id}_id2" name="dependr_${tmp.id}" onclick="javascript:document.getElementById('depend_${tmp.id}').style.display='none';" checked="true" <c:if test="${tmp.waiting=='all'}">disabled=true</c:if> ><msg:getText key="text.io.3" />
    		    <c:set var="dependCondition" value="none" />
		</c:otherwise>
		</c:choose>
		<div id="depend_${tmp.id}" style="display:${dependCondition};">
        	    <table style="text-align:left;">
            		<tr>
                	    <td><msg:getText key="text.io.4" /></td>
        		    <td>
                		<select title='<msg:getText key="config.input.equaltype" />' ${tmp.enabledEqualType} name="input_${tmp.id}_pequaltype" id="input_${tmp.id}_pequaltype">
            			<c:choose>
                		    <c:when test="${tmp.equalType==0}"><option value="0" selected="true"></option></c:when>
                    		    <c:otherwise><option value="0"></option></c:otherwise>
            			</c:choose>
            			<c:choose>
                		    <c:when test="${tmp.equalType==1}"><option value="1" selected="true">==</option></c:when>
                		    <c:otherwise><option value="1">==</option></c:otherwise>
            			</c:choose>
                    		<c:choose>
                		    <c:when test="${tmp.equalType==2}"><option value="2" selected="true">!=</option></c:when>
                		    <c:otherwise><option value="2">!=</option></c:otherwise>
            			</c:choose>
                    		<c:choose>
                		    <c:when test="${tmp.equalType==3}"><option value="3" selected="true">contain</option></c:when>
                		    <c:otherwise><option value="3">contain</option></c:otherwise>
            			</c:choose>
                    		</select>
                		<c:if test="${tmp.labelEqualType!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelEqualType}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
                		<c:if test="${tmp.descEqualType!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descEqualType}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
            		    </td>
            		</tr>
    			<tr>
            		<c:choose>
                        <c:when test="${tmp.equalInput!=''}">
            		    <td>
            			<input title='<msg:getText key="config.input.eqalvalue" />' ${tmp.enabledEqualType} type="radio" name="input_${tmp.id}_pequaltype" onclick="javascript:document.getElementById('input_${tmp.id}_pequalinput').disabled=true;document.getElementById('input_${tmp.id}_pequalvalue').disabled=false;document.getElementById('input_${tmp.id}_pequalinput').selectedIndex='0';">Value:
            		    </td>
                    	    <td>
                            	<input title='<msg:getText key="config.input.eqalvalue" />' ${tmp.enabledEqualType} type="text" name="input_${tmp.id}_pequalvalue" id="input_${tmp.id}_pequalvalue" disabled="true" id="input_${tmp.id}_pequalvalue" value="<c:out value="${tmp.equalValue}" escapeXml="true"  />"/>
                    		<c:if test="${tmp.labelEqualValue!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelEqualValue}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
                            	<c:if test="${tmp.descEqualValue!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descEqualValue}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
            		    </td>
                        </c:when>
                        <c:otherwise>
        		    <td>
        			<input title='<msg:getText key="config.input.equalvalue" />' ${tmp.enabledEqualType} type="radio" name="input_${tmp.id}_pequaltype" checked="true" onclick="javascript:document.getElementById('input_${tmp.id}_pequalinput').disabled=true;document.getElementById('input_${tmp.id}_pequalvalue').disabled=false;document.getElementById('input_${tmp.id}_pequalinput').selectedIndex='0';"><msg:getText key="text.io.5" />:
        		    </td>
            		    <td>
                		<input title='<msg:getText key="config.input.equalvalue" />' ${tmp.enabledEqualType} type="text" name="input_${tmp.id}_pequalvalue" id="input_${tmp.id}_pequalvalue" value="<c:out value="${tmp.equalValue}" escapeXml="true"  />"/>
                        	<c:if test="${tmp.labelEqualValue!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelEqualValue}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
                        	<c:if test="${tmp.descEqualValue!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descEqualValue}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
        		    </td>
            		</c:otherwise>
                	</c:choose>
            		</tr>
    			<tr>
            		<c:choose>
                        <c:when test="${tmp.equalInput!=''}">
                	    <td>
                		<input title='<msg:getText key="config.input.pequalinput" />' ${tmp.enabledEqualType} type="radio" name="input_${tmp.id}_pequaltype" checked="true" onclick="javascript:document.getElementById('input_${tmp.id}_pequalinput').disabled=false;document.getElementById('input_${tmp.id}_pequalvalue').disabled=true;document.getElementById('input_${tmp.id}_pequalvalue').value='';"><msg:getText key="text.io.6" />:
                	    </td>
                            <td>
                    		<select title='<msg:getText key="config.input.pequalinput" />' ${tmp.enabledEqualType} name="input_${tmp.id}_pequalinput" id="input_${tmp.id}_pequalinput">
                        </c:when>
                        <c:otherwise>
                	    <td>
                		<input title='<msg:getText key="config.input.pequalinput" />' ${tmp.enabledEqualType} type="radio" name=input_${tmp.id}_pequaltype onclick="javascript:document.getElementById('input_${tmp.id}_pequalinput').disabled=false;document.getElementById('input_${tmp.id}_pequalvalue').disabled=true;document.getElementById('input_${tmp.id}_pequalvalue').value='';"><msg:getText key="text.io.6" />:</td>
                            <td>
                        	<select title='<msg:getText key="config.input.pequalinput" />' ${tmp.enabledEqualType} name="input_${tmp.id}_pequalinput" id="input_${tmp.id}_pequalinput" disabled="true">
          		</c:otherwise>
                	</c:choose>
    				    <option></option>
        			<c:forEach var="tmp0" items="${allinputs}">
                		<c:choose>
				    <c:when test="${tmp.equalInput==tmp0}"><option value="${tmp0}" selected="true">${tmp0}</option></c:when>
				    <c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
				</c:choose>	
                		</c:forEach>
				</select>
				<c:if test="${tmp.labelEqualInput!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelEqualInput}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
				<c:if test="${tmp.descEqualInput!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descEqualInput}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
			    </td>
			</tr>
		    </table>
    		</div> <%-- dependCondition --%>
	    </div><%--<div style="display:${inputCondition}">--%>
	</td>	    	
	</tr>
	<tr><td colspan="2"><div class="iodline"></td></tr>
	
<%--------------NEM CSATORNA INPUT------------------%>
	<c:if test="${tmp.preJob==''}">	
	<tr>
	    <th class="prop-text" width="30%"><msg:getText key="text.io.23" />: <br/><br/> <msg:getText key="text.io.24" /></th>
	    <td>
		<table style="text-align:left;">
		<div id='page'>
		    <div class='menu'>
	    		<a class="shadow" href="javascript:inputtypepanelhide('${tmp.id}','upload');"><img id="it0_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/workflow.png' title='<msg:getText key="text.io.25" />|' alt='' /></a>
			<a class="shadow" href="javascript:inputtypepanelhide('${tmp.id}','remote');"><img id="it1_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/service.png' title='<msg:getText key="text.io.26" />|' alt='' /></a>
			<a class="shadow" href="javascript:inputtypepanelhide('${tmp.id}','constans');"><img id="it2_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/binary.png' title='<msg:getText key="text.io.5" />|' alt='' /></a>
			<a class="shadow" href="javascript:inputtypepanelhide('${tmp.id}','sql');"><img id="it3_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/cloud.png' title='<msg:getText key="text.io.28" />|' alt='' /></a>
			<a class="shadow" href="javascript:inputtypepanelhide('${tmp.id}','cgrid');"><img id="it4_${tmp.id}" src='${pageContext.request.contextPath}/imgs/configure/cloud.png' title='<msg:getText key="text.io.115" />|' alt='' /></a>
		    </div>
		</div>

		

<div id="jobinputtype_${tmp.id}_upload" style="display:none">
		<msg:getText key="text.io.21" />

		<c:out value="${tmp.externalName}" escapeXml="true"  />
		<msg:toolTip id="cicc" tkey="config.input.externalname" img="${pageContext.request.contextPath}/img/tooltip.gif" />
		<c:if test="${tmp.labelExternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelExternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descExternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descExternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
		<br />
			<div id="upload_input${tmp.id}" style="float:left;display:block;">
			<c:choose> 
			<c:when test="${tmp.defaultFile || tmp.externalName=='N/A'}">
			    <input title='<msg:getText key="config.input.file" />' type="file" ${tmp.enabledInputType} name="input_${tmp.id}_file" id="input_${tmp.id}_file" >
			</c:when>
			<c:otherwise>
    			    <input type="file" disabled="true" name="input_${tmp.id}_file" id="input_${tmp.id}_file" >
			</c:otherwise>
			</c:choose>
			</div>
			<c:set var="adi" value="${adi};additem('input${tmp.id}','input_${tmp.seq}','${jobID}','input_${tmp.id}_file')" />
			<c:if test="${tmp.labelExternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelExternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
			<c:if test="${tmp.descExternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descExternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
</div>

<div id="jobinputtype_${tmp.id}_remote" style="display:none">
			<c:choose>
			    <c:when test="${tmp.remoteName=='' || jobgridtype=='gemlca'}">					
				<input title='<msg:getText key="config.input.remote" />' type="text" disabled="true" name="input_${tmp.id}_remote" id="input_${tmp.id}_remote" value="<c:out value="${tmp.remoteName}" escapeXml="true"  />">
    				<input title='<msg:getText key="config.input.remotecopy" />' type="checkbox" disabled="true" name="input_${tmp.id}_remotecopy" id="input_${tmp.id}_remotecopy" checked="true" value=""> <msg:getText key="text.io.100" />: 
			    </c:when>
			    <c:otherwise>
				<input title='<msg:getText key="config.input.remote" />' type="text" ${tmp.enabledInputType} name="input_${tmp.id}_remote" id="input_${tmp.id}_remote" value="<c:out value="${tmp.remoteName}" escapeXml="true"  />" <c:if test="${jobgridtype=='gemlca'}">disabled</c:if> >
    				<input title='<msg:getText key="config.input.remotecopy" />' type="checkbox" ${tmp.enabledInputType} name="input_${tmp.id}_remotecopy" id="input_${tmp.id}_remotecopy" checked="true" value=""> <msg:getText key="text.io.100" />: 
			    </c:otherwise>
			</c:choose>
			<c:if test="${tmp.labelRemoteName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelRemoteName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
			<c:if test="${tmp.descRemoteName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descRemoteName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
</div>

<div id="jobinputtype_${tmp.id}_constans" style="display:none">
			<c:choose>
			    <c:when test="${tmp.value==''}">
				<input title='<msg:getText key="config.input.value" />' type="text" disabled="true" name="input_${tmp.id}_value" id="input_${tmp.id}_value" value="<c:out value="${tmp.value}" escapeXml="true"  />">
			    </c:when>
			    <c:otherwise>
				<input title='<msg:getText key="config.input.value" />' type="text" ${tmp.enabledInputType}  name="input_${tmp.id}_file" id="input_${tmp.id}_value" value="<c:out value="${tmp.value}" escapeXml="true"  />">
			    </c:otherwise>
			</c:choose>
			<c:if test="${tmp.labelValue!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelValue}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /> </c:if>
			<c:if test="${tmp.descValue!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descValue}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /> </c:if>
</div>

<div id="jobinputtype_${tmp.id}_sql" style="display:none" >
				    <table border="1">
					<tr>
					    <td><msg:getText key="text.io.29" />
						<c:if test="${tmp.labelSqlurl!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqlurl}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqlurl!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqlurl}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					    <td colsan="3"><input type="text" ${tmp.enabledInputType} name="input_${tmp.id}_sqlurl" id="input_${tmp.id}_sqlurl" size="100" value="<c:out value="${tmp.sqlurl}" escapeXml="true"  />"/> </td>
					</tr>
					<tr>
					    <td><msg:getText key="text.io.30" />
						<c:if test="${tmp.labelSqluser!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqluser}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqluser!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqluser}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					    <td><input type="text" ${tmp.enabledInputType} name="input_${tmp.id}_sqluser" id="input_${tmp.id}_sqluser" size="20" value="<c:out value="${tmp.sqluser}" escapeXml="true"  />"/></td>
					</tr>    
					<tr>
					    <td><msg:getText key="text.io.31" />
						<c:if test="${tmp.labelSqlpass!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqlpass}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqlpass!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqlpass}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					    <td><input type="password" ${tmp.enabledInputType} name="input_${tmp.id}_sqlpass" id="input_${tmp.id}_sqlpass" size="20" value="<c:out value="${tmp.sqlpass}" escapeXml="true"  />"/></td>
					</tr>        
					<tr>
					    <td><msg:getText key="text.io.32" />
						<c:if test="${tmp.labelSqlselect!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelSqlselect}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
						<c:if test="${tmp.descSqlselect!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descSqlselect}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
					    </td>
					    <td colsan="3">SELECT <input type="text" ${tmp.enabledInputType} name="input_${tmp.id}_sqlselect" id="input_${tmp.id}_sqlselect"  size="100" value="<c:out value="${tmp.sqlselect}" escapeXml="true"  />"/> </td>
					</tr>
				    </table>
				    
</div>


<div id="jobinputtype_${tmp.id}_cgrid" style="display:none">
			<c:choose>
			    <c:when test="${tmp.cprop==''}">
				<input type="button" disabled="true" name="input_${tmp.id}_cpropbutton" id="input_${tmp.id}_cpropbutton" onclick="javascript:getConfForm('m=GetPropertyWindow&job=${jobname}&pdiv=input_${tmp.id}_cpropdiv&pvl=input_${tmp.id}_cprop');loadForm('input_${tmp.id}_cprop')" value="view property window"/>
				<input type="hidden" ${tmp.enabledExternalName} name="input_${tmp.id}_cprop" id="input_${tmp.id}_cprop" value="${tmp.cprop}"/>
			    </c:when>
			    <c:otherwise>
				<input type="button" name="input_${tmp.id}_cpropbutton" id="input_${tmp.id}_cpropbutton" onclick="javascript:getConfForm('m=GetPropertyWindow&job=${jobname}&pdiv=input_${tmp.id}_cpropdiv&pvl=input_${tmp.id}_cprop');loadForm('input_${tmp.id}_cprop')" value="view property window"/>
				<input type="hidden" ${tmp.enabledExternalName} name="input_${tmp.id}_cprop" id="input_${tmp.id}_cprop" disabled="true" value="${tmp.cprop}"/>
			    </c:otherwise>
			</c:choose>
			
			<msg:toolTip id="cicc" tkey="config.input.cprop" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			<c:if test="${tmp.labelCprop!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelCprop}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
			<c:if test="${tmp.descCprop!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descCprop}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
			<br />
			<div id="input_${tmp.id}_cpropdiv"></div>

</div>

	    </td>
	</tr>
	</c:if>
	
<%------------------PREJOB------------------%>
	<tr>
	<td colspan="2">
	    <div class="iodline">
	</td>
	</tr>
	
	<tr>
	    <th class="prop-text" width="30%"<msg:getText key="text.io.7" /></th>
	    <td>
		<c:choose>
		<c:when test="${tmp.max!=''}">
		    <input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id1" name="input_${tmp.id}_eparam" value="1" checked="true" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='block';"><msg:getText key="text.io.2" />
		    <input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id2" name="input_${tmp.id}_eparam" value="0" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='none';"><msg:getText key="text.io.3" />
		</c:when>
		<c:otherwise>
		    <input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id1" name="input_${tmp.id}_eparam" value="1" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='block';"><msg:getText key="text.io.2" />
		    <input title='<msg:getText key="config.input.paraminput" />' type="radio" id="input_${tmp.id}_eparam_id2" name="input_${tmp.id}_eparam" value="0" checked="true" onclick="javascript:document.getElementById('${tmp.id}_param').style.display='none';"><msg:getText key="text.io.3" />
		</c:otherwise>
		</c:choose>
	    </td>
	</tr>
    </table></td></tr>
    <tr><td>
		<c:choose>
		<c:when test="${tmp.max!=''}">
		    <div id="${tmp.id}_param" style="display:block">
		</c:when>
		<c:otherwise>
		    <div id="${tmp.id}_param" style="display:none">
		</c:otherwise>
		</c:choose>
    <table style="text-align:left;" width="100%" >	
	<tr>
	    <th class="prop-text" width="30%"><msg:getText key="text.io.15" /></th>
	    <td>
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

	    </td>
	</tr>
	<tr>
	    <td colspan="2">
		<c:choose>
		    <c:when test="${tmp.iinput==''}">
		<div id="inp_hid_div1_${tmp.id}_param" style="display:block">
		    </c:when>
		    <c:otherwise>
		<div id="inp_hid_div1_${tmp.id}_param" style="display:none">
		    </c:otherwise>
		</c:choose>
		    <div class="iodline">
		</div>
	    </td>
	</tr>
<%----kompatibilitasi okok miatt ---%>
	<input type="hidden" name="input_${tmp.id}_hnt"  id="input_${tmp.id}_hnt" />

	<c:if test="${tmp.preJob==''}">
	<tr>
	    <th class="prop-text" width="30%"><msg:getText key="text.io.19" /></th>
	    <td>
		<input title='<msg:getText key="config.input.max" />' ${tmp.enabledMax} name="input_${tmp.id}_max" value="${tmp.max}" id="input_${tmp.id}_max" onkeyup="this.value = this.value.replace(/[^0-9]/g, '')">
		<c:if test="${tmp.labelMax!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelMax}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descMax!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descMax}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </td>
	</tr>
	<tr>
	    <td colspan="2">
		<div class="iodline">
	    </td>
	</tr>
	</c:if>
	<c:if test="${tmp.preJob!=''}">
	<tr>
	    <td width="30%">
		<c:choose>
		    <c:when test="${tmp.iinput==''}">
		<div id="inp_hid_div2_${tmp.id}_param" style="display:block">
		    </c:when>
		    <c:otherwise>
		<div id="inp_hid_div2_${tmp.id}_param" style="display:none">
		    </c:otherwise>
		</c:choose>
		    <msg:getText key="text.io.16" />
		</div>
	    </td>
	    <td>
	    <c:choose>
		<c:when test="${tmp.iinput==''}">
	    <div id="inp_hid_div3_${tmp.id}_param" style="display:block">
		</c:when>
		<c:otherwise>
	    <div id="inp_hid_div3_${tmp.id}_param" style="display:none">
		</c:otherwise>
	    </c:choose>
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
	    </td>
	</tr>
	<tr><td colspan="2"><div class="iodline"></td></tr>
	</c:if>
    </table>
    </div>
    <tr><td colspan="2"><div class="iodline"></td></tr>
    </c:forEach>
    
</table>

<table width="100%" class="window-main">
    <c:if test="${outputsnum>0}">
	<tr><th colspan="2" class="khead"><msg:getText key="text.io.8" /></th></tr>    	
    </c:if>	
    <c:forEach var="tmp" items="${outputs}">
    <tr><td colspan="2" style="padding-top:5px;" class="ksline"></tr>
	<tr>
	    <th class="kahead">${tmp.seq}: <c:out value="${tmp.name}" escapeXml="true"  /></th>
	    <th class="kahead">${tmp.txt}</th>
	</tr>    	
	
	<tr><td colspan="2"><div class="iodline">&nbsp</div></td></tr>
	<tr><td colspan="2"> 
	
	
	<c:choose>	
	<c:when test="${jobgridtype=='gemlca'}">	
    	    <tr>
		<th class="prop-text" width="30%"><msg:getText key="text.io.gemlcaint" />:</th>
		<td>
		    <select ${tmp.enabledInternalName} class="portlet-form-button" name="output_${tmp.id}_gout" id="output_${tmp.id}_gout" onClick="javascript:document.getElementById('output_${tmp.id}_intname').value=(this.value);">
			<c:forEach var="tmp0" items="${goutinames}" varStatus="tmpi">
			<c:choose>
			    <c:when test="${tmp0==tmp.internalName}"><option selected="true" value="${tmp0}">${goutfriendlyNames[tmpi.index]}</option></c:when>
			    <c:otherwise><option value="${tmp0}">${goutfriendlyNames[tmpi.index]}</option></c:otherwise>
			</c:choose>    
			</c:forEach>			
		    </select>
		    <msg:getText key="${gmsg}" />
		</td>    	
	    </tr>
	    <tr><td colspan="2"><div class="iodline"></td></tr>	
	</c:when>	
	<c:otherwise></c:otherwise>	
	</c:choose>	
	
	
	</td></tr>
	<tr>
	    <th class="prop-text" width="30%">
			<c:choose>
			    <c:when test="${jobgridtype=='gemlca'}"><msg:getText key="text.io.gemlcaintoptional" />:</c:when>
			    <c:otherwise><msg:getText key="text.io.9" />: </c:otherwise>
			</c:choose>
	    </th>
	    <td>
		<input type="text" ${tmp.enabledInternalName} name="output_${tmp.id}_intname" id="output_${tmp.id}_intname" value="<c:out value="${tmp.internalName}" escapeXml="true"  />" />
			<c:choose>
			    <c:when test="${jobgridtype=='gemlca'}"><msg:toolTip id="cicc" tkey="config.output.intnamegemlca" img="${pageContext.request.contextPath}/img/tooltip.gif" /></c:when>
			    <c:otherwise><msg:toolTip id="cicc" tkey="config.output.intname" img="${pageContext.request.contextPath}/img/tooltip.gif" /></c:otherwise>
			</c:choose>	
		<c:if test="${tmp.labelInternalName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelInternalName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descInternalName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descInternalName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </td>
	</tr>
	<tr><td colspan="2"><div class="iodline"></td></tr>

<c:set var="hide" value="" />

<c:if test="${iworkflow!='null'}">
    <c:set var="hide" value="display:none" />    
</c:if>	
	<tr>
	    <th class="prop-text" width="30%" style="${hide}"><msg:getText key="text.io.10" />: </th>
	    <td style="${hide}" >
		<c:choose>
		    <c:when test="${(jobgridtype=='Local')||(jobgridtype=='gemlca')}">
			<input title='<msg:getText key="config.input.remotelogical" />' type="text" disabled="true" name="output_${tmp.id}_remote" id="output_${tmp.id}_remote" value="" />
		    </c:when>
		    <c:otherwise>
			<input title='<msg:getText key="config.input.remotelogical" />' type="text" name="output_${tmp.id}_remote" id="output_${tmp.id}_remote" value="<c:out value="${tmp.remoteName}" escapeXml="true"  />" />
		    </c:otherwise>
		</c:choose>
		<c:if test="${tmp.labelRemoteName!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelRemoteName}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descRemoteName!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descRemoteName}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </td>
	</tr>	
	<tr>
	    <th class="prop-text" width="30%" style="${hide}"><msg:getText key="text.io.11" />: </th>
	    <td style="${hide}">
		<c:choose>
		    <c:when test="${jobgridtype=='glite'}">
			<input title='<msg:getText key="config.input.remotehost" />' type="text"  name="output_${tmp.id}_remotehost" id="output_${tmp.id}_remotehost" value="<c:out value="${tmp.remoteHost}" escapeXml="true"  />" />
		    </c:when>
		    <c:otherwise>
			<input title='<msg:getText key="config.input.remotehost" />' type="text" disabled="true" name="output_${tmp.id}_remotehost" id="output_${tmp.id}_remotehost" value="" />
		    </c:otherwise>
		</c:choose>
		<c:if test="${tmp.labelRemoteHost!=''}"><msg:toolTip id="tmp_label" tkey="${tmp.labelRemoteHost}" img="${pageContext.request.contextPath}/img/tooltip2.gif" /></c:if>
		<c:if test="${tmp.descRemoteHost!=''}"><msg:toolTip id="tmp_desc" tkey="${tmp.descRemoteHost}" img="${pageContext.request.contextPath}/img/tooltip5.gif" /></c:if>
	    </td>
	</tr>
	<tr><td colspan="2" style="${hide}"><div class="iodline"></td></tr>
<c:if test="${iworkflow!='null'}">
	<tr>
	    <th class="prop-text" width="30%"><msg:getText key="text.io.51" />: </th>
	    <td>
	    <c:choose>
		<c:when test="${tmp.ioutput!=''}">	    
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" checked="true" value="yes" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='block';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='none';document.getElementById('output_${tmp.id}_maincount').value='1';document.getElementById('output_${tmp.id}_maincount0_id1').checked=true;document.getElementById('output_${tmp.id}_maincount0_id2').checked=false;" />Yes
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" value="no" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='none';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='block';document.getElementById('output_${tmp.id}_ioutput').value='';" />No
		</c:when>
		<c:otherwise>
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" value="yes" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='block';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='none';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='none';document.getElementById('output_${tmp.id}_maincount').value='1';document.getElementById('output_${tmp.id}_maincount0_id1').checked=true;document.getElementById('output_${tmp.id}_maincount0_id2').checked=false;" />Yes
		    <input title='<msg:getText key="config.output.embedparam" />' ${tmp.enabledIoutput} type="radio" name="output_${tmp.id}_embedparam" checked="true" value="no" onclick="javascript:document.getElementById('outputembed_${tmp.id}').style.display='none';document.getElementById('hidden_div1_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div2_${tmp.id}_gen').style.display='block';document.getElementById('hidden_div3_${tmp.id}_gen').style.display='block';document.getElementById('output_${tmp.id}_ioutput').value='';" />No
		</c:otherwise>
	    </c:choose>
	    <msg:toolTip id="cicc" tkey="config.output.embedparam" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	</tr>
        <tr><td colspan="2">
	    <c:choose>
		<c:when test="${tmp.ioutput!=''}">	    
	    <div id="outputembed_${tmp.id}" style="display:block">
		</c:when>
		<c:otherwise>
	    <div id="outputembed_${tmp.id}" style="display:none">
		</c:otherwise>
	    </c:choose>
	        <table width="100%">	
	    	    <tr>
			<th class="prop-text" width="30%">&nbsp;&nbsp;<msg:getText key="text.io.enbedoutput" /></th>
			<td>
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
			</td>
		    </tr>
		</table>
	    </div>
	</td></tr>	
	<tr><td colspan="2"><div class="iodline"></td></tr>
</c:if>	
	<tr>
	    <th class="prop-text" width="30%"><msg:getText key="text.io.12" />: </th>
	    <td>
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
	    </td>
	</tr>
	<tr><td colspan="2"><div class="iodline"></td></tr>

	<tr>
	    <th class="prop-text" width="30%">
		<msg:getText key="text.io.14" />: 
	    </th>
	    <td>
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
	    </td>
	</tr>
	<tr><td colspan="2"><div class="iodline" /></td></tr>

    <c:if test="${enabledDinamicWF!=null}">
        <c:set var="portID" value="${tmp.id}" scope="request" />
        <jsp:include page="output_dinamicwf.jsp" />

	<tr><td colspan="2"><div class="iodline" /></td></tr>
    </c:if>        
    </c:forEach>
    <tr>
	<td colspan="2">
    <input type="button" class="portlet-form-button" onclick="javascript:${adi};setRemoteParamDOMValue('${snd}');closePopUp();" value="<msg:getText key="button.ssave" />">
	</td>
    </tr>
</table>
</div></div>