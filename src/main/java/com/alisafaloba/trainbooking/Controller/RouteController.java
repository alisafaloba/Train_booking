package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Domain.Route;
import com.alisafaloba.trainbooking.Domain.Station;
import com.alisafaloba.trainbooking.Repository.StationRepository;
import com.alisafaloba.trainbooking.Service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;
    private final StationRepository stationRepository;

    public RouteController(RouteService routeService, StationRepository stationRepository) {
        this.routeService = routeService;
        this.stationRepository = stationRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchRoutes(@RequestParam Long departureId, @RequestParam Long arrivalId) {
        Station departure = stationRepository.findById(departureId)
                .orElseThrow(() -> new IllegalArgumentException("Departure station not found"));
        Station arrival = stationRepository.findById(arrivalId)
                .orElseThrow(() -> new IllegalArgumentException("Arrival station not found"));

        // 1. Try finding direct routes first
        List<Route> directRoutes = routeService.findDirectRoutes(departure, arrival);
        if (!directRoutes.isEmpty()) {
            return ResponseEntity.ok(directRoutes);
        }

        // 2. If no direct routes, try finding a changeover
        List<RouteService.JourneyPlan> changeoverRoutes = routeService.findChangeoverRoutes(departure, arrival);
        if (!changeoverRoutes.isEmpty()) {
            return ResponseEntity.ok(changeoverRoutes);
        }

        // 3. Appropriate error message if no link exists
        return ResponseEntity.badRequest().body("No possible link found between the selected stations.");
    }
}