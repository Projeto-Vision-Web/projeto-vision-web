<template>
  <div>
    <MainNavbar />
    <main class="content-wrapper">
      <slot />
    </main>
    </div>
</template>

<script setup>
import MainNavbar from '../components/MainNavbar.vue' 
</script>

<style scoped>
.content-wrapper {
  /* padding-top: 100px;  <-- ESTA LINHA FOI REMOVIDA */
}
</style>