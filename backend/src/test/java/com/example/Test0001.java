package com.example;

import com.example.entity.ApiResponse;
import com.example.service.BackendBashTask.BaseBackendBashTask;
import com.example.service.BackendBashTask.DownloadJMComic;
import com.example.service.BackendBashTaskService;
import com.example.util.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
public class Test0001 {

    @Test
    public void t0001(){
        String path1 = "V:\\download\\XD590\\Roboquest.Build.15214401";
//        String path2 = "V:\\download\\XD590\\Roboquest.Build.15214401\\RoboQuest";
//        String path3 = "V:\\download\\SAE.3.20.4\\Package";
//        String path4 = "./aaa";

        Path path_1 = Paths.get(path1);
//        Path path_2 = Paths.get(path2);
//        Path path_3 = Paths.get(path3);
//
//        log.info(">>> 0 >>> {}", path_1.resolve(path_2).toString());
//        log.info(">>> 1 >>> {}", path_1.resolve(path_3).toString());

        String[] testPath = new String[]{
                "V:\\download\\XD590\\Roboquest.Build.15214401\\RoboQuest"
                ,"V:\\download\\SAE.3.20.4\\Package"
                ,"./aaa"
                ,"../../bbb"
                ,"..\\..\\..\\..\\..\\ccc"
        };
        for (int i = 0; i < testPath.length; i++) {
            Path path_test = Paths.get(testPath[i]);

            path_test = path_test.toAbsolutePath();
            path_test = path_test.normalize();
            /*try {
                path_test = path_test.toRealPath(LinkOption.NOFOLLOW_LINKS);
            } catch (IOException e) {
//                throw new RuntimeException(e);
                log.error("路径转换失败!\r\n {}", e.getMessage());
            }*/

            log.info(">>> {} >>> {}  [{}]", i, path_1.resolve(path_test).toString(), path_test.startsWith(path1));
        }
    }

    @Test
    public void t0002(){
        String[] testArr = "a0001,a0010,a0100,b0001,a0002,a0003".split(",");
        List<String> arr = new ArrayList<>();
        for (int i = 0; i < testArr.length; i++) {
            String t = testArr[i];
            arr.add(t);
        }

        Collections.sort(arr);

        for (int i = 0; i < arr.size(); i++) {
            String s = arr.get(i);
            System.out.println(s);
        }
    }

    @Test
    public void t0003(){
        String text = "";

        text = "第11話 美夜酱的女王生活0123456789";

        char[] charArray = text.toCharArray();

        //System.out.println(JSON.toJSONString(charArray));
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            System.out.println(c + " <> "+ (int)c);
        }
    }

    @Test
    public void t0004(){
        String t1 = "";
        String t2 = "";

//        t1 = "第10話";
//        t2 = "第1話";
//        System.out.println(FileUtil.compare(t1, t2));

        t1 = "第10話-1";
        t2 = "第10話-2";
        System.out.println(FileUtil.compare(t1, t2));
    }

    @Test
    public void t0005(){
        String id = "";
        id="422866";
        DownloadJMComic d = new DownloadJMComic(id);
        d.setLogMode(DownloadJMComic.LOGMODE_FULL);
        d.startRun();
        try {
            d.waitFor();

            log.info("下载本子：{} -> {}", id, d.getName());
            String out = d.getLog_full().toString();
            log.info("out:\r\n{}", out);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void t0006(){
        BackendBashTaskService backTaskService = new BackendBashTaskService();
        ApiResponse response = backTaskService.updateJmcomic();

        BaseBackendBashTask backendBashTask = (BaseBackendBashTask) response.getData();
        try {
            backendBashTask.waitFor();
            String str = backendBashTask.getLog_full().toString();
            System.out.println(str);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void t0007(){
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);

            KeyPair key = keyGen.generateKeyPair();
            PrivateKey privateKey = key.getPrivate();
            PublicKey publicKey = key.getPublic();

//            String privateKeyFormat = privateKey.getAlgorithm();
//            String publicKeyFormat = publicKey.getFormat();
//
//            log.info("privateKey   {}", privateKeyFormat);
//            log.info("publicKey   {}", publicKeyFormat);

            // 保存公钥到文件
            Files.write(Paths.get("test_public_key.pem"), ("-----BEGIN PUBLIC KEY-----\n" +
                    Base64.getMimeEncoder().encodeToString(publicKey.getEncoded()) +
                    "\n-----END PUBLIC KEY-----").getBytes());

            // 保存私钥到文件
            Files.write(Paths.get("test_private_key.pem"), ("-----BEGIN PRIVATE KEY-----\n" +
                    Base64.getMimeEncoder().encodeToString(privateKey.getEncoded()) +
                    "\n-----END PRIVATE KEY-----").getBytes());

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void t008(){
        String path = "/app/./Archive";
        String savePath = "./Archive";

        Path path_ = Paths.get(savePath);
        path_ = path_.normalize();
        try {
            path_ = path_.toRealPath(LinkOption.NOFOLLOW_LINKS);
        } catch (IOException e) {
            //throw new RuntimeException(e);
            log.warn("路径转换出现异常, {} <> {}", savePath, path_.toString());
        }
        savePath = path_.toString();
        log.info(">> {}", savePath);

        //Pattern.compile("");
    }

    @Test
    public void t009(){
        String tagArr = "['口交', '兽耳', '睡姦', '中文']";
        ObjectMapper mapper = new ObjectMapper();
        try {
            tagArr = tagArr.replaceAll("'","\"");
            String[] value = mapper.readValue(tagArr, String[].class);
            log.info(">>> {}", value);
        } catch (JsonProcessingException e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

    @Test
    public void t010(){
        String tagArr = "['口交', '兽耳', '睡姦', '中文', 'aaaa', 'cccc', 'bbbb']";
        tagArr = tagArr.replaceAll("'","\"");

        ObjectMapper mapper = new ObjectMapper();
        Set<String> markSet1 = new LinkedHashSet<>();
        Set<String> markSet2 = new HashSet<>();
        try {
            markSet1 = mapper.readValue(tagArr, markSet1.getClass());
            log.info(">1> {}", markSet1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            markSet2 = mapper.readValue(tagArr, markSet2.getClass());
            log.info(">2> {}", markSet2);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void t011(){
        String outPrint = "锟斤拷api.update_domain.error锟斤拷锟皆讹拷锟斤拷锟斤拷API锟斤拷锟斤拷失锟杰ｏ拷锟斤拷使锟斤拷jmcomic锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷通锟斤拷锟斤拷锟斤拷[JmModuleConfig.FLAG_API_CLIENT_AUTO_UPDATE_DOMAIN=False]锟截憋拷锟皆讹拷锟斤拷锟斤拷API锟斤拷锟斤拷. 锟届常锟斤拷 Incorrect padding";
        try {
            log.info("[ISO_8859_1 -> UTF-8] {}", new String(outPrint.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
            log.info("[GBK -> UTF-8] {}", new String(outPrint.getBytes("GBK"), StandardCharsets.UTF_8));
            log.info("[ISO_8859_1 -> GBK] {}", new String(outPrint.getBytes(StandardCharsets.ISO_8859_1), "GBK"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
