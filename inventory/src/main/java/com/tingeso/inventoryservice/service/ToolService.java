package com.tingeso.inventoryservice.service;

import com.tingeso.inventoryservice.controller.models.CreateToolDTO;
import com.tingeso.inventoryservice.controller.models.ToolAvailableDTO;
import com.tingeso.inventoryservice.entity.ToolEntity;
import com.tingeso.inventoryservice.entity.ToolStateType;
import com.tingeso.inventoryservice.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ToolService {

    private final ToolRepository toolRepository;
    private final RestTemplate restTemplate;

    public void save(CreateToolDTO tool, String username) {
        if (toolRepository.getStock(tool.getName()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tool already exists");
        }

        for (int i = 0; i < tool.getQuantity(); i++) {
            ToolEntity toolCopy = ToolEntity.builder()
                    .name(tool.getName())
                    .category(tool.getCategory())
                    .state(ToolStateType.AVAILABLE)
                    .repoFee(tool.getRepoFee())
                    .rentalFee(tool.getRentalFee())
                    .lateFee(tool.getLateFee())
                    .maintenanceFee(tool.getMaintenanceFee())
                    .build();
            toolRepository.save(toolCopy);
        }

        // Call Kardex Service
        // Using generic Map for simplicity, or we can define a DTO.
        // Payload: { type, quantity, userName, toolName, movementDate }
        try {
            Map<String, Object> kardexPayload = Map.of(
                    "type", "INCOME",
                    "quantity", tool.getQuantity(),
                    "userName", username,
                    "toolName", tool.getName(),
                    "movementDate", LocalDateTime.now().toString());
            restTemplate.postForEntity("http://kardex-service/api/kardex/movement", kardexPayload, Void.class);
        } catch (Exception e) {
            // Log error, maybe don't fail transaction? dependent on requirement.
            System.err.println("Error calling Kardex Service: " + e.getMessage());
        }
    }

    public List<ToolEntity> findAll() {
        return toolRepository.findAll();
    }

    public List<ToolAvailableDTO> findAllList() {
        return toolRepository.findAvailableToolsGrouped();
    }

    public ToolEntity sentMaintenance(Long toolId, String username) {
        ToolEntity tool = toolRepository.findById(toolId).orElse(null);
        if (tool == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tool not found");
        }
        tool.setState(ToolStateType.IN_REPAIR);
        ToolEntity saved = toolRepository.save(tool);

        // Call Kardex
        try {
            Map<String, Object> kardexPayload = Map.of(
                    "type", "REPAIR",
                    "quantity", 1,
                    "userName", username,
                    "toolName", tool.getName(),
                    "movementDate", LocalDateTime.now().toString(),
                    "toolId", toolId);
            restTemplate.postForEntity("http://kardex-service/api/kardex/movement", kardexPayload, Void.class);
        } catch (Exception e) {
            System.err.println("Error calling Kardex Service: " + e.getMessage());
        }
        return saved;
    }

    public ToolEntity writeOff(Long toolId, String username) {
        ToolEntity tool = toolRepository.findById(toolId).orElse(null);
        if (tool == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tool not found");
        }

        // Call Loan Service to check/close active loan
        // Need to define endpoint in M2.
        try {
            // Assuming M2 has endpoint: POST /api/loans/tools/{id}/force-close
            restTemplate.postForEntity("http://loan-service/api/loans/tools/" + toolId + "/force-close", null,
                    Void.class);
        } catch (Exception e) {
            System.err.println("Error calling Loan Service (Write Off): " + e.getMessage());
            // Should we block? Probably. But if loan service is down, we can't write off?
            // For now, assume it's critical.
        }

        tool.setState(ToolStateType.WRITTEN_OFF);
        ToolEntity saved = toolRepository.save(tool);

        // Call Kardex
        try {
            Map<String, Object> kardexPayload = Map.of(
                    "type", "WRITE_OFF",
                    "quantity", 1,
                    "userName", username,
                    "toolName", tool.getName(),
                    "movementDate", LocalDateTime.now().toString(),
                    "toolId", toolId);
            restTemplate.postForEntity("http://kardex-service/api/kardex/movement", kardexPayload, Void.class);
        } catch (Exception e) {
            System.err.println("Error calling Kardex Service: " + e.getMessage());
        }

        return saved;
    }

    // Helper for Loans or Fees
    public ToolEntity getToolById(Long id) {
        return toolRepository.findById(id).orElse(null);
    }
}
