package com.alisafaloba.trainbooking.Controller;

import com.alisafaloba.trainbooking.Repository.TrainRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    private final TrainRepository trainRepository;

    public WebController(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    // Maps the root URL (localhost:8080/) to the index.html template
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    // Maps /trains to the trains.html template and passes database data to it
    @GetMapping("/view-trains")
    public String viewTrainsPage(Model model) {
        // Fetch all trains from the database
        model.addAttribute("trains", trainRepository.findAll());
        // Return the name of the Thymeleaf template
        return "trains";
    }
}