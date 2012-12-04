
var $ = function (id) {
    return document.getElementById(id);
};


function isNumber( value )
{
    return (parseFloat(value)==value);    
}


var isInteger = function (value) {
    return (parseInt(value)==value);
};


function getElementsByClass(className) {
  var all = document.all ? document.all : document.getElementsByTagName('*');
  var elements = [];
  for (var e = 0; e < all.length; e++) {
    if (all[e].className == className) {
	elements[elements.length] = all[e];
    }
  }
  return elements;
}


function hideAllErrors() { 
    //$$('[class=error]').each(function (item) {item.hide();});
    var elements = getElementsByClass("error");
    for (var i = 0; i < elements.length; i++) {
	elements[i].style.display = "none";   
    }
}


var setRadioButton = function (id, value) {
    if (!$(id+ "_"+ value))
        return false;
    DEBUG.radioButton = DEBUG.radioButton || new Array();
    DEBUG.radioButton.push({id:id, value:value});
    $(id+ "_"+ value).checked = true;
    return true;
};


function enableField(source, target) {
  hideAllErrors();
  if ($(source) && $(target))
     $(target).disabled = ($(source).checked === true)?false:true;
  else 
     return false;
  return true;
}

function saveForm(pvl) {
    var save = "";
    DEBUG.element=new Array();
    for (var key in document.forms[1].elements) {
	if (document.forms[1].elements.hasOwnProperty(key)) {
    	    var element = document.forms[1].elements[key];
    	    DEBUG.element.push(element);
    	    if (typeof(element.id) !== "undefined" &&
        	element.id !== ""  && typeof(element.value) !== "undefined"  && 
        	element.value !== "" && element.id !== "save" &&
		!element.disabled) {
        	if (element.type == 'radio') {
        	    if (element.checked) {
            		// we use name instead of id here !!!
			save = save + element.name + " = " + element.value + "\n";
        	    }
        	} else if (element.type == 'textarea' || element.type == 'text') {
            	    var txt = element.value;
            	    save = save + element.id + " = " + txt.replace('\n', ' ') + '\n'; 
        	} else if (element.type == 'checkbox' && element.className != 'group') {
            	    if (element.checked) {
    	    		save = save + element.id + " = " + "1" + "\n";
  	            } else {
            		save = save + element.id + " = " + "0" + "\n";
            	    }
        	} else if (element.type=='hidden' && element.className=='disabledCheckboxValue' && element.id.indexOf("_value")!=0) {
		  // create a hidden field with classname "disabledCheckboxValue" and id "%ID_OF_CHECKBOX%_value" to post the value  	
		  save += element.id.replace(/_value/, "")+ " = "+ element.value+ "\n"; 
		}
    	    } else if (element.disabled && (element.type == 'textarea' || element.type == 'text')) {
        	save = save + "#" + element.id + " = " + element.value + "\n";
    	    } else if (element.type=="hidden" && element.className=="includeText") {
		  save += element.value + "\n";
	    }	    
        }
    }

    $(pvl).value = save;
}


var clearForm = function (plv) {
    $(plv).value = "";
};


function cmol3dConf() {
	hideAllErrors();
	if ($("CONF_SEARCH").checked === false && 
	    $("USE2D").checked === false) {
		$("CONF_SEARCH_ERROR").style.display = "block";
	}
	if ($("CONF_SEARCH").checked === false) {
		$("POP_SIZE").disabled = true;
		$("NUMBER_OF_POP_INITIALIZERS").disabled = true;
		$("ENERGY_WINDOW").disabled = true;
		$("MAX_STRUCTURES").disabled = true;
		$("MAX_DISTANCE").disabled = true;
		$("MAX_USAGE").disabled = true;
		$("STEP_SIZE").disabled = true;
		$("MAX_MOVE").disabled = true;
		$("NUMBER_OF_EIGENVECTORS").disabled = true;
	} else {
		$("POP_SIZE").disabled = false;
		$("NUMBER_OF_POP_INITIALIZERS").disabled = false;
		$("ENERGY_WINDOW").disabled = false;
		$("MAX_STRUCTURES").disabled = false;
		$("MAX_DISTANCE").disabled = false;
		$("MAX_USAGE").disabled = false;
		$("STEP_SIZE").disabled = false;
		$("MAX_MOVE").disabled = false;
		$("NUMBER_OF_EIGENVECTORS").disabled = false;
	}
	return true;
}

