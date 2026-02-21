package com.gmail.zerosetup.survivalg.controller;
import com.gmail.zerosetup.survivalg.model.Game;
import com.gmail.zerosetup.survivalg.service.GameService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Optional;
@Controller
public class HomeController {
    private final GameService gameService;
    public HomeController(GameService gameService) {
        this.gameService = gameService;
    }
    @GetMapping("/")
    public String home(Model model) {
        Optional<Game> openGame = gameService.getOpenGameForRegistration();
        openGame.ifPresent(game -> model.addAttribute("game", game));
        return "index";
    }
}
