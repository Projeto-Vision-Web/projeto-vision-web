<script setup>
import { ref, computed, watchEffect, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// ======================================================================
// API CONFIG
// ======================================================================

// baseURL vem do .env (ex.: VITE_API_URL=http://localhost:8080)
const api = axios.create({
  baseURL: 'http://localhost:8080/',
});

// ======================================================================
// ESTADOS PRINCIPAIS
// ======================================================================

const currentScreen = ref('access');
const profile = ref('admin');

const isCreatingSurvey = ref(false);
const isResponding = ref(false);
const isPreviewingSurvey = ref(false);

const currentSurveyToRespond = ref(null);
const collaboratorResponses = ref({});

const editingSurveyId = ref(null);

const surveys = ref([]); // lista de formulários vinda da API

// survey em edição/criação
const newSurvey = ref({
  id: null,
  title: '',
  questions: [],
  status: 'Rascunho',
  responses: 0,
});

const loading = ref(false);
const errorMessage = ref('');

// Pesquisas ativas (para colaborador)
const activeSurveys = computed(() =>
  surveys.value.filter((s) => s.status === 'Ativa')
);

// ======================================================================
// HELPERS DE MAPEAR DTO <-> VIEW MODEL
// ======================================================================

/**
 * Mapeia um FormularioResponseDto vindo da API
 * para o formato que o componente usa (survey).
 *
 * Assumindo que o back devolve algo assim:
 * {
 *   id: 1,
 *   titulo: "Clima Q4",
 *   descricao: "...",
 *   status: "ATIVO" | "RASCUNHO",
 *   dataCriacao: "2025-10-01",
 *   totalRespostas: 42,
 *   perguntas: [
 *     {
 *       id: 10,
 *       textoPergunta: "A comunicação é eficaz?",
 *       tipo: "TEXTO" | "MULTIPLA_ESCOLHA" | "ESCALA",
 *       opcoes: [{ id: 1, textoOpcao: "Sim", valorNum: 1 }, ...]
 *     }
 *   ],
 *   coletaAtualId: 5 // id da coleta ativa (se houver)
 * }
 */
function mapFormularioToSurvey(dto) {
  const statusFront =
    dto.status === 'ATIVO' || dto.status === 'PUBLICADO' ? 'Ativa' : 'Rascunho';

  const created =
    dto.dataCriacao?.split('-').reverse().join('/') ?? '—';

  return {
    id: dto.id,
    title: dto.titulo,
    description: dto.descricao,
    status: statusFront,
    createdDate: created,
    responses: dto.totalRespostas ?? 0,
    coletaId: dto.coletaAtualId ?? null,
    questions: (dto.perguntas || []).map((p) => ({
      id: p.id,
      text: p.textoPergunta,
      type: mapTipoPerguntaBackToFront(p.tipo),
      typeDisplay: mapTipoPerguntaDisplay(p.tipo),
      options: (p.opcoes || []).map((o) => o.textoOpcao),
    })),
  };
}

function mapTipoPerguntaBackToFront(tipo) {
  switch (tipo) {
    case 'TEXTO':
      return 'text';
    case 'MULTIPLA_ESCOLHA':
      return 'multiple';
    case 'ESCALA':
      return 'scale';
    default:
      return 'text';
  }
}

function mapTipoPerguntaFrontToBack(type) {
  switch (type) {
    case 'text':
      return 'TEXTO';
    case 'multiple':
      return 'MULTIPLA_ESCOLHA';
    case 'scale':
      return 'ESCALA';
    default:
      return 'TEXTO';
  }
}

function mapTipoPerguntaDisplay(tipoBackOuFront) {
  const t = tipoBackOuFront.toUpperCase?.() || tipoBackOuFront;
  if (t === 'TEXTO' || t === 'TEXT') return 'Texto Livre';
  if (t === 'MULTIPLA_ESCOLHA' || t === 'MULTIPLE') return 'Múltipla Escolha';
  if (t === 'ESCALA' || t === 'SCALE') return 'Escala (1-5)';
  return 'Texto Livre';
}

/**
 * Mapeia o survey do front para o payload de criação/edição de formulário.
 *
 * Assumindo que o endpoint POST/PUT /formularios espera algo como:
 * {
 *   titulo,
 *   descricao,
 *   status,
 *   perguntas: [
 *     {
 *       id (opcional em criação),
 *       textoPergunta,
 *       tipo,
 *       opcoes: [{ textoOpcao, valorNum }]
 *     }
 *   ]
 * }
 */
function mapSurveyToFormularioPayload(survey, statusBack) {
  return {
    titulo: survey.title,
    descricao: survey.description || '',
    status: statusBack, // "RASCUNHO" ou "ATIVO"
    perguntas: survey.questions.map((q, indexPergunta) => ({
      id: q.id ?? null,
      textoPergunta: q.text,
      tipo: mapTipoPerguntaFrontToBack(q.type),
      opcoes:
        q.type === 'multiple' || q.type === 'scale'
          ? q.options
              .filter((o) => o && o.trim() !== '')
              .map((o, idx) => ({
                id: null,
                textoOpcao: o,
                valorNum: idx + 1,
              }))
          : [],
    })),
  };
}

// ======================================================================
// CARREGAR PESQUISAS DA API
// ======================================================================

async function loadSurveysFromApi() {
  try {
    loading.value = true;
    errorMessage.value = '';

    const { data } = await api.get('/formulario'); // ajuste se seu path for diferente
    surveys.value = (data || []).map(mapFormularioToSurvey);
    console.log(data)
  } catch (err) {
    console.error(err);
    errorMessage.value = 'Erro ao carregar pesquisas.';
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadSurveysFromApi();
});

// ======================================================================
// NAVEGAÇÃO / PERFIL / HEADER
// ======================================================================

const router = useRouter();
const showProfileDropdown = ref(false);

const closeProfileDropdown = () => {
  showProfileDropdown.value = false;
};

const toggleProfileDropdown = (event) => {
  event.stopPropagation();
  showProfileDropdown.value = !showProfileDropdown.value;
};

const goToProfile = () => {
  router.push('/perfil');
  closeProfileDropdown();
};

const logout = () => {
  router.push('/login');
  closeProfileDropdown();
};

const handleClickOutside = (event) => {
  // se clicar em qualquer lugar, fecha
  showProfileDropdown.value = false;
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

const goToReportsPage = () => {
  router.push('/relatorios'); // ou apenas um alert se ainda for MVP
};

const selectProfile = (p) => {
  profile.value = p;
};

// ======================================================================
// MODAL DE CRIAÇÃO / EDIÇÃO
// ======================================================================

const resetNewSurvey = () => {
  newSurvey.value = {
    id: null,
    title: '',
    description: '',
    questions: [],
    responses: 0,
    status: 'Rascunho',
  };
};

const openCreateSurveyModal = () => {
  resetNewSurvey();
  editingSurveyId.value = null;
  isCreatingSurvey.value = true;
};

const closeCreateSurveyModal = () => {
  isCreatingSurvey.value = false;
  editingSurveyId.value = null;
};

const openEditSurveyModal = (survey) => {
  newSurvey.value = JSON.parse(JSON.stringify(survey));
  editingSurveyId.value = survey.id;
  isCreatingSurvey.value = true;
};

const openSurveyDetails = (survey) => {
  if (survey.status === 'Rascunho') {
    openEditSurveyModal(survey);
  } else {
    openResponseModal(survey);
    isPreviewingSurvey.value = true;
  }
};

// ======================================================================
// CRUD / PUBLICAÇÃO (INTEGRADO COM API)
// ======================================================================

const addQuestion = (type) => {
  let question = {
    id: null,
    text: '',
    type,
    typeDisplay: '',
    options: [],
  };

  if (type === 'multiple') {
    question.typeDisplay = 'Múltipla Escolha';
    question.options = ['', ''];
  } else if (type === 'scale') {
    question.typeDisplay = 'Escala (1-5)';
    question.options = ['1', '2', '3', '4', '5']; // opcional
  } else {
    question.typeDisplay = 'Texto Livre';
  }

  newSurvey.value.questions.push(question);
};

const removeQuestion = (index) => {
  newSurvey.value.questions.splice(index, 1);
};

const addOption = (qIndex) => {
  newSurvey.value.questions[qIndex].options.push('');
};

const removeOption = (qIndex, oIndex) => {
  newSurvey.value.questions[qIndex].options.splice(oIndex, 1);
};

async function saveSurvey(statusFront) {
  if (!newSurvey.value.title) {
    alert('O Título da Pesquisa é obrigatório para salvar!');
    return;
  }

  const questionsToSave = newSurvey.value.questions.filter((q) => {
    if (!q.text || q.text.trim() === '') return false;
    if (q.type === 'multiple') {
      return q.options.filter((o) => o && o.trim() !== '').length >= 2;
    }
    return true;
  });

  if (questionsToSave.length === 0) {
    alert(
      'Adicione pelo menos uma pergunta válida (Múltipla Escolha requer no mínimo 2 opções).'
    );
    return;
  }

  const statusBack =
    statusFront === 'Ativa' ? 'ATIVO' : 'RASCUNHO';

  const surveyToPersist = {
    ...newSurvey.value,
    questions: questionsToSave,
  };

  const payload = mapSurveyToFormularioPayload(
    surveyToPersist,
    statusBack
  );

  try {
    loading.value = true;
    errorMessage.value = '';

    if (editingSurveyId.value) {
      const { data } = await api.put(
        `/formularios/${editingSurveyId.value}`,
        payload
      );
      const updated = mapFormularioToSurvey(data);
      const idx = surveys.value.findIndex(
        (s) => s.id === editingSurveyId.value
      );
      if (idx !== -1) {
        surveys.value[idx] = updated;
      }
      alert(
        `Pesquisa "${updated.title}" atualizada com sucesso como ${updated.status}.`
      );
    } else {
      const { data } = await api.post('/formularios', payload);
      const created = mapFormularioToSurvey(data);
      surveys.value.push(created);
      alert(
        `Pesquisa "${created.title}" salva como ${created.status}.`
      );
    }

    closeCreateSurveyModal();
  } catch (err) {
    console.error(err);
    alert('Erro ao salvar pesquisa.');
  } finally {
    loading.value = false;
  }
}

const publishSurvey = () => saveSurvey('Ativa');

async function publishExistingSurvey(surveyId) {
  try {
    loading.value = true;
    errorMessage.value = '';

    // Exemplo de payload – ajuste para o que seu endpoint espera
    const payload = {
      idEmpresa: 1,
      periodoRef: '2025Q4',
      canal: 'EMAIL',
    };

    await api.post(`/formularios/${surveyId}/publicar`, payload);

    // recarrega a lista para pegar status e coletaAtualId atualizado
    await loadSurveysFromApi();

    alert('Pesquisa publicada com sucesso!');
  } catch (err) {
    console.error(err);
    alert('Erro ao publicar pesquisa.');
  } finally {
    loading.value = false;
  }
}

async function deleteSurvey(surveyId) {
  if (
    !confirm(
      'Tem certeza que deseja deletar esta pesquisa? Esta ação é irreversível.'
    )
  ) {
    return;
  }

  try {
    loading.value = true;
    errorMessage.value = '';

    await api.delete(`/formularios/${surveyId}`);
    surveys.value = surveys.value.filter((s) => s.id !== surveyId);
    alert('Pesquisa deletada com sucesso.');
  } catch (err) {
    console.error(err);
    alert('Erro ao deletar pesquisa.');
  } finally {
    loading.value = false;
  }
}

// ======================================================================
// RESPOSTAS DO COLABORADOR -> ENDPOINT DE COLETA
// ======================================================================

const openResponseModal = (survey) => {
  currentSurveyToRespond.value = survey;
  isPreviewingSurvey.value = false;

  const initialResponses = {};
  survey.questions.forEach((q) => {
    // usa o id da pergunta como chave
    initialResponses[q.id] = q.type === 'scale' ? null : '';
  });

  collaboratorResponses.value = initialResponses;
  isResponding.value = true;
};

const closeResponseModal = () => {
  isResponding.value = false;
  currentSurveyToRespond.value = null;
  collaboratorResponses.value = {};
  isPreviewingSurvey.value = false;
};

async function submitResponses() {
  if (!currentSurveyToRespond.value) return;

  const qts = currentSurveyToRespond.value.questions;
  const totalQuestions = qts.length;

  const answeredCount = qts.filter((q) => {
    const value = collaboratorResponses.value[q.id];
    if (typeof value === 'string') return value.trim() !== '';
    return value !== null && value !== undefined;
  }).length;

  if (answeredCount < totalQuestions) {
    alert('Por favor, responda a todas as perguntas antes de enviar.');
    return;
  }

  // Assumindo que o survey tem `coletaId` (id da coleta ativa)
  const coletaId = currentSurveyToRespond.value.coletaId;
  if (!coletaId) {
    alert(
      'Não foi possível identificar a coleta ativa para esta pesquisa.'
    );
    return;
  }

  // Payload esperado pelo endpoint POST /coletas/{id}/respostas
  // Ajuste para bater com seu DTO de request (idColaborador, etc.)
  const respostasPayload = qts.map((q) => ({
    idPergunta: q.id,
    valorResposta: collaboratorResponses.value[q.id],
  }));

  const payload = {
    idColaborador: 1, // TODO: pegar do usuário logado / token
    respostas: respostasPayload,
  };

  try {
    loading.value = true;
    errorMessage.value = '';

    await api.post(`/coletas/${coletaId}/respostas`, payload);

    alert(
      `Obrigado! Sua resposta para "${currentSurveyToRespond.value.title}" foi enviada com sucesso!`
    );

    // Atualiza contador de respostas localmente
    const idx = surveys.value.findIndex(
      (s) => s.id === currentSurveyToRespond.value.id
    );
    if (idx !== -1) {
      surveys.value[idx].responses =
        (surveys.value[idx].responses || 0) + 1;
    }

    closeResponseModal();
  } catch (err) {
    console.error(err);
    alert('Erro ao enviar respostas.');
  } finally {
    loading.value = false;
  }
}
</script>
<<<<<<< HEAD

<style>
/* ========================================================= */
/* VARIÁVEIS DE CORES */
/* ========================================================= */
:root {
  --color-bg-dark: #0d0d0d; 
  --color-bg-card: rgba(31, 31, 31, 0.8); 
  --color-text-light: #ffffff;
  --color-text-secondary: #d0d0d0; 
  --color-primary-gradient: linear-gradient(90deg, #313ef8, #7049fa); 
  --color-primary-shadow: rgba(112, 73, 250, 0.4); 
  --color-success: #3cb371; 
  --color-secondary-btn: #2d2d2d; 
}

/* Reset, Body, App, Container */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  background-color: var(--color-bg-dark);
  color: var(--color-text-light);
  font-family: 'Segoe UI', Arial, sans-serif; 
  min-height: 100vh;
}

#app {
  min-height: 100vh;
}

.container {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  padding-top: 100px; /* <<<<< Espaço para o Header Fixo */
}

/* ============== HEADER FIXO (REMOVIDO DESTE ARQUIVO) ============== */
/* Todo o CSS .header, .header-left, .logo, .header-title foi movido para MainNavbar.vue */


/* ============== HERO ACCESS SECTION (Containers principais) ============== */
@keyframes neon-pulse {
    0% { box-shadow: 0 10px 60px rgba(0,0,0,0.6), 0 0 30px rgba(49, 62, 248, 0.2); }
    50% { box-shadow: 0 10px 60px rgba(0,0,0,0.8), 0 0 50px rgba(49, 62, 248, 0.4); }
    100% { box-shadow: 0 10px 60px rgba(0,0,0,0.6), 0 0 30px rgba(49, 62, 248, 0.2); }
}

.access-page-content {
    width: 100%;
    max-width: 900px;
    text-align: center;
    padding-bottom: 40px;
    display: flex;
    flex-direction: column;
    align-items: center;
}

.header-titles {
    margin-bottom: 50px; 
}

.hero-vision-title.large-title {
    font-size: 3.5em; 
    font-weight: 800;
    margin: 0;
    background: var(--color-primary-gradient);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-shadow: 0 0 40px rgba(112, 73, 250, 0.5);
}

.hero-vision-subtitle.large-subtitle {
    color: var(--color-text-secondary);
    font-size: 1.3em; 
    margin-top: 10px;
}

.hero-card {
  display: none; 
}

.access-system-box {
    background-color: var(--color-bg-card); 
    border-radius: 12px;
    padding: 25px;
    width: 100%;
    max-width: 400px; 
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.4);
    border: 1px solid #333;
    transition: all 0.3s ease; 
}

