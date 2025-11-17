package com.visionweb.app_vision_web.api.controller;

import com.visionweb.app_vision_web.application.dto.*;
import com.visionweb.app_vision_web.application.services.PerguntaServiceImpl;
import com.visionweb.app_vision_web.domain.contracts.service.ColetaService;
import com.visionweb.app_vision_web.domain.contracts.service.FormularioService;
import com.visionweb.app_vision_web.domain.core.entities.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/formulario")
public class FormularioController {

    private final FormularioService formularioService;
    private final ColetaService coletaService;
    private final PerguntaServiceImpl perguntaService;


    public FormularioController(FormularioService formularioService, ColetaService coletaService, PerguntaServiceImpl perguntaService) {
        this.formularioService = formularioService;
        this.coletaService = coletaService;
        this.perguntaService = perguntaService;
    }

    @PostMapping
    public ResponseEntity<FormularioResponseDto> criar(@RequestBody @Valid FormularioCreateDto dto) {
        Formulario form = Formulario.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .ativo(dto.ativo())
                .empresa(Empresa.builder().id_empresa(dto.idEmpresa()).build()) // referencia por id
                .criadoPor(dto.idUsuarioCriador() != null ? Usuario.builder().id_usuario(dto.idUsuarioCriador()).build() : null)
                .build();

        form.setDataCriacao(LocalDateTime.now());
        Formulario formSalvo = formularioService.salvar(form);

        List<PerguntaResponseDto> retorno = new ArrayList<>();

        for(var item : dto.perguntas()){
            var perguntaDto = new PerguntaCreateDto(
                    formSalvo.getId(),
                    item.textoPergunta(),
                    item.tipo(),
                    item.opcoes()
                    );

            var pergunta = perguntaService.criar(perguntaDto);

            var perguntaResponseDto = new PerguntaResponseDto(
                    pergunta.getId(),
                    pergunta.getTextoPergunta(),
                    pergunta.getTipo().name(),
                    pergunta.getFormulario().getId(),
                    pergunta.getOpcoes() != null
                            ? pergunta.getOpcoes().stream()
                            .map(o -> new PerguntaOpcaoResponseDto(o.getId(), o.getTextoOpcao(), o.getValorNum()))
                            .toList()
                            : null
            );

            retorno.add(perguntaResponseDto);
        }


        var body = new FormularioResponseDto(
                formSalvo.getId(), formSalvo.getTitulo(), formSalvo.getDescricao(), formSalvo.getAtivo(),
                formSalvo.getEmpresa() != null ? formSalvo.getEmpresa().getId_empresa() : null, retorno
        );
        return ResponseEntity.created(URI.create("/api/formularios/" + formSalvo.getId())).body(body);
    }


    @PutMapping("/{id}")
    public ResponseEntity<FormularioResponseDto> editar(
            @PathVariable Integer id,
            @RequestBody @Valid FormularioCreateDto dto
    ) {
        // 1) Busca o formulário existente
        Formulario formExistente = formularioService.buscarPorId(id);

        // 2) Atualiza os campos básicos
        formExistente.setTitulo(dto.titulo());
        formExistente.setDescricao(dto.descricao());
        formExistente.setAtivo(dto.ativo());

        // Empresa (referência por id)
        if (dto.idEmpresa() != null) {
            formExistente.setEmpresa(
                    Empresa.builder()
                            .id_empresa(dto.idEmpresa())
                            .build()
            );
        } else {
            formExistente.setEmpresa(null);
        }

        // Usuário criador (se vier no DTO; se quiser manter o antigo, é só tirar esse else)
        if (dto.idUsuarioCriador() != null) {
            formExistente.setCriadoPor(
                    Usuario.builder()
                            .id_usuario(dto.idUsuarioCriador())
                            .build()
            );
        }

        // Se tiver campo de atualização, dá pra usar
        // formExistente.setDataAtualizacao(LocalDateTime.now());

        // 3) Salva o formulário atualizado
        Formulario formSalvo = formularioService.atualizar(id, formExistente);

        // 4) Remove as perguntas antigas desse formulário
        // (implementa esse método na service/repository)
        perguntaService.deletarPorFormulario(formSalvo.getId());

        // 5) Recria as perguntas com base no DTO (igual ao POST)
        List<PerguntaResponseDto> retorno = new ArrayList<>();

        for (var item : dto.perguntas()) {
            var perguntaDto = new PerguntaCreateDto(
                    formSalvo.getId(),
                    item.textoPergunta(),
                    item.tipo(),
                    item.opcoes()
            );

            var pergunta = perguntaService.criar(perguntaDto);

            var perguntaResponseDto = new PerguntaResponseDto(
                    pergunta.getId(),
                    pergunta.getTextoPergunta(),
                    pergunta.getTipo().name(),
                    pergunta.getFormulario().getId(),
                    pergunta.getOpcoes() != null
                            ? pergunta.getOpcoes().stream()
                            .map(o -> new PerguntaOpcaoResponseDto(
                                    o.getId(),
                                    o.getTextoOpcao(),
                                    o.getValorNum()
                            ))
                            .toList()
                            : null
            );

            retorno.add(perguntaResponseDto);
        }

        // 6) Monta o mesmo body do POST, mas retorna 200 OK
        var body = new FormularioResponseDto(
                formSalvo.getId(),
                formSalvo.getTitulo(),
                formSalvo.getDescricao(),
                formSalvo.getAtivo(),
                formSalvo.getEmpresa() != null ? formSalvo.getEmpresa().getId_empresa() : null,
                retorno
        );

        return ResponseEntity.ok(body);
    }



    @PostMapping("/{id}/publicar")
    public ResponseEntity<ColetaResponseDto> publicar(@PathVariable Integer id, @RequestBody PublicarColetaDto dto) throws Exception{

        String canal = dto.canal() != null ? dto.canal().name() : "";

        Coleta coleta = coletaService.criarColetaAoPublicar(id, dto.idEmpresa(), dto.periodoRef(), canal);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ColetaResponseDto(coleta.getId(), id, coleta.getEmpresa().getId_empresa(),coleta.getPeriodoRef(), coleta.getCanal(), "CRIADA",  LocalDateTime.now()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormularioResponseDto> getById(@PathVariable Integer id) {
        Formulario form = formularioService.buscarPorId(id);

        var perguntas = perguntaService.listarPorFormulario(id);


        var retorno = new FormularioResponseDto(
                form.getId(), form.getTitulo(), form.getDescricao(), form.getAtivo(),
                form.getEmpresa() != null ? form.getEmpresa().getId_empresa() : null, perguntas
        );

        return ResponseEntity.ok(retorno);
    }

    @GetMapping
    public ResponseEntity<List<FormularioResponseDto>> get() {
        List<Formulario> forms = formularioService.listarTodos();

        List<FormularioResponseDto> retorno = forms.stream()
                .map(f -> {
                    // Busca as perguntas do formulário
                    List<PerguntaResponseDto> perguntas = perguntaService.listarPorFormulario(f.getId());

                    // Mapeia para PerguntaResponseDto

                    // Cria o DTO final do formulário
                    return new FormularioResponseDto(
                            f.getId(),
                            f.getTitulo(),
                            f.getDescricao(),
                            f.getAtivo(),
                            f.getEmpresa() != null ? f.getEmpresa().getId_empresa() : null,
                            perguntas
                    );
                })
                .toList();

        return ResponseEntity.ok(retorno);
    }




}
