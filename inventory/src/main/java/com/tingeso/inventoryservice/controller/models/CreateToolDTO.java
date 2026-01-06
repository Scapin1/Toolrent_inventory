package com.tingeso.inventoryservice.controller.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateToolDTO {
    private String name;
    private String category;
    private int quantity;
    private int repoFee;
    private int rentalFee;
    private int lateFee;
    private int maintenanceFee;
}
