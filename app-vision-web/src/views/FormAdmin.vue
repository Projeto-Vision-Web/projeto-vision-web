<template>
  <div id="app">
    <main class="container">

      <section v-if="currentScreen === 'access'" class="access-page-content">
        
        <div class="header-titles">
          <h1 class="hero-vision-title large-title">VisionWeb</h1>
          <p class="hero-vision-subtitle large-subtitle">Sistema de Pesquisas de Clima Organizacional</p>
        </div>

        <div class="access-system-box">
          <h2>Acesso ao Sistema</h2>
          <p>Selecione seu tipo de acesso</p>
          
          <div class="profile-selector">
            <button 
              :class="['profile-btn', { 'active': profile === 'admin' }]" 
              @click="selectProfile('admin')"
            >
              <img src="../assets/do-utilizador.png" alt="Admin" class="profile-btn-icon">
              Administrador
            </button>
            
            <button 
              :class="['profile-btn', { 'active': profile === 'collaborator' }]" 
              @click="selectProfile('collaborator')"
            >
              <img src="../assets/coordenacao.png" alt="Colaborador" class="profile-btn-icon">
              Colaborador
            </button>
          </div>
        </div>
        
        <transition name="fade-slide" mode="out-in">
          <!-- ======================= DASHBOARD ADMIN ======================= -->
          <section v-if="profile === 'admin'" key="admin" class="dashboard">
            <div class="dashboard-header">
              <div class="header-content-left">
                <h2>Gerenciar Pesquisas</h2>
                <p>Crie e gerencie pesquisas de clima organizacional</p>
              </div>
              
              <div class="dashboard-header-right">
                <button class="action-btn primary" @click="goToReportsPage">
                  <i class="fas fa-chart-pie"></i> Relatórios
                </button>
                <button class="action-btn primary" @click="openCreateSurveyModal">
                  <i class="fas fa-plus"></i> Nova Pesquisa
                </button>
              </div>
            </div>

            <div class="stacked-survey-card">
              <div class="survey-list">
                <p v-if="surveys.length === 0" class="no-data">
                  Nenhuma pesquisa cadastrada ainda. Clique em "Nova Pesquisa" para começar.
                </p>
                
                <div 
                  v-for="survey in surveys" 
                  :key="survey.id" 
                  class="survey-item" 
                  @click.self="openSurveyDetails(survey)"
                >
                  <div class="survey-info" @click.self="openSurveyDetails(survey)">
                    <h3>{{ survey.title }}</h3>
                    <span :class="['status-tag', survey.status.toLowerCase()]">{{ survey.status }}</span>
                    <p class="subtitle">Total de {{ survey.questions.length }} perguntas.</p>
                    <div class="meta">
                      <span><i class="fas fa-calendar-alt"></i> Criada em {{ survey.createdDate }}</span>
                      <span><i class="fas fa-chart-bar"></i> {{ survey.responses }} respostas</span>
                      <span><i class="fas fa-question-circle"></i> {{ survey.questions.length }} perguntas</span>
                    </div>
                  </div>
                  
                  <div class="survey-actions">
                    <!-- Publicar formulário já criado -->
                    <button 
                      class="icon-btn" 
                      title="Publicar" 
                      v-if="survey.status === 'Rascunho'" 
                      @click.stop="publishExistingSurvey(survey)"
                    >
                      <img src="../assets/publicar.png" alt="Publicar">
                    </button>

                     <button 
                      v-if="survey.status === 'Publicada'"
                      class="icon-btn"
                      title="Disparar a coleta" 
                      @click.stop="dispararColeta(survey)"
                    >
                      <img src="../assets/publicar.png" alt="Disparar">
                    </button>
                    
                    <button 
                      v-if="survey.status === 'Rascunho'"
                      class="icon-btn" 
                      title="Editar" 
                      @click.stop="openEditSurveyModal(survey)"
                    >
                      <img src="../assets/editar.png" alt="Editar">
                    </button>
                    
                    <button 
                      v-if="survey.status === 'Rascunho'"
                      class="icon-btn" 
                      title="Deletar" 
                      @click.stop="deleteSurvey(survey.id)"
                    >
                      <img src="../assets/deletar.png" alt="Deletar">
                    </button>

                     <button 
                      v-if="survey.status === 'Disparada'"
                      class="icon-btn"
                      title="Encerrar a coleta" 
                      @click.stop="encerrarColeta(survey)"
                    >
                      <img src="../assets/stop.png" alt="Encerrar">
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- =================== DASHBOARD COLABORADOR ===================== -->
          <section v-else-if="profile === 'collaborator'" key="collaborator" class="dashboard">
            <div class="dashboard-header">
              <div class="header-content-left">
                <h2>Pesquisas Disponíveis</h2>
                <p>Responda às pesquisas disponíveis para colaborar com o clima organizacional.</p>
              </div>
            </div>
            
            <div class="stacked-survey-card">
              <div class="survey-list">
                <p v-if="activeSurveys.length === 0" class="no-data">
                  Não há pesquisas ativas no momento.
                </p>
                
                <div v-for="survey in activeSurveys" :key="survey.id" class="survey-item">
                  <div class="survey-info">
                    <h3>{{ survey.title }}</h3>
                    <span :class="['status-tag', survey.status.toLowerCase()]">{{ survey.status }}</span>
                    <p class="subtitle">Pesquisa organizacional semestral</p>
                    <div class="meta">
                      <span><i class="fas fa-question-circle"></i> {{ survey.questions.length }} perguntas</span>
                    </div>
                  </div>
                  <div class="survey-actions">
                    <button class="action-btn primary small-btn" @click="openResponseModal(survey)">
                      <i class="fas fa-comment-dots"></i> Responder
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </section>
        </transition>

      </section>

      <!-- =================== MODAL CRIAÇÃO/EDIÇÃO ===================== -->
      <transition name="modal">
        <div v-if="isCreatingSurvey" class="modal-overlay">
          <div class="card create-survey-card modal-content">
            <button class="close-modal-btn" @click="closeCreateSurveyModal"><i class="fas fa-times"></i></button>

            <div class="modal-title-group">
              <h2 v-if="editingSurveyId">Editar Pesquisa: {{ newSurvey.title }}</h2>
              <h2 v-else>Criar Pesquisa de Clima</h2>
              <p>Configure sua pesquisa de clima organizacional</p>
            </div>

            <div class="form-group">
              <label for="title">Título da Pesquisa</label>
              <input
                type="text"
                id="title"
                placeholder="Ex: Pesquisa de Clima Organizacional Q1 2024"
                v-model="newSurvey.title"
              >
            </div>

            <div class="form-group questions-section">
              <div class="questions-header-row">
                <label>Perguntas</label>
                <div class="question-type-buttons">
                  <button class="type-btn" @click="addQuestion('text')">
                    <i class="fas fa-keyboard"></i> Texto
                  </button>
                  <button class="type-btn" @click="addQuestion('multiple')">
                    <i class="fas fa-list-ul"></i> Múltipla Escolha
                  </button>
                  <button class="type-btn" @click="addQuestion('scale')">
                    <i class="fas fa-sliders-h"></i> Escala
                  </button>
                </div>
              </div>

              <div v-if="newSurvey.questions.length === 0" class="no-data">
                Adicione suas perguntas acima.
              </div>
              
              <div
                v-for="(question, index) in newSurvey.questions"
                :key="index"
                class="question-item"
              >
                <div class="question-header">
                  <span class="q-type-tag">{{ question.typeDisplay }}</span>
                  <button class="delete-q-btn" @click="removeQuestion(index)">
                    <i class="fas fa-times"></i>
                  </button>
                </div>
                
                <label :for="'q-text-' + index">Pergunta {{ index + 1 }}</label>
                <input 
                  type="text" 
                  :id="'q-text-' + index" 
                  placeholder="Digite o texto da pergunta" 
                  v-model="question.text"
                  required
                >
                
                <div v-if="question.type === 'multiple'" class="options-group">
                  <label>Opções de Resposta</label>
                  <div
                    v-for="(option, oIndex) in question.options"
                    :key="oIndex"
                    class="option-item"
                  >
                    <input
                      type="text"
                      v-model="question.options[oIndex]"
                      :placeholder="'Opção ' + (oIndex + 1)"
                    >
                    <button
                      class="icon-btn remove-option-btn"
                      @click="removeOption(index, oIndex)"
                    >
                      <img src="../assets/deletar.png" alt="Deletar Opção">
                    </button>
                  </div>
                  <button class="add-option-btn" @click="addOption(index)">
                    + Adicionar Opção
                  </button>
                </div>

                <div v-if="question.type === 'scale'" class="scale-info">
                  <p>Escala Padrão: 1 (Discordo Totalmente) a 5 (Concordo Totalmente)</p>
                  <div style="display: flex; justify-content: space-between; margin-top: 10px; color: #a8a8a8;">
                    <span>1</span><span>2</span><span>3</span><span>4</span><span>5</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="action-buttons-footer">
              <button class="action-btn secondary" @click="closeCreateSurveyModal">
                <i class="fas fa-arrow-left"></i> Cancelar
              </button>
              
              <button class="action-btn purple-gradient" @click="saveSurvey('Rascunho')">
                <i class="fas fa-save"></i> 
                Salvar {{ editingSurveyId ? 'como Rascunho' : 'Rascunho' }}
              </button>
              
              <button 
                class="action-btn success" 
                @click="publishSurvey" 
                :disabled="!newSurvey.title || newSurvey.questions.length === 0"
              >
                <i class="fas fa-paper-plane"></i> 
                {{ editingSurveyId && newSurvey.status === 'Ativa'
                  ? 'Salvar e Manter Ativa'
                  : 'Publicar Pesquisa' }}
              </button>
            </div>
          </div>
        </div>
      </transition>
      
      <!-- =================== MODAL RESPOSTA COLABORADOR ===================== -->
      <transition name="modal">
        <div v-if="isResponding" class="modal-overlay">
          <div class="card create-survey-card modal-content response-modal">
            <button class="close-modal-btn" @click="closeResponseModal">
              <i class="fas fa-times"></i>
            </button>

            <div class="modal-title-group" v-if="currentSurveyToRespond">
              <h2 v-if="!isPreviewingSurvey">
                Responder: {{ currentSurveyToRespond.title }}
              </h2>
              <h2 v-if="isPreviewingSurvey">
                Visualizar: {{ currentSurveyToRespond.title }}
              </h2>
              
              <p v-if="!isPreviewingSurvey">
                Sua opinião é valiosa para o clima organizacional.
              </p>
              <p v-if="isPreviewingSurvey">
                Visualização da pesquisa como Administrador.
              </p>
            </div>

            <fieldset
              :disabled="isPreviewingSurvey"
              class="survey-form-content"
              v-if="currentSurveyToRespond"
            >
              <div
                v-for="(question, index) in currentSurveyToRespond.questions"
                :key="index"
                class="question-item form-group"
              >
                <label :for="'q-' + (index + 1)">
                  {{ index + 1 }}. {{ question.text }} 
                  <span class="q-type-tag">{{ question.typeDisplay }}</span>
                </label>
                
                <!-- Texto Livre -->
                <textarea 
                  v-if="question.type === 'text'"
                  :id="'q-' + (index + 1)" 
                  rows="3"
                  placeholder="Digite sua resposta aqui..."
                  v-model="collaboratorResponses[question.id]"
                  class="text-input"
                ></textarea>

                <!-- Múltipla Escolha -->
                <div
                  v-else-if="question.type === 'multiple'"
                  class="multiple-options-group"
                >
                  <div
                    v-for="(option, oIndex) in question.options"
                    :key="oIndex"
                    class="option-radio-item"
                  >
                    <input 
                      type="radio" 
                      :id="'q-' + (index + 1) + '-' + oIndex" 
                      :name="'q-' + (index + 1)" 
                      :value="option"
                      v-model="collaboratorResponses[question.id]"
                    >
                    <label :for="'q-' + (index + 1) + '-' + oIndex">
                      {{ option }}
                    </label>
                  </div>
                </div>

                <!-- Escala 1–5 -->
                <div
                  v-else-if="question.type === 'scale'"
                  class="scale-group radio-scale"
                >
                  <div class="scale-radio-options">
                    <div
                      v-for="n in 5"
                      :key="n"
                      class="scale-radio-item"
                    >
                      <input 
                        type="radio" 
                        :id="'q-' + (index + 1) + '-scale-' + n" 
                        :name="'q-' + (index + 1)" 
                        :value="n"
                        v-model.number="collaboratorResponses[question.id]"
                      >
                      <label :for="'q-' + (index + 1) + '-scale-' + n">
                        {{ n }}
                      </label>
                    </div>
                  </div>
                </div>
              </div>
            </fieldset>

            <div v-if="!isPreviewingSurvey" class="action-buttons-footer">
              <button class="action-btn secondary" @click="closeResponseModal">
                <i class="fas fa-arrow-left"></i> Cancelar
              </button>
              <button class="action-btn primary" @click="submitResponses">
                <i class="fas fa-check-circle"></i> Enviar Respostas
              </button>
            </div>

            <div v-if="isPreviewingSurvey" class="action-buttons-footer preview-footer">
              <button class="action-btn purple-gradient" @click="closeResponseModal">
                <i class="fas fa-times"></i> Fechar Visualização
              </button>
            </div>
          </div>
        </div>
      </transition>
      
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

