package com.posto.abastecimento.bomba;

import com.posto.abastecimento.abastecimento.AbastecimentoRepository;
import com.posto.abastecimento.bomba.dto.AtualizarBombaRequest;
import com.posto.abastecimento.bomba.dto.BombaResponse;
import com.posto.abastecimento.bomba.dto.CriarBombaRequest;
import com.posto.abastecimento.combustivel.Combustivel;
import com.posto.abastecimento.combustivel.CombustivelService;
import com.posto.abastecimento.exception.RecursoNaoEncontradoException;
import com.posto.abastecimento.exception.RegraNegocioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BombaService {

    private final BombaRepository bombaRepository;
    private final CombustivelService combustivelService;
    private final AbastecimentoRepository abastecimentoRepository;

    public BombaService(BombaRepository bombaRepository, CombustivelService combustivelService,
                        AbastecimentoRepository abastecimentoRepository) {
        this.bombaRepository = bombaRepository;
        this.combustivelService = combustivelService;
        this.abastecimentoRepository = abastecimentoRepository;
    }

    @Transactional
    public BombaResponse criar(CriarBombaRequest request) {
        validarNomeDisponivel(request.nome());
        Combustivel combustivel = combustivelService.buscarEntidade(request.combustivelId());
        return BombaResponse.from(bombaRepository.save(new Bomba(request.nome().trim(), combustivel)));
    }

    @Transactional(readOnly = true)
    public Page<BombaResponse> listar(Pageable pageable) {
        return bombaRepository.findAll(pageable).map(BombaResponse::from);
    }

    @Transactional(readOnly = true)
    public BombaResponse buscar(Long id) {
        return BombaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public BombaResponse atualizar(Long id, AtualizarBombaRequest request) {
        Bomba bomba = buscarEntidade(id);
        if (bombaRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new RegraNegocioException("Ja existe bomba com este nome");
        }
        Combustivel combustivel = combustivelService.buscarEntidade(request.combustivelId());
        bomba.atualizar(request.nome().trim(), combustivel);
        return BombaResponse.from(bomba);
    }

    @Transactional
    public void excluir(Long id) {
        Bomba bomba = buscarEntidade(id);
        if (abastecimentoRepository.existsByBombaId(id)) {
            throw new RegraNegocioException("Nao e possivel excluir bomba com abastecimentos registrados");
        }
        bombaRepository.delete(bomba);
    }

    @Transactional(readOnly = true)
    public Bomba buscarEntidade(Long id) {
        return bombaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Bomba nao encontrada"));
    }

    private void validarNomeDisponivel(String nome) {
        if (bombaRepository.existsByNomeIgnoreCase(nome)) {
            throw new RegraNegocioException("Ja existe bomba com este nome");
        }
    }
}
