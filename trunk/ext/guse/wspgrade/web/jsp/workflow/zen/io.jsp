<!-- JOB IO szekesztesenek UIja -->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<link type="text/css" href="/gridsphere/themes/default/css/default.css" rel="stylesheet">

<%@taglib uri="http://java.sun.com/portlet_2_0" prefix="p" %>
    
<script>
    function submitInputFiles(){
    <c:forEach var="tmp" items="${inputs}">
        additem('input${tmp.id}','input_${tmp.seq}','${jobID}','input_${tmp.id}_file');
    </c:forEach>
    }
    $(document).ready(function($){
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
        toggleMenu();        
    
    });
</script>
    
<p:resourceURL var="ajaxURL">                             
    <p:param name="m" value="GetIOView" />
</p:resourceURL>
<div style="width:100%; height:380px;overflow:auto;">
    <form method="post" action="${sessionScope['ajaxGetIOViewURL']}" onsubmit="javascript:formsubmit(this,reloadPanel);submitInputFiles();return false;" id="job_ioform">
    <input type="hidden" name="smsg" />

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easing.1.3.js"></script>
<br />
<ul class="container">
    <c:forEach var="input" items="${inputs}">
	<c:set var="tmp" scope="request" value="${input}" />
    <li>
	<ul>
	    <li class="button">
		<a href="#" class="input">Port Number:<b>${tmp.seq}</b> &nbsp;&nbsp; Port Name:
		    <b><c:out value="${tmp.name}" escapeXml="true"  /> </b>
		    <c:if test="${tmp.preJob!=''}">(channel)</c:if>
		    <i style="float:right"><c:out value="${tmp.txt}" escapeXml="true"  /></i>
		</a>
		
	    </li>
            <li class="dropdown shadownoborder">
                <ul>
                    <li><table><tr><td><jsp:include page="io_01.jsp" /></td></tr></table></li>
                    
		    <c:if test="${iworkflow!='null'}">
                    <li><table><tr><td><jsp:include page="io_02.jsp" /></td></tr></table></li>
                    </c:if>

		    <c:if test="${iworkflow=='null' || iworkflow==''}">
		    <li><table><tr><td><jsp:include page="io_03.jsp" /></td></tr></table></li>
		    </c:if>                
		    
		    <c:if test="${tmp.preJob==''}">
			<li><table><tr><td><jsp:include page="io_04.jsp" /></td></tr></table></li>
		    </c:if>
			<li><table><tr><td><jsp:include page="io_05.jsp" /></td></tr></table></li>

                </ul>
	    </li>
          </ul>
    </li>
    </c:forEach>
    
    <c:forEach var="output" items="${outputs}">
	<c:set var="tmp" scope="request" value="${output}" />
    <li>
	<ul>
	    <li class="button">
		<a href="#" class="output">Port Number:<b>${tmp.seq}</b> &nbsp;&nbsp; Port Name:
		    <c:out value="${tmp.name}" escapeXml="true"  /> 
		    <c:if test="${tmp.preJob!=''}">(channel)</c:if>
		    <i style="float:right"><c:out value="${tmp.txt}" escapeXml="true"  /></i>
		</a>
	    </li>
            <li class="dropdown shadownoborder">
                <ul>
                    <li>
                	<table><tr><td>
                	<jsp:include page="io_06.jsp" />
                	</td></tr></table>
            	    </li>
            <c:if test="${jobgridtype=='gt2' || jobgridtype=='gt4' || jobgridtype=='glite' || jobgridtype=='unicore'}">
                <li><table><tr><td><jsp:include page="io_07.jsp" /></td></tr></table></li>
            </c:if>

		    <c:if test="${iworkflow!='null' && iworkflow!=''}">
		    <li><table><tr><td><jsp:include page="io_08.jsp" /></td></tr></table></li>
		    </c:if>                
		    
		    <c:if test="${tmp.preJob==''}">
			<li><table><tr><td><jsp:include page="io_09.jsp" /></td></tr></table></li>
		    </c:if>
			<li><table><tr><td><jsp:include page="io_10.jsp" /></td></tr></table></li>

                </ul>
	    </li>
          </ul>
    </li>
    </c:forEach>
    
    

</ul>
<div class="clear"></div>
<center>
<input type="image" src="${pageContext.request.contextPath}/imgs/accept_64.png" onclick=""/>
</center>
</form>
</div>
<%--

    <c:if test="${enabledDinamicWF!=null}">
        <c:set var="portID" value="${tmp.id}" scope="request" />
        <jsp:include page="output_dinamicwf.jsp" />

	<tr><td colspan="2"><div class="iodline" /></td></tr>
    </c:if>        

    <input type="button" class="portlet-form-button" onclick="javascript:${adi};setRemoteParamDOMValue('${snd}');closePopUp();" value="<msg:getText key="button.ssave" />">

--%>

