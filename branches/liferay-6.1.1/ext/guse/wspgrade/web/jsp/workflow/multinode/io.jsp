<!-- JOB IO szekesztesenek UIja -->



<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<link type="text/css" href="/gridsphere/themes/default/css/default.css" rel="stylesheet">
<div style="color:#ffffff">
<tablewidth="100%" class="window-main">
    <tr>
	<td width="20%" class="kback"><div class="bold">Phase's name: </div></td>
	<td width="80%" class="kback">${jobname}</td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.global.2" />: </div></td>
	<td width="80%" class="kback">${jobtxt}</td>
    </tr>
</table>
<tablewidth="100%">
    <tr>
	<td>
	    <a href="javascript:getConfForm('m=GetJobView&job=${jobname}')" class="clink" style="color:#ffffff">Phase definition </a>&nbsp;&nbsp;
	    <font class="clink">Flow Connectors &nbsp;&nbsp;</font>
	    <%--<a href="javascript:getConfForm('m=GetHistoryView&job=${jobname}')" class="clink" style="color:#ffffff"><msg:getText key="text.job.global.6" /></a> &nbsp;&nbsp; --%>
	</td>
    </tr>
</table>
<div  style="position:relative;border:1px solid #ad2e27; width:100%; height:400px;overflow:auto;" align="left">
<msg:help id="helptext" tkey="help.phase.flow" img="${pageContext.request.contextPath}/img/help.gif" />    
<tablewidth="100%" class="window-main">
    <c:if test="${inputsnum>0}">
	<tr><th colspan="2" class="khead">Requires</th></tr>    	
    </c:if>	
    <c:forEach var="tmp" items="${inputs}"> 
    <tr><td>
    <tablewidth="100%" ">
        <tr><td colspan="2" style="padding-top:5px;"></td></tr>    	
	<tr><td colspan="2" class="ksline">
	<tr>
	    <th class="kahead">
		 ${tmp.seq}: ${tmp.name}
		<c:if test="${tmp.prejob!=''}">(channel) </c:if>
	    </th>
	    <th class="kahead">${tmp.txt}</th>
	</tr>  
	
	
	

        <tr>
            <td width="30%"><msg:getText key="text.phase.key" />:</td>
            <td>
                <msg:toolTip id="cicc" tkey="config.help.key" img="${pageContext.request.contextPath}/img/tooltip.gif" /> 
                <input type="text" name="input_${tmp.id}_key" id="input_${tmp.id}_key" value="${tmp.key}" <c:if test="${tmp.prejob!=''}">disabled</c:if> />                
            </td>
        </tr>
        <tr>
            <td width="30%"><msg:getText key="text.phase.defaultValue" />:</td>
            <td>
                <msg:toolTip id="cicc" tkey="config.help.defaultvalue" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <input type="text" name="input_${tmp.id}_defaultvalue" id="input_${tmp.id}_defaultvalue" value="${tmp.defaultvalue}" />                 
            </td>
        </tr>        
        <tr><td colspan="2"><div class="iodline"></div></td></tr>
<%--
        <tr>
            <td width="30%"><msg:getText key="text.phase.scope" />: </td>
            <td>
                <msg:toolTip id="cicc" tkey="help.phase.scope" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <select name="input_${tmp.id}_scope" id="input_${tmp.id}_scope">
                    <c:choose>
                        <c:when test="${tmp.scope=='phase'}"><option selected="true">phase</option></c:when>
                        <c:otherwise><option>phase</option></c:otherwise>
                    </c:choose>   
                    <c:choose>
                        <c:when test="${tmp.scope=='service'}"><option selected="true">service</option></c:when>
                        <c:otherwise><option>service</option></c:otherwise>
                    </c:choose>      
                    <c:choose>
                        <c:when test="${tmp.scope=='node'}"><option selected="true">node</option></c:when>
                        <c:otherwise><option>node</option></c:otherwise>
                    </c:choose> 
                    <c:choose>
                        <c:when test="${tmp.scope=='global'}"><option selected="true">global</option></c:when>
                        <c:otherwise><option>global</option></c:otherwise>
                    </c:choose>                     
                </select>    
            </td>
        </tr>  

        <tr>
            <td width="30%"><msg:getText key="text.phase.mandatory" />: </td>
            <td>
                <msg:toolTip id="cicc" tkey="help.phase.mandatory" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <select name="input_${tmp.id}_mandatory" id="input_${tmp.id}_mandatory">
                    <c:choose>
                        <c:when test="${tmp.mandatory=='mandatory'}"><option selected="true">mandatory</option></c:when>
                        <c:otherwise><option>mandatory</option></c:otherwise>
                    </c:choose>   
                    <c:choose>
                        <c:when test="${tmp.mandatory=='optional'}"><option selected="true">optional</option></c:when>
                        <c:otherwise><option>optional</option></c:otherwise>
                    </c:choose>                     
                </select>    
            </td>
        </tr>  

        <tr>
            <td width="30%"><msg:getText key="text.phase.resolutionTime" />: </td>
            <td>
                <msg:toolTip id="cicc" tkey="help.phase.resolutionTime" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <select name="input_${tmp.id}_resolutionTime" id="input_${tmp.id}_resolutionTime">
                    <c:choose>
                        <c:when test="${tmp.resolutionTime=='Runtime'}"><option selected="true">Runtime</option></c:when>
                        <c:otherwise><option>Runtime</option></c:otherwise>
                    </c:choose>   
                    <c:choose>
                        <c:when test="${tmp.resolutionTime=='Design'}"><option selected="true">Design</option></c:when>
                        <c:otherwise><option>Design</option></c:otherwise>
                    </c:choose>                     
                </select>    
            </td>
        </tr>  
        
        <tr>
            <td width="30%"><msg:getText key="text.phase.valueResolution" />:</td>
            <td>
                <msg:toolTip id="cicc" tkey="config.help.valueResolution" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <input type="text" name="input_${tmp.id}_valueResolution" id="input_${tmp.id}_valueResolution" value="${tmp.valueResolution}" />                 
            </td>
        </tr>                    
