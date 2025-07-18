<template>
  <div class="library">
    <div class="controller gap-row" ref="controller">
      <el-button-group>
        <el-button @click="listPath()" >
          <el-icon><HomeFilled /></el-icon>
          <span class="a-text">主页</span>
        </el-button>
        <el-button @click="listPath(nowPath)" >
          <el-icon><Refresh /></el-icon>
          <span class="a-text">刷新</span>
        </el-button>
        <el-button @click="listPath(faPath)" v-if="faPath" >
          <el-icon><Top /></el-icon>
        </el-button>
        <el-popover title="收藏夹" placement="bottom" width="90%">
          <template #reference>
            <el-button>
              <el-icon class="favorites"><Management /></el-icon>
            </el-button>
          </template>
          <div class="favoriteItem" v-for="(item,i) in favorites" :key="i">
            <el-tooltip
                class="box-item"
                effect="dark"
                :content="item.path"
                placement="right"
            >
              <el-icon><Warning /></el-icon>
            </el-tooltip>
            <el-link underline="always" truncated @click="listPath(item.path)">
              <el-icon><Management /></el-icon>
              <span class="name">
                {{ item.showName }}
              </span>
            </el-link>
            <el-button text @click="delFavorites(item.path)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </el-popover>
      </el-button-group>
      <el-divider direction="vertical" />
      <el-text>
        <span class="a-hidden"><el-icon><Monitor /></el-icon></span>
        <span class="a-text">阅读方向</span>
      </el-text>
      <el-segmented v-model="setting.direction" :options="direction" size="default" @change="saveSetting" />
      <el-divider direction="vertical" />
      <el-icon @click="openViewSettingWindow=true;"><View /></el-icon>
      <el-drawer v-model="openViewSettingWindow" title="修改视图" class="viewSettingWindow"><!-- size="50%" -->
        <el-form :model="form" label-width="auto" class="viewSetting">
          <el-form-item label="文件排序">
            <div>
              <el-select
                  v-model="setting.fileSortItem"
                  placeholder="Select"
                  size="small"
                  style="width: 6rem;"
                  @change="analyzePath()"
              >
                <el-option
                    v-for="item in fileSortOptions"
                    :key="item.key"
                    :label="item.label"
                    :value="item.value"
                />
              </el-select>
              <el-switch v-model="setting.fileSort" active-text="↑" inactive-text="↓" @change="analyzePath()" />
            </div>
          </el-form-item>
          <el-form-item label="文件分组">
            <el-segmented v-model="setting.fileGroup" :options="groupOptions" size="default" @change="analyzePath()" />
          </el-form-item>
          <el-form-item label="最多显示Tag">
            <el-segmented v-model="setting.maxShowFileTag" :options="showMaxTagOptions" size="default" @change="changeMaxShowFileTag" />
          </el-form-item>
        </el-form>
      </el-drawer>

    </div>
    <el-divider class="smailDivider" />
    <list-card v-model:in-files="files"
               v-loading.fullscreen.lock="isLoad"
               @updateData="updateFileItem" @openPath="listPath"
               @openView="openImage" @delFile="delFilePath"
               :inTags="tagOptions.out" :style="styleAuto"
               :maxShowFileTag="maxShowFileTagVal"
               v-if="setting.fileGroup==='关闭'"
               :favoritesList="favorites" @refreshFavoritesList="listPath(nowPath)" @delFavorites="delFavorites"
               :tagList="tagList"
    />
<!--    <div class="groupView" :style="styleAuto">
      <el-collapse v-model="groupViewShowTag" accordion v-if="setting.fileGroup!=='关闭'">
        <el-collapse-item :title="item.name" :name="item.name" v-for="(item,i) in groupFiles" :key="i">
          <list-card v-model:in-files="item.files"
                     v-loading.fullscreen.lock="isLoad"
                     @updateData="updateFileItem" @openPath="listPath"
                     @openView="openImage" @delFile="delFilePath"
                     :inTags="tagOptions.out"
          />
        </el-collapse-item>
      </el-collapse>
    </div>-->
    <div class="groupView" :style="styleAuto">
      <div class="groupFileView" v-for="(item,i) in groupFiles" :key="i" v-if="setting.fileGroup!=='关闭'">
        <div class="title">
          <el-divider content-position="left">{{ item.name }}</el-divider>
        </div>
        <list-card v-model:in-files="item.files"
                   v-loading.fullscreen.lock="isLoad"
                   @updateData="updateFileItem" @openPath="listPath"
                   @openView="openImage" @delFile="delFilePath"
                   :inTags="tagOptions.out"
                   :maxShowFileTag="maxShowFileTagVal"
                   :favoritesList="favorites" @refreshFavoritesList="listPath(nowPath)" @delFavorites="delFavorites"
                   :tagList="tagList"
        />
      </div>
    </div>
