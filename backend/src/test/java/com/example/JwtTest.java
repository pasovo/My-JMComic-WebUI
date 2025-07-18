package com.example;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;

import io.smallrye.jwt.build.Jwt;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;

@Slf4j
public class JwtTest {
    private long ExpirationDate = 86400;
    private String jwtSecret = "1ahrieGVsb86jFqpl3KCJSWDHyZ4PgmvXUonw90dcfOMRLBItAzQTk5ExNu27YfLOtUvPqgWTLpH9Wzz8P3v8D5YjMKHYDB9sIeHSU0VFFYba5d5S6RRJJIrs9MkitFoyNshEv30bzxjpQBUABGDv8BT6Cv5vV2Rf2KRBl9Ohwc75s7tzwJfLXM61cDM1Qz7c5VOYJX4gMPNTIVg61C6RoIKrbkl0ABBAwdTcUVPjgGmldP5B45AQ3tq5yBy5h17";
    private SecretKey secret;

    private String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCgGyFP2JZGXy5hx0CR1nXXVQgH+wd6t0xM3ZtwkbzdB/mnKc1VmndlT7iqDiSbORnEGr7r0kybyk39Jy6hp5VS16VWaH0P8XcZNy2Uszt/46AsPytII059Bsu46/gvbSVLLxQ6QIm3AhxVL5awkyk7uoYDiGunjzgMcSVUNWF1w5PbcXJJH+7RjJ7Qo1D0DPKAs1z/aXocnE58y+cNBncW8mgNEf4/DTRGWq8bK8U+5dUCy24v+RxSqqP9wi2dCYd/YtcSmxCFljuGZhRcgPL/ZuZ4CKfp+pFR4zsVukqDFW0PjYnPbOb2I8qBhO2NuQb+efYH4lBxf0z6/S/5yhcXAgMBAAECggEAAtk4uIDz+4CKBLdU0MNuCUy7/dhbg7VrKBrxPwl4wL35lR3LR3CeGzFV6cCiqhhT+wc2yl9NeaAZzx05WSrwHy8zA9yMDRKQueQfbXva3SWhnefpzzjBlrveOAYKvl1Ewpet1nN7f8QL2QAlwDJ+E0/4PmvY7nw/1QjWTy878qpm60ON1CTDJ2PqSJYYuS7ldrNKiuU5DB5dLEH3YpmNE8JwOsHmdAAIJ098yUAF1p+SNTiIt2xIS6wQnuyjiDyYnxH+CgT0fQ0ubkLO04MkJIS4z1O2JFR/q5+W5ar6z1DZTg9yfwvnM4GtB0pVrcrYhyaUbWrCsgBQVh3Nym7lcQKBgQDS80z9iIeOyaH6fS6dYyvSg6oqJFl6ae3KJK0PpW7wMRLMP8KCqnhDVlGcvwXGb0URRkQ40s/bsiUEHutIu4MxCPFDVq981tT1JVtSccyt4pmD3VQCp1wrlCZfPzF3FywLWkEz09bn1pcZx9DcTbbkSwicCIVu9LAJFKoHEYXCewKBgQDCTCe6owuTX4DjcoyQmG1/XQjs/x7w6bMQSIBm3mJ3Pc0rILZoSBINn8HqPTu2N2MbYlkdqVi855QM/b1TWA5F2YwefVk+Dh0phTdOzNob6TygggBJ6qgOxV11P/KLyDMD+oMaJ9kgL5qf08AmTKCVTxU7QrmCCKnd7qMZDB95FQKBgE5a7xMRXoDMJm1biDAPwRxNqaxDmjTdGeVG4D16jo2LPNc/K6nLViZ07pWRYrzFYds7xWVx8KFDN3qjXXPDqsRQ9QS1MzPkvG9ec+BYwtUn4SvYzza5IYRSYDpqx53brpF88dElc6Lj03ZRASSDfVapBEao559CJg0Jbup3OL73AoGACyZ1rTdIJcf4TJU5Td8BgaqkJX2ql+bBePDANacTfYZVbOEutqNULeX6KegjshxRmrO1K4eZaRXdIDW7lLnb79RBP0qwQWhr8gMMHoCRD8L90LRFMfi+dkKLT3xA/haaHuNUfL7GrhstQ3jScSA6ovbr3/+/R6AZp3vXbkuMztUCgYBTqsYrLgYz/57EEPIqApQFMfezrwfW6YYvfAHMEZR/cFF0f6wivv/7JQ1ZRSgcSY+JvGwArGkBFiZqEFZYT0ddyT6UqyDt4dZlvInrcMc/DG4dOC2MYondG/agmm3Td1eHbz3rx+UPplxl8UwRC5LcILITepXI4N/ew25sQd6gcQ==";
    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoBshT9iWRl8uYcdAkdZ111UIB/sHerdMTN2bcJG83Qf5pynNVZp3ZU+4qg4kmzkZxBq+69JMm8pN/ScuoaeVUtelVmh9D/F3GTctlLM7f+OgLD8rSCNOfQbLuOv4L20lSy8UOkCJtwIcVS+WsJMpO7qGA4hrp484DHElVDVhdcOT23FySR/u0Yye0KNQ9AzygLNc/2l6HJxOfMvnDQZ3FvJoDRH+Pw00RlqvGyvFPuXVAstuL/kcUqqj/cItnQmHf2LXEpsQhZY7hmYUXIDy/2bmeAin6fqRUeM7FbpKgxVtD42Jz2zm9iPKgYTtjbkG/nn2B+JQcX9M+v0v+coXFwIDAQAB";

