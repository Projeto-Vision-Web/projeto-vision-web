<template>
  <div id="app">
    <main class="container">
      
      <button class="btn-voltar" @click="goBack" title="Voltar">
        <img src="../assets/setas-para-a-esquerda.png" alt="Voltar">
      </button>
      <section class="hero-card">
        <div class="hero-bg"></div>
        <div class="hero-overlay"></div>
        <div class="hero-content">
          <div class="hero-text">
            <h1>Transforme dados em <br>decisões estratégicas</h1>
            <p>
              Nossa IA analisa o clima organizacional em tempo real,
              oferecendo insights preditivos para uma gestão mais eficaz.
            </p>
          </div>

          <div class="hero-cards">
            <div class="card green">
              <span class="card-icon"><img src="/icon_raio.png" alt="Logo" class="logo" width="32" height="32"></span>
              <div>
                <h3>Tempo Real</h3>
                <p>Análise contínua</p>
              </div>
            </div>
            <div class="card purple">
              <span class="card-icon"><img src="/icon_grafico.png" alt="Logo" class="logo" width="32" height="32"></span>
              <div>
                <h3>IA Avançada</h3>
                <p>Insights preditivos</p>
              </div>
            </div>
            <div class="card blue">
              <span class="card-icon"><img src="/icon_olho.png" alt="Logo" class="logo" width="32" height="32"></span>
              <div>
                <h3>360° View</h3>
                <p>Visão completa</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="features-section">
        <h2 class="section-title">Por que escolher a VisionWeb?</h2>
        <p class="section-subtitle">
          Transforme a cultura da sua empresa com análises precisas e insights gerados por IA.
        </p>

        <div class="features-cards">
          <div class="feature-card">
            <h3><img src="/icon_barras.png" alt="Logo" class="logo" width="32" height="32"><br>Índice de Satisfação</h3>
            <p>Monitore o bem-estar da equipe em tempo real e identifique pontos de melhoria.</p>
          </div>
          <div class="feature-card">
            <h3><img src="/icon_mao.png" alt="Logo" class="logo" width="32" height="32"><br> Engajamento Geral</h3>
            <p>Compreenda o engajamento de colaboradores e fortaleça a comunicação interna.</p>
          </div>
          <div class="feature-card">
            <h3><img src="/icon_foguete.png" alt="Logo" class="logo" width="32" height="32"><br> Desenvolvimento</h3>
            <p>Receba recomendações de crescimento e desenvolvimento contínuo para sua equipe.</p>
          </div>
          <div class="feature-card">
            <h3><img src="/icon_robo.png" alt="Logo" class="logo" width="32" height="32"><br> Insights com IA</h3>
            <p>Utilize inteligência artificial para prever tendências organizacionais e apoiar decisões estratégicas.</p>
          </div>
        </div>
      </section>

      <section class="stats-section">
        <div class="stat">
          <h3>87%</h3>
          <p>de colaboradores satisfeitos</p>
        </div>
        <div class="stat">
          <h3>92%</h3>
          <p>de engajamento médio em equipes analisadas</p>
        </div>
        <div class="stat">
          <h3>10+</h3>
          <p>relatórios de tendências gerados por IA</p>
        </div>
      </section>

      <section class="highlight-section">
        <div class="highlight-bg"></div>
        <div class="highlight-overlay"></div>
        <div class="highlight-content">
          <h2>Transforme sua empresa através de dados e inteligência</h2>
          <p>Decisões estratégicas mais rápidas e um ambiente organizacional mais saudável.</p>
        </div>
      </section>

      <section class="features-tabs-section">
        <h2 class="section-title">Funcionalidades</h2>
        <p class="section-subtitle">
          Descubra tudo o que a VisionWeb pode fazer para melhorar o clima organizacional da sua empresa.
        </p>

        <div class="tabs">
          <button
            v-for="(tab, index) in tabs"
            :key="index"
            class="tab"
            :class="{ active: activeTab === index }"
            @click="activeTab = index"
          >
            {{ tab.title }}
          </button>
        </div>

        <div class="tab-content">
          <div class="tab-panel">
            <img :src="tabs[activeTab].image" :alt="tabs[activeTab].title" class="tab-image">
            <div class="tab-text">
              <h3>{{ tabs[activeTab].title }}</h3>
              <p>{{ tabs[activeTab].description }}</p>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const goBack = () => {
  router.go(-1); // Volta para a página anterior
};

const activeTab = ref(0);

