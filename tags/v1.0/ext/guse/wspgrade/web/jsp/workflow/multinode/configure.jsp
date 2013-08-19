<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="/portletUI" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ui.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tooltip.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/graph.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/props/props.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/calendar.js"></script>
<link type="text/css" href="${pageContext.request.contextPath}/css/calendar.css" rel="stylesheet"/>
<link type="text/css" href="${pageContext.request.contextPath}/props/form.css" rel="stylesheet"/>
<div id="rwlist" style="position:relative;"> </div>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.configure" img="${pageContext.request.contextPath}/img/help.gif" />

<style>
.clink
{
    color:#ff0000;
    font-weight:bolder;
    font-size: 18px;
}
</style>

<script>
<!-- 
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
    var formid=0;    
//    var sid0=document.cookie.split("=");
//-->
 
</script>


<portlet:defineObjects/>
<portlet:actionURL var="pURL" action="lpds" portletMode="VIEW" />

    <table width="100%">
        <tr>
	    <td class="portlet-section-body">
		<form id="confform" method="post" action="${pURL}">
		    <input type="hidden" name="job" id="job">
		    <input type="hidden" name="workflow" id="workflow" value="${wrkdata.workflowID}" >
		    <input type="hidden" name="action" id="action">
		    <input type="hidden" name="confIDparam" id="confIDparam" value="${confID}" >
		    <lpds:submit actionID="action" actionValue="doList" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.back" tkey="true"/>		
	
	<!--	</form> -->
	    </td>	
	</tr>	    
        <tr>
	    <td class="portlet-section-body">
		<table width="100%" class="darktable"">
    		    <tr>
			<td width="20%" class="kback"><div class="bold"><msg:getText key="text.configure.workflowname"/> </div></td>
			<td width="80%" class="kback">${wrkdata.workflowID}</td>
    		    </tr>
    		    <tr>
			<td width="20%" class="kback"><div class="bold"><msg:getText key="text.configure.0" />: </div></td>
			<td width="80%" class="kback">${wrkdata.txt}</td>
    		    </tr>
		    <tr>
			<td width="20%" class="kback"><div class="bold"><msg:getText key="text.configure.1" />: </div></td>
			

			<td class="kback"><c:out value="${wrkdata.graf}" default="--" />	
		    <c:choose>
			<c:when test="${(wrkdata.template=='--')||(wrkdata.template=='')}">	
				<msg:getText key="text.configure.2" />:
				
				<select name="pgraf" id="pgraf">
					<c:forEach var="tmp" items="${grafs}">
						<option id="${tmp.workflowID}">${tmp.workflowID}</option>
					</c:forEach>
				</select>
				<msg:toolTip id="ctmpdiv" tkey="portal.config.graphlist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				<lpds:submit actionID="action" actionValue="doNewGraf" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.modifygraf" tkey="true"/>
			</c:when>
			<c:otherwise>	
				-
<!--				<select name="pgraf" id="pgraf">
					<c:forEach var="tmp" items="${grafs}">
						<option id="${tmp.workflowID}">${tmp.workflowID}</option>
					</c:forEach>
				</select>
				<msg:toolTip id="ctmpdiv" tkey="portal.config.graphlist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				<lpds:submit actionID="action" actionValue="doNewGraf" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.modifygraf" tkey="true"/>
-->
			</c:otherwise>
		    </c:choose>

			</td>
			</tr>

		    <tr>
			<td width="20%" class="kback"><div class="bold"><msg:getText key="text.configure.3" />: </div></td>
			<td class="kback"><c:out value="${wrkdata.template}" default="--" />
			<c:choose>
			    <c:when test="${awkfs[0]!=null}"> 
						
				<msg:getText key="text.configure.4" />:  
				
				<select name="pawkf" id="pawkf">
					<c:forEach var="tmp" items="${awkfs}">
						<option id="${tmp.workflowID}">${tmp.workflowID}</option>
					</c:forEach>
				</select>
				<msg:toolTip id="ctmpdiv" tkey="portal.config.templatelist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				<lpds:submit actionID="action" actionValue="doNewTemplate" paramID="workflow" paramValue="${wrkdata.workflowID}" cssClass="portlet-form-button" txt="button.modifytemplate" tkey="true"/>
			    </c:when>
			</c:choose> 
				 
			</td>
		    </tr>
		</table>
	<!--	</form> -->
	    </td>	
	</tr>	    

    </table>
		    <div id="disbl" class="shape" style="z-index:100;display: none; position: absolute; width: 1280px; height: 1000px; top: 0; left: 0; color: rgb(255, 255, 255); filter: 'alpha(opacity=80)';">

			<div class="hdn_txt" style="top:200px">
			    <div id="jsmsg" ><msg:getText key="text.configure.uploadinp"/></div>
			    <div id="jsmsg0" style="display:none" ><msg:getText key="text.configure.uploadsuc"/></div>
			    <div> <msg:getText key="text.configure.uploadstat"/> <div id="statusforms" style="float:left;display:block;"></div> </div>
			    <div id="jmsgreload" style="display:none;width:490px;">
			     <input type="button" value="OK" onclick="javascript:callDoConfigure();" />
			    </div>
			</div>
		    
		    </div> 
    
