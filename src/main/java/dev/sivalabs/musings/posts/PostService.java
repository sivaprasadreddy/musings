package dev.sivalabs.musings.posts;

import dev.sivalabs.musings.shared.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PostService {
    private static final int PAGE_SIZE = 10;
    private final PostRepository postRepository;

    PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional
    public void createPost(CreatePostRequest request, Long authorId) {
        postRepository.save(
                new PostEntity(request.title().strip(), request.content().strip(), authorId));
    }

    @Transactional(readOnly = true)
    public PagedResult<PostVM> findLatest(int page) {
        return PagedResult.from(postRepository.findLatest(PageRequest.of(page - 1, PAGE_SIZE)));
    }
}
