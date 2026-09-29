package com.posto.abastecimento.bomba;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.posto.abastecimento.abastecimento.AbastecimentoRepository;
import com.posto.abastecimento.bomba.dto.CriarBombaRequest;
import com.posto.abastecimento.combustivel.Combustivel;
import com.posto.abastecimento.combustivel.CombustivelService;
import com.posto.abastecimento.exception.RecursoNaoEncontradoException;
import com.posto.abastecimento.exception.RegraNegocioException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BombaServiceTest {

    @Mock
    private BombaRepository bombaRepository;

    @Mock
    private CombustivelService combustivelService;

    @Mock
    private AbastecimentoRepository abastecimentoRepository;

    @InjectMocks
    private BombaService service;

    @Test
    void criaBomba() {
        when(combustivelService.buscarEntidade(1L)).thenReturn(new Combustivel("Etanol", new BigDecimal("3.99")));
        when(bombaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.criar(new CriarBombaRequest("Bomba 01", 1L));

        assertThat(response.nome()).isEqualTo("Bomba 01");
        assertThat(response.combustivelNome()).isEqualTo("Etanol");
    }

    @Test
    void rejeitaCombustivelInexistente() {
        when(combustivelService.buscarEntidade(99L)).thenThrow(new RecursoNaoEncontradoException("Combustivel nao encontrado"));

        assertThatThrownBy(() -> service.criar(new CriarBombaRequest("Bomba 01", 99L)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void rejeitaNomeDuplicado() {
        when(bombaRepository.existsByNomeIgnoreCase("Bomba 01")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new CriarBombaRequest("Bomba 01", 1L)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Ja existe bomba");
    }
    @Test
    void naoExcluiBombaComAbastecimentos() {
        Bomba bomba = new Bomba("Bomba 01", new Combustivel("Gasolina", new BigDecimal("5.79")));
        when(bombaRepository.findById(1L)).thenReturn(Optional.of(bomba));
        when(abastecimentoRepository.existsByBombaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("abastecimentos registrados");
    }
}
