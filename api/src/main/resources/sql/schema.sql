DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS folders;
DROP TABLE IF EXISTS cart;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS deploy;

-- wishboard.deploy definition

CREATE TABLE `deploy` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `create_at` datetime default current_timestamp,
                          `update_at` datetime default current_timestamp on update current_timestamp,
                          `min_version` varchar(20) NOT NULL,
                          `platform` varchar(20) NOT NULL,
                          `recommended_version` varchar(20) NOT NULL,
                          `release_date` varbinary(255) DEFAULT NULL,
                          PRIMARY KEY (`id`)
)


-- wishboard.users definition

CREATE TABLE `users` (
                         `user_id` bigint NOT NULL AUTO_INCREMENT,
                         `create_at` datetime default current_timestamp,
                         `update_at` datetime default current_timestamp on update current_timestamp,
                         `auth_type` varchar(45) NOT NULL,
                         `email` varchar(256)  NOT NULL,
                         `is_active` bit(1) NOT NULL,
                         `nickname` varchar(512)  DEFAULT NULL,
                         `os_type`  varchar(45) NOT NULL,
                         `password` varchar(256)  NOT NULL,
                         `profile_img` varchar(512)  DEFAULT NULL,
                         `profile_img_url` varchar(1000) DEFAULT NULL,
                         `push_state` bit(1) NOT NULL,
                         `provider_type`  varchar(45) DEFAULT NULL,
                         `social_id` varchar(200)  DEFAULT NULL,
                         unique key (`email`),
                         primary key (`user_id`)
)


-- wishboard.folders definition

CREATE TABLE `folders` (
                           `folder_id` bigint NOT NULL AUTO_INCREMENT,
                           `create_at` datetime default current_timestamp,
                           `update_at` datetime default current_timestamp on update current_timestamp,
                           `folder_name` varchar(512)  DEFAULT NULL,
                           `user_id` bigint DEFAULT NULL,
                           foreign key (`user_id`) references `users` (`user_id`) on update cascade on delete cascade,
                           primary key (`folder_id`)
)

-- wishboard.items definition

CREATE TABLE `items` (
                         `item_id` bigint NOT NULL AUTO_INCREMENT,
                         `create_at` datetime default current_timestamp,
                         `update_at` datetime default current_timestamp on update current_timestamp,
                         `add_type`varchar(45) DEFAULT NULL,
                         `item_memo` text ,
                         `item_name` varchar(512) NOT NULL,
                         `item_price` varchar(255) NOT NULL,
                         `item_url` varchar(1024) DEFAULT NULL,
                         `folder_id` bigint DEFAULT NULL,
                         `user_id` bigint NOT NULL,
                         `version` bigint,
                         foreign key (`user_id`) references `users` (`user_id`) on update cascade on delete cascade,
                         foreign key (`folder_id`) references `folders` (`folder_id`) on update cascade on delete set null,
                         primary key (`item_id`)
)


-- wishboard.notifications definition

CREATE TABLE `notifications` (
                                 `create_at` datetime default current_timestamp,
                                 `update_at` datetime default current_timestamp on update current_timestamp,
                                 `item_notification_date` datetime(6) NOT NULL,
                                 `item_notification_type` varchar(45)  NOT NULL,
                                 `read_state` bit(1) NOT NULL,
                                 `user_id` bigint NOT NULL,
                                 `item_id` bigint NOT NULL,
                                 foreign key (`user_id`) references `users` (`user_id`) on update cascade on delete cascade,
                                 foreign key (`item_id`) references `items` (`item_id`) on update cascade on delete cascade,
                                 primary key (`user_id`, `item_id`)
)


-- wishboard.user_token definition

CREATE TABLE `user_token` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `fcm_token` varchar(255) DEFAULT NULL,
                              `user_id` bigint DEFAULT NULL,
                              `device` varchar(255) DEFAULT NULL,
                              `create_at` datetime default current_timestamp,
                              `update_at` datetime default current_timestamp on update current_timestamp,
                              foreign key (`user_id`) references `users` (`user_id`) on update cascade on delete cascade,
                              PRIMARY KEY (`id`)
)


-- wishboard.cart definition

CREATE TABLE `cart` (
                        `create_at` datetime default current_timestamp,
                        `update_at` datetime default current_timestamp on update current_timestamp,
                        `item_count` int DEFAULT NULL,
                        `user_id` bigint NOT NULL,
                        `item_id` bigint NOT NULL,
                        foreign key (`user_id`) references users (`user_id`) on update cascade on delete cascade,
                        foreign key (`item_id`) references items (`item_id`) on update cascade on delete cascade,
                        primary key (`user_id`, `item_id`)
)


-- wishboard.item_image definition

CREATE TABLE `item_image` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `item_img_url` varchar(1000) DEFAULT NULL,
                              `item_img` varchar(522) DEFAULT NULL,
                              `item_id` bigint DEFAULT NULL,
                              `create_at` datetime default current_timestamp,
                              `update_at` datetime default current_timestamp on update current_timestamp,
                              foreign key (`item_id`) references `items` (`item_id`) on update cascade on delete cascade,
                              PRIMARY KEY (`id`)
)



