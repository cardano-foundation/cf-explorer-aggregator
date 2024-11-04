package org.cardanofoundation.cfexploreraggregator.health.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/__internal__/healthcheck")
public class Healthcheck {

    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("okay");
    }

}
