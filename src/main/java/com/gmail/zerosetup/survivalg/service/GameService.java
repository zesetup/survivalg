package com.gmail.zerosetup.survivalg.service;

import com.gmail.zerosetup.survivalg.model.Game;
import com.gmail.zerosetup.survivalg.model.GameStatus;
import com.gmail.zerosetup.survivalg.model.Participant;
import com.gmail.zerosetup.survivalg.model.ParticipantStatus;
import com.gmail.zerosetup.survivalg.repository.GameRepository;
import com.gmail.zerosetup.survivalg.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    
    private final GameRepository gameRepository;
    private final ParticipantRepository participantRepository;
    
    public GameService(GameRepository gameRepository, ParticipantRepository participantRepository) {
        this.gameRepository = gameRepository;
        this.participantRepository = participantRepository;
    }
    
    @Transactional
    public Game createGame(String name, LocalDateTime startDateTime) {
        Game game = new Game(name, startDateTime);
        return gameRepository.save(game);
    }
    
    @Transactional
    public Game updateGameStatus(Long gameId, GameStatus newStatus) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        game.setStatus(newStatus);
        return gameRepository.save(game);
    }
    
    @Transactional
    public Game updateGame(Long gameId, String name, LocalDateTime startDateTime) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        game.setName(name);
        game.setStartDateTime(startDateTime);
        return gameRepository.save(game);
    }
    
    public List<Game> getAllGames() {
        return gameRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }
    
    public Optional<Game> getOpenGameForRegistration() {
        return gameRepository.findFirstByStatusOrderByStartDateTimeAsc(GameStatus.OPEN_FOR_REGISTRATION);
    }
    
    @Transactional
    public Participant registerParticipant(Long gameId, String nickname) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));
        
        if (game.getStatus() != GameStatus.OPEN_FOR_REGISTRATION) {
            throw new IllegalStateException("Game is not open for registration");
        }
        
        Participant participant = new Participant(nickname, game);
        return participantRepository.save(participant);
    }
    
    @Transactional
    public Participant removeFromShelter(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        
        participant.setStatus(ParticipantStatus.OUTSIDE_SHELTER);
        participant.setLeftShelterAt(LocalDateTime.now());
        return participantRepository.save(participant);
    }
    
    @Transactional
    public Participant returnToShelter(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        
        participant.setStatus(ParticipantStatus.IN_SHELTER);
        participant.setLeftShelterAt(null);
        return participantRepository.save(participant);
    }
    
    @Transactional
    public void checkAndUpdateParticipantTimers() {
        List<Participant> outsideParticipants = participantRepository.findAll().stream()
                .filter(p -> p.getStatus() == ParticipantStatus.OUTSIDE_SHELTER)
                .toList();
        
        LocalDateTime now = LocalDateTime.now();
        for (Participant participant : outsideParticipants) {
            if (participant.getLeftShelterAt() != null) {
                Duration duration = Duration.between(participant.getLeftShelterAt(), now);
                if (duration.toHours() >= 24) {
                    participant.setStatus(ParticipantStatus.ELIMINATED);
                    participantRepository.save(participant);
                }
            }
        }
    }
    
    public Optional<Participant> getParticipantByHash(String hash) {
        Optional<Participant> participantOpt = participantRepository.findByUniqueHash(hash);
        
        // Check timer for participants outside shelter
        if (participantOpt.isPresent()) {
            Participant participant = participantOpt.get();
            if (participant.getStatus() == ParticipantStatus.OUTSIDE_SHELTER && 
                participant.getLeftShelterAt() != null) {
                
                Duration duration = Duration.between(participant.getLeftShelterAt(), LocalDateTime.now());
                if (duration.toHours() >= 24) {
                    participant.setStatus(ParticipantStatus.ELIMINATED);
                    participantRepository.save(participant);
                }
            }
        }
        
        return participantOpt;
    }
    
    public long getRemainingSeconds(Participant participant) {
        if (participant.getStatus() != ParticipantStatus.OUTSIDE_SHELTER || 
            participant.getLeftShelterAt() == null) {
            return 0;
        }
        
        Duration duration = Duration.between(participant.getLeftShelterAt(), LocalDateTime.now());
        long secondsElapsed = duration.getSeconds();
        long totalSeconds = 24 * 60 * 60; // 24 hours in seconds
        long remaining = totalSeconds - secondsElapsed;
        
        return Math.max(0, remaining);
    }
}

