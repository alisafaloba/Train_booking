package com.alisafaloba.trainbooking.Repository;

import com.alisafaloba.trainbooking.Domain.Booking;
import com.alisafaloba.trainbooking.Domain.Route;
import com.alisafaloba.trainbooking.Domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {


    @Query("SELECT COALESCE(SUM(b.numberOfSeats), 0) FROM Booking b WHERE b.route = :route")
    int sumBookedSeatsByRoute(@Param("route") Route route);


    List<Booking> findByRouteTrain(Train train);
}