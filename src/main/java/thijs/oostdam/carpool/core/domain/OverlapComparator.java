package thijs.oostdam.carpool.core.domain;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import java.time.Instant;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class OverlapComparator {

    /**
     * Compare trips for having overlap
     *
     * expects a trip to have more than 1 stop.
     */
    public static boolean overlap(Trip trip, Collection<Trip> existingTrips){

        Range<Instant> tripDuration = Range.closed(trip.departure(), trip.arrival());

        Optional<Trip> overlappingTrip = existingTrips.stream().filter(t -> {
            Range<Instant> existingTripDuration = Range.closed(t.departure(), t.arrival());
            return tripDuration.isConnected(existingTripDuration);
        }).findAny();

        return overlappingTrip.isPresent();
    }
}
