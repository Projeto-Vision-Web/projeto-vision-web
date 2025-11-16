<template>
  <div class="page">
    <div class="background-container">
      <img src="/Imagens/fundo2.jpg" alt="Fundo" class="bg-img" />
      <div class="overlay"></div>
    </div>

    <div class="content">
      <h1>Sistema de Insights Organizacionais</h1>
      <p>Transforme dados e percepções em decisões estratégicas com a força da análise inteligente.</p>
      
      <div class="buttons">
        <button class="btn-primary" @click="goToLogin">Acessar o Sistema</button>
        <button class="btn-secondary" @click="goToMarketing">Saiba Mais</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
const router = useRouter();

const goToLogin = () => {
  router.push('/login');
};

const goToMarketing = () => {
  router.push('/marketing');
};
</script>

<style scoped>
.page {
  position: relative;
  width: 100%;
  
  /* INÍCIO DA CORREÇÃO:
     A altura deve ser 100% da tela (vh).
     A margem negativa foi removida.
  */
  height: 100vh; 
  /* margin-top: -100px;  <-- REMOVIDO */
  /* FIM DA CORREÇÃO */
  
  overflow: hidden;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

.background-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.bg-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.829);
}

.content {
  position: relative;
  z-index: 2; /* sobre o overlay */
  max-width: 800px;
  padding: 1rem;
}

.content h1 {
  font-size: 3rem;       
  margin-bottom: 1rem;
  white-space: nowrap;  
}

.content p {
  font-size: 1.2rem;
  line-height: 1.6; 
  max-width: 650px; 
  margin-left: auto;   
  margin-right: auto;
  margin-bottom: 2rem;
}

.buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-primary, .btn-secondary {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 5px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease; 
}

.btn-primary {
  background: linear-gradient(90deg, #313ef8, #7049fa); 
  color: #ffffff;
  box-shadow: 0 5px 15px rgba(112, 73, 250, 0.4);
}

.btn-secondary {
  background: transparent;
  border: 2px solid #7049fa; 
  color: #fff;
}

.btn-primary:hover {
  opacity: 0.9; 
  transform: scale(1.05); 
}

.btn-secondary:hover {
  background: #7049fa;  
  color: #ffffff;       
  transform: scale(1.05); 
  border: 2px solid #7049fa;
}
</style>