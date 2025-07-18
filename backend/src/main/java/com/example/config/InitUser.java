package com.example.config;

import com.example.entity.User;
import com.example.security.TokenManage;
import com.example.util.DigestUtils;
import io.quarkus.runtime.StartupEvent;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;

@Slf4j
@ApplicationScoped
public class InitUser {

//    @Inject
//    TokenManage tm;

    @ConfigProperty(name = "mp.jwt.verify.publickey.location", defaultValue = "")
    String publicPemFilePath;

    @ConfigProperty(name = "smallrye.jwt.sign.key.location", defaultValue = "")
    String privatePemFilePath;

    @ConfigProperty(name = "setting.dataPath", defaultValue = "./Archive")
    String dataSave;

    public void init() {
        log.info("开始初始化");

        long count = User.count();
        if(count == 0){
            log.info("开始初始化用户...");
            String pass = DigestUtils.createRandom(16);
            log.info("\t用户: root, 密码: {}", pass);
            User user = new User("root", pass);
            user.setRules("User,Admin");
            user.persistAndFlush();
        }

//        log.info("开始密钥...");
//        tm.initKey();

        Path publicKeyPath = Paths.get(publicPemFilePath);
        Path privateKeyPath = Paths.get(privatePemFilePath);
        if(!Files.exists(publicKeyPath) || !Files.exists(privateKeyPath)){
            log.info("密钥不存在，自动创建...");
            try {
                if(Files.exists(publicKeyPath)){
                    Files.delete(publicKeyPath);
                }
                if(Files.exists(privateKeyPath)){
                    Files.delete(privateKeyPath);
                }
                publicKeyPath.toFile().createNewFile();
                privateKeyPath.toFile().createNewFile();
                generateRSAKey(publicKeyPath, privateKeyPath);
            } catch (Exception e) {
                log.error("密钥初始化出现异常！{}", e.getMessage());
                e.printStackTrace();
            }
        }

        Path jm_option_yml = Paths.get("option.yml");
        if(!Files.exists(jm_option_yml)){
            log.info("Jmcomic 下载器配置文件不存在，自动创建...");
            String data = "client:\n" +
                    "  impl: api\n" +
                    "dir_rule:\n" +
                    "  base_dir: .\\Archive\n" +
                    "  rule: Bd_Aname_Pindextitle\n" +
                    "download:\n" +
                    "  image:\n" +
                    "    decode: true\n" +
                    "    suffix: .jpg";
            try {
                jm_option_yml.toFile().createNewFile();
                Files.write(jm_option_yml, data.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                log.error("Jmcomic 下载器配置文件初始化出现异常！{}", e.getMessage());
                e.printStackTrace();
            }
        }

        Path dataPath = Paths.get(dataSave);
        if(!Files.exists(dataPath)){
            log.info("数据库路径不存在，自动创建...");
            dataPath.toFile().mkdirs();
        }

        log.info("初始化结束");
    }

    @Transactional
    void onStart(@Observes StartupEvent event) {
        // 在这里编写初始化逻辑
        //System.out.println("Quarkus 应用程序已启动，执行初始化操作...");
        // 例如：启动后台线程、加载配置或初始化资源
        init();
    }

    public void generateRSAKey(Path publicKeyPath, Path privateKeyPath) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        KeyPair key = keyGen.generateKeyPair();
        PrivateKey privateKey = key.getPrivate();
        PublicKey publicKey = key.getPublic();

        // 保存公钥到文件
        Files.write(publicKeyPath, ("-----BEGIN PUBLIC KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(publicKey.getEncoded()) +
                "\n-----END PUBLIC KEY-----").getBytes());

        // 保存私钥到文件
        Files.write(privateKeyPath, ("-----BEGIN PRIVATE KEY-----\n" +
                Base64.getMimeEncoder().encodeToString(privateKey.getEncoded()) +
                "\n-----END PRIVATE KEY-----").getBytes());
    }
}