.access-system-box:hover,
.access-system-box:focus-within { 
    border: 1px solid #7049fa; 
    box-shadow: 0 0 15px rgba(112, 73, 250, 0.5); 
}

.access-system-box h2 {
    font-size: 1.5em;
    color: var(--color-text-light);
    margin-top: 0;
    margin-bottom: 15px;
}

.access-system-box p {
    color: var(--color-text-secondary);
    margin-bottom: 20px;
}

.profile-selector {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin: 0;
}

.profile-btn {
  background: var(--color-secondary-btn); 
  color: var(--color-text-light);
  border: 1px solid #444; 
  padding: 12px 24px;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 1em;
  font-weight: 500;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px; 
}

/* INÍCIO DA MUDANÇA: Estilo para os ícones de botão de perfil */
.profile-btn-icon {
  width: 24px; /* Aumentado de 22px para 24px */
  height: 24px; /* Aumentado de 22px para 24px */
  filter: invert(1) brightness(0.8); 
  opacity: 0.8;
  transition: all 0.3s;
}

.profile-btn.active .profile-btn-icon,
.profile-btn:hover:not(:disabled) .profile-btn-icon {
  filter: invert(1) brightness(1);
  opacity: 1;
}
/* FIM DA MUDANÇA */


.profile-btn.active, .profile-btn:hover:not(:disabled) {
  background: var(--color-primary-gradient);
  border-color: #7049fa;
  box-shadow: 0 0 25px var(--color-primary-shadow), 0 0 5px rgba(255, 255, 255, 0.5); 
  transform: translateY(-2px);
}

