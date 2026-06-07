package com.student.demo.mq.producer;

import com.student.demo.mq.ApiRabbitMQConfig;
import com.student.demo.mq.dto.AnalysisMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalysisProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendForAnalysis(AnalysisMessage message) {

        rabbitTemplate.convertAndSend(
                ApiRabbitMQConfig.EXCHANGE_NAME,
                "",
                message);

        log.info("Message sent to RabbitMQ: {}", message);
    }
}