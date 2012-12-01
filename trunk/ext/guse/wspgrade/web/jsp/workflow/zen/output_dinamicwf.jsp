<%--
Dinamikus wf konfiguraciokat letrehozo output konfiguracio
@call io.jsp
String ${portID} port belso azonosito
hu.sztaki.lpds.pgportal.service.base.data.PortData ${tmp} portkonfiguracio
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>
<script>
function showDinamicWFPanel(pValue){
    if(pValue){
        $('output_${portID}_dinamic').value='true';
        $('output_dwf_${portID}').style.display="block";
    }
    else{
        $('output_${portID}_dinamic').value='false';
        $('output_dwf_${portID}').style.display="none";
    }
}
</script>
<tr>
    <td width="30%"><msg:getText key="text.io.dinamic" />: </td>
    <td>
    	<input type="hidden" name="output_${portID}_dinamic" id="output_${portID}_dinamic" value="false" />
        <input type="radio" name="output_${portID}_dinamic0" id="output_${portID}_dinamic0_id1" value="0" onclick="showDinamicWFPanel(false);" /> <msg:getText key="text.io.14.no" />
    	<input type="radio" name="output_${portID}_dinamic0" id="output_${portID}_dinamic0_id2" value="1" onclick="showDinamicWFPanel(true)" /> <msg:getText key="text.io.14.yes" />
        <div id="output_dwf_${portID}" style="display:none">
            <table>
                <tr>
            	    <td><msg:getText key="text.io.dinamic.jobs" /></td>
            	    <td><input type="text" name="output_${portID}_dinamicjobs" id="output_${portID}_dinamicjobs" value=""/></td>
                </tr>
            </table>
        </div>
    </td>
</tr>