.profile-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    background: #1a1a1a;
    border-color: #333;
    box-shadow: none;
    transform: none;
}


/* ============== ESTILOS DASHBOARD (Gestor e Colaborador) ============== */
.dashboard {
  width: 100%;
  max-width: 900px;
  padding-top: 20px; 
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start; 
  margin-bottom: 30px;
  padding: 0 5px;
}

.dashboard-header-right {
  display: flex;
  gap: 15px; 
}


.dashboard-header .header-content-left {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.dashboard-header h2 {
    background: var(--color-primary-gradient);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    text-shadow: 0 0 10px rgba(112, 73, 250, 0.3);
}
.dashboard-header p {
  font-size: 1em;
  color: var(--color-text-secondary);
  margin-bottom: 0; 
  margin-top: 5px; 
}

.action-btn {
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  font-size: 1em;
  transition: all 0.3s;
}

.action-btn.primary {
  background: var(--color-primary-gradient);
  color: var(--color-text-light);
  box-shadow: 0 5px 15px rgba(112, 73, 250, 0.4);
}

.action-btn.small-btn {
    padding: 8px 15px;
    font-size: 0.9em;
}

.action-btn.secondary {
  background-color: var(--color-secondary-btn);
  color: var(--color-text-light);
  border: 1px solid #444;
}

.action-btn.success {
  background-color: var(--color-success);
  color: var(--color-bg-dark);
}

.action-btn.purple-gradient {
    background: linear-gradient(90deg, #4f46e5, #8a2be2);
    color: var(--color-text-light);
    box-shadow: 0 5px 15px rgba(112, 73, 250, 0.4);
}


.action-btn:hover:not(:disabled) {
    opacity: 0.9;
    transform: translateY(-1px);
}

/* LISTA DE PESQUISAS EMPILHADAS */
.stacked-survey-card {
    background-color: rgba(31, 31, 31, 0.95);
    border-radius: 12px;
    padding: 10px; 
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.5);
    border: 1px solid rgba(255, 255, 255, 0.08);
}

.survey-list {
    max-height: 500px; 
    overflow-y: auto; 
    padding: 10px 0; 
    
    scrollbar-color: #7049fa #1a1a1a;
    scrollbar-width: thin;
    transition: all 0.2s;
}

.survey-item {
  background-color: var(--color-bg-card);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-left: 5px solid transparent; 
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
  margin-bottom: 10px; 
  border-top: 1px solid rgba(255, 255, 255, 0.05); 
  border-right: 1px solid rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  cursor: pointer; 
  transition: all 0.3s;
}

.survey-item:hover {
    background-color: rgba(31, 31, 31, 0.95); 
    transform: translateY(-2px);
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.8), 0 0 10px rgba(112, 73, 250, 0.3);
    border-left-color: #7049fa; 
}

