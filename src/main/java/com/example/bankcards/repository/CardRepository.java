package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.crypto.spec.OAEPParameterSpec;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByOwnerId(Long id);

    Optional<Card> findByIdAndOwnerId(Long cardId, Long ownerId);

}
