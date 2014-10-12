$(document).ready(function() {

	$(document).on('vmousedown vmouseup', '.clickable', function (event){
	    //console.log(event.type);
	    var obj_id = $(this).attr("id");
	    if (event.type == "vmousedown") {
		change_IMG(obj_id);
	    }
	    if (event.type == "vmouseup") {
		change_IMG_back(obj_id);
	    }
/*	    if (event.type == "click") {
	    }*/
	});
});

//NO	$.mobile.loading( "show");
//NO	$.mobile.loading("hide");


//onclick function
//id01-iR-GUI-adv_ to 01_iR_GUI_adv_C

function change_IMG(obj_id) {
  var str = obj_id;
	str=str.substr(2, str.length-2);
  str='images/' + str + '_C.png';
  document.getElementById(obj_id).src = str;
}
function change_IMG_back(obj_id) {
  var str = obj_id;
	str=str.substr(2, str.length-2);
  str='images/' + str + '.png';
  document.getElementById(obj_id).src = str;
}


/*
function playSound(){
alert('lets sound');
try{
		var sound = new Audio("button-3.mp3");
		sound.play;
                }catch (e) {
		alert(e);
                    // Fail silently but show in F12 developer tools console
                     if(window.console && console.error("Error:" + e));
                }

alert('fin sound');
}*/

/*
function sendKey(command){
	$.mobile.loadPage( "http://dmt_send_key/" + command );
	return true;
}
*/
/*
function sendKey(toast) {
	Android.sendKeyToNmt(toast);
}
*/
