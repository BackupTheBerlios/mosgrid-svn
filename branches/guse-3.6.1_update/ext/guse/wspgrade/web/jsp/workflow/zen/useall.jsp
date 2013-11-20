<%-- 
minden jobra alkalmazas
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/lpdsmessage" prefix="msg"%>

<div style="display:table-row;margin-top:1px;float:left;">
    <div class="jobconfig-header"><msg:getText key="text.job.20" />:</div>
    <div class="jobconfig-data">
        <input title='<msg:getText key="config.job.usealljob" />' type="checkbox" name="job_useallthis" id="job_useallthis" value="${jobname}" />
    </div>
</div>
