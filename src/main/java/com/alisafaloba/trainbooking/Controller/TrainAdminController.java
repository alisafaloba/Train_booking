package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Domain.Train;
import com.alisafaloba.trainbooking.Domain.Booking;
import com.alisafaloba.trainbooking.Repository.TrainRepository;
import com.alisafaloba.trainbooking.Repository.BookingRepository;
import com.alisafaloba.trainbooking.Service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // --- NEW ENDPOINT: View all bookings for a specific train ---
    @GetMapping("/{trainId}/bookings")
    public ResponseEntity<?> getBookingsForTrain(@PathVariable Long trainId) {
        Train train = trainRepository.findById(trainId).orElse(null);

        if (train == null) {
            return ResponseEntity.badRequest().body("Train not found.");
        }

        List<Booking> trainBookings = bookingRepository.findByRouteTrain(train);

        if (trainBookings.isEmpty()) {
            return ResponseEntity.ok("No bookings found for this train.");
        }

        return ResponseEntity.ok(trainBookings);
    }
}