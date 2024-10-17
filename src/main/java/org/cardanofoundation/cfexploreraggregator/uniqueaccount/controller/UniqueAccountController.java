package org.cardanofoundation.cfexploreraggregator.uniqueaccount.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.cardanofoundation.cfexploreraggregator.uniqueaccount.model.domain.UniqueAccountRecord;
import org.cardanofoundation.cfexploreraggregator.uniqueaccount.service.UniqueAccountService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Unique Account Controller", description = "APIs for Unique Account aggregation")
public class UniqueAccountController {

    private final UniqueAccountService uniqueAccountService;

    @GetMapping("/uniqueaccount/latest")
    @Operation(summary = "Latest Unique Account",
            description = "Get the latest unique account")
    @ApiResponse(responseCode = "200", description = "Latest unique account found",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UniqueAccountRecord.class))})
    public ResponseEntity<UniqueAccountRecord> getLatestUniqueAccount() {
        return ResponseEntity.ok(uniqueAccountService.getLatestUniqueAccount());
    }

    @GetMapping("/uniqueaccount/{epoch}")
    @Operation(summary = "Unique Account Epoch",
            description = "Get the unique account for a specific epoch")
    @ApiResponse(responseCode = "200", description = "Unique account found",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = UniqueAccountRecord.class))})
    @ApiResponse(responseCode = "404", description = "Unique account not found",
            content = {@Content(schema = @Schema())})
    public ResponseEntity<UniqueAccountRecord> getUniqueAccount(@PathVariable Integer epoch) {
        return uniqueAccountService.getUniqueAccount(epoch)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/uniqueaccount")
    @Operation(summary = "All Unique Account",
            description = "Get all unique account")
    @ApiResponse(responseCode = "200", description = "All unique account found",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = UniqueAccountRecord.class)))})
    public ResponseEntity<List<UniqueAccountRecord>> getAllUniqueAccount(Pageable pageable) {
        return ResponseEntity.ok(uniqueAccountService.getAllUniqueAccount(pageable));
    }
}