    private PublicKey publicKey_;
    private PrivateKey privateKey_;

//    @BeforeAll
    private void init(){
        //secret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        /*
        try {
            secret = Keys.hmacShaKeyFor(HMACSHA256(jwtSecret));
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        */
        //secret = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), "HmacHS256");
        try {
            //byte[] bytes = HMACSHA256(publicKey);
            //byte[] bytes = publicKey.getBytes(StandardCharsets.UTF_8);
            //secret = new SecretKeySpec(bytes, "RS256");

            byte[] bytes = Base64.getDecoder().decode(publicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
            publicKey_ = KeyFactory.getInstance("RSA").generatePublic(keySpec);

            byte[] bytes_private = Base64.getDecoder().decode(privateKey);
            PKCS8EncodedKeySpec keySpec_private = new PKCS8EncodedKeySpec(bytes_private);
            privateKey_ = KeyFactory.getInstance("RSA").generatePrivate(keySpec_private);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }
/*
    @Test
    public void t001(){
        init();

        String token = "";
        token = createToken_Jwt("root");
        log.info("jwt -> {}", token);
        vToken(token);

        try {
            token = createToken_Jwts("root");
            log.info("jwts -> {}", token);
            vToken(token);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }
*/
    public String createToken_Jwt(String username){
        return Jwt.issuer("http://MyJmcomicManage.jackxwb.top")
                .upn(username)
                .groups(new HashSet<>(Arrays.asList("User", "Admin")))
                //.groups(new HashSet<>(Arrays.asList(result.getRules().split(","))))
                .claim("birthdate", Instant.now())
                .claim("username", username)
                .claim("rules", "User,Admin")
                // 过期时间
                .expiresAt(Instant.now().plusSeconds(ExpirationDate))
                .sign();
    }
    /*
    public String createToken_Jwts(String username) throws Exception {
        return Jwts.builder()
                //代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userid，roldid之类的，作为什么用户的唯一标志。
                .setSubject(String.format("[%s]%s", "0001", username))
                //塞入参数
//                .claim("UserID",u.getUid())
//                .claim("UserName",u.getUsername())
                .claim("birthdate", Instant.now().getEpochSecond())
                .claim("username", username)
                .claim("rules", "User,Admin")
                //jwt签发时间
                .setIssuedAt(new Date())
                //过期时间
                //.setExpiration(new Date(System.currentTimeMillis() + expiration))
                .setExpiration(new Date(Instant.now().plusSeconds(ExpirationDate).getEpochSecond()))
                //设置加密算法
                //.signWith(SignatureAlgorithm.HS256, jwtSecret)
                //.signWith(SignatureAlgorithm.HS256, HMACSHA256_String("Jackxwb"))
                //.signWith(SignatureAlgorithm.RS256, secret)
                .signWith(SignatureAlgorithm.RS256, privateKey_)
                .compact();
    }
    */
    public static byte[] HMACSHA256(byte[] data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return sha256_HMAC.doFinal(data);
    }
    public static String HMACSHA256_String(byte[] data, String key) throws Exception {
        byte[] array = HMACSHA256(data, key);
        StringBuilder sb = new StringBuilder();
        for (byte item : array) {
            sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString().toUpperCase();
    }
    public static byte[] HMACSHA256(String key) throws Exception {
        return HMACSHA256(key.getBytes(StandardCharsets.UTF_8), key);
    }
    public static String HMACSHA256_String(String key) throws Exception {
        return HMACSHA256_String(key.getBytes(StandardCharsets.UTF_8), key);
    }

    public void vToken(String token){
        /*
        //SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        //Claims body = Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
        //Claims body = Jwts.parser().setSigningKey(secret).build().parseClaimsJws(token).getBody();

        Claims body = Jwts.parser().setSigningKey(publicKey_).build().parseClaimsJws(token).getBody();

        // 获取过期时间
        Date expiration = body.getExpiration();
        log.info("expiration date: {}", expiration);
        */
    }
}
