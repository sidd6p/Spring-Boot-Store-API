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
