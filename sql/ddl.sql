create table user
(
    id            bigint not null auto_increment,
    created_at    datetime(6),
    display_name  varchar(255),
    email         varchar(255),
    profile_image varchar(255),
    updated_at    datetime(6),
    uuid          varchar(255),
    primary key (id)
) engine=InnoDB

create table feed
(
    id         bigint not null auto_increment,
    content    TEXT   not null,
    created_at datetime(6),
    updated_at datetime(6),
    user_id    bigint,
    primary key (id)
) engine=InnoDB


create table image
(
    id         bigint not null auto_increment,
    created_at datetime(6),
    image_path varchar(255),
    image_url  varchar(255),
    updated_at datetime(6),
    feed_id    bigint,
    primary key (id)
) engine=InnoDB