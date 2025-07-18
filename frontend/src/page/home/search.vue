<template>
  <div class="search">
    search
  </div>
</template>
<script>
import {computed} from "vue";

export default {
  name: "search",
  //引入模块
  components: {},
  //父级传入数据
  props: {
    inData: Object,
  },
  data() {
    return {
      //双向绑定处理
      tabPosition: computed({
        get: () => this.inData,
        set: val => {
          this.$emit("update:inData", val);
        }
      }),
    }
  },
  //方法
  methods: {},
  //启动事件
  mounted() {
  },
  //销毁
  beforeUnmount() {
  }
}
</script>
<style scoped>
/*@import "@/style/search.css";*/
</style>