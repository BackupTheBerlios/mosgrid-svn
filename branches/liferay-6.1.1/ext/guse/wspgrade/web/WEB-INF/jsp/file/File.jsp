<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html" %>
<portlet:actionURL var="pURL" portletMode="VIEW" >
</portlet:actionURL>

<portlet:resourceURL var="ajaxURL" >

    <portlet:param name="selected_file" value="${}"/>
</portlet:resourceURL>

<portlet:defineObjects/>



<jsp:useBean id="showFileBrowser" class="java.lang.String" scope="request"/>
<jsp:useBean id="isDetails" class="java.lang.String" scope="request"/>
<jsp:useBean id="guid" class="java.lang.String" scope="request"/>
<jsp:useBean id="fileName" class="java.lang.String" scope="request"/>
<jsp:useBean id="dirName" class="java.lang.String" scope="request"/>
<jsp:useBean id="dirMode" class="java.lang.String" scope="request"/>
<jsp:useBean id="ownerACL" class="java.lang.String" scope="request"/>
<jsp:useBean id="groupACL" class="java.lang.String" scope="request"/>
<jsp:useBean id="otherACL" class="java.lang.String" scope="request"/>

<jsp:useBean id="entryNum" class="java.lang.String" scope="request"/>
<jsp:useBean id="status" class="java.lang.String" scope="request"/>
<jsp:useBean id="fileMode" class="java.lang.String" scope="request"/>
<jsp:useBean id="owner" class="java.lang.String" scope="request"/>
<jsp:useBean id="group" class="java.lang.String" scope="request"/>
<jsp:useBean id="modDate" class="java.lang.String" scope="request"/>
<jsp:useBean id="fileSize" class="java.lang.String" scope="request"/>
<jsp:useBean id="path" class="java.lang.String" scope="request"/>
<jsp:useBean id="showErrorButton" class="java.lang.String" scope="request"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/ajax.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/tooltip.js"></script>
<script>
var ajaxURL = "${ajaxURL}";
 

 function getDownloadedFile()
{
    var x=document.getElementById("selected_file").value;

        var url= ajaxURL+'&selected_file='+x;
    request=GetXmlHttpObject(setDownloadedFile);
    request.open("POST",url, true);
    request.send("");
}

function setDownloadedFile()
{
    try
    {
        if((request.readyState == 4)&&(request.status == 200))
        {
            var resp =  request.responseText;
            
            if (resp != null)
            {
                document.getElementById('download_form').innerHTML=resp;
               
            }
        }
//        else {alert("A problem occurred with communicating between the XMLHttpRequest object and the server program.");}
    }
    catch (err) { alert("It does not appear that the server is available for this application. Please try again very soon. \nError: "+err.message);}

}


</script>

<%@ taglib uri="/sztaki" prefix="lpds"%>

<div id="formdiv">
<form method="post"  portletMode="VIEW" action="${pURL}">
       <input type="hidden" name="action" id="action" value="00"/>
       <input type="hidden" name="fileName" id="fileName" value="00"/>
       <input type="hidden" name="file_arguments" id="file_arguments" value="00"/>

    <table>
	
        <tr>
         
		<td> Select VO:  </td>
		<td>

                <SELECT id=VOListBoxBean_select" name="VOListBoxBean_select" onchange="">
                   <c:forEach var="vos" items="${voListBoxBean}">


                           <option Value = ${vos}>${vos}</option>
                       
                   </c:forEach>

                </SELECT>
                </td>



		<td><lpds:submit  actionID="action" actionValue="doListHostName" cssClass="portlet-form-button" txt="Get LFC Hosts" tkey="false" /></td>


	</tr>
      
        <tr>

		<td> Select LFC Host:  </td>
		<td>

                <SELECT id=hostNameListBoxBean_select" name="hostNameListBoxBean_select" onchange="">
                   <c:forEach var="lfcs" items="${hostNameListBoxBean}">
                       <option Value = ${lfcs}>${lfcs}</option>
                   </c:forEach>

                </SELECT>

		</td>
		<td>
        <lpds:submit  actionID="action" actionValue="doList" cssClass="portlet-form-button" txt="List LFC Host content" tkey="false" />
         </td>

	</tr>
	<tr>
		<td align="center"> <font color="#808080">or Enter</font> </td>
		<td> <input type="text" name="hostNameText" /> </td>
	</tr>
     
    </table>
