package com.visionweb.app_vision_web.domain.contracts.repository;

import com.visionweb.app_vision_web.domain.core.entities.Pergunta;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerguntaRepository extends JpaRepository<Pergunta, Integer> {

    List<Pergunta> findByFormulario_Id(Integer idFormulario);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pergunta p WHERE p.formulario.id = :idFormulario")
    void deleteByFormularioId(@Param("idFormulario") Integer idFormulario);

}
