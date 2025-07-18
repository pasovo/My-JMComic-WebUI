package com.example.service;

import com.example.entity.ApiResponse;
import com.example.entity.JmcomicConfig;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@ApplicationScoped
public class ConfigService {

    public ApiResponse loadConfig(){
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream("./config.yml")) {
            JmcomicConfig yml = yaml.load(inputStream);
            return ApiResponse.returnOK().setDataNow(yml);
        }catch (IOException e){
            log.error("读取文件失败");
            return ApiResponse.returnFail("读取文件失败");
        }
    }

    public ApiResponse saveConfig(JmcomicConfig data){
        Yaml yaml = new Yaml();
        try (FileWriter writer = new FileWriter("./config.yml")) {
            yaml.dump(data, writer);
            return ApiResponse.returnOK();
        } catch (IOException e) {
            //e.printStackTrace();
            log.error("保存失败");
            return ApiResponse.returnFail("保存失败");
        }
    }
}