.survey-info {
    display: flex;
    flex-direction: column;
    align-items: flex-start; 
    flex-grow: 1; 
    padding-right: 20px; 
}

.survey-info h3 {
    margin-bottom: 5px; 
}

.survey-info .subtitle {
    margin-top: 5px; 
}

.survey-info h3, 
.survey-info .subtitle {
    text-align: left; 
}

.survey-actions {
    display: flex;
    gap: 10px;
    flex-shrink: 0; 
}

.status-tag {
  padding: 3px 8px;
  border-radius: 4px;
  font-size: 0.75em;
  font-weight: bold;
  margin-left: 10px;
  color: var(--color-bg-dark);
}

.status-tag.ativa { background-color: #22c55e; } 
.status-tag.finalizada { background-color: #6a6a6a; color: var(--color-text-light); }
.status-tag.rascunho { background-color: #ffb400; } 

.meta {
  display: flex;
  gap: 20px;
  font-size: 0.9em;
  color: #a8a8a8;
  margin-top: 10px;
}

.icon-btn {
  background: var(--color-secondary-btn); 
  border: 1px solid #333;
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 8px; 
  border-radius: 6px;
  font-size: 1em;
  transition: all 0.2s;
  
  display: flex;
  justify-content: center;
  align-items: center;
  
  width: 40px; 
  height: 40px;
}

.icon-btn img {
    width: 24px; 
    height: 24px;
    filter: invert(70%) saturate(200%); 
    transition: filter 0.2s;
    vertical-align: middle; 
}

.icon-btn i {
    color: var(--color-text-secondary);
    transition: color 0.2s;
}

.icon-btn:hover {
  border-color: #7049fa; 
  background-color: #1a1a1a; 
  box-shadow: 0 0 15px var(--color-primary-shadow); 
}

.icon-btn:hover img {
    filter: invert(100%) saturate(100%); 
}

.icon-btn:hover i {
    color: var(--color-text-light);
}


/* ============== ESTILOS DO MODAL/BOX (TELA 9) ============== */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.9); 
    backdrop-filter: blur(5px); 
    -webkit-backdrop-filter: blur(5px);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2000; 
}

.modal-content {
    position: relative;
    max-width: 750px;
    width: 90%;
    max-height: 90vh; 
    overflow-y: auto; 
    transform: scale(1);
    animation: fadeIn 0.3s ease;
    padding: 30px; 
}

.close-modal-btn {
    position: absolute;
    top: 20px;
    right: 20px;
    background: none;
    border: none;
    color: var(--color-text-secondary);
    font-size: 1.5em;
    cursor: pointer;
    z-index: 2;
    transition: color 0.2s;
}

.close-modal-btn:hover {
    color: #ff4d4d;
}

.modal-enter-active, .modal-leave-active {
    transition: opacity 0.3s ease;
}

.modal-enter-from, .modal-leave-to {
    opacity: 0;
}

.fade-slide-enter-active,
.fade-slide-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-slide-enter-from {
    opacity: 0;
    transform: translateY(10px);
}

.fade-slide-leave-to {
    opacity: 0;
    transform: translateY(-10px);
}

.modal-title-group h2 {
    font-size: 1.8rem;
    color: var(--color-text-light); 
    background: none; 
    -webkit-background-clip: initial;
    -webkit-text-fill-color: initial;
    text-shadow: none;
    margin-bottom: 5px;
}
.modal-title-group p {
    color: var(--color-text-secondary);
    margin-bottom: 20px;
}

/* FORMULÁRIO DE PERGUNTAS (Estilos da Tela 9) */
.questions-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
}

