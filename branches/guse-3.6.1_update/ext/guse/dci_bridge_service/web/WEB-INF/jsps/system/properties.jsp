<%-- 
    Document   : properties
    Created on : 2011.06.30., 14:25:10
    Author     : krisztian
--%>
<%@taglib  prefix="f" uri="http://java.sun.com/jsp/jstl/fmt" %>
<f:requestEncoding value="UTF-8" />
<f:setLocale value="${pageContext.request.locale}" />
<f:bundle basename="dict"  >

<form method="post" action="conf">
<table class="newdata">
    <caption> <f:message key="caption.properties" /> </caption>
    <tr>
        <td><f:message key="properties.workdir" /></td>
        <td><input type="text" name="workdir" size="100" value="${config.system.path}"></td>
    </tr>

    <tr>
        <td><f:message key="properties.mbwsdl" /></td>
        <td><input type="text" name="metabroker" size="100" value="${config.system.metabroker}"></td>
    </tr>
    
    <tr><td colspan="2"><input type="submit" value="<f:message key="form.ok" />" /></td></tr>
</table>
</form>
</f:bundle>