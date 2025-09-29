package org.example.backend_fivegivechill.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

@Service
public class Encryption {

    @Value("${encryption}")
    private String encryptionKey;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final int KEY_LENGTH = 16;
    private static final int IV_LENGTH = 16;

    public String encrypt(String userId) throws Exception {
        if (userId == null || userId.isEmpty()) throw new IllegalArgumentException("UserId không được null hoặc rỗng");

        SecretKeySpec secretKeySpec = new SecretKeySpec(generateKey(encryptionKey), "AES");
        byte[] iv = generateIV();
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

        byte[] encrypted = cipher.doFinal(userId.getBytes(StandardCharsets.UTF_8));
        byte[] combined = new byte[IV_LENGTH + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
        System.arraycopy(encrypted, 0, combined, IV_LENGTH, encrypted.length);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }

    public String decrypt(String encryptedUserId) throws Exception {
        if (encryptedUserId == null || encryptedUserId.isEmpty()) throw new IllegalArgumentException("EncryptedUserId không được null hoặc rỗng");

        byte[] combined = Base64.getUrlDecoder().decode(encryptedUserId);
        if (combined.length < IV_LENGTH) throw new IllegalArgumentException("Dữ liệu mã hóa không hợp lệ");
        byte[] iv = new byte[IV_LENGTH];
        byte[] encrypted = new byte[combined.length - IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

        SecretKeySpec secretKeySpec = new SecretKeySpec(generateKey(encryptionKey), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

        byte[] decrypted = cipher.doFinal(encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private byte[] generateKey(String secretKey) throws Exception {
        byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        byte[] result = new byte[KEY_LENGTH];
        System.arraycopy(key, 0, result, 0, KEY_LENGTH);
        return result;
    }

    private byte[] generateIV() {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        return iv;
    }

    public static String sign(String data, String key) {
        try {
            System.out.println("data:" + data);
            System.out.println("key:" + key);
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo chữ ký: " + e.getMessage(), e);
        }
    }

}
