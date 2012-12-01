<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>




<portlet:defineObjects/>


<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<portlet:renderURL var="rURL" />




<div id="div_wait" class="shape" style="display:none;position:fixed;left:30%;top:40%" >
    <div class="hdn_txt" id="div_wait_txt">
	 <br><br><br><msg:getText key="please.wait" /><br><br><br>
    </div>
</div>



<portlet:actionURL var = "pURL" >
    <portlet:param name="guse" value="doList"/>
</portlet:actionURL>

<form method="post" id="reload" action="${pURL}">

<input class="portlet-form-button" type="submit" name="refresh" id="refresh" value="<msg:getText key="button.refresh" />" />
<br/><br/>
<msg:getText key="text.ginfo" />

<table>
    <tr>
	<td><msg:getText key="text.job.Gemlcarepo" />:</td>
        <td class="portlet-section-body">
				    <select class="portlet-form-button" name="GLCurl" id="GLCurl" onChange="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('reload').submit();">
					<c:if test="${sgrid==null}"><option value="-">-</option></c:if>
					<c:forEach var="tmp0" items="${grids}">
					    <c:choose>
						<c:when test="${tmp0==sgrid}"><option selected="true" value="${tmp0}">${tmp0}</option></c:when>
						<c:otherwise><option value="${tmp0}">${tmp0}</option></c:otherwise>
					    </c:choose>    
					</c:forEach>	
				    </select>
	</td>
    </tr>
    <tr>
	<td><msg:getText key="text.job.GemlcaLegacyCode" />:</td>
        <td class="portlet-section-body">
				    <select class="portlet-form-button" name="GLC" id="GLC" onChange="javascript:document.getElementById('div_wait').style.display='block';document.getElementById('reload').submit();">
                        
					<c:if test="${sGLC==null}"><option value="-">-</option></c:if>
					<c:forEach var="legacyCode" items="${GLCs}">
                          
					    <c:choose>
                                <c:when test="${legacyCode==sGLC}">
                                    <c:choose>
                                        <c:when test="${fn:startsWith(legacyCode,'gusedelimit---')==true}">
	                                    <c:set var="delimiter" value="${fn:substring(legacyCode,11,-1)}" scope="page" />
                                            <option disabled="true" style="color:red" value="${delimiter}"><strong>${delimiter}</strong></option>
        	                        </c:when>
            	                        <c:otherwise>
                	                    <option selected="true" value="${legacyCode}">${legacyCode}</option>
                        	        </c:otherwise>
				    </c:choose>
                                </c:when>
						<c:otherwise>
                            <c:choose>
                                    
                                    <c:when test="${fn:startsWith(legacyCode,'gusedelimit---')==true}">
                                    <c:set var="delimiter" value="${fn:substring(legacyCode,11,-1)}" scope="page" />
                                            <option disabled="true" style="color:red" value="${delimiter}"><strong>${delimiter}</strong></option>
                                    </c:when>
                                    <c:otherwise>
                                            <option value="${legacyCode}">${legacyCode}</option>
                                    </c:otherwise>

                                
                            </c:choose>

                        </c:otherwise>
					    </c:choose>    
					</c:forEach>	
				    </select>

                 

	</td>
    </tr>
</table>

</br>

<c:if test="${sGLC!=null}">
<table>
	    	<tr>
		<td width="30%"><b><msg:getText key="text.ginfo.paramdesc" /></b></td>
		<td ><b><msg:getText key="text.ginfo.paramvalue" /></b></td>
		<td ><b><msg:getText key="text.ginfo.paramtype" /></b></td>
		</tr>

	<c:forEach var="tmp0" items="${GLCparams}">
			<c:choose>
			    <c:when test="${(tmp0.index%2)==1}">
    				<c:set var="color" value="kline1" />
			    </c:when>
			    <c:otherwise>
    				<c:set var="color" value="kline0" />
			    </c:otherwise>
			</c:choose>

	    	<tr>
		<td class="${color}" width="30%">${tmp0['friendlyName']} :</td>
		<td class="${color}">		
			${tmp0['value']}
		</td>
		<td class="${color}">
		<c:choose>
			<c:when test="${tmp0['input']=='true'}">
			    Input <c:if test="${tmp0['file']=='true'}">File</c:if>
			</c:when>
			<c:otherwise>
			    Output <c:if test="${tmp0['file']=='true'}">File</c:if>
		        </c:otherwise>	
		</c:choose>
			
		</td>
		</tr>
	</c:forEach>	

</table>

<br />
<msg:getText key="text.ginfo.inputnumber" />: ${GLCin}
<br />
<msg:getText key="text.ginfo.outputnumber" />: ${GLCout}
<br />
</c:if>

<c:if test="${msg!=null}">
    <table style="padding-top:15px;">
        <tr>
            <td><div class="bold"><msg:getText key="text.global.0" />: </div></td>
            <td><div id="msg"><msg:getText key="${msg}" /></div></td>
        </tr>
    </table>
</c:if>
</form>