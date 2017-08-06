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
     * expects a trip to have more than 1 stop.
     */
    public static boolean overlap(Collection<Stop> stops, Collection<Trip> existingTrips){
        Preconditions.checkArgument(stops.size() > 1);
        Range<Instant> tripDuration = getDuration(stops);

        Optional<Trip> overlappingTrip = existingTrips.stream().filter(t -> {
            Range<Instant> existingTripDuration = getDuration(t.stops());
            return tripDuration.isConnected(existingTripDuration);
        }).findAny();

        return overlappingTrip.isPresent();
    }

    /**
     * Compare trips for having overlap
     *
     * expects a trip to have more than 1 stop.
     */
    public static boolean overlap(Trip trip, Collection<Trip> existingTrips){
        Collection<Stop> stops = trip.stops();
        return OverlapComparator.overlap(stops, existingTrips);
    }

    /**
     * Check if a stop is between other stops.
     * @param newStop
     * @param existingStops
     * @return
     */
    public static boolean inBetween(Stop newStop, Collection<Stop> existingStops){
        return getDuration(existingStops)
                .contains(newStop.departure());
    }

    private static Range<Instant> getDuration(Collection<Stop> stops){
        //TODO: optimize, stream only once.
        Instant from = stops.stream().min(Comparator.comparing(Stop::departure)).get().departure();
        Instant to = stops.stream().max(Comparator.comparing(Stop::departure)).get().departure();
        return Range.closed(from, to);
    }
}
