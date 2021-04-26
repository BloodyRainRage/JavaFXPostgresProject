package com.baddragon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Car {

    Integer id;
    String carNum;
    String carMark;
    String carColor;
    Boolean isForeign;

}
