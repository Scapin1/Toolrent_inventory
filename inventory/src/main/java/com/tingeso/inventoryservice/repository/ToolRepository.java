package com.tingeso.inventoryservice.repository;

import com.tingeso.inventoryservice.controller.models.ToolAvailableDTO;
import com.tingeso.inventoryservice.entity.ToolEntity;
import com.tingeso.inventoryservice.entity.ToolStateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, Long> {
    ToolEntity findByName(String name);

    @Query("SELECT COUNT(t) FROM ToolEntity t WHERE t.name = :toolName AND t.state = com.tingeso.inventoryservice.entity.ToolStateType.AVAILABLE")
    int getStock(String toolName);

    ToolEntity findTopByNameAndState(String name, ToolStateType state);

    @Query("SELECT new com.tingeso.inventoryservice.controller.models.ToolAvailableDTO(t.name, t.category, COUNT(t), t.repoFee, t.maintenanceFee, t.rentalFee, t.lateFee) FROM ToolEntity t WHERE t.state = com.tingeso.inventoryservice.entity.ToolStateType.AVAILABLE GROUP BY t.name, t.category, t.repoFee, t.maintenanceFee, t.rentalFee, t.lateFee")
    List<ToolAvailableDTO> findAvailableToolsGrouped();
}
