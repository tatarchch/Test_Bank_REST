package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/v1/card")
public class CardController {

    private final CardService service;
    private final CardMapper mapper;

    @GetMapping("/getAll")
    public List<CardDto> getAllCards() {
        return service.getAllCards();
    }

    @GetMapping("/getById/{cardId}")
    public CardDto getCardById(@PathVariable("cardId") Long id) {
        return service.getCardById(id);
    }

    @GetMapping("/getByIdWithMask/{cardId}")
    public CardDto getCardByIdWithMask(@PathVariable("cardId") Long id) {
        return service.getCardByIdWithMask(id);
    }

    @GetMapping("/getByIdAndOwnerId/{cardId}/{ownerId}")
    public CardDto getCardByIdAndOwnerId(@PathVariable("cardId") Long cardId, @PathVariable("ownerId") Long ownerId) {
        return service.getCardByIdAndOwnerId(cardId, ownerId);
    }

    @GetMapping("/getByIdAndOwnerIdWithMask/{cardId}/{ownerId}")
    public CardDto getCardByIdAndOwnerIdWithMask(@PathVariable("cardId") Long cardId, @PathVariable("ownerId") Long ownerId) {
        return service.getCardByIdAndOwnerIdWithMask(cardId, ownerId);
    }

    @PostMapping("/createNew")
    public CardDto createNewCard(@Valid @RequestBody CardDto cardDto) {
        return service.createNewCard(cardDto);
    }

    @DeleteMapping("/deleteById/{cardId}")
    public void deleteCardById(@PathVariable("cardId") Long id) {
        service.deleteCardById(id);
    }

    @PatchMapping("/activeCard")
    public CardDto activeCard(@Valid @RequestBody CardDto cardDto) {
        return service.activeCard(cardDto);
    }

    @PatchMapping("/blockCard")
    public CardDto blockCard(@RequestBody CardDto cardDto) {
        return service.blockCard(cardDto);
    }

    @GetMapping("/getAllByOwnerId/{ownerId}")
    public List<CardDto> getAllCardsByOwnerId(@PathVariable("ownerId") Long ownerId) {
        return service.getCardsByOwnerId(ownerId);
    }

    @PostMapping("/transfer")
    public void transferBetweenOwnersCards(@RequestParam("fromCardId") Long fromCardId, @RequestParam("toCardId") Long toCardId, @RequestParam("amount") BigDecimal amount, @RequestParam("userId") Long userId) {
        service.transferBetweenCards(fromCardId, toCardId, amount, userId);
    }

    @GetMapping("/getBalance/{cardId}")
    public BigDecimal getCardBalance(@PathVariable("cardId") Long id) {
        return service.getCardBalance(id);
    }

    @PatchMapping("/setOwner")
    public void setOwner(@Valid @RequestBody CardDto cardDto, @RequestParam Long ownerId) {
        service.setOwner(cardDto, ownerId);
    }

}
