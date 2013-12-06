<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Colorful Content Accordion | Tutorialzine demo</title>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/demo.css" />

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.easing.1.3.js"></script>

<script>
$(document).ready(function(){
	/* This code is executed after the DOM has been completely loaded */

	/* Changing thedefault easing effect - will affect the slideUp/slideDown methods: */
	$.easing.def = "easeOutBounce";

	/* Binding a click event handler to the links: */
	$('li.button a').click(function(e){
	
		/* Finding the drop down list that corresponds to the current section: */
		var dropDown = $(this).parent().next();
		
		/* Closing all other drop down sections, except the current one */
		$('.dropdown').not(dropDown).slideUp('slow');
		dropDown.slideToggle('slow');
		
		/* Preventing the default event (which would be to navigate the browser to the link's address) */
		e.preventDefault();
	})
	
});
</script>

</head>

<body>

<div id="main">

  <h1>Colorful Content Accordion, CSS &amp; jQuery</h1>
  
  <h2>View the <a href="http://tutorialzine.com/2009/12/colorful-content-accordion-css-jquery/">original tutorial &raquo;</a></h2>

  <ul class="container">
      <li class="menu">
      
          <ul>
		    <li class="button"><a href="#" class="green">Kiwis <span></span></a></li>

            <li class="dropdown">
                <ul>
                    <li><a href="#" onclick="$('.button a').eq(2).click();return false;">Open Grapes Section</a></li>
                    <li><a href="#" onclick="$('.dropdown').slideUp('slow');return false;">Close This Section</a></li>
                    <li><a href="http://en.wikipedia.org/wiki/Kiwifruit">Read on Wikipedia</a></li>
                    <li><a href="http://www.flickr.com/search/?w=all&amp;q=kiwi&amp;m=text">Flickr Stream</a></li>
                </ul>
			</li>

          </ul>
          
      </li>
      
      <li class="menu">
      
          <ul>
		    <li class="button"><a href="#" class="orange">Oranges <span></span></a></li>          	

            <li class="dropdown">
                <ul>
                    <li><a href="#" onclick="$('.button a:last').click();return false;">Open Last Section</a></li>
                    <li><a href="http://en.wikipedia.org/wiki/Orange_%28fruit%29">Wikipedia Page</a></li>
                    <li><a href="http://www.flickr.com/search/?w=all&amp;q=oranges&amp;m=text">Flickr Photos</a></li>
                </ul>
			</li>

          </ul>
          
      </li>

      
      <li class="menu">
      
          <ul>
		    <li class="button"><a href="#" class="blue">Grapes <span></span></a></li>

            <li class="dropdown">
                <ul>
                    <li><a href="http://en.wikipedia.org/wiki/Grapes">Wiki page</a></li>
                    <li>Text label 1</li>
                    <li>Text label 2</li>
                    <li><a href="http://www.flickr.com/search/?w=all&amp;q=grapes&amp;m=text">Flickr Stream</a></li>
                </ul>
			</li>

          </ul>
          
      </li>

    
      <li class="menu">
      
          <ul>
		    <li class="button"><a href="#" class="red">Strawberries <span></span></a></li>

            <li class="dropdown">
                <ul>
                    <li><a href="http://en.wikipedia.org/wiki/Strawberry">Wiki page</a></li>
                    <li><a href="http://www.flickr.com/photos/mojeecat/368540120/">Strawberry Pie</a></li>
                    <li><a href="http://www.flickr.com/search/?w=all&amp;q=strawberries&amp;m=text">Photo Stream</a></li>
                </ul>
			</li>

          </ul>
          
      </li>
  </ul>

<div class="clear"></div>

</div>
    <p>This is a tutorialzine demo. View the <a href="http://tutorialzine.com/2009/12/colorful-content-accordion-css-jquery/">original tutorial</a>, or download the <a href="demo.zip">source files</a>.</p>
</body>
</html>
