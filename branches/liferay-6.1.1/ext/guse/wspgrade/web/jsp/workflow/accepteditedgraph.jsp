<%-- 
    Document   : accepteditedgraph
    Created on : 2011.09.13., 14:05:41
    Author     : Krisztian Karoczkai
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<p:actionURL var="pURL" >
    <p:param name="guse" value="doConfigure" />
    <p:param name="pcwkf" value="${workflow}" />
</p:actionURL>

<form method="post" action="${pURL}">
    <table width="800px">
        <caption><msg:getText key="text.modify.graph.warning" /></caption>
        <tr>
            <td class="kline0"><msg:getText key="text.modify.graph.name" /> </td>
            <td class="kline0">
                <select name="pgraf">
                <c:forEach var="t" items="${graphs}">
                    <c:choose>
                        <c:when test="${graph==t}">
                            <option selected="true">${t}</option>
                        </c:when>
                        <c:otherwise><option>${t}</option></c:otherwise>
                    </c:choose>
                </c:forEach>
                </select>
<%--                <msg:toolTip id="ctmpdiv" tkey="portal.config.graphlist" img="${pageContext.request.contextPath}/img/tooltip.gif" /> --%>
            </td>
        </tr>
        <tr>
            <td class="kline1"><msg:getText key="text.modify.graph.newwfname" /></td>
            <td class="kline1">
                <input name="workflow" value="${workflow}"/>
<%--                <msg:toolTip id="ctmpdiv" tkey="portal.config.newwfname" img="${pageContext.request.contextPath}/img/tooltip.gif" /> --%>
            </td>
        </tr>
        <tr>
            <td class="kline0"><msg:getText key="text.modify.graph.newwfdesc" /></td>
            <td class="kline0">
                <jsp:useBean id="dt" class="java.util.Date" />
                <textarea name="pNewWkfDesc" rows="5" cols="25">${dt.year+1900}-${dt.month+1}-${dt.date}</textarea>
<%--                <msg:toolTip id="ctmpdiv" tkey="portal.config.newwfdesc" img="${pageContext.request.contextPath}/img/tooltip.gif" /> --%>
            </td>
        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="image" src="${pageContext.request.contextPath}/imgs/accept_64.png" />
                <a href="#" onclick="TINY.box.hide();"><img src="${pageContext.request.contextPath}/imgs/remove_64.png" /></a>
            </td>
        </tr>
    </table>

</form>