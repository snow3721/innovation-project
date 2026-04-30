package com.innovation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.innovation.entity.Message;

public interface MessageService extends IService<Message> {

    IPage<Message> listMessages(int page, int size, Integer receiverId, String type, Integer isRead);

    long getUnreadCount(Integer receiverId);

    void markRead(Long messageId, Integer receiverId);

    void markAllRead(Integer receiverId);

    void deleteMessage(Long messageId, Integer receiverId);
}
