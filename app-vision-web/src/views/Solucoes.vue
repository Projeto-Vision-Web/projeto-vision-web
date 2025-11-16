<!-- src/views/Home.vue -->
<template>
  <div class="home-page">

    <!-- Seção de Resultados -->
    <section class="resultados-section">
      <div class="resultados-header">
        <h1>Resultados que Comprovam</h1>
        <p>Números que demonstram o impacto positivo em organizações de todos os tamanhos.</p>
      </div>
      <div class="resultados-cards">
        <div class="card">
          <div class="icon-wrapper">
            <div class="icon-circle"></div>
            <img src="https://img.icons8.com/ios-filled/50/00ff00/user.png" alt="Empresas Atendidas"/>
          </div>
          <h2>500+</h2>
          <p>Empresas Atendidas</p>
          <span>Organizações que confiam em nossa solução</span>
        </div>
        <div class="card">
          <div class="icon-wrapper">
            <div class="icon-circle"></div>
            <img src="https://img.icons8.com/ios-filled/50/00ff00/combo-chart.png" alt="Precisão IA"/>
          </div>
          <h2>95%</h2>
          <p>Precisão IA</p>
          <span>Acurácia em análises preditivas</span>
        </div>
        <div class="card">
          <div class="icon-wrapper">
            <div class="icon-circle"></div>
            <img src="https://img.icons8.com/ios-filled/50/00ff00/like.png" alt="Satisfação"/>
          </div>
          <h2>98%</h2>
          <p>Satisfação</p>
          <span>Índice de satisfação dos clientes</span>
        </div>
        <div class="card">
          <div class="icon-wrapper">
            <div class="icon-circle"></div>
            <img src="https://img.icons8.com/ios-filled/50/00ff00/lightning-bolt.png" alt="Melhoria"/>
          </div>
          <h2>50%</h2>
          <p>Melhoria</p>
          <span>Aumento médio na produtividade</span>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer-section">
      <div class="footer-top">
        <div class="footer-logo">
          <h2>Radar</h2>
          <span>Saúde Organizacional</span>
          <p>Transformando dados em insights estratégicos para organizações mais saudáveis e produtivas.</p>
          <div class="footer-tags">
            <span>IA Avançada</span>
            <span>LGPD</span>
          </div>
        </div>
        <div class="footer-links">
          <div class="link-group">
            <h3>Soluções</h3>
            <ul>
              <li>Análise de Clima</li>
              <li>Dashboard IA</li>
              <li>Relatórios Preditivos</li>
              <li>Monitoramento 24/7</li>
            </ul>
          </div>
          <div class="link-group">
            <h3>Suporte</h3>
            <ul>
              <li>Central de Ajuda</li>
              <li>Documentação</li>
              <li>API Reference</li>
              <li>Status da Plataforma</li>
            </ul>
          </div>
          <div class="link-group">
            <h3>Contato</h3>
            <ul>
              <li>contato@radarsaude.com</li>
              <li>+55 (11) 9999-9999</li>
              <li>São Paulo, Brasil</li>
            </ul>
            <button class="contact-btn">Falar com Especialista →</button>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2024 Radar de Saúde Organizacional. Todos os direitos reservados.</p>
        <div class="footer-privacy">
          <span>Privacidade</span>
          <span>Termos</span>
          <span>Cookies</span>
        </div>
      </div>
    </footer>

  </div>
</template>

<script setup>
// Este componente é estático
</script>

<style scoped>
.home-page {
  font-family: 'Inter', sans-serif;
  color: #fff;
  margin: 0;
  padding: 0;
}

