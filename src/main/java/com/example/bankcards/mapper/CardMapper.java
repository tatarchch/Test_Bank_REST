package com.example.bankcards.mapper;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.EncryptionUtils;
import jdk.jfr.Name;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CardMapper {

    @Mapping(target = "cardNumber", source = "cardNumber", qualifiedByName = "encryptCardNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "expireDate", source = "expireDate")
    @Mapping(target = "balance", source = "balance")
    Card toEntity(CardDto cardDto);

    @Mapping(target = "cardNumber", source = "cardNumber", qualifiedByName = "decryptCardNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "expireDate", source = "expireDate")
    @Mapping(target = "balance", source = "balance")
    CardDto toDto(Card card);

    @Mapping(target = "cardNumber", source = "cardNumber", qualifiedByName = "maskCardNumber")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "expireDate", source = "expireDate")
    @Mapping(target = "balance", source = "balance")
    CardDto toDtoWithMask(Card card);

    @Named("encryptCardNumber")
    default String encryptCardNumber(String cardNumber) {
        return EncryptionUtils.encrypt(cardNumber);
    }

    @Named("decryptCardNumber")
    default String decryptCardNumber(String cardNumber) {
        return EncryptionUtils.decrypt(cardNumber);
    }

    @Named("maskCardNumber")
    default String maskCardNumber(String cardNumber) {
        String decrypted = EncryptionUtils.decrypt(cardNumber);
        return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
    }

}
