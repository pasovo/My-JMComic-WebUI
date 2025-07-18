<template>
  <div class="Vertical imageView">
    <el-page-header @back="close" :class="showSwitchToolbar?'title':'title hide'">
      <template #content>
        <div class="headTitle">
          <el-button type="primary" @click="openBeforePath"
                     v-if="faFile && faFile.length>1 && faFileIndex>0"
          ><el-icon><Back /></el-icon></el-button>

          <el-popover
              class="faFileName"
              title="上级文件列表"
              placement="bottom"
              width="50vw"
              v-if="faFile && faFile.length>0"
          >
            <template #reference>
              <el-text truncated>
                {{ faFile[faFileIndex]?.name }}
              </el-text>
            </template>
            <div class="fileItem" v-for="(item,i) in faFile" :key="i" @click="switchPath(item.path, faFile, i)">
              <el-link underline="always"
                       :type=" faFileIndex===i?'primary':'' "
              >
                <el-icon v-if="faFileIndex===i" ><CaretRight /></el-icon>
                {{ item.name }}
              </el-link>
            </div>
          </el-popover>

          <el-button type="primary" @click="openNextPath"
                     v-if="faFile && faFile.length>1 && faFileIndex< faFile.length-1"
          ><el-icon><Right /></el-icon></el-button>
        </div>
      </template>
    </el-page-header>

    <div :class="'VerticalImageBox ' + direction" @click="switchToolbar" @scroll="handleScroll" ref="imageBox">
<!--      <el-image v-for="(item,i) in files" :key="i" :src=" encodeURI('/api/library/img?img=' + item.image) "
                :ref="item.name"
      >
        <template #error>
          <div class="image-slot">
            <el-icon><icon-picture /></el-icon>
            <div>{{ encodeURI('/api/library/img?img=' + item.image) }}</div>
          </div>
        </template>
      </el-image>-->
      <TImg v-for="(item,i) in files" :key="i" :authSrc=" encodeURI('/api/library/img?img=' + encodeURIComponent(item.image)) "
            :ref="item.name"
      />
    </div>
    <div :class="showSwitchToolbar?'bottom':'bottom hide'">
      <el-slider v-model="now_slider" :max="files.length" @input="changeScroll" />
      <el-text size="large" @click="switchImage" @touchend="switchImage" >{{ now_slider }}/{{ files.length }}</el-text>
    </div>
  </div>
</template>
<script>
import {computed} from "vue";
import TImg from "@/components/T-Img.vue";
import {ElMessageBox} from "element-plus";

