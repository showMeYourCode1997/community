create table `comment`
(
    id bigint auto_increment,
    type int,
    gmt_creator bigint,
    gmt_modified bigint,
    parent_id bigint,
    commentator bigint,
    like_count bigint default 0,
    content varchar(1024),
    comment_count int default 0,
    constraint comment_pk
        primary key (id)
);
