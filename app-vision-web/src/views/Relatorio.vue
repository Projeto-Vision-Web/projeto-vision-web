<template lang="pug">
.dashboard
  .header
    .header-top
      div
        h1.header-title Relatórios e Análises
        p.header-subtitle Visualize os dados de clima organizacional da sua empresa
      .header-actions
        button Filtros
        button(@click="exportToPdf") Exportar PDF
    .filter-controls
      .control-group
        label Período:
        select
          option Último Trimestre
      .control-group
        label Pesquisa:
        select
          option Todas as Pesquisas

  main.dashboard-grid
    .kpi-row
  .kpi-card
    .kpi-header
      span.kpi-icon
      span.kpi-change.success ↑ +5%
    .kpi-value {{ header.taxaParticipacao }}%
    .kpi-label Taxa de Participação
    .kpi-subtext vs trimestre anterior

  .kpi-card
    .kpi-header
      span.kpi-icon
      span.kpi-change.success ↑ +8 pts
    .kpi-value {{ header.indiceSatisfacao }}
    .kpi-label Índice de Satisfação
    .kpi-subtext escala 0-100

  .kpi-card
    .kpi-header
      span.kpi-icon
      span.kpi-change.success ↑ +3%
    .kpi-value {{ header.engajamentoMedio }}%
    .kpi-label Engajamento Médio
    .kpi-subtext vs mês anterior

  .kpi-card
    .kpi-header
      span.kpi-icon
      span.kpi-change.danger ↓ -2 pts
    .kpi-value {{ header.npsOrganizacional }}
    .kpi-label NPS Organizacional
    .kpi-subtext Net Promoter Score

    .charts-row
      .chart-card.large
        h3.chart-title Evolução dos Indicadores
        p.chart-subtitle Últimos 6 meses - Pontuação média por categoria
        canvas#lineChart(style="max-height: 200px;")
        .chart-legend-custom
          span.legend-item.blue Satisfação
          span.legend-item.purple Engajamento
          span.legend-item.light-purple Comunicação

      .chart-card.large
        h3.chart-title Desempenho por Departamento
        p.chart-subtitle Índice de satisfação geral por área
        canvas#barChart(style="max-height: 200px;")

    .insights-top-grid
      .chart-card.small
        h3.chart-title Distribuição de Satisfação
        p.chart-subtitle Percentual de colaboradores por nível
        canvas#doughnutChart(style="max-height: 250px;")
      .chart-legend-pie
        p.legend-item-pie Muito Satisfeito: {{ distribuicaoSatisfacao.muitoSatisfeito }}%
        p.legend-item-pie Satisfeito: {{ distribuicaoSatisfacao.satisfeito }}%
        p.legend-item-pie Neutro: {{ distribuicaoSatisfacao.neutro }}%
        p.legend-item-pie Muito Insatisfeito: {{ distribuicaoSatisfacao.muitoInsatisfeito }}%
        p.legend-item-pie Insatisfeito: {{ distribuicaoSatisfacao.insatisfeito }}%

      .chart-card.small
        h3.chart-title Análise Comparativa
        p.chart-subtitle Período atual vs anterior por categoria
        canvas#radarChart(style="max-height: 250px;")
        .chart-legend-custom
          span.legend-item.purple Período Atual
          span.legend-item.light-purple Período Anterior

      .insights-container
        h3.insights-title Insights e Recomendações
        .insights-list
          .insight-card(
            v-for="insight in relatorio?.insights || []"
            :key="insight.titulo"
            :class="insightClass(insight.tipo)"
          )
            span.insight-icon
            div
              h4 {{ insight.titulo }}
              p {{ insight.descricao }}
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import Chart from 'chart.js/auto';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';

const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// mesmo decode do outro componente
function decodeJwtPayload(token) {
  try {
    const [, payloadBase64Url] = token.split('.');
    if (!payloadBase64Url) return null;

    const base64 = payloadBase64Url.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(
      base64.length + ((4 - (base64.length % 4 || 4)) % 4),
      '='
    );
    const json = atob(padded);
    return JSON.parse(json);
  } catch (e) {
    console.error('Erro ao decodificar JWT:', e);
    return null;
  }
}