--%>        
    </table>
    
  
    
    </td></tr>
    
    </c:forEach>
</table>

    
    
    


<tablewidth="100%" class="window-main">
    <c:if test="${outputsnum>0}">
	<tr><th colspan="2" class="khead">Provides</th></tr>    	
    </c:if>	
    <c:forEach var="tmp" items="${outputs}">
        <tr><td colspan="2" style="padding-top:5px;">
        <tr><td colspan="2" class="ksline">
	<tr>
	    <th class="kahead">${tmp.seq}: ${tmp.name}</th>
	    <th class="kahead">${tmp.txt}</th>
	</td>    	
	        
        <tr>
            <td width="30%"><msg:getText key="text.phase.key" />:</td>
            <td>
                <msg:toolTip id="cicc" tkey="config.help.key" img="${pageContext.request.contextPath}/img/tooltip.gif" /> 
                <input type="text" name="output_${tmp.id}_key" id="output_${tmp.id}_key" value="${tmp.key}" />                
            </td>
        </tr>
        <tr>
            <td width="30%"><msg:getText key="text.phase.defaultValue" />:</td>
            <td>
                <msg:toolTip id="cicc" tkey="config.help.defaultvalue" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <input type="text" name="output_${tmp.id}_defaultvalue" id="output_${tmp.id}_defaultvalue" value="${tmp.defaultvalue}" />                 
            </td>
        </tr>       
        <tr><td colspan="2"><div class="iodline"></div></td></tr>        
<%--
        <tr>
            <td width="30%"><msg:getText key="text.phase.scope" />: </td>
            <td>
                <msg:toolTip id="cicc" tkey="help.phase.scope" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <select name="output_${tmp.id}_scope" id="output_${tmp.id}_scope">
                    <c:choose>
                        <c:when test="${tmp.scope=='phase'}"><option selected="true">phase</option></c:when>
                        <c:otherwise><option>phase</option></c:otherwise>
                    </c:choose>   
                    <c:choose>
                        <c:when test="${tmp.scope=='service'}"><option selected="true">service</option></c:when>
                        <c:otherwise><option>service</option></c:otherwise>
                    </c:choose>      
                    <c:choose>
                        <c:when test="${tmp.scope=='node'}"><option selected="true">node</option></c:when>
                        <c:otherwise><option>node</option></c:otherwise>
                    </c:choose> 
                    <c:choose>
                        <c:when test="${tmp.scope=='global'}"><option selected="true">global</option></c:when>
                        <c:otherwise><option>global</option></c:otherwise>
                    </c:choose>                     
                </select>    
            </td>
        </tr>  

        <tr>
            <td width="30%"><msg:getText key="text.phase.mandatory" />: </td>
            <td>
                <msg:toolTip id="cicc" tkey="help.phase.mandatory" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <select name="output_${tmp.id}_mandatory" id="output_${tmp.id}_mandatory">
                    <c:choose>
                        <c:when test="${tmp.mandatory=='mandatory'}"><option selected="true">mandatory</option></c:when>
                        <c:otherwise><option>mandatory</option></c:otherwise>
                    </c:choose>   
                    <c:choose>
                        <c:when test="${tmp.mandatory=='optional'}"><option selected="true">optional</option></c:when>
                        <c:otherwise><option>optional</option></c:otherwise>
                    </c:choose>                     
                </select>    
            </td>
        </tr>  

        <tr>
            <td width="30%"><msg:getText key="text.phase.resolutionTime" />: </td>
            <td>
                <msg:toolTip id="cicc" tkey="help.phase.resolutionTime" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                <select name="output_${tmp.id}_resolutionTime" id="output_${tmp.id}_resolutionTime">
                    <c:choose>
                        <c:when test="${tmp.resolutionTime=='Runtime'}"><option selected="true">Runtime</option></c:when>
                        <c:otherwise><option>Runtime</option></c:otherwise>
                    </c:choose>   
                    <c:choose>
                        <c:when test="${tmp.resolutionTime=='Design'}"><option selected="true">Design</option></c:when>
                        <c:otherwise><option>Design</option></c:otherwise>
                    </c:choose>                     
                </select>    
            </td>
        </tr>  
        
        <tr>
            <td width="30%"><msg:getText key="text.phase.valueResolution" />:</td>
            <td>
                <msg:toolTip id="cicc" tkey="config.help.valueResolution" img="${pageContext.request.contextPath}/img/tooltip.gif" /> 
                <input type="text" name="output_${tmp.id}_valueResolution" id="output_${tmp.id}_valueResolution" value="${tmp.valueResolution}" />
            </td>
        </tr>   
--%>          





    </c:forEach>

</table>          
        
        


    <input type="button" class="portlet-form-button" onclick="${adi};setRemoteParamDOMValue('${snd}');closediv('cdata');" value="<msg:getText key="button.ok" />">
    <input type="button" class="portlet-form-button" onclick="closediv('cdata');" value="<msg:getText key="button.quit" />">

</div></div>

<!--

<service name="string" module="string">
	<version name="string" configuration="string">
		<platformMapping platform="string">
			<phase name="string" script="string">
				<logEntry type="pre/post"/>
				<pair key="string"
				      type="requires/provides"
				      scope="phase/service/node/global"
				      mandatory="boolean"
				      resolutionTime="Runtime/Design"
				      valueResolution="string"
				      defaultValue="string"/>
				<runsAfter phase="string"/>
			</phase>
		</platformMapping>
	</version>
</service>

-->