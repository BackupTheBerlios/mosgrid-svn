<!-- workflow import from repository -->

<jsp:include page="/jsp/core.jsp" />

<%@ page contentType="text/html" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>

<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.wfimport" img="${pageContext.request.contextPath}/img/help.gif" />



<script type="text/javascript">
    function wfimp_jenabled(checkboxid, textid) {
    if(document.getElementById(checkboxid).checked) {
    document.getElementById(textid).value = "";
    document.getElementById(textid).disabled = false;
    } else {
    document.getElementById(textid).value = "";        
    document.getElementById(textid).disabled = true;
    }
    }

popUP_OK="<msg:getText key="text.io.14.yes" />";
popUP_NO="<msg:getText key="text.io.14.no" />";


</script>

<portlet:defineObjects/>
<portlet:actionURL var = "lURL">
    <portlet:param name="guse" value="doList"/>
</portlet:actionURL>
<portlet:actionURL var = "pURL">
    <portlet:param name="guse" value="doJustDoIt"/>
</portlet:actionURL>


<c:if test="${(isroot==true)}">
    <table class="kback">

	<br />
    

	<tr>
	    <td class="khead"><msg:getText key="text.wfimport.0" />:</td>
	    <td>${quotamax} Byte</td>
	</tr>
	<tr>
	    <td class="khead"><msg:getText key="text.wfimport.1" />:</td>
	    <td>${quota} Byte</td>
	</tr>
	<tr>
	    <td class="khead"><msg:getText key="text.wfimport.2" />:</td>
	    <td>${quotapercent} %</td>
	</tr>
    </table>

    <br />

</c:if>

<form method="post" action="${lURL}">
    <table>
        <tr>
            <td>
	    <msg:getText key="text.wfimport.3" />:
	    <select name="wfType" id="wfTypeId" onchange="">
		<option value="appl" ${sappl}><msg:getText key="text.wfimport.application" /></option>
		<option value="proj" ${sproj}><msg:getText key="text.wfimport.project" /></option>
		<option value="real" ${sreal}><msg:getText key="text.wfimport.concrete" /></option>
		<option value="abst" ${sabst}><msg:getText key="text.wfimport.template" /></option>
		<option value="graf" ${sgraf}><msg:getText key="text.wfimport.graph" /></option>
	    </select>
            </td>
	    <td>
		<msg:toolTip id="ctmpdiv" tkey="portal.wfimport.wftype" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
            <td>
		<input class="portlet-form-button" type="submit" value="<msg:getText key="button.load" />" />
            </td>
        </tr>
    </table>
</form>

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0"  >
<tr><td>

    <form method="POST" action="${pURL}">

        <table width="1200px">

            <tr>
                <td>
		<c:if test="${(wfListSize>0)}">
		    <msg:getText key="${wfimpListName}" /> <msg:getText key="text.wfimport.wflistfromrepo" />
		</c:if>
		<c:if test="${(wfListSize==0)}">
		    <msg:getText key="${wfimpListName}" /> <msg:getText key="text.wfimport.wflistisempty" />
		</c:if>
                </td>
	    </tr>
	    
            <tr>
                <td>
		
        	<c:if test="${(wfListSize>0)}">
		    <table class="kback">
		    <tr>
			<th class="khead"></th>
			<th class="khead"><msg:getText key="text.wfimport.4" /></th>
			<th class="khead"><msg:getText key="text.wfimport.5" /></th>
			<th class="khead"><msg:getText key="text.wfimport.user" /></th>
                        <th class="khead"><msg:getText key="button.delete" /></th>
		    </tr>
                    <c:forEach var="wfLista" items="${wfList}" varStatus="pos">
    			<c:choose>
			<c:when test="${(pos.index%2)==1}">
			    <c:set var="color" value="kline1" />
			</c:when>
			<c:otherwise>
			    <c:set var="color" value="kline0" />
			</c:otherwise>
			</c:choose>
			<tr>
			<td class="${color}">
                    	    <c:choose>
                    	    <c:when test="${pos.index == 0}">
                                <input class="portlet-form-field" type="radio" name="impItemId" value="${wfLista.id}" checked="checked" />
                    	    </c:when>
                    	    <c:otherwise>
                                <input class="portlet-form-field" type="radio" name="impItemId" value="${wfLista.id}" />
                    	    </c:otherwise>
                    	    </c:choose>
			</td>
                        <td class="${color}"><c:out value="${wfLista.workflowID}" escapeXml="true" /></td>
			<td class="${color}">
			    <div style="position:relative;border:0px solid; height:50px; width:500px; overflow:auto; padding:8px;" id="pmsgp">
                                <c:out value="${wfLista.exportText}" escapeXml="true" />
			    </div>
			</td>
                        <td class="${color}"><c:out value="${wfLista.userID}" escapeXml="true" /></td>
                        <td class="${color}">
                            <c:if test="${deletable || userid==wfLista.userID}">
                                <portlet:actionURL var="pDelete">
                                    <portlet:param name="impMethode" value="delete" />
                                    <portlet:param name="impItemId" value="${wfLista.id}" />
                                    <portlet:param name="guse" value="doJustDoIt"/>
                                    <portlet:param name="impWfType" value="${wfimpListType}" />
                                </portlet:actionURL>
                                <a href="#" class="icolink" onclick="TINY.box.show(popUPLink('${pDelete}','<msg:getText key="text.wfimport.delete" />'),0,300,300,1);">
                                    <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
                                    <msg:getText key="button.delete" />
                                </a>
                            </c:if>
