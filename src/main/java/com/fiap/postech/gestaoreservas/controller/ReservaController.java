package com.fiap.postech.gestaoreservas.controller;

import com.fiap.postech.gestaoreservas.model.Reserva;
import com.fiap.postech.gestaoreservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/gestaoReservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/{idReserva}")
    public ResponseEntity<?> obterReservas(@PathVariable UUID idReserva){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(reservaService.obterReservas(idReserva));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("")
    public ResponseEntity<?> obterTodasReservas(){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(reservaService.obterTodasReservas());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> cadastrarReserva(@RequestBody Reserva reserva){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(reservaService.cadastrarReserva(reserva));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{idReserva}")
    public ResponseEntity<?> deletarReserva(@PathVariable UUID idReserva){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(reservaService.deletarReserva(idReserva));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{idReserva}")
    public ResponseEntity<?> atualizarReserva(@PathVariable UUID idReserva, @RequestBody Reserva reserva){
        try{
            return ResponseEntity.status(HttpStatus.OK).body(reservaService.atualizarReserva(idReserva, reserva));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
