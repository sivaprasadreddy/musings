create sequence post_id_seq start with 100 increment by 50;

create table posts
(
    id         bigint       not null default nextval('post_id_seq'),
    title      varchar(200) not null,
    content    text         not null,
    author_id  bigint       not null references users(id),
    created_at timestamp    not null default CURRENT_TIMESTAMP,
    updated_at timestamp,
    version    bigint       not null default 0,
    primary key (id)
);

create index posts_created_at_id_idx on posts (created_at desc, id desc);
