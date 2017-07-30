(function () {//IIFE

    $(function () {
        $("#addTrip").click(function () {
            console.log("adding trip");
            $.post("/trip", getNewTripData(), function () {
                console.log("posted");
            })
                .done(function () {
                    console.log("done");
                })
                .fail(function () {
                    console.log("fail");
                })
                .always(function () {
                    console.log("always");
                });
        });

        $("#addStop").click(function () {
            //add new stop to list.
        });

        $("#listTrips").click(function () {
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
                    $(".passengers", newTrip).text(trip.driver.email);
                    trip.stops.forEach(function(stop){
                        console.log("stop: ", stop);
                        var newStop = $("#stopTemplate .stop").clone();
                        $(".latitude", newStop).text(stop.latitude);
                        $(".longitude", newStop).text(stop.longitude);
                        $(".departure", newStop).text(stop.departure);
                        $(".stops", newTrip).append(newStop);
                    });

                    //TODO: add rest of fields
                    $("#trips").append(newTrip);
                });
                var pretty = JSON.stringify(trips, undefined, 2);
                //console.log("got trips", pretty);
            })
        });
    });

    var getNewTripData = function () {
        var trip = {
            driver: {
                email: $("#trip .email").val(),
                name: $("#trip .name").val()
            },
            maxPassengers: $("#trip .passengers").val(),
            stops: []
        };

        var stops = $("#trip .stops .stop").each(function (index) {
            console.log(index + ": " + $(this).text());
            var stop = {
                latitude: $(".latitude", this).val(),
                longitude: $(".longitude", this).val(),
                departure: $(".departure", this).val()
            };
            trip.stops.push(stop);
        });
        var stringify = JSON.stringify(trip);
        return stringify;
    }
})(); //IIFE