<!--  :faFile="faFile" :faFileIndex="faFileIndex"  -->
    <el-empty v-if="files==null || files.length===0" description="目录为空" />
    <div class="imageView" v-if="viewImage">
      <vertical v-model:inData="filterFiles" v-model:inSwitchView="viewImage"
                :setDirection="setting.direction" :fileIndex="fileIndex"
                :faFile="faFile" :faFileIndex="faFileIndex"
                @openBeforePath="openBeforePath" @openNextPath="openNextPath"
                @switchPath="listPath"
      />
    </div>

    <!-- el-divider content-position="left" -->
    <div class="statusBottomBar">
      <el-button type="primary" @click="openBeforePath" size="small"
                 v-if="faFile && faFile.length>1 && faFileIndex>0"
      ><el-icon><Back /></el-icon></el-button>
      <el-tag size="large" effect="light" >
        <el-icon ><Document /></el-icon>
        <span class="a-text">文件数量</span>
        <el-tag size="small" effect="dark">{{ status.num_files }}</el-tag>
        <el-icon><Folder /></el-icon>
        <span class="a-text">文件夹数量</span>
        <el-tag size="small" effect="dark">{{ status.num_dir }}</el-tag>
      </el-tag>
      <el-button type="primary" @click="openNextPath" size="small"
                 v-if="faFile && faFile.length>1 && faFileIndex< faFile.length-1"
      ><el-icon><Right /></el-icon></el-button>

      <el-tooltip
          class="faFileName"
          v-if="faFile"
          effect="dark"
          :content="faFile[faFileIndex]?.name"
          placement="top-start"
      >
        <el-text truncated>
          {{ faFile[faFileIndex]?.name }}
        </el-text>
      </el-tooltip>

      <el-popover
          class="box-item"
          title="上级文件列表"
          placement="top-end"
          width="50vw"
          v-if="faFile && faFile.length>0"
      >
        <template #reference>
          <el-button><el-icon><Memo /></el-icon></el-button>
        </template>
        <div class="fileItem" v-for="(item,i) in faFile" :key="i" @click="listPath(item.path, faFile, i)">
          <el-link underline="always"
                   :type=" faFileIndex===i?'primary':'' "
          >
            <el-icon v-if="faFileIndex===i" ><CaretRight /></el-icon>
            {{ item.name }}
          </el-link>
        </div>
      </el-popover>
    </div>
  </div>
</template>
<script>
import {computed} from "vue";
import API from "@/config/axios/axiosInstance";
import ListCard from "@/components/library/ListCard.vue";
import Vertical from "@/components/image/Vertical.vue";
import ArraySortUtil from "@/utils/ArraySortUtil.js";

