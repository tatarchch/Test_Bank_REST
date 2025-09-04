package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.enums.CardStatus;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.OperationNotAllowedException;
import com.example.bankcards.exception.OtherException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardService cardService;

    private Card testCard;
    private CardDto testCardDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("encryptedCardNumber");
        testCard.setStatus(CardStatus.ACTIVE.getStatus());
        testCard.setExpireDate(LocalDate.now().plusYears(2));
        testCard.setBalance(BigDecimal.valueOf(1000));
        testCard.setOwner(testUser);

        testCardDto = new CardDto();
        testCardDto.setId(1L);
        testCardDto.setCardNumber("decryptedCardNumber");
        testCardDto.setStatus(CardStatus.ACTIVE.getStatus());
        testCardDto.setExpireDate(LocalDate.now().plusYears(2));
        testCardDto.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void getAllCardsShouldReturnListOfCards() {
        List<Card> cards = List.of(testCard);
        when(cardRepository.findAll()).thenReturn(cards);
        when(cardMapper.toDto(any(Card.class))).thenReturn(testCardDto);

        List<CardDto> result = cardService.getAllCards();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCardDto, result.get(0));
        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void getCardByIdShouldReturnCardWhenCardExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardMapper.toDto(testCard)).thenReturn(testCardDto);

        CardDto result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(testCardDto, result);
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void getCardByIdShouldThrowExceptionWhenCardNotExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.getCardById(1L));
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void getCardByIdWithMaskShouldReturnMaskedCard() {
        CardDto maskedCardDto = new CardDto();
        maskedCardDto.setCardNumber("**** **** **** 1234");

        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(cardMapper.toDtoWithMask(testCard)).thenReturn(maskedCardDto);

        CardDto result = cardService.getCardByIdWithMask(1L);

        assertNotNull(result);
        assertEquals("**** **** **** 1234", result.getCardNumber());
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void createNewCardShouldReturnCreatedCard() {
        when(cardMapper.toEntity(testCardDto)).thenReturn(testCard);
        when(cardRepository.save(testCard)).thenReturn(testCard);
        when(cardMapper.toDtoWithMask(testCard)).thenReturn(testCardDto);

        CardDto result = cardService.createNewCard(testCardDto);

        assertNotNull(result);
        assertEquals(testCardDto, result);
        verify(cardMapper, times(1)).toEntity(testCardDto);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    void deleteCardByIdShouldDeleteCardWhenCardExistsAndNotExpired() {
        testCard.setExpireDate(LocalDate.now().plusDays(1));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        cardService.deleteCardById(1L);

        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).delete(testCard);
    }

    @Test
    void deleteCardByIdShouldThrowException_WhenCardExpired() {
        testCard.setExpireDate(LocalDate.now().minusDays(1));
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        assertThrows(OtherException.class, () -> cardService.deleteCardById(1L));
        verify(cardRepository, times(1)).findById(1L);
        verify(cardRepository, never()).delete(any());
    }

    @Test
    void activeCardShouldActivateCard_WhenConditionsMet() {
        testCardDto.setStatus(CardStatus.BLOCKED.getStatus());
        Card activeCard = new Card();
        activeCard.setStatus(CardStatus.ACTIVE.getStatus());

        CardDto activeCardDto = new CardDto();
        activeCardDto.setStatus(CardStatus.ACTIVE.getStatus());

        when(cardMapper.toEntity(testCardDto)).thenReturn(activeCard);
        when(cardRepository.save(activeCard)).thenReturn(activeCard);
        when(cardMapper.toDtoWithMask(activeCard)).thenReturn(activeCardDto);

        CardDto result = cardService.activeCard(testCardDto);

        assertNotNull(result);
        assertEquals(CardStatus.ACTIVE.getStatus(), result.getStatus());
        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void transferBetweenCardsShouldTransferFundsWhenAllConditionsMet() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(1000));
        fromCard.setStatus(CardStatus.ACTIVE.getStatus());
        fromCard.setExpireDate(LocalDate.now().plusYears(1));
        fromCard.setOwner(testUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500));
        toCard.setStatus(CardStatus.ACTIVE.getStatus());
        toCard.setExpireDate(LocalDate.now().plusYears(1));
        toCard.setOwner(testUser);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        cardService.transferBetweenCards(1L, 2L, BigDecimal.valueOf(200), 1L);

        assertEquals(BigDecimal.valueOf(800), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700), toCard.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferBetweenCardsShouldThrowExceptionWhenCardsBelongToDifferentUsers() {
        User differentUser = new User();
        differentUser.setId(2L);

        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setOwner(testUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setOwner(differentUser);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThrows(OperationNotAllowedException.class,
                () -> cardService.transferBetweenCards(1L, 2L, BigDecimal.valueOf(200), 1L));
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    void getCardBalanceShouldReturnBalanceWhenCardExists() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        BigDecimal result = cardService.getCardBalance(1L);

        assertEquals(BigDecimal.valueOf(1000), result);
        verify(cardRepository, times(1)).findById(1L);
    }

    @Test
    void setOwnerShouldSetOwnerWhenUserExists() {
        when(cardMapper.toEntity(testCardDto)).thenReturn(testCard);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cardRepository.save(testCard)).thenReturn(testCard);

        cardService.setOwner(testCardDto, 1L);

        assertEquals(testUser, testCard.getOwner());
        verify(userRepository, times(1)).findById(1L);
        verify(cardRepository, times(1)).save(testCard);
    }

    @Test
    void setOwnerShouldThrowExceptionWhenUserNotExists() {
        when(cardMapper.toEntity(testCardDto)).thenReturn(testCard);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardService.setOwner(testCardDto, 1L));
        verify(userRepository, times(1)).findById(1L);
        verify(cardRepository, never()).save(any(Card.class));
    }
}
