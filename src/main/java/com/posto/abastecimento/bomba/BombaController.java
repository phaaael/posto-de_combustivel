package com.posto.abastecimento.bomba;

import com.posto.abastecimento.bomba.dto.AtualizarBombaRequest;
import com.posto.abastecimento.bomba.dto.BombaResponse;
import com.posto.abastecimento.bomba.dto.CriarBombaRequest;
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
@RequestMapping("/api/v1/bombas")
public class BombaController {

    private final BombaService service;

    public BombaController(BombaService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<BombaResponse> criar(@Valid @RequestBody CriarBombaRequest request) {
        BombaResponse response = service.criar(request);
        return ResponseEntity.created(URI.create("/api/v1/bombas/" + response.id())).body(response);
    }

    @GetMapping
    Page<BombaResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    BombaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    BombaResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarBombaRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
