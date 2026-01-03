-- 在training_plan_exercise表中添加video_id列
ALTER TABLE training_plan_exercise ADD COLUMN video_id BIGINT NULL COMMENT '教学视频ID';