// ======================================================================
// API CONFIG
// ======================================================================



const api = axios.create({
  baseURL:  'http://localhost:8080',
});

console.log('BASE URL API =>', api.defaults.baseURL);
// ======================================================================
// HELPERS
// ======================================================================
const COLETA_STORAGE_KEY = 'visionweb_form_coletas';

function loadColetaMap() {
  try {
    const raw = localStorage.getItem(COLETA_STORAGE_KEY);
    return raw ? JSON.parse(raw) : {};
  } catch {
    return {};
  }
}

function saveColetaMap(map) {
  localStorage.setItem(COLETA_STORAGE_KEY, JSON.stringify(map));
}



function decodeJwtPayload(token) {
  try {
    const [, payloadBase64Url] = token.split('.');
    if (!payloadBase64Url) return null;

    const base64 = payloadBase64Url.replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(
      base64.length + (4 - (base64.length % 4 || 4)) % 4,
      '='
    );
    const json = atob(padded);
    return JSON.parse(json);
  } catch (e) {
    console.error('Erro ao decodificar JWT:', e);
    return null;
  }
}

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

const loading = ref(false);
const errorMessage = ref('');

const usuarioLogado = ref(null); // Colaborador carregado do back

// survey em edição/criação
const newSurvey = ref({
  id: null,
  title: '',
  description: '',
  questions: [],
  status: 'Rascunho',
  responses: 0,
});

