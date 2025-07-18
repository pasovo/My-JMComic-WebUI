package com.example.security;

import com.example.entity.ApiResponse;
import com.example.entity.User;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecureService {

    /*public ApiResponse<User> login(String name, String pass){
        ApiResponse<User> api = new ApiResponse();
        User result = User.find("name", name).firstResult();
        if(result==null){
            return api.generateFail("用户名不存在");
        }
        if(!result.verifyPassword(pass)){
            return api.generateFail("密码错误");
        }
//        return api.generateOK().setDataNow(result);
        api.generateOK().setData(result);
        return api;
    }*/
    public ApiResponse login(String username, String password){
        if(username==null || username.isEmpty()){
            return ApiResponse.returnFail("用户名异常");
        }
        if(password == null || password.length() < 8){
            return ApiResponse.returnFail("密码异常");
        }

        User result = User.find("name", username).firstResult();
        if(result==null){
            return ApiResponse.returnFail("用户名不存在");
        }
        if(!result.verifyPassword(password)){
            return ApiResponse.returnFail("密码错误");
        }
        return ApiResponse.returnOK().setDataNow(result);
    }
}
