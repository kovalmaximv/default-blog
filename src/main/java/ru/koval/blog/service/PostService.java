package ru.koval.blog.service;

import org.springframework.stereotype.Service;
import ru.koval.blog.dto.api.PostCreateRequest;
import ru.koval.blog.model.Post;
import ru.koval.blog.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }


    public void createPost(PostCreateRequest createRequest) {
        Post post = new Post();
        post.setContent(createRequest.getContent());
        postRepository.save(post);
    }


    public Post get(Long id) {
        return postRepository.findById(id).orElseThrow();
    }
}
