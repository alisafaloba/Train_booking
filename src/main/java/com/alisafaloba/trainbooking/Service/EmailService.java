package com.alisafaloba.trainbooking.Service;

import com.alisafaloba.trainbooking.Domain.Booking;
import com.alisafaloba.trainbooking.Domain.Train;
import com.alisafaloba.trainbooking.Domain.User;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(Booking booking) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(booking.getCustomer().getEmail());
            message.setSubject("Train Booking Confirmation");
            message.setText("Dear Customer, your booking is confirmed.\n" +
                    "Route: " + booking.getDepartureStation().getName() + " to " +
                    booking.getArrivalStation().getName() + "\n" +
                    "Seats: " + booking.getNumberOfSeats() + "\n" +
                    "Train: " + booking.getRoute().getTrain().getName());

            mailSender.send(message);
            System.out.println("Email successfully sent to: " + booking.getCustomer().getEmail());

        } catch (MailException e) {
            // This stops the app from crashing if there is no email server configured!
            System.err.println("SIMULATED EMAIL: Could not connect to mail server. " +
                    "Pretending we sent an email to " + booking.getCustomer().getEmail());
        }
    }

    public void sendDelayNotification(Train train, List<User> affectedUsers) {
        for (User user : affectedUsers) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("URGENT: Train Delay Notification");
                message.setText("Dear Customer, please be advised that train " +
                        train.getName() + " is currently experiencing delays.");
                mailSender.send(message);
            } catch (MailException e) {
                System.err.println("SIMULATED DELAY EMAIL: Pretending to notify " + user.getEmail());
            }
        }
    }
}