package com.tingeso.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tb_tools")
public class ToolEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private ToolStateType state;

    // Flattened FeeEntity fields
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int repoFee;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int rentalFee;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int lateFee;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int maintenanceFee;
}
