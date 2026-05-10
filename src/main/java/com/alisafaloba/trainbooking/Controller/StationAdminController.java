package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Domain.Station;
import com.alisafaloba.trainbooking.Repository.StationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/stations")
public class StationAdminController {

    private final StationRepository stationRepository;

    public StationAdminController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        return ResponseEntity.ok(stationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createStation(@RequestBody Station station) {
        if (stationRepository.findByName(station.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Station already exists.");
        }
        return ResponseEntity.ok(stationRepository.save(station));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStation(@PathVariable Long id, @RequestBody Station stationDetails) {
        return stationRepository.findById(id).map(station -> {
            station.setName(stationDetails.getName());
            return ResponseEntity.ok(stationRepository.save(station));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Long id) {
        if (!stationRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        stationRepository.deleteById(id);
        return ResponseEntity.ok("Station deleted successfully.");
    }
}