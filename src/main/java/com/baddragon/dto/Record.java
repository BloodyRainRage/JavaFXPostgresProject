package com.baddragon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Record {

    Integer id;
    Date date;
    String master;
    String carNumber;
    String carModel;
    String carColor;
    Boolean isForeign;
    String serviceName;
    Float serviceCost;


}
