package com.fiap.postech.gestaoreservas.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

public class Quarto {


    @Id
    private UUID idQuarto;
    private String tipoQuarto;
    private Integer totalPessoas;
    private Integer totalCamas;
    private List<String> outrosMoveis;
    private LocalDate fimReserva;
    private LocalDate inicioReserva;


    @ManyToOne
    @JoinColumn(name = "idReserva", referencedColumnName = "idReserva")
    private Reserva reserva;


}
