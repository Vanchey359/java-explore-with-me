package ru.practicum.ewmService.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewmService.dto.compilation.CompilationDto;
import ru.practicum.ewmService.service.compilation.CompilationService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/compilations")
@RequiredArgsConstructor
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping("/{compilationId}")
    public ResponseEntity<CompilationDto> getCompilationById(@PathVariable("compilationId") Long compilationId) {
        log.info("Get compilation with id = {}", compilationId);
        return ResponseEntity.ok(compilationService.getCompilationById(compilationId));
    }

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getAllCompilationsWithFilters(
            @RequestParam(name = "pinned", required = false) Boolean pinned,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {

        log.info("Get compilations with pined {}, from {}, size {}", pinned, from, size);
        return ResponseEntity.ok(compilationService.getAllCompilationsWithFilters(pinned, from, size));
    }
}