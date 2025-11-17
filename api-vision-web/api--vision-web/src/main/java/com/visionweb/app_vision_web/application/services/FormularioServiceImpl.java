package com.visionweb.app_vision_web.application.services;

import com.visionweb.app_vision_web.domain.contracts.repository.FormularioRepository;
import com.visionweb.app_vision_web.domain.contracts.repository.PerguntaOpcaoRepository;
import com.visionweb.app_vision_web.domain.contracts.repository.PerguntaRepository;
import com.visionweb.app_vision_web.domain.contracts.service.FormularioService;
import com.visionweb.app_vision_web.domain.core.entities.Formulario;
import com.visionweb.app_vision_web.domain.core.entities.Pergunta;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormularioServiceImpl implements FormularioService {

    private final FormularioRepository formularioRepository;
    private final PerguntaRepository perguntaRepository;
    private final PerguntaOpcaoRepository perguntaOpcaoRepository;

    public FormularioServiceImpl(FormularioRepository formularioRepository, PerguntaRepository perguntaRepository, PerguntaOpcaoRepository perguntaOpcaoRepository) {
        this.formularioRepository = formularioRepository;
        this.perguntaRepository = perguntaRepository;
        this.perguntaOpcaoRepository = perguntaOpcaoRepository;
    }


    @Override
    public List<Formulario> listarTodos() {
        return formularioRepository.findAll();
    }

    @Override
    public Formulario buscarPorId(Integer id) {
        return formularioRepository.findById(id).orElse(null);
    }

    @Override
    public Formulario buscarPorTitulo(String titulo) {
       return formularioRepository.findByTitulo(titulo);
    }

    @Override
    public Formulario salvar(Formulario form) {
        return formularioRepository.save(form);
    }

    @Override
    public Formulario atualizar(Integer id, Formulario form) {
        Formulario existente = formularioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulário não encontrado: " + id));

        // Atualiza apenas os campos que podem mudar
        existente.setTitulo(form.getTitulo());
        existente.setDescricao(form.getDescricao());
        existente.setAtivo(form.getAtivo());
        existente.setEmpresa(form.getEmpresa());
        existente.setCriadoPor(form.getCriadoPor());

        // Se você já montou a lista de perguntas no "form"
        // e quiser substituir tudo:
        if (form.getPerguntas() != null) {
            existente.getPerguntas().clear();
            form.getPerguntas().forEach(p -> {
                p.setFormulario(existente); // garante o vínculo
                existente.getPerguntas().add(p);
            });
        }

        return formularioRepository.save(existente);
    }



    @Override
    @Transactional
    public void excluir(Integer idFormulario) {

        // 1. Carrega todas as perguntas do formulário
        List<Pergunta> perguntas = perguntaRepository.findByFormulario_Id(idFormulario);

        for (Pergunta pergunta : perguntas) {

            // 2. Deleta opções da pergunta
            perguntaOpcaoRepository.deleteByPerguntaId(pergunta.getId());
        }

        // 3. Deleta as perguntas
        perguntaRepository.deleteByFormularioId(idFormulario);

        // 4. Deleta o formulário
        formularioRepository.deleteById(idFormulario);
    }
}
