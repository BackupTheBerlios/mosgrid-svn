<html>
<head>
<style type="text/css">
/*----------------------jqDoc -fisheye-------------*/
/*position and hide the menu initially - leave room for menu items to expand...*/
#page {padding-top:160px; padding-bottom:20px; width:100%;}
#menu {position:absolute; top:0; left:0; width:100%; display:none;}
/*dock styling...*/
/*...centre the dock...*/
#menu div.jqDockWrap {margin:0 auto;}
/*...set the cursor...*/
#menu div.jqDock {cursor:pointer;}
/*menu element styling...*/
#menu a {background-color:#666666;}
#menu a:hover {background-color:#00ff99;}
/*label styling...*/
div.jqDockLabel {color:#ffffff; cursor:pointer; top:0; left:0; width:100%; height:90%; overflow:hidden;}
.myLabel {position:relative;}
.myLabel h4 {font-size:1em; line-height:1.25em; font-weight:bold; margin:1.25em 0; text-align:center; text-decoration:none;}
/*...only show the paragraph when the cursor is on the menu item...*/
.myLabel p {display:none; font-size:10px; padding:0 10px;}
a:hover .myLabel p {display:block;}

</style>

<script type='text/javascript' src="${pageContext.request.contextPath}/js/jquery/jquery-1.7.2.min.js"></script>
<script type='text/javascript' src="${pageContext.request.contextPath}/js/jquery/jquery.jqdock.min.js"></script>


<script>
jQuery(document).ready(function($){
  // set up the options to be used for jqDock...
  var dockOptions =
      //IMPORTANT! Do NOT enable the labels option or this won't work!
      //ALSO, this will only work since v1.7, and for v1.7 it will ONLY work with
      //images inside anchors!
      { align: 'top' // horizontal menu, with expansion DOWN from a fixed TOP edge
      , size: 60 //increase 'at rest' size to 60px
      , setLabel: function(t, i, el){  //NB : el is div.jqDockLabel
          //Since I can't enable labels I need to set the css myself (see the 
          //styles for div.jqDockLabel above), and the position of the label is 
          //going to have to be purely dependent on that styling because jqDock
          //won't do any calculating for me.
          //split the text on bar, wrap it up, append it to div.jqDockLabel, and
          //show the label...
          t = t.split('|');
          $('<div class="myLabel"><h4>' + t[0] + '</h4><p>' + t[1] + '</p></div>')
            .appendTo($(el).show());
          //I've set the text content myself so...
          return false;
        }
      };
  // ...and apply...
  $('#menu').jqDock(dockOptions);
});

</script>
</head>

<body>
<div id='page'>
  <div id='menu'>
    <a href='#'><img src='http://www.wizzud.com/jqDock/images/empty.png' title='Alpha|Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent at lorem diam, tincidunt feugiat risus.' alt='' /></a>
    <a href='#'><img src='http://www.wizzud.com/jqDock/images/empty.png' title='Bravo|Maecenas bibendum mauris ut dui semper ullamcorper. Donec vitae leo nec lectus accumsan ornare imperdiet vel dui.' alt='' /></a>
    <a href='#'><img src='http://www.wizzud.com/jqDock/images/empty.png' title='Charlie|Donec fermentum accumsan enim quis iaculis. Vestibulum aliquet vulputate ultrices. Nunc imperdiet elit at quam viverra bibendum.' alt='' /></a>
    <a href='#'><img src='http://www.wizzud.com/jqDock/images/empty.png' title='Delta|Nulla pretium tortor et nulla sodales adipiscing. Aliquam erat volutpat. Nunc a erat eu lectus molestie cursus consequat sed massa.' alt='' /></a>
    <a href='#'><img src='http://www.wizzud.com/jqDock/images/empty.png' title='Echo|Nullam elementum viverra felis id volutpat. Pellentesque nisi diam, venenatis in ullamcorper id, ultricies sed erat.' alt='' /></a>
  </div>
</div>

</body>
</html>