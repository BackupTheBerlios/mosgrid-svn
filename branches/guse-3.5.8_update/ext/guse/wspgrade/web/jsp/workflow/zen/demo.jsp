<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easing.1.3.js"></script>
    <ul class="container">
	<c:forEach var="tmp" items="${inputs}">
        <li class="toogle_menu">
          <ul>
	    <li class="button">
		<a href="#" class="green">
		    <c:out value="${tmp.name}" escapeXml="true"  /> 
		    <c:if test="${tmp.preJob!=''}">(channel)</c:if>
		    <c:out value="${tmp.txt}" escapeXml="true"  />
		</a>
	    </li>
            <li class="dropdown">
                <ul class="toggle_data">
                    <li class="toggle_data">alma</li>
                    <li class="toggle_data">korte</li>
                </ul>
	    </li>
          </ul>
        </li>
	</c:forEach>
  </ul>
    <div class="clear"></div>
