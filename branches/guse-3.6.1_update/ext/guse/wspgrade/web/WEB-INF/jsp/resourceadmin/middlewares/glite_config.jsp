<%-- 
    @include /WEB-INF/jsp/resourceadmin/middlewares/gLite.jsp
    List<String> ${sessionScope.grids} elerheto VO-k listaja
--%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/portlet_2_0" %>
<p:resourceURL var="ajaxURL" />
<script>
    var ajaxURL="${ajaxURL}";
</script>
<table border="1">
    <caption>VO administration</caption>
    <tr>
        <th>VO Name</th>
        <td colspan="2">
            <select name="new.name" id="vo-name" onchange="javascript:getGLITEconfig()">
            <c:forEach var="t" items="${sessionScope.grids}" >
                <c:choose>
                    <c:when test="${selected==t}">
                        <option selected="true"><c:out value="${t}" escapeXml="true" /></option>
                    </c:when>
                    <c:otherwise>
                        <option><c:out value="${t}" escapeXml="true" /></option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            </select>
        </td>
   </tr>
    <tr>
        <th>Service</th>
        <th>Primary</th>
        <th>Secondary</th>
   </tr>

    <tr>
        <td>BDII</td>
        <td><input type="text" name="primary.bdii" value="<c:out value="${prop['primary.bdii']}"/>" /></td>
        <td><input type="text" name="secondary.bdii" value="<c:out value="${prop['secondary.bdii']}" />"/></td>
   </tr>

    <tr>
        <td>RB</td>
        <td><input type="text" name="primary.rb" value="<c:out value="${prop['primary.rb']}"/>"/></td>
        <td><input type="text" name="secondary.rb" value="<c:out value="${prop['secondary.rb']}" />" /></td>
   </tr>
    <tr>
        <td>WMS</td>
        <td><input type="text" name="primary.wms" value="<c:out value="${prop['primary.wms']}"/>"/></td>
        <td><input type="text" name="secondary.wms" value="<c:out value="${prop['secondary.wms']}" />" /></td>
   </tr>
    <tr>
        <td>VOMS</td>
        <td><input type="text" name="primary.voms" value="<c:out value="${prop['primary.voms']}"/>"/></td>
        <td><input type="text" name="secondary.voms" value="<c:out value="${prop['secondary.voms']}" />" /></td>
   </tr>
    <tr>
        <td>LFC</td>
        <td><input type="text" name="primary.lfc" value="<c:out value="${prop['primary.lfc']}"/>"/></td>
        <td><input type="text" name="secondary.lfc" value="<c:out value="${prop['secondary.lfc']}" />" /></td>
   </tr>
    <tr>
        <td>FTS</td>
        <td><input type="text" name="primary.fts" value="<c:out value="${prop['primary.fts']}"/>"/></td>
        <td><input type="text" name="secondary.fts" value="<c:out value="${prop['secondary.fts']}" />" /></td>
   </tr>
    <tr>
        <td>AMGA</td>
        <td><input type="text" name="primary.amga" value="<c:out value="${prop['primary.amga']}"/>"/></td>
        <td><input type="text" name="secondary.amga" value="<c:out value="${prop['secondary.amga']}" />"/></td>
   </tr>
    <tr>
        <td>MyProxy</td>
        <td><input type="text" name="primary.myproxy" value="<c:out value="${prop['primary.myproxy']}"/>"/></td>
        <td><input type="text" name="secondary.myproxy" value="<c:out value="${prop['secondary.myproxy']}" />" /></td>
   </tr>
    <tr>
        <td>RGMA</td>
        <td><input type="text" name="primary.rgma" value="<c:out value="${prop['primary.rgma']}"/>"/></td>
        <td><input type="text" name="secondary.rgma" value="<c:out value="${prop['secondary.rgma']}" />" /></td>
   </tr>
    <tr>
        <td>Web site</td>
        <td colspan="2"><input type="text" name="website" value="<c:out value="${prop['website']}"/>"/></td>
   </tr>
</table>