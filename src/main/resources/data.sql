-- =========================
-- USERS
-- =========================
INSERT INTO users (created_at, updated_at, name)
VALUES (NOW(), NOW(), '홍길동');

INSERT INTO users (created_at, updated_at, name)
VALUES (NOW(), NOW(), '김철수');

INSERT INTO users (created_at, updated_at, name)
VALUES (NOW(), NOW(), '이영희');

-- =========================
-- POINTS
-- =========================
INSERT INTO points (created_at, updated_at, user_id, balance)
VALUES (NOW(), NOW(), 1, 10000);

INSERT INTO points (created_at, updated_at, user_id, balance)
VALUES (NOW(), NOW(), 2, 5000);

INSERT INTO points (created_at, updated_at, user_id, balance)
VALUES (NOW(), NOW(), 3, 0);

-- =========================
-- MENUS
-- =========================
INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '아메리카노', 3000, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '카페라떼', 4000, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '바닐라라떼', 4500, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '카푸치노', 4300, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '콜드브루', 4200, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '카라멜마끼아또', 4800, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '에스프레소', 2500, 'ON_SALE');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '디카페인 아메리카노', 3500, 'HIDDEN');

INSERT INTO menus (created_at, updated_at, name, price, status)
VALUES (NOW(), NOW(), '돌체라떼', 5000, 'SOLD_OUT');