export default {
  name: 'DashboardClima',
  setup() {
    const relatorio = ref(null);
    const loading = ref(false);
    const errorMessage = ref('');

    const charts = {
      line: null,
      bar: null,
      doughnut: null,
      radar: null,
    };

    const header = computed(() => relatorio.value?.header ?? {
      taxaParticipacao: 0,
      indiceSatisfacao: 0,
      engajamentoMedio: 0,
      npsOrganizacional: 0,
    });

    const distribuicaoSatisfacao = computed(
      () => relatorio.value?.distribuicaoSatisfacao ?? {
        muitoSatisfeito: 0,
        satisfeito: 0,
        neutro: 0,
        insatisfeito: 0,
        muitoInsatisfeito: 0,
      }
    );

    const exportToPdf = async () => {
    try {
      const element = document.querySelector('.dashboard');
      if (!element) {
        console.warn('Elemento .dashboard não encontrado.');
        return;
      }

    const pdf = new jsPDF('landscape', 'pt', 'a4'); // pt = pontos (mais preciso p/ html)

    await pdf.html(element, {
      callback: (doc) => {
        doc.save('relatorio-clima.pdf');
      },
      margin: [20, 20, 20, 20],
      autoPaging: 'text',
      html2canvas: {
        scale: 1.2,
        useCORS: true,
        scrollY: 0,
      },
      x: 10,
      y: 10,
    });
  } catch (e) {
    console.error('Erro ao exportar PDF:', e);
  }
};


    onMounted(() => {
      loadRelatorio();
    });

    function insightClass(tipo) {
      // mapeia o tipo vindo do back para a classe de cor
      switch (tipo) {
        case 'POSITIVO':
          return 'success-insight';
        case 'ALERTA':
          return 'warning-insight';
        case 'NEGATIVO':
          return 'info-insight'; // ou criar uma classe danger se quiser
        default:
          return 'info-insight';
      }
    }

    async function obterEmpresaId() {
      const token = localStorage.getItem('token');
      if (!token) {
        console.warn('Sem token no localStorage.');
        return null;
      }

      const payload = decodeJwtPayload(token);
      const idUsuario = payload?.id_usuario;
      if (!idUsuario) {
        console.warn('Não foi possível extrair id_usuario do token.');
        return null;
      }

      // mesmo fluxo do outro componente: pega colaborador e usa idEmpresa
      const { data } = await api.get(`/colaborador/${idUsuario}`);
      return data.idEmpresa;
    }

    function destroyCharts() {
      Object.keys(charts).forEach((key) => {
        if (charts[key]) {
          charts[key].destroy();
          charts[key] = null;
        }
      });
    }

    function buildCharts() {
      if (!relatorio.value) return;

      const {
        evolucaoIndicadores,
        desempenhoPorDepartamento,
        distribuicaoSatisfacao,
        analiseComparativa,
      } = relatorio.value;

      destroyCharts();

      const colorSatisfacao = '#5d50c6'; // Azul/Roxo
      const colorEngajamento = '#9c27b0'; // Roxo Escuro
      const colorComunicacao = '#ce93d8'; // Roxo Claro
      const colorAnterior = '#ce93d8';
      const colorAtual = '#9c27b0';

      // --- GRÁFICO DE LINHA (Evolução dos Indicadores) ---
      const lineCtx = document.getElementById('lineChart');
      if (lineCtx && evolucaoIndicadores?.pontos) {
        const labels = evolucaoIndicadores.pontos.map((p) => p.mes);
        const satisfacao = evolucaoIndicadores.pontos.map((p) => p.satisfacao);
        const engajamento = evolucaoIndicadores.pontos.map((p) => p.engajamento);
        const comunicacao = evolucaoIndicadores.pontos.map((p) => p.comunicacao);

        charts.line = new Chart(lineCtx.getContext('2d'), {
          type: 'line',
          data: {
            labels,
            datasets: [
              {
                label: 'Satisfação',
                data: satisfacao,
                borderColor: colorSatisfacao,
                backgroundColor: 'rgba(93, 80, 198, 0.1)',
                tension: 0.4,
                pointRadius: 5,
              },
              {
                label: 'Engajamento',
                data: engajamento,
                borderColor: colorEngajamento,
                backgroundColor: 'rgba(156, 39, 176, 0.1)',
                tension: 0.4,
                pointRadius: 5,
              },
              {
                label: 'Comunicação',
                data: comunicacao,
                borderColor: colorComunicacao,
                backgroundColor: 'rgba(206, 147, 216, 0.1)',
                tension: 0.4,
                pointRadius: 5,
              },
            ],
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              x: { grid: { color: '#333355' }, ticks: { color: '#f0f0f0' } },
              y: {
                beginAtZero: true,
                max: 100,
                grid: { color: '#333355' },
                ticks: { color: '#f0f0f0' },
              },
            },
            plugins: { legend: { display: false } },
          },
        });
      }

      // --- GRÁFICO DE BARRA (Desempenho por Departamento) ---
      const barCtx = document.getElementById('barChart');
      if (barCtx && desempenhoPorDepartamento) {
        const labels = desempenhoPorDepartamento.map((d) => d.departamento);
        const scores = desempenhoPorDepartamento.map((d) => d.scoreSatisfacao);

        charts.bar = new Chart(barCtx.getContext('2d'), {
          type: 'bar',
          data: {
            labels,
            datasets: [
              {
                label: 'Pontuação',
                data: scores,
                backgroundColor: colorSatisfacao,
                borderRadius: 5,
              },
            ],
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              x: { grid: { display: false }, ticks: { color: '#f0f0f0' } },
              y: {
                beginAtZero: true,
                max: 100,
                grid: { color: '#333355' },
                ticks: { color: '#f0f0f0' },
              },
            },
            plugins: { legend: { display: false } },
          },
        });
      }

      // --- GRÁFICO DE PIZZA (Distribuição de Satisfação) ---
      const doughnutCtx = document.getElementById('doughnutChart');
      if (doughnutCtx && distribuicaoSatisfacao) {
        charts.doughnut = new Chart(doughnutCtx.getContext('2d'), {
          type: 'doughnut',
          data: {
            labels: [
              'Muito Satisfeito',
              'Satisfeito',
              'Neutro',
              'Insatisfeito',
              'Muito Insatisfeito',
            ],
            datasets: [
              {
                data: [
                  distribuicaoSatisfacao.muitoSatisfeito,
                  distribuicaoSatisfacao.satisfeito,
                  distribuicaoSatisfacao.neutro,
                  distribuicaoSatisfacao.insatisfeito,
                  distribuicaoSatisfacao.muitoInsatisfeito,
                ],
                backgroundColor: [
                  '#9c27b0',
                  '#5d50c6',
                  '#a0a0c0',
                  '#ff416c',
                  '#ffc107',
                ],
                borderWidth: 0,
              },
            ],
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
          },
        });
      }

      // --- GRÁFICO RADAR (Análise Comparativa) ---
      const radarCtx = document.getElementById('radarChart');
      if (radarCtx && analiseComparativa?.categorias) {
        const labels = analiseComparativa.categorias.map((c) => c.categoria);
        const atual = analiseComparativa.categorias.map((c) => c.periodoAtual);
        const anterior = analiseComparativa.categorias.map(
          (c) => c.periodoAnterior
        );

        charts.radar = new Chart(radarCtx.getContext('2d'), {
          type: 'radar',
          data: {
            labels,
            datasets: [
              {
                label: 'Período Atual',
                data: atual,
                borderColor: colorAtual,
                backgroundColor: 'rgba(156, 39, 176, 0.2)',
                pointBackgroundColor: colorAtual,
              },
              {
                label: 'Período Anterior',
                data: anterior,
                borderColor: colorAnterior,
                backgroundColor: 'rgba(206, 147, 216, 0.2)',
                pointBackgroundColor: colorAnterior,
              },
            ],
          },
          options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
              r: {
                angleLines: { color: '#333355' },
                grid: { color: '#333355' },
                pointLabels: { color: '#f0f0f0', font: { size: 12 } },
                suggestedMin: 0,
                suggestedMax: 100,
                ticks: { display: false },
              },
            },
            plugins: { legend: { display: false } },
          },
        });
      }
    }

    async function loadRelatorio() {
      try {
        loading.value = true;
        errorMessage.value = '';

        const empresaId = await obterEmpresaId();
        if (!empresaId) {
          errorMessage.value = 'Não foi possível identificar a empresa do usuário.';
          return;
        }

        const { data } = await api.get('api/relatorios/clima', {
          params: {
            empresaId,
            periodoRef: '2025Q4', // fixo, como você comentou
          },
        });

        relatorio.value = data;
        buildCharts();
      } catch (err) {
        console.error('Erro ao carregar relatório de clima:', err);
        errorMessage.value = 'Erro ao carregar relatório de clima.';
      } finally {
        loading.value = false;
      }
    }

    onMounted(() => {
      loadRelatorio();
    });

    return {
      relatorio,
      header,
      distribuicaoSatisfacao,
      insightClass,
      loading,
      errorMessage,
      exportToPdf
    };
  },
};
</script>

