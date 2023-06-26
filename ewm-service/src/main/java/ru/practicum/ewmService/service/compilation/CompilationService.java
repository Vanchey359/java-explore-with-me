package ru.practicum.ewmService.service.compilation;

import ru.practicum.ewmService.dto.compilation.CompilationDto;
import ru.practicum.ewmService.dto.compilation.NewCompilationDto;
import ru.practicum.ewmService.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto dto);

    void deleteCompilationById(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest dto);

    CompilationDto getCompilationById(Long compId);

    List<CompilationDto> getAllCompilationsWithFilters(Boolean pinned, Integer from, Integer size);

}