<% if(showFileBrowser.equals(new String("1"))) { %>

<table>
     <tr bgcolor="#CCCCFF">
	<td><b>File Browser</b></td>
    </tr>

    <tr>
        <td>
            <table>
                <script>
                    function getSelectedItem()
                    {
                    var t=document.getElementById("fileLsList_select");
                    var hiddenfile=document.getElementById("selected_file");
                    hiddenfile.value = t.options[t.selectedIndex].value;

                    }

                </script>
                
                <tr>
                    <td rowspan="7" valign="top">

                    <SELECT id="fileLsList_select" name="fileLsList_select" onchange="getSelectedItem()" size="16">
                        <c:forEach var="entity" items="${fileLsList}">
                            
                            <c:choose>
                           <c:when test="${entity.dir}">
                               <c:choose>

                                   <c:when test="${entity.name eq selecteditem}">

                                    <option selected value="+@${entity.name}"> + ${entity.name}</option>
                                   </c:when>
                               <c:otherwise>
                               <option value="+@${entity.name}"> + ${entity.name}</option>
                               </c:otherwise>
                               </c:choose>
                           </c:when>
                           <c:otherwise>
                               <c:choose>
                                <c:when test="${entity.name eq selecteditem}">
                                        <option selected value="-@${entity.name}"> - ${entity.name}</option>
                               </c:when>
                               <c:otherwise>
                                   <option value="-@${entity.name}"> - ${entity.name}</option>
                                   </c:otherwise>
                               </c:choose>
                           </c:otherwise>
                            </c:choose>
                       
                        </c:forEach>

                    </SELECT>
                    </td>
		<td valign="top" > <ui:actionsubmit action="doGoUp" value="Go Up"/> </td>
		<td rowspan="6" width="10"> </td>
            </tr>
            <tr>
		<td> <lpds:submit  actionID="action" actionValue="doListSelectedDir" cssClass="portlet-form-button" txt="Change Directory" tkey="false" />
                </td>
            </tr>
             <tr>
		<td> <lpds:submit  actionID="action" actionValue="doGoUp" cssClass="portlet-form-button" txt="Back" tkey="false" />
                </td>
            </tr>

        
            <tr>
              	<td> <lpds:submit  actionID="action" actionValue="doGoDelete" cssClass="portlet-form-button" txt="Remove" tkey="false" />
                </td>

            </tr>
            <tr>
                <td> <lpds:submit  actionID="action" actionValue="doShowDetails" cssClass="portlet-form-button" txt="Details" tkey="false" />
                </td>
		
            </tr>

            <tr>
                <td> <lpds:submit  actionID="action" actionValue="doListReplicas" cssClass="portlet-form-button" txt="Replicas" tkey="false" />
                </td>
		
            </tr>

            <tr>
		<td>

                <table>
                     <tr>
                        <td>
                            <input type="text" id="dirName" name="dirName" value=""/>
                            <lpds:submit  actionID="action" actionValue="doMakeDir" cssClass="portlet-form-button" txt="Make Directory" tkey="false" />

                        </td>
                    </tr>
                </table>
		 
      
		</td>
	</tr>
	<tr>
		<td>
		 <table>
                     <tr>
                        <td>
                     <input type="text" name="newName" id="newName" value=""/>
                     <lpds:submit  actionID="action" actionValue="doRename" cssClass="portlet-form-button" txt="Rename" tkey="false" />
                     
		 </td>
                    </tr>
                </table>

		</td>
	</tr>
	</table>
	</td>

	<% if(isDetails.equals("showFileDetails")) { 
		String fullFileName = "lfn:" + path + "/" + fileName; %>
		<td> 
                   <table> 
                       <tr>
                            <td valign="top"> 
                                <table>
                                <tr><td>File Name:</td> <td><%=fullFileName%></td> </tr>
                                <tr><td>Owner:</td> <td><%=owner%></td> </tr>
                                <tr><td>Group:</td> <td><%=group%></td> </tr>
                                <tr><td> File Mode:</td> <td> <%=ownerACL%> </td> </tr>
                                <tr> <td> </td> <td> <%=groupACL%> </td> </tr>
                                <tr> <td> </td> <td> <%=otherACL%> </td> </tr>
                                </table>
                             </td>
                      </tr>
                      <tr>
                            <td>
                                <table>
                                <tr><td>File Size:</td> <td><%=fileSize%></td></tr>
                                <tr><td>Last Modified:</td> <td><%=modDate%></td> </tr>		
                                <tr><td>GUID:</td> <td><%=guid%></td> </tr>
                                <tr><td>
                                 <% String arguments = "0" + "@"+path+ "@"+ ownerACL+ "@"+ groupACL+ "@"+ otherACL; %>
                               <lpds:submit  actionID="action" actionValue="doGoUpdate" paramID="fileName" paramValue='<%=fileName%>' param0ID="file_arguments" param0Value="<%=arguments%>" cssClass="portlet-form-button" txt="Change Mode" tkey="false" />
                                </td></tr>
                                </table>>
                        </td>
		</tr> </table> </td>

	<% } else if(isDetails.equals("showDirDetails")) {
		String fullDirName =  "lfn:" + path + "/" + dirName; %>
		<td>
                    <table>
                      <tr>
                        <td valign="top" >
                            <table>
                                <tr><td>Directory Name:</td> <td><%=fullDirName%></td> </tr>
                                <tr><td>Number of Entries in the Directory:</td> <td><%=entryNum%></td> </tr>
                                <tr><td>Owner:</td> <td><%=owner%></td> </tr>
                                <tr><td>Group:</td> <td><%=group%></td> </tr>
                                <tr><td> Directory Mode:</td> <td> <%=ownerACL%> </td> </tr>
                                <tr> <td> </td> <td> <%=groupACL%> </td> </tr>
                                <tr> <td> </td> <td> <%=otherACL%> </td> </tr>
                                </table>
                        </td>
                     </tr>
                     <tr>
                         <td>
                             <table>
                                <tr><td>Last Modified:</td> <td><%=modDate%></td> </tr>
                                <tr><td

                               <% String arguments = "0" + "@"+path+ "@"+ ownerACL+ "@"+ groupACL+ "@"+ otherACL; %>
                               <lpds:submit  actionID="action" actionValue="doGoUpdate" paramID="fileName" paramValue='<%=fileName%>' param0ID="file_arguments" param0Value="<%=arguments%>" cssClass="portlet-form-button" txt="Change Mode" tkey="false" />
                                </td></tr>
		</table>
		</td>
		</tr> </table> </td>
	<% } %>
</tr> 
</table>

<table>
<tr bgcolor="#ffffff">
	<td><table>
		<td>Current Path:</td>
		<td><%=path%></td>
	</table></td>
</table>

<table>
    <tr>
<td align=left width="50%">
<lpds:submit  actionID="action" actionValue="doGoUpload"  cssClass="portlet-form-button" txt="Upload" tkey="false" /></td>
<td align=left width="50%">

</td>
</tr>
</table>

<% } %>
</form>

</div>
 <% if(showFileBrowser.equals(new String("1"))) { %>
<form method="post" action="${ajaxURL}" > <%--onsubmit="getDownloadedFile();return false;"--%>
          <input type="hidden" id="selected_file" name="selected_file" value="" />
          <input type="submit" id="submit" name="submit" value="Download" />
      </form>
  <div id="download_form">
  </div>
<% } %>

	<table>
  <tr bgcolor="#EFEFFF"><td>
    <b><i>Message:</i></b>&nbsp;&nbsp;<font color="990033">${message}</font>
  </td></tr>
  
  <% if(showErrorButton.equals("1")) { %>
  <tr> <td>
	<lpds:submit  actionID="action" actionValue="doShowRemoveError" paramID="itemName" paramValue='<%=fileName%>' cssClass="portlet-form-button" txt="Error" tkey="false" />
			
		</td>
  </tr>
  
  <% } %>
  </table>

 
  