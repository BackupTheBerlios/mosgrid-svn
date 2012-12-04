<!-- workflow grafok letrehozasa -->
<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<portlet:defineObjects/>
<portlet renderURL="rURL" />
<portlet:actionURL var="eURL" >
    <portlet:param name="guse" value="doExport" />
</portlet:actionURL>

<portlet:resourceURL var="r0" />

<div id="rwlist" style="position:relative;">

<form method="POST" action="${rURL}">
    <input class="portlet-form-button" type="submit"  value="<msg:getText key="button.refresh" />"/>
</form>

<form method="POST" action="${r0}">
    <input type="hidden" name="wfId" value=""/>
    <input type="hidden" name="jnlp" value=""/>
    <input class="portlet-form-button" type="submit"  value="<msg:getText key="button.opengrapheditor" />"/>
</form>

<script>
popUP_OK="<msg:getText key="text.io.14.yes" />";
popUP_NO="<msg:getText key="text.io.14.no" />";
</script>
									 

<table  class="portlet-pane"  cellspacing="1"  cellpadding="1"  border="0"  width="100%" >

    <tr>
    <td style="width:100%">


    <table width="100%" >    
        <tr>
	    <td style="border-bottom:2px solid" class="portlet-section-body" width="30%"><msg:getText key="text.grafswrk.name" /></td>
    	    <td style="border-bottom:2px solid" class="portlet-section-body" width="10%"> <center><msg:getText key="text.grafswrk.action" /></center> </td>
	    <td style="border-bottom:2px solid" class="portlet-section-body" width="60%"><msg:getText key="text.grafswrk.description" /></td>
	</tr>
	
	<c:forEach var="awkf" items="${requestScope.aWorkflowList}" varStatus="ln">

	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
	    	    <c:set var="color" value="kline1" />
    		</c:when>
		<c:otherwise>
	    	    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>		
	
	    <tr>
		<td class="${color}"><div class="${color}" style="font-weight:bold;"><c:out value="${awkf.workflowID}" escapeXml="true" /></div></td>
		<td class="${color}">
		    <table>
			<tr>
			    <td>
<portlet:resourceURL var="r1" >
<portlet:param name="wfId" value="${awkf.workflowID}" />
<portlet:param name="jnlp" value="" />
</portlet:resourceURL>

                    <form method="post" action="${r1}">
				    <input class="portlet-form-button" type="submit" value="<msg:getText key="button.attach" />">
				</form>
			    </td>    	
			    <td>
<portlet:resourceURL var="dwl" >
    <portlet:param name="workflowID" value="${awkf.workflowID}" />
</portlet:resourceURL>

		    		<form method="post" action="${dwl}" name="upform">
				    <input type="submit" value="<msg:getText key="button.download" />" class="portlet-form-button">
				</form>
			    </td>    	
			    <td>

				<c:if test="${awkf.tmp=='0'}" >
                    <portlet:actionURL var="pURL" >
                        <portlet:param name="workflow" value="${awkf.workflowID}" />
                        <portlet:param name="guse" value="doDelete" />
                    </portlet:actionURL>
                    <input type="button" class="portlet-form-button"  value="<msg:getText key="button.delete" />"
                    onclick="TINY.box.show(popUPLink('${pURL}','<msg:getText key="text.grafswrk.0" /> ${awkf.workflowID} <msg:getText key="text.grafswrk.1" /><br>'),0,300,300,1);" />
                </c:if>
			    </td>    	
			    <td>
		    		<form method="post" action="" name="">
    				    <input type="button" id="ex_${awkf.workflowID}" class="portlet-form-button" onclick="javascript:document.getElementById('template_${awkf.workflowID}').style.display='block';document.getElementById('ex_${awkf.workflowID}').disabled=true;" value="<msg:getText key="button.export" />" />
				</form>
			    </td>
			</tr>
		    </table>	    	
		</td>
		<td class="${color}"><c:out value="${awkf.txt}" escapeXml="true" /></td>
	    </tr>
	    <tr>
		<td colspan="2">

		    <div id="template_${awkf.workflowID}" style="display:none">
			<form method="post" action="${eURL}">  
                <input type="hidden" name="portalID"    value="${portalID}">
			    <input type="hidden" name="storageID"   value="${awkf.storageID}">
			    <input type="hidden" name="wfsID"       value="${awkf.wfsID}">
		    	    <input type="hidden" name="userID"      value="${puser}">
			    <input type="hidden" name="workflowID"  value="${awkf.workflowID}">
			    <input type="hidden" name="typ"         value="graf"> 
			    <table border="1" align="center">
			    <tr>
				<td><msg:getText key="text.grafswrk.2" /></td>
				<td>
				    <textarea cols="40" name="exporttext"></textarea>
				    <msg:toolTip id="ctmpdiv" tkey="portal.graph.exportdesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
				</td>
			    </tr>    
			    <tr>
			        <td colspan="2">
				    <input type="button" onclick="javascript:document.getElementById('template_${awkf.workflowID}').style.display='none';document.getElementById('ex_${awkf.workflowID}').disabled=false;" value="<msg:getText key="button.cancel" />">
				    <input type="submit" value="<msg:getText key="button.send" />"> 
			        </td>
			    </tr> 
			    </table>   
			</form> 
		    </div>

		</td>
	    </tr>
	</c:forEach>

    </table>
    </td>
    </tr>

    <tr>
	<td style="width:100%">
	    <table class="padding-top:15px;">
    		<tr>
        	    <td class="portlet-section-body"><div class="bold"><msg:getText key="text.global.0" />: </div></td>
        	    <td class="portlet-section-body"><div id="msg"><msg:getText key="${msg}" /></div></td>
    		</tr>
	    </table>
	</td>
    </tr>
    
</table>

</div>
