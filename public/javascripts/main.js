$(document).ready(function() {
	$('.tooltip-div').tooltip();

  	$('.slider').slider({
		formater: function(value) {
			return 'Current value: ' + value;
		}
	});
}); 
