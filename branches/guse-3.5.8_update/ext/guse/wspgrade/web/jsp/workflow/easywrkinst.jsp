<!--
Workfflow details main page
-->
<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<msg:help id="helptext" tkey="help.easywrkinst" img="${pageContext.request.contextPath}/img/help.gif" />

<div id="rwlist" style="position:relative;">
<portlet:resourceURL var="ajaxURL" />
<portlet:resourceURL var="uploadStatusURL" >
    <portlet:param name="sid" value="${confID}" />
    <portlet:param name="uploadStatus" value="" />
</portlet:resourceURL>
<script type="text/javascript">
loadingconf_txt='<msg:getText key="portal.config.loadingconfig" />';  
//var ajaxURL="${ajaxURL}";


    var ajaxURL="${ajaxURL}";
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

    var uploadURL="${uploadURL}";
    var uploadStatusURL="${uploadStatusURL}";


    function reloadPanel(pValue){
        $("#popup1").html(pValue);
        ReRunJS("popup1");
    }


function modalView(popWidth,pTXT){
    $('body').append('<div id="popup0"  class="popup_block"><div id="popup1">'+pTXT+'</div></div>');
    $('#popup0').fadeIn().css({ 'width': Number( popWidth ) }).prepend('<a href="#" class="close"><img src="${pageContext.request.contextPath}/imgs/close_pop.png" class="btn_close" title="Close Window" alt="Close" /></a>');

    var popMargTop = ($('#popup0').height() + 80) / 2;
    var popMargLeft = ($('#popup0').width() + 80) / 2;

    $('#popup0').css({
	'margin-top' : -popMargTop,
	'margin-left' : -popMargLeft,
    });
    $('body').append('<div id="fade"></div>');
    $('#fade').css({'filter' : 'alpha(opacity=80)'}).fadeIn();
		return false;
}


function closePopUp(){
	$('#fade , .popup_block').fadeOut(function() {$('#fade, a.close').remove();});
	$("#popup0").remove();
}


function toggleMenu(){

/*$("#nav ul").tabs("#contents >", {effect: 'fade', fadeOutSpeed: 400});*/

	/* This code is executed after the DOM has been completely loaded */
	/* Changing thedefault easing effect - will affect the slideUp/slideDown methods: */
	$.easing.def = "easeOutBounce";
	/* Binding a click event handler to the links: */
	$('li.button a').click(function(e){
		/* Finding the drop down list that corresponds to the current section: */
		var dropDown = $(this).parent().next();
		/* Closing all other drop down sections, except the current one */
		/*$('.dropdown').not(dropDown).slideUp('slow');*/
		dropDown.slideToggle('slow');
		/* Preventing the default event (which would be to navigate the browser to the link's address) */
		e.preventDefault();
	})

}


$(document).ready(function($){

    $('a.close, #fade').live('click', function() {
	closePopUp();
	return false;
    });
});




function getConfForm(pParam){
        TINY.box.show(loadingconf_txt,0,300,300,0);
        comParams=pParam;
//communication

	var url= ajaxURL+'&'+pParam;
	request=GetXmlHttpObject(openConf);
	request.open("POST",url, true);
	request.send("");
}

function openConf(){
try{
        if((request.readyState == 4)&&(request.status == 200)){
            var resp =  request.responseText;

	    var opts=resp.split("<!-- div id -->");
            if(opts.length==2){
    		document.getElementById(opts[0]).innerHTML=opts[1];
                ReRunJS(opts[0]);
                //viewToolTipBT("#"+opts[0]);
            }
            else{
        	if(document.getElementById("popup1")==null)
                	modalView(900,resp);
                else
            	    document.getElementById("popup1").innerHTML=resp;
                ReRunJS("popup1");
        	if(document.getElementById("popup1")!=null) viewToolTipBT("#popup1");

            }
        }
//	else {alert("A problem occurred with communicating between the XMLHttpRequest object and the server program.");}
    }
    catch (err) { alert("It does not appear that the server is available for this application. Please try again very soon.easy \nError: "+err.message);}
    TINY.box.hide();
}


//jobkonfiguracio

function getMiddlewarePanel(pParam)
	{
//kommunikacio
	var url= ajaxURL+'&m=GetMiddlewareConfigPanel&j='+pParam;
	request=GetXmlHttpObject(showMiddlewarePanel);
	request.open("POST",url, true);
	request.send("");
}

