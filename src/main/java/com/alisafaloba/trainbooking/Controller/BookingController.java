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

import java.util.List;

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

    // A simple DTO to receive JSON data cleanly
    public record BookingRequest(Long userId, Long routeId, Long departureStationId, Long arrivalStationId, int seats) {}

    @PostMapping
    public ResponseEntity<?> bookTicket(@RequestBody BookingRequest request) {
        try {
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Route route = routeRepository.findById(request.routeId())
                    .orElseThrow(() -> new IllegalArgumentException("Route not found"));
            Station departure = stationRepository.findById(request.departureStationId())
                    .orElseThrow(() -> new IllegalArgumentException("Departure station not found"));
            Station arrival = stationRepository.findById(request.arrivalStationId())
                    .orElseThrow(() -> new IllegalArgumentException("Arrival station not found"));

            Booking booking = bookingService.bookTicket(user, route, departure, arrival, request.seats());
            return ResponseEntity.ok(booking);

        } catch (IllegalStateException e) {
            // Catches the overbooking error from the service layer[cite: 1, 2]
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
    @GetMapping("/my-tickets/{userId}")
    public ResponseEntity<?> getUserBookings(@PathVariable Long userId) {
        // Verify the user exists first
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        // Fetch and return their bookings
        List<Booking> myBookings = bookingRepository.findByCustomerId(userId);

        if (myBookings.isEmpty()) {
            return ResponseEntity.ok("You have no upcoming trips.");
        }

        return ResponseEntity.ok(myBookings);
    }
}