const tabs = ref([
  {
    title: 'Análise de Satisfação',
    description: 'Monitore continuamente o nível de satisfação dos colaboradores, identifique pontos críticos e proponha melhorias rápidas para aumentar o bem-estar corporativo.',
    image: '/public/heart_image.png'
  },
  {
    title: 'Engajamento de Equipe',
    description: 'Acompanhe o engajamento geral e individual, identifique colaboradores mais engajados e implemente estratégias para reforçar a comunicação interna e a colaboração.',
    image: '/public/teamwork.png'
  },
  {
    title: 'Insights com IA',
    description: 'Receba análises preditivas baseadas em inteligência artificial para antecipar tendências organizacionais, permitindo decisões estratégicas mais rápidas e precisas.',
    image: '/public/IA_brain.png'
  },
  {
    title: 'Relatórios Avançados',
    description: 'Gere relatórios detalhados com métricas, gráficos e indicadores-chave, facilitando apresentações para líderes e contribuindo para a melhoria contínua.',
    image: '/public/grafico_roxo.png'
  }
]);
</script>

<style scoped>
/* INÍCIO DA MUDANÇA: CSS do Botão Voltar (com imagem) */
.btn-voltar {
  background: transparent;
  border: none;
  cursor: pointer;
  display: flex; 
  align-items: center;
  z-index: 3;
  padding: 0;
  margin-bottom: 2rem; 
  transition: transform 0.3s ease, filter 0.3s ease;
}

.btn-voltar img {
  width: 24px; /* Tamanho do ícone */
  height: 24px;
  /* Efeito de brilho roxo sutil na imagem */
  filter: drop-shadow(0 0 8px rgba(112, 73, 250, 0.7)); 
  transition: all 0.3s ease;
}

.btn-voltar:hover {
  transform: scale(1.1); /* Aumenta o botão */
}

.btn-voltar:hover img {
  filter: drop-shadow(0 0 15px rgba(112, 73, 250, 1)); /* Brilho mais intenso */
  transform: translateX(-3px); /* Efeito de "empurrar" */
}
/* FIM DA MUDANÇA */


/* Reset */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: "Segoe UI", Arial, sans-serif;
  color: #fff;
  min-height: 100vh;
  background-color: #161616;
}

/* Container central*/
.container {
  max-width: 1400px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 2rem;
  padding-right: 2rem;
  
  /* INÍCIO DA CORREÇÃO: Adicionado padding-top para compensar o header fixo */
  padding-top: 100px; /* 70px do header + 30px de espaço */
  /* FIM DA CORREÇÃO */
  
  padding-bottom: 5rem;
  box-sizing: border-box;
}

/* Hero Card */
.hero-card {
  position: relative;
  border-radius: 28px;
  overflow: hidden;
  box-shadow: 0 10px 60px rgba(0,0,0,0.6), 0 0 40px rgba(49, 62, 248, 0.2);
  padding: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 45vh;
  border: 1px solid rgba(112, 73, 250, 0.3);
  z-index: 0;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.hero-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 80px rgba(0,0,0,0.7), 0 0 60px rgba(49, 62, 248, 0.4);
}

.hero-bg {
  position: absolute;
  inset: 0;
  background: url("/image1.png") center/cover no-repeat;
  z-index: -2;
}

.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(13,13,13,0.9), rgba(13,13,13,0.7), transparent);
  z-index: -1;
}

.hero-content {
  position: relative;
  z-index: 2;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  text-align: center; 
}

.hero-text h1 {
  font-size: 3rem;
  font-weight: 800;
  background: linear-gradient(90deg, #313ef8, #7049fa, #a855f7);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  text-shadow: 0 0 40px rgba(112, 73, 250, 0.3);
}

.hero-text p {
  color: #d0d0d0;
  font-size: 1.1rem;
  line-height: 1.6;
  margin-left: auto;   
  margin-right: auto;  
}

/* Cards Hero */
.hero-cards {
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
}

.card {
  padding: 1.5rem 1.5rem;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 1.2rem;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  cursor: pointer;
}

.card:hover {
  transform: translateX(10px) scale(1.02);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
}

.card-icon {
  font-size: 2rem;
}

.card.green {
  background: rgba(34, 197, 94, 0.15);
  border: 1px solid rgba(34, 197, 94, 0.4);
  color: #22c55e;
  box-shadow: 0 5px 20px rgba(34, 197, 94, 0.2);
}

.card.purple {
  background: rgba(168, 85, 247, 0.15);
  border: 1px solid rgba(168, 85, 247, 0.4);
  color: #a855f7;
  box-shadow: 0 5px 20px rgba(168, 85, 247, 0.2);
}

.card.blue {
  background: rgba(59, 130, 246, 0.15);
  border: 1px solid rgba(59, 130, 246, 0.4);
  color: #3b82f6;
  box-shadow: 0 5px 20px rgba(59, 130, 246, 0.2);
}

.card h3 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.2rem;
}

.card p {
  font-size: 0.9rem;
  color: #bbb;
}

/* Seção de Marketing */
.features-section {
  margin-top: 5rem;
  text-align: center;
}

