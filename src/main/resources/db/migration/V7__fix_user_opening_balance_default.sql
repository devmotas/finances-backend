SET @exist_user_opening_balance := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'users'
      AND COLUMN_NAME = 'opening_balance_amount'
);

SET @sql_user_opening_balance := IF(
    @exist_user_opening_balance > 0,
    'ALTER TABLE users DROP COLUMN opening_balance_amount',
    'SELECT 1'
);

PREPARE stmt_user_opening_balance FROM @sql_user_opening_balance;
EXECUTE stmt_user_opening_balance;
DEALLOCATE PREPARE stmt_user_opening_balance;
