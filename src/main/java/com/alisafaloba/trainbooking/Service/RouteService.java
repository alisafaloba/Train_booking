package com.alisafaloba.trainbooking.Service;

import com.alisafaloba.trainbooking.Domain.Route;
import com.alisafaloba.trainbooking.Domain.RouteStation;
import com.alisafaloba.trainbooking.Domain.Station;
import com.alisafaloba.trainbooking.Repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    // A DTO (Data Transfer Object) to hold the result of a changeover search
    public record JourneyPlan(Route firstLeg, Route secondLeg, Station transferStation) {}

    public List<Route> findDirectRoutes(Station departure, Station arrival) {
        return routeRepository.findDirectRoutesBetweenStations(departure, arrival);
    }

    public List<JourneyPlan> findChangeoverRoutes(Station departure, Station arrival) {
        List<JourneyPlan> possibleJourneys = new ArrayList<>();
        List<Route> allRoutes = routeRepository.findAll();

        // Find all routes that leave the departure station
        List<Route> departingRoutes = allRoutes.stream()
                .filter(r -> r.getRouteStations().stream().anyMatch(rs -> rs.getStation().equals(departure)))
                .toList();

        // Find all routes that go to the arrival station
        List<Route> arrivingRoutes = allRoutes.stream()
                .filter(r -> r.getRouteStations().stream().anyMatch(rs -> rs.getStation().equals(arrival)))
                .toList();

        // Look for intersecting stations between these routes
        for (Route leg1 : departingRoutes) {
            for (Route leg2 : arrivingRoutes) {
                if (leg1.equals(leg2)) continue; // Direct route, skip

                for (RouteStation rs1 : leg1.getRouteStations()) {
                    for (RouteStation rs2 : leg2.getRouteStations()) {

                        // If they share a station AND Leg 1 arrives BEFORE Leg 2 departs
                        if (rs1.getStation().equals(rs2.getStation()) &&
                                rs1.getArrivalTime().isBefore(rs2.getDepartureTime())) {

                            possibleJourneys.add(new JourneyPlan(leg1, leg2, rs1.getStation()));
                        }
                    }
                }
            }
        }
        return possibleJourneys;
    }
}