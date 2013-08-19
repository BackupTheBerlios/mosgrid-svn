<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- WF tulajdonsag szerkesztes UI -->

<%@ page contentType="text/html;charset=Shift_JIS" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>



<script>
<!--
    var jobID="${jobID}";    
//-->
 		    
</script> 

<input type="hidden" id="pgridtype" value="${sgridtype}">
<div id="div_wait" class="shape" style="display:none;position:fixed;left:30%;top:40%" >
    <div class="hdn_txt" id="div_wait_txt">
	 <br><br><br><msg:getText key="please.wait" /><br><br><br>
    </div>
</div>


<tablewidth="100%" class="window-main">
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.global.pluginname" />: </div></td>
	<td width="80%" class="kback">${jobname}</td>
    </tr>
    <tr>
	<td width="20%" class="kback"><div class="bold"><msg:getText key="text.global.pluginnote" />: </div></td>
	<td width="80%" class="kback">${jobtxt}</td>
    </tr>
</table>


<div id="plugin" style="display:block">

    <table width="100%" "><caption class="khead">WF properies</caption>
    <tr><td><msg:help id="helptext" tkey="help.phase.project" img="${pageContext.request.contextPath}/img/help.gif" /></td></tr>
    <tr><td>
            <p align="center">
            <tablewidth="80%">
            <tr>
                <td >    
                    Node
                </td>                   
                <td>
                     Platform                                 
                </td>
                <td>
                </td>
            </tr>                  
           <c:forEach var="node" items="${nodes}">
            <tr>
                <td>    
                    ${node.key}
                </td>                   
                <td>
                     ${node.value}                                   
                </td>
                <td>
                    <input type="button" class="portlet-form-button" onclick="getRemoteSelectOptions('nn=${node.key}&mode=del&m=AddNode');getConfForm('m=GetWFConfAddNodeView');" value="Delete node ">                         
                </td>
            </tr>   
            </c:forEach> 
            <tr>
                <td>    
                    <msg:toolTip id="cicc" tkey="help.phase.node" img="${pageContext.request.contextPath}/img/tooltip.gif" />
                    <input type="text" name="nname" id="nname" onkeyup="this.value = this.value.replace(' ', '')">
                </td>                   
                <td>
                    <select name="nplatform" id="nplatform" >
                    <option></option>    
                    <c:forEach var="tmp0" items="${platforms}">
                        <c:choose>
                            <c:when test="${tmp0.name==snode}"><option selected="true" value="${tmp0.name}">${tmp0.displayName}</option></c:when>
                            <c:otherwise><option value="${tmp0.name}">${tmp0.displayName}</option></c:otherwise>
                        </c:choose>    
                    </c:forEach>
                    </select>                                   
                </td>
            </tr> 
            </table>
            </p>
    </td></tr>
    
    
 
</table>
<div id="plugin" style="display:none"><select name="res" id="res" ></select></div>

    <c:choose>
    <c:when test="${error==null}">   
   <input type="button" class="portlet-form-button" onclick="if (document.getElementById('nname').value!='' && document.getElementById('nplatform').value!=''){getRemoteSelectOptions('nn='+document.getElementById('nname').value+'&np='+document.getElementById('nplatform').value+'&m=AddNode');getConfForm('m=GetWFConfAddNodeView');}" value="Add new node ">                         
 
    </c:when>
    <c:otherwise>
    <tr>
        <td width="30%">Error: </td>
        <td>           
            <p style="color:#ff0000"><b><msg:getText key="${error}"/></b></p>
        </td>
    </tr>     
    
    </c:otherwise>
    </c:choose>
    
    
    <input type="button" class="portlet-form-button" onclick="closediv('cdata');" value="<msg:getText key="button.quit" />">
</div>


 

  
 