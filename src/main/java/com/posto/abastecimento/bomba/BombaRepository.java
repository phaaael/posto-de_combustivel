package com.posto.abastecimento.bomba;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BombaRepository extends JpaRepository<Bomba, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
    boolean existsByCombustivelId(Long combustivelId);
}
