package com.posto.abastecimento.combustivel;

import com.posto.abastecimento.combustivel.dto.AtualizarCombustivelRequest;
import com.posto.abastecimento.combustivel.dto.CombustivelResponse;
import com.posto.abastecimento.combustivel.dto.CriarCombustivelRequest;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/combustiveis")
public class CombustivelController {

    private final CombustivelService service;

    public CombustivelController(CombustivelService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<CombustivelResponse> criar(@Valid @RequestBody CriarCombustivelRequest request) {
        CombustivelResponse response = service.criar(request);
        return ResponseEntity.created(URI.create("/api/v1/combustiveis/" + response.id())).body(response);
    }

    @GetMapping
    Page<CombustivelResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    CombustivelResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    CombustivelResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarCombustivelRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
