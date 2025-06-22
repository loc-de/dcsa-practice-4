package com.polis;

public record Product(
        Integer id,
        String name,
        String category,
        int amount,
        double price
) {}
