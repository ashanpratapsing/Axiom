package com.student.demo.mq.consumer;

import com.student.demo.mq.ApiRabbitMQConfig;
import com.student.demo.mq.dto.AnalysisMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnalysisConsumer {

    @RabbitListener(queues = ApiRabbitMQConfig.QUEUE_NAME)
    public void consume(AnalysisMessage message) {

        log.info("Received Message From Queue: {}", message);

        // TODO:
        // call AI service
        // save analysis result
    }
}