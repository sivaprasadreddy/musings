package dev.sivalabs.musings.posts;

import dev.sivalabs.musings.ApplicationProperties;
import dev.sivalabs.musings.shared.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PostService {
    private final PostRepository postRepository;
    private final ApplicationProperties properties;

    PostService(PostRepository postRepository, ApplicationProperties properties) {
        this.postRepository = postRepository;
        this.properties = properties;
    }

    @Transactional
    public void createPost(CreatePostRequest request, Long authorId) {
        PostEntity entity =
                new PostEntity(request.title().strip(), request.content().strip(), authorId);
        postRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public PagedResult<PostVM> findLatest(int page) {
        PageRequest pageable = PageRequest.of(page - 1, properties.feedPageSize());
        return PagedResult.from(postRepository.findLatest(pageable));
    }
}
