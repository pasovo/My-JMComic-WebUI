<template>
  <router-view />
</template>

<script>
export default {
  name: 'App',
  components: {
  },
  methods: {

  },
  mounted() {
    // this.upPageSize()
    // window.addEventListener('resize', this.upPageSize);
  },
  beforeUnmount() {
    // window.removeEventListener('resize', this.upPageSize);
  },
}
</script>

<style>
@import "@/style/base.css";
</style>
<style scoped>
body{
  background: #dadce0;
}
</style>
