package com.example.entity;

import com.example.util.DigestUtils;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.SecureRandom;
import java.util.Base64;

@Entity
@Table(name = "userEntity")
public class User extends PanacheEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Getter
    @Column(name="name", unique = true)
    private String name;
//    @Setter
    private String pass;
    private String salt;

    @Getter
    @Setter
    private String rules;

    public User(){

    }
    public User(String name, String pass){
        this.name = name;
        //this.pass = pass;
        createSalt();
        this.pass = DigestUtils.hashPassword(pass, salt);
    }
    private void createSalt(){
        /*
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        this.salt = Base64.getEncoder().encodeToString(salt);
        */
        this.salt = DigestUtils.createRandom(16);
    }

    public void updatePass(String pass) {
        this.pass = pass;
        if(this.salt == null || this.salt.isEmpty()){
            createSalt();
        }
        String pass_new = DigestUtils.hashPassword(pass, salt);
        this.pass = pass_new;
    }

    // 验证密码
    public boolean verifyPassword(String password) {
        return DigestUtils.verifyPassword(password, pass, salt);
    }
}