.questions-header-row label {
    font-size: 1.2em; 
    font-weight: bold;
    color: var(--color-text-light);
    flex-shrink: 0;
}

.question-type-buttons {
    display: flex;
    gap: 8px;
}

.question-type-buttons .type-btn {
    margin-top: 15px;
    padding: 8px 15px;
    font-size: 0.9em;
    background-color: var(--color-secondary-btn); 
    color: var(--color-text-light);
    border: 1px solid #444;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.2s;
}

.question-type-buttons .type-btn:hover {
    background: var(--color-primary-gradient); 
    border-color: #7049fa;
    box-shadow: 0 0 10px var(--color-primary-shadow);
    transform: translateY(-1px);
    opacity: 1; 
}


.form-group input[type="text"] {
    margin-top: 15px;
    width: 100%; 
    padding: 12px;
    background-color: #222222;
    border: 1px solid #444444;
    color: var(--color-text-light);
    border-radius: 6px;
    box-sizing: border-box;
}

.question-item {
    background-color: rgba(255, 255, 255, 0.05);
    border-radius: 8px;
    padding: 15px;
    margin-top: 15px;
    border: 1px solid #444;
}

.action-buttons-footer {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    padding-top: 20px;
    border-top: 1px solid #333;
    margin-top: 30px;
}

