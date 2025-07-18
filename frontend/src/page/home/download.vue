<template>
  <div class="download gap1">
    <h2>下载本子</h2>
    <el-form :model="form" label-width="auto">
      <el-form-item label="下载本子ID">
        <div class="downloadController">
          <el-input v-model="form.downloadAlbumId" class="albumId" />
          <el-button type="primary" @click="downloadAlbum">下载</el-button>
        </div>
      </el-form-item>
    </el-form>
    <div class="gap-row">
      <h2>下载任务</h2>
      <div class="gap-row">
        <el-button circle @click="loadTask(false)" ><el-icon><Refresh /></el-icon></el-button>
        <el-text size="small">自动刷新</el-text>
        <el-switch v-model="autoRefresh" @change="loadTask()" />
      </div>
    </div>
    <div class="downloadBox gap1">
      <div class="downloadItem gap05" v-for="(item,i) in taskList" :key="i">
        <div class="line gap05">
          <el-tag effect="dark">{{ item.a_id }}</el-tag>
          <el-text tag="ins">{{ item.name }}</el-text>
          <el-tag effect="plain">{{ item.pid }}</el-tag>
        </div>
        <div class="line gap05">
          <el-tag v-if="item.running===true" type="success" effect="dark">下载中</el-tag>
          <el-tag v-else-if="item.running===false && item.manualStop===false" type="danger" effect="dark">已停止</el-tag>
          <el-tag v-else type="warning" effect="dark">手动停止</el-tag>

          <el-text tag="p" line-clamp="5">{{ item.log_lastLine }}</el-text>
        </div>
        <div class="line buttonLine gap05">
          <div class="left">
            <el-tag :type="item.running?'success':'info'" effect="dark" round >
              {{ formatRunTime(item.time, item.endTime) }}
            </el-tag>
          </div>
          <el-button type="danger" class="gap05" v-if="item.running===true">
            <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>手动停止</div>
          </el-button>
          <el-button type="info" class="gap05" v-if="item.running===false" @click="delTask(item.id)">
            <div class="gap05"><el-icon><Delete /></el-icon>删除记录</div>
          </el-button>
          <el-button type="info" class="gap05" disabled v-if="item.manualStop===true">
            <div class="gap05"><el-icon><CircleCloseFilled /></el-icon>已手动停止</div>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
    // import {computed} from "vue";
    import API from "@/config/axios/axiosInstance.js";
    import {ElNotification} from "element-plus";

    export default {
      name: "download",
      //引入模块
      components: {},
      //父级传入数据
      props: {
        // inData: Object,
      },
      data() {
        return {
          //双向绑定处理
          // tabPosition: computed({
          //   get: () => this.inData,
          //   set: val => {
          //     this.$emit("update:inData", val);
          //   }
          // }),

          form:{
            downloadAlbumId: "",
          },

          // status: running | stop | stop(Manual)
          taskList: [
          ],

          autoRefresh: false,
        }
      },
      //方法
      methods: {
        loadTask: function () {
          API.post("/api/download").then(res=>{
            let data = API.isSucess(res);
            if(data){
              this.taskList = data;
              if(this.autoRefresh===true){
                this.autoLoadTask();
              }
            }else {
              API.showDefErrMessage(res);
            }
          })
        },
        downloadAlbum: function () {
          API.post("/api/download/d",{
            "id": this.form.downloadAlbumId
          }).then(res=>{
            let data = API.isSucess(res);
            if(data){
              //this.taskList = data;
              ElNotification({
                title: "已开始下载",
                message: "请稍后刷新任务列表查看下载详情",
                type: 'success',
                duration: 10000,
              })
              this.autoRefresh = true;
              this.loadTask();
              //this.loadTask(true);
            }else {
              API.showDefErrMessage(res)
            }
          })
        },
        delTask: function (id) {
          API.post("/api/download/del", {
            "id": id,
          }).then(res=>{
            let data = API.isSucess(res);
            if(data){
              API.TipSuccess("任务已删除")
              this.loadTask();
            }else {
              API.showDefErrMessage(res)
            }
          })
        },
        autoLoadTask: function () {
          let willNext = false;
          let that = this;
          for (const task of that.taskList) {
            if(task.running === true){
              willNext = true;
              break;
            }
          }
          this.autoRefresh = !!willNext
          if(willNext){
            setTimeout(()=>{
              //that.autoRefresh = true;
              that.loadTask();
              //that.loadTask(true);
            },1000)
          }
        },
        formatRunTime: function (time, endTime) {
          if(!endTime){
            endTime = new Date().getTime()
          }
          let runTime = endTime - time;
          if(isNaN(runTime) || runTime < 0){
            return "--";
          }
          runTime = Math.round(runTime / 1000)

          return this.formUseTime(runTime)
        },
        formUseTime: function (useTime) {
          let unit = [60, 60, 60, 24]
          let unitStr = ["s", "m", "h", "d"]

          let buff = []
          for (let i = 0; i < unit.length; i++) {
            if(useTime>=unit[i]){
              buff.push( useTime % unit[i] );
              useTime = Math.floor(useTime / unit[i])
            }
          }
          if(useTime>=0){
            buff.push(useTime);
          }
          let str = "";
          let k = 0;// 跳过0位置
          let n = buff.length - 1;
          for (let i = n; i >= 0 && i > (n - 2 - k); i--) { // 最多显示两位
            if(buff[i] === 0){
              k++;
              continue;
            }
            str += buff[i] + unitStr[i] + " "
          }
          return str.trim();
        },
      },
      //启动事件
      mounted() {
        this.loadTask();
      },
      //销毁
      beforeUnmount() {
      }
    }
</script>
<style scoped>
@import "@/style/download.css";
</style>