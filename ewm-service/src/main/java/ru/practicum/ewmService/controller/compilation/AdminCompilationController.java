package ru.practicum.ewmService.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.ewmService.dto.compilation.CompilationDto;
import ru.practicum.ewmService.dto.compilation.NewCompilationDto;
import ru.practicum.ewmService.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewmService.service.compilation.CompilationService;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping(path = "/admin/compilations")
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Create compilation {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createCompilation(dto));
    }

    @DeleteMapping("{compilationId}")
    public ResponseEntity<Void> deleteCompilationById(@PathVariable("compilationId") Long compilationId) {
        log.info("Delete compilation with id = {}", compilationId);
        compilationService.deleteCompilationById(compilationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("{compilationId}")
    public ResponseEntity<CompilationDto> updateCompilation(
            @PathVariable("compilationId") Long compilationId,
            @RequestBody @Valid UpdateCompilationRequest dto) {

        log.info("Update compilation with id = {}, dto = {}", compilationId, dto);
        return ResponseEntity.ok(compilationService.updateCompilation(compilationId, dto));
    }
}
