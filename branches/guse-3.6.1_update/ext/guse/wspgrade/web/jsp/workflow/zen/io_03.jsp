<%-- input condition --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<div class="ioconfigtable">


    <div style="display:table-row;margin-top:1px;float:left;">
	<div class="jobconfig-header"><msg:getText key="text.io.1" />:</div>
	<div class="ioconfig-data">
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
    		</div> 
	    </div>
	</div>    	
    </div>

