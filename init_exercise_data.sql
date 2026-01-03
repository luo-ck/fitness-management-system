-- 确保training_plan_exercise表存在
CREATE TABLE IF NOT EXISTS `training_plan_exercise` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `plan_id` BIGINT NOT NULL,
    `exercise_id` BIGINT NOT NULL,
    `sets` INT NOT NULL,
    `reps` INT NOT NULL,
    `duration` INT,
    `order_index` INT NOT NULL,
    FOREIGN KEY (`plan_id`) REFERENCES `training_plans`(`plan_id`),
    FOREIGN KEY (`exercise_id`) REFERENCES `exercise`(`exercise_id`),
    INDEX `idx_plan_id` (`plan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 简化的INSERT语句，直接使用VALUES
INSERT IGNORE INTO `exercise` (`exercise_id`, `name`) VALUES
(1, '跑步'),
(2, '游泳'),
(3, '举重'),
(4, '俯卧撑'),
(5, '仰卧起坐'),
(6, '深蹲'),
(7, '引体向上'),
(8, '跳绳');