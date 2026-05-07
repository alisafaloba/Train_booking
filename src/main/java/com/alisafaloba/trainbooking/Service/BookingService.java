package com.alisafaloba.trainbooking.Service;

import com.alisafaloba.trainbooking.Domain.*;
import com.alisafaloba.trainbooking.Repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public BookingService(BookingRepository bookingRepository, EmailService emailService) {
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Booking bookTicket(User customer, Route route, Station departure, Station arrival, int requestedSeats) {

        // 1. Calculate remaining capacity
        int totalCapacity = route.getTrain().getCapacity();
        int currentlyBookedSeats = bookingRepository.sumBookedSeatsByRoute(route);
        int availableSeats = totalCapacity - currentlyBookedSeats;

        // 2. Prevent overbooking
        if (requestedSeats > availableSeats) {
            throw new IllegalStateException("Overbooking prevented: Only " + availableSeats + " seats remaining on this route.");
        }

        // 3. Create and save the booking
        Booking newBooking = new Booking(customer, requestedSeats, route, departure, arrival);
        Booking savedBooking = bookingRepository.save(newBooking);

        // 4. Send confirmation email
        emailService.sendBookingConfirmation(savedBooking);

        return savedBooking;
    }
}