var config = {
 sensitivity: 3, // number = sensitivity threshold (must be 1 or higher)
 interval: 1000, // number = milliseconds for onMouseOver polling interval
 over: makeTall, // function = onMouseOver callback (REQUIRED)
 timeout: 500, // number = milliseconds delay before onMouseOut
 out: fakeShort // function = onMouseOut callback (REQUIRED)
};


var tmpBT=null;;

function makeTall(item){
    if(tmpBT!=null) tmpBT.btOff();
    tmpBT=item.target;
    $(item.target).btOn();
}
function makeShort(item){$(item.target).btOff();}
function fakeShort(item){}



function viewToolTipBT(pID){
    var btTip={
    	    trigger: ['none'],
    	    width: 300,
    	    animate: true,
    	    cssStyles: {width:'300px', 'max-height':'250px',overflow:'auto'},
	    showTip: function(box){$(box).fadeIn(500);},
	    hideTip: function(box, callback){$(box).animate({opacity: 0}, 500, callback);},
	    shrinkToFit: true,
    };



    $(pID+" input").each(
	function(index){
	    try{
    		$(this).hoverIntent(config);
    		$(this).bt(btTip);
    		$(this).click(function(){tmpBT.btOff();});
        }
	catch(e){alert(index+":"+e);}
    });

    $(pID+" select").each(
	function(index){
	try{
        $(this).hoverIntent(config);
        $(this).bt(btTip);
  		$(this).click(function(){tmpBT.btOff();});
    }
	catch(e){alert(index+":"+e);}

	}); /*function(index)*/

    $('.ajaxhelp').each(
	function(index){
	    var btAjaxTip={
    		trigger: ['click', 'blur'],
        	width: 300,
		animate: true,
    	        cssStyles: {width:'800px', 'max-height':'500px',overflow:'auto'},
		showTip: function(box){$(box).fadeIn(500);},
		hideTip: function(box, callback){$(box).animate({opacity: 0}, 500, callback);},
		shrinkToFit: true,
		hoverIntentOpts: {
		    interval: 0,
		    timeout: 0
		},
		ajaxPath: ["$(this).attr('href')"]
	    };
	    $(this).bt(btAjaxTip);
    });


}

function showMiddlewarePanel(){
    try
    {
        if((request.readyState == 4)&&(request.status == 200))
	{
            var resp =  request.responseText;
            if (resp != null)
	    {
            	document.getElementById("middleware-panel").innerHTML=resp;
		viewToolTipBT("#middleware-panel");
		if(navigator.appName!="Microsoft Internet Explorer")
		{
		    try{document.getElementById("middleware-panel").style.top="0px";}
		    catch (err) {}
		}
            }
        }
//	else {alert("A problem occurred with communicating between the XMLHttpRequest object and the server program.");}
    }
    catch (err) { alert("It does not appear that the server is available for this application. Please try again very soon. \nError: "+err.message);}
}

        function showMidleware()
        {
            var x=document.getElementById("job_gridtype");
	    txt=x.options[x.selectedIndex].text;
	    getMiddlewarePanel(txt);
    	    document.getElementById("middleware-panel").style.display="block";
        }


</script>

<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />
<form method="post" action="${pURL}"> 
    <input type="hidden" name="workflow" id="workflow" value="${workflow.workflowID}">
    <input type="hidden" name="guse" id="action">
    <input type="hidden" name="rtid" id="rtid">    

<div id="div_abort" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_abort_txt">Do you really suspend ${workflow.workflowID} workflow <div id="div_abort_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doAbort" cssClass="portlet-form-button" txt="button.suspend" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_abort')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_delete" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_delete_txt">Do you really delete ${workflow.workflowID} workflow <div id="div_delete_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doDeleteInstance" cssClass="portlet-form-button" txt="button.delete" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_delete')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_submit" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_submit_txt">Do you really ReSubmit ${workflow.workflowID} workflow <div id="div_submit_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doReSubmit" cssClass="portlet-form-button" txt="button.submit" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_submit')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>

<div id="div_rescue" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="div_rescue_txt">Do you really resume ${workflow.workflowID} workflow <div id="div_rescue_instance"></div> instance?<br>
    <lpds:submit actionID="action" actionValue="doRescue" paramID="workflow" paramValue="${workflow.workflowID}" cssClass="portlet-form-button" txt="button.resume" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('div_rescue')" value="<msg:getText key='button.cancel' />" />
    </div>
