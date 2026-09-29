package com.posto.abastecimento.abastecimento;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.posto.abastecimento.abastecimento.dto.CriarAbastecimentoRequest;
import com.posto.abastecimento.bomba.Bomba;
import com.posto.abastecimento.bomba.BombaService;
import com.posto.abastecimento.combustivel.Combustivel;
import com.posto.abastecimento.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbastecimentoServiceTest {

    @Mock
    private AbastecimentoRepository abastecimentoRepository;

    @Mock
    private BombaService bombaService;

    @InjectMocks
    private AbastecimentoService service;

    @Test
    void criaAbastecimentoCalculandoValorTotal() {
        Bomba bomba = new Bomba("Bomba 01", new Combustivel("Gasolina", new BigDecimal("5.79")));
        when(bombaService.buscarEntidade(1L)).thenReturn(bomba);
        when(abastecimentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new CriarAbastecimentoRequest(1L, LocalDateTime.parse("2026-09-29T10:30:00"), new BigDecimal("35.50")));

        assertThat(response.precoLitro()).isEqualByComparingTo("5.79");
        assertThat(response.valorTotal()).isEqualByComparingTo("205.55");
    }

    @Test
    void arredondaValorTotal() {
        Bomba bomba = new Bomba("Bomba 01", new Combustivel("Gasolina", new BigDecimal("5.789")));
        when(bombaService.buscarEntidade(1L)).thenReturn(bomba);
        when(abastecimentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new CriarAbastecimentoRequest(1L, LocalDateTime.parse("2026-09-29T10:30:00"), new BigDecimal("1.005")));

        assertThat(response.valorTotal()).isEqualByComparingTo("5.82");
    }

    @Test
    void rejeitaBombaInexistente() {
        when(bombaService.buscarEntidade(99L)).thenThrow(new RecursoNaoEncontradoException("Bomba nao encontrada"));

        assertThatThrownBy(() -> service.criar(new CriarAbastecimentoRequest(99L, LocalDateTime.now(), BigDecimal.ONE)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void preservaPrecoHistoricoNoRegistroCriado() {
        Combustivel combustivel = new Combustivel("Gasolina", new BigDecimal("5.79"));
        Bomba bomba = new Bomba("Bomba 01", combustivel);
        when(bombaService.buscarEntidade(1L)).thenReturn(bomba);
        when(abastecimentoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new CriarAbastecimentoRequest(1L, LocalDateTime.now(), BigDecimal.TEN));
        combustivel.atualizar("Gasolina", new BigDecimal("6.50"));

        assertThat(response.precoLitro()).isEqualByComparingTo("5.79");
        assertThat(response.valorTotal()).isEqualByComparingTo("57.90");
    }
}
