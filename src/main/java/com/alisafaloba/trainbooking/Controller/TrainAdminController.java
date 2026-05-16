package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Domain.Train;
import com.alisafaloba.trainbooking.Domain.Booking;
import com.alisafaloba.trainbooking.Repository.TrainRepository;
import com.alisafaloba.trainbooking.Repository.BookingRepository;
import com.alisafaloba.trainbooking.Service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map; // Make sure to import Map!

@RestController
@RequestMapping("/api/admin/trains")
public class TrainAdminController {

    private final TrainService trainService;
    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;

    public TrainAdminController(TrainService trainService, TrainRepository trainRepository, BookingRepository bookingRepository) {
        this.trainService = trainService;
        this.trainRepository = trainRepository;
        this.bookingRepository = bookingRepository;
    }

    @GetMapping
    public ResponseEntity<List<Train>> getAllTrains() {
        return ResponseEntity.ok(trainRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Train> createTrain(@RequestBody Train train) {
        return ResponseEntity.ok(trainRepository.save(train));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrain(@PathVariable Long id, @RequestBody Train trainDetails) {
        return trainRepository.findById(id).map(train -> {
            train.setName(trainDetails.getName());
            train.setCapacity(trainDetails.getCapacity());
            return ResponseEntity.ok(trainRepository.save(train));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrain(@PathVariable Long id) {
        if (!trainRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trainRepository.deleteById(id);
        return ResponseEntity.ok("Train deleted successfully.");
    }

    @PostMapping("/{trainId}/delay")
    public ResponseEntity<?> markTrainDelayed(@PathVariable Long trainId) {
        try {
            trainService.markTrainAsDelayed(trainId);
            return ResponseEntity.ok("Train marked as delayed. Customers notified.");
        } catch (IllegalArgumentException e) {
            // FIXED: Send errors as JSON Maps
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{trainId}/bookings")
    public ResponseEntity<?> getBookingsForTrain(@PathVariable Long trainId) {
        Train train = trainRepository.findById(trainId).orElse(null);

        if (train == null) {
            // FIXED: Send missing train error as JSON
            return ResponseEntity.badRequest().body(Map.of("error", "Train not found."));
        }

        List<Booking> trainBookings = bookingRepository.findByRouteTrain(train);

        if (trainBookings.isEmpty()) {
            // FIXED: Send zero-bookings alert as JSON so the frontend doesn't crash
            return ResponseEntity.ok(Map.of("message", "No bookings found for this train."));
        }

        return ResponseEntity.ok(trainBookings);
    }
}