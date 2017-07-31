(function () {//IIFE

    // initialize
    $(function () {
        refreshTrips();

        //register buttons.
        $("#addTrip").click(postNewTrip);
        $("#addStop").click(addStopInput);
        $("#listTrips").click(refreshTrips);
    });

    // add another stop input block
    var addStopInput = function(){
        var newStopInput = $('#stopInputTemplate .stop').clone();
        $(newStopInput).insertAfter('#trip .stops .stop:last');
    };

    // remove trips from html, download from server and render trips.
    var refreshTrips = function(){
        $.get("/trips", function (data) {
            var trips = JSON.parse(data);
            //clear list
            $("#trips").empty();
            //add items to list
            trips.forEach(function(trip){
                var newTrip = $("#tripTemplate").clone();
                newTrip.removeAttr('id');
                $(".name", newTrip).text(trip.driver.name);
                $(".email", newTrip).text(trip.driver.email);
                $(".passengers", newTrip).text(trip.maxPassengers);
                trip.stops.forEach(function(stop){
                    console.log("stop: ", stop);
                    var newStop = $("#stopTemplate .stop").clone();
                    $(".latitude", newStop).text(stop.latitude);
                    $(".longitude", newStop).text(stop.longitude);
                    $(".departure", newStop).text(stop.departure);
                    $(".stops", newTrip).append(newStop);
                });
                $("#trips").append(newTrip);
            });
        });
    };

    // parse new trip input and send to server.
    var postNewTrip = function () {
        var trip = {
            driver: {
                email: $("#trip .email").val(),
                name: $("#trip .name").val()
            },
            maxPassengers: $("#trip .passengers").val(),
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

})(); //IIFE
