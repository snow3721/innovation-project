package com.innovation.mq;

import com.innovation.config.RabbitMQConfig;
import com.innovation.dto.MessageSendDTO;
import com.innovation.entity.Message;
import com.innovation.mapper.MessageMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class MessageConsumer {

    @Autowired
    private MessageMapper messageMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_AUDIT)
    public void onAuditMessage(MessageSendDTO dto, Channel channel,
                               @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        handleMessage(dto, "审核", channel, tag);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REVIEW)
    public void onReviewMessage(MessageSendDTO dto, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        handleMessage(dto, "评审", channel, tag);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_MILESTONE)
    public void onMilestoneMessage(MessageSendDTO dto, Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        handleMessage(dto, "里程碑", channel, tag);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ACHIEVEMENT)
    public void onAchievementMessage(MessageSendDTO dto, Channel channel,
                                     @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        handleMessage(dto, "成果", channel, tag);
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_SYSTEM)
    public void onSystemMessage(MessageSendDTO dto, Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws Exception {
        handleMessage(dto, "系统", channel, tag);
    }

    @Transactional
    private void handleMessage(MessageSendDTO dto, String typeLabel, Channel channel, long tag) throws Exception {
        try {
            saveMessage(dto);
            channel.basicAck(tag, false);
            log.info("[MQ] 消费{}消息: userId={}", typeLabel, dto.getReceiverId());
        } catch (Exception e) {
            log.error("[MQ] 消费{}消息失败", typeLabel, e);
            channel.basicNack(tag, false, true);
        }
    }

    private void saveMessage(MessageSendDTO dto) {
        Message msg = new Message();
        msg.setReceiverId(dto.getReceiverId());
        msg.setSenderId(dto.getSenderId());
        msg.setTitle(dto.getTitle());
        msg.setContent(dto.getContent());
        msg.setType(dto.getType());
        msg.setRelationId(dto.getRelationId());
        msg.setIsRead(0);
        messageMapper.insert(msg);
    }
}
