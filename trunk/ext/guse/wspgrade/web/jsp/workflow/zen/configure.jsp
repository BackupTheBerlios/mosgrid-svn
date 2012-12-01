  
<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<portlet:actionURL var="uploadURL" />
<portlet:resourceURL var="uploadStatusURL" >
    <portlet:param name="sid" value="${confID}" />
    <portlet:param name="uploadStatus" value="" />
</portlet:resourceURL>
<%--
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="http://cherne.net/brian/resources/jquery.hoverIntent.js"></script>

<script type="text/javascript" src="http://www.lullabot.com/files/bt/bt-latest/jquery.bt.min.js" charset="utf-8"></script>
<script type='text/javascript' src='http://www.wizzud.com/jqDock/jquery.jqDock.min.js'></script>
<script type='text/javascript' src='http://interface.eyecon.ro/interface/interface.js'></script>
<script src="http://malsup.github.com/jquery.form.js"></script>

--%>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery-1.3.2.min.js?<%=Math.random()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.hoverIntent.js?<%=Math.random()%>"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.bt.min.js?<%=Math.random()%>" charset="utf-8"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.jqDock.min.js?<%=Math.random()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/interface.js?<%=Math.random()%>"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery/jquery.form.js?<%=Math.random()%>"></script>


<style>

/*---------------------shadow-----------------*/



.shadow{
    border:#000000 solid 1px;
    -moz-box-shadow: 10px 10px 5px #888;
    -webkit-box-shadow: 10px 10px 5px #888;
    box-shadow: 10px 10px 5px #888;
 }
 
.shadownoborder, .jobheader, li.button{
     -moz-box-shadow: 10px 10px 5px #888;
    -webkit-box-shadow: 10px 10px 5px #888;
    box-shadow: 10px 10px 5px #888;
 }

.shadowR {
    border-right:#000000 solid 1px;
    -moz-box-shadow: 10px 0px 5px #888;
    -webkit-box-shadow: 10px 0px 5px #888;
    box-shadow: 10px 0px 5px #888;
 }


.borderradius, .jobheader, .dropdown, li.button, li.button a{
    -webkit-border-radius: 10px;
    -moz-border-radius: 10px;
    border-radius: 10px;
}

.borderleftradius, .jobconfig-header{
    -webkit-border-top-left-radius: 10px;
    -webkit-border-bottom-left-radius: 10px;
    -moz-border-radius-topleft: 10px;
    -moz-border-radius-bottomleft: 10px;
    border-top-left-radius: 10px;
    border-bottom-left-radius: 10px;
}

.borderrightradius, .jobconfig-data, .ioconfig-data{
    -webkit-border-top-right-radius: 10px;
    -webkit-border-bottom-right-radius: 10px;
    -moz-border-radius-topright: 10px;
    -moz-border-radius-bottomright: 10px;
    border-top-right-radius: 10px;
    border-bottom-right-radius: 10px;
}

.config-color0, .jobconfig-header{
    background-color:#138F6A;
    color:black;
}
.config-color1, .jobconfig-data, .ioconfig-data{
    background-color:#32cc99;
    color:black;
}

.jobheader, .jobconfigtable{
    display:table;
    float:left;
    width:900px;
}

.ioconfigtable{
    dispaly:table;
    float:left;
    width:700px;
    backgroud-color:red;
}


.jobheader{
    margin-bottom:20px;
}

.jobconfig-header{
    padding: 1px 0px 1px 10px;
    margin:1px 1px 0 0;
    display:table-cell;
    width:270px;
    font-style:normal;
    font-weight:bold;}

.jobconfig-data{
    padding: 1px 0px 1px 10px;
    color:black;
    display:table-cell;
    width:609px;
    margin:1px 0 0 0;
    padding-left:10px;
    font-style:normal;
}

.ioconfig-data{
    padding: 1px 0px 1px 10px;
    color:black;
    display:table-cell;
    width:409px;
    margin:1px 0 0 0;
    padding-left:10px;
    font-style:normal;
}


