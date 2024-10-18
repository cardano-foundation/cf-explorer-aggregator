package org.cardanofoundation.cfexploreraggregator.addresstxcount.controller;

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

import org.cardanofoundation.cfexploreraggregator.addresstxcount.model.domain.AddressTxCountRecord;
import org.cardanofoundation.cfexploreraggregator.addresstxcount.service.AddressTxCountService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Address Transaction Count Controller", description = "APIs for Address Transaction Count")
public class AddressTxCountController {

    private final AddressTxCountService addressTxCountService;

    @GetMapping("/addresstxcount/{address}")
    @Operation(summary = "Address Transaction Count",
            description = "Get the transaction count for a specific address")
    @ApiResponse(responseCode = "200", description = "Address transaction count found",
            content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = AddressTxCountRecord.class))})
    @ApiResponse(responseCode = "404", description = "Address transaction count not found",
            content = {@Content(schema = @Schema())})
    public ResponseEntity<AddressTxCountRecord> getAddressTxCount(@PathVariable String address) {
        Optional<AddressTxCountRecord> txCount = addressTxCountService.getTxCountForAddress(address);
        return txCount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/addresstxcount")
    @Operation(summary = "All Address Transaction Count",
            description = "Get all address transaction count")
    @ApiResponse(responseCode = "200", description = "All address transaction count found",
            content = {@Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = AddressTxCountRecord.class)))})
    public ResponseEntity<List<AddressTxCountRecord>> getAllAddressTxCount(Pageable pageable) {
        return ResponseEntity.ok(addressTxCountService.getAllTxCount(pageable));
    }

}
