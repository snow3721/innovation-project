-- 高校创新项目管理系统 示例数据
-- 密码统一为: admin123 (BCrypt加密)
-- $2a$10$ERRRSjcbtYBPdVnbMVriYuq6HlIXLkzvt18hNgrYRF13G086d/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.

USE innovation_project;

SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 用户数据 ====================
INSERT IGNORE INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `college_id`, `major`, `status`) VALUES
-- 管理员
('admin', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '系统管理员', '13800000001', 'admin@innovation.edu.cn', 'school_admin', NULL, NULL, 1),
('college_admin1', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '张建国', '13800000002', 'zhangjg@innovation.edu.cn', 'college_admin', 1, NULL, 1),
('college_admin2', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '李明辉', '13800000003', 'limh@innovation.edu.cn', 'college_admin', 2, NULL, 1),
('college_admin3', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '王秀芳', '13800000004', 'wangxf@innovation.edu.cn', 'college_admin', 3, NULL, 1),

-- 指导老师
('teacher1', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '陈教授', '13800000010', 'chenjs@innovation.edu.cn', 'teacher', 1, NULL, 1),
('teacher2', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '刘教授', '13800000011', 'liujs@innovation.edu.cn', 'teacher', 1, NULL, 1),
('teacher3', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '赵教授', '13800000012', 'zhaojs@innovation.edu.cn', 'teacher', 2, NULL, 1),
('teacher4', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '孙教授', '13800000013', 'sunjs@innovation.edu.cn', 'teacher', 3, NULL, 1),
('teacher5', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '周教授', '13800000014', 'zhoujs@innovation.edu.cn', 'teacher', 5, NULL, 1),