<style scoped>
:root {
  --bg-color: #1a1a2e;
  --card-bg-color: #24243d;
  --text-color: #f0f0f0;
  --subtext-color: #a0a0c0;
  --primary-color: #5d50c6;
  --success-color: #00e090;
  --danger-color: #ff416c;
  --warning-color: #ffc107;
  --info-color: #4a90e2;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: var(--bg-color);
  color: var(--text-color);
  padding: 20px;
}

.dashboard {
  max-width: 1400px;
  margin: 0 auto;
  /* NOVO: espaço para não ficar debaixo da navbar */
  padding: 100px 20px 40px;
  min-height: 100vh; /* opcional, só pra página “fechar” a tela */
}
.header {
  padding-bottom: 20px;
  border-bottom: 1px solid #333355;
  margin-bottom: 20px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.header-title {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
}

.header-subtitle {
  font-size: 14px;
  color: var(--subtext-color);
}

.header-actions button {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid var(--primary-color);
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 14px;
}

.header-actions button:last-child {
  background-color: var(--primary-color); 
  border-color: var(--primary-color);
}

.filter-controls {
  display: flex;
  gap: 20px;
  padding-bottom: 20px;
}

.control-group {
  display: flex;
  align-items: center;
}

.control-group label {
  font-size: 14px;
  color: var(--subtext-color);
  margin-right: 10px;
}

.filter-controls select {
  background-color: var(--card-bg-color);
  color: var(--text-color);
  border: 1px solid #444;
  padding: 8px 12px;
  border-radius: 5px;
  cursor: pointer;
  min-width: 150px;
}

.dashboard-grid {
  display: grid;
  gap: 20px;
}

.kpi-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(250px, 1fr));
  gap: 20px;
}

