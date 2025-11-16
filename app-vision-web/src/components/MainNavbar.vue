<template>
  <div>
    <header class="header">
      <div class="header-left">
        <button v-if="showBackButton" class="btn-voltar-navbar" @click="goBack" title="Voltar">
          <img src="../assets/setas-para-a-esquerda.png" alt="Voltar">
        </button>
        
        <img src="../assets/LogoFinal.png" alt="Logo" class="logo" width="40" height="40">
        <h2 class="header-title">VisionWeb</h2>
      </div>
      
      <div class="header-right" v-if="showProfileIcon">
        <button 
          class="profile-icon-btn" 
          @click.stop="toggleProfileDropdown" 
          title="Ver Perfil"
        >
          <img src="../assets/perfil.png" alt="Ver Perfil" class="profile-icon-img">
        </button>

        <transition name="fade">
          <div 
            v-if="showProfileDropdown" 
            class="profile-dropdown"
            @click.stop 
          >
            <button class="dropdown-btn" @click="goToProfile">
              <i class="fas fa-user-circle"></i> Ver Perfil
            </button>
            <button class="dropdown-btn logout" @click="openLogoutModal">
              <i class="fas fa-sign-out-alt"></i> Sair
            </button>
          </div>
        </transition>
      </div>
    </header>

    <transition name="modal">
      <div v-if="showLogoutConfirmation" class="modal-overlay" @click="closeLogoutModal">
        <div class="card logout-modal-content" @click.stop>
          
          <div class="modal-title-group">
            <h2>Sair da Conta</h2>
            <p>Você será redirecionado para a tela de login. Deseja continuar?</p>
          </div>
          
          <div class="action-buttons-footer">
            <button class="action-btn secondary" @click="closeLogoutModal">
              <i class="fas fa-times"></i> Cancelar
            </button>
            <button class="action-btn danger" @click="confirmLogout">
              <i class="fas fa-sign-out-alt"></i> Sim, sair
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router'; 

const router = useRouter();
const route = useRoute(); 
const showProfileDropdown = ref(false);
const showLogoutConfirmation = ref(false);

const showProfileIcon = computed(() => {
  const allowedRoutes = ['/form-admin', '/perfil'];
  return allowedRoutes.includes(route.path);
});

const showBackButton = computed(() => {
  return route.path !== '/';
});

// INÍCIO DA MUDANÇA: Lógica do "Voltar" inteligente
const goBack = () => {
  // Se estivermos na tela 'form-admin', voltar significa "Sair"
  if (route.path === '/form-admin') {
    openLogoutModal(); // Mostra o modal de confirmação
  } else {
    // Para qualquer outra tela (ex: /perfil), apenas volte
    router.go(-1); 
  }
};
// FIM DA MUDANÇA

// --- Funções do Dropdown e Modal ---

const closeProfileDropdown = () => {
  showProfileDropdown.value = false;
};

const toggleProfileDropdown = () => {
  showProfileDropdown.value = !showProfileDropdown.value;
};

const goToProfile = () => {
  router.push('/perfil');
  closeProfileDropdown();
};

// Abre o modal de "Sair" (usado pelo dropdown E pelo botão voltar)
const openLogoutModal = () => {
  closeProfileDropdown(); // Fecha o dropdown (caso esteja aberto)
  showLogoutConfirmation.value = true;
};

// Fecha o modal de "Sair"
const closeLogoutModal = () => {
  showLogoutConfirmation.value = false;
};

// Confirma o logout e redireciona
const confirmLogout = () => {
  router.push('/login');
  closeLogoutModal(); 
};

// Lógica para fechar o dropdown ao clicar fora
onMounted(() => {
  document.addEventListener('click', closeProfileDropdown);
});
onUnmounted(() => {
  document.removeEventListener('click', closeProfileDropdown);
});
</script>

<style scoped>
/* ... (todo o seu CSS de .header, .logo, .profile-icon-btn, etc. permanece o mesmo) ... */

.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 70px;
  padding: 1rem 2rem;
  display: flex;
  align-items: center;
  justify-content: space-between; 
  background: rgba(13, 13, 13, 0.75); 
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.btn-voltar-navbar {
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 5px;
  margin-right: 10px; 
  border-radius: 50%;
  transition: all 0.3s ease;
}

.btn-voltar-navbar img {
  width: 24px;
  height: 24px;
  filter: drop-shadow(0 0 5px rgba(0,0,0,0.5));
  transition: all 0.3s ease;
}

