package com.alisafaloba.trainbooking.Config;

import com.alisafaloba.trainbooking.Domain.*;
import com.alisafaloba.trainbooking.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   StationRepository stationRepository,
                                   TrainRepository trainRepository,
                                   RouteRepository routeRepository,
                                   BookingRepository bookingRepository) { // Removed PasswordEncoder injection
        return args -> {
            System.out.println("Loading test data into H2 Database...");

            // Create the encoder directly here to avoid Autowiring issues
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            // 1. Create Users (with encrypted passwords)
            User admin = new User("admin@trains.com", passwordEncoder.encode("admin123"), Role.ADMIN);
            User customer1 = new User("john.doe@gmail.com", passwordEncoder.encode("password"), Role.CUSTOMER);
            User customer2 = new User("jane.smith@gmail.com", passwordEncoder.encode("password"), Role.CUSTOMER);
            userRepository.saveAll(List.of(admin, customer1, customer2));

            // 2. Create Stations
            Station london = new Station("London Kings Cross");
            Station york = new Station("York");
            Station edinburgh = new Station("Edinburgh Waverley");
            Station manchester = new Station("Manchester Piccadilly");
            Station glasgow = new Station("Glasgow Central");
            Station isolated1 = new Station("Nowhere Village");
            Station isolated2 = new Station("Ghost Town");
            stationRepository.saveAll(List.of(london, york, edinburgh, manchester, glasgow, isolated1, isolated2));

            // 3. Create Trains
            Train express = new Train("Flying Scotsman", 200);
            Train local = new Train("Northern Pacer", 50);
            Train tinyTrain = new Train("Micro Shuttle", 5);
            trainRepository.saveAll(List.of(express, local, tinyTrain));

            // 4. Create Routes & Schedules
            LocalDateTime today = LocalDateTime.now();

            // --- ROUTE 1: London -> York -> Edinburgh (Direct Route) ---
            Route route1 = new Route();
            route1.setTrain(express);
            RouteStation r1_stop1 = new RouteStation(route1, london, 1, today.plusHours(1), today.plusHours(1).plusMinutes(15));
            RouteStation r1_stop2 = new RouteStation(route1, york, 2, today.plusHours(3), today.plusHours(3).plusMinutes(10));
            RouteStation r1_stop3 = new RouteStation(route1, edinburgh, 3, today.plusHours(5), today.plusHours(5).plusMinutes(20));
            route1.setRouteStations(List.of(r1_stop1, r1_stop2, r1_stop3));
            routeRepository.save(route1);

            // --- ROUTE 2: York -> Manchester (Changeover Leg) ---
            Route route2 = new Route();
            route2.setTrain(local);
            RouteStation r2_stop1 = new RouteStation(route2, york, 1, today.plusHours(3).plusMinutes(50), today.plusHours(4));
            RouteStation r2_stop2 = new RouteStation(route2, manchester, 2, today.plusHours(5), today.plusHours(5).plusMinutes(10));
            route2.setRouteStations(List.of(r2_stop1, r2_stop2));
            routeRepository.save(route2);

            // --- ROUTE 3: Glasgow -> Edinburgh (Overbooking Test) ---
            Route route3 = new Route();
            route3.setTrain(tinyTrain);
            RouteStation r3_stop1 = new RouteStation(route3, glasgow, 1, today.plusHours(2), today.plusHours(2).plusMinutes(10));
            RouteStation r3_stop2 = new RouteStation(route3, edinburgh, 2, today.plusHours(3), today.plusHours(3).plusMinutes(10));
            route3.setRouteStations(List.of(r3_stop1, r3_stop2));
            routeRepository.save(route3);

            // 5. Create Pre-existing Bookings
            Booking booking1 = new Booking(customer1, 1, route1, london, edinburgh);
            Booking booking2 = new Booking(customer2, 4, route3, glasgow, edinburgh);
            bookingRepository.saveAll(List.of(booking1, booking2));

            System.out.println("Test data loaded successfully!");
        };
    }
}