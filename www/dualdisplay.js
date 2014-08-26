
var dualdisplay ={
	
	send: function(msg,successCallback, errorCallback){
		try{
//			console.log('****Message**** '+msg);
			cordova.exec(successCallback, errorCallback, "DualDisplay", "send", [msg]);
//			console.log('****DualDisplay Send****');
		} catch(e){
			console.log('DualDisplay.js[exception]'+e.name+' : '+e.message);
		}
	},
	bindDisplay2Listener:function(listener){
		try{
			cordova.exec(listener, listener, "DualDisplay", "ACTION_BIND_LISTENER", []);
//			console.log('****DualDisplay bindDisplay2Listener****');
		} catch(e){
			console.log('DualDisplay.js[exception].bindDisplay2Listener: '+e.name+' : '+e.message);
		}
	}
}
module.exports = dualdisplay;