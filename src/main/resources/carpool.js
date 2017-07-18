(function(){//IIFE

    $(function() {
        $("#submitBtn").click(function(){
            $.post("/trip",createTrip(), function(){console.log("posted");})
            .done(function(){console.log("done");})
            .fail(function() {console.log( "fail" );})
            .always(function() {console.log( "always" );});
        });

        $("#listBtn").click(function(){
            $.get("/trips", function(data){
                var trips = JSON.parse(data);
                var pretty = JSON.stringify(trips, undefined, 2)
                console.log("got trips", pretty);
                $("#trips").text(pretty);
            })
        });

    });



    var createTrip = function(){
        var trip = {
            maxPassengers : $("#passengers").val(),
            driver : {
                email : $("#email").val(),
                name: $("#name").val()
            },
            stops : [{
                departure : $("#stop1departure").val(),
                latitude : $("#stop1lat").val(),
                longitude : $("#stop1long").val()
            },{
                departure : $("#stop2departure").val(),
                latitude : $("#stop2lat").val(),
                longitude : $("#stop2long").val()
            }]
        };
        return JSON.stringify(trip);
    }



})() //IIFE
