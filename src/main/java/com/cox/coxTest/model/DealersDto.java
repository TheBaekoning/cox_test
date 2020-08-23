package com.cox.coxTest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DealersDto {
    private int dealerId;
    private String name;
    private List<VehiclesDto> vehicles = new ArrayList<VehiclesDto>();
}
