package com.posto.abastecimento.abastecimento;

import com.posto.abastecimento.abastecimento.dto.AbastecimentoResponse;
import com.posto.abastecimento.abastecimento.dto.AtualizarAbastecimentoRequest;
import com.posto.abastecimento.abastecimento.dto.CriarAbastecimentoRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/abastecimentos")
public class AbastecimentoController {

    private final AbastecimentoService service;

    public AbastecimentoController(AbastecimentoService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<AbastecimentoResponse> criar(@Valid @RequestBody CriarAbastecimentoRequest request) {
        AbastecimentoResponse response = service.criar(request);
        return ResponseEntity.created(URI.create("/api/v1/abastecimentos/" + response.id())).body(response);
    }

    @GetMapping
    Page<AbastecimentoResponse> listar(
            @RequestParam(required = false) Long bombaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @PageableDefault(sort = "data", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(bombaId, dataInicio, dataFim, pageable);
    }

    @GetMapping("/{id}")
    AbastecimentoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    AbastecimentoResponse atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarAbastecimentoRequest request) {
        return service.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