export default {
  name: "library",
  //引入模块
  components: {Vertical, ListCard},
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

      setting:{
        direction: "竖向",
        fileSortItem: "fileName",
        fileSort: true,
        fileGroup: "关闭",
        /*groupDate: "月",*/
        maxShowFileTag: "不限制",
      },
      direction: ["竖向","从左到右","从右到左"],
      fileSortOptions: [
        {key:"fileName", label:"文件名", value:"fileName"}
        ,{key:"changeTime", label:"修改日期", value:"changeTime"}
      ],
      groupOptions: ["关闭","时间","标记","评分"],
      openViewSettingWindow: false,
      /*dateOptions: ["年","月","日"],*/
      showMaxTagOptions: ["不限制","3个","6个"],
      maxShowFileTagVal: computed({
        get: () => {
          console.log("maxShowFileTagVal - get", this.setting.maxShowFileTag)
          switch (this.setting.maxShowFileTag) {
            case "不限制":return 0;
            case "3个":return 3;
            case "6个":return 6;
          }
          return 0;
        },
        set: val => {
          console.log("maxShowFileTagVal - set1", val, this.maxShowFileTag)
          switch (val) {
            case 0:this.maxShowFileTag = "不限制";break;
            case 3:this.maxShowFileTag = "3个";break;
            case 6:this.maxShowFileTag = "6个";break;
          }
          console.log("maxShowFileTagVal - set2", val, this.maxShowFileTag)
        }
      }),

      files:[],
      groupFiles:[],
      filterFileText: "",

      nowPath: "",
      isLoad: false,
      viewImage: false,
      filterFiles: [],

      fileIndex: 0,

      //statusText: "",
      status:{
        num_files: 0,
        num_dir: 0,
      },

      tagOptions: {
        buff: null,
        mode: "",
        out: [],
      },

      styleVal:{
        controllerHeight: 32,
      },
      styleAuto: {
      },

      faPath: "",
      faFile: [],
      faFileIndex: 0,

      groupViewShowTag: null,

      // 收藏夹
      favorites: [],

      // 特殊Tag列表
      tagList: {},
    }
  },
  //方法
  methods: {
    listPath: async function (path, faFile, faFileIndex) {
      this.files = []
      this.isLoad = true;
      this.nowPath = path;
      // console.log("切换路径", path)
      let that = this

      that.listTag();

      API.post("/api/library/",{"path":path}).then(async function (res){
        let data = API.isSucess(res);
        if(data){
          that.files = data;
          that.analyzePath();
          that.listFavorites();

          if(faFile){
            //that.faPath = path;
            that.faFile = faFile;
            that.faFileIndex = faFileIndex;
          }else{
            that.faPath = "";
            that.faFile = [];
            that.faFileIndex = 0;
          }
          // 允许随便切换路径后，不再只能是父子关系了
          await that.getParentPath(path);

          //判定是否在图片预览状态下切换目录
          if(that.viewImage===true){
            //that.openImage(that.fileIndex)
            that.openImage(0)
            console.log("更新当前图片预览")
            that.viewImage=false;
            that.$nextTick(() => {
              that.viewImage=true;
            });
          }
        }else {
          API.showDefErrMessage(res)
        }
      }).finally(res=>{
        that.$nextTick(() => {
          that.isLoad = false;
          that.reSizeWindow();
        });
      })
    },
    updateFileItem: function (item) {
      API.post("/api/library/update", item).then(res=>API.defaultSuccessFuc)
      this.analyzePath();
    },
    openImage: function (index) {
      this.filterFiles = [];

      for (let i = 0; i < this.files.length; i++) {
        let fileItem = this.files[i];
        if(fileItem.isFile){
          this.filterFiles.push(fileItem);
        }
      }

      this.viewImage = true;

      console.log("打开图片")
      this.fileIndex = index;
      /*this.$nextTick(() => {
        console.log("this.$refs=", this.$refs, this)
      });*/
    },
    delFilePath: function (path) {
      API.post("/api/library/del", {"path": path}).then(res=>{
        let data = API.isSucess(res);
        if(data){
          API.TipSuccess("路径已删除")
        }else {
          API.showDefErrMessage(res)
        }
      })
    },
    saveSetting: function () {
      localStorage.setItem("library.setting", JSON.stringify(this.setting));
    },
    analyzePath: function () {
      this.status.num_files=0;
      this.status.num_dir=0;
      let that = this;
      this.tag_init();
      for (const file of this.files) {
        if( file.isFile ){
          this.status.num_files++;
        }else{
          this.status.num_dir++;
        }

        if(typeof file.mark === 'string' && file.mark.trim() !== ""){
          try {
            file.mark = JSON.parse(file.mark);
          } catch (e) {
            file.mark = [file.mark]
          }
        }

        if(file.mark){
          for (let tag of file.mark) {
            this.tag_add(tag);
          }
        }
      }
      this.tag_get();

      // 排序
      this.files.sort(function (a, b) {
        switch (that.setting.fileSortItem) {
          case "fileName":{
            if(that.setting.fileSort){
              return ArraySortUtil.compare(a.name, b.name)
            }else{
              return ArraySortUtil.compare(b.name, a.name)
            }
          }
          break;
          case "changeTime":{
            if(that.setting.fileSort){
              return new Date(a.time).getTime() - new Date(b.time).getTime()
            }else{
              return new Date(b.time).getTime() - new Date(a.time).getTime()
            }
          }
          break;
        }
      })

      // 分组
      this.fileGroup();

      this.saveSetting();
    },
    fileGroup: function () {
      this.groupFiles = [];
      // 分组
      if(this.setting.fileGroup === "时间"){
        this.groupFiles = [
          {name: "今天", files: []}
          ,{name: "昨天", files: []}
          ,{name: "这周", files: []}
          ,{name: "这月", files: []}
          ,{name: "半年内", files: []}
          ,{name: "很久之前", files: []}
        ];
        let now_d = new Date().clearhhmmss();
        let today = now_d.Format("yyyy-MM-dd");
        let yesterday = now_d.addDayReturnNew(-1).Format("yyyy-MM-dd");
        let day = Math.floor(now_d.getTime() / 1000 / 3600 / 24);
        let month = now_d.Format("yyyy-MM");

        for (const file of this.files) {
          let time = new Date(file.time).clearhhmmss();
          let fileDay = time.Format("yyyy-MM-dd");
          let day_file = Math.floor(time.getTime() / 1000 / 3600 / 24);
          let month_file = time.Format("yyyy-MM");

          if(today === fileDay){// 今天
            this.groupFiles[0].files.push(file)
          }else if(yesterday === fileDay){// 昨天
            this.groupFiles[1].files.push(file)
          }else if( (day - day_file)<=7 ){// 这周
            this.groupFiles[2].files.push(file)
          }else if(month === month_file){// 这月
            this.groupFiles[3].files.push(file)
          }else if( (day - day_file)<=183 ){//半年内
            this.groupFiles[4].files.push(file)
          }else{
            this.groupFiles[5].files.push(file)
          }
        }
      }
      if(this.setting.fileGroup === "评分") {
        this.groupFiles = [
          {name: "五星", files: []}
          , {name: "四星", files: []}
          , {name: "三星", files: []}
          , {name: "二星", files: []}
          , {name: "一星", files: []}
          , {name: "未评分", files: []}
        ];
        for (const file of this.files) {
          switch (file.rate) {
            case 5: this.groupFiles[0].files.push(file);break;
            case 4: this.groupFiles[1].files.push(file);break;
            case 3: this.groupFiles[2].files.push(file);break;
            case 2: this.groupFiles[3].files.push(file);break;
            case 1: this.groupFiles[4].files.push(file);break;
            default:
              this.groupFiles[5].files.push(file);
          }
        }
      }
      if(this.setting.fileGroup === "标记") {
        for (const tag of this.tagOptions.out) {
          this.groupFiles.push({name: tag, files: []})
        }
        this.groupFiles.push({name: "未标记", files: []})
        for (const file of this.files) {
          if(file.mark === ''){
            this.groupFiles[this.tagOptions.out.length].files.push(file);
            continue;
          }
          //let tags = JSON.parse(file.mark);
          let tags = file.mark;
          for (const tagItem of this.groupFiles) {
            // console.log("tagItem.name", tags, tagItem.name)
            if(tags.indexOf(tagItem.name)>-1){
              tagItem.files.push(file)
            }
          }
        }
      }

      let buff = [];
      // 清空空组
      for (let i = 0; i < this.groupFiles.length; i++) {
        let tmp = this.groupFiles[i];
        if(tmp.files.length>0){
          buff.push(tmp);
        }
      }
      this.groupFiles = buff;
    },
    tag_init: function () {
      if( Set ){
        this.tagOptions.buff = new Set();
        this.tagOptions.mode = "set";
      }else{
        this.tagOptions.buff = {};
        this.tagOptions.mode = "object";
      }
    },
    tag_add: function (item) {
      switch (this.tagOptions.mode) {
        case "set":{
          this.tagOptions.buff.add(item)
        }break;
        default:{
          this.tagOptions.buff[item] = {}
        }
      }
    },
    tag_get: function () {
      switch (this.tagOptions.mode) {
        case "set":{
          this.tagOptions.out = Array.from(this.tagOptions.buff);
        }break;
        default:{
          this.tagOptions.out = Object.keys(this.tagOptions.buff);
        }
      }
    },
    reSizeWindow: function () {
      // console.log("reSizeWindow", this, this.styleVal.controllerHeight, this.styleAuto)
      this.styleVal.controllerHeight = this.$refs["controller"].clientHeight
      this.styleAuto = {
        "max-height": "calc(100vh - 168px - " + this.styleVal.controllerHeight + "px + 1.5rem)"
      };

      let width = window.innerWidth;
      let w1= 640;
      let w2= 450;
      if(width>w1){
        this.direction = ["竖向","从左到右","从右到左"];
        switch (this.setting.direction) {
          case "竖向":case "↓↓":case "↓":this.setting.direction="竖向";break;
          case "从左到右":case "|→":case "→":this.setting.direction="从左到右";break;
          case "从右到左":case "←|":case "←":this.setting.direction="从右到左";break;
        }
      }else if(width<=w1 && width>w2){
        this.direction = ["↓↓","|→","←|"];
        switch (this.setting.direction) {
          case "竖向":case "↓↓":case "↓":this.setting.direction="↓↓";break;
          case "从左到右":case "|→":case "→":this.setting.direction="|→";break;
          case "从右到左":case "←|":case "←":this.setting.direction="←|";break;
        }
      }else{
        this.direction = ["↓","→","←"];
        switch (this.setting.direction) {
          case "竖向":case "↓↓":case "↓":this.setting.direction="↓";break;
          case "从左到右":case "|→":case "→":this.setting.direction="→";break;
          case "从右到左":case "←|":case "←":this.setting.direction="←";break;
        }
      }
    },

    openBeforePath: function () {
      if(this.faFile && this.faFile.length>1 && this.faFileIndex>0){
        let nextIndex = this.faFileIndex-1;
        let tmp = this.faFile[nextIndex];
        this.listPath(tmp.path, this.faFile, nextIndex)
      }else{
        API.TipError("文件夹不存在")
      }
    },
    openNextPath: function () {
      if(this.faFile && this.faFile.length>1 && this.faFileIndex< this.faFile.length-1){
        let nextIndex = this.faFileIndex+1;
        let tmp = this.faFile[nextIndex];
        this.listPath(tmp.path, this.faFile, nextIndex)
      }else{
        API.TipError("文件夹不存在")
      }
    },

    changeMaxShowFileTag: function (item) {
      //console.log("changeMaxShowFileTag", aaa, bbb);
      this.setting.maxShowFileTag = item
      switch (this.setting.maxShowFileTag) {
        case "不限制":this.maxShowFileTagVal=0;break;
        case "3个":this.maxShowFileTagVal=3;break;
        case "6个":this.maxShowFileTagVal=6;break;
      }
    },

    listFavorites: function () {
      API.post("/api/library/favorites/list").then(res=>{
        let data = API.isSucess(res);
        if(data){
          this.favorites = data;
        }else {
          API.showDefErrMessage(res)
        }
      })
    },
    delFavorites: function (path) {
      API.post("/api/library/favorites/del",{"path": path}).then(res=>{
        let data = API.isSucess(res);
        if(data){
          API.TipSuccess("删除成功")
          //this.listFavorites();
          this.listPath(this.nowPath);
        }else {
          API.showDefErrMessage(res)
        }
      })
    },

    getParentPath: async function (path) {
      let that = this;
      await API.post("/api/library/parent",{"path":path}).then(res=> {
        let data = API.isSucess(res);
        if (data) {
          that.faPath = data.path;
          that.faFile = data.parent;
          that.faFileIndex = data.index;
        }else{
          that.faPath = "";
          that.faFile = [];
          that.faFileIndex = 0;
        }
      });
    },

    listTag: function () {
      let that = this;
      that.isLoad = true;
      that.tagList = {};

      API.post("/api/tag/").then(function (res){
        let data = API.isSucess(res);
        if(data){
          //that.tagList = data;
          console.log("tagList", data)
          for (const tagItem of data) {
            that.tagList[tagItem.name] = tagItem;
          }
        }else {
          API.showDefErrMessage(res)
        }
      }).finally(res=>{
        that.$nextTick(() => {
          that.isLoad = false;
        });
      })
    },
  },
  //启动事件
  mounted() {
    this.listPath()
    let setting = localStorage.getItem("library.setting");
    if(setting!=null && typeof setting === "string"){
      this.setting = JSON.parse(setting);
    }
    window.addEventListener('resize', this.reSizeWindow);
    this.$nextTick(() => {
      this.reSizeWindow();
    });
  },
  //销毁
  beforeUnmount() {
    window.removeEventListener('resize', this.reSizeWindow);
  }
}
</script>
<style scoped>
@import "@/style/library.css";
</style>
<style lang="scss">
.library{
  .statusBottomBar .el-tag__content{
    display: flex;
    grid-gap: 0.5rem;
    gap: 0.5rem;
    justify-content: center;
    align-items: center;
  }
  .el-popper.el-tooltip.el-popover{
    max-height: calc(100vh - 32px - 1rem - var(--el-menu-horizontal-height));
    overflow-y: scroll;
  }
  .viewSettingWindow{
    width: 50% !important;
  }
}
.phone .library, .smallPhone .library{
  .viewSettingWindow{
    width: 100% !important;
  }
}
</style>