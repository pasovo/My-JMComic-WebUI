<template>
  <div class="specialTag">
    <h2>标记特殊Tag</h2>
    <el-form :model="form" :inline="true" label-width="auto">
      <el-form-item label="Tag名称">
        <el-input v-model="form.name" class="albumId" />
      </el-form-item>
      <el-form-item label="Tag类型">
        <el-select filterable allow-create clearable v-model="form.type" placeholder="Tag类型" >
          <el-option value="primary">primary</el-option>
          <el-option value="success">success</el-option>
          <el-option value="info">info</el-option>
          <el-option value="warning">warning</el-option>
          <el-option value="danger">danger</el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="自定义颜色">
        <el-color-picker v-model="form.color" show-alpha :predefine="predefineColors" />
      </el-form-item>
      <el-form-item label="主题">
        <el-select filterable allow-create clearable v-model="form.effect" placeholder="Tag主题" >
          <el-option value="dark">dark</el-option>
          <el-option value="light">light</el-option>
          <el-option value="plain">plain</el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="圆角">
        <el-switch v-model="form.round" />
      </el-form-item>
      <el-divider />
      <el-form-item label="预览">
<!--        <el-tag class="addButton" :type="form.type" :color="form.color" :effect="form.effect" :round="form.round">
          <span v-if="!form.id">添加特殊Tag</span>
          <span v-else>保存特殊Tag</span>
        </el-tag>-->
        <div class="gap-row">
          <MySpecialTag :special-tag-data="form" @click="saveTag">
            <span v-if="!form.id">添加特殊Tag</span>
            <span v-else>保存特殊Tag</span>
          </MySpecialTag>
          <MySpecialTag v-if="form.id" :special-tag-data="form" @click="clearInfo">清空</MySpecialTag>
        </div>
      </el-form-item>
      <el-divider />
    </el-form>
    <div class="gap-row">
      <h2>特殊Tag</h2>
      <div class="gap-row">
        <el-button circle @click="listTag" ><el-icon><Refresh /></el-icon></el-button>
      </div>
    </div>
    <div class="gap-row tagBox">
      <MySpecialTag v-for="(tag,i) in tagList" :key="i" :special-tag-data="tag" closable @close="delTagHook" @click="setInfo(tag)">
        {{ tag.name }}
      </MySpecialTag>
    </div>

  </div>
</template>
<script>
import {computed} from "vue";
import API from "@/config/axios/axiosInstance.js";
import MySpecialTag from "@/components/library/MySpecialTag.vue";

export default {
  name: "specialTag",
  //引入模块
  components: {MySpecialTag},
  //父级传入数据
  props: {
    inData: Object,
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

      form:{
        id: null,
        name: "",
        type: "info",
        color: "",
        effect: "",
        round: false,
      },
      predefineColors:[
        '#ff4500',
        '#ff8c00',
        '#ffd700',
        '#90ee90',
        '#00ced1',
        '#1e90ff',
        '#c71585',
        'rgba(255, 69, 0, 0.68)',
        'rgb(255, 120, 0)',
        'hsv(51, 100, 98)',
        'hsva(120, 40, 94, 0.5)',
        'hsl(181, 100%, 37%)',
        'hsla(209, 100%, 56%, 0.73)',
        '#c7158577',
      ],
      isLoad: false,
      tagList: [],
    }
  },
  //方法
  methods: {
    listTag: function () {
      let that = this;
      that.isLoad = true;

      API.post("/api/tag/").then(function (res){
        let data = API.isSucess(res);
        if(data){
          that.tagList = data;
        }else {
          API.showDefErrMessage(res)
        }
      }).finally(res=>{
        that.$nextTick(() => {
          that.isLoad = false;
        });
      })
    },
    saveTag: function () {
      let that = this;
      that.isLoad = true;

      if(!that.form.id){
        for (const tagItem of that.tagList) {
          if(tagItem.name == that.form.name){
            API.TipError("标签已存在")
            return;
          }
        }
      }
      if(!that.form.name || that.form.name===""){
        API.TipError("标签名称不能为空")
        return;
      }

      API.post("/api/tag/add", that.form).then(function (res){
        let data = API.isSucess(res);
        if(data){
          API.TipSuccess("保存成功");
          that.listTag();
          that.clearInfo();
        }else {
          API.showDefErrMessage(res)
        }
      })
    },
    delTag: function (id) {
      let that = this;
      that.isLoad = true;

      API.post("/api/tag/del", {"id":id}).then(function (res){
        let data = API.isSucess(res);
        if(data){
          API.TipSuccess("删除成功");
          that.listTag();
        }else {
          API.showDefErrMessage(res)
        }
      })
    },
    delTagHook: function (m, data) {
      this.delTag(data.id);
    },
    clearInfo: function () {
      this.form = {
        id: null,
        name: "",
        type: "info",
        color: "",
        effect: "",
        round: false,
      }
    },
    setInfo: function (info) {
      this.form = info;
    }
  },
  //启动事件
  mounted() {
    this.listTag();
  },
  //销毁
  beforeUnmount() {
  }
}
</script>
<style scoped>
@import "@/style/specialTag.css";
</style>