package com.cox.coxTest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class AnswerDto {
    private List<DealersDto> dealers = new ArrayList<DealersDto>();
}
