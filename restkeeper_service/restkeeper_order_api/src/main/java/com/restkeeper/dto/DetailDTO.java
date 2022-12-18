package com.restkeeper.dto;

import lombok.Data;

import java.util.List;

@Data
public class DetailDTO {
    private String detailId;
    private List<String> remarks;
}