-- 学生
('2021001001', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '王小明', '13900001001', 'wangxm@stu.innovation.edu.cn', 'student', 1, '计算机科学与技术', 1),
('2021001002', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '李小红', '13900001002', 'lixh@stu.innovation.edu.cn', 'student', 1, '软件工程', 1),
('2021001003', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '张小华', '13900001003', 'zhangxh@stu.innovation.edu.cn', 'student', 1, '人工智能', 1),
('2021001004', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '刘小刚', '13900001004', 'liuxg@stu.innovation.edu.cn', 'student', 1, '数据科学', 1),
('2021002001', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '陈小丽', '13900002001', 'chenxl@stu.innovation.edu.cn', 'student', 2, '电子信息工程', 1),
('2021002002', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '杨小伟', '13900002002', 'yangxw@stu.innovation.edu.cn', 'student', 2, '通信工程', 1),
('2021002003', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '吴小芳', '13900002003', 'wuxf@stu.innovation.edu.cn', 'student', 2, '微电子学', 1),
('2021003001', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '黄小龙', '13900003001', 'huangxl@stu.innovation.edu.cn', 'student', 3, '机械设计制造', 1),
('2021003002', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '林小梅', '13900003002', 'linxm@stu.innovation.edu.cn', 'student', 3, '自动化', 1),
('2021005001', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '郑小强', '13900005001', 'zhengxq@stu.innovation.edu.cn', 'student', 5, '工商管理', 1),
('2021005002', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '马小云', '13900005002', 'maxy@stu.innovation.edu.cn', 'student', 5, '市场营销', 1),
('2021007001', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '何小文', '13900007001', 'hexw@stu.innovation.edu.cn', 'student', 7, '应用数学', 1),
('2020001001', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '赵小天', '13900010001', 'zhaoxt@stu.innovation.edu.cn', 'student', 1, '计算机科学与技术', 1),
('2020001002', '$2a$10$N/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '钱小雨', '13900010002', 'qianxy@stu.innovation.edu.cn', 'student', 1, '网络工程', 1);

-- ==================== 专家数据 ====================
INSERT IGNORE INTO `expert` (`user_id`, `real_name`, `unit`, `title`, `research_field`, `is_inner`, `status`) VALUES
(NULL, '张院士', '清华大学', '教授/院士', '人工智能', 0, 1),
(NULL, '李博导', '北京大学', '教授/博导', '大数据分析', 0, 1),
(NULL, '王研究员', '中国科学院', '研究员', '物联网技术', 0, 1),
(6, '陈教授', '本校计算机学院', '教授', '机器学习', 1, 1),
(7, '刘教授', '本校计算机学院', '教授', '软件工程', 1, 1),
(8, '赵教授', '本校电子工程学院', '教授', '信号处理', 1, 1),
(9, '孙教授', '本校机械工程学院', '教授', '智能制造', 1, 1),
(NULL, '吴教授', '浙江大学', '教授', '深度学习', 0, 1),
(NULL, '郑教授', '复旦大学', '教授', '区块链技术', 0, 1),
(NULL, '冯研究员', '华为研究院', '高级研究员', '云计算', 0, 1);

-- ==================== 项目数据 ====================
INSERT IGNORE INTO `project` (`project_name`, `cat_id`, `leader_id`, `teacher_id`, `college_id`, `apply_year`, `total_budget`, `status`, `start_time`, `end_time`) VALUES
-- 已结题项目 (2023年)
('基于深度学习的图像识别系统', 1, 14, 6, 1, 2023, 15000.00, 'concluded', '2023-03-01', '2024-03-01'),
('智能校园导航APP开发', 1, 15, 7, 1, 2023, 12000.00, 'concluded', '2023-03-01', '2024-03-01'),
('物联网环境监测节点设计', 1, 6, 8, 2, 2023, 18000.00, 'concluded', '2023-04-01', '2024-04-01'),

-- 运行中项目 (2024年)
('基于大模型的智能问答助手', 1, 6, 6, 1, 2024, 20000.00, 'running', '2024-03-01', '2025-03-01'),
('区块链存证平台设计', 1, 2, 7, 1, 2024, 16000.00, 'running', '2024-03-15', '2025-03-15'),
('5G边缘计算资源调度研究', 1, 7, 8, 2, 2024, 22000.00, 'running', '2024-04-01', '2025-04-01'),
('智能制造产线优化系统', 1, 9, 9, 3, 2024, 18000.00, 'running', '2024-04-15', '2025-04-15'),
('大学生校园二手交易平台', 2, 11, 10, 5, 2024, 10000.00, 'running', '2024-05-01', '2025-05-01'),

-- 中期检查中项目
('基于联邦学习的隐私计算框架', 1, 3, 6, 1, 2024, 25000.00, 'mid_checking', '2024-03-01', '2025-06-01'),
('AI辅助医学影像诊断系统', 1, 8, 8, 2, 2024, 28000.00, 'mid_checking', '2024-04-01', '2025-06-30'),

-- 已批准项目 (2025年)
('基于多模态大模型的内容审核系统', 1, 1, 6, 1, 2025, 30000.00, 'approved', '2025-03-01', '2026-03-01'),
('智能机器人巡检系统研发', 1, 9, 9, 3, 2025, 25000.00, 'approved', '2025-03-15', '2026-03-15'),
('绿色能源管理物联网平台', 1, 7, 8, 2, 2025, 22000.00, 'approved', '2025-04-01', '2026-04-01'),

-- 审核中项目 (2025年)
('基于RAG的法律咨询助手', 1, 4, 7, 1, 2025, 18000.00, 'wait_school_review', '2025-03-01', '2026-03-01'),
('校园跑腿小程序创业项目', 2, 12, 10, 5, 2025, 8000.00, 'wait_college_review', '2025-04-01', '2026-04-01'),
('数字孪生校园可视化平台', 1, 5, 6, 1, 2025, 26000.00, 'wait_teacher_audit', '2025-05-01', '2026-05-01'),
('量子通信仿真实验系统', 1, 13, 8, 2, 2025, 20000.00, 'wait_college_audit', '2025-04-01', '2026-04-01'),

-- 草稿项目
('AI绘画创作平台', 1, 3, NULL, 1, 2025, 15000.00, 'draft', NULL, NULL),

-- 被驳回项目
('校园外卖配送平台', 2, 11, 10, 5, 2024, 12000.00, 'rejected', NULL, NULL),
('简易计算器创新项目', 1, 5, NULL, 1, 2024, 5000.00, 'rejected', NULL, NULL),

-- 结题申请中
('智能停车管理系统', 1, 14, 7, 1, 2024, 16000.00, 'conclude_apply', '2024-03-01', '2025-03-01');

-- ==================== 项目成员数据 ====================
INSERT IGNORE INTO `project_member` (`project_id`, `user_id`, `role`) VALUES
-- 项目4: 基于大模型的智能问答助手 (leader=6)
(4, 6, 'leader'), (4, 1, 'normal'), (4, 2, 'normal'), (4, 3, 'normal'),
-- 项目5: 区块链存证平台设计 (leader=2)
(5, 2, 'leader'), (5, 4, 'normal'), (5, 14, 'normal'),
-- 项目6: 5G边缘计算资源调度研究 (leader=7)
(6, 7, 'leader'), (6, 8, 'normal'),
-- 项目7: 智能制造产线优化系统 (leader=9)
(7, 9, 'leader'), (7, 10, 'normal'),
-- 项目8: 大学生校园二手交易平台 (leader=11)
(8, 11, 'leader'), (8, 12, 'normal'),
-- 项目9: 基于联邦学习的隐私计算框架 (leader=3)
(9, 3, 'leader'), (9, 1, 'normal'), (9, 4, 'normal'),
-- 项目10: AI辅助医学影像诊断系统 (leader=8)
(10, 8, 'leader'), (10, 7, 'normal'),
-- 项目11: 基于多模态大模型的内容审核系统 (leader=1)
(11, 1, 'leader'), (11, 2, 'normal'), (11, 3, 'normal'), (11, 4, 'normal'),
-- 项目12: 智能机器人巡检系统研发 (leader=9)
(12, 9, 'leader'), (12, 10, 'normal'),
-- 项目13: 绿色能源管理物联网平台 (leader=7)
(13, 7, 'leader'), (13, 8, 'normal'), (13, 13, 'normal'),
-- 项目1: 基于深度学习的图像识别系统 (已结题)
(1, 14, 'leader'), (1, 15, 'normal'), (1, 1, 'normal'),
-- 项目2: 智能校园导航APP开发 (已结题)
(2, 15, 'leader'), (2, 14, 'normal'),
-- 项目3: 物联网环境监测节点设计 (已结题)
(3, 6, 'leader'), (3, 7, 'normal'),
-- 项目14: 基于RAG的法律咨询助手
(14, 4, 'leader'), (14, 1, 'normal'),
-- 项目15: 校园跑腿小程序创业项目
(15, 12, 'leader'), (15, 11, 'normal'),
-- 项目16: 数字孪生校园可视化平台
(16, 5, 'leader'), (16, 2, 'normal'), (16, 3, 'normal'),
-- 项目19: 智能停车管理系统
(19, 14, 'leader'), (19, 15, 'normal');

-- ==================== 导师审核数据 ====================
INSERT IGNORE INTO `project_teacher_audit` (`project_id`, `teacher_id`, `result`) VALUES
(1, 6, 'pass'), (2, 7, 'pass'), (3, 8, 'pass'),
(4, 6, 'pass'), (5, 7, 'pass'), (6, 8, 'pass'), (7, 9, 'pass'), (8, 10, 'pass'),
(9, 6, 'pass'), (10, 8, 'pass'),
(11, 6, 'pass'), (12, 9, 'pass'), (13, 8, 'pass'),
(14, 7, 'pass'), (16, 6, 'pass'),
(17, 8, 'pass'), (19, 7, 'pass');

-- ==================== 学院终审数据 ====================
INSERT IGNORE INTO `project_college_audit` (`project_id`, `admin_id`, `result`) VALUES
(1, 2, 'pass'), (2, 2, 'pass'), (3, 3, 'pass'),
(4, 2, 'pass'), (5, 2, 'pass'), (6, 3, 'pass'), (7, 4, 'pass'), (8, 2, 'pass'),
(9, 2, 'pass'), (10, 3, 'pass'),
(11, 2, 'pass'), (12, 4, 'pass'), (13, 3, 'pass'),
(14, 2, 'pass'), (17, 3, 'pass'), (19, 2, 'pass');

-- ==================== 学校终审数据 ====================
INSERT IGNORE INTO `project_school_audit` (`project_id`, `admin_id`, `result`) VALUES
(1, 1, 'pass'), (2, 1, 'pass'), (3, 1, 'pass'),
(4, 1, 'pass'), (5, 1, 'pass'), (6, 1, 'pass'), (7, 1, 'pass'), (8, 1, 'pass'),
(9, 1, 'pass'), (10, 1, 'pass'),
(11, 1, 'pass'), (12, 1, 'pass'), (13, 1, 'pass');

-- ==================== 专家分配数据 ====================
INSERT IGNORE INTO `expert_assignment` (`project_id`, `expert_id`, `stage`) VALUES
-- 院级评审
(4, 4, 'college'), (4, 5, 'college'),
(5, 5, 'college'), (5, 6, 'college'),
(6, 6, 'college'), (6, 7, 'college'),
(7, 7, 'college'), (7, 4, 'college'),
(8, 5, 'college'), (8, 10, 'college'),
-- 校级评审
(4, 1, 'school'), (4, 2, 'school'),
(5, 2, 'school'), (5, 3, 'school'),
(6, 3, 'school'), (6, 8, 'school'),
(7, 8, 'school'), (7, 1, 'school'),
(8, 9, 'school'), (8, 2, 'school'),
-- 2025年项目
(11, 1, 'school'), (11, 4, 'school'), (11, 5, 'school'),
(12, 3, 'school'), (12, 7, 'school'),
(13, 2, 'school'), (13, 6, 'school');

-- ==================== 评审打分数据 ====================
INSERT IGNORE INTO `project_review_score` (`project_id`, `expert_id`, `review_stage`, `score_innovation`, `score_feasibility`, `score_team`, `score_value`, `total_score`) VALUES
-- 项目4: 基于大模型的智能问答助手 (院级)
(4, 4, 'college', 23, 22, 20, 21, 86),
(4, 5, 'college', 24, 21, 22, 20, 87),
-- 项目4 校级
(4, 1, 'school', 25, 24, 22, 23, 94),
(4, 2, 'school', 24, 23, 21, 22, 90),
-- 项目5: 区块链存证平台设计 (院级)
(5, 5, 'college', 20, 21, 19, 18, 78),
(5, 6, 'college', 21, 22, 20, 19, 82),
-- 项目5 校级
(5, 2, 'school', 22, 23, 21, 20, 86),
(5, 3, 'school', 23, 22, 20, 21, 86),
-- 项目6: 5G边缘计算 (院级)
(6, 6, 'college', 22, 24, 21, 23, 90),
(6, 7, 'college', 21, 23, 20, 22, 86),
-- 项目6 校级
(6, 3, 'school', 24, 25, 22, 23, 94),
(6, 8, 'school', 23, 24, 21, 22, 90),
-- 项目7: 智能制造 (院级)
(7, 7, 'college', 21, 23, 22, 20, 86),
(7, 4, 'college', 22, 22, 21, 21, 86),
-- 项目7 校级
(7, 8, 'school', 23, 24, 22, 21, 90),
(7, 1, 'school', 24, 23, 23, 22, 92),
-- 项目8: 二手交易平台 (院级)
(8, 5, 'college', 18, 20, 19, 21, 78),
(8, 10, 'college', 19, 21, 20, 22, 82),
-- 项目8 校级
(8, 9, 'school', 20, 22, 21, 23, 86),
(8, 2, 'school', 21, 23, 20, 22, 86),
-- 项目11: 多模态内容审核 (校级)
(11, 1, 'school', 25, 24, 23, 24, 96),
(11, 4, 'school', 24, 23, 22, 23, 92),
(11, 5, 'school', 23, 22, 21, 22, 88),
-- 项目12: 机器人巡检 (校级)
(12, 3, 'school', 22, 24, 21, 20, 87),
(12, 7, 'school', 23, 23, 22, 21, 89),
-- 项目13: 绿色能源 (校级)
(13, 2, 'school', 21, 23, 20, 22, 86),
(13, 6, 'school', 22, 22, 21, 23, 88);

-- ==================== 里程碑数据 ====================
INSERT IGNORE INTO `project_milestone` (`project_id`, `milestone_name`, `plan_time`, `actual_time`, `status`, `is_warning`) VALUES
-- 项目4: 基于大模型的智能问答助手
(4, '需求分析与方案设计', '2024-04-01', '2024-03-28', 'finished', 0),
(4, '数据采集与预处理', '2024-06-01', '2024-05-30', 'finished', 0),
(4, '模型训练与调优', '2024-09-01', '2024-09-05', 'finished', 0),
(4, '系统开发与集成', '2024-12-01', '2024-12-10', 'finished', 0),
(4, '测试与优化', '2025-01-15', NULL, 'doing', 0),
-- 项目5: 区块链存证平台设计
(5, '技术调研与架构设计', '2024-04-15', '2024-04-12', 'finished', 0),
(5, '智能合约开发', '2024-07-01', '2024-07-08', 'finished', 0),
(5, '前端界面开发', '2024-10-01', '2024-10-03', 'finished', 0),
(5, '系统集成测试', '2025-01-01', NULL, 'doing', 0),
-- 项目6: 5G边缘计算
(6, '5G网络环境搭建', '2024-05-01', '2024-04-28', 'finished', 0),
(6, '边缘节点部署', '2024-08-01', '2024-08-10', 'finished', 0),
(6, '调度算法实现', '2024-11-01', NULL, 'doing', 0),
(6, '性能测试与优化', '2025-02-01', NULL, 'pending', 0),
-- 项目7: 智能制造
(7, '产线数据采集', '2024-05-15', '2024-05-20', 'finished', 0),
(7, '优化模型构建', '2024-09-01', '2024-09-08', 'finished', 0),
(7, '系统部署与联调', '2024-12-15', NULL, 'doing', 0),
-- 项目9: 联邦学习 (中期检查中)
(9, '文献调研与方案设计', '2024-04-01', '2024-03-30', 'finished', 0),
(9, '联邦学习框架搭建', '2024-07-01', '2024-07-15', 'finished', 0),
(9, '隐私保护算法实现', '2024-10-01', NULL, 'overdue', 1),
(9, '系统测试与评估', '2025-01-01', NULL, 'pending', 0),
-- 项目10: AI辅助医学影像 (中期检查中)
(10, '医学数据集获取', '2024-05-01', '2024-05-05', 'finished', 0),
(10, '影像分割模型训练', '2024-08-01', NULL, 'overdue', 1),
(10, '诊断模型优化', '2024-11-01', NULL, 'pending', 0),
(10, '临床验证', '2025-03-01', NULL, 'pending', 0);

-- ==================== 中期检查数据 ====================
INSERT IGNORE INTO `project_mid_check` (`project_id`, `submit_time`, `status`) VALUES
(9, '2024-10-15 10:30:00', 'waiting'),
(10, '2024-10-20 14:00:00', 'waiting'),
(4, '2024-09-01 09:00:00', 'pass'),
(5, '2024-09-05 11:00:00', 'pass'),
(6, '2024-09-10 16:00:00', 'pass'),
(7, '2024-09-15 10:00:00', 'pass');

-- ==================== 成果数据 ====================
INSERT IGNORE INTO `project_achievement` (`project_id`, `type`, `name`, `achievement_no`, `publish_time`, `status`) VALUES
-- 项目1成果 (已结题)
(1, 'paper', '基于深度卷积神经网络的图像识别算法研究', 'DOI:10.1234/innovation.2023.001', '2023-12-15', 'published'),
(1, 'software', '图像识别系统V1.0', '软著-2023SR-001234', '2024-01-10', 'published'),
(1, 'competition', '全国大学生计算机设计大赛一等奖', 'NCC-2023-001', '2023-11-20', 'published'),
-- 项目2成果
(2, 'software', '智能校园导航系统V2.0', '软著-2023SR-005678', '2024-02-01', 'published'),
(2, 'paper', '基于A*算法的校园室内外融合导航研究', 'DOI:10.5678/campus.2024.002', '2024-01-20', 'published'),
-- 项目3成果
(3, 'patent', '一种低功耗物联网环境监测传感器节点', 'ZL202321001234.5', '2024-03-15', 'published'),
(3, 'paper', '面向智慧校园的物联网环境监测系统设计', 'DOI:10.9012/iot.2024.003', '2024-02-10', 'published'),
-- 项目4成果 (运行中)
(4, 'paper', '大语言模型在智能问答场景中的应用研究', 'DOI:10.3456/llm.2024.004', '2024-10-01', 'published'),
(4, 'software', '智能问答助手V1.0', NULL, '2024-12-20', 'approved'),
-- 项目5成果
(5, 'paper', '基于区块链的电子存证平台安全架构设计', NULL, NULL, 'applying'),
-- 项目7成果
(7, 'competition', '全国大学生机械创新设计大赛二等奖', 'NMID-2024-056', '2024-11-15', 'published'),
-- 项目8成果
(8, 'business', '校园二手交易服务平台', NULL, NULL, 'landed'),
-- 项目11成果 (已批准/即将启动)
(11, 'paper', '多模态内容审核技术综述', NULL, NULL, 'applying');

-- ==================== 结题验收数据 ====================
INSERT IGNORE INTO `project_conclude` (`project_id`, `submit_time`, `status`) VALUES
(1, '2024-02-15 10:00:00', 'pass'),
(2, '2024-03-01 14:30:00', 'pass'),
(3, '2024-04-10 09:00:00', 'pass'),
(19, '2025-02-20 16:00:00', 'waiting');

-- ==================== 附件元数据 ====================
INSERT IGNORE INTO `attachment` (`attach_type`, `relation_id`, `file_name`, `file_size`, `minio_path`, `upload_user`) VALUES
('apply', 4, '基于大模型的智能问答助手-申报书.pdf', 2048576, 'apply/4/申报书.pdf', 6),
('apply', 4, '项目预算明细.xlsx', 512000, 'apply/4/预算明细.xlsx', 6),
('apply', 5, '区块链存证平台-申报书.pdf', 1536000, 'apply/5/申报书.pdf', 2),
('apply', 6, '5G边缘计算资源调度-申报书.pdf', 1843200, 'apply/6/申报书.pdf', 7),
('apply', 7, '智能制造产线优化-申报书.pdf', 1228800, 'apply/7/申报书.pdf', 9),
('mid', 4, '中期检查报告.pdf', 1024000, 'mid/4/中期报告.pdf', 6),
('mid', 5, '中期检查报告.pdf', 896000, 'mid/5/中期报告.pdf', 2),
('mid', 6, '中期检查报告.pdf', 1152000, 'mid/6/中期报告.pdf', 7),
('achievement', 1, '论文原文.pdf', 3072000, 'achievement/1/论文.pdf', 14),
('achievement', 1, '软件著作权证书.pdf', 512000, 'achievement/1/软著证书.pdf', 14),
('conclude', 1, '结题报告.pdf', 2560000, 'conclude/1/结题报告.pdf', 14),
('conclude', 2, '结题报告.pdf', 2048000, 'conclude/2/结题报告.pdf', 15),
('conclude', 3, '结题报告.pdf', 1792000, 'conclude/3/结题报告.pdf', 6);

SET FOREIGN_KEY_CHECKS = 1;
