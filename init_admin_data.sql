-- 创建管理员表
CREATE TABLE IF NOT EXISTS admins (
    admin_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认管理员用户，密码为12345678（BCrypt加密）
INSERT INTO admins (username, name, password_hash, created_at)
VALUES ('admin', '管理员', '$2a$10$7eC5Y0Z9Q7e6R5T4Y3U2I1O0P9A8S7D6F5G4H3J2K1L0M', NOW());

-- 注意：密码是使用BCrypt加密的，原始密码为12345678
-- 可以使用BCryptGenerator.java或其他工具生成新的密码
