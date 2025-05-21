
-- ENUM Definitions (PostgreSQL requires separate type creation)
DO $$ BEGIN
    CREATE TYPE user_role AS ENUM ('Admin', 'Accountant', 'Resident');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE fee_category AS ENUM ('Service', 'Management', 'Parking', 'Utility', 'Voluntary');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE calc_method AS ENUM ('Fixed', 'PerSqM', 'PerVehicle');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE billing_status AS ENUM ('Pending', 'Paid');
EXCEPTION WHEN duplicate_object THEN null; END $$;

DO $$ BEGIN
    CREATE TYPE vehicle_type_enum AS ENUM ('MOTORBIKE', 'CAR');
EXCEPTION WHEN duplicate_object THEN null; END $$;

SELECT setval('residents_resident_id_seq', (SELECT MAX(resident_id) FROM residents)); // nếu anh em bị lỗi khi thêm nhân khẩu mà thấy nó  bao lỗi đã tồn tại id hãy chạy lệnh này : ))
-- 1. users: tài khoản hệ thống
CREATE TABLE users (
  user_id      SERIAL PRIMARY KEY,
  username     VARCHAR(50)    NOT NULL UNIQUE,
  password     VARCHAR(255)   NOT NULL,
  full_name    VARCHAR(100)   NOT NULL,
  phone_number VARCHAR(15),
  role         user_role      NOT NULL DEFAULT 'Accountant',
  is_active    BOOLEAN        NOT NULL DEFAULT TRUE,
  last_login   TIMESTAMP,
  created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. households
CREATE TABLE households (
  household_id     SERIAL PRIMARY KEY,
  apartment_code   VARCHAR(20)    NOT NULL UNIQUE,
  address          VARCHAR(255)   NOT NULL,
  area             DECIMAL(6,2)   NOT NULL DEFAULT 0,
  head_resident_id INT,
  created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. residents
CREATE TABLE residents (
  resident_id    SERIAL PRIMARY KEY,
  user_id        INT REFERENCES users(user_id),
  household_id   INT NOT NULL REFERENCES households(household_id),
  name           VARCHAR(100) NOT NULL,
  national_id // CCCD
  phone_number // SDT
  birthday       DATE NOT NULL,
  relationship   VARCHAR(50) NOT NULL,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. vehicles
CREATE TABLE vehicles (
  vehicle_id     SERIAL PRIMARY KEY,
  household_id   INT NOT NULL REFERENCES households(household_id),
  type           vehicle_type_enum NOT NULL,
  plate_number   VARCHAR(20) NOT NULL,
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 5. fees
CREATE TABLE fees (
  fee_id             SERIAL PRIMARY KEY,
  fee_name           VARCHAR(100) NOT NULL,
  fee_category       fee_category NOT NULL,
  fee_amount         DECIMAL(15,2) NOT NULL,
  calculation_method calc_method NOT NULL DEFAULT 'Fixed',
  created_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. vehicle_fee_mapping
CREATE TABLE vehicle_fee_mapping (
  vehicle_type vehicle_type_enum PRIMARY KEY,
  fee_id       INT NOT NULL REFERENCES fees(fee_id)
);

-- 7. collection_batches
CREATE TABLE collection_batches (
  batch_id    SERIAL PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  period      DATE NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 8. batch_fees
CREATE TABLE batch_fees (
  batch_fee_id SERIAL PRIMARY KEY,
  batch_id     INT NOT NULL REFERENCES collection_batches(batch_id),
  fee_id       INT NOT NULL REFERENCES fees(fee_id),
  created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 9. billing_items
CREATE TABLE billing_items (
  billing_item_id SERIAL PRIMARY KEY,
  household_id    INT NOT NULL REFERENCES households(household_id),
  fee_id          INT NOT NULL REFERENCES fees(fee_id),
  batch_id        INT NOT NULL REFERENCES collection_batches(batch_id),
  expected_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
  actual_amount   DECIMAL(15,2) NOT NULL DEFAULT 0,
  status          billing_status NOT NULL DEFAULT 'Pending',
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 10. transactions
CREATE TABLE transactions (
  transaction_id   SERIAL PRIMARY KEY,
  billing_item_id  INT NOT NULL REFERENCES billing_items(billing_item_id),
  amount_paid      DECIMAL(15,2) NOT NULL,
  payment_date     DATE,
  created_by       INT NOT NULL REFERENCES users(user_id),
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11. receipts
CREATE TABLE receipts (
  receipt_id      SERIAL PRIMARY KEY,
  transaction_id  INT NOT NULL REFERENCES transactions(transaction_id),
  receipt_number  VARCHAR(50) NOT NULL UNIQUE,
  issue_date      DATE NOT NULL,
  file_url        VARCHAR(255),
  created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_bi_house_batch_fee ON billing_items(household_id, batch_id, fee_id);
CREATE INDEX idx_tx_bi ON transactions(billing_item_id);

USERS 
| Cột                                      | Ý nghĩa                                       |
| ---------------------------------------- | --------------------------------------------- |
| `user_id`                                | Khoá chính                                    |
| `username`, `password`                   | Dùng để đăng nhập                             |
| `role`                                   | Phân quyền: `Admin`, `Accountant`, `Resident` |
| `is_active`                              | Trạng thái hoạt động (dùng thay cho xóa mềm)  |
| `last_login`, `created_at`, `updated_at` | Thông tin audit                               |

Quan hệ:

User tạo nhiều Transaction (qua created_by)

BẢNG HOUSEHOLDS

| Cột                | Ý nghĩa                                          |
| ------------------ | ------------------------------------------------ |
| `apartment_code`   | Mã căn hộ (hiển thị như A101)                    |
| `address`, `area`  | Thông tin chi tiết căn hộ                        |
| `head_resident_id` | FK tới `residents.resident_id` – xác định chủ hộ |

1 Household có nhiều Resident, Vehicle, BillingItem

RESIDENTS 

| Cột                                | Ý nghĩa                                   |
| ---------------------------------- | ----------------------------------------- |
| `household_id`                     | Gắn với 1 căn hộ                          |
| `user_id`                          | Có thể liên kết tài khoản `User` để login |
| `name`, `birthday`, `relationship` | Thông tin thành viên                      |

Quan hệ:

1 Resident có thể là head_resident của Household

VEHICLES 
| Cột            | Ý nghĩa                  |
| -------------- | ------------------------ |
| `household_id` | FK tới `households`      |
| `type`         | Enum: `MOTORBIKE`, `CAR` |
| `plate_number` | Biển số                  |

Quan hệ:

Dùng để tính phí gửi xe thông qua vehicle_fee_mapping

FEES 
| Cột                                | Ý nghĩa              |
| ---------------------------------- | -------------------- |
| `fee_name`, `fee_category`         | Tên và loại phí      |
| `fee_amount`, `calculation_method` | Đơn giá và cách tính |

Quan hệ:

Dùng trong billing_items, batch_fees, vehicle_fee_mapping


VEHICLE_FEE_MAPPING 

| Cột            | Ý nghĩa                      |
| -------------- | ---------------------------- |
| `vehicle_type` | Enum: `MOTORBIKE`, `CAR`     |
| `fee_id`       | Gắn với phí gửi xe tương ứng |

COLLECTION_BATCHES 
| Cột      | Ý nghĩa                   |
| -------- | ------------------------- |
| `name`   | VD: "Tháng 6/2025"        |
| `period` | Định kỳ, ví dụ 2025-06-01 |

Gắn với nhiều batch_fees và billing_items

BATCH_FEES 

| Cột                  | Ý nghĩa                      |
| -------------------- | ---------------------------- |
| `batch_id`, `fee_id` | Gắn phí vào kỳ thu tương ứng |


BILLING_ITEMS 

| Cột                                  | Ý nghĩa                                        |
| ------------------------------------ | ---------------------------------------------- |
| `household_id`, `fee_id`, `batch_id` | Xác định kỳ thu nào của hộ nào, áp dụng phí gì |
| `expected_amount`, `actual_amount`   | Số tiền kỳ vọng và đã thu                      |
| `status`                             | Enum: `Pending`, `Paid`                        |

1 BillingItem có nhiều Transaction



TRANSACTIONS 

| Cột                           | Ý nghĩa                          |
| ----------------------------- | -------------------------------- |
| `billing_item_id`             | Gắn với hoá đơn                  |
| `amount_paid`, `payment_date` | Số tiền nộp và thời điểm nộp     |
| `created_by`                  | Nhân viên tạo transaction (user) |

RECEIPTS 
| Cột              | Ý nghĩa                        |
| ---------------- | ------------------------------ |
| `transaction_id` | Gắn với 1 transaction duy nhất |
| `receipt_number` | Số biên nhận, duy nhất         |
| `file_url`       | Link tới PDF (tuỳ chọn)        |

