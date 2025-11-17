package com.visionweb.app_vision_web.domain.contracts.repository;

import com.visionweb.app_vision_web.domain.core.entities.FeedbackProcessado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackProcessadoRepository extends JpaRepository<FeedbackProcessado, Integer> {

    @Query("""
           select fp
           from FeedbackProcessado fp
           where fp.resposta.coleta.id in :coletaIds
           """)
    List<FeedbackProcessado> findByColetaIds(@Param("coletaIds") List<Integer> coletaIds);

}
