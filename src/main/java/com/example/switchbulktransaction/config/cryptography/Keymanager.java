package com.example.switchbulktransaction.config.cryptography;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Service
@Slf4j
public class Keymanager {

    private PrivateKey privateKey;
    private PublicKey publicKey;


    public Keymanager(){
        this.privateKey =  loadPrivateKey("config/private_key.pem");
        this.publicKey =  loadPublicKey("config/public_key.pem");
    }


    public PrivateKey getPrivateKey(){
        if (this.privateKey == null){
            this.privateKey = loadPrivateKey("config/private_key.pem");
        }
        return this.privateKey;
    }

    public PublicKey getPublicKey(){
        if (this.publicKey == null){
            this.publicKey = loadPublicKey("config/public_key.pem");
        }
        return this.publicKey;
    }


    private PrivateKey loadPrivateKey(String path){
        try {
            byte[] encodedKey = getEndcodedKey(path);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    private PublicKey loadPublicKey(String path) {
        try {
            byte[] encodedKey = getEndcodedKey(path);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    private byte[] getEndcodedKey(String path){
        boolean isPrivateKey = path.startsWith("classpath:config/private");
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes;
            try (InputStream inputStream = resource.getInputStream()) {
                bytes = inputStream.readAllBytes();
            }
            String pem = new String(bytes)
                    .replaceAll("-----BEGIN([^-]+)-----", "")
                    .replaceAll("-----END([^-]+)-----", "")
                    .replaceAll("\\s+", "");

            return java.util.Base64.getDecoder().decode(pem);
        }catch (Exception e){
            throw new IllegalArgumentException(e);
        }
    }
}
