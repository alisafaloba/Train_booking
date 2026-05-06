package com.alisafaloba.trainbooking.Repository;

import com.alisafaloba.trainbooking.Domain.Route;
import com.alisafaloba.trainbooking.Domain.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {

    @Query("SELECT r FROM Route r JOIN r.routeStations rs1 JOIN r.routeStations rs2 " +
            "WHERE rs1.station = :departure AND rs2.station = :arrival " +
            "AND rs1.stationOrder < rs2.stationOrder")
    List<Route> findDirectRoutesBetweenStations(
            @Param("departure") Station departure,
            @Param("arrival") Station arrival
    );
}