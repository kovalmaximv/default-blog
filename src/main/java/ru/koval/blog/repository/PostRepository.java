package ru.koval.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.koval.blog.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}
