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
