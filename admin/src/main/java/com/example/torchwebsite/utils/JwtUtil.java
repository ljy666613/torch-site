package com.example.torchwebsite.utils;

import io.fusionauth.jwt.JWTExpiredException;
import io.fusionauth.jwt.Signer;
import io.fusionauth.jwt.Verifier;
import io.fusionauth.jwt.domain.JWT;
import io.fusionauth.jwt.rsa.RSASigner;
import io.fusionauth.jwt.rsa.RSAVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Map;

@Component
//  jwt(json web token)
public class JwtUtil {
    @Value("${jwt.rsa-public-key-path}")
    private String rsaPublicKeyPath;
    @Value("${jwt.rsa-private-key-path}")
    private String rsaPrivateKeyPath;
    @Value("${jwt.ttl}")
    private int ttl;

    @Resource
    private RedisUtil redisUtil;

    private String rsaPrivateKey;
    private String rsaPublicKey;
    private final ReadFileUtil fileUtil = new ReadFileUtil();

    private String getRsaPrivateKey() {
        if (rsaPrivateKey == null){
            try {
                rsaPrivateKey = fileUtil.readFileToString(
                        new ClassPathResource(rsaPrivateKeyPath).getInputStream(),
                        StandardCharsets.UTF_8
                );
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return rsaPrivateKey;
    }

    private String getRsaPublicKey() {
        if(rsaPublicKey == null){
            try {
                rsaPublicKey = fileUtil.readFileToString(
                        new ClassPathResource(rsaPublicKeyPath).getInputStream(),
                        StandardCharsets.UTF_8
                );
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return rsaPublicKey;
    }

    public String encodeToken(Map<String, Object> payload) {
        Signer signer = RSASigner.newSHA256Signer(getRsaPrivateKey()); // 用私钥签名

        JWT jwt = new JWT()
                .setIssuedAt(ZonedDateTime.now(ZoneOffset.UTC))  // 设置签发时间
                .setExpiration(ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(ttl)); // 设置超时时间,单位：分组


        for(Map.Entry<String, Object> entry : payload.entrySet()){
            jwt.addClaim(entry.getKey(), entry.getValue());
        }
        String encode = JWT.getEncoder().encode(jwt, signer);
        redisUtil.set(payload.get("uid").toString(),encode);
        return encode;
    }

    public Map<String, Object> decodeToken(String token) throws JWTExpiredException {
        Verifier verifier = RSAVerifier.newVerifier(getRsaPublicKey());  // 用公钥解密

        JWT jwt = JWT.getDecoder().decode(token, verifier);

        if(jwt.isExpired()) return null;

        return jwt.getAllClaims();
    }
}
