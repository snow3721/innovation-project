package com.innovation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.innovation.entity.Message;
import com.innovation.mapper.MessageMapper;
import com.innovation.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Override
    public IPage<Message> listMessages(int page, int size, Integer receiverId, String type, Integer isRead) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, receiverId);
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Message::getType, type);
        }
        if (isRead != null) {
            wrapper.eq(Message::getIsRead, isRead);
        }
        wrapper.orderByDesc(Message::getCreateTime);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public long getUnreadCount(Integer receiverId) {
        return count(new LambdaQueryWrapper<Message>()
                .eq(Message::getReceiverId, receiverId)
                .eq(Message::getIsRead, 0));
    }

    @Override
    @Transactional
    public void markRead(Long messageId, Integer receiverId) {
        Message msg = getById(messageId);
        if (msg != null && msg.getReceiverId().equals(receiverId) && msg.getIsRead() == 0) {
            msg.setIsRead(1);
            msg.setReadTime(LocalDateTime.now());
            updateById(msg);
        }
    }

    @Override
    @Transactional
    public void markAllRead(Integer receiverId) {
        baseMapper.markAllRead(receiverId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Integer receiverId) {
        remove(new LambdaQueryWrapper<Message>()
                .eq(Message::getMessageId, messageId)
                .eq(Message::getReceiverId, receiverId));
    }
}
