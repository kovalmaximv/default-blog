package ru.koval.blog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.koval.blog.dto.api.PostResponse;
import ru.koval.blog.model.Post;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface PostMapper {
    PostResponse toResponse(Post post);
}
