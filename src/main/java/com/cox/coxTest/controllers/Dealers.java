package com.cox.coxTest.controllers;

import com.cox.coxTest.model.AnswerDto;
import com.cox.coxTest.model.DataSet;
import com.cox.coxTest.model.DealersDto;
import com.cox.coxTest.model.VehiclesDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class Dealers {
    private AnswerDto answer = new AnswerDto();
    private final List<VehiclesDto> vehiclesDetailList = new ArrayList<>();
    private final List<DealersDto> dealerList = new ArrayList<>();

    public String retrieveDealersList(String id) throws InterruptedException, TimeoutException, ExecutionException, JsonProcessingException {
        return buildAnswer(id);
    }

    public String postDealerList(String id, String result) {
        return coxApi(HttpMethod.POST, "/api/" + id + "/answer").contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(result))
                .retrieve()
                .bodyToMono(String.class).block();
    }

    private String buildAnswer(String id) throws InterruptedException, TimeoutException, ExecutionException, JsonProcessingException {
        Long start = System.currentTimeMillis();
        List<Future<VehiclesDto>> futureList = new ArrayList<>();
        List<Future<DealersDto>> dealerFutureList = new ArrayList<>();
        Set<Integer> dealerIdSet = new HashSet<>();

        String[] vehicleIds = retrieveVehicleIds(id).getVehicleIds();


        for (String ids : vehicleIds) {
            futureList.add(new GetDealerInfo().retrieveVehicleInfo(id, ids));
        }

        for (Future<VehiclesDto> future : futureList) {
            vehiclesDetailList.add(future.get(10, TimeUnit.SECONDS));
        }

        for (VehiclesDto dealerId : vehiclesDetailList) {
            dealerIdSet.add(dealerId.getDealerId());
        }

        Integer[] dealerIdList = new Integer[dealerIdSet.size()];
        dealerIdSet.toArray(dealerIdList);

        for (Integer ids : dealerIdList) {
            dealerFutureList.add(new GetDealerInfo().retrieveDealerInfo(id, ids));
        }

        for (Future<DealersDto> future : dealerFutureList) {
            dealerList.add(future.get(10, TimeUnit.SECONDS));
        }

        for (DealersDto dealers : dealerList){
            for (VehiclesDto vehicles : vehiclesDetailList){
                if (vehicles.getDealerId() == dealers.getDealerId()){
                    dealers.getVehicles().add(vehicles);
                }
            }
        }
        answer.getDealers().addAll(dealerList);
        System.out.println(System.currentTimeMillis() - start);
        return convertAnswerToJson(answer);
    }

    private DataSet retrieveVehicleIds(String id) {
        return coxApi(HttpMethod.GET, "/api/" + id + "/vehicles").retrieve().bodyToMono(DataSet.class).block();
    }

    public DataSet getDataSetId() {
        return coxApi(HttpMethod.GET, "/api/datasetId").retrieve().bodyToMono(DataSet.class).block();
    }

    private WebClient.RequestBodySpec coxApi(HttpMethod httpMethod, String uriString) {
        WebClient client = WebClient.create("http://api.coxauto-interview.com");
        return client
                .method(httpMethod)
                .uri(uriString);
    }

    private String convertAnswerToJson(AnswerDto answerDto) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(answerDto);
    }
}

class GetDealerInfo {
    private ExecutorService executor
            = Executors.newSingleThreadExecutor();
    private VehiclesDto vehiclesDto;

    public Future<VehiclesDto> retrieveVehicleInfo(String id, String vehicleId) {
        return executor.submit(() -> {
            return coxApi(HttpMethod.GET, "/api/" + id + "/vehicles/" + vehicleId)
                    .retrieve()
                    .bodyToMono(VehiclesDto.class)
                    .block();
        });
    }

    public Future<DealersDto> retrieveDealerInfo(String id, Integer dealerId) {
        return executor.submit(() -> {
            return coxApi(HttpMethod.GET, "/api/" + id + "/dealers/" + dealerId)
                    .retrieve()
                    .bodyToMono(DealersDto.class)
                    .block();
        });
    }

    private WebClient.RequestBodySpec coxApi(HttpMethod httpMethod, String uriString) {
        WebClient client = WebClient.create("http://api.coxauto-interview.com");
        return client
                .method(httpMethod)
                .uri(uriString);
    }
}