.btn-voltar-navbar:hover {
  background-color: #2d2d2d;
  transform: scale(1.1);
}

.btn-voltar-navbar:hover img {
  filter: drop-shadow(0 0 8px rgba(112, 73, 250, 0.7));
  transform: translateX(-2px);
}

.logo {
  flex-shrink: 0;
  filter: drop-shadow(0 0 5px rgba(49, 62, 248, 0.5));
}

.header-title {
  margin: 0;
  font-size: 1.8rem;
  font-weight: bold;
  background: linear-gradient(90deg, #313ef8, #7049fa); 
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  text-shadow: 0 0 30px rgba(112, 73, 250, 0.3);
}

.header-right {
  display: flex;
  align-items: center;
  position: relative; 
}

.profile-icon-btn {
  background: transparent;
  border: 1px solid transparent; 
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  width: 44px; 
  height: 44px;
  padding: 0;
}

.profile-icon-img {
  width: 28px;
  height: 28px;
  transition: transform 0.2s ease, filter 0.2s ease; 
  filter: drop-shadow(0 0 5px rgba(0,0,0,0.5)); 
}

.profile-icon-btn:hover {
  background-color: #2d2d2d; 
  transform: scale(1.1);
  box-shadow: 0 0 15px rgba(112, 73, 250, 0.4);
  border-color: #7049fa; 
}

.profile-icon-btn:hover .profile-icon-img {
    filter: drop-shadow(0 0 8px rgba(112, 73, 250, 0.7)); 
    transform: scale(1.05);
}

.profile-dropdown {
  position: absolute;
  top: calc(100% + 15px); 
  right: 0;
  background-color: rgba(31, 31, 31, 0.8); 
  border: 1px solid #333;
  border-radius: 8px;
  width: 180px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  padding: 8px;
  z-index: 1100; 
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
}

.dropdown-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  background: transparent;
  border: none;
  color: #d0d0d0;
  padding: 12px 10px;
  width: 100%;
  text-align: left;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95em;
  font-weight: 500;
  transition: background-color 0.2s, color 0.2s;
}

.dropdown-btn i {
  width: 20px; 
  text-align: center;
  font-size: 1.1em;
  color: #888;
  transition: color 0.2s;
}

.dropdown-btn:hover {
  background-color: rgba(112, 73, 250, 0.15); 
  color: #ffffff; 
}

.dropdown-btn:hover i {
  color: #7049fa; 
}

.dropdown-btn.logout:hover {
  background-color: rgba(255, 77, 77, 0.15);
  color: #ff4d4d;
}
.dropdown-btn.logout:hover i {
  color: #ff4d4d;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* CSS para o Modal de Logout */
:root {
  --color-primary-gradient: linear-gradient(90deg, #313ef8, #7049fa); 
  --color-secondary-btn: #2d2d2d; 
}

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5); 
    backdrop-filter: blur(5px); 
    -webkit-backdrop-filter: blur(5px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2000; 
}

/* Estilo .card genérico para o modal */
.card {
  background-color: rgba(31, 31, 31, 0.8);
  border-radius: 12px;
  padding: 30px;
  border: 1px solid #333;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.logout-modal-content {
    max-width: 450px;
    width: 90%;
    text-align: center;
}

.modal-title-group h2 {
    font-size: 1.8rem;
    color: #ffffff; 
    margin-bottom: 10px;
}
.modal-title-group p {
    color: #d0d0d0;
    margin-bottom: 25px;
    font-size: 1.1em;
    line-height: 1.5;
}

.action-buttons-footer {
    display: flex;
    justify-content: center; 
    gap: 15px;
    padding-top: 20px;
    border-top: 1px solid #333;
    margin-top: 30px;
}

.action-btn {
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  font-size: 1em;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn.secondary {
  background-color: var(--color-secondary-btn);
  color: #ffffff;
  border: 1px solid #444;
}

/* Botão de "Sair" (Perigo) */
.action-btn.danger {
  background-color: #ff4d4d;
  color: #ffffff;
  box-shadow: 0 5px 15px rgba(255, 77, 77, 0.3);
}

.action-btn:hover:not(:disabled) {
    opacity: 0.9;
    transform: translateY(-1px);
}

.modal-enter-active, .modal-leave-active {
    transition: opacity 0.3s ease;
}

.modal-enter-from, .modal-leave-to {
    opacity: 0;
}
</style>