var DEBUG=DEBUG || {};

function loadForm0(src) {
  var source = $(src).value;
  if (source == "none") {
    return true;
  }
  var pairs = source.split('\n');
  var length = pairs.length; 
  DEBUG.length = length;
  DEBUG.left = DEBUG.left || {};
  DEBUG.right = DEBUG.right || {};
  DEBUG.which = DEBUG.which || new Array();
  for (var i = 0; i < length; i++) {
    var element = pairs[i];
    var pos = element.search(" = ");
    var left = element.substring(0, pos);
    var right = element.substring(pos+3);
    DEBUG.left[i] = left;
    DEBUG.right[i] = right;    
    var which = (left.charAt(0)=='#')?$(left.substr(1)):$(left);
    DEBUG.which.push(which);
    
    if (which) {
	if (which.type === "checkbox" && which.className != 'group') {
	    which.checked = (right==1)?true:false;
	} else if ((which.type === "textarea" || which.type === "text") && which.className == 'group') {
	    var c = left.charAt(0);
	    var group = getElementsByClass('group');
	    if (group.length > 0)
	       group[0].checked = (c=='#')?false:true;
	    which.value=right;
	} else if (which.type === 'radio') {
            setRadioButton(which.name, right);
	} else {
	    which.value = right;
	    if (left.charAt(0) == '#' && which.type === "text") {
	       which.disabled='disabled';
	    }
       }
    }
  } // for
  if ($("cmol3d") !== null)
    cmol3dConf();
  enableField('enableKey1', 'KEYWORDS1');
  enableField('enableVar', 'VARIANCE_LIMIT_FOR_DESCRIPTOR_DELETION');
  return true;
}


function loadForm(src) {
    var src_ = src;
    var loaderHelper = function () {
	if($("cgridcheck") === null)
	    setTimeout(loaderHelper, 30);
	else 
	    loadForm0(src_);
    };
    setTimeout(loaderHelper, 30);
}

var showError = function (id) {
    $(id+ "_ERROR").style.display = "block";
    $(id).select();
    $(id).focus();    
};


function checkFormCmol3d(pvl) {
  var population = $("POP_SIZE").value;
  var pop_init = $("NUMBER_OF_POP_INITIALIZERS").value; 
  var energy = $("ENERGY_WINDOW").value;
  var structures = $("MAX_STRUCTURES").value;  
  var distance = $("MAX_DISTANCE").value;  
  var perturbations = $("MAX_USAGE").value;
  var step = $("STEP_SIZE").value;
  var move = $("MAX_MOVE").value;   
  var eigen = $("NUMBER_OF_EIGENVECTORS").value;  
  hideAllErrors();
  var isValid = true;
  if (!isInteger(population)) {
    showError("POP_SIZE");
    isValid=false; 
  }
  if (!isInteger(pop_init) || parseInt(pop_init) < parseInt(population)) {
    showError("NUMBER_OF_POP_INITIALIZERS");
    isValid=false; 
  } 
  if (!isNumber(energy) || parseFloat(energy) <= 0) {
    showError("ENERGY_WINDOW");
    isValid=false; 
  } 
  if (!isInteger(structures) || parseInt(structures) <= 0 || parseInt(structures) > 100) {
    showError("MAX_STRUCTURES");
    isValid=false; 
  } 
  if (!isNumber(distance) || parseFloat(distance) <= 0 ) {
    showError("MAX_DISTANCE");
    isValid=false;     
  } 
  if (!isInteger(perturbations) || parseInt(perturbations) <= 0) {
    showError("MAX_USAGE");
    isValid=false; 
  } 
  if (!isNumber(step) || parseFloat(step) <= 0) {
    showError("STEP_SIZE");
    isValid=false; 
  } 
  if (!isNumber(move) || parseFloat(move) <= 0) {
    showError("MAX_MOVE");
    isValid=false; 
  } 
  if (!isInteger(eigen) || parseInt(eigen) <= 0) {
    showError("NUMBER_OF_EIGENVECTORS");
    isValid=false; 
  } 
  if (isValid)
    saveForm(pvl);
  return isValid;
}

