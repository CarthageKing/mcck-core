$(document).ready(() => {
	$("#loginSubmitBtn").click(() => {
		$("#basicInfoLoginForm").submit();
	});
	
	$("#username, #password").keypress(evt => {
		const key = evt.which || evt.key || evt.keyCode || 0;
		if (13 === key) {
			$("#basicInfoLoginForm").submit();
			return;
		}
	});
});