package com.posto.abastecimento.abastecimento;

import com.posto.abastecimento.bomba.Bomba;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Abastecimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bomba_id", nullable = false)
    private Bomba bomba;

    @Column(nullable = false)
    private LocalDateTime data;

    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal litros;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoLitro;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    protected Abastecimento() {
    }

    public Abastecimento(Bomba bomba, LocalDateTime data, BigDecimal litros, BigDecimal precoLitro, BigDecimal valorTotal) {
        this.bomba = bomba;
        this.data = data;
        this.litros = litros;
        this.precoLitro = precoLitro;
        this.valorTotal = valorTotal;
    }

    @PrePersist
    void prePersist() {
        criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Bomba getBomba() { return bomba; }
    public LocalDateTime getData() { return data; }
    public BigDecimal getLitros() { return litros; }
    public BigDecimal getPrecoLitro() { return precoLitro; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public void atualizar(Bomba bomba, LocalDateTime data, BigDecimal litros, BigDecimal precoLitro, BigDecimal valorTotal) {
        this.bomba = bomba;
        this.data = data;
        this.litros = litros;
        this.precoLitro = precoLitro;
        this.valorTotal = valorTotal;
    }
}
