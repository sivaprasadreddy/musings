DELETE FROM posts;
DELETE FROM users;

insert into users(id, email, password, name, role, created_at) values
(1, 'sivaprasadreddy.k@gmail.com', '$2a$10$hTBSjTpFUjeClUeYHxJnQ.mckrjPhi5iYrPLX9w.TxcgP9FgBASMK', 'Siva', 'ROLE_ADMIN', TIMESTAMP '2026-01-01 12:00:00');

-- Equal timestamps exercise ID ordering; post 24 is newer than post 25.
insert into posts(id, title, content, author_id, created_at) values
(1, 'Post 1', 'Message 1', 1, TIMESTAMP '2026-01-01 12:00:00'),
(2, 'Post 2', 'Message 2', 1, TIMESTAMP '2026-01-01 12:00:00'),
(3, 'Post 3', 'Message 3', 1, TIMESTAMP '2026-01-01 12:00:00'),
(4, 'Post 4', 'Message 4', 1, TIMESTAMP '2026-01-01 12:00:00'),
(5, 'Post 5', 'Message 5', 1, TIMESTAMP '2026-01-01 12:00:00'),
(6, 'Post 6', 'Message 6', 1, TIMESTAMP '2026-01-01 12:00:00'),
(7, 'Post 7', 'Message 7', 1, TIMESTAMP '2026-01-01 12:00:00'),
(8, 'Post 8', 'Message 8', 1, TIMESTAMP '2026-01-01 12:00:00'),
(9, 'Post 9', 'Message 9', 1, TIMESTAMP '2026-01-01 12:00:00'),
(10, 'Post 10', 'Message 10', 1, TIMESTAMP '2026-01-01 12:00:00'),
(11, 'Post 11', 'Message 11', 1, TIMESTAMP '2026-01-01 12:00:00'),
(12, 'Post 12', 'Message 12', 1, TIMESTAMP '2026-01-01 12:00:00'),
(13, 'Post 13', 'Message 13', 1, TIMESTAMP '2026-01-01 12:00:00'),
(14, 'Post 14', 'Message 14', 1, TIMESTAMP '2026-01-01 12:00:00'),
(15, 'Post 15', 'Message 15', 1, TIMESTAMP '2026-01-01 12:00:00'),
(16, 'Post 16', 'Message 16', 1, TIMESTAMP '2026-01-01 12:00:00'),
(17, 'Post 17', 'Message 17', 1, TIMESTAMP '2026-01-01 12:00:00'),
(18, 'Post 18', 'Message 18', 1, TIMESTAMP '2026-01-01 12:00:00'),
(19, 'Post 19', 'Message 19', 1, TIMESTAMP '2026-01-01 12:00:00'),
(20, 'Post 20', 'Message 20', 1, TIMESTAMP '2026-01-01 12:00:00'),
(21, 'Post 21', 'Message 21', 1, TIMESTAMP '2026-01-01 12:00:00'),
(22, 'Post 22', 'Message 22', 1, TIMESTAMP '2026-01-01 12:00:00'),
(23, 'Post 23', 'Message 23', 1, TIMESTAMP '2026-01-01 12:00:00'),
(24, 'Post 24', 'Message 24', 1, TIMESTAMP '2026-01-03 12:00:00'),
(25, 'Post 25', 'Message 25', 1, TIMESTAMP '2026-01-02 12:00:00');
