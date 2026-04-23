SET @exist_cat := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'categories' AND COLUMN_NAME = 'opening_balance_amount'
);
SET @sql_cat := IF(
    @exist_cat = 0,
    'ALTER TABLE categories ADD COLUMN opening_balance_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00',
    'SELECT 1'
);
PREPARE stmt_cat FROM @sql_cat;
EXECUTE stmt_cat;
DEALLOCATE PREPARE stmt_cat;

SET @exist_user := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'opening_balance_amount'
);
SET @sql_user := IF(
    @exist_user > 0,
    'ALTER TABLE users DROP COLUMN opening_balance_amount',
    'SELECT 1'
);
PREPARE stmt_user FROM @sql_user;
EXECUTE stmt_user;
DEALLOCATE PREPARE stmt_user;