// Pesquisas ativas (para colaborador)
const activeSurveys = computed(() => {
  console.log('O valor da pesquisa é: ', surveys.value);
  return surveys.value.filter((s) => s.status === 'Disparada');
});

// ======================================================================
// MAPEAMENTO DTO <-> VIEW MODEL
// ======================================================================

function mapTipoPerguntaBackToFront(tipo) {
  switch (tipo) {
    case 'TEXTO':
      return 'text';
    case 'MULTIPLA':
      return 'multiple';
    case 'LIKERT':
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
      return 'MULTIPLA';
    case 'scale':
      return 'LIKERT';
    default:
      return 'TEXTO';
  }
}

function mapTipoPerguntaDisplay(tipoBackOuFront) {
  const t = tipoBackOuFront?.toString().toUpperCase?.() || tipoBackOuFront;
  if (t === 'TEXTO' || t === 'TEXT') return 'Texto Livre';
  if (t === 'MULTIPLA_ESCOLHA' || t === 'MULTIPLE') return 'Múltipla Escolha';
  if (t === 'ESCALA' || t === 'SCALE') return 'Escala (1-5)';
  return 'Texto Livre';
}

// Mapeia FormularioResponseDto -> survey do front
function mapFormularioToSurvey(dto) {
  const statusFront = dto.status === 1 ? 'Ativa' : 'Rascunho';

  return {
    id: dto.id,
    title: dto.titulo,
    description: dto.descricao,
    status: statusFront,
    idEmpresa: dto.idEmpresa,
    createdDate: '—',
    responses: 0,
    coletaId: null, // quando o back passar a devolver info da coleta, você ajusta aqui
    questions: (dto.pergunta || []).map((p) => ({
      id: p.id,
      text: p.textoPergunta,
      type: mapTipoPerguntaBackToFront(p.tipo),
      typeDisplay: mapTipoPerguntaDisplay(p.tipo),
      options: (p.opcoes || []).map((o) => o.textoOpcao),
    })),
  };
}

