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
    constraint NOTIFICATION_PK
        primary key (id)
);
