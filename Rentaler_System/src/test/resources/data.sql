-- 测试环境初始化数据
-- 使用 INSERT IGNORE 避免重复插入错误

-- ========== 组织架构 ==========
INSERT IGNORE INTO organization (id, name, parent_ids, type, parent_id) VALUES (1, '根节点', '/', 0, null);
INSERT IGNORE INTO organization (id, name, parent_ids, type, parent_id) VALUES (2, '测试组织1', '/1/', 0, 1);
INSERT IGNORE INTO organization (id, name, parent_ids, type, parent_id) VALUES (3, '测试组织2', '/1/', 0, 1);

-- ========== 资源权限（完整列表）==========
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (1, null, '根节点', null, '*', null, null, null);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (2, 'Odometer', '仪表盘', null, 'dashboard', 0, '/dashboard', 1);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (3, 'SetUp', '系统管理', null, 'sys', 0, '/sys', 1);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (4, 'User', '用户管理', null, 'user:view', 0, '/users', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (5, 'Tickets', '角色管理', null, 'role:view', 0, '/roles', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (6, 'Collection', '权限资源', null, 'resource:view', 0, '/resources', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (7, null, '查看用户', null, 'user:view', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (8, null, '新增用户', null, 'user:create', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (9, null, '修改用户', null, 'user:update', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (10, null, '删除用户', null, 'user:delete', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (11, null, '查看角色', null, 'role:view', 1, null, 5);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (12, null, '新增角色', null, 'role:create', 1, null, 5);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (13, null, '修改角色', null, 'role:update', 1, null, 5);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (14, null, '删除角色', null, 'role:delete', 1, null, 5);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (15, null, '查看资源', null, 'resource:view', 1, null, 6);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (16, null, '新增资源', null, 'resource:create', 1, null, 6);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (17, null, '修改资源', null, 'resource:update', 1, null, 6);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (18, null, '删除资源', null, 'resource:delete', 1, null, 6);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (19, null, '新增组织架构', null, 'organization:create', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (20, null, '修改组织架构', null, 'organization:update', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (21, null, '删除组织架构', null, 'organization:delete', 1, null, 4);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (22, 'Timer', '操作日志', null, 'log:view', 0, '/logs', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (23, null, '清空日志', null, 'log:clean', 1, null, 22);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (24, null, '查看日志', null, 'log:view', 1, null, 22);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (25, 'Files', '对象存储', null, 'storage:view', 0, '/storage', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (26, null, '查看对象存储', null, 'storage:view', 1, null, 25);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (27, null, '新增对象存储', null, 'storage:create', 1, null, 25);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (28, null, '更新对象存储', null, 'storage:update', 1, null, 25);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (29, null, '删除对象存储', null, 'storage:delete', 1, null, 25);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (30, null, '设置默认存储', null, 'storage:markAsDefault', 1, null, 25);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (32, 'VideoCameraFilled', 'GPU设备管理', null, 'gpu', 0, '/gpu', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (35, null, '删除GPU设备', null, 'gpu:release', 1, null, 32);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (36, null, '查看GPU设备', null, 'gpu:view', 1, null, 32);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (37, null, '修改GPU设备信息', null, 'gpu:modify', 1, null, 32);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (41, 'Collection', '服务器管理', null, 'server:view', 0, '/server', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (42, null, '查看服务器', null, 'server:view', 1, null, 41);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (43, null, '更新服务器', null, 'server:modify', 1, null, 41);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (44, null, '删除服务器', null, 'server:delete', 1, null, 41);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (48, 'Document', '沙盒环境', null, 'sandbox', 0, '', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (50, null, '沙盒充值', null, 'sandbox:wallet:recharge', 1, null, 48);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (61, 'Collection', '任务管理', null, 'task:all', 0, '/task', 3);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (62, null, '查看任务', null, 'task:all', 1, null, 61);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (63, 'Coin', '应用中心', null, 'application', 0, '', 1);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (64, 'Reading', '租借GPU设备', null, 'gpu:isRentable:view', 0, '/rent', 63);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (65, null, '查看可租借GPU设备', null, 'gpu:isRentable:view', 1, null, 64);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (67, null, '租借GPU设备', null, 'gpu:lease', 1, null, 64);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (68, 'Collection', '我的任务', null, 'task:me', 0, '/my/task', 63);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (69, null, '查看任务', null, 'task:me', 1, null, 68);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (70, null, '任务日志', null, 'task:log', 1, null, 68);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (71, null, '完成任务', null, 'task:finish', 1, null, 68);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (72, null, '导出数据', null, 'task:data:export', 1, null, 68);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (73, 'Collection', '我的钱包', null, 'wallet:my', 0, '/wallet', 63);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (74, null, '查看钱包', null, 'wallet:my', 1, null, 73);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (75, 'Files', '我的镜像', null, 'dockerImage:me', 0, '/docker/image', 63);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (76, null, '查看镜像', null, 'dockerImage:me', 1, null, 75);
INSERT IGNORE INTO resource (id, icon, name, parent_ids, permission, type, url, parent_id) VALUES (77, null, '上传镜像', null, 'dockerImage:upload', 1, null, 75);

-- ========== 角色 ==========
INSERT IGNORE INTO role (id, available, description, name) VALUES (1, true, '测试管理员角色', '管理员');
INSERT IGNORE INTO role (id, available, description, name) VALUES (2, true, '测试普通用户角色', '普通用户');

-- ========== 角色资源关联（管理员拥有所有权限）==========
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19), (1, 20);
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (1, 21), (1, 22), (1, 23), (1, 24), (1, 25), (1, 26), (1, 27), (1, 28), (1, 29), (1, 30);
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (1, 32), (1, 35), (1, 36), (1, 37), (1, 41), (1, 42), (1, 43), (1, 44), (1, 48), (1, 50);
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (1, 61), (1, 62), (1, 63), (1, 64), (1, 65), (1, 67), (1, 68), (1, 69), (1, 70), (1, 71);
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (1, 72), (1, 73), (1, 74), (1, 75), (1, 76), (1, 77);

-- 普通用户只有应用中心相关权限
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (2, 2), (2, 63), (2, 64), (2, 65), (2, 67), (2, 68), (2, 69), (2, 70), (2, 71), (2, 72);
INSERT IGNORE INTO role_resource (role_id, resource_id) VALUES (2, 73), (2, 74), (2, 75), (2, 76), (2, 77);

-- ========== 测试用户 ==========
INSERT IGNORE INTO user (id, avatar, created_time, gender, state, username, organization_id)
VALUES (1, 'avatar.jpg', '2023-01-05 17:16:11.000000', 0, 0, 'testadmin', 1);
INSERT IGNORE INTO user (id, avatar, created_time, gender, state, username, organization_id)
VALUES (2, 'avatar.jpg', '2023-01-05 17:16:11.000000', 0, 0, 'testuser', 2);

-- ========== 用户凭证（密码都是: 123456，MD5加密）==========
INSERT IGNORE INTO user_credential (id, credential, identifier, identity_type, user_id)
VALUES (1, 'a66abb5684c45962d887564f08346e8d', 'testadmin', 0, 1);
INSERT IGNORE INTO user_credential (id, credential, identifier, identity_type, user_id)
VALUES (2, 'a66abb5684c45962d887564f08346e8d', 'testuser', 0, 2);

-- ========== 用户角色关联 ==========
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (1, 1);
INSERT IGNORE INTO user_role (user_id, role_id) VALUES (2, 2);

-- ========== 测试Session（自动登录用）==========
-- 管理员Session（拥有所有权限）
INSERT IGNORE INTO session (id, data, expire_time, last_login_time, last_modified_time, token, credential_id)
VALUES (1,
    '{"token":"test_admin_token_123456","userId":1,"username":"testadmin","avatar":"avatar.jpg","credential":{"identifier":"testadmin","type":"PASSWORD"},"permissions":["*","dashboard","sys","user:view","user:create","user:update","user:delete","role:view","role:create","role:update","role:delete","resource:view","resource:create","resource:update","resource:delete","organization:create","organization:update","organization:delete","log:view","log:clean","storage:view","storage:create","storage:update","storage:delete","storage:markAsDefault","gpu","gpu:release","gpu:view","gpu:modify","server:view","server:modify","server:delete","sandbox","sandbox:wallet:recharge","task:all","application","gpu:isRentable:view","gpu:lease","task:me","task:log","task:finish","task:data:export","wallet:my","dockerImage:me","dockerImage:upload"]}',
    '2099-12-31 23:59:59',
    NOW(),
    NOW(),
    'test_admin_token_123456',
    1);

-- 普通用户Session（只有应用中心相关权限）
INSERT IGNORE INTO session (id, data, expire_time, last_login_time, last_modified_time, token, credential_id)
VALUES (2,
    '{"token":"test_user_token_123456","userId":2,"username":"testuser","avatar":"avatar.jpg","credential":{"identifier":"testuser","type":"PASSWORD"},"permissions":["dashboard","application","gpu:isRentable:view","gpu:lease","task:me","task:log","task:finish","task:data:export","wallet:my","dockerImage:me","dockerImage:upload"]}',
    '2099-12-31 23:59:59',
    NOW(),
    NOW(),
    'test_user_token_123456',
    2);

-- ========== 测试服务器数据 ==========
INSERT IGNORE INTO servers (id, hostname, ip_address, location, cpu_model, cpu_cores, ram_total_gb, storage_total_gb, gpu_slots, status, bandwidth_mbps, datacenter, region)
VALUES
(1, 'gpu-server-001', '192.168.1.101', 'Beijing', 'Intel Xeon Gold 6248R', 48, 256, 2000, 8, 'ACTIVE', 10000, 'DC-Beijing-01', 'China-North'),
(2, 'gpu-server-002', '192.168.1.102', 'Shanghai', 'AMD EPYC 7763', 64, 512, 4000, 8, 'ACTIVE', 10000, 'DC-Shanghai-01', 'China-East'),
(3, 'gpu-server-003', '192.168.1.103', 'Guangzhou', 'Intel Xeon Platinum 8358', 64, 512, 4000, 4, 'ACTIVE', 10000, 'DC-Guangzhou-01', 'China-South'),
(4, 'gpu-server-004', '192.168.1.104', 'Shenzhen', 'AMD EPYC 7713', 64, 256, 2000, 4, 'MAINTENANCE', 10000, 'DC-Shenzhen-01', 'China-South'),
(5, 'gpu-server-005', '192.168.1.105', 'Hangzhou', 'Intel Xeon Gold 6248R', 48, 256, 2000, 8, 'ACTIVE', 10000, 'DC-Hangzhou-01', 'China-East');

-- ========== GPU 真实设备数据 ==========
-- Server 1 的 GPU 设备 (8x RTX 4090)
INSERT IGNORE INTO gpu_real_devices (id, real_device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, total_runtime_hours, total_revenue, rental_count)
VALUES
(1, 'GPU-SRV1-0', 1, 0, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(2, 'GPU-SRV1-1', 1, 1, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(3, 'GPU-SRV1-2', 1, 2, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(4, 'GPU-SRV1-3', 1, 3, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(5, 'GPU-SRV1-4', 1, 4, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'IN_USE', true, 120.5, 603.00, 3),
(6, 'GPU-SRV1-5', 1, 5, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'IN_USE', true, 85.0, 425.00, 2),
(7, 'GPU-SRV1-6', 1, 6, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(8, 'GPU-SRV1-7', 1, 7, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0);

-- Server 2 的 GPU 设备 (8x A100)
INSERT IGNORE INTO gpu_real_devices (id, real_device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, total_runtime_hours, total_revenue, rental_count)
VALUES
(9, 'GPU-SRV2-0', 2, 0, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 0.0, 0.00, 0),
(10, 'GPU-SRV2-1', 2, 1, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 0.0, 0.00, 0),
(11, 'GPU-SRV2-2', 2, 2, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 0.0, 0.00, 0),
(12, 'GPU-SRV2-3', 2, 3, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 0.0, 0.00, 0),
(13, 'GPU-SRV2-4', 2, 4, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'IN_USE', true, 256.3, 2563.00, 5),
(14, 'GPU-SRV2-5', 2, 5, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'IN_USE', true, 180.0, 1800.00, 4),
(15, 'GPU-SRV2-6', 2, 6, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 0.0, 0.00, 0),
(16, 'GPU-SRV2-7', 2, 7, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 0.0, 0.00, 0);

-- Server 3 的 GPU 设备 (4x RTX 3090)
INSERT IGNORE INTO gpu_real_devices (id, real_device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, total_runtime_hours, total_revenue, rental_count)
VALUES
(17, 'GPU-SRV3-0', 3, 0, 'NVIDIA', 'RTX 3090', 'Ampere', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(18, 'GPU-SRV3-1', 3, 1, 'NVIDIA', 'RTX 3090', 'Ampere', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0),
(19, 'GPU-SRV3-2', 3, 2, 'NVIDIA', 'RTX 3090', 'Ampere', 24576, 'GDDR6X', 'IN_USE', true, 50.0, 200.00, 1),
(20, 'GPU-SRV3-3', 3, 3, 'NVIDIA', 'RTX 3090', 'Ampere', 24576, 'GDDR6X', 'AVAILABLE', true, 0.0, 0.00, 0);

-- Server 4 的 GPU 设备 (4x V100，维护状态)
INSERT IGNORE INTO gpu_real_devices (id, real_device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, total_runtime_hours, total_revenue, rental_count)
VALUES
(21, 'GPU-SRV4-0', 4, 0, 'NVIDIA', 'V100', 'Volta', 32768, 'HBM2', 'MAINTENANCE', false, 350.0, 2800.00, 8),
(22, 'GPU-SRV4-1', 4, 1, 'NVIDIA', 'V100', 'Volta', 32768, 'HBM2', 'MAINTENANCE', false, 420.0, 3360.00, 10),
(23, 'GPU-SRV4-2', 4, 2, 'NVIDIA', 'V100', 'Volta', 32768, 'HBM2', 'MAINTENANCE', false, 380.0, 3040.00, 9),
(24, 'GPU-SRV4-3', 4, 3, 'NVIDIA', 'V100', 'Volta', 32768, 'HBM2', 'MAINTENANCE', false, 300.0, 2400.00, 7);

-- Server 5 的 GPU 设备 (8x H100)
INSERT IGNORE INTO gpu_real_devices (id, real_device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, total_runtime_hours, total_revenue, rental_count)
VALUES
(25, 'GPU-SRV5-0', 5, 0, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(26, 'GPU-SRV5-1', 5, 1, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(27, 'GPU-SRV5-2', 5, 2, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(28, 'GPU-SRV5-3', 5, 3, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(29, 'GPU-SRV5-4', 5, 4, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(30, 'GPU-SRV5-5', 5, 5, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(31, 'GPU-SRV5-6', 5, 6, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0),
(32, 'GPU-SRV5-7', 5, 7, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 0.0, 0.00, 0);

-- ========== GPU 虚拟设备数据（可租用）==========
-- RTX 4090 设备（$5.00/小时）
INSERT IGNORE INTO gpu_devices (id, device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, hourly_rate, total_runtime_hours, total_revenue, rental_count)
VALUES
(1, 'DEV-4090-001', 1, 0, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 5.00, 0.0, 0.00, 0),
(2, 'DEV-4090-002', 1, 1, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 5.00, 0.0, 0.00, 0),
(3, 'DEV-4090-003', 1, 2, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 5.00, 0.0, 0.00, 0),
(4, 'DEV-4090-004', 1, 3, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'AVAILABLE', true, 5.00, 0.0, 0.00, 0);

-- A100 设备（$10.00/小时）
INSERT IGNORE INTO gpu_devices (id, device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, hourly_rate, total_runtime_hours, total_revenue, rental_count)
VALUES
(5, 'DEV-A100-001', 2, 0, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 10.00, 0.0, 0.00, 0),
(6, 'DEV-A100-002', 2, 1, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 10.00, 0.0, 0.00, 0),
(7, 'DEV-A100-003', 2, 2, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 10.00, 0.0, 0.00, 0),
(8, 'DEV-A100-004', 2, 3, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'AVAILABLE', true, 10.00, 0.0, 0.00, 0);

-- RTX 3090 设备（$4.00/小时）
INSERT IGNORE INTO gpu_devices (id, device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, hourly_rate, total_runtime_hours, total_revenue, rental_count)
VALUES
(9, 'DEV-3090-001', 3, 0, 'NVIDIA', 'RTX 3090', 'Ampere', 24576, 'GDDR6X', 'AVAILABLE', true, 4.00, 0.0, 0.00, 0),
(10, 'DEV-3090-002', 3, 1, 'NVIDIA', 'RTX 3090', 'Ampere', 24576, 'GDDR6X', 'AVAILABLE', true, 4.00, 0.0, 0.00, 0);

-- H100 设备（$15.00/小时）
INSERT IGNORE INTO gpu_devices (id, device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, hourly_rate, total_runtime_hours, total_revenue, rental_count)
VALUES
(11, 'DEV-H100-001', 5, 0, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 15.00, 0.0, 0.00, 0),
(12, 'DEV-H100-002', 5, 1, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 15.00, 0.0, 0.00, 0),
(13, 'DEV-H100-003', 5, 2, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 15.00, 0.0, 0.00, 0),
(14, 'DEV-H100-004', 5, 3, 'NVIDIA', 'H100', 'Hopper', 81920, 'HBM3', 'AVAILABLE', true, 15.00, 0.0, 0.00, 0);

-- 已租用的设备（用于测试）
INSERT IGNORE INTO gpu_devices (id, device_id, server_id, device_index, brand, model, architecture, memory_total, memory_type, status, is_rentable, hourly_rate, total_runtime_hours, total_revenue, rental_count)
VALUES
(15, 'DEV-4090-005', 1, 4, 'NVIDIA', 'RTX 4090', 'Ada Lovelace', 24576, 'GDDR6X', 'IN_USE', true, 5.00, 120.5, 603.00, 3),
(16, 'DEV-A100-005', 2, 4, 'NVIDIA', 'A100', 'Ampere', 40960, 'HBM2e', 'IN_USE', true, 10.00, 256.3, 2563.00, 5);