.q-type-tag {
    background: linear-gradient(90deg, #4f46e5, #8a2be2); 
    padding: 3px 8px;
    border-radius: 4px;
    font-size: 0.8em;
    font-weight: bold;
    -webkit-background-clip: initial;
    -webkit-text-fill-color: initial;
    color: var(--color-text-light); 
}

.question-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 10px;
}

.delete-q-btn {
    background: none;
    border: none;
    color: #ff4d4d; 
    font-size: 1.2em;
    cursor: pointer;
    transition: color 0.2s, transform 0.2s;
}

.delete-q-btn:hover {
    color: #ff0000;
    transform: scale(1.1);
}

.options-group input {
    margin-bottom: 8px;
}

.option-item {
    display: flex;
    gap: 5px;
    align-items: center;
}

.remove-option-btn {
    width: 30px;
    height: 30px;
    padding: 5px;
    background-color: #333; 
    border-color: #ff4d4d;
    transition: all 0.2s;
}

.remove-option-btn img {
    width: 14px; 
    height: 14px;
    filter: invert(40%) sepia(90%) saturate(1000%) hue-rotate(330deg); 
    transition: filter 0.2s;
}

.remove-option-btn:hover {
    background-color: #ff4d4d; 
    box-shadow: 0 0 10px rgba(255, 77, 77, 0.4);
    border-color: #ff4d4d;
}

