<template>
  <div class="container">
    <section class="profile-card">
      <div class="avatar">CD</div>
      <div class="info">
        <h2>Carlos Daniel</h2>
        <p>Analista de Desenvolvimento</p>
        <p><span>📧</span> carlosdaniel.messiasdossantos@gmail.com</p>
        <p><span>📍</span> São Paulo, SP</p>
        <p><span>💼</span> Tecnologia da Informação</p>
        <p><span>📅</span> Desde Janeiro 2023</p>
      </div>
      <button class="edit-btn">Editar Perfil</button>
    </section>

    <section class="stats">
      <div class="stat">
        <p class="number">12/15</p>
        <p class="label">Pesquisas Respondidas</p>
      </div>
      <div class="stat">
        <p class="number">80%</p>
        <p class="label">Taxa de Participação</p>
      </div>
      <div class="stat">
        <p class="number">5x</p>
        <p class="label">Sequência Atual</p>
      </div>
      <div class="stat">
        <p class="number">5 min</p>
        <p class="label">Tempo Médio</p>
      </div>
    </section>

    <div class="tabs">
      <button class="tab active">Atividade Recente</button>
      <button class="tab">Medalhas</button>
    </div>

    <section class="activity">
      <h3>Atividade Recente</h3>
      <ul class="activity-list">
        <li>
          <div class="activity-title">Pesquisa de Clima Q1 2024</div>
          <div class="activity-meta">Concluída • 14/01/2024</div>
          <span class="tag ativo">Ativo</span>
        </li>
        <li>
          <div class="activity-title">Satisfação com Benefícios</div>
          <div class="activity-meta">Concluída • 05/01/2024</div>
          <span class="tag finalizado">Finalizado</span>
        </li>
        <li>
          <div class="activity-title">Ambiente de Trabalho Remoto</div>
          <div class="activity-meta">Concluída • 27/12/2023</div>
          <span class="tag ativo">Ativo</span>
        </li>
      </ul>
    </section>
  </div>
</template>

<script>
export default {
  name: 'UserProfile',
  data() {
    return {
      activeTab: 'atividade',
    };
  },
  methods: {
    selectTab(tab) {
      this.activeTab = tab;
      console.log(`Aba "${tab}" selecionada`);
    },
  },
};
</script>

<style scoped>
.container {
  max-width: 800px;
  margin: auto;
  margin-top: 100PX;
}

p, h3, h2 {
  color: #fff;
}

.profile-card {
  display: flex;
  background: #1a1a1a;
  padding: 20px;
  border-radius: 10px;
  align-items: center;
  position: relative;
  gap: 20px;
}

.avatar {
  background: linear-gradient(135deg, #6a11cb, #2575fc);
  color: white;
  font-weight: bold;
  font-size: 24px;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: grid;
  place-items: center;
}

.info p {
  font-size: 14px;
  color: #ccc;
}

.edit-btn {
  position: absolute;
  right: 20px;
  top: 20px;
  background: linear-gradient(135deg, #6a11cb, #2575fc);
  color: white;
  border: none;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
}

.stats {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  background: #1a1a1a;
  padding: 20px;
  border-radius: 10px;
}

.stat {
  text-align: center;
}

.stat .number {
  font-size: 22px;
  font-weight: bold;
  color: #bb86fc;
}

.tabs {
  margin-top: 30px;
  display: flex;
  gap: 10px;
}

.tab {
  padding: 10px 20px;
  background: #262626;
  border: none;
  color: #fff;
  border-radius: 8px;
  cursor: pointer;
}

.tab.active {
  background: #6a11cb;
}

.activity {
  background: #1a1a1a;
  margin-top: 20px;
  border-radius: 10px;
  padding: 20px;
}

.activity-list {
  list-style: none;
  margin-top: 10px;
}

.activity-list li {
  background: #262626;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 8px;
  position: relative;
}

.activity-title {
  font-weight: bold;
}

.activity-meta {
  font-size: 12px;
  color: #aaa;
}

.tag {
  position: absolute;
  right: 15px;
  top: 15px;
  padding: 5px 10px;
  border-radius: 6px;
  font-size: 12px;
}

.tag.ativo {
  background-color: #6a11cb;
}

.tag.finalizado {
  background-color: #555;
}
</style>

<style>
body {
  background-color: #121212;
  margin: 0;
  padding: 0;
  font-family: sans-serif;
}
</style>
