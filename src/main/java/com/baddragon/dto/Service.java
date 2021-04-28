package com.baddragon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Service {

    private Integer id;
    private String name;
    private Float cost_our;
    private Float cost_foreign;


}
