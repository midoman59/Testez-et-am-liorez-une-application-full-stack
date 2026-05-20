package com.openclassrooms.starterjwt.controllers;


import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.services.SessionService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/session")
@Log4j2
public class SessionController {
    private final SessionMapper sessionMapper;
    private final SessionService sessionService;


    public SessionController(SessionService sessionService,
                             SessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
        this.sessionService = sessionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(this.sessionMapper.toDto(this.sessionService.getByIdOrThrow(id)));
    }

    @GetMapping()
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok().body(this.sessionMapper.toDto(this.sessionService.findAll()));
    }

    @PostMapping()
    public ResponseEntity<?> create(@Valid @RequestBody SessionDto sessionDto) {
        log.info(sessionDto);

        return ResponseEntity.ok().body(this.sessionMapper.toDto(this.sessionService.create(this.sessionMapper.toEntity(sessionDto))));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody SessionDto sessionDto) {
        return ResponseEntity.ok().body(this.sessionMapper.toDto(this.sessionService.update(id, this.sessionMapper.toEntity(sessionDto))));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> save(@PathVariable("id") Long id) {
        this.sessionService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("{id}/participate/{userId}")
    public ResponseEntity<?> participate(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        this.sessionService.participate(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}/participate/{userId}")
    public ResponseEntity<?> noLongerParticipate(@PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        this.sessionService.noLongerParticipate(id, userId);
        return ResponseEntity.ok().build();
    }
}
