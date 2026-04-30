package com.innovation.common;

public class Constants {
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String[] PROJECT_STATUSES = {
        "draft", "wait_teacher_audit", "wait_college_assign", "wait_college_review", "wait_college_audit",
        "wait_school_assign", "wait_school_review", "wait_school_audit", "approved", "rejected",
        "running", "mid_checking", "conclude_apply", "concluded"
    };
    public static final String[] PROJECT_STATUS_TEXTS = {
        "草稿", "待导师审核", "待院级分配", "待院级评审", "待院级终审",
        "待校级分配", "待校级评审", "待校级终审", "已立项", "已驳回",
        "运行中", "中期检查", "待结题", "已结题"
    };
}
