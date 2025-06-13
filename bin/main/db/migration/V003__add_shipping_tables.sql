
CREATE TABLE logistics_providers (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  provider_code VARCHAR(50) NOT NULL UNIQUE,
  provider_name VARCHAR(255) NOT NULL,
  api_endpoint VARCHAR(255),
  api_key VARCHAR(255),
  is_active BOOLEAN DEFAULT TRUE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shipping_orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_number VARCHAR(50) UNIQUE NOT NULL,
  contract_id BIGINT NOT NULL,
  provider_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  shipping_address_id BIGINT NOT NULL,
  device_id VARCHAR(255) NOT NULL,
  tracking_number VARCHAR(100),
  estimated_delivery_date DATE,
  delivery_time_window VARCHAR(50),
  delivery_options TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (provider_id) REFERENCES logistics_providers(id),
  FOREIGN KEY (shipping_address_id) REFERENCES addresses(id)
);

CREATE TABLE tracking_events (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  shipping_order_id BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  location VARCHAR(255),
  timestamp TIMESTAMP NOT NULL,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (shipping_order_id) REFERENCES shipping_orders(id)
);

CREATE INDEX idx_shipping_orders_contract ON shipping_orders(contract_id);
CREATE INDEX idx_shipping_orders_status ON shipping_orders(status);
CREATE INDEX idx_shipping_orders_provider ON shipping_orders(provider_id);
CREATE INDEX idx_tracking_events_order ON tracking_events(shipping_order_id);
CREATE INDEX idx_tracking_events_timestamp ON tracking_events(timestamp);

INSERT INTO logistics_providers (provider_code, provider_name, api_endpoint, is_active) VALUES
('YAMATO', 'ヤマト運輸', 'https://api.yamato.co.jp/v1', TRUE),
('SAGAWA', '佐川急便', 'https://api.sagawa-exp.co.jp/v1', TRUE),
('JAPAN_POST', '日本郵便', 'https://api.post.japanpost.jp/v1', TRUE);
