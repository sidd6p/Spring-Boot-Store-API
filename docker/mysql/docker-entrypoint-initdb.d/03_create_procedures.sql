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
