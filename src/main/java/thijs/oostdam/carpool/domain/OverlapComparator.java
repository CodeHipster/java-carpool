package thijs.oostdam.carpool.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class OverlapComparator {

    /**
     * Compare stops with existing trips for having overlap
     *
     * expects a trip to have atleast 1 stop.
     */
    public static boolean overlap(Collection<Stop> stops, Collection<Trip> existingTrips){
        Preconditions.checkArgument(stops.size() > 1);
        //TODO: optimize, stream only once.
        Instant from = stops.stream().min(Comparator.comparing(Stop::departure)).get().departure();
        Instant to = stops.stream().max(Comparator.comparing(Stop::departure)).get().departure();
        Range<Instant> tripDuration = Range.closed(from, to);

        Optional<Trip> overlappingTrip = existingTrips.stream().filter(t -> {
            //TODO: optimize, stream only once.
            Instant existingFrom = t.stops().stream().min(Comparator.comparing(Stop::departure)).get().departure();
            Instant existingTo = t.stops().stream().max(Comparator.comparing(Stop::departure)).get().departure();
            Range<Instant> existingTripDuration = Range.closed(existingFrom, existingTo);
            return tripDuration.isConnected(existingTripDuration);
        }).findAny();

        return overlappingTrip.isPresent();
    }

    /**
     * Compare trips for having overlap
     *
     * expects a trip to have atleast 1 stop.
     */
    public static boolean overlap(Trip trip, Collection<Trip> existingTrips){
        Collection<Stop> stops = trip.stops();
        return OverlapComparator.overlap(stops, existingTrips);
    }

}
