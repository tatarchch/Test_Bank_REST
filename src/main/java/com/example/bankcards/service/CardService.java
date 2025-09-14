package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.OperationNotAllowedException;
import com.example.bankcards.exception.OtherException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository repository;
    private final UserRepository userRepository;
    private final CardMapper mapper;


    public List<CardDto> getAllCards() {
        return repository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    public CardDto getCardById(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    public CardDto getCardByIdWithMask(Long id) {
        return repository.findById(id)
                .map(mapper::toDtoWithMask)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    public CardDto getCardByIdAndOwnerId(Long carId, Long ownerId) {
        return repository.findByIdAndOwnerId(carId, ownerId)
                .map(mapper::toDto)
                .orElseThrow(CardNotFoundException::new);
    }

    public CardDto getCardByIdAndOwnerIdWithMask(Long carId, Long ownerId) {
        return repository.findByIdAndOwnerId(carId, ownerId)
                .map(mapper::toDtoWithMask)
                .orElseThrow(CardNotFoundException::new);
    }

    @Transactional
    public CardDto createNewCard(CardDto cardDto) {
        return Optional.of(cardDto)
                .map(mapper::toEntity)
                .map(repository::save)
                .map(mapper::toDtoWithMask)
                .orElseThrow(OtherException::new);
    }

    @Transactional
    public void deleteCardById(Long id) {
        repository.delete(repository.findById(id)
                .filter(card -> card.getExpireDate().isAfter(LocalDate.now()))
                .orElseThrow(OtherException::new));
    }

    @Transactional
    public CardDto activeCard(CardDto cardDto) {
        return Optional.of(cardDto)
                .filter(cardDto1 -> cardDto1.getExpireDate().isAfter(LocalDate.now()))
                .filter(cardDto1 -> !cardDto1.getStatus().equals(CardStatus.ACTIVE.getString()))
                .map(cardDto1 -> {
                    cardDto1.setStatus(CardStatus.ACTIVE.getString());
                    return cardDto1;
                })
                .map(mapper::toEntity)
                .map(repository::save)
                .map(mapper::toDtoWithMask)
                .orElseThrow(CardNotFoundException::new);
    }

    @Transactional
    public CardDto blockCard(CardDto cardDto) {
        return Optional.of(cardDto)
                .filter(cardDto1 -> cardDto1.getExpireDate().isAfter(LocalDate.now()))
                .filter(cardDto1 -> !cardDto1.getStatus().equals(CardStatus.BLOCKED.getString()))
                .map(cardDto1 -> {
                    cardDto1.setStatus(CardStatus.BLOCKED.getString());
                    return cardDto1;
                })
                .map(mapper::toEntity)
                .map(repository::save)
                .map(mapper::toDtoWithMask)
                .orElseThrow(CardNotFoundException::new);
    }

    public List<CardDto> getCardsByOwnerId(Long id) {
        return repository.findAllByOwnerId(id).stream()
                .map(mapper::toDtoWithMask)
                .toList();
    }

    @Transactional
    public void transferBetweenCards(Long fromCardId, Long toCardId, BigDecimal amount, Long userId) {

        Card fromCard = repository.findById(fromCardId)
                .orElseThrow(() -> new OperationNotAllowedException("Карта, с которой осуществляется перевод, не найдена"));

        Card toCard = repository.findById(toCardId)
                .orElseThrow(() -> new OperationNotAllowedException("Карта, на которою осуществляется перевод, не найдена"));

        if (!fromCard.getOwner().getId().equals(userId) || !toCard.getOwner().getId().equals(userId)) {
            throw new OperationNotAllowedException("Карты не принадлежат одному пользователю");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OperationNotAllowedException("Сумма перевода должна быть положительной");
        }

        if (!fromCard.getStatus().equals(CardStatus.ACTIVE)) {
            throw new OperationNotAllowedException("Карта неактивна");
        }

        if (fromCard.getExpireDate().isBefore(LocalDate.now())) {
            throw new OperationNotAllowedException("Карта просрочена");
        }

        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new OperationNotAllowedException("Недостаточно средств");
        }

        if (!toCard.getStatus().equals(CardStatus.ACTIVE)) {
            throw new OperationNotAllowedException("Карта неактивна");
        }

        if (toCard.getExpireDate().isBefore(LocalDate.now())) {
            throw new OperationNotAllowedException("Карта просрочена");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        repository.save(fromCard);
        repository.save(toCard);

        log.info(String.format("Перевод суммы %s с карты %d на карту %d выполнен", amount, fromCardId, toCardId));

    }

    public BigDecimal getCardBalance(Long id) {
        return repository.findById(id)
                .map(Card::getBalance)
                .orElseThrow(CardNotFoundException::new);
    }

    @Transactional
    public void setOwner(CardDto cardDto, Long ownerId) {
        Optional.of(cardDto)
                .map(mapper::toEntity)
                .map(card -> {
                    card.setOwner(userRepository.findById(ownerId)
                            .orElseThrow(() -> new UserNotFoundException(ownerId)));
                    return repository.save(card);
                })
                .orElseThrow(OtherException::new);
    }

}
