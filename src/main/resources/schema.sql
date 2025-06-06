CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    first_name_kana VARCHAR(100),
    last_name_kana VARCHAR(100),
    birth_date DATE,
    gender VARCHAR(10),
    contract_number VARCHAR(50) UNIQUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    is_phone_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS devices (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('iPhone', 'Android')),
    price_range VARCHAR(50) NOT NULL CHECK (price_range IN ('entry', 'mid', 'premium')),
    price DECIMAL(10,2) NOT NULL,
    colors TEXT,
    storage_options TEXT,
    in_stock BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(500),
    specifications TEXT,
    gallery_images TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS options (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL CHECK (category IN ('insurance', 'accessory', 'service')),
    description TEXT,
    monthly_fee DECIMAL(10,2) NOT NULL,
    one_time_fee DECIMAL(10,2) DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    effective_start_date TIMESTAMP,
    effective_end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS option_dependencies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_id VARCHAR(255) NOT NULL,
    required_option_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (option_id) REFERENCES options(id),
    FOREIGN KEY (required_option_id) REFERENCES options(id)
);

CREATE TABLE IF NOT EXISTS option_exclusions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_id VARCHAR(255) NOT NULL,
    excluded_option_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (option_id) REFERENCES options(id),
    FOREIGN KEY (excluded_option_id) REFERENCES options(id)
);

CREATE TABLE IF NOT EXISTS addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    postal_code VARCHAR(10),
    prefecture VARCHAR(50),
    city VARCHAR(100),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    building VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    first_name_kana VARCHAR(100),
    last_name_kana VARCHAR(100),
    birth_date DATE,
    gender VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(255),
    contract_number VARCHAR(50) UNIQUE,
    address_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

CREATE TABLE IF NOT EXISTS customer_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(255),
    ip_address VARCHAR(45),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);
