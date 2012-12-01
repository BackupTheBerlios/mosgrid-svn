<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<jsp:include page="/jsp/core.jsp" />
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>


<portlet:actionURL var = "pURLup">
    <portlet:param value="doRepositoryUpload" name="guse"/>
</portlet:actionURL>

<c:if test="${msg!=null}">
    <p><b><i>Message:</i></b> <font color="red"> ${msg}</font>
    </c:if>
<p><a target="blank" href="${dspaceURL}/register">Click here to register for a DSpace account</a>
<form method="post" action="${pURLup}" >

    <table >
        <tr>
            <td>*DSpace email: </td>
            <td><input type="text" name="dsemail"  value="<c:out value="${dsemail}" escapeXml="true" />" /></td>
        </tr>

        <tr>
            <td>*DSpace password: </td>
            <td><input type="password" name="dspass"/></td>
        </tr>

        <tr>
            <td colspan="0">&nbsp;</td>
        </tr>
        <tr>
            <td>*<b>Select Workflow:</b> </td>
            <td>
                <select name="workflowID" id="workflowID" >
                    <option id="-">-</option>
                    <c:forEach var="tmp" items="${wkfs}">
                        <option id="<c:out value="${tmp.workflowID}" escapeXml="true" />"><c:out value="${tmp.workflowID}" escapeXml="true" /></option>
                    </c:forEach>
                </select>
            </td>
        </tr>

        <tr>
            <td colspan="0"><b>Author: </b></td>
        </tr>
        <tr>
            <td>*First Name: </td>
            <td><input type="text" size="25"  name="firstName" value="<c:out value="${firstName}" escapeXml="true" />"></td>
        </tr>
        <tr>
            <td>*Last Name: </td>
            <td><input type="text" size="25"  name="lastName" value="<c:out value="${lastName}" escapeXml="true" />"></td>
        </tr>

        <tr>
            <td colspan="0"><b>Information:</b></td>
        </tr>
        <tr>
            <td>*Title: </td>
            <td><input type="text" size="61"  name="title" value="<c:out value="${title}" escapeXml="true" />"></td>
        </tr>
        <tr>
            <td>Keywords: </td>
            <td><input type="text" size="61"  name="keywords" value="<c:out value="${keywords}" escapeXml="true" />"> Comma-separated list (e.g. Keyword1, Keyword2)</td>
        </tr>
        <tr>
            <td>Grid: </td>
            <td><input type="text" size="35"  name="grid" value="<c:out value="${grid}" escapeXml="true" />"> (e.g. EGEE, SEE-GRID)</td>
        </tr>
        <tr>
            <td>VO: </td>
            <td><input type="text" size="35"  name="vo" value="<c:out value="${vo}" escapeXml="true" />">(e.g. GILDA, Seismology VO)</td>
        </tr>
    </table>

    <p>Abstract:<br/>
        <textarea rows="10" cols="120" name="abstract" ><c:out value="${abstract}" escapeXml="true" /></textarea>
        <br/>Enter an abstract for your workflow here

    <p>Description:<br/>
        <textarea rows="25" cols="120" name="description" ><c:out value="${description}" escapeXml="true" /></textarea>
        <br/>Enter a detailed description for your workflow here

    <p> *Required information

    <p><input type="submit" value="Export to DSpace" />
</form>

<div><b><i>Message:</i></b> <font color="990033"> ${msg}</font></div>