.remove-option-btn:hover img {
    filter: invert(100%); 
}


.add-option-btn {
    background-color: #333;
    color: #7049fa;
    border: 1px dashed #7049fa;
    padding: 6px 10px;
    border-radius: 6px;
    cursor: pointer;
    font-size: 0.9em;
    margin-top: 5px;
    transition: background-color 0.2s;
}

.add-option-btn:hover {
    background-color: #444;
}


.scale-info p {
    color: var(--color-text-secondary);
    font-size: 0.9em;
    margin-top: 5px;
}

.no-data {
    color: #6a6a6a;
    text-align: center;
    padding: 20px 0;
}

/* ========================================================= */
/* ESTILOS DE RESPOSTA DO COLABORADOR */
/* ========================================================= */

.response-modal {
    max-width: 700px;
}

.survey-form-content {
    padding-bottom: 20px;
    border: none; 
}

/* Estilos para o modo de visualização (read-only) */
.survey-form-content:disabled {
    opacity: 0.7;
    background: none; 
}

.survey-form-content:disabled .text-input,
.survey-form-content:disabled .option-radio-item input[type="radio"] + label,
.survey-form-content:disabled .scale-radio-item label {
  color: #a8a8a8;
  cursor: not-allowed;
}

.survey-form-content:disabled .option-radio-item,
.survey-form-content:disabled .scale-radio-options {
    background-color: #111; 
}

.survey-form-content:disabled .option-radio-item input[type="radio"],
.survey-form-content:disabled .scale-radio-item input[type="radio"] {
  opacity: 0.4;
  cursor: not-allowed;
}

.survey-form-content:disabled .option-radio-item:hover,
.survey-form-content:disabled .scale-radio-item:hover {
  /* Remove o efeito de hover no modo preview */
  border-color: #333;
  transform: none;
  box-shadow: none;
  background-color: #111;
}

/* Footer do modo preview */
.preview-footer {
  display: flex;
  justify-content: center; /* Centraliza o botão */
  padding-top: 20px;
  border-top: 1px solid #333;
  margin-top: 30px;
}

.question-item {
    background-color: rgba(255, 255, 255, 0.05);
    border-radius: 8px;
    padding: 15px;
    margin-bottom: 15px;
    border: 1px solid #444;
}

.question-item label {
    display: block;
    font-weight: bold;
    margin-bottom: 10px;
    color: var(--color-text-light);
    font-size: 1.1em;
}

