$(document).ready(function() {
	$('.tooltip-div').tooltip();

  	$('.slider').slider({
		formater: function(value) {
			return 'Current value: ' + value;
		}
	});

	$(".clickableRow").click(function() {
            window.document.location = $(this).attr("href");
      });
}); 
