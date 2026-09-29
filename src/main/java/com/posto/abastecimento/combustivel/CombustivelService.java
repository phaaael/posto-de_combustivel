package com.posto.abastecimento.combustivel;

import com.posto.abastecimento.bomba.BombaRepository;
import com.posto.abastecimento.combustivel.dto.AtualizarCombustivelRequest;
import com.posto.abastecimento.combustivel.dto.CombustivelResponse;
import com.posto.abastecimento.combustivel.dto.CriarCombustivelRequest;
import com.posto.abastecimento.exception.RecursoNaoEncontradoException;
import com.posto.abastecimento.exception.RegraNegocioException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CombustivelService {

    private final CombustivelRepository combustivelRepository;
    private final BombaRepository bombaRepository;

    public CombustivelService(CombustivelRepository combustivelRepository, BombaRepository bombaRepository) {
        this.combustivelRepository = combustivelRepository;
        this.bombaRepository = bombaRepository;
    }

    @Transactional
    public CombustivelResponse criar(CriarCombustivelRequest request) {
        validarNomeDisponivel(request.nome());
        return CombustivelResponse.from(combustivelRepository.save(new Combustivel(request.nome().trim(), request.precoLitro())));
    }

    @Transactional(readOnly = true)
    public Page<CombustivelResponse> listar(Pageable pageable) {
        return combustivelRepository.findAll(pageable).map(CombustivelResponse::from);
    }

    @Transactional(readOnly = true)
    public CombustivelResponse buscar(Long id) {
        return CombustivelResponse.from(buscarEntidade(id));
    }

    @Transactional
    public CombustivelResponse atualizar(Long id, AtualizarCombustivelRequest request) {
        Combustivel combustivel = buscarEntidade(id);
        if (combustivelRepository.existsByNomeIgnoreCaseAndIdNot(request.nome(), id)) {
            throw new RegraNegocioException("Ja existe combustivel com este nome");
        }
        combustivel.atualizar(request.nome().trim(), request.precoLitro());
        return CombustivelResponse.from(combustivel);
    }

    @Transactional
    public void excluir(Long id) {
        Combustivel combustivel = buscarEntidade(id);
        if (bombaRepository.existsByCombustivelId(id)) {
            throw new RegraNegocioException("Nao e possivel excluir combustivel associado a uma bomba");
        }
        combustivelRepository.delete(combustivel);
    }

    @Transactional(readOnly = true)
    public Combustivel buscarEntidade(Long id) {
        return combustivelRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Combustivel nao encontrado"));
    }

    private void validarNomeDisponivel(String nome) {
        if (combustivelRepository.existsByNomeIgnoreCase(nome)) {
            throw new RegraNegocioException("Ja existe combustivel com este nome");
        }
    }
}
