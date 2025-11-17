package com.visionweb.app_vision_web.application.services;

import com.visionweb.app_vision_web.application.dto.ItemRespostaDto;
import com.visionweb.app_vision_web.application.dto.NlpResultado;
import com.visionweb.app_vision_web.application.dto.RespostaColetaRequestDto;
import com.visionweb.app_vision_web.application.dto.RespostaColetaResponseDto;
import com.visionweb.app_vision_web.domain.contracts.repository.*;
import com.visionweb.app_vision_web.domain.contracts.service.ColetaService;
import com.visionweb.app_vision_web.domain.core.entities.*;
import com.visionweb.app_vision_web.domain.core.entities.Enum.Canal;
import com.visionweb.app_vision_web.domain.core.entities.Enum.Sentimento;
import com.visionweb.app_vision_web.domain.core.entities.Enum.StatusColeta;
import com.visionweb.app_vision_web.domain.core.entities.Enum.TipoPergunta;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ColetaServiceImpl  implements ColetaService {

    private final ColetaRepository coletaRepository;
    private final FormularioRepository formularioRepository;
    private final EmpresaRepository empresaRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final PerguntaRepository perguntaRepository;
    private final PerguntaOpcaoRepository perguntaOpcaoRepository;
    private final RespostaRepository respostaRepository;
    private final ColetaParticipanteRepository coletaParticipanteRepository;
    private final NlpService nlpService;
    private final FeedbackProcessadoRepository feedbackProcessadoRepository;

    public ColetaServiceImpl(ColetaRepository coletaRepository,
                             FormularioRepository formularioRepository,
                             EmpresaRepository empresaRepository,
                             ColaboradorRepository colaboradorRepository,
                             PerguntaRepository perguntaRepository,
                             PerguntaOpcaoRepository perguntaOpcaoRepository,
                             RespostaRepository respostaRepository, ColetaParticipanteRepository coletaParticipanteRepository, NlpService nlpService, FeedbackProcessadoRepository feedbackProcessadoRepository) {
        this.coletaRepository = coletaRepository;
        this.formularioRepository = formularioRepository;
        this.empresaRepository = empresaRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.perguntaRepository = perguntaRepository;
        this.perguntaOpcaoRepository = perguntaOpcaoRepository;
        this.respostaRepository = respostaRepository;
        this.coletaParticipanteRepository = coletaParticipanteRepository;
        this.nlpService = nlpService;
        this.feedbackProcessadoRepository = feedbackProcessadoRepository;
    }

    // -------------------------------------------------
    // CONSULTAS BÁSICAS
    // -------------------------------------------------

    public List<Coleta> listarTodas() {
        return coletaRepository.findAll();
    }

    public Coleta buscarPorId(Integer id) {
        return coletaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coleta não encontrada"));
    }

    // -------------------------------------------------
    // CRIAR COLETA (ao publicar formulário)
    // -------------------------------------------------


    @Transactional
    public Coleta criarColetaAoPublicar(Integer idFormulario,
                                        Integer idEmpresa,
                                        String periodoRef,
                                        String canalStr) {

        Formulario formulario = formularioRepository.findById(idFormulario)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado"));

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Canal canal = (canalStr == null || canalStr.isBlank())
                ? Canal.APP
                : Canal.valueOf(canalStr.toUpperCase());

        Coleta coleta = Coleta.builder()
                .formulario(formulario)
                .empresa(empresa)
                .periodoRef(periodoRef)
                .canal(canal)
                .status(StatusColeta.CRIADA)
                .criadoEm(LocalDateTime.now())
                .build();

        return coletaRepository.save(coleta);
    }

    // -------------------------------------------------
    // DISPARAR COLETA
    // -------------------------------------------------

    /**
     * Marca a coleta como DISPARADA (libera para respostas).
     */
    @Transactional
    public Coleta disparar(Integer idColeta){
        Coleta coleta = buscarPorId(idColeta);

        if (coleta.getStatus() == StatusColeta.DISPARADA) {
            throw new RuntimeException("Coleta já está disparada");
        }
        if (coleta.getStatus() == StatusColeta.ENCERRADA) {
            throw new RuntimeException("Coleta já foi encerrada");
        }

        coleta.setStatus(StatusColeta.DISPARADA);
        // se quiser, pode ter um campo dataInicio na entidade:
        // coleta.setDataInicio(LocalDateTime.now());

        return coletaRepository.save(coleta);
    }

    // -------------------------------------------------
    // ENCERRAR COLETA (opcional, mas é legal ter)
    // -------------------------------------------------

    /**
     * Encerra a coleta, bloqueando novas respostas.
     */
    @Transactional
    public Coleta encerrar(Integer idColeta){
        Coleta coleta = buscarPorId(idColeta);

        if (coleta.getStatus() == StatusColeta.ENCERRADA) {
            throw new RuntimeException("Coleta já está encerrada");
        }

        coleta.setStatus(StatusColeta.ENCERRADA);

        return coletaRepository.save(coleta);
    }

    // -------------------------------------------------
    // REGISTRAR RESPOSTAS DE COLABORADORES
    // -------------------------------------------------

    /**
     * Registra as respostas de um colaborador para uma coleta.
     * Endpoint: POST /api/coletas/{idColeta}/respostas
     */
    @Transactional
    public RespostaColetaResponseDto registrarRespostas(Integer idColeta,
                                                        RespostaColetaRequestDto dto) {

        Coleta coleta = buscarPorId(idColeta);

        if (coleta.getStatus() != StatusColeta.DISPARADA) {
            throw new RuntimeException("Coleta não está aberta para respostas");
        }

        Colaborador colaborador = colaboradorRepository.findById(dto.idColaborador())
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));


        if (coletaParticipanteRepository.existsByColeta_IdAndColaborador_Id(idColeta, colaborador.getId())) {
             throw new RuntimeException("Colaborador já respondeu esta coleta");
        }

        int totalRespondidas = 0;

        for (ItemRespostaDto item : dto.respostas()) {

            Pergunta pergunta = perguntaRepository.findById(item.idPergunta())
                    .orElseThrow(() -> new RuntimeException("Pergunta não encontrada: " + item.idPergunta()));

            // valida o valor conforme o tipo da pergunta
            validarResposta(pergunta, item.valor());

            Resposta resposta = Resposta.builder()
                    .pergunta(pergunta)
                    .colaborador(colaborador)
                    .coleta(coleta)
                    .valorResposta(item.valor())
                    .dataResposta(LocalDateTime.now())
                    .build();

            respostaRepository.save(resposta);

            NlpResultado analise;

            if (pergunta.getTipo() == TipoPergunta.TEXTO) {
                analise = nlpService.analisarTexto(resposta.getValorResposta());
            } else {
                // Heurística para escala / múltipla escolha
                analise = heuristicaAPartirDeValor(pergunta, resposta.getValorResposta());
            }

            salvarFeedback(resposta, analise);
            totalRespondidas++;

        }

        ColetaParticipanteId cpId = new ColetaParticipanteId(
                coleta.getId(),
                colaborador.getId()
        );

        ColetaParticipante cp = ColetaParticipante.builder()
                .id(cpId)
                .coleta(coleta)
                .colaborador(colaborador)
                .enviadoEm(LocalDateTime.now())
                .respondidoEm(LocalDateTime.now())
                .build();

        coletaParticipanteRepository.save(cp);




        return new RespostaColetaResponseDto(
                idColeta,
                dto.idColaborador(),
                totalRespondidas,
                "RESPOSTAS_SALVAS"
        );
    }

    // -------------------------------------------------
    // VALIDAÇÃO POR TIPO DE PERGUNTA
    // -------------------------------------------------

    private void validarResposta(Pergunta pergunta, String valor) {

        TipoPergunta tipo = pergunta.getTipo(); // ajusta se o nome do getter estiver diferente

        switch (tipo) {
            case TEXTO -> {
                if (valor == null || valor.isBlank()) {
                    throw new RuntimeException("Resposta de texto não pode ser vazia");
                }
            }
            case LIKERT -> {
                int v;
                try {
                    v = Integer.parseInt(valor);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Resposta LIKERT deve ser numérica (ex: 1..5)");
                }
                if (v < 1 || v > 5) {
                    throw new RuntimeException("Resposta LIKERT fora do intervalo permitido (1..5)");
                }
            }
            case MULTIPLA -> {
                List<PerguntaOpcao> opcoes = perguntaOpcaoRepository.findByPergunta_Id(pergunta.getId());
                boolean existe = opcoes.stream().anyMatch(o ->
                        o.getTextoOpcao().equalsIgnoreCase(valor) ||
                                (o.getValorNum() != null && String.valueOf(o.getValorNum()).equals(valor))
                );
                if (!existe) {
                    throw new RuntimeException("Opção inválida para a pergunta " + pergunta.getId());
                }
            }
            default -> throw new RuntimeException("Tipo de pergunta não suportado: " + tipo);
        }
    }

    private void salvarFeedback(Resposta resposta, NlpResultado analise) {
        if (analise == null) {
            // fallback neutro só pra não quebrar
            analise = new NlpResultado(Sentimento.NEUTRO, 0.0, "GERAL", 0.0);
        }

        FeedbackProcessado feedback = FeedbackProcessado.builder()
                .resposta(resposta)
                .sentimento(analise.sentimento().name())
                .scoreSentimento(analise.scoreSentimento())
                .cluster(analise.cluster())
                .riscoTurnover(analise.riscoTurnover())
                .build();

        feedbackProcessadoRepository.save(feedback);
    }

    private NlpResultado heuristicaAPartirDeValor(Pergunta pergunta, String valorBruto) {
        if (valorBruto == null || valorBruto.isBlank()) {
            return new NlpResultado(Sentimento.NEUTRO, 0.0, "GERAL", 0.0);
        }

        // 1) Tenta extrair um "score" numérico 1..5
        int nota = extrairNota(pergunta, valorBruto);

        // Garante no range 1..5
        if (nota < 1) nota = 1;
        if (nota > 5) nota = 5;

        // 2) Converte nota 1..5 para score -1..1
        //    1 -> -1.0, 3 -> 0.0, 5 -> +1.0
        double score = (nota - 3) / 2.0;
        if (score < -1.0) score = -1.0;
        if (score > 1.0)  score =  1.0;

        // 3) Define sentimento a partir da nota
        Sentimento sentimento;
        if (nota <= 2) {
            sentimento = Sentimento.NEGATIVO;
        } else if (nota == 3) {
            sentimento = Sentimento.NEUTRO;
        } else {
            sentimento = Sentimento.POSITIVO;
        }

        // 4) Risco de turnover heurístico
        double riscoTurnover;
        if (nota <= 2) {
            riscoTurnover = 0.8;     // bem alto
        } else if (nota == 3) {
            riscoTurnover = 0.4;     // médio
        } else {
            riscoTurnover = 0.1;     // baixo
        }

        // 5) Cluster simples baseado no texto da pergunta
        String enunciado = pergunta.getTextoPergunta(); // ajuste o getter se o nome for outro
        String cluster = gerarClusterHeuristico(enunciado);

        return new NlpResultado(sentimento, score, cluster, riscoTurnover);
    }

    private int extrairNota(Pergunta pergunta, String valorBruto) {
        // Caso resposta já venha como "1", "2", "3", "4", "5"
        try {
            return Integer.parseInt(valorBruto.trim());
        } catch (NumberFormatException ignored) {}

        String v = valorBruto.toLowerCase().trim();

        // Mapeamento comum de múltipla escolha para nota
        if (v.contains("discordo totalmente") || v.contains("muito insatisfeito")) return 1;
        if (v.contains("discordo") || v.contains("insatisfeito"))                  return 2;
        if (v.contains("neutro") || v.contains("indiferente"))                     return 3;
        if (v.contains("concordo") || v.contains("satisfeito"))                    return 4;
        if (v.contains("concordo totalmente") || v.contains("muito satisfeito"))   return 5;

        // Se tiver opção associada com valorNum, você pode usar isso aqui,
        // mas precisa ter acesso às opções da pergunta.
        // Exemplo (se tiver getOpcoes()):
    /*
    if (pergunta.getOpcoes() != null) {
        return pergunta.getOpcoes().stream()
                .filter(o -> v.equalsIgnoreCase(o.getTextoOpcao()))
                .map(o -> Optional.ofNullable(o.getValorNum()).orElse(3))
                .findFirst()
                .orElse(3);
    }
    */

        // Default: neutro
        return 3;
    }

    private String gerarClusterHeuristico(String enunciadoPergunta) {
        if (enunciadoPergunta == null || enunciadoPergunta.isBlank()) {
            return "GERAL";
        }

        String texto = enunciadoPergunta.toLowerCase();

        if (texto.contains("liderança"))      return "LIDERANCA";
        if (texto.contains("comunicação"))    return "COMUNICACAO";
        if (texto.contains("benefício") || texto.contains("salário"))
            return "REMUNERACAO_BENEFICIOS";
        if (texto.contains("ambiente") || texto.contains("clima"))
            return "AMBIENTE_TRABALHO";
        if (texto.contains("crescimento") || texto.contains("carreira"))
            return "CRESCIMENTO_CARREIRA";
        if (texto.contains("reconhecimento"))
            return "RECONHECIMENTO";

        return "GERAL";
    }
}
