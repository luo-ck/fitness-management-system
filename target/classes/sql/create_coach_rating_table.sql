-- 创建教练评价表
CREATE TABLE IF NOT EXISTS coach_rating (
    rating_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    coach_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id),
    FOREIGN KEY (coach_id) REFERENCES coach(coach_id)
);