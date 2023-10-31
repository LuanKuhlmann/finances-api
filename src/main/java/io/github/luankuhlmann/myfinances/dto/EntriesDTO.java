package io.github.luankuhlmann.myfinances.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EntriesDTO {

    private Long id;
    private String description;
    private Integer month;
    private Integer year;
    private BigDecimal value;
    private Long user;
    private String type;
    private String status;


}
