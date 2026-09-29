package com.posto.abastecimento.abastecimento;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AbastecimentoRepository extends JpaRepository<Abastecimento, Long>, JpaSpecificationExecutor<Abastecimento> {
    boolean existsByBombaId(Long bombaId);
    Page<Abastecimento> findByBombaId(Long bombaId, Pageable pageable);
    Page<Abastecimento> findByDataBetween(LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
    Page<Abastecimento> findByBombaIdAndDataBetween(Long bombaId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);
}