/* Estilo para Textarea */
.text-input {
    width: 100%;
    padding: 10px;
    background-color: #1a1a1a;
    border: 1px solid #444;
    color: var(--color-text-light);
    border-radius: 6px;
    resize: vertical;
}

/* Estilos para Múltipla Escolha */
.multiple-options-group {
    display: flex;
    flex-direction: column;
    gap: 10px;
}

.option-radio-item {
    display: flex;
    align-items: center;
    background-color: #1a1a1a;
    border: 1px solid #333;
    padding: 10px;
    border-radius: 6px;
    transition: all 0.2s;
}

.option-radio-item:hover {
    border-color: #7049fa;
}

.option-radio-item input[type="radio"] {
    margin-right: 10px;
    appearance: none;
    width: 18px;
    height: 18px;
    border: 2px solid #7049fa;
    border-radius: 50%;
    position: relative;
    cursor: pointer;
    background-color: transparent;
}

.option-radio-item input[type="radio"]:checked::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #7049fa;
    transform: translate(-50%, -50%);
}

.option-radio-item label {
    margin-bottom: 0;
    cursor: pointer;
    font-weight: normal;
}

/* ========================================================= */
/* ESTILOS PARA ESCALA (NOVO DESIGN: RADIO BUTTONS EM LINHA) */
/* ========================================================= */
.scale-group.radio-scale {
    padding-top: 10px;
    margin-bottom: 0;
}

.scale-radio-options {
    display: flex;
    justify-content: space-between; 
    align-items: center;
    width: 100%;
    margin-top: 10px;
    /* Adiciona um fundo sutil para a área de seleção */
    background-color: #1a1a1a;
    border-radius: 6px;
    padding: 10px 20px;
    border: 1px solid #333;
}

.scale-radio-item {
    display: flex;
    flex-direction: column; 
    align-items: center;
    position: relative;
    padding: 5px 0;
    cursor: pointer;
    transition: all 0.2s; /* Transição para o efeito de destaque */
}

/* NOVO: Efeito de destaque na caixa quando o rádio está selecionado */
.scale-radio-item:has(input[type="radio"]:checked) {
    background: rgba(112, 73, 250, 0.15); /* Fundo roxo suave */
    border-radius: 6px;
    box-shadow: 0 0 10px rgba(112, 73, 250, 0.3); /* Brilho sutil */
    transform: scale(1.05); /* Pequeno zoom no item selecionado */
}

/* Estiliza a bolinha de radio para a escala (maior e roxo no checked) */
.scale-radio-item input[type="radio"] {
    appearance: none;
    width: 25px;
    height: 25px;
    border: 2px solid #7049fa; 
    border-radius: 50%;
    position: relative;
    cursor: pointer;
    background-color: transparent;
    transition: all 0.2s;
    margin: 0; 
    margin-bottom: 8px; 
}

/* Efeito de preenchimento ao selecionar (ponto interno) */
.scale-radio-item input[type="radio"]:checked {
    border-color: #7049fa; /* Borda roxa/azul no círculo */
}

.scale-radio-item input[type="radio"]:checked::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 50%;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background-color: #7049fa; /* Cor de preenchimento (roxo/azul) */
    transform: translate(-50%, -50%);
}

/* Estilo do número (label) */
.scale-radio-item label {
    margin-bottom: 0;
    cursor: pointer;
    font-weight: bold;
    font-size: 1.1em;
    color: var(--color-text-light); 
}

/* Garante que o número dentro do item selecionado fique branco (por causa do fundo) */
.scale-radio-item:has(input[type="radio"]:checked) label {
    color: var(--color-text-light) !important; 
}


/* Remove todos os elementos de slider que não são mais usados */
.scale-labels.bottom-labels,
.scale-markers,
.scale-slider,
.scale-thumb-value,
.scale-info p,
.scale-info div {
    display: none !important;
}
/* FIM DOS ESTILOS DE ESCALA CORRIGIDOS */
/* ========================================================= */

/* CORREÇÃO DO BOTÃO DE PERFIL */
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
  background-color: var(--color-secondary-btn); 
  transform: scale(1.1);
  box-shadow: 0 0 15px var(--color-primary-shadow);
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
  background-color: var(--color-bg-card); 
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
  color: var(--color-text-secondary);
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
  color: var(--color-text-light); 
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
</style>
=======
>>>>>>> e8094ca10ac269b7e8efaf52d063c6276c239cdd
