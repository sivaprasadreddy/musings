package dev.sivalabs.musings.posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query(value = """
                    select new dev.sivalabs.musings.posts.PostVM(p.id, p.title, p.content, u.name, p.createdAt)
                    from PostEntity p join UserEntity u on u.id = p.authorId
                    order by p.createdAt desc, p.id desc
                    """, countQuery = "select count(p) from PostEntity p join UserEntity u on u.id = p.authorId")
    Page<PostVM> findLatest(Pageable pageable);
}
