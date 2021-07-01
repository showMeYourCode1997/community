create table notification
(
    id bigint auto_increment,
    notifier bigint,
    receiver bigint,
    outer_id bigint,
    type int,
    gmt_create bigint,
    status int default 0,
    notifier_name varchar(100),
    outer_title varchar(256),
    constraint notification_pk
        primary key (id)
);
