$(document).ready(function () {
    $('#submit').click(function () {
        let forecastText = document.getElementById('data');
        forecastText.innerText = "";
        let queriedCity = document.getElementById('queriedCity').value;
        
        $.ajax({
            url: '/forecast/forecast',
            type: 'GET',
            data: { "queriedCity": queriedCity },
            contentType: false,
            success: function (data) {
                forecastText.innerText = data;
            }
        });
    });
});