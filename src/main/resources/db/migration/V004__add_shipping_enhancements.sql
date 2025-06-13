CREATE TABLE IF NOT EXISTS convenience_stores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_code VARCHAR(50) UNIQUE NOT NULL,
    store_name VARCHAR(255) NOT NULL,
    chain_name VARCHAR(100) NOT NULL,
    postal_code VARCHAR(10) NOT NULL,
    prefecture VARCHAR(50) NOT NULL,
    city VARCHAR(100) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    phone VARCHAR(20),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    operating_hours TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS delivery_time_slots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    slot_name VARCHAR(100) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_type VARCHAR(20) NOT NULL CHECK (slot_type IN ('MORNING', 'AFTERNOON', 'EVENING', 'CUSTOM')),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS shipping_options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    option_code VARCHAR(50) UNIQUE NOT NULL,
    option_name VARCHAR(255) NOT NULL,
    description TEXT,
    requires_recipient_info BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_convenience_stores_location ON convenience_stores(prefecture, city);
CREATE INDEX idx_convenience_stores_chain ON convenience_stores(chain_name);
CREATE INDEX idx_delivery_time_slots_type ON delivery_time_slots(slot_type);
CREATE INDEX idx_shipping_options_code ON shipping_options(option_code);

INSERT INTO convenience_stores (store_code, store_name, chain_name, postal_code, prefecture, city, address_line1, latitude, longitude, operating_hours) VALUES
('7ELV001', 'セブン-イレブン渋谷駅前店', 'セブン-イレブン', '150-0002', '東京都', '渋谷区', '渋谷1-1-1', 35.6580, 139.7016, '24時間営業'),
('FAMI001', 'ファミリーマート新宿南口店', 'ファミリーマート', '160-0022', '東京都', '新宿区', '新宿3-35-1', 35.6896, 139.7006, '24時間営業'),
('LAWS001', 'ローソン池袋東口店', 'ローソン', '170-0013', '東京都', '豊島区', '東池袋1-1-1', 35.7295, 139.7190, '24時間営業');

INSERT INTO delivery_time_slots (slot_name, start_time, end_time, slot_type) VALUES
('午前中', '08:00:00', '12:00:00', 'MORNING'),
('12時-14時', '12:00:00', '14:00:00', 'AFTERNOON'),
('14時-16時', '14:00:00', '16:00:00', 'AFTERNOON'),
('16時-18時', '16:00:00', '18:00:00', 'AFTERNOON'),
('18時-20時', '18:00:00', '20:00:00', 'EVENING'),
('19時-21時', '19:00:00', '21:00:00', 'EVENING');

INSERT INTO shipping_options (option_code, option_name, description, requires_recipient_info) VALUES
('unattended', '置き配', '玄関前などに荷物を置いて配送完了', FALSE),
('delivery_box', '宅配ボックス', 'マンション等の宅配ボックスに配送', FALSE),
('face_to_face', '対面受取', '受取人との対面での受け渡し', TRUE),
('recipient_only', '本人限定受取', '本人確認書類による本人限定受取', TRUE);