<div style="position:absolute">

    <table width="100%">
        <tr class="portlet-section-body">
	    <td class="portlet-section-body">
		<table>
		<tr><td>
	        <div style="position:relative;border:1px solid; width:700px; height:300px;overflow:auto;" id="pp" >

		</div>
		
		<script>
		<!--
		    var jg= new jsGraphics('pp');
		    jg.setColor("#ff0000");
		    <c:forEach var="oneLin" items="${requestScope.lineList}">
			jg.drawLine(${oneLin.x0+10},${oneLin.y0+10},${oneLin.x1+10},${oneLin.y1+10});
		    </c:forEach>
		    <c:forEach var="oneJob" items="${requestScope.jobs}">
			jg.setColor("#fea602");
			jg.fillLinkRect(${oneJob.x}+10, ${oneJob.y}+10, 60, 60,"${oneJob.name}","${oneJob.txt}");
			jg.setColor("#92ee92");
			<c:forEach var="inputs" items="${oneJob.inputs}">
			    jg.fillInputRect(${inputs.x}+10, ${inputs.y}+10, 13, 13,"${oneJob.name}","${inputs.id}","${inputs.txt}","${inputs.seq}");
			</c:forEach>
			jg.setColor("#d2d2d2");
			<c:forEach var="inputs" items="${oneJob.outputs}">
			    jg.fillRect(${inputs.x}+10, ${inputs.y}+10, 13, 13);

			    jg.fillOutputRect(${inputs.x}+10, ${inputs.y}+10, 13, 13,"${oneJob.name}","${inputs.id}","${inputs.txt}","${inputs.seq}");

			</c:forEach>
		    </c:forEach>    
		    jg.paint();
		-->
		</script>
		</td>
		<td>
<%--  		    <div id="disbl" class="shape" style="display: none; position: absolute; left: 5px; min-height: 500px; color: rgb(255, 255, 255); top: 219px;"></div> --%>


		    <div style="height:300px;overflow:auto;">
<%--			<input type="button" onclick="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);" value="Upload Files" class="portlet-form-button"><br/> --%>
<%--			<input type="button" onclick="javascript:setSaveWorkflow('')" value="Save Workflow" class="portlet-form-button"><br/> --%>
<%--			<input type="button" onclick="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);setSaveWorkflow('radioDeleteYes,radioDeleteNo')" value="Save and Upload" class="portlet-form-button"><br/> --%>
<%-- 			<input type="button" onclick="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);setSaveWorkflow('radioDeleteYes,radioDeleteNo,confIDparam')" value="<msg:getText key="button.saveandupload" />" class="portlet-form-button"><br/> --%>
			<input type="button" onclick="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);" value="<msg:getText key="button.saveandupload" />" class="portlet-form-button"><br/> 
			
			<input id="radioDeleteYes" type="radio" name="radioDeleteInstances" value="yes"><msg:getText key="text.configure.5" /><br/>
			<input id="radioDeleteNo" type="radio" name="radioDeleteInstances" value="no" checked="true"><msg:getText key="text.configure.6" /><br/>

    			<div id="hideforms" style="display:none;"></div>
			<div id="uploadstatus" style="float:left;display:block"></div>
			<IFRAME src="" width="0" height="0" name="none" scrolling="none" frameborder="0"></IFRAME>
                        
<%--                        <br><input type="button" onclick="javascript:getConfForm('m=GetWFConfView');document.getElementById('cdata').style.display='block';" value="<msg:getText key="button.wfprop" />" class="portlet-form-button"><br/> --%>
                        <br><input type="button" onclick="javascript:getConfForm('m=GetWFConfAddNodeView');document.getElementById('cdata').style.display='block';" value="Set Nodes..." class="portlet-form-button"><br/>
		    </div>	
		</td>
		</tr>
		</table>			
		</form>
	    </td>
	    <td>
	    </td>
	</tr>	    
    </table>		    
    <div id="cdata" class="shape" style="display:none;position:absolute;left:5px;font-color:#ffffff;min-height:600px;color:#ffffff" ></div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/tooltip.js"></script>
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
 