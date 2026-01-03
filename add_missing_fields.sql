-- 添加缺少的字段到users表
ALTER TABLE users ADD COLUMN username VARCHAR(50) NOT NULL UNIQUE;
ALTER TABLE users ADD COLUMN password_hash VARCHAR(255) NOT NULL;

-- 添加缺少的字段到coaches表
ALTER TABLE coaches ADD COLUMN username VARCHAR(50) NOT NULL UNIQUE;
ALTER TABLE coaches ADD COLUMN password_hash VARCHAR(255) NOT NULL;
