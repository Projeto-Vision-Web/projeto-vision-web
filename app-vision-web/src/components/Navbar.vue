<template>
  <nav class="navbar">
    <div class="logo">
      <img src="/Imagens/LogoFinal[1].png" alt="Logo Vision Web" class="logo-img" />
      <span class="logo-text">Vision Web</span>
    </div>

    <div class="right-buttons">
      <router-link to="/login" class="btn-login">Login</router-link>
      <router-link to="/marketing" class="btn-login">Marketing</router-link>
      <router-link to="/perfil" class="btn-login">Perfil</router-link>
      <router-link to="/form-admin" class="btn-login">pesquisas</router-link>
    </div>
  </nav>
</template>

<script setup>
// Nenhum script necessário
</script>

<style scoped>
* {
  box-sizing: border-box;
  font-family: Arial, Helvetica, sans-serif;
}

/* Navbar com altura fixa */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 70px; /* altura fixa da barra */
  padding: 0 5%;
  background: rgba(0, 0, 0, 0.527); 
  backdrop-filter: blur(12px) saturate(180%);
  -webkit-backdrop-filter: blur(12px) saturate(180%);
  color: #ffffff;
  box-shadow: 0 4px 30px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 999;
  overflow: visible; /* permite que elementos maiores “ultrapassem” a barra */
}

/* Logo com imagem + texto */
.logo {
  display: flex;
  align-items: center;
  gap: 0.6rem;
  font-weight: 700;
  font-size: 1.5rem;
  line-height: 70px; /* mantém o texto centralizado verticalmente */
  
  margin-left: -20px; /* move o conjunto logo + texto para mais à esquerda */
}

/* Ajuste da imagem da logo */
.logo-img {
  height: 50px; /* tamanho da logo */
  width: auto;
  display: block;
}

.logo-text {
  font-family: 'Roboto', 'Helvetica Neue', Arial, sans-serif; /* fonte fina */
  font-size: 1.5rem; /* tamanho */
  font-weight: 700; /* mais fino possível */
  letter-spacing: 0.5px; /* espaçamento delicado */
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3); /* sombra suave */

  /* gradiente azul para roxo */
  background: linear-gradient(90deg, #00c3ff, #8b51ff, #8f10ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* Container para o botão de login à direita */
.right-buttons {
  display: flex;
  align-items: center;
  gap: 1rem;
  height: 100%;
}

/* Botão de login estilizado */
.btn-login {
  padding: 0.4rem 1.1rem;
  border-radius: 10px;
  background-color: rgba(38, 185, 1, 0.281);
  color: white;
  font-size: 1rem;
  font-weight: 600;
  border: solid 1px #26b901;
  cursor: pointer;
  transition: background-color 0.3s ease;
  text-decoration: none;
  font-family: Arial, Helvetica, sans-serif;
}

.btn-login:hover {
  background-color: rgba(24, 122, 0, 0.281);
  border: solid 1px #26b901;
}
</style>
