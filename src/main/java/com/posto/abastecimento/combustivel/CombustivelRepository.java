package com.posto.abastecimento.combustivel;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CombustivelRepository extends JpaRepository<Combustivel, Long> {
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);
}
