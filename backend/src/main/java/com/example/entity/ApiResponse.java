package com.example.entity;

//import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApiResponse<Item> implements Serializable {
    private int code;
    private String message;
    private String time;
    private Item data;
    private String response;


    public ApiResponse(){
//        this.setTime(TimeUtil.format(new Date()));
        this.setTime(String.valueOf(new Date().getTime()));
    }
    public static <T> ApiResponse<T> returnOK(){
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.setCode(200);
        apiResponse.setResponse("success");
        return apiResponse;
    }
    public <T> ApiResponse<T> generateOK(){
        setCode(200);
        setResponse("success");
        return (ApiResponse<T>) this;
    }
    public static <T> ApiResponse<T> returnFail(String message, int code){
        ApiResponse<T> apiResponse = new ApiResponse<T>();
        apiResponse.setCode(code);
        apiResponse.setResponse("fail");
        apiResponse.setMessage(message);
        return apiResponse;
    }
    public <T> ApiResponse<T> generateFail(String message, int code){
        setCode(code);
        setResponse("fail");
        setMessage(message);
        return (ApiResponse<T>) this;
    }
    public <T> ApiResponse<T> generateFail(String message){
        return generateFail(message, 500);
    }
    public static <T> ApiResponse<T> returnFail(String message){
        return returnFail(message,501);
    }

    public ApiResponse<Item> setDataNow(Item data){
        this.data = data;
        return this;
    }
    public ApiResponse<Item> setMessageNow(String message){
        this.message = message;
        return this;
    }
    public boolean isSuccess(){
        if("success".equalsIgnoreCase(this.getResponse())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        //return JSON.toJSONString(this);
        try {
            // return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this); // 带格式化
            return new ObjectMapper().writeValueAsString(this); // 无格式化
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}