export default {
  name: "Vertical",
  //引入模块
  components: {TImg},
  //父级传入数据
  props: {
    inData: Object,
    inSwitchView: Boolean,

    showToolbar: true,
    setDirection: String,

    fileIndex: Number,

    faFile: Array,
    faFileIndex: Number,
  },
  data() {
    return {
      //双向绑定处理
      files: computed({
        get: () => this.inData,
        set: val => {
          this.$emit("update:inData", val);
        }
      }),
      switchView: computed({
        get: () => this.inSwitchView,
        set: val => {
          this.$emit("update:inSwitchView", val);
        }
      }),
      direction: computed({
        get: () => {
          switch (this.setDirection) {
            case "竖向":
            case "↓↓":
            case "↓":
              return "ver"
            case "从左到右":
            case "|→":
            case "→":
              return "lTor horizontal"
            case "从右到左":
            case "←|":
            case "←":
              return "rTol horizontal"
          }
        },
        set: val => {/*this.$emit("update:inSwitchView", val);*/}
      }),

      showSwitchToolbar: true,

      now_slider: 0,
      inMove: false,
    }
  },
  //方法
  methods: {
    close: function () {
      this.$emit("update:inSwitchView", false);
    },
    switchToolbar: function () {
      this.showSwitchToolbar = !this.showSwitchToolbar;
    },
    handleScroll: function (event) {
      if(!this.inMove){
        // console.log("handleScroll", event)
        switch (this.setDirection) {
          case "竖向":case "↓↓":case "↓":{
            let all = event.target.scrollHeight - event.target.clientHeight;
            let now = event.target.scrollTop / all;
            // console.log("now", now)
            this.now_slider = Math.round(now * this.files.length)
          }
          break;
          case "从左到右":case "|→":case "→":{
            let all = event.target.scrollWidth;
            let now = event.target.scrollLeft / all;
            // console.log("now", now)
            this.now_slider = Math.round(now * this.files.length)
          }
          break;
          case "从右到左":case "←|":case "←":{
            let nowIndex = event.target.scrollWidth + event.target.scrollLeft;
            let now = nowIndex / event.target.scrollWidth;
            // console.log("now", now)
            this.now_slider = Math.round(now * this.files.length)
          }
          break;
        }
      }
    },
    changeScroll: function (index) {
      this.inMove = true;
      this.now_slider = index;
      let arr = document.querySelector(".VerticalImageBox").querySelectorAll(".el-image");
      // console.log("changeScroll", index)
      switch (this.setDirection) {
        case "从右到左":{
          index = arr.length - index - 1;
        }
        break
      }
      if(index >= 0 && index < arr.length){
        let element = arr[index];
        element.scrollIntoView({ behavior: 'smooth' });
      }
      let that = this;
      window.setTimeout(() => {
        that.inMove = false;
      },1000)
    },
    wheelFunc: function (event) {
      let that = this;
      let imageBox = that.$refs["imageBox"];
      // console.log('滚动了', event, event.deltaY, event.deltaX);
      let def = function () {
        //window.scrollBy(event.deltaY, 0)
        imageBox.scrollBy(event.deltaY, 0)
        // console.log("<>", event.deltaY, that, imageBox)
      }
      switch (this.setDirection) {
        case "从左到右":case "|→":case "→":
        case "从右到左":case "←|":case "←":
          def()
          break
      }
    },

    openBeforePath: function () {
      this.$emit('openBeforePath');
    },
    openNextPath: function () {
      this.$emit('openNextPath');
    },

    switchPath: function (path, faFile, faFileIndex) {
      this.$emit('switchPath', path, faFile, faFileIndex);
    },

    switchImage: function () {
      ElMessageBox.prompt('请输入要跳转到的图片(1~' + (this.files.length) + ')', '跳转图片', {
        confirmButtonText: '跳转',
        cancelButtonText: '取消',
        // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
        // inputErrorMessage: 'Invalid Email',
        inputValidator: (value) => {
          const index = Number(value)
          if (isNaN(index)) {
            return '请输入数字'
          }
          if (index <= 1) {
            return '必须是正数';
          }
          if(index > this.files.length){
            return '超出范围';
          }
          return true
        }
      })
          .then(({ value }) => {
            /*ElMessage({
              type: 'success',
              message: `Your email is:${value}`,
            })*/
            this.changeScroll(value-1)
          })
          .catch(() => {
            /*ElMessage({
              type: 'info',
              message: 'Input canceled',
            })*/
          })
    },
  },
  //启动事件
  mounted() {
    this.$nextTick(() => {
      // console.log("this.$refs=",
      //     this.$refs, this, this.$refs["00181.jpg"]
      // )
      console.log("fileIndex", this.fileIndex)
      let that = this
      if(this.fileIndex){
        window.setTimeout(() => {
          that.changeScroll(that.fileIndex)
        },1000)
      }
    });

    document.addEventListener('wheel', this.wheelFunc);
  },
  //销毁
  beforeUnmount() {
    document.removeEventListener('wheel', this.wheelFunc);
  }
}
</script>
<style scoped>
@import "@/style/Vertical.css";
@import "@/style/ImageView.css";
</style>
<style lang="scss">
/*@import "@/style/Vertical.css";*/
.el-slider__runway{
  background: aquamarine;
}
.imageView{
  .el-page-header__left{
    width: 100%;
    &>div{
      flex-shrink: 0;
    }
    .el-page-header__content{
      flex-grow: 1;
    }
  }
}
</style>