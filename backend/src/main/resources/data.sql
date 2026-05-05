-- ============================================================
-- 高校创新项目管理系统 - 初始数据脚本 (DML)
-- 数据库: innovation_project
-- 密码统一为: admin123 (BCrypt加密)
-- $2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.
--
-- 用户ID映射:
--   1=admin, 2-6=college_admin(学院1-5), 7-11=teacher(学院1,1,2,3,5)
--   12-15=学生(学院1), 16-18=学生(学院2), 19-20=学生(学院3),
--   21=学生(学院4), 22-23=学生(学院5), 24=学生(学院7), 25-26=学生(学院1)
-- ============================================================

USE innovation_project;

SET FOREIGN_KEY_CHECKS = 0;

-- ==================== 学院数据 ====================
INSERT IGNORE INTO `college` (`college_name`, `sort`) VALUES
('计算机学院', 1),
('电子工程学院', 2),
('机械工程学院', 3),
('化工学院', 4),
('经济管理学院', 5),
('外国语学院', 6),
('数学学院', 7),
('物理学院', 8);

-- ==================== 项目类别数据 ====================
INSERT IGNORE INTO `project_category` (`cat_name`, `remark`) VALUES
('创新训练', '面向本科生个人或团队，开展创新性研究项目'),
('创业训练', '面向本科生团队，开展创业模拟与实训'),
('创业实践', '面向创业团队，开展真实创业项目实践');

-- ==================== 用户数据 ====================
INSERT IGNORE INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `role`, `college_id`, `major`, `status`) VALUES
-- 管理员 (user_id=1)
('admin', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '系统管理员', '13800000001', 'admin@innovation.edu.cn', 'school_admin', NULL, NULL, 1),
-- 学院管理员 (user_id=2-6)
('college_admin1', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '张建国', '13800000002', 'zhangjg@innovation.edu.cn', 'college_admin', 1, NULL, 1),
('college_admin2', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '李明辉', '13800000003', 'limh@innovation.edu.cn', 'college_admin', 2, NULL, 1),
('college_admin3', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '王秀芳', '13800000004', 'wangxf@innovation.edu.cn', 'college_admin', 3, NULL, 1),
('college_admin4', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '刘志强', '13800000005', 'liuzq@innovation.edu.cn', 'college_admin', 4, NULL, 1),
('college_admin5', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '陈慧敏', '13800000006', 'chenhm@innovation.edu.cn', 'college_admin', 5, NULL, 1),

-- 指导老师 (user_id=7-11)
('teacher1', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '陈教授', '13800000010', 'chenjs@innovation.edu.cn', 'teacher', 1, NULL, 1),
('teacher2', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '刘教授', '13800000011', 'liujs@innovation.edu.cn', 'teacher', 1, NULL, 1),
('teacher3', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '赵教授', '13800000012', 'zhaojs@innovation.edu.cn', 'teacher', 2, NULL, 1),
('teacher4', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '孙教授', '13800000013', 'sunjs@innovation.edu.cn', 'teacher', 3, NULL, 1),
('teacher5', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '周教授', '13800000014', 'zhoujs@innovation.edu.cn', 'teacher', 5, NULL, 1),

