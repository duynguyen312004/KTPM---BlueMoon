-- 2. fees
INSERT INTO fees (feename, feecategory, feeamount, calculationmethod,created_at,updated_at) VALUES
('Phí Quản Lý Chung Cư', 'Management', 2000, 'PerSqM',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('Phí Gửi Xe Máy', 'Parking', 100000, 'Fixed',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('Phí Gửi Ô Tô', 'Parking', 1200000, 'Fixed',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('Tiền Nước Sinh Hoạt', 'Utility', 15000, 'Fixed',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), -- Đây có thể là đơn giá/m3 hoặc phí cố định tùy mô hình
('Phí Vệ Sinh Hành Lang', 'Service', 50000, 'Fixed',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('Quỹ Từ Thiện (Tự nguyện)', 'Voluntary', 20000, 'Fixed',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

-- Giả sử các fee_id được tạo là: 1 (QLCC), 2 (Xe máy), 3 (Ô tô), 4 (Nước), 5 (Vệ sinh), 6 (Từ thiện)

INSERT INTO vehicle_fee_mapping (vehicle_type, fee_id) VALUES
('MOTORBIKE', 10), -- Phí gửi xe máy (fee_id = 2)
('CAR', 11);       -- Phí gửi ô tô (fee_id = 3)

INSERT INTO households (apartment_code, address, area,created_at,updated_at) VALUES
('A101', 'Tòa A, Tầng 1, Căn 101, Khu Đô Thị ABC', 75.50,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('B205', 'Tòa B, Tầng 2, Căn 205, Khu Đô Thị ABC', 90.00,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('C303', 'Tòa C, Tầng 3, Căn 303, Khu Đô Thị ABC', 60.25,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

INSERT INTO residents (user_id, household_id, name, birthday, relationship,created_at,updated_at) VALUES
(NULL, 12, 'Trần Thị Cư Dân', '1985-05-15', 'Chủ hộ',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), -- Liên kết với user_id = 3 (resident01)
(NULL, 12, 'Trần Văn An', '1980-02-20', 'Chồng',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(NULL, 12, 'Trần Bảo Ngọc', '2010-10-10', 'Con gái',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);
INSERT INTO residents (user_id, household_id, name, birthday, relationship,created_at,updated_at) VALUES
(NULL, 13, 'Lê Văn Bình', '1990-07-25', 'Chủ hộ',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(NULL, 13, 'Phạm Thị Yên', '1992-11-30', 'Vợ',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);


INSERT INTO vehicles (household_id, type, plate_number,created_at,updated_at) VALUES
(12, 'MOTORBIKE', '29-A1-12345',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(12, 'CAR', '30-B2-67890',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
(13, 'MOTORBIKE', '59-C3-11111',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);

-- 7. collection_batches
INSERT INTO collection_batches (name, period,created_at,updated_at) VALUES
('Thu phi thang 06/2024', '2024-06-01',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP),
('Thu phí thang 07/2024', '2024-07-01',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);


INSERT INTO batch_fees (batch_id, fee_id,created_at,updated_at) VALUES
(2, 9,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (3, 11,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (3, 13,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (3, 13,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (3, 11,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), -- Đợt 1 có tất cả các phí
 (2, 10,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (2, 12,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (2, 14,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP), (2, 13,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP);          -- Đợt 2 không có phí từ thiện


 -- Hộ A101 (id=1), diện tích 75.50 m2, có 1 xe máy, 1 ô tô. Đợt tháng 06/2024 (batch_id = 1)
INSERT INTO billing_items (household_id, fee_id, batch_id, expected_amount, actual_amount, status, created_at, updated_at) VALUES
(12, 10, 2, 151000, 151000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),    -- QLCC: 75.50 * 2000
(12, 12, 2, 100000, 100000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),    -- Xe Máy
(12, 13, 2, 1200000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),     -- Ô tô (chưa thanh toán)
(12, 13, 2, 15000, 15000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),      -- Nước
(12, 13, 2, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),      -- Vệ Sinh
(12, 13, 2, 20000, 20000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);      -- Từ Thiện

-- Hộ B205 (id=2), diện tích 90.00 m2, có 1 xe máy. Đợt tháng 06/2024 (batch_id = 1)
INSERT INTO billing_items (household_id, fee_id, batch_id, expected_amount, actual_amount, status, created_at, updated_at) VALUES
(13, 11, 2, 180000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),     -- QLCC: 90.00 * 2000
(13, 12, 2, 100000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),     -- Xe Máy
(13, 13, 2, 15000, 15000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),     -- Nước
(13, 13, 2, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);     -- Vệ Sinh

-- Hộ C303 (id=3), diện tích 60.25 m2, không có xe. Đợt tháng 07/2024 (batch_id = 2)
INSERT INTO billing_items (household_id, fee_id, batch_id, expected_amount, actual_amount, status, created_at, updated_at) VALUES
(14, 11, 2, 120500, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),     -- QLCC: 60.25 * 2000
(14, 11, 2, 15000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),      -- Nước
(14, 11, 2, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);     -- Vệ Sinh


INSERT INTO transactions (billing_item_id, amount_paid, payment_date, created_by, created_at, updated_at) VALUES
(25, 151000, '2024-06-05', 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(26, 100000, '2024-06-05', 19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(27, 15000, '2024-06-06',  19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(28, 50000, '2024-06-06',  19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(29, 20000, '2024-06-07',  19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(30, 15000, '2024-06-10',  19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(31, 50000, '2024-06-10',  19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(32, 50000, '2024-07-08',  19, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


INSERT INTO receipts (transaction_id, receipt_number, issue_date, file_url, created_at, updated_at) VALUES
(9, 'RCPT0624A101001', '2024-06-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'RCPT0624A101002', '2024-06-05', '/receipts/RCPT0624A101002.pdf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'RCPT0624A101003', '2024-06-06', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'RCPT0624A101004', '2024-06-06', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'RCPT0624A101005', '2024-06-07', '/receipts/RCPT0624A101005.pdf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'RCPT0624B205001', '2024-06-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'RCPT0624B205002', '2024-06-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'RCPT0724C303001', '2024-07-08', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


UPDATE fees SET 
    feename = 'Phi Quan Ly Chung Cu', 
    feecategory = 'Management' 
WHERE feename = 'Phí Quản Lý Chung Cư';

UPDATE fees SET 
    feename = 'Phi Gui Xe May', 
    feecategory = 'Parking' 
WHERE feename = 'Phí Gửi Xe Máy';

UPDATE fees SET 
    feename = 'Phi Gui O To', 
    feecategory = 'Parking' 
WHERE feename = 'Phí Gửi Ô Tô';

UPDATE fees SET 
    feename = 'Tien Nuoc Sinh Hoat', 
    feecategory = 'Utility' 
WHERE feename = 'Tiền Nước Sinh Hoạt';

UPDATE fees SET 
    feename = 'Phi Ve Sinh Hanh Lang', 
    feecategory = 'Service' 
WHERE feename = 'Phí Vệ Sinh Hành Lang';

UPDATE fees SET 
    feename = 'Quy Tu Thien (Tu nguyen)', 
    feecategory = 'Voluntary' 
WHERE feename = 'Quỹ Từ Thiện (Tự nguyện)';



UPDATE households SET 
    address = 'Toa A, Tang 1, Can 101, Khu Do Thi ABC' 
WHERE apartment_code = 'A101';

UPDATE households SET 
    address = 'Toa B, Tang 2, Can 205, Khu Do Thi ABC' 
WHERE apartment_code = 'B205';

UPDATE households SET 
    address = 'Toa C, Tang 3, Can 303, Khu Do Thi ABC' 
WHERE apartment_code = 'C303';


-- Hộ A101 (id=12)
UPDATE residents SET 
    name = 'Tran Thi Cu Dan',
    relationship = 'Chu ho'
WHERE name = 'Trần Thị Cư Dân' AND household_id = 12;

UPDATE residents SET 
    name = 'Tran Van An',
    relationship = 'Chong'
WHERE name = 'Trần Văn An' AND household_id = 12;

UPDATE residents SET 
    name = 'Tran Bao Ngoc',
    relationship = 'Con gai'
WHERE name = 'Trần Bảo Ngọc' AND household_id = 12;

-- Hộ B205 (id=13)
UPDATE residents SET 
    name = 'Le Van Binh',
    relationship = 'Chu ho'
WHERE name = 'Lê Văn Bình' AND household_id = 13;

UPDATE residents SET 
    name = 'Pham Thi Yen',
    relationship = 'Vo'
WHERE name = 'Phạm Thị Yên' AND household_id = 13;
