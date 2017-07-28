(function () {//IIFE

    $(function () {
        $("#addTrip").click(function () {
            console.log("adding trip");
            $.post("/trip", getTripData(), function () {
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
                var pretty = JSON.stringify(trips, undefined, 2);
                console.log("got trips", pretty);
                $("#trips").text(pretty);
            })
        });

    });

    var getTripData = function () {
        var trip = {
            maxPassengers: $("#trip .passengers").val(),
            driver: {
                email: $("#trip .email").val(),
                name: $("#trip .name").val()
            },
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
        console.log(stringify);
        return stringify;
    }
})(); //IIFE
