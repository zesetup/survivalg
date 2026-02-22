package com.gmail.zerosetup.survivalg.controller;

import com.gmail.zerosetup.survivalg.model.Participant;
import com.gmail.zerosetup.survivalg.service.GameService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/participant")
public class ParticipantController {
    
    private final GameService gameService;
    
    @Value("${game.domain.name}")
    private String domainName;

    public ParticipantController(GameService gameService) {
        this.gameService = gameService;
    }
    
    @PostMapping("/register")
    public String register(@RequestParam Long gameId, 
                          @RequestParam String nickname,
                          Model model) {
        try {
            Participant participant = gameService.registerParticipant(gameId, nickname);

            // Build base URL using configured domain name
            String baseUrl = "https://" + domainName;

            model.addAttribute("participant", participant);
            model.addAttribute("baseUrl", baseUrl);
            return "registration-success";
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/?error=" + e.getMessage();
        }
    }
    
    @GetMapping("/{hash}")
    public String viewParticipant(@PathVariable String hash, Model model) {
        Participant participant = gameService.getParticipantByHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        
        long remainingSeconds = gameService.getRemainingSeconds(participant);
        
        model.addAttribute("participant", participant);
        model.addAttribute("remainingSeconds", remainingSeconds);
        model.addAttribute("game", participant.getGame());
        
        return "participant-page";
    }
    
    @GetMapping("/{hash}/timer")
    @ResponseBody
    public String getTimer(@PathVariable String hash) {
        Participant participant = gameService.getParticipantByHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        
        long remainingSeconds = gameService.getRemainingSeconds(participant);
        
        long hours = remainingSeconds / 3600;
        long minutes = (remainingSeconds % 3600) / 60;
        long seconds = remainingSeconds % 60;
        
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
    
    @GetMapping("/{hash}/status")
    public String getStatus(@PathVariable String hash, Model model) {
        Participant participant = gameService.getParticipantByHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        
        long remainingSeconds = gameService.getRemainingSeconds(participant);
        
        model.addAttribute("participant", participant);
        model.addAttribute("remainingSeconds", remainingSeconds);
        
        return "fragments/participant-status :: status";
    }
}

