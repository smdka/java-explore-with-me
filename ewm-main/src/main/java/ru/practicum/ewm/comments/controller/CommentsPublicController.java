package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("comment")
@RequiredArgsConstructor
public class CommentsPublicController {
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public Collection<CommentDto> getByEvent(@PathVariable Long eventId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("POST /comment/{}?from={}&size={}", eventId, from, size);
        return commentService.getAllByEventId(eventId, from, size);
    }
}