<%--
                            <portlet:actionURL var="pImport">
                                <portlet:param name="impMethode" value="import" />
                                <portlet:param name="impItemId" value="${wfLista.id}" />
                                <portlet:param name="guse" value="doJustDoIt"/>
                                <portlet:param name="impWfType" value="${wfimpListType}" />
                            </portlet:actionURL>
                            <a href="#" class="icolink" onclick="TINY.box.show(popUPNewForm('${pImport}','import'),0,300,300,1);">
                                <img src="${pageContext.request.contextPath}/imgs/serviceadmin/delete.png" /><br />
                                <msg:getText key="text.wfimport.import" />
                            </a>
--%>
                        </td>
			</tr>
                    </c:forEach>
		    </table>

    		</c:if>
		    
                </td>
            </tr>

	    <br />
    
        <tr>
    	    <td>
	    <c:if test="${(wfListSize>0)}">
	        <input type="hidden" name="impMethode" value="import" >
<%--		<msg:toolTip id="ctmpdiv" tkey="portal.wfimport.executetype" img="${pageContext.request.contextPath}/img/tooltip.gif" /> --%>
		<input class="portlet-form-button" type="submit" name="gs_action=doJustDoIt" value="<msg:getText key="text.wfimport.import" />"/>
		<input  type="hidden" name="impWfType" value="${wfimpListType}" />

	    </c:if>
    	    </td>
        </tr>
	    
	    
	    
	    
	    
	    
        </table>

        <table>
            <tr>
                <td>
	            <c:if test="${(wfListSize>0)}">
			<br />
            		<msg:getText key="text.wfimport.7" />:
		    </c:if>
                </td>
            </tr>
        </table>
		    
        <table border="1" width="410px">
	
        <c:if test="${(wfListSize>0)}">

        <tr>
            <td class="portlet-section-body">

            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfimport.8" /> :
                </td> 
                <td class="portlet-section-body">            
                    <input id="wfimp_grafName" type="text" name="wfimp_newGrafName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_grafCheckBox" type="checkbox" value="OFF" onclick="wfimp_jenabled('wfimp_grafCheckBox', 'wfimp_grafName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfimport.graf" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfimport.9" /> : 
                </td>                 
                <td class="portlet-section-body">
                    <input id="wfimp_abstName" type="text" name="wfimp_newAbstName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_abstCheckBox" type="checkbox" value="OFF" onclick="wfimp_jenabled('wfimp_abstCheckBox', 'wfimp_abstName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfimport.abst" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>
            <tr>
                <td class="portlet-section-body" width="200px">
                    <msg:getText key="text.wfimport.10" /> : 
                </td>                 
                <td class="portlet-section-body">
                    <input id="wfimp_realName" type="text" name="wfimp_newRealName" value="" disabled="true"/>
                </td>
                <td class="portlet-section-body">
                    <input id="wfimp_realCheckBox" type="checkbox" value="OFF" onclick="wfimp_jenabled('wfimp_realCheckBox', 'wfimp_realName')"/>
                </td>
                <td class="portlet-section-body">
		    <msg:toolTip id="ctmpdiv" tkey="portal.wfimport.real" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                </td>
            </tr>

            </td>
        </tr>

        </c:if>

        </table>

    </form>

</td></tr>
</table>

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0"  >
<tr><td>

    <table>
        <tr>
            <td><b><msg:getText key="text.global.0" />: </b></td>
        </tr>
        <tr>
            <td>
	        <div style="position:relative;border:0px solid; height:128px; width:700px; overflow:auto; padding:8px;" id="pmsgp">
		    ${wfimpmsg}
		</div>
            </td>
        </tr>
    </table>

</td></tr>
</table>
