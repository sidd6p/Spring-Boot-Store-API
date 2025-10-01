CREATE TABLE
    `users` (
                `id` bigint NOT NULL AUTO_INCREMENT,
                `name` varchar(255) NOT NULL,
                `email` varchar(255) NOT NULL,
                `password` varchar(255) NOT NULL,
                `role` varchar(225) NOT NULL DEFAULT 'USER',
                PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;



CREATE TABLE
    `addresses` (
                    `id` bigint NOT NULL AUTO_INCREMENT,
                    `city` varchar(255) NOT NULL,
                    `zip` varchar(255) NOT NULL,
                    `street` varchar(255) NOT NULL,
                    `user_id` bigint NOT NULL,
                    PRIMARY KEY (`id`),
                    KEY `addresses_users_id_fk` (`user_id`),
                    CONSTRAINT `addresses_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE
    `profiles` (
                   `id` bigint NOT NULL,
                   `bio` text,
                   `phone_number` varchar(15) DEFAULT NULL,
                   `date_of_birth` date DEFAULT NULL,
                   `loyalty_points` int unsigned DEFAULT '0',
                   PRIMARY KEY (`id`),
                   CONSTRAINT `profiles_relation_1` FOREIGN KEY (`id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE
    `tags` (
               `id` int unsigned NOT NULL AUTO_INCREMENT,
               `name` varchar(255) DEFAULT NULL,
               PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE
    `user_tags` (
                    `user_id` bigint NOT NULL,
                    `tag_id` int unsigned NOT NULL,
                    PRIMARY KEY (`user_id`, `tag_id`),
                    KEY `user_tags_relation_2` (`tag_id`),
                    CONSTRAINT `user_tags_relation_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                    CONSTRAINT `user_tags_relation_2` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE
    `categories` (
                     `id` int unsigned NOT NULL AUTO_INCREMENT,
                     `name` varchar(255) NOT NULL,
                     PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

CREATE TABLE
    `products` (
                   `id` int unsigned NOT NULL AUTO_INCREMENT,
                   `name` varchar(255) NOT NULL,
                   `price` decimal(10, 0) NOT NULL,
                   `category_id` int unsigned DEFAULT NULL,
                   PRIMARY KEY (`id`),
                   KEY `products_relation_1` (`category_id`),
                   CONSTRAINT `products_relation_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;



CREATE TABLE
  `carts` (
    `id` binary(16) NOT NULL DEFAULT(uuid_to_bin(uuid())),
    `date_created` date NOT NULL DEFAULT(curdate()),
    PRIMARY KEY (`id`)
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;


CREATE TABLE
  `cart_items` (
    `id` int unsigned NOT NULL AUTO_INCREMENT,
    `cart_id` binary(16) NOT NULL,
    `product_id` int unsigned NOT NULL,
    `quantity` int NOT NULL DEFAULT '1',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_cart_product` (`cart_id`, `product_id`),
    KEY `cart_items_relation_1` (`cart_id`),
    KEY `cart_items_relation_2` (`product_id`),
    CONSTRAINT `cart_items_relation_1` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`),
    CONSTRAINT `cart_items_relation_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
  ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;