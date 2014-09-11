Dualscreen Phonegap Plugin
==========================

dualscreen Phonegap plugin

- Add Plugin and create android platform 
under android/asset add another www directory called www2 

The "www2" directory will hold the second screen html application.

Listening to second screen
==========================

- Add dualscreen listener in cordova onDeviceReady function 

function onDeviceReady(){
 ...
 	try{
		dualdisplay.bindDisplay2Listener(Display2EventsListener.onReceivedEvent);
		console.log('DualDisplay binding ...');
	}catch(e){
		console.log('[Exception] in binding dualdisplay event: '+e.name+' : '+e.message);
	}
...	
 }
 
//------ for Dual Display: setting event listener  ------------
var Display2EventsListener = (function() {

	this.onReceivedEvent = function(eventData) {
		console.log("onReceivedEvent :: eventData :: " + JSON.stringify(eventData));

		var eventHandler = function(){};

		switch (eventData.eventType){
			case "screenConnected":
				var displayName = eventData.message; 
				console.log("DualDisplay"+displayName+" is connected");
				//eventHandler = processScreenConnected;  // call to processScreenConnected function
				break;
			case "screenDisconnected":
				eventHandler = processScreenDisconnected; // call to processScreenDisconnected function
				break;
			case "screenReady":
				eventHandler = processScreenReady; 	// call to processScreenReady function
				break;	
			default: 
				console.log("onReceivedEvent :: unrecognized eventType");
		}
		eventHandler(eventData);
	};	
	return this;
}());

Sending commend to second screen (Second HTML application in www2)
==================================================================
 dualdisplay.send(<JAVASCRIPTcommend> );    //call this function from www

<JAVASCRIPTcommend> will be executed in www2 application.

  
 dualdisplay.send("alert('This is a message from www to www2')"); 

 
 