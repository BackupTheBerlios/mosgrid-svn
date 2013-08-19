<!-- template kezeles -->
<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>


<%@ taglib uri="/lpdsmessage" prefix="msg"%> 

<msg:help id="helptext" tkey="help.templatelist" img="${pageContext.request.contextPath}/img/help.gif" />

<jsp:useBean id="dt" class="java.util.Date" />

<portlet:defineObjects/>

<portlet:renderURL var="rURL" />

<portlet:actionURL var="pURL" >
    <portlet:param name="guse" value="doCreateAbstrackt" />
    </portlet:actionURL>
<portlet:actionURL var="eURL">
    <portlet:param name="guse" value="doExport" />
</portlet:actionURL>

<div id="rwlist" style="position:relative;">



<script>
popUP_OK="<msg:getText key="text.io.14.yes" />";
popUP_NO="<msg:getText key="text.io.14.no" />";
</script>

									 
<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%" >
<tr><td>

    <form method="post" action="${pURL}"> 
    

    <table width="100%" cssClass="kback">
        <tr>
	    <td colspan="3">
		<table>
		    <tr>
<%--		    
			<td rowspan="2" style="border-bottom:2px solid #0"> Create from</td>
--%>
			<td><msg:getText key="text.templatelist.0" /></td>
			<td> 
			    <input type="radio" name="pfrom" id="pfrom0" checked="true" value="c" onclick="document.getElementById('pcw').disabled=false;document.getElementById('pt').disabled=document.getElementById('ptw').disabled=true;"/>
			    <select name="pcw" id="pcw"> 
				<c:forEach var="tmp" items="${cWorkflowList}">
                    <option value="<c:out value="${tmp}" escapeXml="true" />"> <c:out value="${tmp}" escapeXml="true" /></option>
				</c:forEach>
			    </select> 
			    <msg:toolTip id="ctmpdiv" tkey="portal.template.wflist1" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		    <tr>
			<td style="border-bottom:2px solid #0"><msg:getText key="text.templatelist.1" /></td>
			<td style="border-bottom:2px solid #0"> 
			    <input type="radio" name="pfrom" id="pfrom1" value="t" onclick="document.getElementById('ptw').disabled=false;document.getElementById('pt').disabled=document.getElementById('pcw').disabled=true;"/>
			    <select name="ptw" id="ptw" disabled> 
				<c:forEach var="tmp" items="${aWorkflowList}">
                    <option value="<c:out value="${tmp}" escapeXml="true" />"> <c:out value="${tmp}" escapeXml="true" /></option>
				</c:forEach>
			    </select> 
			    <msg:toolTip id="ctmpdiv" tkey="portal.template.wflist2" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		    <tr>
			<td style="border-bottom:2px solid #0"><msg:getText key="text.templatelist.template" /></td>
			<td style="border-bottom:2px solid #0"> 
			    <input type="radio" name="pfrom" id="pfrom2" value="templ" onclick="document.getElementById('pt').disabled=false;document.getElementById('ptw').disabled=document.getElementById('pcw').disabled=true;"/>
			    <select name="pt" id="pt" disabled> 
				<c:forEach var="tmp" items="${requestScope.aWorkflowListSorted}">
                    <option value="<c:out value="${tmp.workflowID}" escapeXml="true" />"> <c:out value="${tmp.workflowID}" escapeXml="true" /></option>
				</c:forEach>
			    </select> 
			    <msg:toolTip id="ctmpdiv" tkey="portal.template.wflist3" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>                    
		    <tr>
			<td><msg:getText key="text.templatelist.2" />:</td>
			<td> 
			    <input type="text" name="pntname" />
			</td>
			<td>
			    <msg:toolTip id="ctmpdiv" tkey="portal.template.newname" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		    <tr>
			<td><msg:getText key="text.templatelist.3" />:</td>
			<td> 
			    <input type="text" name="pntdesc" value="${dt.year+1900}-${dt.month+1}-${dt.date}" />
			</td>
			<td>
			    <msg:toolTip id="ctmpdiv" tkey="portal.template.desc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
			</td>
		    </tr>
		</table>
	    </td>
	</tr>
	<tr>
	    <td><input type="submit" value="<msg:getText key="button.configure" />"></td>
	</tr>
    </table>
    </form>
    
    <table width="100%" cssClass="kback">
        <tr>
	    <td style="border-bottom:2px solid" class="portlet-section-body" style="width:30%"><msg:getText key="text.templatelist.4" /></td>
    	    <td style="border-bottom:2px solid" class="portlet-section-body" style="width:10%"><msg:getText key="text.templatelist.5" /></td>
	    <td style="border-bottom:2px solid" class="portlet-section-body" style="width:60%"><msg:getText key="text.templatelist.6" /></td>
	</tr>

	<c:forEach var="awkf" items="${requestScope.aWorkflowListSorted}" varStatus="ln">

	    <c:choose>
		<c:when test="${(ln.index%2)==1}">
	    	    <c:set var="color" value="kline1" />
    		</c:when>
		<c:otherwise>
	    	    <c:set var="color" value="kline0" />
		</c:otherwise>
	    </c:choose>		
	
	    <tr>
            <td class="${color}"><div class="bold"><c:out value="${awkf.workflowID}" escapeXml="true" /></div></td>
		<td class="${color}">
			<table>
			    <tr>
                    <portlet:resourceURL var="r0" >
                        <portlet:param name="workflowID" value="${awkf.workflowID}" />
                    </portlet:resourceURL>
	        	        <td>
	        		    <form method="post" action="${r0}" name="upform">
					<input type="submit" value="<msg:getText key="button.download" />" class="portlet-form-button">
				    </form>
				</td>
				<td>
	        			<c:if test="${awkf.tmp=='0'}" >
                            <portlet:actionURL var="pDURL">
                                <portlet:param name="guse" value="doDelete" />
                                <portlet:param name="workflow" value="${awkf.workflowID}" />
                            </portlet:actionURL>
                    <input type="button" class="portlet-form-button"  value="<msg:getText key="button.delete" />"
                    onclick="TINY.box.show(popUPLink('${pDURL}','<msg:getText key="text.templatelist.7" /> ${awkf.workflowID} <msg:getText key="text.templatelist.8" /><br />'),0,300,300,1);" />
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
					<input type="hidden" name="portalID"   value="${portalID}">
					<input type="hidden" name="storageID"  value="${storageID}">
					<input type="hidden" name="wfsID"      value="${awkf.wfsID}">
		    			<input type="hidden" name="userID"     value="${puser}">
					<input type="hidden" name="workflowID" value="${awkf.workflowID}">
					<input type="hidden" name="typ"        value="abst">
					<table border="1" align="center">
					<tr>
					    <td><msg:getText key="text.templatelist.9" /></td>
					    <td>
						<textarea cols="40" name="exporttext"></textarea>
						<msg:toolTip id="ctmpdiv" tkey="portal.template.exportdesc" img="${pageContext.request.contextPath}/img/tooltip.gif" />
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
    <table style="padding-top:15px;">
        <tr>
            <td><div class="bold"><msg:getText key="text.global.0" />: </div></td>
            <td><div id="msg"><msg:getText key="${msg}" /></div></td>
        </tr>
    </table>

</td></tr>
</table>

</div>
