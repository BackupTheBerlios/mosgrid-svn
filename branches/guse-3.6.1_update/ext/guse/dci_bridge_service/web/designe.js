/**
 * Linkre kattintas megerositese
 * @param pOKlink megerosites eseten a link href-e
 * @param pMsg megjelenitendo szoveges uzenet
 */
function popUPLink(pOKlink,pMsg){
    var content="<img src=\""+webapp+"/imgs/comment_64.png\" /><br/>";
    content=content+pMsg+"<br/>";
    content =content+"<a href=\""+pOKlink+"\" style=\"width:70px;float:left;display:block;text-align:center;\">";
    content=content+"<img src=\""+webapp+"/imgs/accept_64.png\" /><br/>";
    content=content+popUP_OK;
    content=content+"</a>"
    content=content+"<a href=\"#\" style=\"width:70px;float:left;display:block;text-align:center;\" onclick=\"TINY.box.hide();\">";
    content=content+"<img src=\""+webapp+"/imgs/remove_64.png\" /><br/>";
    content=content+popUP_NO;
    content=content+"</a>";
    return content;
}
/**
 * Form elkuldes megerositese
 * @param pOKFormID megerosites eseten a link href-e
 * @param pMsg megjelenitendo szoveges uzenet
 */

function popUPForm(pOKFormID,pMsg){
    var content="<img src=\""+webapp+"/imgs/comment_64.png\" /><br/>";
    content=content+pMsg+"<br/>";
    content =content+"<a href=\"#\" style=\"width:70px;float:left;display:block;text-align:center;\"  onclick=\"javascript:document.getElementById('"+pOKFormID+"').submit();\">";
    content=content+"<img src=\""+webapp+"/imgs/accept_64.png\" /><br/>";
    content=content+popUP_OK;
    content=content+"</a>"
    content=content+"<a href=\"#\" style=\"width:70px;float:left;display:block;text-align:center;\" onclick=\"TINY.box.hide();\">";
    content=content+"<img src=\""+webapp+"/imgs/remove_64.png\" /><br/>";
    content=content+popUP_NO;
    content=content+"</a>";
    return content;
}

function popUPNewForm(pFormUrl,pHTML){
    var content="<img src=\""+webapp+"/imgs/comment_64.png\" /><br/>";
    content=content+"<form action=\""+pFormUrl+"\" method=\"post\" >";
    content=content+pHTML;
    content=content+"<input type=\"image\"src=\""+webapp+"/imgs/accept_64.png\" /><br/>";
    content=content+"<a href=\"#\" style=\"width:70px;float:left;display:block;text-align:center;\" onclick=\"TINY.box.hide();\">";
    content=content+"<img src=\""+webapp+"/imgs/remove_64.png\" /><br/>";
    content=content+"</a>";
    return content;
}


/**
 * Linkre kattintas megerositese
 * @param pOKlink megerosites eseten a link href-e
 * @param pMsg megjelenitendo szoveges uzenet
 */
function popUPDelete(pOKlink,pMsg){
    var content="<img src=\""+webapp+"/imgs/comment_64.png\" /><br/>";
    content=content+pMsg+"<br/>";
    content =content+"<a href=\""+pOKlink+"\" class=\"icolink\">";
    content=content+"<img src=\""+webapp+"/imgs/accept_64.png\" /><br/>";
    content=content+popUP_OK;
    content=content+"</a>&nbsp;&nbsp;&nbsp;"
    content=content+"<a href=\"#\" class=\"icolink\" onclick=\"TINY.box.hide();\">";
    content=content+"<img src=\""+webapp+"/imgs/remove_64.png\" /><br/>";
    content=content+popUP_NO;
    content=content+"</a>";
    return content;
}

