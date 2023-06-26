package ru.practicum.ewmService.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmService.dto.compilation.CompilationDto;
import ru.practicum.ewmService.dto.compilation.NewCompilationDto;
import ru.practicum.ewmService.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.exception.NotFoundException;
import ru.practicum.ewmService.mapper.compilation.CompilationMapper;
import ru.practicum.ewmService.mapper.event.EventMapper;
import ru.practicum.ewmService.model.compilation.Compilation;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.repository.compilation.CompilationRepository;
import ru.practicum.ewmService.repository.event.EventRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventsRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto dto) {
        if (dto.getPinned() == null) {
            dto.setPinned(false);
        }

        List<Event> eventList = new ArrayList<>();
        List<EventShortDto> eventShortDtoList = new ArrayList<>();

        if (dto.getEvents() != null) {
            eventList = eventsRepository.findAllById(dto.getEvents());
            eventShortDtoList = eventList.stream()
                    .map(eventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        Compilation compilation = compilationMapper.toCompilation(dto, eventList);
        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Created new compilation=" + newCompilation);

        return compilationMapper.toCompilationDto(newCompilation, eventShortDtoList);
    }

    @Transactional
    public void deleteCompilationById(Long compilationId) {
        compilationRepository.deleteById(compilationId);
        log.info("Deleted compilation with id=" + compilationId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest dto) {
        List<Event> eventList;
        List<EventShortDto> eventShortDtoList;
        Compilation compilation = findCompilationById(compilationId);

        if (dto.getEvents() != null) {
            eventList = eventsRepository.findAllById(dto.getEvents());
            eventShortDtoList = eventList.stream()
                    .map(eventMapper::toEventShortDto)
                    .collect(Collectors.toList());
            compilation.setEvents(eventList);
        } else {
            eventList = compilation.getEvents();
            eventShortDtoList = eventList.stream()
                    .map(eventMapper::toEventShortDto)
                    .collect(Collectors.toList());
        }

        ofNullable(dto.getPinned()).ifPresent(compilation::setPinned);
        ofNullable(dto.getTitle()).ifPresent(compilation::setTitle);
        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Updated compilation with id=" + compilationId);

        return compilationMapper.toCompilationDto(newCompilation, eventShortDtoList);
    }

    public CompilationDto getCompilationById(Long compilationId) {
        CompilationDto compilationDto = compilationMapper.toCompilationDto(findCompilationById(compilationId));
        log.info("Found compilation with id=" + compilationId);

        return compilationDto;
    }

    public List<CompilationDto> getAllCompilationsWithFilters(Boolean pinned, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page);
        List<CompilationDto> compilationDtoList = compilations.stream()
                .map(compilationMapper::toCompilationDto)
                .collect(Collectors.toList());
        log.info("Found compilations with filters, size=" + compilationDtoList.size());

        return compilationDtoList;
    }

    private Compilation findCompilationById(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compilationId + "  not found"));
    }
}
