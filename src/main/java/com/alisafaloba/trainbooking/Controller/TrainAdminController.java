package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Service.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/trains")
public class TrainAdminController {

    private final TrainService trainService;

    public TrainAdminController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping("/{trainId}/delay")
    public ResponseEntity<?> markTrainDelayed(@PathVariable Long trainId) {
        try {
            trainService.markTrainAsDelayed(trainId);
            return ResponseEntity.ok("Train marked as delayed. All affected customers have been notified via email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}