// Mapeia survey do front -> FormularioCreateDto
function mapSurveyToFormularioPayload(survey, colaboradorDto, ativoBack) {
  return {
    titulo: survey.title,
    descricao: survey.description || '',
    ativo: ativoBack, // int (0 ou 1) – se não puder criar inativo, sempre mande 1
    idEmpresa: colaboradorDto.idEmpresa,
    idUsuarioCriador: colaboradorDto.idColaborador,
    perguntas: survey.questions.map((q) => ({
      textoPergunta: q.text,
      tipo: mapTipoPerguntaFrontToBack(q.type),
      opcoes:
        q.type === 'multiple' || q.type === 'scale'
          ? q.options
              .filter((o) => o && o.trim() !== '')
              .map((o, idx) => ({
                textoOpcao: o,
                valorNum: idx + 1,
              }))
          : [],
    })),
  };
}

// ======================================================================
// CARREGAR COLABORADOR E FORMULÁRIOS
// ======================================================================

async function carregarUsuarioLogado() {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('Sem token no localStorage.');
      return;
    }

    let idColaborador;
    const payload = decodeJwtPayload(token);
    if (payload && payload.id_usuario) {
      idColaborador = payload.id_usuario;
    } else {
      // fallback: se você tiver salvo userId separadamente
      const storedId = localStorage.getItem('userId');
      if (storedId) {
        idColaborador = Number(storedId);
      }
    }

    if (!idColaborador) {
      console.warn('Não foi possível extrair id do colaborador do token.');
      return;
    }

    const { data } = await api.get(`/colaborador/${idColaborador}`);
    usuarioLogado.value = data;
    console.log('Colaborador logado:', usuarioLogado.value);
  } catch (err) {
    console.error('Erro ao carregar colaborador logado:', err);
  }
}

