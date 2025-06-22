package com.polis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSearchParams {

    Double priceMin;
    Double priceMax;

    Integer amountMin;
    Integer amountMax;

    String name;
    String category;
    String orderBy;

    Integer page;
    Integer pageSize;

}
