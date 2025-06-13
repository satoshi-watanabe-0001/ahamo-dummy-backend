INSERT INTO carrier_info (carrier_code, carrier_name, api_endpoint, timeout_seconds, retry_attempts, is_active) VALUES
('DOCOMO', 'NTTドコモ', 'https://api.docomo.ne.jp/mnp', 30, 3, true),
('AU', 'KDDI au', 'https://api.au.com/mnp', 30, 3, true),
('SOFTBANK', 'ソフトバンク', 'https://api.softbank.jp/mnp', 30, 3, true),
('RAKUTEN', '楽天モバイル', 'https://api.rakuten.co.jp/mnp', 30, 3, true);
