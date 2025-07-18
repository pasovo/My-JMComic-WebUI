<template>
  <div class="SwitchMenu">
    <el-menu mode="horizontal" class="gap-row mode noSelect" :default-active="default_active">
      <el-text tag="b">JMComic辅助工具</el-text>
      <el-menu-item index="1">
        <router-link to="/home/download">快速下载</router-link>
      </el-menu-item>
      <el-menu-item index="4">
        <router-link to="/home/library">书库管理</router-link>
      </el-menu-item>
<!--      <el-menu-item index="2">
        <router-link to="/home/search">漫画搜索</router-link>
      </el-menu-item>-->
      <el-menu-item index="5">
        <router-link to="/home/special_tag">特殊Tag</router-link>
      </el-menu-item>
      <el-menu-item index="3">
        <router-link to="/home/config">下载配置</router-link>
      </el-menu-item>
      <el-sub-menu index="99">
        <template #title>
<!--          <el-icon><MoreFilled /></el-icon>-->
          <el-avatar class="userICO" :size="35" fit="fit" :icon="UserFilled"  />
        </template>
        <el-menu-item index="99-1" @click="openResetWindow">重置密码</el-menu-item>
        <el-menu-item index="99-2" @click="openUserToken">用户信息</el-menu-item>
        <el-menu-item index="99-3" @click="logout">退出登录</el-menu-item>
      </el-sub-menu>
    </el-menu>
    <el-dialog v-model="resetWindow" title="重置密码" class="resetWindow"><!-- width="500" -->
      <el-form :model="reSetForm" label-width="auto" :rules="rules" ref="reSetFormRef">
        <el-form-item label="新密码" prop="newPass">
          <el-input type="password" v-model="reSetForm.newPass" >
            <template #append>{{ reSetForm?.newPass?.length }}</template>
          </el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="reset(reSetFormRef)">修改密码</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="验证信息" v-model="tokenInfo">
      <el-text truncated style="width: 100%;">{{ $store.state.user.token }}</el-text>
      <el-descriptions title="" :column="1" width="50%">