async function encerrarColeta(survey) {
  if (!survey.coletaId) {
    alert('Não foi possível identificar a coleta desta pesquisa.');
    return;
  }

  // opcional, mas ajuda:
  if (!confirm('Tem certeza que deseja encerrar esta coleta?')) {
    return;
  }

  try {
    loading.value = true;
    errorMessage.value = '';

    // chama o endpoint de encerramento
    const { data } = await api.post(`/coleta/${survey.coletaId}/encerrar`);

    // Atualiza na memória
    // Se a API devolver o status da coleta, usamos ele, senão caímos para "Encerrada"
    survey.status = data?.status ?? 'Encerrada';

    // 🔹 Atualiza no localStorage
    const map = loadColetaMap();
    const persisted = map[survey.id] ?? {};

    map[survey.id] = {
      ...persisted,
      coletaId: data?.id ?? survey.coletaId,
      statusFront: survey.status, // ou 'Encerrada' se quiser fixo
    };

    saveColetaMap(map);

    alert('Coleta encerrada com sucesso!');
  } catch (err) {
    console.error(err);
    alert('Erro ao encerrar coleta.');
  } finally {
    loading.value = false;
  }
}


async function dispararColeta(survey) {
  if (!survey.coletaId) {
    alert('Não foi possível identificar a coleta desta pesquisa.');
    return;
  }

  try {
    loading.value = true;
    errorMessage.value = '';

    await api.post(`/coleta/${survey.coletaId}/disparar`);

    // atualiza na memória
    survey.status = 'Disparada';

    // 🔹 atualiza no localStorage
    const map = loadColetaMap();
    const persisted = map[survey.id] ?? {};
    map[survey.id] = {
      ...persisted,
      coletaId: survey.coletaId,
      statusFront: 'Disparada',
    };
    saveColetaMap(map);

    alert('Coleta disparada com sucesso!');
  } catch (err) {
    console.error(err);
    alert('Erro ao disparar coleta.');
  } finally {
    loading.value = false;
  }
}

async function loadSurveysFromApi() {
  try {
    loading.value = true;
    errorMessage.value = '';

    const { data } = await api.get('/formulario');
    const mapped = (data || []).map(mapFormularioToSurvey);

     const coletaMap = loadColetaMap();

     mapped.forEach((s) => {
      const persisted = coletaMap[s.id];
      if (persisted) {
        s.coletaId = persisted.coletaId ?? s.coletaId;
        s.status = persisted.statusFront ?? s.status;
      }
    });

    surveys.value = mapped;
  } catch (err) {
    console.error('Erro no loadSurveysFromApi', err);
    errorMessage.value = 'Erro ao carregar pesquisas.';
  } finally {
    loading.value = false;
  }
}

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

