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

CREATE TABLE IF NOT EXISTS plans (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    monthly_fee DECIMAL(10,2) NOT NULL,
    data_capacity VARCHAR(100),
    voice_calls VARCHAR(100),
    sms VARCHAR(100),
    version VARCHAR(50),
    parent_plan_id VARCHAR(255),
    is_current_version BOOLEAN DEFAULT TRUE,
    effective_start_date TIMESTAMP,
    effective_end_date TIMESTAMP,
    campaign_start_date TIMESTAMP,
    campaign_end_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    change_reason TEXT,
    approval_status VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS plan_features (
    plan_id VARCHAR(255) NOT NULL,
    feature VARCHAR(255) NOT NULL,
    FOREIGN KEY (plan_id) REFERENCES plans(id)
);

CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id VARCHAR(255) NOT NULL,
    color VARCHAR(50) NOT NULL,
    storage VARCHAR(50) NOT NULL,
    total_stock INT NOT NULL DEFAULT 0,
    available_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    allocated_stock INT NOT NULL DEFAULT 0,
    alert_threshold INT NOT NULL DEFAULT 5,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_inventory UNIQUE (device_id, color, storage),
    CONSTRAINT fk_inventory_device FOREIGN KEY (device_id) REFERENCES devices(id)
);

CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_id BIGINT NOT NULL,
    customer_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL CHECK (status IN ('RESERVED', 'ALLOCATED', 'CANCELLED', 'EXPIRED')),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_reservation_inventory FOREIGN KEY (inventory_id) REFERENCES inventory(id),
    CONSTRAINT fk_reservation_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_inventory_device_color ON inventory(device_id, color);
CREATE INDEX idx_inventory_status ON inventory(available_stock, alert_threshold);
CREATE INDEX idx_reservation_expiry ON reservations(expires_at, status);
CREATE INDEX idx_reservation_status ON reservations(status);
CREATE INDEX idx_inventory_updated ON inventory(updated_at);

CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    alert_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    source VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at TIMESTAMP NULL,
    resolved_at TIMESTAMP NULL,
    INDEX idx_alert_type (alert_type),
    INDEX idx_severity (severity),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS backup_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    backup_id VARCHAR(100) NOT NULL UNIQUE,
    backup_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NULL,
    size BIGINT NULL,
    file_path VARCHAR(500),
    error_message TEXT,
    INDEX idx_backup_id (backup_id),
    INDEX idx_backup_type (backup_type),
    INDEX idx_status (status),
    INDEX idx_start_time (start_time)
);

CREATE TABLE IF NOT EXISTS mnp_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mnp_id VARCHAR(255) UNIQUE NOT NULL,
    contract_id VARCHAR(255),
    phone_number VARCHAR(20) NOT NULL,
    current_carrier VARCHAR(50) NOT NULL,
    account_name VARCHAR(255),
    account_number VARCHAR(255),
    account_password VARCHAR(255),
    reservation_number VARCHAR(50),
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED', 'CANCELLED')),
    type VARCHAR(20) NOT NULL CHECK (type IN ('TRANSFER_IN', 'TRANSFER_OUT', 'ELIGIBILITY_CHECK')),
    desired_porting_date DATE,
    estimated_completion_date DATE,
    actual_completion_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS mnp_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mnp_request_id BIGINT NOT NULL,
    from_status VARCHAR(20),
    to_status VARCHAR(20) NOT NULL,
    reason TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    FOREIGN KEY (mnp_request_id) REFERENCES mnp_requests(id)
);

CREATE TABLE IF NOT EXISTS carrier_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrier_code VARCHAR(50) UNIQUE NOT NULL,
    carrier_name VARCHAR(255) NOT NULL,
    api_endpoint VARCHAR(500),
    api_key VARCHAR(255),
    timeout_seconds INT DEFAULT 30,
    retry_attempts INT DEFAULT 3,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_mnp_requests_phone ON mnp_requests(phone_number);
CREATE INDEX idx_mnp_requests_status ON mnp_requests(status);
CREATE INDEX idx_mnp_requests_carrier ON mnp_requests(current_carrier);
CREATE INDEX idx_mnp_requests_created ON mnp_requests(created_at);
CREATE INDEX idx_mnp_status_history_request ON mnp_status_history(mnp_request_id);
CREATE INDEX idx_carrier_info_code ON carrier_info(carrier_code);
