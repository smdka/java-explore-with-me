package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("admin/comment")
@RequiredArgsConstructor
public class CommentsAdminController {
    private final CommentService commentService;

    @GetMapping("/{userId}")
    public Collection<CommentDto> findByUser(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/comment/{}?from={}&size={}", userId, from, size);
        return commentService.getAllByUserId(userId, from, size);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        log.info("DELETE /admin/comment/{}", commentId);
        commentService.delete(commentId);
    }
}
