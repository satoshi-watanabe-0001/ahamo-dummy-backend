INSERT INTO users (id, email, phone_number, password_hash, first_name, last_name, first_name_kana, last_name_kana, birth_date, gender, contract_number, is_email_verified, is_phone_verified, is_active, failed_login_attempts, created_at, updated_at) VALUES
(1, 'test@example.com', '090-1234-5678', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6YVmOLwFxtMplVcpLG6', '太郎', '田中', 'タロウ', 'タナカ', '1990-01-01', 'MALE', 'C001234567', true, true, true, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00'),
(2, 'user@ahamo.com', '080-9876-5432', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqyc6YVmOLwFxtMplVcpLG6', '花子', '佐藤', 'ハナコ', 'サトウ', '1985-05-15', 'FEMALE', 'C987654321', true, true, true, 0, '2024-01-01 00:00:00', '2024-01-01 00:00:00');

INSERT INTO user_roles (user_id, role) VALUES
(1, 'USER'),
(2, 'USER');

INSERT INTO devices (id, name, brand, category, price_range, price, colors, storage_options, in_stock, image_url, specifications, created_at, updated_at, created_by, updated_by) VALUES
('device_iphone15_001', 'iPhone 15', 'Apple', 'iPhone', 'premium', 124800.00, 'ブラック,ブルー,グリーン,イエロー,ピンク', '128GB,256GB,512GB', true, '/images/iphone15.jpg', '{"display_size":"6.1インチ","battery":"3349mAh","camera":"48MP","processor":"A16 Bionic","memory":"6GB"}', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'admin@example.com', 'admin@example.com'),
('device_iphone15pro_001', 'iPhone 15 Pro', 'Apple', 'iPhone', 'premium', 159800.00, 'ナチュラルチタニウム,ブルーチタニウム,ホワイトチタニウム,ブラックチタニウム', '128GB,256GB,512GB,1TB', true, '/images/iphone15pro.jpg', '{"display_size":"6.1インチ","battery":"3274mAh","camera":"48MP","processor":"A17 Pro","memory":"8GB"}', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'admin@example.com', 'admin@example.com'),
('device_galaxys24_001', 'Galaxy S24', 'Samsung', 'Android', 'premium', 124800.00, 'オニキスブラック,マーブルグレー,コバルトバイオレット,アンバーイエロー', '256GB,512GB', true, '/images/galaxys24.jpg', '{"display_size":"6.2インチ","battery":"4000mAh","camera":"50MP","processor":"Snapdragon 8 Gen 3","memory":"8GB"}', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'admin@example.com', 'admin@example.com'),
('device_pixel8_001', 'Pixel 8', 'Google', 'Android', 'mid', 112900.00, 'オブシディアン,ヘーゼル,ローズ', '128GB,256GB', true, '/images/pixel8.jpg', '{"display_size":"6.2インチ","battery":"4575mAh","camera":"50MP","processor":"Google Tensor G3","memory":"8GB"}', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'admin@example.com', 'admin@example.com'),
('device_aquosr8_001', 'AQUOS R8', 'Sharp', 'Android', 'mid', 146850.00, 'ブルー,クリーム', '256GB', true, '/images/aquosr8.jpg', '{"display_size":"6.39インチ","battery":"4570mAh","camera":"50.3MP","processor":"Snapdragon 8 Gen 2","memory":"8GB"}', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'admin@example.com', 'admin@example.com');
