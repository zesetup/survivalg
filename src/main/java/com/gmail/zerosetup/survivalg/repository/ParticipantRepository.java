package com.gmail.zerosetup.survivalg.repository;

import com.gmail.zerosetup.survivalg.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByUniqueHash(String uniqueHash);
}

