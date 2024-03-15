package com.fiap.postech.gestaoreservas.repository;

import com.fiap.postech.gestaoreservas.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

}
