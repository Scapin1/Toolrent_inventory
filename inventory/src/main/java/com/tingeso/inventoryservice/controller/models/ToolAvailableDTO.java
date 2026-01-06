package com.tingeso.inventoryservice.controller.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ToolAvailableDTO {
    private String name;
    private String category;
    private Long quantity; // Count of available tools
    private int repoFee;
    private int maintenanceFee;
    private int rentalFee;
    private int lateFee;
}
