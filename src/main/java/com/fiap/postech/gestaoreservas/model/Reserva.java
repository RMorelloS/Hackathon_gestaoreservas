package com.fiap.postech.gestaoreservas.model;

import jakarta.persistence.*;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reserva {

    @Id
    private UUID idReserva;

    private Float valorTotal;

    private LocalDate dataInicio;

    private LocalDate dataFim;

    private List<UUID> listaItensServicos;

    @Min(value=0, message="A reserva deve ter no mínimo 1 pessoa")
    private Integer totalPessoas;

    @Min(value=0, message="A reserva deve ter no mínimo 1 quarto")
    private Integer totalQuartos;

    @JoinColumn(name="cpf", referencedColumnName = "cpf")
    private String cpf;

    private List<UUID> listaQuartos;



}
