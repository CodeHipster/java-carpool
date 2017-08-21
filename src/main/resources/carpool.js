(function () {//IIFE

    // initialize
    $(function () {
        refreshTrips();

        // register buttons.
        $("#addTrip").click(postNewTrip);
        $("#extraStopInput").click(extraStopInput);
        $("#listTrips").click(refreshTrips);
    });

    var closeMessage = function(){
        messageElement = $('#message');
        messageElement.slideUp(function(){messageElement.text("");});
    }

    var showMessage = function(message){
        messageElement = $('#message');
        messageElement.text(message).slideDown().click(closeMessage);
        // close automagically after 3 seconds.
        setTimeout(closeMessage, 5000);
    }

    // add another stop input block
    var extraStopInput = function(){
        var newStopInput = $('#stopInputTemplate .stop').clone();
        $(newStopInput).insertAfter('#trip .stops .stop:last');
    };

    // remove trips from page, download from server and add them to page
    var refreshTrips = function(){
        $.get("/trips", function (trips) {
            console.log("trips:", trips);
            $("#tripList .row").remove();
            //add items to list
            trips.forEach(function(trip){
                var htmlTrip = $("#tripTemplate").clone();
                htmlTrip.removeAttr('id');
                $(".name", htmlTrip).text(trip.driver.name);
                $(".email", htmlTrip).text(trip.driver.email);
                $(".maxPassengers", htmlTrip).text(trip.maxPassengers);
                trip.stops.forEach(function(stop){
                    console.log("stop: ", stop);
                    var newStop = $("#stopTemplate .stop").clone();
                    $(".latitude", newStop).text(stop.latitude);
                    $(".longitude", newStop).text(stop.longitude);
                    $(".departure", newStop).text(new Date(stop.departure).toLocaleString());
                    //wire remove button
                    $(".removeStop", newStop).click(function(){
                        removeStop(trip.id,stop.id);
                    });
                    $(".stops", htmlTrip).prepend(newStop);
                });
                trip.passengers.forEach(function(passenger){
                    console.log("passenger: ", passenger);
                    var newPassenger = $("#passengerTemplate .passenger").clone();
                    $(".name", newPassenger).text(passenger.name);
                    $(".email", newPassenger).text(passenger.email);
                    //wire remove button
                    $(".removePassenger", newPassenger).click(function(){
                        removePassenger(trip.id, passenger.id);
                    });
                    $(".passengers", htmlTrip).prepend(newPassenger);
                });
                //wire buttons
                $(".addPassenger", htmlTrip).click(function(){
                    var data = {
                        id: trip.id,
                        passengers: [{
                            name: $(this).siblings(".name").val(),
                            email: $(this).siblings(".email").val()
                        }]
                    };
                    postNewPassenger(data);
                });
                $(".addStop", htmlTrip).click(function(){
                    var data = {
                        id: trip.id,
                        stops: [{
                            latitude: $(this).siblings(".latitude").val(),
                            longitude: $(this).siblings(".longitude").val(),
                            departure: new Date($(this).siblings(".departure").val()).toISOString()
                        }]
                    };
                    postNewStop(data);
                });
                $(".deleteTrip", htmlTrip).click(function(){
                    deleteTrip(trip.id);
                });
                $("#tripList").append(htmlTrip);
            });
        });
    };

    var removePassenger = function(tripId, passengerId){
        $.delete("/trip/passenger?trip-id="+tripId+"&passenger-id="+ passengerId)
          .fail(function(jqXHR) {
            var error = JSON.parse(jqXHR.responseText);
            showMessage(error.message);
          });
    }

    var removeStop = function(tripId, stopId){
        $.delete("/trip/stop?trip-id="+tripId+"&stop-id=" + stopId)
          .fail(function(jqXHR) {
            var error = JSON.parse(jqXHR.responseText);
            showMessage(error.message);
          });
    };

    var deleteTrip = function(tripId){
        $.delete("/trip?id="+tripId)
          .fail(function(jqXHR) {
            var error = JSON.parse(jqXHR.responseText);
            showMessage(error.message);
          });
    };

    var postNewPassenger = function(trip){
        var postData = JSON.stringify(trip);
        console.log("posting new passenger: ", postData);
        $.post("/trip/passenger", postData)
          .fail(function(jqXHR) {
            var error = JSON.parse(jqXHR.responseText);
            showMessage(error.message);
          });
    };

    var postNewStop = function(trip){
        var postData = JSON.stringify(trip);
        console.log("posting new stop: ", postData);
        $.post("/trip/stop", postData)
          .fail(function(jqXHR) {
            var error = JSON.parse(jqXHR.responseText);
            showMessage(error.message);
          });
    };

    // parse new trip input and send to server.
    var postNewTrip = function () {
        var trip = {
            driver: {
                email: $("#newTrip .email").val(),
                name: $("#newTrip .name").val()
            },
            maxPassengers: $("#newTrip .maxPassengers").val(),
            stops: []
        };

        $("#newTrip .stops .stop").each(function (index) {
            console.log(index + ": " + $(this).text());
            var stop = {
                latitude: $(".latitude", this).val(),
                longitude: $(".longitude", this).val(),
                departure: new Date($(".departure", this).val()).toISOString()
            };
            trip.stops.push(stop);
        });

        $.post("/trip", JSON.stringify(trip));
    };

    /* Extend jQuery with function for DELETE requests. */
    function _ajax_request(url, data, callback, type, method) {
        if (jQuery.isFunction(data)) {
            callback = data;
            data = {};
        }
        return jQuery.ajax({
            type: method,
            url: url,
            data: data,
            success: callback,
            dataType: type
            });
    }

    jQuery.extend({
        delete: function(url, data, callback, type) {
            return _ajax_request(url, data, callback, type, 'DELETE');
        }
    });
})(); //IIFE


function initAutocomplete() {

    document.querySelectorAll('.address-input').forEach(function(node){
    console.log("creating autocomplete for node", node)
        var autocomplete = new google.maps.places.Autocomplete(
              (node),
              {types: ['geocode']});

        function fillInLocation() {
            console.log(arguments);
            var place = autocomplete.getPlace();
            console.log(place);

            if (!place.geometry) {
                // User entered the name of a Place that was not suggested and
                // pressed the Enter key, or the Place Details request failed.
                console.log("No details available for input: '" + place.name + "'");
                return;
            }

            node.parentNode.querySelector(".latitude").value = place.geometry.location.lat();
            node.parentNode.querySelector(".longitude").value = place.geometry.location.lng();
            console.log(place.geometry.location.lat());
            console.log(place.geometry.location.lng());
        }

        autocomplete.addListener('place_changed', fillInLocation);
    });
}
