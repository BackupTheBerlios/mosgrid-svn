<%@ page contentType="text/html"%>
<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<portlet:actionURL var = "pURLadd">
    <portlet:param value="doAddRes" name="guse"/>
</portlet:actionURL>
<portlet:actionURL var = "pURLdel">
    <portlet:param value="doDelRes" name="guse"/>
</portlet:actionURL>


<table>
    <tr>
        <td>${publickey}</td>
    </tr>
</table>
<br/>
<form method="post" action="${pURLdel}" >
    <input type="hidden" name="guse" id="guse" value="doDelRes">
    <input type="hidden" name="delid" id="delid" value="" />
    <table cellpadding="2">
        <tr>
            <th><msg:getText key="publickey.useraccount" /> @ <msg:getText key="publickey.resourcequeue" /></th>
            <th><msg:getText key="text.realworkflowlist.action" /></th>
        </tr>
        <c:forEach var="res" items="${rlist}" >
                <tr>
                    <td>${res.r}</td>
                    <td><lpds:submit actionID="guse" actionValue="doDelRes" paramID="delid" paramValue="${res.n}" cssClass="portlet-form-button" txt="button.delete" tkey="true" /></td>
                </tr>
        </c:forEach>
    </table>
</form>
<form method="post" action="${pURLadd}" >
<msg:getText key="publickey.resourcequeue" />:
    <select name="stype" >
        <c:forEach var="typ" items="${gridtypes}" >
            <option value="${typ}" <c:if test="${stype==typ}">selected</c:if>>${typ}</option>
        </c:forEach>
    </select>
    <input type="hidden" name="type" value="${stype}">
    <msg:getText key="publickey.useraccount" />: <input type="text" name="sshuser"  value="" /> <%-- onkeyup="this.value = this.value.replace(/[^0-9]/g, '')" --%>

    <input type="submit" value="Add" />
</form>

<br/>
<div><b><i>Message:</i></b> <font color="990033"> ${msg}</font></div>