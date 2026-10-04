create sequence user_id_seq start with 100 increment by 50;

create table users
(
    id         bigint    not null default nextval('user_id_seq'),
    email      text      not null,
    password   text      not null,
    name       text      not null,
    role       text      not null,
    created_at timestamp not null default CURRENT_TIMESTAMP,
    updated_at timestamp,
    version    bigint       not null default 0,
    primary key (id),
    constraint user_email_unique unique (email)
);

insert into users(id, email, password, name, role, created_at) values
(1, 'sivaprasadreddy.k@gmail.com', '$2a$10$hTBSjTpFUjeClUeYHxJnQ.mckrjPhi5iYrPLX9w.TxcgP9FgBASMK', 'Siva', 'ROLE_ADMIN', CURRENT_TIMESTAMP)
;
