--1. users
INSERT INTO users (username, password, full_name, phone_number, role, is_active, last_login, created_at, updated_at) VALUES
    ('accountant02', 'hashed_acc_password2', 'pham hong duong', '0933333333', 'Accountant', FALSE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. fees
INSERT INTO fees (fee_name, fee_category, fee_amount, calculation_method, created_at, updated_at) VALUES
    ('Phi Quan Ly Chung Cu', 'Management', 2000, 'PerSqM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Gui Xe May', 'Parking', 100000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Gui O To', 'Parking', 1200000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Tien Nuoc Sinh Hoat', 'Utility', 15000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Ve Sinh Hanh Lang', 'Service', 50000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Quy Tu Thien (Tu nguyen)', 'Voluntary', 20000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. households
INSERT INTO households (apartment_code, address, area, created_at, updated_at) VALUES
    ('A101', 'Toa A, Tang 1, Can 101, Khu Do Thi ABC', 75.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B205', 'Toa B, Tang 2, Can 205, Khu Do Thi ABC', 90.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C303', 'Toa C, Tang 3, Can 303, Khu Do Thi ABC', 60.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. residents
INSERT INTO residents (resident_id, household_id, name, birthday, relationship, national_id, phone_number, created_at, updated_at) VALUES
    (1, 1, 'Tran Thi Cu Dan', '1985-05-15', 'Chu ho', '012345678901', '0901000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 'Tran Van An', '1980-02-20', 'Chong', '012345678902', '0901000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 'Tran Bao Ngoc', '2010-10-10', 'Con gai', '012345678903', '0901000003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 2, 'Le Van Binh', '1990-07-25', 'Chu ho', '012345678904', '0902000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 2, 'Pham Thi Yen', '1992-11-30', 'Vo', '012345678905', '0902000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. vehicles
INSERT INTO vehicles (household_id, type, plate_number, created_at, updated_at) VALUES
    (1, 'MOTORBIKE', '29-A1-12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'CAR', '30-B2-67890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'MOTORBIKE', '59-C3-11111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. vehicle_fee_mapping
INSERT INTO vehicle_fee_mapping (vehicle_type, fee_id) VALUES
    ('MOTORBIKE', 2),
    ('CAR', 3);

-- 7. collection_batches
INSERT INTO collection_batches (name, period, created_at, updated_at) VALUES
    ('Thu phi thang 06/2024', '2024-06-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 07/2024', '2024-07-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. batch_fees
INSERT INTO batch_fees (batch_id, fee_id, created_at, updated_at) VALUES
    (1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 9. billing_items
INSERT INTO billing_items (household_id, fee_id, batch_id, expected_amount, actual_amount, status, created_at, updated_at) VALUES
    (1, 1, 1, 151000, 151000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 2, 1, 100000, 100000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 3, 1, 1200000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 4, 1, 15000, 15000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 5, 1, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 6, 1, 20000, 20000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 1, 1, 180000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, 1, 100000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 4, 1, 15000, 15000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 5, 1, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 1, 2, 120500, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 4, 2, 15000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 5, 2, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 10. transactions
INSERT INTO transactions (billing_item_id, amount_paid, payment_date, created_by, created_at, updated_at) VALUES
    (1, 151000, '2024-06-05', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 100000, '2024-06-05', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 15000, '2024-06-06', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 50000, '2024-06-06', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 20000, '2024-06-07', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 15000, '2024-06-10', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 50000, '2024-06-10', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (13, 50000, '2024-07-08', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 11. receipts
INSERT INTO receipts (transaction_id, receipt_number, issue_date, file_url, created_at, updated_at) VALUES
    (1, 'RCPT0624A101001', '2024-06-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'RCPT0624A101002', '2024-06-05', '/receipts/RCPT0624A101002.pdf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 'RCPT0624A101003', '2024-06-06', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'RCPT0624A101004', '2024-06-06', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'RCPT0624A101005', '2024-06-07', '/receipts/RCPT0624A101005.pdf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'RCPT0624B205001', '2024-06-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'RCPT0624B205002', '2024-06-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'RCPT0724C303001', '2024-07-08', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

UPDATE fees SET
                fee_name = 'Phi Quan Ly Chung Cu',
                fee_category = 'Management'
WHERE fee_name = 'Phi Quan Ly Chung Cu';

UPDATE fees SET
                fee_name = 'Phi Gui Xe May',
                fee_category = 'Parking'
WHERE fee_name = 'Phi Gui Xe May';

UPDATE fees SET
                fee_name = 'Phi Gui O To',
                fee_category = 'Parking'
WHERE fee_name = 'Phi Gui O To';

UPDATE fees SET
                fee_name = 'Tien Nuoc Sinh Hoat',
                fee_category = 'Utility'
WHERE fee_name = 'Tien Nuoc Sinh Hoat';

UPDATE fees SET
                fee_name = 'Phi Ve Sinh Hanh Lang',
                fee_category = 'Service'
WHERE fee_name = 'Phi Ve Sinh Hanh Lang';

UPDATE fees SET
                fee_name = 'Quy Tu Thien (Tu nguyen)',
                fee_category = 'Voluntary'
WHERE fee_name = 'Quy Tu Thien (Tu nguyen)';

UPDATE households SET
    address = 'Toa A, Tang 1, Can 101, Khu Do Thi ABC'
WHERE apartment_code = 'A101';

UPDATE households SET
    address = 'Toa B, Tang 2, Can 205, Khu Do Thi ABC'
WHERE apartment_code = 'B205';

UPDATE households SET
    address = 'Toa C, Tang 3, Can 303, Khu Do Thi ABC'
WHERE apartment_code = 'C303';

UPDATE residents SET
                     name = 'Tran Thi Cu Dan',
                     relationship = 'Chu ho'
WHERE name = 'Tran Thi Cu Dan' AND household_id = 1;

UPDATE residents SET
                     name = 'Tran Van An',
                     relationship = 'Chong'
WHERE name = 'Tran Van An' AND household_id = 2;

UPDATE residents SET
                     name = 'Tran Bao Ngoc',
                     relationship = 'Con gai'
WHERE name = 'Tran Bao Ngoc' AND household_id = 2;

UPDATE residents SET
                     name = 'Le Van Binh',
                     relationship = 'Chu ho'
WHERE name = 'Le Van Binh' AND household_id = 3;

UPDATE residents SET
                     name = 'Pham Thi Yen',
                     relationship = 'Vo'
WHERE name = 'Pham Thi Yen' AND household_id = 3;