const handleClickOutside = () => {
  showProfileDropdown.value = false;
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
  carregarUsuarioLogado();
  loadSurveysFromApi();
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});

const goToReportsPage = () => {
  router.push('/relatorio');
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
// CRUD / PUBLICAÇÃO
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
    question.options = ['1', '2', '3', '4', '5'];
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

  if (!usuarioLogado.value) {
    alert('Não foi possível identificar o usuário logado.');
    return;
  }

  // se quiser permitir 0/1, mantenha essa lógica;
  // se NÃO puder criar não ativo, troque por: const ativoBack = 1;
  const ativoBack = statusFront === 'Ativa' ? 1 : 0;

  const surveyToPersist = {
    ...newSurvey.value,
    questions: questionsToSave,
  };

  const payload = mapSurveyToFormularioPayload(
    surveyToPersist,
    usuarioLogado.value,
    ativoBack
  );

  console.log('Payload de criação/edição:', payload);

  try {
    loading.value = true;
    errorMessage.value = '';

    let data;
    if (editingSurveyId.value) {
      ({ data } = await api.put(`/formulario/${editingSurveyId.value}`, payload));
    } else {
      ({ data } = await api.post('/formulario', payload));
    }

    const mapped = mapFormularioToSurvey(data);
    const idx = surveys.value.findIndex((s) => s.id === mapped.id);
    if (idx !== -1) {
      surveys.value[idx] = mapped;
    } else {
      surveys.value.push(mapped);
    }

    alert(`Pesquisa "${mapped.title}" salva com sucesso.`);
    closeCreateSurveyModal();
  } catch (err) {
    console.error(err);
    alert('Erro ao salvar pesquisa.');
  } finally {
    loading.value = false;
  }
}

const publishSurvey = () => saveSurvey('Ativa');

// Publicar formulário já existente (Rascunho -> Ativa)
async function publishExistingSurvey(survey) {
  try {
    if (!survey.idEmpresa && !usuarioLogado.value) {
      alert('Não foi possível identificar a empresa para publicação.');
      return;
    }

    const payload = {
      idEmpresa: survey.idEmpresa ?? usuarioLogado.value.idEmpresa,
      periodoRef: '2025Q4', // TODO: tornar dinâmico
    };

    loading.value = true;
    errorMessage.value = '';

    console.log('Publicando formulário', survey.id, 'com payload:', payload);

    const { data } = await api.post(`/formulario/${survey.id}/publicar`, payload);

    survey.coletaId = data.id;
    survey.coletaStatus = data.status; // "CRIADA"
    survey.status = 'Publicada';
    

    console.log("O status da pesquisa é: ", survey.status)

    const map = loadColetaMap();
    map[survey.id] = {
      coletaId: data.id,
      statusFront: 'Publicada',
    };
    saveColetaMap(map);

    alert('Pesquisa publicada com sucesso!');
  } catch (err) {
    console.error('Erro ao publicar pesquisa:', err);
    alert('Erro ao publicar pesquisa.');
  } finally {
    loading.value = false;
  }
}

