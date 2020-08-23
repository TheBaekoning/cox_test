package com.cox.coxTest.controllers;

import com.cox.coxTest.model.DataSet;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping(value = "answer")
public class DealersController {

    @GetMapping(value = "/getDealers", produces = "application/json")
    public String getDealers() throws InterruptedException, TimeoutException, ExecutionException, JsonProcessingException {
        Dealers dealers = new Dealers();
        DataSet id = dealers.getDataSetId();
        return dealers.retrieveDealersList(id.getDatasetId());
    }

    @PostMapping(value = "/postDealers", produces = "application/json")
    public String postAnswer() throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {
        Dealers dealers = new Dealers();
        DataSet id = dealers.getDataSetId();
        String result = dealers.retrieveDealersList(id.getDatasetId());
        return dealers.postDealerList(id.getDatasetId(), result);
    }
}
