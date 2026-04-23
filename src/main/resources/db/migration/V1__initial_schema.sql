CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    default_recurrence_months INTEGER NOT NULL DEFAULT 12,
    emergency_fund_target_months INTEGER NOT NULL DEFAULT 6,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    flow VARCHAR(32) NOT NULL,
    expense_group VARCHAR(32),
    opening_balance_amount DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_category_per_user UNIQUE (user_id, name)
);

CREATE TABLE recurrences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    description VARCHAR(500),
    amount DECIMAL(15, 2) NOT NULL,
    start_date DATE NOT NULL,
    months INTEGER NOT NULL,
    installment_total INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_recurrences_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_recurrences_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT
);

CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    recurrence_id BIGINT,
    description VARCHAR(500),
    amount DECIMAL(15, 2) NOT NULL,
    date DATE NOT NULL,
    schedule VARCHAR(32) NOT NULL,
    flow VARCHAR(32) NOT NULL,
    recurrence_index INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_transactions_category FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT,
    CONSTRAINT fk_transactions_recurrence FOREIGN KEY (recurrence_id) REFERENCES recurrences (id) ON DELETE SET NULL
);

CREATE INDEX idx_categories_user ON categories (user_id);
CREATE INDEX idx_recurrences_user ON recurrences (user_id);
CREATE INDEX idx_recurrences_category ON recurrences (category_id);
CREATE INDEX idx_transactions_user_date ON transactions (user_id, date);
CREATE INDEX idx_transactions_user_flow_date ON transactions (user_id, flow, date);
CREATE INDEX idx_transactions_recurrence_user_date ON transactions (recurrence_id, user_id, date);
