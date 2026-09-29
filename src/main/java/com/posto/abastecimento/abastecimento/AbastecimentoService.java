package com.posto.abastecimento.abastecimento;

import com.posto.abastecimento.abastecimento.dto.AbastecimentoResponse;
import com.posto.abastecimento.abastecimento.dto.AtualizarAbastecimentoRequest;
import com.posto.abastecimento.abastecimento.dto.CriarAbastecimentoRequest;
import com.posto.abastecimento.bomba.Bomba;
import com.posto.abastecimento.bomba.BombaService;
import com.posto.abastecimento.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AbastecimentoService {

    private final AbastecimentoRepository abastecimentoRepository;
    private final BombaService bombaService;

    public AbastecimentoService(AbastecimentoRepository abastecimentoRepository, BombaService bombaService) {
        this.abastecimentoRepository = abastecimentoRepository;
        this.bombaService = bombaService;
    }

    @Transactional
    public AbastecimentoResponse criar(CriarAbastecimentoRequest request) {
        Bomba bomba = bombaService.buscarEntidade(request.bombaId());
        BigDecimal precoLitro = bomba.getCombustivel().getPrecoLitro();
        BigDecimal valorTotal = calcularValorTotal(request.litros(), precoLitro);
        Abastecimento abastecimento = new Abastecimento(bomba, request.data(), request.litros(), precoLitro, valorTotal);
        return AbastecimentoResponse.from(abastecimentoRepository.save(abastecimento));
    }

    @Transactional(readOnly = true)
    public Page<AbastecimentoResponse> listar(Long bombaId, LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        LocalDateTime inicio = dataInicio == null ? null : dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim == null ? null : dataFim.plusDays(1).atStartOfDay().minusNanos(1);

        Page<Abastecimento> page;
        if (bombaId != null && inicio != null && fim != null) {
            page = abastecimentoRepository.findByBombaIdAndDataBetween(bombaId, inicio, fim, pageable);
        } else if (bombaId != null) {
            page = abastecimentoRepository.findByBombaId(bombaId, pageable);
        } else if (inicio != null && fim != null) {
            page = abastecimentoRepository.findByDataBetween(inicio, fim, pageable);
        } else {
            page = abastecimentoRepository.findAll(pageable);
        }
        return page.map(AbastecimentoResponse::from);
    }

    @Transactional(readOnly = true)
    public AbastecimentoResponse buscar(Long id) {
        return AbastecimentoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public AbastecimentoResponse atualizar(Long id, AtualizarAbastecimentoRequest request) {
        Abastecimento abastecimento = buscarEntidade(id);
        Bomba bomba = bombaService.buscarEntidade(request.bombaId());
        BigDecimal precoLitro = abastecimento.getPrecoLitro();
        abastecimento.atualizar(bomba, request.data(), request.litros(), precoLitro, calcularValorTotal(request.litros(), precoLitro));
        return AbastecimentoResponse.from(abastecimento);
    }

    @Transactional
    public void excluir(Long id) {
        abastecimentoRepository.delete(buscarEntidade(id));
    }

    private Abastecimento buscarEntidade(Long id) {
        return abastecimentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Abastecimento nao encontrado"));
    }

    private BigDecimal calcularValorTotal(BigDecimal litros, BigDecimal precoLitro) {
        return litros.multiply(precoLitro).setScale(2, RoundingMode.HALF_UP);
    }
}
