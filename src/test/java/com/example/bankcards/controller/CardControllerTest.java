package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CardService cardService;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardController cardController;

    private CardDto testCardDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
        objectMapper = new ObjectMapper();

        testCardDto = new CardDto();
        testCardDto.setId(1L);
        testCardDto.setCardNumber("1234 5678 9012 3456");
        testCardDto.setStatus("active");
        testCardDto.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    void getAllCards_ShouldReturnListOfCards() throws Exception {
        List<CardDto> cards = List.of(testCardDto);
        when(cardService.getAllCards()).thenReturn(cards);

        mockMvc.perform(get("/api/v1/card/getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].cardNumber").value("1234 5678 9012 3456"));

        verify(cardService, times(1)).getAllCards();
    }

    @Test
    void getCardByIdShouldReturnCard() throws Exception {
        when(cardService.getCardById(1L)).thenReturn(testCardDto);

        mockMvc.perform(get("/api/v1/card/getById/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234 5678 9012 3456"));

        verify(cardService, times(1)).getCardById(1L);
    }

    @Test
    void getCardByIdWithMask_ShouldReturnMaskedCard() throws Exception {
        CardDto maskedCardDto = new CardDto();
        maskedCardDto.setId(1L);
        maskedCardDto.setCardNumber("**** **** **** 3456");

        when(cardService.getCardByIdWithMask(1L)).thenReturn(maskedCardDto);

        mockMvc.perform(get("/api/v1/card/getByIdWithMask/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 3456"));

        verify(cardService, times(1)).getCardByIdWithMask(1L);
    }

    @Test
    void createNewCardShouldReturnCreatedCard() throws Exception {
        when(cardService.createNewCard(any(CardDto.class))).thenReturn(testCardDto);

        mockMvc.perform(post("/api/v1/card/createNew")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCardDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234 5678 9012 3456"));

        verify(cardService, times(1)).createNewCard(any(CardDto.class));
    }

    @Test
    void deleteCardByIdShouldCallService() throws Exception {
        doNothing().when(cardService).deleteCardById(1L);

        mockMvc.perform(delete("/api/v1/card/deleteById/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cardService, times(1)).deleteCardById(1L);
    }

    @Test
    void activeCardShouldReturnActivatedCard() throws Exception {
        when(cardService.activeCard(any(CardDto.class))).thenReturn(testCardDto);

        mockMvc.perform(patch("/api/v1/card/activeCard")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCardDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234 5678 9012 3456"));

        verify(cardService, times(1)).activeCard(any(CardDto.class));
    }

    @Test
    void getAllCardsByOwnerIdShouldReturnCards() throws Exception {
        List<CardDto> cards = List.of(testCardDto);
        when(cardService.getCardsByOwnerId(1L)).thenReturn(cards);

        mockMvc.perform(get("/api/v1/card/getAllByOwnerId/{ownerId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(cardService, times(1)).getCardsByOwnerId(1L);
    }

    @Test
    void transferBetweenOwnersCardsShouldCallService() throws Exception {
        doNothing().when(cardService).transferBetweenCards(1L, 2L, BigDecimal.valueOf(100), 1L);

        mockMvc.perform(post("/api/v1/card/transfer")
                        .param("fromCardId", "1")
                        .param("toCardId", "2")
                        .param("amount", "100")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cardService, times(1)).transferBetweenCards(1L, 2L, BigDecimal.valueOf(100), 1L);
    }

    @Test
    void getCardBalanceShouldReturnBalance() throws Exception {
        when(cardService.getCardBalance(1L)).thenReturn(BigDecimal.valueOf(1000));

        mockMvc.perform(get("/api/v1/card/getBalance/{cardId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));

        verify(cardService, times(1)).getCardBalance(1L);
    }

    @Test
    void setOwnerShouldCallService() throws Exception {
        doNothing().when(cardService).setOwner(any(CardDto.class), anyLong());

        mockMvc.perform(patch("/api/v1/card/setOwner")
                        .param("ownerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCardDto)))
                .andExpect(status().isOk());

        verify(cardService, times(1)).setOwner(any(CardDto.class), eq(1L));
    }

}
