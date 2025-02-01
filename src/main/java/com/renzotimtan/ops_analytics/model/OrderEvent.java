package com.renzotimtan.ops_analytics.model;

import java.util.Map;

import lombok.Data;

@Data
public class OrderEvent {

    private String id;
    private User user;
    private Map<String, Integer> productQuantities;
    private double totalAmount;
    private String orderTime;

}
