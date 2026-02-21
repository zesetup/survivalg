package com.gmail.zerosetup.survivalg.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String uniqueHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantStatus status = ParticipantStatus.IN_SHELTER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column
    private LocalDateTime leftShelterAt;

    @Column(nullable = false)
    private LocalDateTime registeredAt = LocalDateTime.now();

    // Constructors
    public Participant() {
        this.uniqueHash = UUID.randomUUID().toString();
    }

    public Participant(String nickname, Game game) {
        this.nickname = nickname;
        this.game = game;
        this.uniqueHash = UUID.randomUUID().toString();
        this.status = ParticipantStatus.IN_SHELTER;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUniqueHash() {
        return uniqueHash;
    }

    public void setUniqueHash(String uniqueHash) {
        this.uniqueHash = uniqueHash;
    }

    public ParticipantStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipantStatus status) {
        this.status = status;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public LocalDateTime getLeftShelterAt() {
        return leftShelterAt;
    }

    public void setLeftShelterAt(LocalDateTime leftShelterAt) {
        this.leftShelterAt = leftShelterAt;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}

