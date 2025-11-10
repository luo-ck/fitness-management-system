-- ===============================
use Fitness;
-- 用户表
-- ===============================
CREATE TABLE Users (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    age INT CHECK (age BETWEEN 15 AND 100),
    gender NVARCHAR(10),
    height FLOAT,
    weight FLOAT,
    goal NVARCHAR(20) -- 减脂 / 增肌 / 保持
);

-- ===============================
-- 教练表
-- ===============================
CREATE TABLE Coaches (
    coach_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL,
    specialty NVARCHAR(100),
    contact NVARCHAR(50),
    intro NVARCHAR(MAX), -- 教练简介
    is_verified BIT DEFAULT 0 -- 管理员审核状态
);

-- ===============================
-- 管理员表
-- ===============================
CREATE TABLE Admins (
    admin_id INT IDENTITY(1,1) PRIMARY KEY,
    username NVARCHAR(50) UNIQUE NOT NULL,
    password_hash NVARCHAR(255) NOT NULL
);

-- ===============================
-- 用户-教练关系表
-- ===============================
CREATE TABLE UserCoach (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    coach_id INT FOREIGN KEY REFERENCES Coaches(coach_id),
    start_date DATE DEFAULT GETDATE(),
    is_active BIT DEFAULT 1
);

-- ===============================
-- 教练上传教学视频表
-- ===============================
CREATE TABLE TrainerVideos (
    video_id INT IDENTITY(1,1) PRIMARY KEY,
    coach_id INT FOREIGN KEY REFERENCES Coaches(coach_id),
    title NVARCHAR(100),
    description NVARCHAR(255),
    video_url NVARCHAR(255),
    upload_date DATETIME DEFAULT GETDATE()
);

-- ===============================
-- 训练计划表
-- ===============================
CREATE TABLE TrainingPlans (
    plan_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    coach_id INT FOREIGN KEY REFERENCES Coaches(coach_id),
    plan_date DATE,

);

CREATE TABLE Exercises (
    exercise_id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100),
    description NVARCHAR(255),
    muscle_group NVARCHAR(100)
);

CREATE TABLE TrainingPlanExercise (
    id INT IDENTITY(1,1) PRIMARY KEY,
    plan_id INT FOREIGN KEY REFERENCES TrainingPlans(plan_id),
    exercise_id INT FOREIGN KEY REFERENCES Exercises(exercise_id),
    sets INT,
    reps INT,
    duration INT,
    order_index INT
);

-- ===============================
-- 饮食计划表
-- ===============================
CREATE TABLE MealPlans (
    meal_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    coach_id INT FOREIGN KEY REFERENCES Coaches(coach_id),
    plan_date DATE,
    breakfast NVARCHAR(255),
    lunch NVARCHAR(255),
    dinner NVARCHAR(255),
    total_calories INT
);

-- ===============================
-- 运动打卡表
-- ===============================
CREATE TABLE WorkoutLogs (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    log_date DATE DEFAULT GETDATE(),
    exercise_type NVARCHAR(100),
    duration INT,
    calories_burned INT
);

-- ===============================
-- 训练反馈表
-- ===============================
CREATE TABLE Feedbacks (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    plan_id INT FOREIGN KEY REFERENCES TrainingPlans(plan_id),
    feeling NVARCHAR(20), -- 轻松 / 普通 / 较难 / 困难
    rating INT,
    comments NVARCHAR(MAX)
);

-- ===============================
-- 支付记录表（用户→教练→平台抽成）
-- ===============================
CREATE TABLE Payments (
    payment_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT FOREIGN KEY REFERENCES Users(user_id),
    coach_id INT FOREIGN KEY REFERENCES Coaches(coach_id),
    amount DECIMAL(10,2) NOT NULL,
    platform_fee DECIMAL(10,2) NOT NULL, -- 平台抽成
    coach_earnings AS (amount - platform_fee) PERSISTED,
    payment_date DATETIME DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'Completed' -- Pending / Completed / Failed
);

-- ===============================
-- 每周统计视图
-- ===============================
CREATE VIEW WeeklyWorkoutStats AS
SELECT 
    u.user_id,
    u.name,
    DATEPART(YEAR, w.log_date) AS year,
    DATEPART(WEEK, w.log_date) AS week_num,
    COUNT(DISTINCT w.log_date) AS active_days,
    SUM(w.calories_burned) AS total_calories
FROM WorkoutLogs w
JOIN Users u ON w.user_id = u.user_id
GROUP BY u.user_id, u.name, DATEPART(YEAR, w.log_date), DATEPART(WEEK, w.log_date);
GO

-- ===============================
-- 每月统计视图
-- ===============================
CREATE VIEW MonthlyWorkoutStats AS
SELECT 
    u.user_id,
    u.name,
    DATEPART(YEAR, w.log_date) AS year,
    DATEPART(MONTH, w.log_date) AS month_num,
    COUNT(DISTINCT w.log_date) AS active_days,
    SUM(w.calories_burned) AS total_calories
FROM WorkoutLogs w
JOIN Users u ON w.user_id = u.user_id
GROUP BY u.user_id, u.name, DATEPART(YEAR, w.log_date), DATEPART(MONTH, w.log_date);
GO



