package com.visionweb.app_vision_web.domain.contracts.repository;

import com.visionweb.app_vision_web.domain.core.entities.PerguntaOpcao;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerguntaOpcaoRepository extends JpaRepository<PerguntaOpcao,Integer> {

    List<PerguntaOpcao> findByPergunta_Id(Integer id);

    @Modifying
    @Transactional
    @Query("DELETE FROM PerguntaOpcao po WHERE po.pergunta.id = :idPergunta")
    void deleteByPerguntaId(@Param("idPergunta") Integer idPergunta);
}
