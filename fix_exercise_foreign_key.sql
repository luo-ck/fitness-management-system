-- 修改training_plan_exercise表的外键约束，从exercises表改为exercise表

-- 1. 首先删除现有外键约束
ALTER TABLE training_plan_exercise DROP FOREIGN KEY training_plan_exercise_ibfk_2;

-- 2. 然后添加新的外键约束，指向exercise表
ALTER TABLE training_plan_exercise ADD CONSTRAINT training_plan_exercise_ibfk_2 FOREIGN KEY (exercise_id) REFERENCES exercise(exercise_id);

-- 3. 最后删除多余的exercises表
DROP TABLE exercises;
