package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Domain.Booking;
import com.alisafaloba.trainbooking.Domain.Route;
import com.alisafaloba.trainbooking.Domain.Station;
import com.alisafaloba.trainbooking.Domain.User;
import com.alisafaloba.trainbooking.Repository.BookingRepository;
import com.alisafaloba.trainbooking.Repository.RouteRepository;
import com.alisafaloba.trainbooking.Repository.StationRepository;
import com.alisafaloba.trainbooking.Repository.UserRepository;
import com.alisafaloba.trainbooking.Service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final BookingRepository bookingRepository;

    public BookingController(BookingService bookingService, UserRepository userRepository,
                             RouteRepository routeRepository, StationRepository stationRepository, BookingRepository bookingRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
        this.bookingRepository = bookingRepository;
    }

    // 1. Create a sub-record to hold specific details for each train leg
    public record LegRequest(Long routeId, Long departureStationId, Long arrivalStationId) {}

    // 2. The main request now takes a List of these legs
    public record BookingRequest(List<LegRequest> legs, int seats) {}

    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest request, java.security.Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(401).body(Map.of("error", "You must be logged in to book tickets."));
            }

            User user = userRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalArgumentException("User profile not found"));

            List<Booking> confirmedBookings = new ArrayList<>();

            // 3. Loop through each leg and use its SPECIFIC departure/arrival stations
            for (LegRequest leg : request.legs()) {
                Route route = routeRepository.findById(leg.routeId())
                        .orElseThrow(() -> new IllegalArgumentException("Route not found"));
                Station departure = stationRepository.findById(leg.departureStationId())
                        .orElseThrow(() -> new IllegalArgumentException("Departure station not found"));
                Station arrival = stationRepository.findById(leg.arrivalStationId())
                        .orElseThrow(() -> new IllegalArgumentException("Arrival station not found"));

                Booking booking = bookingService.bookTicket(user, route, departure, arrival, request.seats());
                confirmedBookings.add(booking);
            }

            return ResponseEntity.ok(confirmedBookings);

        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<?> getUserBookings(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("error", "You must be logged in to view tickets."));
        }

        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("User profile not found."));

        List<Booking> myBookings = bookingRepository.findByCustomerId(user.getId());

        if (myBookings.isEmpty()) {
            return ResponseEntity.ok(Map.of("message", "You have no upcoming trips."));
        }

        return ResponseEntity.ok(myBookings);
    }
}