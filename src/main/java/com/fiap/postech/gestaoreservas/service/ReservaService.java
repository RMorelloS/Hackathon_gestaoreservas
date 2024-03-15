package com.fiap.postech.gestaoreservas.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.postech.gestaoreservas.model.Reserva;
import com.fiap.postech.gestaoreservas.repository.ReservaRepository;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReservaService {
    @Autowired
    private Environment env;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReservaRepository reservaRepository;

    public Reserva obterReservas(UUID idReserva) {
        return reservaRepository.findById(idReserva).get();
    }

    public Reserva cadastrarReserva(Reserva reserva) throws Exception {
        var valorTotalReserva = 0.0f;
        reserva.setIdReserva(UUID.randomUUID());
        if(reserva.getDataInicio().isBefore(LocalDate.now())){
            throw new Exception("Data de início da reserva não pode ser anterior ao dia de hoje");
        }
        if(reserva.getDataFim().isBefore(reserva.getDataInicio())){
            throw new Exception("Data final da reserva não pode ser antes da data de início");
        }
        for(var idQuarto : reserva.getListaQuartos()) {
            Map<String, Object> map = realizaRequisicaoGet(idQuarto.toString(), "quartos.url");
            validaDatas(map, reserva, idQuarto);
            if(map.get("precoDiaria") == null){
                throw new Exception("Preço do quarto inválido");
            }
            atualizarReservaQuarto(reserva, idQuarto);
            valorTotalReserva += (Float.parseFloat(map.get("precoDiaria").toString()) * reserva.getDataInicio().until(reserva.getDataFim()).getDays());
        }
        for(var item: reserva.getListaItensServicos()){
            Map<String, Object> map = realizaRequisicaoGet(item.toString(), "itensservicos.url");
            if(map.get("valorItemServico") == null){
                throw new Exception("Valor do item ou serviço inválido");
            }
            valorTotalReserva += Float.parseFloat(map.get("valorItemServico").toString());
        }
        reserva.setValorTotal(valorTotalReserva);
        Map<String, Object> infoPessoa = realizaRequisicaoGet(reserva.getCpf(), "pessoas.url");

        //emailService.sendMail(infoPessoa.get("email").toString(), reserva.getDataInicio(), reserva.getDataFim());

        return reservaRepository.save(reserva);
    }

    private void atualizarReservaQuarto(Reserva reserva, UUID idQuarto) throws IOException {
        Map<String, Object> paramsReservaQuarto = new HashMap<>();
        paramsReservaQuarto.put("dataInicio", reserva.getDataInicio());
        paramsReservaQuarto.put("dataFim", reserva.getDataFim());
        paramsReservaQuarto.put("idQuarto", idQuarto);
        RequestService.putRequest(env.getProperty("quartos.url") + "/reservarQuarto", paramsReservaQuarto);
    }

    private Map<String, Object> realizaRequisicaoGet(String idObjeto, String envProperty) throws Exception {
        String informacoes;
        try {
            informacoes = RequestService.getRequest(env.getProperty(envProperty) + "/" + idObjeto);
        }catch(Exception e){
            throw new Exception("Erro ao buscar ID " + idObjeto);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(informacoes, Map.class);
    }
    private void validaDatas(Map<String, Object> map, Reserva reserva, UUID idQuarto) throws Exception {
        if(map.containsKey("fimReserva")
                && map.containsKey("inicioReserva")
                && map.get("fimReserva") != null
                && map.get("inicioReserva") != null) {
            String fimReserva = map.get("fimReserva").toString();
            String inicioReserva = map.get("inicioReserva").toString();
            if (fimReserva != null && inicioReserva != null) {
                var dataFimQuarto = LocalDate.parse(fimReserva);
                var dataInicioQuarto = LocalDate.parse(inicioReserva);
                if (!(reserva.getDataInicio().isAfter(dataFimQuarto) || reserva.getDataFim().isBefore(dataInicioQuarto)))
                    throw new Exception("Quarto " + idQuarto.toString() + " já está reservado. Favor selecionar outro");
            }
        }
    }

    public UUID deletarReserva(UUID idReserva) throws IOException {
        var reserva = reservaRepository.findById(idReserva).get();
        reserva.setDataInicio(null);
        reserva.setDataFim(null);
        for(var quarto : reserva.getListaQuartos()) {
            atualizarReservaQuarto(reserva, quarto);
        }
        reservaRepository.deleteById(idReserva);
        return idReserva;
    }

    public Reserva atualizarReserva(UUID idReserva, Reserva reservaAtualizar) throws Exception {
        var reservaOptional = reservaRepository.findById(idReserva);
        float valorAtualizadoReserva = 0.0f;

        if(reservaOptional.isEmpty()){
            throw new Exception("Reserva com id " + idReserva + " não encontrada");
        }
        var reservaBusca = reservaOptional.get();

        if(reservaAtualizar.getDataInicio().isBefore(LocalDate.now())){
            throw new Exception("Data de início da reserva não pode ser anterior ao dia de hoje");
        }
        if(reservaAtualizar.getDataFim().isBefore(reservaAtualizar.getDataInicio())){
            throw new Exception("Data final da reserva não pode ser antes da data de início");
        }
        reservaBusca.setDataInicio(null);
        reservaBusca.setDataFim(null);
        for(var idQuarto : reservaBusca.getListaQuartos()){
            atualizarReservaQuarto(reservaBusca, idQuarto);
        }
        for(var idQuarto : reservaAtualizar.getListaQuartos()) {
            Map<String, Object> map = realizaRequisicaoGet(idQuarto.toString(), "quartos.url");
            validaDatas(map, reservaAtualizar, idQuarto);
            atualizarReservaQuarto(reservaAtualizar, idQuarto);
            valorAtualizadoReserva += (Float.parseFloat(map.get("precoDiaria").toString()) * reservaAtualizar.getDataInicio().until(reservaAtualizar.getDataFim()).getDays());
        }
        for(var item: reservaAtualizar.getListaItensServicos()){
            Map<String, Object> map = realizaRequisicaoGet(item.toString(), "itensservicos.url");
            valorAtualizadoReserva += Float.parseFloat(map.get("valorItemServico").toString());
        }
        reservaBusca.setValorTotal(valorAtualizadoReserva);
        reservaBusca.setCpf(reservaAtualizar.getCpf());
        reservaBusca.setListaQuartos(reservaAtualizar.getListaQuartos());
        reservaBusca.setDataFim(reservaAtualizar.getDataFim());
        reservaBusca.setDataInicio(reservaAtualizar.getDataInicio());
        reservaBusca.setListaItensServicos(reservaAtualizar.getListaItensServicos());
        reservaBusca.setTotalPessoas(reservaAtualizar.getTotalPessoas());
        reservaBusca.setTotalQuartos(reservaAtualizar.getTotalQuartos());

        return reservaRepository.save(reservaBusca);
    }

    public List<Reserva> obterTodasReservas() {
        return reservaRepository.findAll();
    }
}
