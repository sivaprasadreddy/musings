create table post_likes
(
    post_id bigint not null references posts(id) on delete cascade,
    user_id bigint not null references users(id) on delete cascade,
    primary key (post_id, user_id)
);

create index post_likes_user_id_idx on post_likes (user_id);