</div>


    <lpds:submit actionID="action" actionValue="doList" cssClass="portlet-form-button" txt="button.back" tkey="true" />		
    <%--<lpds:submit actionID="action" actionValue="doDetails" paramID="workflow" paramValue="${workflow.workflowID}" cssClass="portlet-form-button" txt="button.refreshinstances" tkey="true"/> --%>
    <input type="submit" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${workflow.workflowID}';document.getElementById('rtid').value='${workflow.einstanceID}';document.getElementById('action').value='doInstanceDetails';" value="<msg:getText key="button.refreshinstances" />" />

    <table width="100%" class="kback">
	<tr><td width="10%"><msg:getText key="text.wrkinst.0" />: </td><td>${workflow.workflowID}</td></tr>
	<tr><td width="10%"><msg:getText key="text.wrkinst.1" />: </td><td>${workflow.txt}</td></tr>
	<%--<tr><td width="10%"><msg:getText key="text.wrkinst.2" />: </td><td><c:out value="${workflow.graf}" default="--" /></td></tr>
	<tr><td width="10%"><msg:getText key="text.wrkinst.3" />: </td><td><c:out value="${workflow.template}" default="--" /></td></tr> --%>
    </table>
    
    <table width="100%" class="kback">
	<c:forEach var="onewrkinst" items="${workflow.allRuntimeInstance}" varStatus="ln">
	<c:if test="${onewrkinst.value.text=='einstance'}">

 	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
	    	    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
	    	    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>
	    
    	    <tr>
		<td width="110" class="${color}">
			    <%-- ${onewrkinst.value.text} --%>
			Workflow status: 
		</td>
		<td width="110" class="<msg:getText key="portal.WorkflowData.status.${onewrkinst.value.status}" />"><msg:getText key="portal.WorkflowData.status.${onewrkinst.value.status}" /></td>
		<td class="${color}">
			 
<%--
<!--
    		    <lpds:submit actionID="action" actionValue="doInstanceDetails" paramID="rtid" paramValue="${onewrkinst.key}" cssClass="portlet-form-button" txt="button.shortdetails" tkey="true"/>
-->
		    <input type="submit" class="portlet-form-button" onclick="javascript:document.getElementById('workflow').value='${workflow.workflowID}';document.getElementById('rtid').value='${onewrkinst.key}';document.getElementById('action').value='doInstanceDetails';" value="<msg:getText key="button.shortdetails" />" />
		    
		    <c:if test="${onewrkinst.value.status=='5'||onewrkinst.value.status=='2'||onewrkinst.value.status=='1'||onewrkinst.value.status=='23'}">
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_abort_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_abort','${workflow.workflowID}',event)" value="<msg:getText key="button.suspend" />" />		
		    </c:if>
		    <c:if test="${onewrkinst.value.status=='7'||onewrkinst.value.status=='22'}">
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_rescue_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_rescue','${workflow.workflowID}',event)" value="<msg:getText key="button.resume" />" />		
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_delete','${workflow.workflowID}',event)" value="<msg:getText key="button.delete" />" />		
		    </c:if>
		    <c:if test="${onewrkinst.value.status=='6'}">
			<input type="button" class="portlet-form-button" onclick="javascript:document.getElementById('div_delete_instance').innerHTML='${onewrkinst.value.text}';document.getElementById('rtid').value='${onewrkinst.key}';hide('div_delete','${workflow.workflowID}',event)" value="<msg:getText key="button.delete" />" />		
<!--
    		    	<form method="post" action="${storageurl}/download" name="downform_${onewrkinst.key}" target="formout">
			    <input type="hidden" name="portalID"      value="${portalID}">
			    <input type="hidden" name="userID"        value="${userID}">
			    <input type="hidden" name="workflowID"    value="${workflow.workflowID}">
			    <input type="hidden" name="outputLogType" value="none">
			    <input type="hidden" name="downloadType"  value="outputs_${onewrkinst.key}">
			    <input type="submit" value="<msg:getText key="button.outputs" />" class="portlet-form-button">
			</form>
-->
		    </c:if>
		    <lpds:submit actionID="action" actionValue="doWkfvisualizer" paramID="rtid" paramValue="${onewrkinst.key}" cssClass="portlet-form-button" txt="button.visualize" tkey="true"/>
		--%>
		</td> 
    	    </tr>
	</c:if>
	</c:forEach>    
    </table>
