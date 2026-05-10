package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Domain.Route;
import com.alisafaloba.trainbooking.Domain.RouteStation;
import com.alisafaloba.trainbooking.Domain.Station;
import com.alisafaloba.trainbooking.Domain.Train;
import com.alisafaloba.trainbooking.Repository.RouteRepository;
import com.alisafaloba.trainbooking.Repository.StationRepository;
import com.alisafaloba.trainbooking.Repository.TrainRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin/routes")
public class RouteAdminController {

    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    public RouteAdminController(RouteRepository routeRepository, TrainRepository trainRepository, StationRepository stationRepository) {
        this.routeRepository = routeRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
    }

    // DTOs for clean JSON mapping
    public record RouteStationRequest(Long stationId, int stationOrder, LocalDateTime arrivalTime, LocalDateTime departureTime) {}
    public record RouteCreateRequest(Long trainId, List<RouteStationRequest> stations) {}

    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> createRoute(@RequestBody RouteCreateRequest request) {
        Train train = trainRepository.findById(request.trainId())
                .orElseThrow(() -> new IllegalArgumentException("Train not found"));

        Route newRoute = new Route();
        newRoute.setTrain(train);

        List<RouteStation> routeStations = new ArrayList<>();

        for (RouteStationRequest rsReq : request.stations()) {
            Station station = stationRepository.findById(rsReq.stationId())
                    .orElseThrow(() -> new IllegalArgumentException("Station not found: " + rsReq.stationId()));

            RouteStation routeStation = new RouteStation(newRoute, station, rsReq.stationOrder(), rsReq.arrivalTime(), rsReq.departureTime());
            routeStations.add(routeStation);
        }

        newRoute.setRouteStations(routeStations);

        // CascadeType.ALL on Route's routeStations list will automatically save the RouteStation entities
        return ResponseEntity.ok(routeRepository.save(newRoute));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable Long id) {
        if (!routeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        routeRepository.deleteById(id);
        return ResponseEntity.ok("Route and its station mappings deleted successfully.");
    }
}