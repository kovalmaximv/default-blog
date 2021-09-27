package ru.koval.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.koval.blog.dto.api.PostCreateRequest;
import ru.koval.blog.dto.api.PostResponse;
import ru.koval.blog.mapper.PostMapper;
import ru.koval.blog.service.PostService;

@RestController
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;

    @Autowired
    public PostController(PostService postService, PostMapper postMapper) {
        this.postService = postService;
        this.postMapper = postMapper;
    }

    @GetMapping(path = "/api/v1/posts/{id}")
    public PostResponse get(@PathVariable Long id) {
        return postMapper.toResponse(postService.get(id));
    }


    @PostMapping(path = "/api/v1/posts")
    public void create(@RequestBody PostCreateRequest postCreateRequest) {
        postService.createPost(postCreateRequest);
    }
}
