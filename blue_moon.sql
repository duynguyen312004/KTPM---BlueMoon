-- TRUNCATE/DELETE only removes data, not sequence state.
-- Use ALTER SEQUENCE ... RESTART WITH 1 to reset auto-increment IDs if you want new data to start from 1.
TRUNCATE TABLE receipts, transactions, billing_items, batch_fees, collection_batches, vehicle_fee_mapping, vehicles, residents, households, fees, users CASCADE;

-- Reset sequences (replace sequence names as needed)
ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE households_household_id_seq RESTART WITH 1;
ALTER SEQUENCE residents_resident_id_seq RESTART WITH 1;
ALTER SEQUENCE vehicles_vehicle_id_seq RESTART WITH 1;
ALTER SEQUENCE fees_fee_id_seq RESTART WITH 1;
ALTER SEQUENCE collection_batches_batch_id_seq RESTART WITH 1;
ALTER SEQUENCE batch_fees_batch_fee_id_seq RESTART WITH 1;
ALTER SEQUENCE billing_items_billing_item_id_seq RESTART WITH 1;
ALTER SEQUENCE transactions_transaction_id_seq RESTART WITH 1;
ALTER SEQUENCE receipts_receipt_id_seq RESTART WITH 1;
--1. users
INSERT INTO users (username, password, full_name, phone_number, role, is_active, last_login, created_at, updated_at) VALUES
    ('accountant02', 'hashed_acc_password2', 'pham hong duong', '0933333333', 'Accountant', FALSE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho users
INSERT INTO users (username, password, full_name, phone_number, role, is_active, last_login, created_at, updated_at) VALUES
    ('admin01', 'hashed_admin01', 'Nguyen Van A', '0901111111', 'Admin', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('acc01', 'hashed_acc01', 'Le Thi B', '0902222222', 'Accountant', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('acc02', 'hashed_acc02', 'Pham Van C', '0903333333', 'Accountant', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res01', 'hashed_res01', 'Tran Thi D', '0904444444', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res02', 'hashed_res02', 'Do Van E', '0905555555', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res03', 'hashed_res03', 'Hoang Thi F', '0906666666', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res04', 'hashed_res04', 'Bui Van G', '0907777777', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res05', 'hashed_res05', 'Ngo Thi H', '0908888888', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res06', 'hashed_res06', 'Vu Van I', '0909999999', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('res07', 'hashed_res07', 'Dang Thi K', '0910000000', 'Resident', TRUE, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 2. fees
INSERT INTO fees (fee_name, fee_category, fee_amount, calculation_method, created_at, updated_at) VALUES
    ('Phi Quan Ly Chung Cu', 'Management', 2000, 'PerSqM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Gui Xe May', 'Parking', 100000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Gui O To', 'Parking', 1200000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Tien Nuoc Sinh Hoat', 'Utility', 15000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Ve Sinh Hanh Lang', 'Service', 50000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Quy Tu Thien (Tu nguyen)', 'Voluntary', 20000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho fees
INSERT INTO fees (fee_name, fee_category, fee_amount, calculation_method, created_at, updated_at) VALUES
    ('Phi Bao Tri Thang May', 'Service', 30000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Bao Tri Thiet Bi', 'Service', 40000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Ve Sinh Cong Cong', 'Service', 25000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Bao Ve', 'Management', 35000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Quan Ly Ho Boi', 'Service', 45000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Bao Tri San Vuon', 'Service', 20000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Quan Ly Ban Quan Tri', 'Management', 50000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Bao Tri He Thong Dien', 'Utility', 60000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Bao Tri He Thong Nuoc', 'Utility', 70000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Phi Quan Ly Bai Xe', 'Parking', 80000, 'Fixed', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 3. households
INSERT INTO households (apartment_code, address, area, created_at, updated_at) VALUES
    ('A101', 'Toa A, Tang 1, Can 101, Khu Do Thi ABC', 75.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B205', 'Toa B, Tang 2, Can 205, Khu Do Thi ABC', 90.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C303', 'Toa C, Tang 3, Can 303, Khu Do Thi ABC', 60.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho households
INSERT INTO households (apartment_code, address, area, created_at, updated_at) VALUES
    ('A102', 'Toa A, Tang 1, Can 102, Khu Do Thi ABC', 80.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('A103', 'Toa A, Tang 1, Can 103, Khu Do Thi ABC', 85.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B206', 'Toa B, Tang 2, Can 206, Khu Do Thi ABC', 95.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('B207', 'Toa B, Tang 2, Can 207, Khu Do Thi ABC', 100.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C304', 'Toa C, Tang 3, Can 304, Khu Do Thi ABC', 65.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('C305', 'Toa C, Tang 3, Can 305, Khu Do Thi ABC', 70.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('D401', 'Toa D, Tang 4, Can 401, Khu Do Thi ABC', 110.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('D402', 'Toa D, Tang 4, Can 402, Khu Do Thi ABC', 120.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('E501', 'Toa E, Tang 5, Can 501, Khu Do Thi ABC', 130.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('E502', 'Toa E, Tang 5, Can 502, Khu Do Thi ABC', 140.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 4. residents
INSERT INTO residents (household_id, name, birthday, relationship, national_id, phone_number, created_at, updated_at) VALUES
    (1, 'Tran Thi Cu Dan', '1985-05-15', 'Chu ho', '012345678901', '0901000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'Tran Van An', '1980-02-20', 'Chong', '012345678902', '0901000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'Tran Bao Ngoc', '2010-10-10', 'Con gai', '012345678903', '0901000003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Le Van Binh', '1990-07-25', 'Chu ho', '012345678904', '0902000001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'Pham Thi Yen', '1992-11-30', 'Vo', '012345678905', '0902000002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho residents
INSERT INTO residents (household_id, name, birthday, relationship, national_id, phone_number, created_at, updated_at) VALUES
    (4, 'Nguyen Van M', '1982-01-01', 'Chu ho', '012345678910', '0911111111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'Le Thi N', '1983-02-02', 'Vo', '012345678911', '0912222222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'Pham Van O', '1984-03-03', 'Con', '012345678912', '0913333333', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'Tran Thi P', '1985-04-04', 'Chu ho', '012345678913', '0914444444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'Do Van Q', '1986-05-05', 'Vo', '012345678914', '0915555555', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 'Hoang Thi R', '1987-06-06', 'Con', '012345678915', '0916666666', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 'Bui Van S', '1988-07-07', 'Chu ho', '012345678916', '0917777777', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (11, 'Ngo Thi T', '1989-08-08', 'Vo', '012345678917', '0918888888', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (12, 'Vu Van U', '1990-09-09', 'Con', '012345678918', '0919999999', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (13, 'Dang Thi V', '1991-10-10', 'Chu ho', '012345678919', '0920000000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 5. vehicles
INSERT INTO vehicles (household_id, type, plate_number, created_at, updated_at) VALUES
    (1, 'MOTORBIKE', '29-A1-12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 'CAR', '30-B2-67890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'MOTORBIKE', '59-C3-11111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho vehicles
INSERT INTO vehicles (household_id, type, plate_number, created_at, updated_at) VALUES
    (4, 'MOTORBIKE', '29-A2-12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'CAR', '30-B3-67890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'MOTORBIKE', '59-C4-11111', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 'CAR', '30-B4-22222', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 'MOTORBIKE', '29-A3-33333', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 'CAR', '30-B5-44444', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 'MOTORBIKE', '59-C5-55555', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 'CAR', '30-B6-66666', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 'MOTORBIKE', '29-A4-77777', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 'CAR', '30-B7-88888', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 6. vehicle_fee_mapping
INSERT INTO vehicle_fee_mapping (vehicle_type, fee_id) VALUES
    ('MOTORBIKE', 2),
    ('CAR', 3);

-- 7. collection_batches
INSERT INTO collection_batches (name, period, created_at, updated_at) VALUES
    ('Thu phi thang 06/2024', '2024-06-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 07/2024', '2024-07-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho collection_batches
INSERT INTO collection_batches (name, period, created_at, updated_at) VALUES
    ('Thu phi thang 08/2024', '2024-08-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 09/2024', '2024-09-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 10/2024', '2024-10-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 11/2024', '2024-11-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 12/2024', '2024-12-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 01/2025', '2025-01-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 02/2025', '2025-02-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 03/2025', '2025-03-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 04/2025', '2025-04-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Thu phi thang 05/2025', '2025-05-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 8. batch_fees
INSERT INTO batch_fees (batch_id, fee_id, created_at, updated_at) VALUES
    (1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Thêm dữ liệu mẫu cho batch_fees
INSERT INTO batch_fees (batch_id, fee_id, created_at, updated_at) VALUES
    (3, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 11, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 13, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 14, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 16, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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

-- Thêm dữ liệu mẫu cho billing_items
INSERT INTO billing_items (household_id, fee_id, batch_id, expected_amount, actual_amount, status, created_at, updated_at) VALUES
    (4, 7, 3, 30000, 30000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 8, 4, 40000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 9, 5, 25000, 25000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (7, 10, 6, 35000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (8, 11, 7, 45000, 45000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (9, 12, 8, 20000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (10, 13, 9, 50000, 50000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (4, 14, 10, 60000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (5, 15, 3, 70000, 70000, 'Paid', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (6, 16, 4, 80000, 0, 'Pending', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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

-- Thêm dữ liệu mẫu cho transactions
INSERT INTO transactions (billing_item_id, amount_paid, payment_date, created_by, created_at, updated_at) VALUES
    (14, 30000, '2024-08-05', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (15, 40000, '2024-09-05', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (16, 25000, '2024-10-05', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (17, 35000, '2024-11-05', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (18, 45000, '2024-12-05', 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (19, 20000, '2025-01-05', 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (20, 50000, '2025-02-05', 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (21, 60000, '2025-03-05', 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (22, 70000, '2025-04-05', 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (23, 80000, '2025-05-05', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 11. receipts
INSERT INTO receipts (transaction_id, receipt_number, issue_date, file_url, created_at, updated_at) VALUES
                                                                                                        (1, 'RCPT0624A101001', '2024-06-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (2, 'RCPT0624A101002', '2024-06-05', '/receipts/RCPT0624A101002.pdf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (3, 'RCPT0624A101003', '2024-06-06', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (4, 'RCPT0624A101004', '2024-06-06', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (5, 'RCPT0624A101005', '2024-06-07', '/receipts/RCPT0624A101005.pdf', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (6, 'RCPT0624B205001', '2024-06-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (7, 'RCPT0624B205002', '2024-06-10', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (8, 'RCPT0724C303001', '2024-07-08', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (9, 'RCPT0824A102001', '2024-08-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (10, 'RCPT0924A103001', '2024-09-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (11, 'RCPT1024B206001', '2024-10-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (12, 'RCPT1124B207001', '2024-11-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (13, 'RCPT1224C304001', '2024-12-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (14, 'RCPT0225D401001', '2025-02-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (15, 'RCPT0325D402001', '2025-03-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (16, 'RCPT0425E501001', '2025-04-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (17, 'RCPT0525E502001', '2025-05-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                        (18, 'RCPT0525E503001', '2025-06-05', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
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
