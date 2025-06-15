-- ENUM types
CREATE TYPE role AS ENUM ('Admin', 'Accountant', 'Resident');
CREATE TYPE fee_category AS ENUM ('Service', 'Management', 'Parking', 'Utility', 'Voluntary');
CREATE TYPE calculation_method AS ENUM ('Fixed', 'PerSqM', 'PerVehicle');
CREATE TYPE vehicle_type AS ENUM ('MOTORBIKE', 'CAR');

-- USERS
CREATE TABLE users (
  user_id SERIAL PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  phone_number VARCHAR(15),
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  last_login TIMESTAMP,
  role role NOT NULL,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- HOUSEHOLDS
CREATE TABLE households (
  household_id SERIAL PRIMARY KEY,
  apartment_code VARCHAR(20) UNIQUE NOT NULL,
  address VARCHAR(255) NOT NULL,
  area FLOAT NOT NULL,
  head_resident_id INT,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- RESIDENTS
CREATE TABLE residents (
  resident_id SERIAL PRIMARY KEY,
  household_id INT REFERENCES households(household_id),
  name VARCHAR(100) NOT NULL,
  birthday DATE NOT NULL,
  relationship VARCHAR(50) NOT NULL,
  national_id VARCHAR(20) UNIQUE NOT NULL,
  phone_number VARCHAR(15) NOT NULL,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- VEHICLES
CREATE TABLE vehicles (
  vehicle_id SERIAL PRIMARY KEY,
  household_id INT REFERENCES households(household_id),
  type vehicle_type NOT NULL,
  plate_number VARCHAR(20) NOT NULL,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- FEES
CREATE TABLE fees (
  fee_id SERIAL PRIMARY KEY,
  fee_name VARCHAR(100) NOT NULL,
  fee_category fee_category NOT NULL,
  fee_amount FLOAT NOT NULL,
  calculation_method calculation_method NOT NULL,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- VEHICLE_FEE_MAPPING (fix dùng id SERIAL)
CREATE TABLE vehicle_fee_mapping (
  id SERIAL PRIMARY KEY,
  vehicle_type vehicle_type NOT NULL UNIQUE,
  fee_id INT NOT NULL REFERENCES fees(fee_id)
);

-- COLLECTION_BATCHES
CREATE TABLE collection_batches (
  batch_id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  period DATE NOT NULL,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- BATCH_FEES
CREATE TABLE batch_fees (
  batch_fee_id SERIAL PRIMARY KEY,
  batch_id INT NOT NULL REFERENCES collection_batches(batch_id),
  fee_id INT NOT NULL REFERENCES fees(fee_id),
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- BILLING_ITEMS
CREATE TABLE billing_items (
  billing_item_id SERIAL PRIMARY KEY,
  household_id INT NOT NULL REFERENCES households(household_id),
  fee_id INT NOT NULL REFERENCES fees(fee_id),
  batch_id INT NOT NULL REFERENCES collection_batches(batch_id),
  expected_amount FLOAT NOT NULL,
  actual_amount FLOAT NOT NULL,
status VARCHAR(20) NOT NULL,
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- TRANSACTIONS
CREATE TABLE transactions (
  transaction_id SERIAL PRIMARY KEY,
  billing_item_id INT NOT NULL REFERENCES billing_items(billing_item_id),
  amount_paid FLOAT NOT NULL,
  payment_date DATE,
  created_by INT NOT NULL REFERENCES users(user_id),
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- RECEIPTS
CREATE TABLE receipts (
  receipt_id SERIAL PRIMARY KEY,
  transaction_id INT NOT NULL REFERENCES transactions(transaction_id),
  receipt_number VARCHAR(50) UNIQUE NOT NULL,
  issue_date DATE NOT NULL,
  file_url VARCHAR(255),
  created_at TIMESTAMP(6),
  updated_at TIMESTAMP(6)
);

-- DỮ LIỆU MẪU

-- USERS
INSERT INTO users (username, password, full_name, phone_number, role, created_at, updated_at) VALUES
('admin', 'admin123', 'Nguyễn Văn A', '0912345678', 'Admin', now(), now()),
('kt01', 'password123', 'Trần Thị B', '0988123456', 'Accountant', now(), now()),
('resident01', 'secret123', 'Lê Văn C', '0933445566', 'Resident', now(), now());

-- HOUSEHOLDS
INSERT INTO households (apartment_code, address, area, created_at, updated_at) VALUES
('A101', 'Tầng 1 - Block A', 75.5, now(), now()),
('B203', 'Tầng 2 - Block B', 68.0, now(), now());

-- RESIDENTS
INSERT INTO residents (household_id, name, birthday, relationship, national_id, phone_number, created_at, updated_at) VALUES
(1, 'Nguyễn Văn A', '1985-05-10', 'Chủ hộ', '001123456789', '0912345678', now(), now()),
(1, 'Nguyễn Văn B', '2010-03-21', 'Con', '001123456790', '0901122334', now(), now()),
(2, 'Trần Thị B', '1990-07-15', 'Chủ hộ', '001123456791', '0988123456', now(), now());

-- VEHICLES
INSERT INTO vehicles (household_id, type, plate_number, created_at, updated_at) VALUES
(1, 'CAR', '30A-123.45', now(), now()),
(2, 'MOTORBIKE', '29C1-456.78', now(), now());

-- FEES
INSERT INTO fees (fee_name, fee_category, fee_amount, calculation_method, created_at, updated_at) VALUES
('Phí quản lý tòa nhà', 'Management', 500000, 'Fixed', now(), now()),
('Phí gửi xe máy', 'Parking', 70000, 'PerVehicle', now(), now()),
('Phí gửi ô tô', 'Parking', 1200000, 'PerVehicle', now(), now()),
('Phí điện, nước, internet', 'Utility', 0, 'Fixed', now(), now()), -- thường cập nhật sau
('Phí vệ sinh định kỳ', 'Service', 100000, 'Fixed', now(), now());


-- VEHICLE_FEE_MAPPING
INSERT INTO vehicle_fee_mapping (vehicle_type, fee_id) VALUES
('CAR', 2),
('MOTORBIKE', 2);

-- COLLECTION_BATCHES
INSERT INTO collection_batches (name, period, created_at, updated_at) VALUES
('Đợt thu tháng 6', '2025-06-01', now(), now());

-- BATCH_FEES
INSERT INTO batch_fees (batch_id, fee_id, created_at, updated_at) VALUES
(1, 1, now(), now()),
(1, 2, now(), now());

-- BILLING_ITEMS
INSERT INTO billing_items (household_id, fee_id, batch_id, expected_amount, actual_amount, status, created_at, updated_at) VALUES
(1, 1, 1, 500000, 500000, 'Paid', now(), now()),
(1, 2, 1, 1000000, 0, 'Pending', now(), now());

-- TRANSACTIONS
INSERT INTO transactions (billing_item_id, amount_paid, payment_date, created_by, created_at, updated_at) VALUES
(1, 500000, '2025-06-05', 1, now(), now());

-- RECEIPTS
INSERT INTO receipts (transaction_id, receipt_number, issue_date, file_url, created_at, updated_at) VALUES
(1, 'RCPT-001', '2025-06-05', 'receipts/rcpt001.pdf', now(), now());
