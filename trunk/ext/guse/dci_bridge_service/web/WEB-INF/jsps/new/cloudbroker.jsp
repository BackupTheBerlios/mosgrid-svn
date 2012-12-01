<%-- 
    Document   : New CloudBroker
    Created on : Jan 18, 2012, 10:15:18 AM
    Author     : Akos Hajnal
--%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >


<table class="newdata">
    <caption><f:message key="caption.cloudbroker.new" /></caption>
    <form method="post" action="conf">
    <tr>
        <th class="ln0"><f:message key="cloudbroker.name" /></th>
        <td class="ln0"><input class="ln0" type="text" name="pcloudbrokername" size="25" value="CloudBroker1" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="general.status" /></th>
        <td class="ln1">
            <input type="radio" name="penabled" checked="true" value="1" /><f:message key="general.yes" />
            <input type="radio" name="penabled" value="0" /><f:message key="general.no" />
        </td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="cloudbroker.url" /></th>
        <td class="ln1"><input class="ln0" type="text" name="pcloudbrokerurl" size="60" value="https://scibus.cloudbroker.com" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="cloudbroker.user" /></th>
        <td class="ln1"><input class="ln0" type="text" name="pcloudbrokeruser" size="60" value="zfarkas@sztaki.hu" /></td>
    </tr>
    <tr>
        <th class="ln1"><f:message key="cloudbroker.password" /></th>
        <td class="ln1"><input class="ln0" type="password" name="pcloudbrokerpassword" size="60" value="" /></td>
    </tr>
    <tr>
        <td colspan="2"><input type="image" src="imgs/save.png" value="<f:message key="form.ok" />" /><br />
            (<f:message key="form.ok" />)
        </td>
    </tr>
</form>
</table>
</f:bundle>
