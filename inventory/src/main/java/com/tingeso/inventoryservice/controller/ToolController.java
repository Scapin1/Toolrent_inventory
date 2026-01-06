package com.tingeso.inventoryservice.controller;

import com.tingeso.inventoryservice.controller.models.CreateToolDTO;
import com.tingeso.inventoryservice.controller.models.ToolAvailableDTO;
import com.tingeso.inventoryservice.entity.ToolEntity;
import com.tingeso.inventoryservice.service.ToolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolController {

    private final ToolService toolService;

    @GetMapping
    public ResponseEntity<List<ToolEntity>> getAllTools() {
        return ResponseEntity.ok(toolService.findAll());
    }

    @GetMapping("/available")
    public ResponseEntity<List<ToolAvailableDTO>> getAvailableTools() {
        return ResponseEntity.ok(toolService.findAllList());
    }

    @PostMapping
    public ResponseEntity<Void> createTool(@RequestBody CreateToolDTO tool, @RequestParam String username) {
        toolService.save(tool, username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/maintenance")
    public ResponseEntity<ToolEntity> sentMaintenance(@PathVariable Long id, @RequestParam String username) {
        return ResponseEntity.ok(toolService.sentMaintenance(id, username));
    }

    @PostMapping("/{id}/write-off")
    public ResponseEntity<ToolEntity> writeOff(@PathVariable Long id, @RequestParam String username) {
        return ResponseEntity.ok(toolService.writeOff(id, username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ToolEntity> getToolById(@PathVariable Long id) {
        ToolEntity tool = toolService.getToolById(id);
        if (tool == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tool);
    }
}
