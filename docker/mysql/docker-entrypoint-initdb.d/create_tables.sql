CREATE TABLE
    `users` (
                `id` bigint NOT NULL AUTO_INCREMENT,
                `name` varchar(255) NOT NULL,
                `email` varchar(255) NOT NULL,
                `password` varchar(255) NOT NULL,
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


DELIMITER //

CREATE PROCEDURE FindProductsByPrice(
    IN min_price DECIMAL(10,2),
    IN max_price DECIMAL(10,2)
)
BEGIN
    SELECT
        p.id,
        p.name,
        p.price,
        p.category_id
    FROM products p
    WHERE p.price BETWEEN min_price AND max_price
    ORDER BY p.name ASC;
END //

DELIMITER ;