.section-title {
  font-size: 2.5rem;
  font-weight: bold;
  background: linear-gradient(90deg, #313ef8, #7049fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 1rem;
}

.section-subtitle {
  color: #ccc;
  font-size: 1.1rem;
  margin-bottom: 3rem;
  margin-left: auto;
  margin-right: auto;
}

.features-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(49, 62, 248, 0.3);
  padding: 2rem;
  border-radius: 20px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
}

.feature-card:hover {
  transform: translateY(-8px);
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(112, 73, 250, 0.5);
  box-shadow: 0 15px 40px rgba(49, 62, 248, 0.3);
}

.feature-card h3 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #fff;
}

.feature-card p {
  color: #ccc;
  font-size: 0.95rem;
  line-height: 1.6;
}

/* Estatísticas */
.stats-section {
  margin-top: 5rem;
  display: flex;
  justify-content: center;
  gap: 4rem;
  flex-wrap: wrap;
  text-align: center;
}

.stat h3 {
  font-size: 3rem;
  font-weight: 800;
  background: linear-gradient(90deg, #313ef8, #7049fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 0.5rem;
}

.stat p {
  color: #ccc;
  font-size: 1rem;
}

/* Seção de Destaque */
.highlight-section {
  position: relative;
  height: 40vh;
  min-height: 300px;
  border-radius: 28px;
  overflow: hidden;
  margin: 5rem 0;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 10px 60px rgba(0,0,0,0.6), 0 0 40px rgba(112, 73, 250, 0.2);
  border: 1px solid rgba(112, 73, 250, 0.3);
  background-color: transparent;
}

.highlight-bg {
  position: absolute;
  inset: 0;
  background: url("/background.png") center/cover no-repeat;
  z-index: 1;
}

.highlight-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to right, rgba(13,13,13,0.9), rgba(13,13,13,0.7), transparent);
  z-index: 2;
}

.highlight-content {
  position: relative;
  z-index: 3;
  text-align: center;
  color: #fff;
  padding: 0 2rem;
}

.highlight-content h2 {
  font-size: 2.8rem;
  font-weight: bold;
  background: linear-gradient(90deg, #313ef8, #7049fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 1.5rem;
  line-height: 1.3;
}

.highlight-content p {
  font-size: 1.3rem;
  color: #d0d0d0;
}

/* Seção de Funcionalidades com Abas */
.features-tabs-section {
  margin-top: 5rem;
  margin-bottom: 5rem;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tabs {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 3rem;
  flex-wrap: wrap;
}

.tab {
  padding: 1rem 2rem;
  border-radius: 25px;
  border: 1px solid rgba(255,255,255,0.2);
  background: rgba(255,255,255,0.05);
  color: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
  font-weight: 500;
  backdrop-filter: blur(10px);
}

.tab:hover {
  background: rgba(49,62,248,0.3);
  border-color: rgba(112, 73, 250, 0.5);
  transform: translateY(-2px);
}

.tab.active {
  background: linear-gradient(90deg, #313ef8, #7049fa);
  color: #fff;
  border-color: #7049fa;
  box-shadow: 0 10px 30px rgba(112, 73, 250, 0.4);
}

.tab-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.tab-panel {
  display: flex;
  text-align: center; 
  max-width: 1100px;
  gap: 3rem;
  align-items: center;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(49, 62, 248, 0.3);
  padding: 3rem;
  border-radius: 28px;
  backdrop-filter: blur(10px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.4);
}

.tab-image {
  width: 400px;
  height: auto;
  border-radius: 20px;
  box-shadow: 0 10px 40px rgba(0,0,0,0.6);
  transition: transform 0.3s ease;
}

.tab-image:hover {
  transform: scale(1.05);
}

.tab-text h3 {
  font-size: 2rem;
  margin-bottom: 1rem;
  background: linear-gradient(90deg, #313ef8, #7049fa);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.tab-text p {
  color: #ccc;
  font-size: 1.05rem;
  line-height: 1.7;
}

/* Responsividade */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }

  .hero-text h1 {
    font-size: 2.5rem;
  }

  .tab-panel {
    flex-direction: column;
    text-align: center;
  }

  .tab-image {
    width: 100%;
    max-width: 500px;
  }

  .tab-text {
    text-align: center;
  }
}

@media (max-width: 768px) {
  .header {
    padding: 1rem;
  }

  .header-title {
    font-size: 1.5rem;
  }

  .hero-text h1 {
    font-size: 2rem;
  }

  .section-title {
    font-size: 2rem;
  }

  .highlight-content h2 {
    font-size: 2rem;
  }

  .stats-section {
    gap: 2rem;
  }

  .tabs {
    flex-direction: column;
  }

  .tab {
    width: 100%;
  }
}
</style>