<!--        <el-descriptions-item label="Token" style="width: 100%;">
          <el-text truncated style="width: 50%;">{{ $store.state.user.token }}</el-text>
        </el-descriptions-item>-->
        <el-descriptions-item label="用户名">{{ token.username }}</el-descriptions-item>
        <el-descriptions-item label="权限">{{ token.rules }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">
          <span v-if="token.exp && token.exp > 0" >
            {{ new Date(token.exp * 1000).Format() }}
          </span>
          <span v-else>--</span>
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          <span v-if="token.exp && token.exp > 0" >
            {{ new Date(token.birthdate * 1000).Format() }}
          </span>
          <span v-else>--</span>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="parseToken">
            <el-icon><ChatLineRound /></el-icon>解析Token信息
          </el-button>
          <el-button type="primary" @click="updateToken">
            <el-icon><Refresh /></el-icon>更新Token信息
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script>
import {computed} from "vue";
import router from '@/config/router'
import store from "@/config/store/index.js";
import API from "@/config/axios/axiosInstance.js";
import {UserFilled} from "@element-plus/icons-vue";
import {jwtDecode} from "jwt-decode";
import {ElNotification} from "element-plus";

export default {
  name: "SwitchMenu",
  computed: {
    UserFilled() {
      return UserFilled
    }
  },
  //引入模块
  components: {},
  //父级传入数据
  props: {
    // inData: Object,
  },
  data() {
    return {
      /*//双向绑定处理
      tabPosition: computed({
        get: () => this.inData,
        set: val => {
          this.$emit("update:inData", val);
        }
      }),*/

      default_active: "1",
      resetWindow: false,
      reSetForm: {
        newPass: "",
      },

      // 规则
      rules: {
        newPass: [
          { validator: this.checkPass, trigger: 'blur' }
        ],
      },

      tokenInfo: false,
      token: {
        username: "",
        rules: "",
        exp: 0,// 过期时间
        birthdate: "",// 更新时间
      },
    }
  },
  //方法
  methods: {
    /*autoInitPath: function () {
      let pathMap = {
        "/":"1",
        "/home/download":"1"
        ,"/home/search":"2"
        ,"/home/config":"3"
        ,"/home/library":"4"
      }
      let that = this;
      let pathIndex = pathMap[this.$route.path];
      if(!pathIndex){
        that.default_active = pathIndex
      }else{
        that.default_active = pathMap[0]
      }
      console.log(this.default_active, pathMap, this.$route.path, pathIndex)
    },*/
    logout: async function () {
      await store.commit("user/clearToken");
      router.replace("/");
    },
    openResetWindow: function () {
      this.resetWindow = true;
      this.reSetForm.newPass = "";
    },
    openUserToken: function () {
      this.parseToken();
      this.tokenInfo = true;
    },
    reset: function (reSetFormRef) {
      // if(!reSetFormRef){
      //   API.TipError("表单初始化失败")
      // }
      this.$refs["reSetFormRef"]?.validate((valid) => {
        console.log("valid", valid)
        let that = this
        if (valid) {
          API.post("/secure/reset",{
            "password": this.reSetForm.newPass,
          }).then(res=>{
            let data = API.isSucess(res);
            if(data){
              API.TipSuccess("修改密码成功")
              that.resetWindow = false;
            }else {
              API.TipError("修改密码失败")
            }
          })
        } else {
          API.TipError("密码异常")
        }
      })

    },
    // 校验密码
    checkPass: function (rule, value, callback) {
      if (value === '') {
        callback(new Error('密码不能为空'))
      } else if (value.length<7) {
        callback(new Error("密码太短"))
      } else {
        callback()
      }
    },
    // 解析 Token
    parseToken: function () {
      let token = this.$store.state.user.token;
      this.$store.state.user.token = token
      console.log("Token", token, this.$store);
      if(token){
        this.token = jwtDecode(token);
      }
    },
    updateToken: function () {
      let that = this;
      try {
        API.post("/secure/check", {"token": this.$store.state.user.token}).then(res => {
          let data = API.isSucess(res);
          //console.log("2", !!data, data, res)
          //return !!data;
          if(data){
            let token = res.data.data;
            if(token){
              console.log("更新Token");
              this.$store.state.user.token = token;
              localStorage.setItem("token", token)
              ElNotification({
                title: '更新Token',
                type: 'success',
                message: 'Token已更新',
                duration: 3000,
              })
              that.parseToken();
            }else{
              ElNotification({
                title: '更新Token',
                type: 'primary',
                message: 'Token无需更新',
                duration: 3000,
              })
            }
          }else{
            ElNotification({
              title: '更新Token',
              type: 'Error',
              message: '出现异常' + res.data.message,
              duration: 0,
            })
          }
        });
      } catch (e) {
        //console.log("checkToken 出现报错", e)
        ElNotification({
          title: '更新Token',
          type: 'Error',
          message: '未知异常' + e,
          duration: 0,
        })
      }
    },
  },
  //启动事件
  mounted() {
    console.log("router", this, router)
    //this.autoInitPath()
    this.default_active = "1"
    switch (this.$route.path) {
      case "/":
        this.default_active = "1";
        router.replace("/home/download")
        break;
      case "/home/download":this.default_active = "1";break;
      case "/home/search":this.default_active = "2";break;
      case "/home/config":this.default_active = "3";break;
      case "/home/library":this.default_active = "4";break;
    }
    console.log(this.default_active, this.$route.path)

  },
  //销毁
  beforeUnmount() {
  }
}
</script>
<style scoped>
@import "@/style/SwitchMenu.css";
</style>
<style>
.el-sub-menu__title{
  border-radius: 0.5rem;
  height: 2rem;
}
.userICO .el-icon{
  margin: 0;
}
.resetWindow{
  max-width: 500px;
  width: 90vw;
}
.mode.noSelect>li:last-child>.el-sub-menu__title.el-tooltip__trigger.el-tooltip__trigger{
  border: 0;
}
</style>