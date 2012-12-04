<jsp:include page="/jsp/core.jsp" />

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/portlet" prefix="portlet" %>
<%@ taglib uri="/sztaki" prefix="lpds"%>
<portlet:defineObjects/>
<portlet:actionURL var="pURL" >
    <portlet:param name="guse" value="doAddTiming" />
</portlet:actionURL>

<portlet:renderURL var="rURL" />


<%@ taglib uri="/lpdsmessage" prefix="msg"%> 
<msg:help id="helptext" tkey="help.timing" img="${pageContext.request.contextPath}/img/help.gif" />
<form method="post" action="${rURL}">
    <input type="submit" class="portlet-form-button" value="<msg:getText key="button.refresh" />" />
</form>

<div id="rwlist" style="position:relative;">

<form method="post" action="${pURL}"> 

<input type="hidden" name="workflow" id="workflow">
<input type="hidden" name="action" id="action">

<div id="delete" class="shape" style="display:none;position:absolute;" >
    <div class="hdn_txt" id="delete_txt">Really delete timed workflow?<br>
    <lpds:submit actionID="action" actionValue="doDelete" cssClass="portlet-form-button" txt="button.delete" tkey="true"/>
    <input type="button" class="portlet-form-button" onclick="javascript:hide('delete')" value="<msg:getText key="button.cancel" />" />
    </div>
</div>

<table class="portlet-pane" cellspacing="1" cellpadding="1" border="0" width="100%" >
<tr><td>

    <table width="30%">
        <tr>
	    <td><msg:getText key="text.timing.0" />:</td>
	</tr>
        <tr>
	    <td>${servertime}</td>
	</tr>
    </table>

    <table width="100%">

        <c:if test="${(wfnum>0)}">
	
        <tr>
    	    <td><msg:getText key="text.timing.1" />:</td>
	    <td>
		<select name="pWorkflow">
		<c:forEach var="pWl" items="${wlist}">
            <option><c:out value="${pWl}" escapeXml="true" /></option>
		</c:forEach>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.timing.wflist" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	    <td><msg:getText key="text.timing.2" />:</td>
	    <td>
		<msg:getText key="text.timing.3" />:
		<select name="pYear" >
		    <option>2010</option>
		    <option>2011</option>
		    <option>2012</option>
                    <option>2013</option>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.timing.year" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	    <td>
		<msg:getText key="text.timing.4" />:
		<select name="pMonth">
		    <option>01</option> <option>02</option> <option>03</option> <option>04</option>
		    <option>05</option> <option>06</option> <option>07</option> <option>08</option>
		    <option>09</option> <option>10</option> <option>11</option> <option>12</option>
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.timing.month" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	    <td>
		<msg:getText key="text.timing.5" />:
		<select name="pDay" />
		    <option>01</option> <option>02</option> <option>03</option> <option>04</option>
		    <option>05</option> <option>06</option> <option>07</option> <option>08</option>
		    <option>09</option> <option>10</option> <option>11</option> <option>12</option>
		    <option>13</option> <option>14</option> <option>15</option> <option>16</option>
		    <option>17</option> <option>18</option> <option>19</option> <option>20</option>
		    <option>21</option> <option>22</option> <option>23</option> <option>24</option>
		    <option>25</option> <option>26</option> <option>27</option> <option>28</option>
		    <option>29</option> <option>30</option> <option>31</option> 
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.timing.day" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	    <td>
		<msg:getText key="text.timing.6" />:
		<select name="pHour" />
		    <option>00</option> 
		    <option>01</option> <option>02</option> <option>03</option> <option>04</option>
		    <option>05</option> <option>06</option> <option>07</option> <option>08</option>
		    <option>09</option> <option>10</option> <option>11</option> <option>12</option>
		    <option>13</option> <option>14</option> <option>15</option> <option>16</option>
		    <option>17</option> <option>18</option> <option>19</option> <option>20</option>
		    <option>21</option> <option>22</option> <option>23</option> 
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.timing.hour" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	    <td>
		<msg:getText key="text.timing.7" />:
		<select name="pMinute"><option>00</option>
		    <option>01</option> <option>02</option> <option>03</option> <option>04</option>
		    <option>05</option> <option>06</option> <option>07</option> <option>08</option>
		    <option>09</option> <option>10</option> <option>11</option> <option>12</option>
		    <option>13</option> <option>14</option> <option>15</option> <option>16</option>
		    <option>17</option> <option>18</option> <option>19</option> <option>20</option>
		    <option>21</option> <option>22</option> <option>23</option> <option>24</option>
		    <option>25</option> <option>26</option> <option>27</option> <option>28</option>
		    <option>29</option> <option>30</option> <option>31</option> <option>32</option> 
		    <option>33</option> <option>34</option> <option>35</option> <option>36</option>
		    <option>37</option> <option>38</option> <option>39</option> <option>40</option>
		    <option>41</option> <option>42</option> <option>43</option> <option>44</option>
		    <option>45</option> <option>46</option> <option>47</option> <option>48</option>
		    <option>49</option> <option>50</option> <option>51</option> <option>52</option> 
		    <option>53</option> <option>54</option> <option>55</option> <option>56</option>
		    <option>57</option> <option>58</option> <option>59</option> 
		</select>
		<msg:toolTip id="ctmpdiv" tkey="portal.timing.minute" img="${pageContext.request.contextPath}/img/tooltip.gif" />
	    </td>
	    <td>
		<lpds:submit actionID="action" actionValue="doAddTiming" cssClass="portlet-form-button" txt="button.add" tkey="true"/>                  
	    </td>	    
        </tr>


        <c:if test="${(twfnum>0)}">

        <tr>
	    <td colspan="7">
		<table width="100%" class="kback">
		    <tr>
			<th><msg:getText key="text.timing.8" /></th>
			<th><msg:getText key="text.timing.9" /></th>
			<th><msg:getText key="text.timing.10" /></th>
		    </tr>
		    <c:forEach var="tmp" items="${twlist}" varStatus="p">
    			<c:choose>
    			    <c:when test="${(p.index%2)==1}">
				<c:set var="color" value="kline1" />
			    </c:when>
			    <c:otherwise>
				<c:set var="color" value="kline0" />
			    </c:otherwise>
			</c:choose>		
    		    <tr>
			<td class="${color}">
			     <table>
                     <tr><td><div class="bold"><c:out value="${tmp.workflowID}" escapeXml="true"  /></div></td></tr>
                     <tr><td>&nbsp;<c:out value="${tmp.txt}"  escapeXml="true" /></td></tr>
			     </table>
			</td>
            <td class="${color}"><c:out value="${tmp.timing}" escapeXml="true"  /> </td>
			<td class="${color}"> 
			    <input type="button" class="portlet-form-button" onclick="javascript:hide('delete','${tmp.workflowID}',event)" value="<msg:getText key="button.delete" />" />
			</td>
    		    </tr>
		    </c:forEach>
		</table>
	    </td>	    
        </tr>

	</c:if>

	</c:if>
	
        <tr>
	    <td colspan="7"><div class="bold"><msg:getText key="text.global.0" />: </div><msg:getText key="${msg}" /></td>
        </tr>

    </table>

</td></td>
</table>

</form>
</div>    
