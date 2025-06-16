
ALTER TABLE tracking_events 
ADD COLUMN latitude DECIMAL(10,8),
ADD COLUMN longitude DECIMAL(11,8),
ADD COLUMN estimated_arrival_time TIMESTAMP;

ALTER TABLE shipping_orders 
ADD COLUMN delivery_rating INTEGER CHECK (delivery_rating >= 1 AND delivery_rating <= 5),
ADD COLUMN delivery_feedback TEXT,
ADD COLUMN delivery_confirmed_at TIMESTAMP;

CREATE INDEX idx_tracking_events_location ON tracking_events(latitude, longitude);
CREATE INDEX idx_tracking_events_timestamp ON tracking_events(timestamp);
