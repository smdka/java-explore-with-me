package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.controller.CommentsPublicController;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.service.CompilationsService;

import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("compilations")
public class CompilationsPublicController {
    private final CompilationsService service;

    @GetMapping
    public Collection<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /compilations?pinned={}&from={}&size={}", pinned, from, size);

        Collection<CompilationDto> response = service.getAll(pinned, from, size);

        log.info("Response body: {}", response);
        return response;
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);

        CompilationDto response = service.getById(compId);

        response.add(linkTo(methodOn(CompilationsAdminController.class)
                        .create(null))
                        .withRel("createCompilation"),
                linkTo(methodOn(CompilationsAdminController.class)
                        .delete(compId))
                        .withRel("deleteCompilation"),
                linkTo(methodOn(CompilationsAdminController.class)
                        .update(compId, null))
                        .withRel("updateCompilation"),
                linkTo(methodOn(CompilationsPublicController.class)
                        .getAll(null, null, null))
                        .withRel("getAllCompilations"),
                linkTo(methodOn(CommentsPublicController.class)
                        .getByEvent(null, null, null))
                        .withSelfRel());

        log.info("Response body: {}", response);
        return response;
    }
}