function checkFormScreening_Cmol3d(pvl) {
  var population = $("POP_SIZE").value;
  var pop_init = $("NUMBER_OF_POP_INITIALIZERS").value; 
  var energy = $("ENERGY_WINDOW").value;
  var structures = $("MAX_STRUCTURES").value;  
  var distance = $("MAX_DISTANCE").value;  
  var perturbations = $("MAX_USAGE").value;
  var step = $("STEP_SIZE").value;
  var move = $("MAX_MOVE").value;   
  var eigen = $("NUMBER_OF_EIGENVECTORS").value;  
  hideAllErrors();
  var isValid = true;
  if (!isInteger(population)) {
    showError("POP_SIZE");
    isValid=false; 
  }
  if (!isInteger(pop_init) || parseInt(pop_init) < parseInt(population)) {
    showError("NUMBER_OF_POP_INITIALIZERS");
    isValid=false; 
  } 
  if (!isNumber(energy) || parseFloat(energy) <= 0) {
    showError("ENERGY_WINDOW");
    isValid=false; 
  } 
  if (!isInteger(structures) || parseInt(structures) <= 0 || parseInt(structures) > 10) {
    showError("MAX_STRUCTURES");
    isValid=false; 
  } 
  if (!isNumber(distance) || parseFloat(distance) <= 0 ) {
    showError("MAX_DISTANCE");
    isValid=false;     
  } 
  if (!isInteger(perturbations) || parseInt(perturbations) <= 0) {
    showError("MAX_USAGE");
    isValid=false; 
  } 
  if (!isNumber(step) || parseFloat(step) <= 0) {
    showError("STEP_SIZE");
    isValid=false; 
  } 
  if (!isNumber(move) || parseFloat(move) <= 0) {
    showError("MAX_MOVE");
    isValid=false; 
  } 
  if (!isInteger(eigen) || parseInt(eigen) <= 0) {
    showError("NUMBER_OF_EIGENVECTORS");
    isValid=false; 
  } 
  if (isValid)
    saveForm(pvl);
  return isValid;
}


function checkFormFMT(pvl) {
  var variance = $("VARIANCE_LIMIT_FOR_DESCRIPTOR_DELETION").value;
  var isValid = true;
  hideAllErrors();
  if (!isNumber(variance) || variance*1 <= 0) {
    showError("VARIANCE_LIMIT_FOR_DESCRIPTOR_DELETION");
    isValid = false;
  }
  if (isValid)
     saveForm(pvl);
  return isValid;
}

