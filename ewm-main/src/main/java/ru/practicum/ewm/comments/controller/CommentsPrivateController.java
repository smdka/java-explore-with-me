package ru.practicum.ewm.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.service.CommentService;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("users/{userId}/comment")
@RequiredArgsConstructor
public class CommentsPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto post(@PathVariable Long userId,
                           @RequestParam Long eventId,
                           @RequestBody @Valid NewCommentDto commentDto) {
        log.info("POST /users/{}/comment?eventId={}", userId, eventId);
        log.info("Request body: {}", commentDto);
        return commentService.add(userId, eventId, commentDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto patch(@PathVariable Long userId,
                            @PathVariable Long commentId,
                            @RequestBody NewCommentDto commentDto) {
        log.info("PATCH /users/{}/comment/{}", userId, commentId);
        log.info("Request body: {}", commentDto);
        return commentService.update(userId, commentId, commentDto);
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("GET /users/{}/comment/{}", userId, commentId);
        CommentDto response = commentService.get(userId, commentId);

        response.add(linkTo(methodOn(CommentsAdminController.class)
                        .findByUser(userId, null, null))
                        .withRel("findComment"),
                linkTo(methodOn(CommentsAdminController.class)
                        .delete(commentId))
                        .withRel("deleteComment"),
                linkTo(methodOn(CommentsPrivateController.class)
                        .patch(userId, commentId, null))
                        .withRel("updateComment"),
                linkTo(methodOn(CommentsPrivateController.class)
                        .post(userId, null, null))
                        .withRel("createComment"),
                linkTo(methodOn(CommentsPrivateController.class)
                        .getById(userId, commentId))
                        .withSelfRel(),
                linkTo(methodOn(CommentsPublicController.class)
                        .findByEvent(null, null, null))
                        .withRel("getCommentsByEvent"));

        return response;
    }
}