/* Seção Resultados */
.resultados-section {
  background: linear-gradient(135deg, #1e90ff, #3cb0fd);
  padding: 85px 5%;
  text-align: center;
  margin-top: -5rem;
}

.resultados-header h1 {
  font-size: 2rem;
  margin-bottom: 10px;
}

.resultados-header p {
  font-size: 0.9rem;
  color: #e0f0ff;
  margin-bottom: 25px;
}

.resultados-cards {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

.resultados-cards .card {
  position: relative;
  background: rgba(255,255,255,0.1);
  border-radius: 10px;
  padding: 60px 20px 30px 20px;
  flex: 1 1 200px;
  max-width: 250px;
  text-align: center;
  overflow: visible;

  /* Transição suave para hover */
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.resultados-cards .card:hover {
  transform: translateY(-10px); /* Move o card para cima */
  box-shadow: 0 15px 25px rgba(0,0,0,0.2); /* Sombra para dar destaque */
  cursor: pointer; /* Indica interatividade */
}

.icon-wrapper {
  position: relative;        /* Container relativo para controlar a posição dos elementos filhos */
  display: flex;
  justify-content: center;   /* Centraliza horizontalmente */
  align-items: center;       /* Centraliza verticalmente */
  margin-bottom: 20px;
}
.icon-wrapper .icon-circle {
  position: absolute; /* Fica atrás do ícone */
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 70px;
  height: 70px;
  
  /* Efeito de vidro */
  background: rgba(255, 255, 255, 0.13); /* branco semi-transparente */
  backdrop-filter: blur(10px); /* desfoca o fundo atrás do círculo */
  -webkit-backdrop-filter: blur(10px); /* compatibilidade Safari */
  border-radius: 50%;
  border:none;
  
  z-index: 0; /* sempre atrás do ícone */
}


.icon-wrapper img {
  position: relative;        /* Ícone fica acima do círculo */
  z-index: 1;                /* Sempre na frente do círculo */
  width: 40px;
  height: 40px;
}
.resultados-cards .card h2 {
  font-size: 2rem;
  margin-bottom: 5px;
}

.resultados-cards .card p {
  font-weight: 600;
  margin-bottom: 5px;
}

.resultados-cards .card span {
  font-size: 0.85rem;
  color: #e0f0ff;
}

/* Footer */
.footer-section {
  background-color: #f5f5f5;
  color: #333;
  padding: 60px 5% 20px 5%;
  font-size: 0.9rem;
}

.footer-top {
  display: flex;
  flex-wrap: wrap;
  gap: 40px;
  justify-content: space-between;
  margin-bottom: 40px;
}

.footer-logo {
  flex: 1 1 300px;
}

.footer-logo h2 {
  color: #1e90ff;
  margin: 0;
}

.footer-logo span {
  display: block;
  font-weight: bold;
  margin-bottom: 15px;
}

.footer-logo p {
  color: #555;
  margin-bottom: 10px;
}

.footer-tags span {
  display: inline-block;
  background: #1e90ff;
  color: white;
  padding: 2px 8px;
  margin-right: 5px;
  border-radius: 4px;
  font-size: 0.75rem;
}

.footer-links {
  display: flex;
  flex: 2 1 600px;
  justify-content: space-between;
  gap: 20px;
}

.link-group h3 {
  margin-bottom: 10px;
  color: #1e90ff;
}

.link-group ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.link-group ul li {
  margin-bottom: 8px;
}

.contact-btn {
  margin-top: 10px;
  padding: 10px 15px;
  background-color: #1e90ff;
  color: #fff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.footer-bottom {
  display: flex;
  flex-direction: column;
  align-items: center;
  border-top: 1px solid #ddd;
  padding-top: 20px;
  gap: 5px;
}

.footer-bottom p {
  margin: 0;
  font-size: 0.8rem;
}

.footer-privacy span {
  margin: 0 8px;
  font-size: 0.8rem;
  color: #555;
}

/* Responsivo */
@media (max-width: 900px) {
  .resultados-cards {
    flex-direction: column;
    align-items: center;
  }
  .footer-top {
    flex-direction: column;
  }
  .footer-links {
    flex-direction: column;
    gap: 20px;
  }
}
</style>