function checkFormMDABMLR(pvl) {
  var fraction = $("Fraction of training set for leave-many-out crossvalidation").value;
  var crossvalid = $("Number of crossvalidation tests").value;
  var random = $("Number of randomization tests").value;
  var intercorr = $("Maximum descriptor intercorrelation").value;
  var corr2 = $("Maximum size of model pool for 2 parameter correlations").value;
  var corr3 = $("Maximum size of model pool for 3+ parameter correlations").value;
  var devel = $("Maximum number of descriptors for developed models").value;
  var outcorr = $("Number of correlations for output (per number of descriptors)").value;
  var isValid = true;
  hideAllErrors();
  if (!isNumber(fraction) || fraction*1 <= 0 || fraction*1 >= 1) {
    $("fractionError").style.display = "block";
    $("Fraction of training set for leave-many-out crossvalidation").select();
    $("Fraction of training set for leave-many-out crossvalidation").focus();
    isValid = false;
  } 
  if (!isNumber(crossvalid) || crossvalid*1 <= 0) {
    $("crossvalidError").style.display = "block";
    $("Number of crossvalidation tests").select();
    $("Number of crossvalidation tests").focus();
    isValid = false;
  } 
  if (!isNumber(random) || random*1 <= 0) {
    $("randomError").style.display = "block";
    $("Number of randomization tests").select();
    $("Number of randomization tests").focus();
    isValid = false;
  } 
  if (!isNumber(intercorr) || intercorr*1.0 <= 0 || intercorr*1.0 >= 1) {
    $("intercorrError").style.display = "block";
    $("Maximum descriptor intercorrelation").select();
    $("Maximum descriptor intercorrelation").focus();
    isValid = false;
  }
  if (!isNumber(corr2) || corr2*1 <= 0) {
    $("corr2Error").style.display = "block";
    $("Maximum size of model pool for 2 parameter correlations").select();
    $("Maximum size of model pool for 2 parameter correlations").focus();
    isValid = false;
  }
  if (!isNumber(corr3) || corr3*1 <= 0) {
    $("corr3Error").style.display = "block";
    $("Maximum size of model pool for 3+ parameter correlations").select();
    $("Maximum size of model pool for 3+ parameter correlations").focus();
    isValid = false;
  }
  if (!isNumber(devel) || devel*1 <= 0) {
    $("develError").style.display = "block";
    $("Maximum number of descriptors for developed models").select();
    $("Maximum number of descriptors for developed models").focus();
    isValid = false;
  }
  if (!isNumber(outcorr) || outcorr*1 <= 0) {
    $("outcorrError").style.display = "block";
    $("Number of correlations for output (per number of descriptors)").select();
    $("Number of correlations for output (per number of descriptors)").focus();
    isValid = false;
  }
  if (isValid)
     saveForm(pvl);
  return isValid;   
}


function checkFormMDASimple(pvl) {
  var fraction = $("Fraction of training set for leave-many-out crossvalidation").value;
  var crossvalid = $("Number of crossvalidation tests").value;
  var random = $("Number of randomization tests").value;
  var isValid = true;
  hideAllErrors();
  if (!isNumber(fraction) || fraction*1 <= 0 || fraction*1 >= 1) {
    $("fractionError").style.display = "block";
    $("Fraction of training set for leave-many-out crossvalidation").select();
    $("Fraction of training set for leave-many-out crossvalidation").focus();
    isValid = false;
  } 
  if (!isNumber(crossvalid) || crossvalid*1 <= 0) {
    $("crossvalidError").style.display = "block";
    $("Number of crossvalidation tests").select();
    $("Number of crossvalidation tests").focus();
    isValid = false;
  }
  if (!isNumber(random) || random*1 <= 0) {
    $("randomError").style.display = "block";
    $("Number of randomization tests").select();
    $("Number of randomization tests").focus();
    isValid = false;
  }
  if (isValid)
     saveForm(pvl);
  return isValid;
}


function checkFormMopac(pvl) {
    saveForm(pvl);
    return true;
}


var checkFormScreening = function (pvl) {
    var retval =  checkFormScreening_Cmol3d(pvl) && checkFormMopac(pvl) && checkFormFMT(pvl);
    //alert($(pvl).value);   
    return retval;
}

var selectMdaAlgorithm = function (algorithm) {
    switch (algorithm) {
    case "BMLR":
  	$('Fraction of training set for leave-many-out crossvalidation').disabled = false;
	$('Number of crossvalidation tests').disabled = false;
	$('Number of randomization tests').disabled = false;
	$('Maximum descriptor intercorrelation').disabled = false;
	$('Maximum size of model pool for 2 parameter correlations').disabled = false;
	$('Maximum size of model pool for 3+ parameter correlations').disabled = false;
	$('Maximum number of descriptors for developed models').disabled = false;
	$('Number of correlations for output (per number of descriptors)').disabled = false;
        break;
    case "SIMPLE":
    default:
  	$('Fraction of training set for leave-many-out crossvalidation').disabled = false;
	$('Number of crossvalidation tests').disabled = false;
	$('Number of randomization tests').disabled = false;
	$('Maximum descriptor intercorrelation').disabled = true;
	$('Maximum size of model pool for 2 parameter correlations').disabled = "disabled";
	$('Maximum size of model pool for 3+ parameter correlations').disabled = "disabled";
	$('Maximum number of descriptors for developed models').disabled = "disabled";
	$('Number of correlations for output (per number of descriptors)').disabled = "disabled";
    }
};


