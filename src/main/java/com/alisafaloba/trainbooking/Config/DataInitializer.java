package com.alisafaloba.trainbooking.Config;

import com.alisafaloba.trainbooking.Domain.*;
import com.alisafaloba.trainbooking.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   StationRepository stationRepository,
                                   TrainRepository trainRepository,
                                   RouteRepository routeRepository) {
        return args -> {
            System.out.println("Loading test data into H2 Database...");

            // 1. Create Users
            User admin = new User("admin@trains.com", Role.ADMIN);
            User customer = new User("john.doe@gmail.com", Role.CUSTOMER);
            userRepository.saveAll(List.of(admin, customer));

            // 2. Create Stations
            Station london = new Station("London Kings Cross");
            Station york = new Station("York");
            Station edinburgh = new Station("Edinburgh Waverley");
            stationRepository.saveAll(List.of(london, york, edinburgh));

            // 3. Create a Train
            Train express = new Train("Flying Scotsman", 200);
            trainRepository.save(express);

            // 4. Create a Route (London -> York -> Edinburgh)
            Route route = new Route();
            route.setTrain(express);

            // 5. Create Route Stations (The schedule)
            LocalDateTime today = LocalDateTime.now();

            RouteStation stop1 = new RouteStation(route, london, 1,
                    today.plusHours(1), today.plusHours(1).plusMinutes(15));

            RouteStation stop2 = new RouteStation(route, york, 2,
                    today.plusHours(3), today.plusHours(3).plusMinutes(10));

            RouteStation stop3 = new RouteStation(route, edinburgh, 3,
                    today.plusHours(5), today.plusHours(5).plusMinutes(20));

            // Link the stops to the route.
            // Because Route has CascadeType.ALL, saving the Route will also save these stops!
            route.setRouteStations(List.of(stop1, stop2, stop3));
            routeRepository.save(route);

            System.out.println("Test data loaded successfully!");
        };
    }
}