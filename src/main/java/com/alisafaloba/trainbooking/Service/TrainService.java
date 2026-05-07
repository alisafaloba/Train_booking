package com.alisafaloba.trainbooking.Service;

import com.alisafaloba.trainbooking.Domain.Booking;
import com.alisafaloba.trainbooking.Domain.Train;
import com.alisafaloba.trainbooking.Domain.User;
import com.alisafaloba.trainbooking.Repository.BookingRepository;
import com.alisafaloba.trainbooking.Repository.TrainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final EmailService emailService;

    public TrainService(TrainRepository trainRepository, BookingRepository bookingRepository, EmailService emailService) {
        this.trainRepository = trainRepository;
        this.bookingRepository = bookingRepository;
        this.emailService = emailService;
    }

    public void markTrainAsDelayed(Long trainId) {
        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new IllegalArgumentException("Train not found"));

        train.setDelayed(true);
        trainRepository.save(train);

        // Find all bookings for this train
        List<Booking> affectedBookings = bookingRepository.findByRouteTrain(train);

        // Extract unique users to avoid spamming someone who booked multiple tickets
        List<User> affectedUsers = affectedBookings.stream()
                .map(Booking::getCustomer)
                .distinct()
                .toList();

        emailService.sendDelayNotification(train, affectedUsers);
    }
}