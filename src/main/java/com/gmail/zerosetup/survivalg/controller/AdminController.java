package com.gmail.zerosetup.survivalg.controller;

import com.gmail.zerosetup.survivalg.model.Game;
import com.gmail.zerosetup.survivalg.model.GameStatus;
import com.gmail.zerosetup.survivalg.service.GameService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final GameService gameService;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/games";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            session.setAttribute("isAdmin", true);
            return "redirect:/admin/games";
        }
        model.addAttribute("error", "Неверное имя пользователя или пароль");
        return "admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    @GetMapping("/games")
    public String listGames(HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        List<Game> games = gameService.getAllGames();
        model.addAttribute("games", games);
        return "admin/games";
    }

    @GetMapping("/games/new")
    public String newGameForm(HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }
        return "admin/game-form";
    }

    @PostMapping("/games/create")
    public String createGame(@RequestParam String name,
                            @RequestParam String startDateTime,
                            HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        LocalDateTime start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        gameService.createGame(name, start);
        return "redirect:/admin/games";
    }

    @GetMapping("/games/{id}/edit")
    public String editGameForm(@PathVariable Long id, HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        Game game = gameService.getGameById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        model.addAttribute("game", game);
        return "admin/game-edit";
    }

    @PostMapping("/games/{id}/update")
    public String updateGame(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam String startDateTime,
                            HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        LocalDateTime start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        gameService.updateGame(id, name, start);
        return "redirect:/admin/games";
    }

    @PostMapping("/games/{id}/status")
    public String updateGameStatus(@PathVariable Long id,
                                   @RequestParam String status,
                                   HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        GameStatus newStatus = GameStatus.valueOf(status);
        gameService.updateGameStatus(id, newStatus);
        return "redirect:/admin/games";
    }

    @GetMapping("/games/{id}/participants")
    public String viewParticipants(@PathVariable Long id, HttpSession session, Model model) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        Game game = gameService.getGameById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        model.addAttribute("game", game);
        model.addAttribute("participants", game.getParticipants());
        return "admin/participants";
    }

    @PostMapping("/participants/{id}/remove-from-shelter")
    public String removeFromShelter(@PathVariable Long id,
                                   @RequestParam Long gameId,
                                   HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        gameService.removeFromShelter(id);
        return "redirect:/admin/games/" + gameId + "/participants";
    }

    @PostMapping("/participants/{id}/return-to-shelter")
    public String returnToShelter(@PathVariable Long id,
                                 @RequestParam Long gameId,
                                 HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/admin/login";
        }

        gameService.returnToShelter(id);
        return "redirect:/admin/games/" + gameId + "/participants";
    }
}