</form>
<table class="kback">
    	    <tr>
		<td width="150">Status</td>
		<td width="150">Instances</td>
    	    </tr>
    	    <tr>
		<td width="150" class="init">init</td>
		<td width="150" >${sinit}</td>
    	    </tr>
	    <tr>
		<td width="150" class="running">running:</td>
		<td width="150" >${sproc}</td>
    	    </tr>
	    <tr>
		<td width="150" class="finished">done:</td>
		<td width="150" >${sfinalok}</td>
    	    </tr>
	    <tr>
		<td width="150" class="error">error:</td>
		<td width="150" >${sfinalerr}</td>
    	    </tr>            
	    <tr>
		<td width="150" class="init">sum:</td>
		<td width="150" >${ssum}</td>
    	    </tr>
</table>

<br/>

<table width="100%" >
    <tr>
        <td><b>Estimation of accumulated progress: ( ${sfinalok+sfinalerr} / ${ssum} )</b></td><%--<msg:getText key="portal.easywrk.statusbar" />--%>
    </tr>
</table>    
<table width="100%" class="kback" style="border:1px solid black;">
    <tr>        
	<c:if test="${psfinalerr!='0'}"><td width="${psfinalerr}%" height="20" class="error"></td></c:if> 
        <c:if test="${psfinalok!='0'}"><td width="${psfinalok}%" height="20" class="finished"></td></c:if>
        <c:if test="${psproc!='0'}"><td width="${psproc}%" height="20" class="running"></td></c:if>
	<c:if test="${psinit!='0'}"><td width="${psinit}%" height="20" class="init"></td></c:if>                
    </tr>
</table>

<br/>

<c:if test="${sfinalerr!='0'}">
    <table width="100%" class="kback">
        <tr>
	    
	    <td width="100%" colspan="4" style="border-bottom:solid 1px #ffffff;">
		<b><msg:getText key="portal.easywrk.insterror" /></b></td>
	</tr>	    
                     <!--<input type="button" class="portlet-form-button" id="jobbutton_${ajob.key}7" value="View erors" onclick="javascript:hidejobstatus('${ajob.key}','7')">-->

        <tr>
	    <td width="20%"><msg:getText key="text.wrkinst.5" /></td>
	    <td width="10%"><msg:getText key="text.wrkinst.6" /></td>
	    <td width="20%"><msg:getText key="text.wrkinst.7" /></td>
	   <!-- <td width="50%">[ <msg:getText key="text.wrkinst.8" /> ]</td>	-->
	</tr>	    
	<c:forEach var="ajob" items="${instJobList}" varStatus="ln">
	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
	    	    <c:set var="color" value="kline1" />
		</c:when>
		<c:otherwise>
	    	    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>

		<tr>
		    <td class="${color}"> ${ajob.key} </td>
		    <td colspan="2"  class="${color}">
			<table width="100%" class="kback">
			<c:forEach var="bjob" items="${ajob.value}">
                            <c:if test="${bjob.key=='7'}">
                                <tr>    
                                    <td class="<msg:getText key="portal.WorkflowData.status.${bjob.key}" />" width="25%"><msg:getText key="portal.WorkflowData.status.${bjob.key}" /></td>
                                    <td class="${color}" width="25%">${bjob.value}</td>
                                    <td class="${color}" width="50%"><input type="button" class="portlet-form-button" id="jobbutton_${ajob.key}${bjob.key}" value="View <msg:getText key="portal.WorkflowData.status.${bjob.key}" />" onclick="javascript:hidejobstatus('${ajob.key}','${bjob.key}')"></td>
                                </tr>
                            </c:if>
			</c:forEach>
			</table>
		    </td>
		    <td class="${color}">
                        <input type="hidden" class="portlet-form-button" id="jobbutton_${ajob.key}all" value="View all content(s)" onclick="javascript:hidejobstatus('${ajob.key}','all')">
                    </td>
    		</tr>
		<tr>
		    <td colspan="4">
			<div id="jobinsttatus_${ajob.key}"></div>
		    </td>
		</tr>
		<tr>
		    <td colspan="4">
			<div id="jobinsttatus0_${ajob.key}" style="display:none;background:#ffffff; border-left:#3e7abd solid 100px;">
			    <iframe name="formout_${ajob.key}" id="formout_${ajob.key}" scrolling="auto" frameborder="0" width="100%" height="auto"></iframe><br>
			    <input type="button" class="portlet-form-button" value="<msg:getText key="button.close" />" onclick="document.getElementById('jobinsttatus0_${ajob.key}').style.display='none'">
			</div>
		    </td>
		</tr>
	</c:forEach>    
    </table>
</c:if>

                </div>
