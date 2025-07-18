<template>
  <span class="MySpecialTag">
    <el-tag :type="tagData.type" :color="tagData.color" :effect="tagData.effect" :round="tagData.round"
            :closable="closable"
            @close="closeHook"
    ><slot/></el-tag>
  </span>
</template>
<script>
import {computed} from "vue";

export default {
  name: "MySpecialTag",
  //引入模块
  components: {},
  //父级传入数据
  props: {
    specialTagData: Object,
    closable: Boolean,
  },
  data() {
    return {
      //双向绑定处理
      tagData: computed({
        get: () => {
          let data = this.specialTagData;
          if(!data){
            data = {
              id: null,
              name: "",
              type: "info",
              color: "",
              effect: "",
              round: false,
            }
          }
          if(!data.type){
            data.type = "info"
          }
          if(!data.effect){
            data.effect = "light"
          }
          return data
        },
        set: val => {
          this.$emit("update:specialTagData", val);
        }
      }),
    }
  },
  //方法
  methods: {
    closeHook: function (MouseEvent) {
      this.$emit("close", MouseEvent, this.tagData);
    }
  },
  //启动事件
  mounted() {
  },
  //销毁
  beforeUnmount() {
  }
}
</script>
<style scoped>
/*@import "@/style/MySpecialTag.css";*/
</style>