.config-gradient{
  background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#00FFAA), to(#138F6A));  
  background: -webkit-linear-gradient(top, #32cc99, #138F6A);
  background: -moz-linear-gradient(top, #32cc99, #138F6A);
  background: -ms-linear-gradient(top, #32cc99, #138F6A);
  background: -o-linear-gradient(top, #32cc99, #138F6A);
}

.config-gradient-invert{
  background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#138F6A), to(#32cc99));
  background: -webkit-linear-gradient(top, #138F6A, #32cc99);
  background: -moz-linear-gradient(top, #138F6A, #32cc99);
  background: -ms-linear-gradient(top, #138F6A, #32cc99);
  background: -o-linear-gradient(top, #138F6A, #32cc99);
}

#nav li a{
    clear:inherited;
    text-decoration:none;
    color:#ffffff;
    margin-bottom:2px;
}
#nav li {
    clear:inherited;
    dispay:block;
    list-style-type:none;
    float:left;
    padding:0 5px;
    text-align:center;
    max-height:122px;
    owerflow:auto;
    margin-left:5px;
}

.config-gradient:hover{
    cursor:pointer;
    color:#ffffff;
    font-style:bolder;
    text-decoration:none;
    background: -webkit-gradient(linear, 0% 0%, 0% 100%, from(#00688B), to(#517693));   /* Safari 4-5, Chrome 1-9 */
    background: -webkit-linear-gradient(top, #00688B, #517693);  /* Safari 5.1, Chrome 10+ */
    background: -moz-linear-gradient(top, #00688B, #517693); /* Firefox 3.6+ */
    background: -ms-linear-gradient(top, #00688B, #517693); /* IE 10 */
    background: -o-linear-gradient(top, #517693, #00688B); /* Opera 11.10+ */
}

#nav strong {
    display:block;
    font-size:13px;
}

#configcontents{
    width:100%;
    display:block;
    float:left;
}



/*dropdown menu*/


ul.container{/* The topmost UL */
    width:800px;
    margin:0 auto;
    padding:5px 0 20px 0;
}

li{
    list-style:none;
    text-align:left;
}

li.menu{/* The main list elements */
    padding:5px 0;
    width:100%;
}
li.button{clear:inherited}

li.button a{/* The section titles */
    display:block;
    font-family:BPreplay,Arial,Helvetica,sans-serif;
    font-size:10px;
    overflow:hidden;
    padding:2px 10px 2px 10px;
}

/* The hover effects */

li.button a.input{padding-left:5px;text-decoration:none;background-color:#92ee92; color:#074384;}
li.button a.output{padding-left:5px;text-decoration:none;background-color:#d2d2d2; color:#436800;}
li.button a:hover{padding:5px 55px;text-shadow: #666666 5px 5px 5px;}


.dropdown{
    margin:10px 0 30px 30px;
    border-left:#000000 solid 1px;
    border-top:#000000 solid 1px;
    -moz-box-shadow: 10px 10px 5px #888;
    -webkit-box-shadow: 10px 10px 5px #888;
    box-shadow: 10px 10px 5px #888;
    display:none;
    width:730px;
}



.dropdown li{/* Each element in the expandable list */
}


/* The styles below are only necessary for the demo page */


.clear{
clear:both;
}


</style>




<script type='text/javascript'>

function additem(pID,pNID,pJobID,pFile){
    try{
        if(document.getElementById(pFile).value!=""){
            $('form[name="form_'+pFile+'"]').each(function(item){ alert($(this).attr("name"));$(this).remove();});
            if(document.getElementById(pFile).name.substring(0,5)!="file_"){
                    formid=formid+1;
                var sid2 = getSID()+confID;
                    tmp=document.createElement("form");
                tmp.setAttribute("enctype","multipart/form-data");
                tmp.setAttribute("encoding","multipart/form-data");
                tmp.setAttribute("method","post");
                    tmp.setAttribute("id","form"+formid);
                tmp.setAttribute("name","form_"+pFile);
                tmp.setAttribute("action",uploadURL);
                    tmp.setAttribute("target","none");
                    for(var ti=1;ti<formid;ti++){
                        if(document.getElementById("form"+ti)!=null){
                        if(jsIdCheck("form"+ti,pFile)){
                            try{document.getElementById("hideforms").removeChild(document.getElementById("form"+ti));}
                            catch(ex){jsSubmitting("form"+ti);}
                        }
                    }
                }
                tmp0=document.getElementById(pFile);
                tmp0.setAttribute("name","file_"+sid2+"_"+get_random());
                tmp.appendChild(tmp0);
                tmp0.setAttribute("id","formfile_"+tmp0.getAttribute("id"));

                tmp.appendChild(createInputElement("hidden","sfile",pNID));
        	    tmp.appendChild(createInputElement("hidden","confID",confID));
                tmp.appendChild(createInputElement("hidden","portalID",portalID));
        	    tmp.appendChild(createInputElement("hidden","userID",userID));
                tmp.appendChild(createInputElement("hidden","workflowID",workflowID));
        	    tmp.appendChild(createInputElement("hidden","jobID",pJobID));

                document.getElementById("hideforms").appendChild(tmp);
        	}
        }
    } catch (ex) {}
}


function fishEyeMenu(){
  var dockOptionsInput =
    { align: 'top' // horizontal menu, with expansion DOWN from a fixed TOP edge
    , size: 60 //increase 'at rest' size to 60px
    , labels: true  // add labels (defaults to 'br')
    , setLabel: function(txt){ //set colours...
        return "<span class='is" + txt + "'>" + txt + '</span>';
      }
    };
  $('.menuInput').jqDock(dockOptionsInput);	
	
	var dockOptions =
			{ align: 'top' // horizontal menu, with expansion DOWN from a fixed TOP edge
			, size: 60 //increase 'at rest' size to 60px
			, setLabel: function(t, i, el){	//NB : el is div.jqDockLabel
					t = t.split('|');
					$('<div class="myLabel"><h4>' + t[0] + '</h4><p>' + t[1] + '</p></div>')
						.appendTo($(el).show());
					//I've set the text content myself so...
					return false;
				}
			};

	$('.menu').jqDock(dockOptions);
	$('.menu a').click(function(){
	    var url;
	    $(this).parent().parent().parent().children().find('img').each(function(item){    
		url=$(this).attr("src");
		if(url.substr(-13) === ".selected.png"){
		    $(this).attr("src",url.substring(0,url.length-13));
		}
	    });
	    url=$(this).children("img").attr("src")+".selected.png";
	    $(this).children("img").attr("src",url);
	});

 var dockOptionsInput =
    { align: 'top' // horizontal menu, with expansion DOWN from a fixed TOP edge
    , size: 60 //increase 'at rest' size to 60px
    , labels: true  // add labels (defaults to 'br')
    , setLabel: function(txt){ //set colours...
        return "<span class='is" + txt + "'>" + txt + '</span>';
      }
    };

	$('.menuInput').jqDock(dockOptionsInput);
	$('.menuInput a').click(function(){
	    var url;
	    $(this).parent().parent().parent().children().find('img').each(function(item){    
		url=$(this).attr("src");
		if(url.substr(-13) === ".selected.png"){
		    $(this).attr("src",url.substring(0,url.length-13));
		}
	    });
	    url=$(this).children("img").attr("src")+".selected.png";
	    $(this).children("img").attr("src",url);
	});	
}

</script>





<portlet:resourceURL var="ajaxURL" />
<script>
<!-- 
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

//-->
function jobtypepanelhide(pview){
//    alert(pview+":"+$("#"+pview).html());
    try{document.getElementById('jobisworkflow').style.display='none';}catch(e){}
    try{document.getElementById('jobisbinary').style.display='none';}catch(e){}
    try{document.getElementById('jobisservice').style.display='none';}catch(e){}
    try{document.getElementById('jobiscloud').style.display='none';}catch(e){}
    document.getElementById(pview).style.display='table';


//    $('configmenu_'+pview.substring(5)).style.setProperty("background-color", "red", "important");

}

function inputtypepanelhide(pID,pType){
    try{document.getElementById('jobinputtype_'+pID+'_upload').style.display='none';}catch(e){}
    try{document.getElementById('jobinputtype_'+pID+'_remote').style.display='none';}catch(e){}
    try{document.getElementById('jobinputtype_'+pID+'_constans').style.display='none';}catch(e){}
    try{document.getElementById('jobinputtype_'+pID+'_sql').style.display='none';}catch(e){}
    try{document.getElementById('jobinputtype_'+pID+'_cgrid').style.display='none';}catch(e){}
    document.getElementById('jobinputtype_'+pID+'_'+pType).style.display='block';
}


function hideshowMBT(pValue,pdiv){
    if(pValue.checked) pdiv.style="display:block";
    else pdiv.style="display:none";
}

</script>



<portlet:defineObjects/>
<portlet:actionURL var="pURL" portletMode="VIEW" />

<%-- back gomb--%>
<portlet:actionURL var="mgURL">
    <portlet:param name="guse" value="doList" />
</portlet:actionURL>
<table width="100%">
<tr>
    <td  width="25%">
	<form id="backForm" method="post" action="${mgURL}">
	<input type="image" src="${pageContext.request.contextPath}/imgs/back_65.png" value="<msg:getText key="button.back" />"/>
	</form>
    </td>
    <td align="center" width="50%">
    <div class="shadow" style="background-color:#cd2626">
        <table>
	<tr>
	<td style="float:left" align="left">
	    <input id="radioDeleteYes" type="radio" name="radioDeleteInstances" value="yes"><msg:getText key="text.configure.5" /><br/>
	    <input id="radioDeleteNo" type="radio" name="radioDeleteInstances" value="no" checked="true"><msg:getText key="text.configure.6" /><br/>
	</td>
	<td >
	    <div style="padding-left:20px">
	    <a href="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);"><img src="${pageContext.request.contextPath}/imgs/save.png" /></a>
<%--
            <input type="button" onclick="javascript:document.getElementById('disbl').style.display='block';submitallforms(sForm);" value="<msg:getText key="button.saveandupload" />">
--%>
	    <div id="hideforms" style="display:none;"></div>
	    <div id="uploadstatus" style="float:left;display:block"></div>
	    <IFRAME src="" width="0" height="0" name="none" scrolling="none" frameborder="0"></IFRAME>
	    </div>
	</td>
	</tr>
	</table>
    </div>
    </td>

    <td width="25%">
	<div style="display:block;float:right">
	<msg:help id="helptext" tkey="help.configure" img="${pageContext.request.contextPath}/img/help.gif" />
	</div>
    </td>
<tr>
</table>


<br /> <br />

<script>
popUP_OK="<msg:getText key="text.io.14.yes" />";
popUP_NO="<msg:getText key="text.io.14.no" />";
mg_txt='<msg:getText key="portal.config.cgraf" />';
loadingconf_txt='<msg:getText key="portal.config.loadingconfig" />';
saveingconf_txt='<msg:getText key="portal.config.saveingconfig" />';
</script>

<form id="confform" method="post" action="${pURL}">
    <input type="hidden" name="job" id="job">
    <input type="hidden" name="workflow" id="workflow" value="${wrkdata.workflowID}" >
    <input type="hidden" name="guse" id="action">
    <input type="hidden" name="confIDparam" id="confIDparam" value="${confID}" >
</form>
    
<%--workflow fejlec,graph&template csere, vegleges mentes --%>
    <table width="100%" class="darktable shadow">
	<tr>
            <td class="jobconfig-header"><div class="bold"><msg:getText key="text.configure.workflowname"/> </div></td>
            <td class="jobconfig-data"><c:out value="${wrkdata.workflowID}" escapeXml="true"  /></td>
	</tr>
        <tr><td colspan="2" class="iodline"></tr>

    	<tr>
            <td  class="jobconfig-header"><div class="bold"><msg:getText key="text.configure.0" />: </div></td>
            <td  class="jobconfig-data"><c:out value="${wrkdata.txt}" escapeXml="true"  /></td>
	    </tr>
        <tr><td colspan="2" class="iodline"></tr>
        <tr>
            <td  class="jobconfig-header"><div class="bold"><msg:getText key="text.configure.1" />: </div></td>
            <td class="jobconfig-data"><c:out value="${wrkdata.graf}" default="--" escapeXml="true" />

            <c:choose>
                <c:when test="${(wrkdata.template=='--')||(wrkdata.template=='')||(wrkdata.template==null)}">
                    <portlet:resourceURL var="editgraphURL" id="editgraphURL">
                        <portlet:param name="wfId" value="${wrkdata.graf}" />
                    </portlet:resourceURL>
                    <portlet:resourceURL var="refreshConfigURL" id="refreshConfigURL">
                        <portlet:param name="wfId" value="${wrkdata.graf}" />
                    </portlet:resourceURL>
                    <form method="post" action="${editgraphURL}">
                        <input type="submit" value="<msg:getText key="button.editgraf" />">
                        <input type="button" onclick="TINY.box.show('${refreshConfigURL}',1,0,0,1);" value="<msg:getText key="button.refressh" />">
                        <msg:toolTip id="ctmpdiv" tkey="portal.config.graphlist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                    </form>
                </c:when>
                <c:otherwise><c:out value="${wrkdata.graf}" default="--" escapeXml="true" /></c:otherwise>
            </c:choose>
            </td>
        </tr>
        <tr><td colspan="2" class="iodline"></tr>

        <tr>
            <td class="jobconfig-header"><div class="bold"><msg:getText key="text.configure.3" />: </div></td>
            <td class="jobconfig-data"><c:out value="${wrkdata.template}" default="--" escapeXml="true" /></td>
        </tr>
        <tr><td colspan="2" class="iodline"></tr>
    </table>


<%--uploads files window --%>
    <div id="disbl" class="shape" style="z-index:100;display: none; position: absolute; width: 100%; height: 150%; top: 0; left: 0; color: rgb(255, 255, 255); filter: 'alpha(opacity=80)';">
        <div class="hdn_txt" style="top:200px">
            <div id="jsmsg" ><msg:getText key="text.configure.uploadinp"/></div>
            <div id="jsmsg0" style="display:none" ><msg:getText key="text.configure.uploadsuc"/></div>
            <div> <msg:getText key="text.configure.uploadstat"/> <div id="statusforms" style="float:left;display:block;"></div> </div>
            <div id="jmsgreload" style="display:none;width:490px;">
             <input type="button" value="OK" onclick="javascript:callDoConfigure();" />
            </div>
        </div>
    </div> 

<%-- graph window --%>
<c:set var="jobX" value="500" />
<c:set var="jobY" value="200" />
<c:forEach var="oneJob" items="${requestScope.jobs}">
    <c:if test="${oneJob.x>jobX}"><c:set var="jobX" value="${oneJob.x}" /> </c:if>
    <c:if test="${oneJob.y>jobY}"><c:set var="jobY" value="${oneJob.y}" /></c:if>
</c:forEach>
<br /> <br />
<!--[if IE]><script type="text/javascript" src="/wspgrade/js/excanvas.js"></script><![endif]-->
<center>
	<canvas class="shadow" id="myDrawing" width="${jobX+100}px" height="${jobY+100}px" >
	    <p>Your browser doesn't support canvas.</p>
	</canvas>
</center>


<style type="text/css">
/*label styling; push the label off the bottom of the image...*/
div.jqDockLabelText {clear:both;position:absolute; top:70px; right:4px; 
font-weight:bold; font-style:italic; white-space:nowrap; cursor:pointer;font-size:15px;}
.page {padding-top:0px; padding-bottom:20px; width:100%;}

.menu, .menuInput {top:0; left:0; width:100%; display:none;}
.menu div.jqDockWrap, .menuInput div.jqDockWrap {margin:0 auto;}
.menu div.jqDock,.menuInput div.jqDock {cursor:pointer;}
<%--
.menu a,.menuInput a {background-color: #ffffff; }
.menu a:hover {background-color:#ff9900;}
--%>
div.jqDockLabel {
	    font-weight:bold;
	    text-align:center;
	    color:#000000; 
	    cursor:pointer; 
	    top:0; left:0; 
	    width:100%; 
	    height:100%; 
	    overflow:hidden;
}
.myLabel {position:relative;}
.myLabel h4 {font-size:1em; line-height:1.25em; font-weight:bold;margin-top:10px; text-align:center; text-decoration:none;}
.myLabel p {display:none; font-size:9px; padding:0 5px;}
a:hover .myLabel p {display:block;}




    img {border: none;}
    a {color: #0052af;}

/*------------------POPUPS------------------------*/
#fade {
	display: none;
	background: #000; 
	position: fixed; left: 0; top: 0; 
	z-index: 10;
	width: 100%; height: 100%;
	opacity: .80;
	z-index: 999;
}
.popup_block{
	min-height:600px;
	display: none;
	background: #fff;
	color: #000;
	padding: 20px; 	
	border: 20px solid #ddd;
	float: left;
	font-size: 1.2em;
	position: fixed;
	top: 50%; left: 50%;
	z-index: 1999;
	-webkit-box-shadow: 0px 0px 20px #000;
	-moz-box-shadow: 0px 0px 20px #000;
	box-shadow: 0px 0px 20px #000;
	-webkit-border-radius: 10px;
	-moz-border-radius: 10px;
	border-radius: 10px;
}
img.btn_close {
	float: right; 
	margin: -55px -55px 0 0;
}

/*--Making IE6 Understand Fixed Positioning--*/
*html #fade {
	position: absolute;
}
*html .popup_block {
	position: absolute;
}


</style>
</head>

<script type="text/javascript">

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
//kommunikacio

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
                viewToolTipBT("#"+opts[0]);
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
    catch (err) { alert("It does not appear that the server is available for this application. Please try again very soon. \nError: "+err.message);}
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


<script>
<!--
var jobs=new Array();
var jobIndex=0;
var selectedJob=null;

var jobSize=5;
var portSize=15;
var fontSize=10;

var drawingCanvas = document.getElementById('myDrawing');
if(drawingCanvas.getContext) var context = drawingCanvas.getContext('2d');
context.globalCompositeOperation = "source-over";

drawingCanvas.addEventListener("mousedown", mouseDown, false);
drawingCanvas.addEventListener("mouseup", mouseUp, false);
//drawingCanvas.addEventListener("mouseout", mouseUp, false);
drawingCanvas.addEventListener("mousemove", mouseMove, false);
function mouseUp(e){
    if(selectedJob!=null){
	getConfForm("m=GetJobView&job="+selectedJob.name);
    }
}
function mouseMove(e){}


function mouseDown(e){
	panel=document.getElementById('myDrawing');
	xx=0;
	yy=0;
	while(panel!=null){
	    xx=xx+panel.offsetLeft;
	    yy=yy+panel.offsetTop;
//	    alert("("+e.clientX+","+e.clientY+")"+panel+"--"+xx+"/"+yy+":"+panel.pageYOffset);
	    panel=panel.offsetParent;
	}
    x=(e.clientX-xx+window.pageXOffset);
    y=(e.clientY-yy+window.pageYOffset);
    moveJob=isJobPlace(x,y);
    if(selectedJob!=null){
	fillLinkRect("#fea602",selectedJob.x, selectedJob.y, selectedJob.size*portSize, selectedJob.size*portSize,selectedJob.name,selectedJob.txt);
    }
    selectedJob=null
    
    if(moveJob!=""){
	selectedJob=jobs[moveJob];
	fillLinkRect("#ff0000",selectedJob.x, selectedJob.y, selectedJob.size*portSize, selectedJob.size*portSize,selectedJob.name,selectedJob.txt);
    }

}

function isJobPlace(x,y){
    p=2;
    for(item in jobs){
	job=jobs[item];
	jobX=job.x;
    	jobY=job.y;
//	if(x>(job.x-(job.size-p)*portSize) && x<(job.x+(job.size+p)*portSize) && y>(job.y-(job.size-p)*portSize) && y<(job.y+(job.size+p)*portSize)) 
	if(x>=job.x && x<=(job.x+(job.size*portSize)) && y>=job.y && y<=(job.y+(job.size*portSize)))
	    return item;
    }
    return "";
}    



function Job(pName,pTxt,pX,pY,pSize){
    this.x = pX;
    this.y = pY;
    this.name=pName;
    this.txt=pTxt;
    this.size=pSize;
    this.inputs=new Array();
    this.outputs=new Array();
    fillLinkRect("#fea602",pX, pY, this.size*portSize, this.size*portSize,pName,pTxt);
}

function Input(pName,pTxt,pSeq,pX,pY,pId,pChJob,pChOutput){
    this.id=pId;
    this.seq=pSeq;
    this.txt=pTxt;
    this.name=pName;
    this.x=pX;
    this.y=pY;
    this.preJob=pChJob;
    this.preOutput=pChOutput;
    fillLinkRect("#92ee92",pX, pY,portSize-2,portSize-2,pSeq,"");
}

function Output(pName,pTxt,pSeq,pX,pY,pId){
    this.id=pId;
    this.seq=pSeq;
    this.txt=pTxt;
    this.name=pName;
    this.x=pX;
    this.y=pY;
    fillLinkRect("#d2d2d2",pX, pY,portSize-2,portSize-2,pSeq,"");
}

Job.prototype.addInput = function(pName,pTxt,pSeq,pX,pY,pId,pChJob,pChOutput){
    this.inputs[this.inputs.length]= new Input(pName,pTxt,pSeq,pX,pY,pId,pChJob,pChOutput);
}

Job.prototype.addOutput = function(pName,pTxt,pSeq,pX,pY,pId){
    this.outputs[this.outputs.length]= new Output(pName,pTxt,pSeq,pX,pY,pId);
}

    context.fillStyle = "#8ED6FF";
function shadowOff(){
    context.shadowColor = "#aaaaaa";
    context.shadowBlur =0;
    context.shadowOffsetX = 0;
    context.shadowOffsetY = 0;

}
    context.shadowColor = "#aaaaaa";
    context.shadowBlur = 20;
    context.shadowOffsetX = 5;
    context.shadowOffsetY = 5;

    context.font = "10px Calibri";
    context.textAlign = "center";
    context.textBaseline = "middle";



function drawLine(fromx, fromy, tox, toy){
    
    if(tox>fromx) fromx=fromx+10;
    else tox=tox+10;

    if(toy<fromy) toy=toy+10;
    else fromy=fromy+10;

    var div0=6;
    var div1=15;
    var headlen = 10;	// length of head in pixels
    var dx = tox-fromx;
    var dy = toy-fromy;
    var angle = Math.atan2(dy,dx);
//    context.beginPath();
    context.moveTo(fromx, fromy);
    context.lineTo(tox-headlen*Math.cos(angle-Math.PI/div1),toy-headlen*Math.sin(angle-Math.PI/div1));
    context.lineTo(tox-headlen*Math.cos(angle-Math.PI/div0),toy-headlen*Math.sin(angle-Math.PI/div0));
    context.lineTo(tox, toy);
    context.lineTo(tox-headlen*Math.cos(angle+Math.PI/div0),toy-headlen*Math.sin(angle+Math.PI/div0));
    context.lineTo(tox-headlen*Math.cos(angle+Math.PI/div1),toy-headlen*Math.sin(angle+Math.PI/div1));

    context.lineTo(fromx, fromy);
//    context.closePath();
//    context.fill();

}

function fillLinkRect(pColor,px,py,x,y,pname,ptxt){

        context.fillStyle = pColor;
	context.fillRect(px,py,x,y);
        context.fillStyle = "#222222";
    	context.fillText(" "+pname+" ",px+x/2-2,py+y/2+2);
}
    context.beginPath();
    
	<c:forEach var="oneLin" items="${requestScope.lineList}">
    	    drawLine(${oneLin.x1+10},${oneLin.y1+10},${oneLin.x0+10},${oneLin.y0+10});
	</c:forEach>

	<c:forEach var="oneJob" items="${requestScope.jobs}">
		jobs[jobIndex]=new Job("${oneJob.name}","${oneJob.txt}",${oneJob.x}+10, ${oneJob.y}+10,4);
		<c:forEach var="inputs" items="${oneJob.inputs}">
        	    jobs[jobIndex].addInput("${oneJob.name}","${inputs.txt}","${inputs.seq}",${inputs.x}+10, ${inputs.y}+10,"${inputs.id}","","");
    		</c:forEach>
		<c:forEach var="output" items="${oneJob.outputs}">
        	    jobs[jobIndex].addOutput("${oneJob.name}","${output.txt}","${output.seq}",${output.x}+10, ${output.y}+10,"${output.id}");
		</c:forEach>
		jobIndex++;
	</c:forEach>    
    context.closePath();
//    context.stroke();
    context.fill();
    shadowOff();
-->

function inputEnabled(p,i){
//    alert(""+p+":"+i);
    if(i==0){

	document.getElementById('input_'+p+'_remote').value='';
        document.getElementById('input_'+p+'_value').value='';
	document.getElementById('input_'+p+'_sqlurl').value='';
        document.getElementById('input_'+p+'_sqlselect').value='';
	document.getElementById('input_'+p+'_sqluser').value='';
        document.getElementById('input_'+p+'_sqlpass').value='';
	
    }

    if(i==1){
        document.getElementById('input_'+p+'_value').value='';
	document.getElementById('input_'+p+'_sqlurl').value='';
        document.getElementById('input_'+p+'_sqlselect').value='';
	document.getElementById('input_'+p+'_sqluser').value='';
        document.getElementById('input_'+p+'_sqlpass').value='';
	if (document.getElementById('input_'+p+'_gout')!=null){//gemlca remote default input
		document.getElementById('input_'+p+'_remote').disabled=true;
		document.getElementById('input_'+p+'_remote').value='Default';
	}
    }

    if(i==2){
	document.getElementById('input_'+p+'_remote').value='';
	document.getElementById('input_'+p+'_sqlurl').value='';
        document.getElementById('input_'+p+'_sqlselect').value='';
	document.getElementById('input_'+p+'_sqluser').value='';
        document.getElementById('input_'+p+'_sqlpass').value='';
    }

    if(i==3){
	document.getElementById('input_'+p+'_remote').value='';
        document.getElementById('input_'+p+'_value').value='';
    }

    if(i==4){
	document.getElementById('input_'+p+'_remote').value='';
        document.getElementById('input_'+p+'_value').value='';
	document.getElementById('input_'+p+'_sqlurl').value='';
        document.getElementById('input_'+p+'_sqlselect').value='';
	document.getElementById('input_'+p+'_sqluser').value='';
        document.getElementById('input_'+p+'_sqlpass').value='';
    }

    if(i==5)
    {
	document.getElementById('input_'+p+'_remote').value='';
        document.getElementById('input_'+p+'_value').value='';
	document.getElementById('input_'+p+'_sqlurl').value='';
        document.getElementById('input_'+p+'_sqlselect').value='';
	document.getElementById('input_'+p+'_sqluser').value='';
        document.getElementById('input_'+p+'_sqlpass').value='';
    }


}

var TINYLIGHT={};
TINYLIGHT.page=function(){
	return{
		top:function(){return document.documentElement.scrollTop||document.body.scrollTop},
		width:function(){return self.innerWidth||document.documentElement.clientWidth||document.body.clientWidth},
		height:function(){return self.innerHeight||document.documentElement.clientHeight||document.body.clientHeight},
		total:function(d){
			var b=document.body, e=document.documentElement;
			return d?Math.max(Math.max(b.scrollHeight,e.scrollHeight),Math.max(b.clientHeight,e.clientHeight)):
			Math.max(Math.max(b.scrollWidth,e.scrollWidth),Math.max(b.clientWidth,e.clientWidth))
		}
	}
}();

function sboxShow(txt){
    var w=300;
    var h=300;
    p=document.getElementById('sbox');
    b=document.getElementById('scontent');
    b.innerHTML=txt;
    p.style.width=w?w+'px':'auto'; p.style.height=h?h+'px':'auto';

    var t=(TINYLIGHT.page.height()/2)-(p.offsetHeight/2); t=t<10?10:t;
    p.style.top=(t+TINYLIGHT.page.top()-(h/2))+'px';
    p.style.left=(TINYLIGHT.page.width()/2)-(250)+'px'

    p.style.display='block';
    setTimeout(function(){document.getElementById('sbox').style.display='none';},3000);
}
</script>

<div id="sbox" style="display: none;">
    <div id="scontent"><msg:getText key="portal.config.saveingconfig" /></div>
</div>

<%-- view messages--%>
<table>
    <tr>
	<td><div class="bold"><msg:getText key="text.global.0" />: </div></td>
	<td>${msg}</td>
    </tr>
</table>



<script type="text/javascript" src="${pageContext.request.contextPath}/js/tooltip.js"></script>
