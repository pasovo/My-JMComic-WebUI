<template>
  <div class="login">

    <el-card>
      <el-form :model="form" label-width="auto" :ref="ruleFormRef" :rules="rules" ><!-- :model="form" style="max-width: 600px" -->
        <el-form-item label="账号" prop="userName">
          <el-input v-model="form.userName" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input type="password" v-model="form.password" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button type="primary" @click="loginFunc">登录</el-button>
      </template>
    </el-card>
  </div>
</template>
<script>
// import {computed, reactive} from "vue";

import API from "@/config/axios/axiosInstance.js";
import router from "@/config/router/index.js";
import TImg from "@/components/T-Img.vue";

export default {
  name: "login",
  //引入模块
  components: {TImg},
  //父级传入数据
  props: {
    // inData: Object,
  },
  data() {
    return {
      //双向绑定处理
      /*tabPosition: computed({
        get: () => this.inData,
        set: val => {
          this.$emit("update:inData", val);
        }
      }),*/

      ruleFormRef: null,
      form: {
        userName: "",
        password: "",
      },
      // 规则
      rules: {
        userName: [
          { validator: this.check_name, trigger: 'blur' }
        ],
        password: [
          { validator: this.check_pass, trigger: 'blur' }
        ],
      }
    }
  },
  //方法
  methods: {
    check_name: function (rule, value, callback) {
      if (value === '') {
        callback(new Error('名称不能为空'))
      } else {
        callback()
      }
    },
    check_pass: function (rule, value, callback) {
      if (value === '') {
        callback(new Error('密码不能为空'))
      } else if (value.length<7) {
        callback(new Error("密码太短"))
      } else {
        callback()
      }
    },
    loginFunc: function () {
      let that = this;
      console.log("loginFunc", router, this.$route)
      API.post("/secure/login",{
        "username": this.form.userName,
        "password": this.form.password,
      }).then(res=>{
        let data = API.isSucess(res);
        if(data && data.token){
          that.$store.commit("user/setToken", data.token)
          API.TipSuccess("登录成功!")
          // router.replace("/")
          router.replace(that.$route.query.redirect)
        }else {
          let errMessage = API.readErrMessage(res);
          if(errMessage){
            API.TipError("登录失败!"+errMessage);
          }else{
            API.TipError("用户名或密码错误")
          }
        }
      })
    },
  },
  //启动事件
  mounted() {
    document.title = "登录"
    console.log("mounted", router, this.$route)
  },
  //销毁
  beforeUnmount() {
  }
}
</script>
<style scoped>
@import "@/style/login.css";
</style>
<style>
.login .el-form-item__label{
  color: white;
}
.login .el-card__footer{
  display: flex;
  justify-content: flex-end;
}
</style>