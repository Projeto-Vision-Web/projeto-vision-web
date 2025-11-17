package com.visionweb.app_vision_web.api.controller;

import com.visionweb.app_vision_web.application.dto.ColaboradorResponseDto;
import com.visionweb.app_vision_web.application.dto.ColetaResponseDto;
import com.visionweb.app_vision_web.domain.contracts.repository.ColaboradorRepository;
import com.visionweb.app_vision_web.domain.core.entities.Colaborador;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/colaborador")
public class ColaboradorController {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorController(ColaboradorRepository colaboradorRepository) {
        this.colaboradorRepository = colaboradorRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColaboradorResponseDto> buscarPorId(@PathVariable Integer id) throws Exception{
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> new Exception("Colaborador não encontrado"));

        ColaboradorResponseDto dto = new ColaboradorResponseDto(
                colaborador.getId(),
                colaborador.getEmpresa().getId_empresa(),
                colaborador.getPerfilGeracional(),
                colaborador.getDepartamento()
        );

        return ResponseEntity.ok(dto);
    }
}
