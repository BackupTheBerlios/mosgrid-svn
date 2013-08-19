<%-- 
    @include /WEB-INF/jsp/information/glite/view.jsp
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<p:resourceURL var="ajaxURL" />
<script>
    var ajaxURL="${ajaxURL}";
</script>
<table border="1">
    
    <tr>
        <th>Service</th>
        <th>Primary</th>
        <th>Secondary</th>
   </tr>

    <tr>
        <td>BDII</td>
        <td><c:out value="${props['primary.bdii']}" default="" escapeXml="true" /></td>
        <td><c:out value="${props['secondary.bdii']}" default="" escapeXml="true" /></td>
   </tr>

    <tr>
        <td>RB</td>
        <td><c:out value="${props['primary.rb']}" default="" escapeXml="true"/></td>
        <td><c:out value="${props['secondary.rb']}" default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>WMS</td>
        <td><c:out value="${props['primary.wms']}" default="" escapeXml="true" /></td>
        <td><c:out value="${props['secondary.wms']}" default="" escapeXml="true"  /></td>
   </tr>
    <tr>
        <td>VOMS</td>
        <td><c:out value="${props['primary.voms']}"  default="" escapeXml="true" /></td>
        <td><c:out value="${props['secondary.voms']}"  default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>LFC</td>
        <td><c:out value="${props['primary.lfc']}" default="" escapeXml="true" /></td>
        <td><c:out value="${props['secondary.lfc']}" default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>FTS</td>
        <td><c:out value="${props['primary.fts']}" default="" escapeXml="true" /></td>
        <td><c:out value="${props['secondary.fts']}"  default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>AMGA</td>
        <td><c:out value="${props['primary.amga']}" default="" escapeXml="true"/></td>
        <td><c:out value="${props['secondary.amga']}" default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>MyProxy</td>
        <td><c:out value="${props['primary.myproxy']}" default="" escapeXml="true"/></td>
        <td><c:out value="${props['secondary.myproxy']}" default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>RGMA</td>
        <td><c:out value="${props['primary.rgma']}" default="" escapeXml="true"/></td>
        <td><c:out value="${props['secondary.rgma']}" default="" escapeXml="true" /></td>
   </tr>
    <tr>
        <td>Web site</td>
        <td colspan="2">
            <c:choose>
                <c:when test="${props['website']!=null}">
                    <a target="_blank" href="<c:out value="${props['website']}" escapeXml="true"/>">[info site]</a>
                </c:when>
                <c:otherwise>
                        Nincs a VO-hoz informacios website konfiguralva
                </c:otherwise>
            </c:choose>
            </td>
   </tr>
</table>