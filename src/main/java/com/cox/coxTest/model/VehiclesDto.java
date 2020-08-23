package com.cox.coxTest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiclesDto {
    private int vehicleId;
    private int year;
    private String make;
    private String model;
    private int dealerId;
}
