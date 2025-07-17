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

-- Insert dummy data for users table
INSERT INTO `users` (`name`, `email`, `password`) VALUES
('John Doe', 'john.doe@email.com', 'password123'),
('Jane Smith', 'jane.smith@email.com', 'securepass456'),
('Mike Johnson', 'mike.johnson@email.com', 'mypassword789'),
('Sarah Wilson', 'sarah.wilson@email.com', 'pass123word'),
('David Brown', 'david.brown@email.com', 'brownpass321');

-- Insert dummy data for addresses table
INSERT INTO `addresses` (`city`, `zip`, `street`, `user_id`) VALUES
('New York', '10001', '123 Main Street', 1),
('Los Angeles', '90210', '456 Oak Avenue', 2),
('Chicago', '60601', '789 Pine Road', 3),
('Houston', '77001', '321 Elm Street', 4),
('Phoenix', '85001', '654 Maple Drive', 5);

-- Insert dummy data for profiles table
INSERT INTO `profiles` (`id`, `bio`, `phone_number`, `date_of_birth`, `loyalty_points`) VALUES
(1, 'Software developer passionate about technology and innovation.', '555-0101', '1990-05-15', 150),
(2, 'Marketing professional with 5 years of experience in digital campaigns.', '555-0102', '1988-12-22', 275),
(3, 'Freelance graphic designer and artist.', '555-0103', '1992-03-08', 89),
(4, 'Teacher and education advocate.', '555-0104', '1985-09-18', 320),
(5, 'Small business owner and entrepreneur.', '555-0105', '1987-07-11', 198);

-- Insert dummy data for tags table
INSERT INTO `tags` (`name`) VALUES
('VIP'),
('Student'),
('Senior'),
('Premium'),
('New Customer');

-- Insert dummy data for user_tags table (many-to-many relationship)
INSERT INTO `user_tags` (`user_id`, `tag_id`) VALUES
(1, 1), -- John Doe is VIP
(1, 4), -- John Doe is Premium
(2, 2), -- Jane Smith is Student
(3, 5), -- Mike Johnson is New Customer
(4, 3), -- Sarah Wilson is Senior
(5, 1), -- David Brown is VIP
(5, 4); -- David Brown is Premium

-- Insert dummy data for categories table
INSERT INTO `categories` (`name`) VALUES
('Electronics'),
('Clothing'),
('Books'),
('Home & Garden'),
('Sports & Outdoors');

-- Insert dummy data for products table
INSERT INTO `products` (`name`, `price`, `category_id`) VALUES
('Smartphone Pro Max', 999, 1),
('Wireless Headphones', 199, 1),
('Cotton T-Shirt', 29, 2),
('Denim Jeans', 79, 2),
('Programming Guide', 45, 3),
('Science Fiction Novel', 15, 3),
('Garden Tool Set', 89, 4),
('Decorative Lamp', 125, 4),
('Running Shoes', 120, 5),
('Yoga Mat', 35, 5);