-- 学生 (user_id=12-26)
('2021001001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '王小明', '13900001001', 'wangxm@stu.innovation.edu.cn', 'student', 1, '计算机科学与技术', 1),
('2021001002', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '李小红', '13900001002', 'lixh@stu.innovation.edu.cn', 'student', 1, '软件工程', 1),
('2021001003', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '张小华', '13900001003', 'zhangxh@stu.innovation.edu.cn', 'student', 1, '人工智能', 1),
('2021001004', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '刘小刚', '13900001004', 'liuxg@stu.innovation.edu.cn', 'student', 1, '数据科学', 1),
('2021002001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '陈小丽', '13900002001', 'chenxl@stu.innovation.edu.cn', 'student', 2, '电子信息工程', 1),
('2021002002', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '杨小伟', '13900002002', 'yangxw@stu.innovation.edu.cn', 'student', 2, '通信工程', 1),
('2021002003', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '吴小芳', '13900002003', 'wuxf@stu.innovation.edu.cn', 'student', 2, '微电子学', 1),
('2021003001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '黄小龙', '13900003001', 'huangxl@stu.innovation.edu.cn', 'student', 3, '机械设计制造', 1),
('2021003002', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '林小梅', '13900003002', 'linxm@stu.innovation.edu.cn', 'student', 3, '自动化', 1),
('2021004001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '钱小化', '13900004001', 'qianxh@stu.innovation.edu.cn', 'student', 4, '化学工程', 1),
('2021005001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '郑小强', '13900005001', 'zhengxq@stu.innovation.edu.cn', 'student', 5, '工商管理', 1),
('2021005002', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '马小云', '13900005002', 'maxy@stu.innovation.edu.cn', 'student', 5, '市场营销', 1),
('2021007001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '何小文', '13900007001', 'hexw@stu.innovation.edu.cn', 'student', 7, '应用数学', 1),
('2020001001', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '赵小天', '13900010001', 'zhaoxt@stu.innovation.edu.cn', 'student', 1, '计算机科学与技术', 1),
('2020001002', '$2a$10$M9huYl/fM0sBarK1v.4WU.JJmbG/6Bixi2vkTUEnasSPeDVg8H0N.', '钱小雨', '13900010002', 'qianxy@stu.innovation.edu.cn', 'student', 1, '网络工程', 1);

-- ==================== 专家数据 ====================
-- expert_id=1-3: 外部专家(user_id=NULL)  expert_id=4-7: 校内专家(user_id=对应teacher)
-- expert_id=8-10: 外部专家
INSERT IGNORE INTO `expert` (`user_id`, `real_name`, `unit`, `title`, `research_field`, `is_inner`, `status`) VALUES
(NULL, '张院士', '清华大学', '教授/院士', '人工智能', 0, 1),
(NULL, '李博导', '北京大学', '教授/博导', '大数据分析', 0, 1),
(NULL, '王研究员', '中国科学院', '研究员', '物联网技术', 0, 1),
(7, '陈教授', '本校计算机学院', '教授', '机器学习', 1, 1),
(8, '刘教授', '本校计算机学院', '教授', '软件工程', 1, 1),
(9, '赵教授', '本校电子工程学院', '教授', '信号处理', 1, 1),
(10, '孙教授', '本校机械工程学院', '教授', '智能制造', 1, 1),
(NULL, '吴教授', '浙江大学', '教授', '深度学习', 0, 1),
(NULL, '郑教授', '复旦大学', '教授', '区块链技术', 0, 1),
(NULL, '冯研究员', '华为研究院', '高级研究员', '云计算', 0, 1);

-- ==================== 项目数据 ====================
INSERT IGNORE INTO `project` (`project_name`, `cat_id`, `leader_id`, `teacher_id`, `college_id`, `apply_year`, `total_budget`, `status`, `start_time`, `end_time`) VALUES
-- 已结题项目 (2023年)
('基于深度学习的图像识别系统', 1, 12, 7, 1, 2023, 15000.00, 'concluded', '2023-03-01', '2024-03-01'),
('智能校园导航APP开发', 1, 13, 8, 1, 2023, 12000.00, 'concluded', '2023-03-01', '2024-03-01'),
('物联网环境监测节点设计', 1, 16, 9, 2, 2023, 18000.00, 'concluded', '2023-04-01', '2024-04-01'),

-- 运行中项目 (2024年)
('基于大模型的智能问答助手', 1, 14, 7, 1, 2024, 20000.00, 'running', '2024-03-01', '2025-03-01'),
('区块链存证平台设计', 1, 15, 8, 1, 2024, 16000.00, 'running', '2024-03-15', '2025-03-15'),
('5G边缘计算资源调度研究', 1, 17, 9, 2, 2024, 22000.00, 'running', '2024-04-01', '2025-04-01'),
('智能制造产线优化系统', 1, 19, 10, 3, 2024, 18000.00, 'running', '2024-04-15', '2025-04-15'),
('大学生校园二手交易平台', 2, 22, 11, 5, 2024, 10000.00, 'running', '2024-05-01', '2025-05-01'),

-- 中期检查中项目
('基于联邦学习的隐私计算框架', 1, 25, 7, 1, 2024, 25000.00, 'mid_checking', '2024-03-01', '2025-06-01'),
('AI辅助医学影像诊断系统', 1, 18, 9, 2, 2024, 28000.00, 'mid_checking', '2024-04-01', '2025-06-30'),

-- 已批准项目 (2025年)
('基于多模态大模型的内容审核系统', 1, 12, 7, 1, 2025, 30000.00, 'approved', '2025-03-01', '2026-03-01'),
('智能机器人巡检系统研发', 1, 20, 10, 3, 2025, 25000.00, 'approved', '2025-03-15', '2026-03-15'),
('绿色能源管理物联网平台', 1, 17, 9, 2, 2025, 22000.00, 'approved', '2025-04-01', '2026-04-01'),

-- 审核中项目 (2025年)
('基于RAG的法律咨询助手', 1, 26, 8, 1, 2025, 18000.00, 'wait_school_assign', '2025-03-01', '2026-03-01'),
('校园跑腿小程序创业项目', 2, 23, 11, 5, 2025, 8000.00, 'wait_college_assign', '2025-04-01', '2026-04-01'),
('数字孪生校园可视化平台', 1, 13, 7, 1, 2025, 26000.00, 'wait_teacher_audit', '2025-05-01', '2026-05-01'),
('量子通信仿真实验系统', 1, 18, 9, 2, 2025, 20000.00, 'wait_college_audit', '2025-04-01', '2026-04-01'),

-- 草稿项目
('AI绘画创作平台', 1, 14, NULL, 1, 2025, 15000.00, 'draft', NULL, NULL),

-- 被驳回项目
('校园外卖配送平台', 2, 23, 11, 5, 2024, 12000.00, 'rejected', NULL, NULL),
('简易计算器创新项目', 1, 26, 8, 1, 2024, 5000.00, 'rejected', NULL, NULL),

-- 结题申请中
('智能停车管理系统', 1, 13, 8, 1, 2024, 16000.00, 'conclude_apply', '2024-03-01', '2025-03-01');

-- ==================== 项目成员数据 ====================
INSERT IGNORE INTO `project_member` (`project_id`, `user_id`, `role`) VALUES
-- 项目1: 基于深度学习的图像识别系统 (leader=12/王小明)
(1, 12, 'leader'), (1, 13, 'normal'), (1, 25, 'normal'),
-- 项目2: 智能校园导航APP开发 (leader=13/李小红)
(2, 13, 'leader'), (2, 12, 'normal'), (2, 26, 'normal'),
-- 项目3: 物联网环境监测节点设计 (leader=16/陈小丽)
(3, 16, 'leader'), (3, 17, 'normal'),
-- 项目4: 基于大模型的智能问答助手 (leader=14/张小华)
(4, 14, 'leader'), (4, 15, 'normal'), (4, 25, 'normal'), (4, 26, 'normal'),
-- 项目5: 区块链存证平台设计 (leader=15/刘小刚)
(5, 15, 'leader'), (5, 12, 'normal'), (5, 13, 'normal'),
-- 项目6: 5G边缘计算资源调度研究 (leader=17/杨小伟)
(6, 17, 'leader'), (6, 18, 'normal'),
-- 项目7: 智能制造产线优化系统 (leader=19/黄小龙)
(7, 19, 'leader'), (7, 20, 'normal'),
-- 项目8: 大学生校园二手交易平台 (leader=22/郑小强)
(8, 22, 'leader'), (8, 23, 'normal'),
-- 项目9: 基于联邦学习的隐私计算框架 (leader=25/赵小天)
(9, 25, 'leader'), (9, 14, 'normal'), (9, 26, 'normal'),
-- 项目10: AI辅助医学影像诊断系统 (leader=18/吴小芳)
(10, 18, 'leader'), (10, 16, 'normal'),
-- 项目11: 基于多模态大模型的内容审核系统 (leader=12/王小明)
(11, 12, 'leader'), (11, 13, 'normal'), (11, 14, 'normal'), (11, 15, 'normal'),
-- 项目12: 智能机器人巡检系统研发 (leader=20/林小梅)
(12, 20, 'leader'), (12, 19, 'normal'),
-- 项目13: 绿色能源管理物联网平台 (leader=17/杨小伟)
(13, 17, 'leader'), (13, 16, 'normal'), (13, 18, 'normal'),
-- 项目14: 基于RAG的法律咨询助手 (leader=26/钱小雨)
(14, 26, 'leader'), (14, 15, 'normal'),
-- 项目15: 校园跑腿小程序创业项目 (leader=23/马小云)
(15, 23, 'leader'), (15, 22, 'normal'),
-- 项目16: 数字孪生校园可视化平台 (leader=13/李小红)
(16, 13, 'leader'), (16, 12, 'normal'), (16, 14, 'normal'),
-- 项目17: 量子通信仿真实验系统 (leader=18/吴小芳)
(17, 18, 'leader'), (17, 16, 'normal'),
-- 项目19: 校园外卖配送平台 (rejected, leader=23/马小云)
(19, 23, 'leader'), (19, 22, 'normal'),
-- 项目20: 简易计算器创新项目 (rejected, leader=26/钱小雨)
(20, 26, 'leader'),
-- 项目21: 智能停车管理系统 (conclude_apply, leader=13/李小红)
(21, 13, 'leader'), (21, 12, 'normal');

-- ==================== 导师审核数据 ====================
INSERT IGNORE INTO `project_teacher_audit` (`project_id`, `teacher_id`, `result`) VALUES
(1, 7, 'pass'), (2, 8, 'pass'), (3, 9, 'pass'),
(4, 7, 'pass'), (5, 8, 'pass'), (6, 9, 'pass'), (7, 10, 'pass'), (8, 11, 'pass'),
(9, 7, 'pass'), (10, 9, 'pass'),
(11, 7, 'pass'), (12, 10, 'pass'), (13, 9, 'pass'),
(14, 8, 'pass'), (15, 11, 'pass'),
(17, 9, 'pass'),
(19, 11, 'pass'), (20, 8, 'reject'), (21, 8, 'pass');

-- ==================== 学院终审数据 ====================
INSERT IGNORE INTO `project_college_audit` (`project_id`, `admin_id`, `result`) VALUES
(1, 2, 'pass'), (2, 2, 'pass'), (3, 3, 'pass'),
(4, 2, 'pass'), (5, 2, 'pass'), (6, 3, 'pass'), (7, 4, 'pass'), (8, 6, 'pass'),
(9, 2, 'pass'), (10, 3, 'pass'),
(11, 2, 'pass'), (12, 4, 'pass'), (13, 3, 'pass'),
(14, 2, 'pass'),
(19, 6, 'pass'),
(21, 2, 'pass');

-- ==================== 学校终审数据 ====================
INSERT IGNORE INTO `project_school_audit` (`project_id`, `admin_id`, `result`) VALUES
(1, 1, 'pass'), (2, 1, 'pass'), (3, 1, 'pass'),
(4, 1, 'pass'), (5, 1, 'pass'), (6, 1, 'pass'), (7, 1, 'pass'), (8, 1, 'pass'),
(9, 1, 'pass'), (10, 1, 'pass'),
(11, 1, 'pass'), (12, 1, 'pass'), (13, 1, 'pass'),
(19, 1, 'reject'),
(21, 1, 'pass');

-- ==================== 专家分配数据 ====================
INSERT IGNORE INTO `expert_assignment` (`project_id`, `expert_id`, `stage`) VALUES
-- 院级评审 (2024年运行中项目)
(4, 4, 'college'), (4, 5, 'college'),
(5, 5, 'college'), (5, 6, 'college'),
(6, 6, 'college'), (6, 7, 'college'),
(7, 7, 'college'), (7, 4, 'college'),
(8, 5, 'college'), (8, 10, 'college'),
-- 院级评审 (中期检查中项目)
(9, 4, 'college'), (9, 5, 'college'),
(10, 6, 'college'), (10, 7, 'college'),
-- 院级评审 (2025年已批准项目)
(11, 4, 'college'), (11, 5, 'college'),
(12, 7, 'college'), (12, 4, 'college'),
(13, 6, 'college'), (13, 7, 'college'),
-- 院级评审 (审核中项目)
(14, 4, 'college'), (14, 5, 'college'),
(15, 5, 'college'), (15, 10, 'college'),
(17, 6, 'college'), (17, 7, 'college'),
-- 院级评审 (被驳回项目19)
(19, 5, 'college'), (19, 10, 'college'),
-- 院级评审 (结题申请项目21)
(21, 4, 'college'), (21, 5, 'college'),
-- 院级评审 (已结题项目)
(1, 4, 'college'), (1, 5, 'college'),
(2, 5, 'college'), (2, 6, 'college'),
(3, 6, 'college'), (3, 7, 'college'),

-- 校级评审 (2024年运行中项目)
(4, 1, 'school'), (4, 2, 'school'),
(5, 2, 'school'), (5, 3, 'school'),
(6, 3, 'school'), (6, 8, 'school'),
(7, 8, 'school'), (7, 1, 'school'),
(8, 9, 'school'), (8, 2, 'school'),
-- 校级评审 (中期检查中项目)
(9, 1, 'school'), (9, 3, 'school'),
(10, 2, 'school'), (10, 8, 'school'),
-- 校级评审 (2025年已批准项目)
(11, 1, 'school'), (11, 4, 'school'), (11, 5, 'school'),
(12, 3, 'school'), (12, 7, 'school'),
(13, 2, 'school'), (13, 6, 'school'),
-- 校级评审 (审核中项目14)
(14, 1, 'school'), (14, 3, 'school'),
-- 校级评审 (被驳回项目19)
(19, 1, 'school'), (19, 3, 'school'),
-- 校级评审 (结题申请项目21)
(21, 1, 'school'), (21, 2, 'school'),
-- 校级评审 (已结题项目)
(1, 1, 'school'), (1, 2, 'school'),
(2, 2, 'school'), (2, 3, 'school'),
(3, 3, 'school'), (3, 8, 'school');

-- ==================== 评审打分数据 ====================
INSERT IGNORE INTO `project_review_score` (`project_id`, `expert_id`, `review_stage`, `score_innovation`, `score_feasibility`, `score_team`, `score_value`, `total_score`) VALUES
-- 项目1 (院级)
(1, 4, 'college', 23, 22, 20, 21, 86),
(1, 5, 'college', 24, 21, 22, 20, 87),
-- 项目1 (校级)
(1, 1, 'school', 25, 24, 22, 23, 94),
(1, 2, 'school', 24, 23, 21, 22, 90),
-- 项目2 (院级)
(2, 5, 'college', 22, 21, 19, 20, 82),
(2, 6, 'college', 23, 22, 20, 21, 86),
-- 项目2 (校级)
(2, 2, 'school', 23, 22, 21, 20, 86),
(2, 3, 'school', 22, 23, 20, 21, 86),
-- 项目3 (院级)
(3, 6, 'college', 21, 23, 20, 22, 86),
(3, 7, 'college', 22, 22, 21, 20, 85),
-- 项目3 (校级)
(3, 3, 'school', 24, 23, 22, 21, 90),
(3, 8, 'school', 23, 22, 21, 20, 86),
-- 项目4 (院级)
(4, 4, 'college', 23, 22, 20, 21, 86),
(4, 5, 'college', 24, 21, 22, 20, 87),
-- 项目4 (校级)
(4, 1, 'school', 25, 24, 22, 23, 94),
(4, 2, 'school', 24, 23, 21, 22, 90),
-- 项目5 (院级)
(5, 5, 'college', 20, 21, 19, 18, 78),
(5, 6, 'college', 21, 22, 20, 19, 82),
-- 项目5 (校级)
(5, 2, 'school', 22, 23, 21, 20, 86),
(5, 3, 'school', 23, 22, 20, 21, 86),
-- 项目6 (院级)
(6, 6, 'college', 22, 24, 21, 23, 90),
(6, 7, 'college', 21, 23, 20, 22, 86),
-- 项目6 (校级)
(6, 3, 'school', 24, 25, 22, 23, 94),
(6, 8, 'school', 23, 24, 21, 22, 90),
-- 项目7 (院级)
(7, 7, 'college', 21, 23, 22, 20, 86),
(7, 4, 'college', 22, 22, 21, 21, 86),
-- 项目7 (校级)
(7, 8, 'school', 23, 24, 22, 21, 90),
(7, 1, 'school', 24, 23, 23, 22, 92),
-- 项目8 (院级)
(8, 5, 'college', 18, 20, 19, 21, 78),
(8, 10, 'college', 19, 21, 20, 22, 82),
-- 项目8 (校级)
(8, 9, 'school', 20, 22, 21, 23, 86),
(8, 2, 'school', 21, 23, 20, 22, 86),
-- 项目9 (院级)
(9, 4, 'college', 22, 23, 21, 20, 86),
(9, 5, 'college', 23, 22, 20, 21, 86),
-- 项目9 (校级)
(9, 1, 'school', 24, 23, 22, 21, 90),
(9, 3, 'school', 23, 24, 21, 22, 90),
-- 项目10 (院级)
(10, 6, 'college', 22, 24, 21, 20, 87),
(10, 7, 'college', 21, 23, 20, 22, 86),
-- 项目10 (校级)
(10, 2, 'school', 23, 24, 22, 21, 90),
(10, 8, 'school', 22, 23, 21, 20, 86),
-- 项目11 (院级)
(11, 4, 'college', 24, 23, 22, 21, 90),
(11, 5, 'college', 23, 22, 21, 22, 88),
-- 项目11 (校级)
(11, 1, 'school', 25, 24, 23, 24, 96),
(11, 4, 'school', 24, 23, 22, 23, 92),
(11, 5, 'school', 23, 22, 21, 22, 88),
-- 项目12 (院级)
(12, 7, 'college', 21, 23, 22, 20, 86),
(12, 4, 'college', 22, 22, 21, 21, 86),
-- 项目12 (校级)
(12, 3, 'school', 22, 24, 21, 20, 87),
(12, 7, 'school', 23, 23, 22, 21, 89),
-- 项目13 (院级)
(13, 6, 'college', 20, 22, 21, 19, 82),
(13, 7, 'college', 21, 23, 20, 22, 86),
-- 项目13 (校级)
(13, 2, 'school', 21, 23, 20, 22, 86),
(13, 6, 'school', 22, 22, 21, 23, 88),
-- 项目14 (院级已完成; 校级尚未评分)
(14, 4, 'college', 22, 23, 21, 20, 86),
(14, 5, 'college', 23, 22, 20, 21, 86),
-- 项目17 (院级已完成; 尚在 wait_college_audit)
(17, 6, 'college', 21, 23, 20, 22, 86),
(17, 7, 'college', 22, 22, 21, 21, 86),
-- 项目19 (被校级驳回)
(19, 5, 'college', 18, 19, 17, 16, 70),
(19, 10, 'college', 19, 20, 18, 17, 74),
(19, 1, 'school', 15, 16, 14, 13, 58),
(19, 3, 'school', 16, 17, 15, 14, 62),
-- 项目21 (院级+校级)
(21, 4, 'college', 22, 24, 21, 23, 90),
(21, 5, 'college', 23, 23, 22, 22, 90),
(21, 1, 'school', 24, 25, 23, 22, 94),
(21, 2, 'school', 23, 24, 22, 21, 90);

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
(7, '2024-09-15 10:00:00', 'pass'),
(21, '2024-09-20 14:00:00', 'pass');

-- ==================== 结题验收数据 ====================
INSERT IGNORE INTO `project_conclude` (`project_id`, `submit_time`, `status`) VALUES
(1, '2024-02-15 10:00:00', 'pass'),
(2, '2024-03-01 14:30:00', 'pass'),
(3, '2024-04-10 09:00:00', 'pass'),
(21, '2025-02-20 16:00:00', 'waiting');

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

-- ==================== 附件元数据 ====================
INSERT IGNORE INTO `attachment` (`attach_type`, `relation_id`, `file_name`, `file_size`, `minio_path`, `upload_user`) VALUES
('apply', 4, '基于大模型的智能问答助手-申报书.pdf', 2048576, 'apply/4/申报书.pdf', 14),
('apply', 4, '项目预算明细.xlsx', 512000, 'apply/4/预算明细.xlsx', 14),
('apply', 5, '区块链存证平台-申报书.pdf', 1536000, 'apply/5/申报书.pdf', 15),
('apply', 6, '5G边缘计算资源调度-申报书.pdf', 1843200, 'apply/6/申报书.pdf', 17),
('apply', 7, '智能制造产线优化-申报书.pdf', 1228800, 'apply/7/申报书.pdf', 19),
('mid', 4, '中期检查报告.pdf', 1024000, 'mid/4/中期报告.pdf', 14),
('mid', 5, '中期检查报告.pdf', 896000, 'mid/5/中期报告.pdf', 15),
('mid', 6, '中期检查报告.pdf', 1152000, 'mid/6/中期报告.pdf', 17),
('achievement', 1, '论文原文.pdf', 3072000, 'achievement/1/论文.pdf', 12),
('achievement', 1, '软件著作权证书.pdf', 512000, 'achievement/1/软著证书.pdf', 12),
('conclude', 1, '结题报告.pdf', 2560000, 'conclude/1/结题报告.pdf', 12),
('conclude', 2, '结题报告.pdf', 2048000, 'conclude/2/结题报告.pdf', 13),
('conclude', 3, '结题报告.pdf', 1792000, 'conclude/3/结题报告.pdf', 16);

-- ==================== 消息中心数据 ====================
INSERT IGNORE INTO `message` (`receiver_id`, `sender_id`, `title`, `content`, `type`, `relation_id`, `is_read`, `create_time`) VALUES
(7, 1, '新项目待审核', '项目「基于大模型的智能问答助手」已提交，请尽快审核。', 'audit', 4, 1, '2024-03-01 10:00:00'),
(8, 1, '新项目待审核', '项目「区块链存证平台设计」已提交，请尽快审核。', 'audit', 5, 1, '2024-03-15 10:00:00'),
(14, 2, '导师审核结果', '项目「基于大模型的智能问答助手」导师审核通过。', 'audit', 4, 1, '2024-03-05 14:30:00'),
(15, 3, '导师审核结果', '项目「区块链存证平台设计」导师审核通过。', 'audit', 5, 0, '2024-03-20 16:00:00'),
(14, 1, '学校审核结果', '项目「基于大模型的智能问答助手」学校审核通过，已立项！', 'audit', 4, 0, '2024-04-01 09:00:00'),
(12, NULL, '评审打分通知', '项目「基于深度学习的图像识别系统」收到新的评审打分，总分：94。', 'review', 1, 0, '2024-05-10 11:00:00'),
(7, NULL, '新评审任务', '您被分配了一个校级评审任务，项目ID：11，请尽快完成评审。', 'review', 11, 0, '2025-03-10 09:00:00'),
(8, NULL, '新评审任务', '您被分配了一个校级评审任务，项目ID：11，请尽快完成评审。', 'review', 11, 0, '2025-03-10 09:00:00'),
(14, NULL, '新增里程碑', '项目「基于大模型的智能问答助手」新增里程碑：「需求分析与方案设计」，计划时间：2024-04-01。', 'milestone', 4, 1, '2024-03-25 10:00:00'),
(14, NULL, '新增里程碑', '项目「基于大模型的智能问答助手」新增里程碑：「模型训练与调优」，计划时间：2024-09-01。', 'milestone', 4, 0, '2024-08-25 10:00:00'),
(14, NULL, '新成果提交', '项目「基于大模型的智能问答助手」提交了新成果：「大语言模型在智能问答场景中的应用研究」。', 'achievement', 4, 0, '2024-10-01 15:00:00'),
(12, NULL, '系统通知', '2025年度创新项目申报已开始，请及时提交项目申请。', 'system', NULL, 0, '2025-02-01 08:00:00'),
(13, NULL, '系统通知', '2025年度创新项目申报已开始，请及时提交项目申请。', 'system', NULL, 0, '2025-02-01 08:00:00'),
(14, NULL, '系统通知', '2025年度创新项目申报已开始，请及时提交项目申请。', 'system', NULL, 0, '2025-02-01 08:00:00'),
(12, NULL, '系统通知', '您的项目「基于深度学习的图像识别系统」已结题，感谢参与！', 'system', 1, 1, '2024-02-15 10:00:00'),
(13, 8, '评审打分通知', '项目「智能停车管理系统」收到新的评审打分。', 'review', 21, 0, '2025-01-20 14:00:00');

SET FOREIGN_KEY_CHECKS = 1;
