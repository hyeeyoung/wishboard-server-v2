DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS folders;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS deploy;

create table users
(
    user_id     bigint       not null auto_increment,
    email       varchar(256) not null,
    password    varchar(256) not null,
    profile_img varchar(512),
    nickname    varchar(512),
    fcm_token   varchar(255) unique,
    is_active   tinyint(1) default 1,
    `create_at` datetime default current_timestamp,
    `update_at` datetime default current_timestamp on update current_timestamp,
    push_state  tinyint(1) default 0 not null,
    os_type varchar(45),
    auth_type varchar(45) not null,
    provider_type varchar(45),
    unique key (email),
    primary key (user_id)
);

create table user_token
(
    id     bigint,
    fcm_token   varchar(255),
    `create_at`  datetime default current_timestamp,
    `update_at`  datetime default current_timestamp on update current_timestamp,
    primary key (id)
);

create table folders
(
    folder_id   bigint not null auto_increment,
    user_id     bigint,
    folder_name varchar(512) default "empty" collate utf8mb4_bin not null,
    `create_at` datetime default current_timestamp,
    foreign key (user_id) references users (user_id) on update cascade on delete cascade,
    primary key (folder_id)
);

create table items
(
    item_id      bigint                 not null auto_increment,
    user_id      bigint,
    folder_id    bigint null,
    item_img     varchar(512),
    item_name    varchar(512)           not null,
    item_price   varchar(255) default '0' not null,
    item_img_url varchar(1000),
    item_memo    text,
    `create_at`  datetime     default current_timestamp,
    add_type varchar(45),
    foreign key (user_id) references users (user_id) on update cascade on delete cascade,
    foreign key (folder_id) references folders (folder_id) on update cascade on delete set null,
    primary key (item_id)
);

create table cart
(
    item_count int,
    user_id    bigint,
    item_id    bigint,
    `create_at`  datetime default current_timestamp,
    foreign key (user_id) references users (user_id) on update cascade on delete cascade,
    foreign key (item_id) references items (item_id) on update cascade on delete cascade,
    primary key (user_id, item_id)
);

create table notifications
(
    item_notification_type varchar(20),
    item_notification_date datetime not null,
    read_state             tinyint(1) default 0 not null,
    `create_at`            datetime default current_timestamp,
    user_id                bigint,
    item_id                bigint,
    foreign key (user_id) references users (user_id) on update cascade on delete cascade,
    foreign key (item_id) references items (item_id) on update cascade on delete cascade,
    primary key (user_id, item_id)
);

create table deploy
(
    id                  bigint      not null auto_increment,
    platform            varchar(20) not null,
    min_version         varchar(20) not null,
    recommended_version varchar(20) not null,
    release_date        date,
    `create_at`         datetime    default current_timestamp,
    `update_at`         datetime    default current_timestamp on update current_timestamp,
    primary key (id)
);
