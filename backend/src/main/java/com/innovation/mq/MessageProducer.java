package com.innovation.mq;

import com.innovation.config.RabbitMQConfig;
import com.innovation.dto.MessageSendDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送审核消息
     */
    public void sendAuditMessage(MessageSendDTO dto) {
        dto.setType("audit");
        rabbitTemplate.convertAndSend(RabbitMQConfig.MSG_EXCHANGE, RabbitMQConfig.RK_AUDIT, dto);
        log.info("[MQ] 发送审核消息 -> userId={}", dto.getReceiverId());
    }

    /**
     * 发送评审消息
     */
    public void sendReviewMessage(MessageSendDTO dto) {
        dto.setType("review");
        rabbitTemplate.convertAndSend(RabbitMQConfig.MSG_EXCHANGE, RabbitMQConfig.RK_REVIEW, dto);
        log.info("[MQ] 发送评审消息 -> userId={}", dto.getReceiverId());
    }

    /**
     * 发送里程碑消息
     */
    public void sendMilestoneMessage(MessageSendDTO dto) {
        dto.setType("milestone");
        rabbitTemplate.convertAndSend(RabbitMQConfig.MSG_EXCHANGE, RabbitMQConfig.RK_MILESTONE, dto);
        log.info("[MQ] 发送里程碑消息 -> userId={}", dto.getReceiverId());
    }

    /**
     * 发送成果消息
     */
    public void sendAchievementMessage(MessageSendDTO dto) {
        dto.setType("achievement");
        rabbitTemplate.convertAndSend(RabbitMQConfig.MSG_EXCHANGE, RabbitMQConfig.RK_ACHIEVEMENT, dto);
        log.info("[MQ] 发送成果消息 -> userId={}", dto.getReceiverId());
    }

    /**
     * 发送系统消息
     */
    public void sendSystemMessage(MessageSendDTO dto) {
        dto.setType("system");
        rabbitTemplate.convertAndSend(RabbitMQConfig.MSG_EXCHANGE, RabbitMQConfig.RK_SYSTEM, dto);
        log.info("[MQ] 发送系统消息 -> userId={}", dto.getReceiverId());
    }
}
