package com.innovation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.innovation.common.PageResult;
import com.innovation.common.Result;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Message;
import com.innovation.mq.MessageProducer;
import com.innovation.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "消息中心")
@RestController
@RequestMapping("/api/v1/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageProducer messageProducer;

    @ApiOperation("获取消息列表")
    @GetMapping
    public Result<PageResult<Message>> listMessages(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer isRead) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        IPage<Message> msgPage = messageService.listMessages(page, size, userId, type, isRead);
        return Result.success(new PageResult<>(msgPage.getTotal(), msgPage.getRecords()));
    }

    @ApiOperation("获取未读消息数量")
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        return Result.success(messageService.getUnreadCount(userId));
    }

    @ApiOperation("标记单条消息已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        messageService.markRead(id, userId);
        return Result.success();
    }

    @ApiOperation("全部标记已读")
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        messageService.markAllRead(userId);
        return Result.success();
    }

    @ApiOperation("删除消息")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Integer userId = (Integer) auth.getPrincipal();
        messageService.deleteMessage(id, userId);
        return Result.success();
    }

    @ApiOperation("发送消息(管理员)")
    @PostMapping("/send")
    @PreAuthorize("hasAnyRole('college_admin','school_admin')")
    public Result<Void> sendMessage(@Validated @RequestBody MessageSendDTO dto) {
        messageProducer.sendSystemMessage(dto);
        return Result.success();
    }
}
