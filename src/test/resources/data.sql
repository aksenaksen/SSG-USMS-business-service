INSERT INTO usms_user (id, username, password, person_name, phone_number, email, security_state, is_lock, role)
VALUES (1, 'storeOwner', '$2a$10$L/0.3f0Qm1eQDRQ4IebOD.Y0dpGQl5Xd4Q9TfkbzhJbcYVnqY77iS', 'kyo705', '010-1234-5678', 'email@gmail.com', 0, false, 1),
       (2, 'admin', '$2a$10$L/0.3f0Qm1eQDRQ4IebOD.Y0dpGQl5Xd4Q9TfkbzhJbcYVnqY77iS', 'kyo705', '010-1111-1111', 'email2@gmail.com', 0, false, 0);
--- password : "1234567890a*"
INSERT INTO usms_device (id, user_id , token)
VALUES (1,1,'newtoken');


INSERT INTO store (id, user_id, store_name, store_address, business_license_code, business_license_img_id, store_state)
 VALUES (1, 1, '매장명1', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키1', 0),
        (2, 1, '매장명2', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키2', 1),
        (3, 1, '매장명3', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키3', 0),
        (4, 1, '매장명4', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키4', 1),
        (5, 2, '매장명5', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키5', 0),
        (6, 2, '매장명6', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키6', 2),
        (7, 2, '매장명7', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키7', 0),
        (8, 2, '매장명8', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키8', 2),
        (9, 1, '매장명9', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키9', 0),
        (10, 1, '매장명10', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키10', 1),
        (11, 1, '매장명11', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키11', 0),
        (12, 3, '매장명12', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키12', 2),
        (13, 3, '매장명13', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키13', 0),
        (14, 1, '매장명14', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키14', 3),
        (15, 3, '매장명15', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키15', 0),
        (16, 1, '매장명16', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키16', 2),
        (17, 2, '매장명17', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키17', 0),
        (18, 1, '매장명18', '매장 주소', '098-76-54321', '사업자등록증 사본 파일 키18', 2),
        (19, 2, '매장명19', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키19', 0),
        (20, 1, '매장명20', '매장 주소', '123-45-67890', '사업자등록증 사본 파일 키20', 1);


INSERT INTO cctv (id, store_id, cctv_name, cctv_stream_key, is_expired)
VALUES (1, 1, '우측 상단 CCTV', '74d18bfc-14c5-46d2-a1a8-1eb627918859', false),
       (2, 1, '우측 하단 CCTV', '74d18bfc-14c5-46d2-a1a8-1eb627918851', false),
       (3, 1, '좌측 상단 CCTV', '74d18bfc-14c5-46d2-a3a8-1eb637918859', false),
       (4, 1, '좌측 하단 CCTV', '74d18bfc-14c5-46d2-a2a8-1eb727918859', false),
       (5, 1, '정면 CCTV', '74a13bfc-14c5-46d2-a1a8-1eb627918859', false);

INSERT INTO accident (id, cctv_id, behavior, start_timestamp)
VALUES (1, 1, 1, 1000000000),
       (2, 1, 1, 1500000000),
       (3, 1, 1, 1500000000),
       (4, 1, 1, 1600000000),
       (5, 1, 2, 1700000000),
       (6, 1, 2, 1800000000),
       (7, 1, 2, 1900000000),
       (8, 1, 2, 2000000000),
       (9, 1, 2, 2200000000),
       (10, 2, 3, 3000000000),
       (11, 2, 3, 2500000000),
       (12, 2, 3, 2500000000),
       (13, 2, 3, 2500000000),
       (14, 2, 3, 2500000000),
       (15, 2, 3, 2500000000),
       (16, 3, 4, 3000000000),
       (17, 3, 4, 3000000000),
       (18, 3, 5, 3500000000),
       (19, 3, 5, 3500000000),
       (20, 3, 6, 4000000000);

INSERT INTO region_warning (id, region, behavior, occurrence_count, occurrence_date)
VALUES (1, '서울특별시 강남구', 3, 20, '2024-01-24'),
       (2, '서울특별시 강남구', 4, 33, '2024-01-24'),
       (3, '서울특별시 강동구', 5, 56, '2024-01-24'),
       (4, '서울특별시 강동구', 3, 20, '2024-01-25'),
       (5, '서울특별시 강남구', 4, 20, '2024-01-25'),
       (6, '서울특별시 강동구', 5, 20, '2024-01-25'),
       (7, '서울특별시 강남구', 6, 20, '2024-01-26');