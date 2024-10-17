package org.cardanofoundation.cfexploreraggregator.poolstatus.controller;

import java.util.List;
import java.util.Optional;

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

import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolAggregationRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.model.domain.PoolStatusRecord;
import org.cardanofoundation.cfexploreraggregator.poolstatus.service.PoolStatusService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Pool Status Controller", description = "APIs for Pool Status")
public class PoolStatusController {

    private final PoolStatusService poolStatusService;

    @GetMapping("/poolstatus/{poolId}")
    @Operation(summary = "Pool Status",
            description = "Get the status of a specific pool")
    @ApiResponse(responseCode = "200", description = "Pool status found",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PoolStatusRecord.class))})
    @ApiResponse(responseCode = "404", description = "Pool status not found",
    content = {@Content(schema = @Schema())})
    public ResponseEntity<PoolStatusRecord> getPoolStatus(@PathVariable String poolId) {
        Optional<PoolStatusRecord> poolStatus = poolStatusService.getPoolStatus(poolId);
        return poolStatus.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/poolstatus/latest")
    @Operation(summary = "Latest Pool Aggregation",
            description = "Get the latest pool aggregation")
    @ApiResponse(responseCode = "200", description = "Latest pool aggregation found",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PoolAggregationRecord.class))})
    public ResponseEntity<PoolAggregationRecord> getLatestPoolAggregation() {
        return ResponseEntity.ok(poolStatusService.getLatestPoolAggregation());
    }

    @GetMapping("/poolstatus")
    @Operation(summary = "All Pool Aggregations",
            description = "Get all pool aggregations")
    @ApiResponse(responseCode = "200", description = "All pool aggregations found",
            content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PoolAggregationRecord.class)))})
    public ResponseEntity<List<PoolAggregationRecord>> getAllPoolAggregations(Pageable pageable) {
        return ResponseEntity.ok(poolStatusService.getAllPoolAggregations(pageable));
    }
}
