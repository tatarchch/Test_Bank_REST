package com.example.bankcards.util;

import com.example.bankcards.exception.DecryptException;
import com.example.bankcards.exception.EncryptException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

@UtilityClass
@Slf4j
public class EncryptionUtils {


    private final String SECRET_KEY = "G1ah%z@9$pLq#7bX*2sT7uVkYp3sC5vH8";
    private final String SALT = "a1b2c3d4e5f678901234567890abcdef";

    private String getSecretKeyFromEnv() {
        return System.getenv("ENCRYPTION_KEY");
    }

    private static String getSaltFromEnv() {
        return System.getenv("ENCRYPTION_SALT");
    }


    public String encrypt(String data) {
        try {
            TextEncryptor encryptor = Encryptors.text(SECRET_KEY, SALT);
            return encryptor.encrypt(data);
        } catch (Exception e) {
            throw new EncryptException("Ошибка при шифровании данных", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            TextEncryptor encryptor = Encryptors.text(SECRET_KEY, SALT);
            return encryptor.decrypt(encryptedData);
        } catch (Exception e) {
            throw new DecryptException("Ошибка при дешифровании данных", e);
        }
    }

}
