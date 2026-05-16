package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Repository.TrainRepository;
import com.alisafaloba.trainbooking.Repository.StationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;

    public WebController(TrainRepository trainRepository, StationRepository stationRepository) {
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
    }

    // Main Menu / Entry point
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // ================= CUSTOMER PAGES =================

    @GetMapping("/search-routes")
    public String searchRoutesPage(Model model) {
        // Pre-load stations so the customer can pick them from a dropdown
        model.addAttribute("stations", stationRepository.findAll());
        return "search-routes";
    }

    @GetMapping("/my-tickets")
    public String myTicketsPage() {
        return "my-tickets";
    }

    // ================= ADMIN PAGES =================

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/admin/manage-trains")
    public String viewTrainsPage(Model model) {
        model.addAttribute("trains", trainRepository.findAll());
        return "trains";
    }

    @GetMapping("/admin/manage-stations")
    public String manageStationsPage(Model model) {
        model.addAttribute("stations", stationRepository.findAll());
        return "manage-stations";
    }

    // Add this inside your WebController.java
    @GetMapping("/perform-logout")
    public String logout() {
        // Redirecting to an invalid basic auth route forces the browser to discard its cached header
        return "redirect:/logout-success";
    }

    @GetMapping("/logout-success")
    public String logoutSuccessPage() {
        return "logout-clear"; // A simple template that finishes clearing credentials
    }
}