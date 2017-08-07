(function () {//IIFE

    // initialize
    $(function () {
        refreshTrips();

        //register buttons.
        $("#addTrip").click(postNewTrip);
        $("#extraStopInput").click(extraStopInput);
        $("#listTrips").click(refreshTrips);
    });

    // add another stop input block
    var extraStopInput = function(){
        var newStopInput = $('#stopInputTemplate .stop').clone();
        $(newStopInput).insertAfter('#trip .stops .stop:last');
    };

    // remove trips from page, download from server and add them to page
    var refreshTrips = function(){
        $.get("/trips", function (trips) {
            //console.log("/trips data: ", data);
            //var trips = JSON.parse(data);
            //clear list
            $("#trips").empty();
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
                    $(".departure", newStop).text(stop.departure);
                    //wire remove button
                    $(".removeStop", newStop).click(function(){
                        removeStop(stop.id);
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
                            departure: $(this).siblings(".departure").val()
                        }]
                    };
                    postNewStop(data);
                });
                $(".deleteTrip", htmlTrip).click(function(){
                    deleteTrip(trip.id);
                });
                $("#trips").append(htmlTrip);
            });
        });
    };

    var removePassenger = function(tripId, passengerId){
        $.delete("/trip/passenger?trip-id="+tripId+"&passenger-id="+ passengerId);
    }

    var removeStop = function(stopId){
        $.delete("/trip/stop?id="+stopId);
    };

    var deleteTrip = function(tripId){
        $.delete("/trip?id="+tripId);
    };

    var postNewPassenger = function(trip){
        var postData = JSON.stringify(trip);
        console.log("posting new passenger: ", postData);
        $.post("/trip/passenger", postData);
    };

    var postNewStop = function(trip){
        var postData = JSON.stringify(trip);
        console.log("posting new stop: ", postData);
        $.post("/trip/stop", postData);
    };

    // parse new trip input and send to server.
    var postNewTrip = function () {
        var trip = {
            driver: {
                email: $("#trip .email").val(),
                name: $("#trip .name").val()
            },
            maxPassengers: $("#trip .maxPassengers").val(),
            stops: []
        };

        $("#trip .stops .stop").each(function (index) {
            console.log(index + ": " + $(this).text());
            var stop = {
                latitude: $(".latitude", this).val(),
                longitude: $(".longitude", this).val(),
                departure: $(".departure", this).val()
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
