package com.restkeeper.entity;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult<T>{
    private List<T> records;
    private long total;
}

