-- folder ordering behavior verification dummy data (using existing DB users)
-- target: /v2/folder, /v2/folder/list
-- order modes:
-- 1) LATEST (or null): folder createdAt desc
-- 2) CUSTOM: folder_order asc, and folders without folder_order fallback to createdAt desc
-- 3) RECENT_ITEM (/v2/folder/list only): latest item createdAt desc per folder,
--    and folders without items fallback to folder createdAt desc

-- pick existing users from current DB
SET @user1_id := (
  SELECT u.user_id
  FROM users u
  ORDER BY u.user_id ASC
  LIMIT 1
);

SET @user2_id := (
  SELECT u.user_id
  FROM users u
  WHERE u.user_id <> @user1_id
  ORDER BY u.user_id ASC
  LIMIT 1
);

-- NOTE: if users table is empty, @user1_id becomes NULL and inserts will not run.

-- cleanup previously inserted dummy folders/items for selected users
DELETE i FROM items i
JOIN folders f ON f.folder_id = i.folder_id
WHERE f.user_id = @user1_id
  AND f.folder_name IN (
    'A_no_custom_old',
    'B_custom_1',
    'C_custom_2',
    'D_no_custom_new',
    'E_custom_3_no_item'
  );

DELETE FROM folders
WHERE user_id = @user1_id
  AND folder_name IN (
    'A_no_custom_old',
    'B_custom_1',
    'C_custom_2',
    'D_no_custom_new',
    'E_custom_3_no_item'
  );

DELETE i FROM items i
JOIN folders f ON f.folder_id = i.folder_id
WHERE f.user_id = @user2_id
  AND f.folder_name = 'Z_other_user_folder';

DELETE FROM folders
WHERE user_id = @user2_id
  AND folder_name = 'Z_other_user_folder';

-- folders for user #1 (existing user)
INSERT INTO folders (folder_name, user_id, folder_order, create_at, update_at)
SELECT 'A_no_custom_old', @user1_id, NULL, '2026-03-01 09:00:00', '2026-03-01 09:00:00'
WHERE @user1_id IS NOT NULL;
SET @folder_a := LAST_INSERT_ID();

INSERT INTO folders (folder_name, user_id, folder_order, create_at, update_at)
SELECT 'B_custom_1', @user1_id, 1, '2026-02-20 09:00:00', '2026-02-20 09:00:00'
WHERE @user1_id IS NOT NULL;
SET @folder_b := LAST_INSERT_ID();

INSERT INTO folders (folder_name, user_id, folder_order, create_at, update_at)
SELECT 'C_custom_2', @user1_id, 2, '2026-02-21 09:00:00', '2026-02-21 09:00:00'
WHERE @user1_id IS NOT NULL;
SET @folder_c := LAST_INSERT_ID();

INSERT INTO folders (folder_name, user_id, folder_order, create_at, update_at)
SELECT 'D_no_custom_new', @user1_id, NULL, '2026-03-05 09:00:00', '2026-03-05 09:00:00'
WHERE @user1_id IS NOT NULL;
SET @folder_d := LAST_INSERT_ID();

INSERT INTO folders (folder_name, user_id, folder_order, create_at, update_at)
SELECT 'E_custom_3_no_item', @user1_id, 3, '2026-02-10 09:00:00', '2026-02-10 09:00:00'
WHERE @user1_id IS NOT NULL;
SET @folder_e := LAST_INSERT_ID();

-- items for user #1 (for RECENT_ITEM sorting)
INSERT INTO items (item_name, item_price, user_id, folder_id, add_type, create_at, update_at)
SELECT 'item_b_1', '10000', @user1_id, @folder_b, 'MANUAL', '2026-03-03 11:00:00', '2026-03-03 11:00:00'
WHERE @user1_id IS NOT NULL;

INSERT INTO items (item_name, item_price, user_id, folder_id, add_type, create_at, update_at)
SELECT 'item_c_1', '20000', @user1_id, @folder_c, 'MANUAL', '2026-03-04 11:00:00', '2026-03-04 11:00:00'
WHERE @user1_id IS NOT NULL;

INSERT INTO items (item_name, item_price, user_id, folder_id, add_type, create_at, update_at)
SELECT 'item_d_1', '30000', @user1_id, @folder_d, 'MANUAL', '2026-03-06 11:00:00', '2026-03-06 11:00:00'
WHERE @user1_id IS NOT NULL;

-- user #2 noise data (if second user exists)
INSERT INTO folders (folder_name, user_id, folder_order, create_at, update_at)
SELECT 'Z_other_user_folder', @user2_id, 1, '2026-03-10 09:00:00', '2026-03-10 09:00:00'
WHERE @user2_id IS NOT NULL;
SET @folder_z := LAST_INSERT_ID();

INSERT INTO items (item_name, item_price, user_id, folder_id, add_type, create_at, update_at)
SELECT 'item_z_1', '99999', @user2_id, @folder_z, 'MANUAL', '2026-03-11 11:00:00', '2026-03-11 11:00:00'
WHERE @user2_id IS NOT NULL;

-- quick verification expectations for user #1
-- LATEST (/v2/folder?order=LATEST): D_no_custom_new, A_no_custom_old, C_custom_2, B_custom_1, E_custom_3_no_item
-- CUSTOM (/v2/folder?order=CUSTOM): B_custom_1, C_custom_2, E_custom_3_no_item, D_no_custom_new, A_no_custom_old
-- RECENT_ITEM (/v2/folder/list?order=RECENT_ITEM): D_no_custom_new, C_custom_2, B_custom_1, A_no_custom_old, E_custom_3_no_item
