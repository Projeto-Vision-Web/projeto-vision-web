<template lang="pug">
.section-wrapper
  .section-label Funcionalidades
  h2.section-title Tecnologia que Transforma
  p.section-subtitle Descubra como nossa plataforma revoluciona a gestão organizacional com soluções inteligentes e dados precisos.
  .cards-wrapper
    .card
      img.card-icon(src="https://img.icons8.com/ios-filled/50/00c853/artificial-intelligence.png" alt="Inteligência Artificial")
      span.card-badge IA
      h3.card-title Inteligência Artificial
      p.card-text Algoritmos avançados que analisam padrões e comportamentos organizacionais em tempo real
    .card
      img.card-icon(src="https://img.icons8.com/ios-filled/50/2979ff/combo-chart.png" alt="Dashboards Dinâmicos")
      span.card-badge Analytics
      h3.card-title Dashboards Dinâmicos
      p.card-text Visualizações interativas que transformam dados complexos em insights claros e acionáveis.
    .card
      img.card-icon(src="https://img.icons8.com/color/48/000000/analytics--v1.png" alt="Insights Preditivos")
      span.card-badge Preditivo
      h3.card-title Insights Preditivos
      p.card-text Antecipe tendências e identifique riscos antes que impactem sua organização.
    .card
      img.card-icon(src="https://img.icons8.com/ios-filled/50/00c853/shield.png" alt="Dados Seguros")
      span.card-badge Segurança
      h3.card-title Dados Seguros
      p.card-text Proteção avançada com criptografia e conformidade total com LGPD e regulamentações.
    .card
      img.card-icon(src="https://img.icons8.com/ios-filled/50/00c853/clock.png" alt="Monitoramento Contínuo")
      span.card-badge Tempo Real
      h3.card-title Monitoramento Contínuo
      p.card-text Acompanhamento 24/7 do clima organizacional com alertas em tempo real.
    .card
      img.card-icon(src="https://img.icons8.com/ios-filled/50/2979ff/teamwork.png" alt="Gestão de Equipes")
      span.card-badge Equipes
      h3.card-title Gestão de Equipes
      p.card-text Ferramentas especializadas para avaliar e melhorar a performance de equipes.
</template>

<script setup>
// Nenhum script necessário, esta é apenas a seção visual
</script>

<style scoped>
.section-wrapper {
  padding: 4rem 5%;
  text-align: center;
  background-color: #f7f8fa;
  font-family: 'Inter', sans-serif;
}

.section-label {
  display: inline-block;
  font-size: 0.75rem;
  color: #9e9e9e;
  background-color: #e0e0e0;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  margin-bottom: 0.5rem;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  color: #212121;
  margin-bottom: 0.5rem;
}

.section-subtitle {
  font-size: 1rem;
  color: #616161;
  max-width: 700px;
  margin: 1rem auto 4rem;
}

.cards-wrapper {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  justify-content: center;
  align-items: stretch;
  max-width: 1200px;
  margin: 0 auto;
}

.card {
  background-color: #fff;
  padding: 2rem 1.5rem;
  border-radius: 12px;
  text-align: left;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
  position: relative;
  display: flex;
  flex-direction: column;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.12);
}

.card-icon {
  width: 30px;
  height: 30px;
  margin-bottom: 1rem;
}

.card-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background-color: #f1f1f1;
  padding: 0.25rem 0.75rem;
  font-size: 0.7rem;
  border-radius: 12px;
  color: #616161;
}

.card-title {
  font-size: 1.125rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #212121;
}

.card-text {
  font-size: 0.875rem;
  color: #616161;
  line-height: 1.4;
}

/* Responsivo para telas menores */
@media (max-width: 900px) {
  .cards-wrapper {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .cards-wrapper {
    grid-template-columns: 1fr;
  }
}
</style>