.kpi-card {
  background-color: var(--card-bg-color);
  padding: 20px;
  border-radius: 10px;
  min-height: 140px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.kpi-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.kpi-icon::before { 
  content: '\2666'; 
  font-size: 24px;
  color: var(--primary-color);
}

.kpi-change {
  font-size: 12px;
  font-weight: bold;
  padding: 2px 6px;
  border-radius: 4px;
}

.kpi-change.success {
  color: var(--success-color);
  background-color: rgba(0, 224, 144, 0.15);
}

.kpi-change.danger {
  color: var(--danger-color);
  background-color: rgba(255, 65, 108, 0.15);
}

.kpi-value {
  font-size: 28px;
  font-weight: bold;
}

.kpi-label {
  font-size: 14px;
  color: var(--subtext-color);
}

.kpi-subtext {
  font-size: 12px;
  color: #666;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background-color: var(--card-bg-color);
  padding: 20px;
  border-radius: 10px;
}

.chart-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 5px;
}

.chart-subtitle {
  font-size: 13px;
  color: var(--subtext-color);
  margin-bottom: 15px;
}

.chart-legend-custom {
  display: flex;
  gap: 20px;
  font-size: 12px;
  margin-top: 15px;
  justify-content: center;
}

.legend-item::before {
  content: "•";
  margin-right: 5px;
  font-size: 20px;
  line-height: 1;
}

.legend-item.blue::before { color: #5d50c6; }
.legend-item.purple::before { color: #9c27b0; }
.legend-item.light-purple::before { color: #ce93d8; }

.insights-top-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.insights-container {
  grid-column: 1 / -1; 
}

.insights-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-top: 20px;
  border-top: 1px solid #333355;
}

.insights-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.insight-card {
  background-color: var(--card-bg-color);
  padding: 15px;
  border-radius: 10px;
  display: flex;
  gap: 15px;
  align-items: flex-start;
  border-left: 4px solid;
}

.insight-icon {
  font-size: 24px;
  color: var(--primary-color);
  padding: 5px;
  border-radius: 50%;
  margin-top: 2px;
}

.insight-icon::before {
  content: '\2605'; 
  font-size: 20px;
}

.insight-card h4 {
  font-size: 16px;
  margin-bottom: 5px;
  color: var(--text-color);
}

.insight-card p {
  font-size: 14px;
  color: var(--subtext-color);
}

.success-insight { border-left-color: var(--success-color); }
.warning-insight { border-left-color: var(--warning-color); }
.info-insight { border-left-color: var(--info-color); }

.chart-legend-pie {
  font-size: 14px;
  margin-top: 20px;
  padding: 10px;
  background-color: #333355;
  border-radius: 5px;
}

.legend-item-pie {
  margin-bottom: 5px;
  padding-left: 15px;
  position: relative;
}

.legend-item-pie::before { content: "•"; position: absolute; left: 0; }
.legend-item-pie:nth-child(1)::before { color: #9c27b0; } 
.legend-item-pie:nth-child(2)::before { color: #5d50c6; } 
.legend-item-pie:nth-child(3)::before { color: #a0a0c0; } 
.legend-item-pie:nth-child(4)::before { color: #ffc107; } 
.legend-item-pie:nth-child(5)::before { color: #ff416c; }
</style>