var loadDefaults = function (formName) {
    switch (formName) {
    case 'SCREENING-CMOL3D':
    	 hideAllErrors();
    	 $('POP_SIZE').value = '100';
    	 $('NUMBER_OF_POP_INITIALIZERS').value = '200';
    	 $('ENERGY_WINDOW').value = '5.0';
    	 $('MAX_STRUCTURES').value = '10';
    	 $('MAX_DISTANCE').value = '0.25';    	 
    	 $('MAX_USAGE').value = '10';    	 
    	 $('STEP_SIZE').value = '0.5';    	 
    	 $('MAX_MOVE').value = '5.0';    	 
    	 $('NUMBER_OF_EIGENVECTORS').value = '10';
    	 $('CONF_SEARCH').checked = true ;
    	 $('USE2D').checked = true;
	 cmol3dConf();
	 break;
    case 'CMOL3D':
    	 hideAllErrors();
    	 $('POP_SIZE').value = '100';
    	 $('NUMBER_OF_POP_INITIALIZERS').value = '200';
    	 $('ENERGY_WINDOW').value = '5.0';
    	 $('MAX_STRUCTURES').value = '10';
    	 $('MAX_DISTANCE').value = '0.25';    	 
    	 $('MAX_USAGE').value = '10';    	 
    	 $('STEP_SIZE').value = '0.5';    	 
    	 $('MAX_MOVE').value = '5.0';    	 
    	 $('NUMBER_OF_EIGENVECTORS').value = '10';
    	 $('CONF_SEARCH').checked = true ;
    	 $('USE2D').checked = true;
	 cmol3dConf();
	 break;
    case 'MOPAC':
    	 hideAllErrors();
    	 $('KEYWORDS0').value = 'AM1 VECTORS BONDS PI POLAR PRECISE ENPART EF MMOK NOINTER T=1800';
    	 $('KEYWORDS1').value = 'AM1 FORCE MMOK PRECISE THERMO ROT=1 NOINTER';
    	 $('enableKey1').checked = false;
	 enableField('enableKey1', 'KEYWORDS1');
	 break;
    case 'FMT':
    	 hideAllErrors();
	 setRadioButton('MISSING_DATA_DELETION', 'DESCRIPTOR_WISE');
	 $('VARIANCE_LIMIT_FOR_DESCRIPTOR_DELETION').value = "1.0E-6";
    	 $('enableVar').checked = true;
	 enableField('enableVar', 'VARIANCE_LIMIT_FOR_DESCRIPTOR_DELETION');
    	 break;
    case 'MDA-BMLR':
    	 hideAllErrors();
	 selectMdaAlgorithm("BMLR");
	 $('METHOD_BMLR(2)').checked="checked";
	 $('Fraction of training set for leave-many-out crossvalidation').value = '0.8';
	 $('Number of crossvalidation tests').value = '1000';
	 $('Number of randomization tests').value = '1000';
	 $('Maximum descriptor intercorrelation').value = '0.5';
	 $('Maximum size of model pool for 2 parameter correlations').value = '1000000';
	 $('Maximum size of model pool for 3+ parameter correlations').value = '1000';
	 $('Maximum number of descriptors for developed models').value = '5';
	 $('Number of correlations for output (per number of descriptors)').value = '10';
    	 break;
    case 'MDA-SIMPLE':
    	 hideAllErrors();
	 $('Fraction of training set for leave-many-out crossvalidation').value = '0.8'
	 $('Number of crossvalidation tests').value = '1000';
	 $('Number of randomization tests').value = '1000';
	 break;
    case 'SCREENING':
    	 loadDefaults('SCREENING-CMOL3D');
	 loadDefaults('MOPAC');
	 loadDefaults('FMT');
    	 break;
    default:
    	 DEBUG.loadDefaults = "incorrect parameter given: '"+ formName+ "'";
    }
    return true;
};


