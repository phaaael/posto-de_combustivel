package com.posto.abastecimento.combustivel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.posto.abastecimento.bomba.BombaRepository;
import com.posto.abastecimento.combustivel.dto.AtualizarCombustivelRequest;
import com.posto.abastecimento.combustivel.dto.CriarCombustivelRequest;
import com.posto.abastecimento.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CombustivelServiceTest {

    @Mock
    private CombustivelRepository combustivelRepository;

    @Mock
    private BombaRepository bombaRepository;

    @InjectMocks
    private CombustivelService service;

    @Test
    void criaCombustivel() {
        when(combustivelRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new CriarCombustivelRequest("Gasolina", new BigDecimal("5.79")));

        assertThat(response.nome()).isEqualTo("Gasolina");
        assertThat(response.precoLitro()).isEqualByComparingTo("5.79");
    }

    @Test
    void rejeitaCombustivelDuplicado() {
        when(combustivelRepository.existsByNomeIgnoreCase("Gasolina")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new CriarCombustivelRequest("Gasolina", new BigDecimal("5.79"))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Ja existe combustivel");
    }

    @Test
    void alteraPreco() {
        Combustivel combustivel = new Combustivel("Gasolina", new BigDecimal("5.79"));
        when(combustivelRepository.findById(1L)).thenReturn(Optional.of(combustivel));

        var response = service.atualizar(1L, new AtualizarCombustivelRequest("Gasolina", new BigDecimal("6.09")));

        assertThat(response.precoLitro()).isEqualByComparingTo("6.09");
    }

    @Test
    void naoExcluiCombustivelAssociadoABomba() {
        when(combustivelRepository.findById(1L)).thenReturn(Optional.of(new Combustivel("Gasolina", new BigDecimal("5.79"))));
        when(bombaRepository.existsByCombustivelId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("associado");
    }

    @Test
    void excluiCombustivelSemAssociacao() {
        Combustivel combustivel = new Combustivel("Gasolina", new BigDecimal("5.79"));
        when(combustivelRepository.findById(1L)).thenReturn(Optional.of(combustivel));

        service.excluir(1L);

        verify(combustivelRepository).delete(combustivel);
    }
}