async function syncSurveyStatusFromColeta(survey) {
  if (!survey.coletaId) return; // rascunho, sem coleta

  try {
    const { data } = await api.get(`/coleta/${survey.coletaId}`);

    survey.coletaStatus = data.status;

    if (data.status === 'CRIADA') {
      survey.status = 'Publicada';
    } else if (data.status === 'DISPARADA') {
      survey.status = 'Disparada';
    } else {
      survey.status = 'Rascunho'; // ou outro mapping se tiver mais estados
    }
  } catch (e) {
    console.error('Erro ao sincronizar coleta', e);
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

    await api.delete(`/formulario/${surveyId}`);
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

  const coletaId = currentSurveyToRespond.value.coletaId;
  if (!coletaId) {
    alert(
      'Não foi possível identificar a coleta ativa para esta pesquisa (ajuste o back para devolver id da coleta).'
    );
    return;
  }

  const respostasPayload = qts.map((q) => ({
    idPergunta: q.id,
    valor: collaboratorResponses.value[q.id],
  }));

  const payload = {
    idColaborador: usuarioLogado.value?.idColaborador ?? 1,
    respostas: respostasPayload,
  };

  try {
    loading.value = true;
    errorMessage.value = '';

    console.log("O payload para resposta da pergunta: ", JSON.stringify(payload))

    await api.post(`/coleta/${coletaId}/respostas`, payload);

    alert(
      `Obrigado! Sua resposta para "${currentSurveyToRespond.value.title}" foi enviada com sucesso!`
    );

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

     const backendMessage =
      err?.response?.data?.message ||        // se você tiver um handler que monta { message: "..." }
      err?.response?.data ||                 // se o Spring estiver devolvendo a string "Colaborador já respondeu esta coleta"
      err?.message;

    if (
      typeof backendMessage === 'string' &&
      backendMessage.includes('Colaborador já respondeu esta coleta')
    ) {
      alert('Você já respondeu esta pesquisa. Obrigado pela participação!');
      closeResponseModal();
      return;
    }

    alert('Erro ao enviar respostas.');
  } finally {
    loading.value = false;
  }
}
</script>

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
  padding-top: 100px;
}

/* HERO ACCESS SECTION */
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

.profile-btn-icon {
  width: 24px;
  height: 24px;
  filter: invert(1) brightness(0.8); 
  opacity: 0.8;
  transition: all 0.3s;
}

.profile-btn.active .profile-btn-icon,
.profile-btn:hover:not(:disabled) .profile-btn-icon {
  filter: invert(1) brightness(1);
  opacity: 1;
}

.profile-btn.active,
.profile-btn:hover:not(:disabled) {
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

/* DASHBOARD */
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

/* LISTA DE PESQUISAS */
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
  background-color: #a855f7; /* Roxo neon */
  color: #fff;
  box-shadow: 0 0 6px #a855f7aa; /* Brilho leve */
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

.icon-btn:hover {
  border-color: #7049fa; 
  background-color: #1a1a1a; 
  box-shadow: 0 0 15px var(--color-primary-shadow); 
}

.icon-btn:hover img {
  filter: invert(100%) saturate(100%); 
}

/* MODAIS */
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

/* FORMULÁRIO PERGUNTAS */
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

/* RESPOSTA COLABORADOR */
.response-modal {
  max-width: 700px;
}

.survey-form-content {
  padding-bottom: 20px;
  border: none; 
}

.survey-form-content:disabled {
  opacity: 0.7;
  background: none; 
}

.badge-disparada {
  display: inline-block;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
  color: #0ff; /* ciano neon */
  background: rgba(0, 255, 255, 0.12);
  border: 1px solid rgba(0, 255, 255, 0.25);
  text-shadow: 0 0 4px #0ff;
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

.preview-footer {
  display: flex;
  justify-content: center;
  padding-top: 20px;
  border-top: 1px solid #333;
  margin-top: 30px;
}

.question-item label {
  display: block;
  font-weight: bold;
  margin-bottom: 10px;
  color: var(--color-text-light);
  font-size: 1.1em;
}

.badge-publicada {
  display: inline-block;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
  color: white;
  background: linear-gradient(90deg, #6a00ff, #9f51ff);
  box-shadow: 0 0 6px #6a00ff55;
}

/* Textarea */
.text-input {
  width: 100%;
  padding: 10px;
  background-color: #1a1a1a;
  border: 1px solid #444;
  color: var(--color-text-light);
  border-radius: 6px;
  resize: vertical;
}

/* Múltipla escolha */
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

/* Escala 1–5 */
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
  transition: all 0.2s;
}

.scale-radio-item:has(input[type="radio"]:checked) {
  background: rgba(112, 73, 250, 0.15);
  border-radius: 6px;
  box-shadow: 0 0 10px rgba(112, 73, 250, 0.3);
  transform: scale(1.05);
}

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

.scale-radio-item input[type="radio"]:checked::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #7049fa;
  transform: translate(-50%, -50%);
}

.scale-radio-item label {
  margin-bottom: 0;
  cursor: pointer;
  font-weight: bold;
  font-size: 1.1em;
  color: var(--color-text-light); 
}

.scale-radio-item:has(input[type="radio"]:checked) label {
  color: var(--color-text-light) !important; 
}
</style>
