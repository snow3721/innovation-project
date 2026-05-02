-- 高校创新项目管理系统 数据库建表脚本
-- 数据库: innovation_project

CREATE DATABASE IF NOT EXISTS innovation_project DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE innovation_project;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `user_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号：学号/工号',
  `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt加密密码',
  `real_name` VARCHAR(20) NOT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) COMMENT '手机号',
  `email` VARCHAR(50) COMMENT '邮箱',
  `role` ENUM('student','teacher','college_admin','school_admin','expert') NOT NULL COMMENT '系统角色',
  `college_id` INT COMMENT '所属学院ID',
  `major` VARCHAR(50) COMMENT '学生专业',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `idx_username` (`username`),
  KEY `idx_college` (`college_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 学院表
CREATE TABLE IF NOT EXISTS `college` (
  `college_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '学院ID',
  `college_name` VARCHAR(50) NOT NULL COMMENT '学院名称',
  `sort` INT DEFAULT 0 COMMENT '展示排序',
  `status` TINYINT DEFAULT 1,
  UNIQUE KEY `idx_college_name` (`college_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院表';

-- 项目类别表
CREATE TABLE IF NOT EXISTS `project_category` (
  `cat_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '类别ID',
  `cat_name` VARCHAR(30) NOT NULL COMMENT '类别：创新训练/创业训练/创业实践',
  `remark` VARCHAR(200) COMMENT '类别说明',
  UNIQUE KEY `idx_cat_name` (`cat_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目类别表';

-- 项目主表
CREATE TABLE IF NOT EXISTS `project` (
  `project_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '项目唯一ID',
  `project_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `cat_id` INT NOT NULL COMMENT '项目类别ID',
  `leader_id` INT NOT NULL COMMENT '负责人ID',
  `teacher_id` INT COMMENT '指导老师ID',
  `college_id` INT NOT NULL COMMENT '所属学院',
  `apply_year` INT NOT NULL COMMENT '申报年份',
  `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申报时间',
  `total_budget` DECIMAL(10,2) COMMENT '总申请经费',
  `status` ENUM(
    'draft','wait_teacher_audit','wait_college_assign','wait_college_review','wait_college_audit',
    'wait_school_assign','wait_school_review','wait_school_audit','approved','rejected',
    'running','mid_checking','conclude_apply','concluded'
  ) NOT NULL DEFAULT 'draft' COMMENT '项目状态',
  `start_time` DATE COMMENT '项目开始时间',
  `end_time` DATE COMMENT '计划结束时间',
  `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
  KEY `idx_leader` (`leader_id`),
  KEY `idx_teacher` (`teacher_id`),
  KEY `idx_college_year` (`college_id`,`apply_year`),
  KEY `idx_status` (`status`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目主表';

-- 项目成员表
CREATE TABLE IF NOT EXISTS `project_member` (
  `member_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '成员ID',
  `project_id` INT NOT NULL COMMENT '项目ID',
  `user_id` INT NOT NULL COMMENT '学生用户ID',
  `role` ENUM('leader','normal') NOT NULL COMMENT '角色：负责人/成员',
  `join_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `idx_project_user` (`project_id`,`user_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员表';

-- 专家库表
CREATE TABLE IF NOT EXISTS `expert` (
  `expert_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '专家ID',
  `user_id` INT UNIQUE COMMENT '关联系统用户ID',
  `real_name` VARCHAR(20) NOT NULL COMMENT '专家姓名',
  `unit` VARCHAR(100) NOT NULL COMMENT '工作单位',
  `title` VARCHAR(30) COMMENT '职称',
  `research_field` VARCHAR(100) COMMENT '研究方向',
  `is_inner` TINYINT DEFAULT 1 COMMENT '是否校内专家',
  `status` TINYINT DEFAULT 1 COMMENT '状态：1可用 0禁用',
  KEY `idx_field` (`research_field`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专家库表';

-- 专家分配表
CREATE TABLE IF NOT EXISTS `expert_assignment` (
  `assignment_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL COMMENT '项目ID',
  `expert_id` INT NOT NULL COMMENT '专家ID',
  `stage` ENUM('college','school') NOT NULL COMMENT '评审阶段',
  `assign_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `deadline` DATETIME COMMENT '评审截止时间',
  KEY `idx_project` (`project_id`),
  KEY `idx_expert` (`expert_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专家分配表';

-- 评审打分表
CREATE TABLE IF NOT EXISTS `project_review_score` (
  `score_id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '评分ID',
  `project_id` INT NOT NULL COMMENT '项目ID',
  `expert_id` INT NOT NULL COMMENT '专家ID',
  `review_stage` ENUM('college','school') NOT NULL COMMENT '评审阶段：院级/校级',
  `score_innovation` TINYINT COMMENT '创新性得分',
  `score_feasibility` TINYINT COMMENT '可行性得分',
  `score_team` TINYINT COMMENT '团队得分',
  `score_value` TINYINT COMMENT '价值得分',
  `total_score` TINYINT NOT NULL COMMENT '总分',
  `score_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `idx_expert_project` (`project_id`,`expert_id`,`review_stage`),
  KEY `idx_project_stage` (`project_id`,`review_stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评审打分表';

-- 导师审核表
CREATE TABLE IF NOT EXISTS `project_teacher_audit` (
  `audit_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL UNIQUE COMMENT '项目ID',
  `teacher_id` INT NOT NULL,
  `result` ENUM('pass','reject') NOT NULL,
  `audit_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_teacher` (`teacher_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='导师审核表';

-- 学院终审表
CREATE TABLE IF NOT EXISTS `project_college_audit` (
  `audit_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL UNIQUE,
  `admin_id` INT NOT NULL,
  `result` ENUM('pass','reject') NOT NULL,
  `audit_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学院终审表';

-- 学校终审表
CREATE TABLE IF NOT EXISTS `project_school_audit` (
  `audit_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL UNIQUE,
  `admin_id` INT NOT NULL,
  `result` ENUM('pass','reject') NOT NULL,
  `audit_time` DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学校终审表';

-- 里程碑表
CREATE TABLE IF NOT EXISTS `project_milestone` (
  `milestone_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `milestone_name` VARCHAR(50) NOT NULL,
  `plan_time` DATE NOT NULL,
  `actual_time` DATE,
  `status` ENUM('pending','doing','finished','overdue') NOT NULL DEFAULT 'pending',
  `is_warning` TINYINT DEFAULT 0,
  KEY `idx_project` (`project_id`),
  KEY `idx_plan_time` (`plan_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='里程碑表';

-- 中期检查表
CREATE TABLE IF NOT EXISTS `project_mid_check` (
  `mid_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL UNIQUE,
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('waiting','pass','reject') DEFAULT 'waiting'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中期检查表';

-- 成果主表
CREATE TABLE IF NOT EXISTS `project_achievement` (
  `achievement_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL,
  `type` ENUM('patent','paper','software','competition','business','other') NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  `achievement_no` VARCHAR(50) COMMENT '成果编号',
  `publish_time` DATE,
  `status` ENUM('applying','approved','published','landed') NOT NULL,
  KEY `idx_project` (`project_id`),
  KEY `idx_type` (`type`),
  KEY `idx_time` (`publish_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成果主表';

-- 结题验收表
CREATE TABLE IF NOT EXISTS `project_conclude` (
  `conclude_id` INT PRIMARY KEY AUTO_INCREMENT,
  `project_id` INT NOT NULL UNIQUE,
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('waiting','pass','reject') DEFAULT 'waiting'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结题验收表';

-- 附件元数据表
CREATE TABLE IF NOT EXISTS `attachment` (
  `attach_id` INT PRIMARY KEY AUTO_INCREMENT,
  `attach_type` ENUM('apply','mid','conclude','achievement') NOT NULL COMMENT '附件类型',
  `relation_id` INT NOT NULL COMMENT '关联ID：项目ID/成果ID',
  `file_name` VARCHAR(100) NOT NULL,
  `file_size` BIGINT NOT NULL COMMENT '文件大小(Byte)',
  `minio_path` VARCHAR(200) NOT NULL COMMENT 'MinIO存储路径',
  `upload_user` INT NOT NULL,
  `upload_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_relation` (`relation_id`,`attach_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='附件元数据表';

-- 消息表
CREATE TABLE IF NOT EXISTS `message` (
  `message_id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
  `receiver_id` INT NOT NULL COMMENT '接收者用户ID',
  `sender_id` INT COMMENT '发送者用户ID(系统消息为NULL)',
  `title` VARCHAR(100) NOT NULL COMMENT '消息标题',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `type` ENUM('system','audit','review','milestone','achievement') NOT NULL DEFAULT 'system' COMMENT '消息类型',
  `relation_id` INT COMMENT '关联业务ID(项目ID等)',
  `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
  `read_time` DATETIME COMMENT '已读时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY `idx_receiver` (`receiver_id`),
  KEY `idx_type` (`type`),
  KEY `idx_read` (`receiver_id`,`is_read`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息中心表';

-- ============
======== 初始数据 ====================
-- 使用 INSERT IGNORE 避免重复插入
-- 完整示例数据见 data.sql

-- 学院数据
INSERT IGNORE INTO `college` (`college_name`, `sort`) VALUES
('计算机学院', 1),
('电子工程学院', 2),
('机械工程学院', 3),
('化工学院', 4),
('经济管理学院', 5),
('外国语学院', 6),
('数学学院', 7),
('物理学院', 8);

-- 项目类别数据
INSERT IGNORE INTO `project_category` (`cat_name`, `remark`) VALUES
('创新训练', '面向本科生个人或团队，开展创新性研究项目'),
('创业训练', '面向本科生团队，开展创业模拟与实训'),
('创业实践', '面向创业团队，开展真实创业项目实践');

-- 管理员账户(密码: admin123) — 完整数据在 data.sql 中
INSERT IGNORE INTO `user` (`username`, `password`, `real_name`, `role`, `status`) VALUES
('admin', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '系统管理员', 'school_admin', 1);

-- ==================== 数据迁移：新增"待分配"状态 ====================
-- 将已有项目中的 wait_college_review 转为 wait_college_assign（尚未分配专家的项目）
-- 将已有项目中的 wait_school_review 转为 wait_school_assign（尚未分配专家的项目）
-- 注意：已分配了专家且专家已开始评审的项目应保留原状态
-- 以下为全量迁移脚本，适用于无已有评审数据的新部署环境

-- 修改 project 表的 status 枚举，新增 wait_college_assign 和 wait_school_assign
-- ALTER TABLE `project` MODIFY COLUMN `status` ENUM(
--   'draft','wait_teacher_audit','wait_college_assign','wait_college_review','wait_college_audit',
--   'wait_school_assign','wait_school_review','wait_school_audit','approved','rejected',
--   'running','mid_checking','conclude_apply','concluded'
-- ) NOT NULL DEFAULT 'draft' COMMENT '项目状态';

-- 对于已有数据库：将处于 wait_college_review 且无专家分配记录的项目转为 wait_college_assign
-- UPDATE `project` p SET p.status = 'wait_college_assign'
--   WHERE p.status = 'wait_college_review'
--   AND NOT EXISTS (SELECT 1 FROM `expert_assignment` ea WHERE ea.project_id = p.project_id AND ea.stage = 'college');

-- 对于已有数据库：将处于 wait_school_review 且无专家分配记录的项目转为 wait_school_assign
-- UPDATE `project` p SET p.status = 'wait_school_assign'
--   WHERE p.status = 'wait_school_review'
--   AND NOT EXISTS (SELECT 1 FROM `expert_assignment` ea WHERE ea.project_id = p.project_id AND ea.stage = 'school');
