package com.student.demo.mq;

import com.student.demo.config.RabbitMQConfig;
import com.student.demo.entity.CodeFile;
import com.student.demo.entity.Metrics;
import com.student.demo.entity.AnalysisStatus;
import com.student.demo.repository.CodeFileRepository;
import com.student.demo.repository.MetricsRepository;
import com.student.demo.service.CodeAnalyzerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AnalysisWorker {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisWorker.class);

    @Autowired
    private CodeAnalyzerService analyzerService;

    @Autowired
    private CodeFileRepository codeFileRepository;

    @Autowired
    private MetricsRepository metricsRepository;

    @Autowired
    private org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(Map<String, Object> message, @org.springframework.messaging.handler.annotation.Header(name = "x-death", required = false) java.util.List<Map<String, Object>> xDeath) {
        Long fileId = ((Number) message.get("fileId")).longValue();
        logger.info("Processing analysis task for fileId: {}", fileId);

        // 1. Strict Idempotency Check (Redis Lock)
        String lockKey = "lock:analysis:" + fileId;
        Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", java.util.Objects.requireNonNull(java.time.Duration.ofMinutes(10)));
        
        if (Boolean.FALSE.equals(acquired)) {
            logger.warn("Another worker is already processing fileId {}. Dropping duplicate message.", fileId);
            return; // Lock already held, drop the message
        }

        try {
            // Check retry count from RabbitMQ DLX headers
            int retryCount = 0;
            if (xDeath != null && !xDeath.isEmpty()) {
                Long count = (Long) xDeath.get(0).get("count");
                retryCount = count.intValue();
            }

            if (retryCount >= 3) {
                logger.error("Max retries (3) reached via DLX for fileId: {}. Moving to FAILED state.", fileId);
                markAsFailed(fileId, "Max retries reached due to repeated failures.");
                return; // Drop message permanently
            }

            CodeFile file = codeFileRepository.findById(fileId).orElseThrow();
            logger.info(
        "Loaded file from DB. fileId={}, fileName={}",
        file.getId(),
        file.getName()
);
            Metrics metrics = metricsRepository.findByCodeFileId(fileId)
                    .orElse(new Metrics());

            if (metrics.getStatus() == AnalysisStatus.COMPLETED) {
                logger.info("File {} already processed (found in DB).", fileId);
                return;
            }

            metrics.setStatus(AnalysisStatus.PROCESSING);
            metrics.setRetryCount(retryCount);
            metricsRepository.save(metrics);
            logger.info(
        "Metrics updated to PROCESSING for fileId={}",
        fileId
);

//             String model = (String) message.getOrDefault("model", "AUTO");
//             @SuppressWarnings("unchecked")
//             Map<String, Object> executionContext = (Map<String, Object>) message.get("executionContext");
//             analyzerService.analyzeCode(file, model, executionContext);
//             logger.info(
//         "Starting analyzeCode() for fileId={}, model={}",
//         fileId,
//         model
// );
        
//                    logger.info(
//         "Finished analyzeCode() for fileId={}",
//         fileId
// );       

            String model = (String) message.getOrDefault("model", "AUTO");

@SuppressWarnings("unchecked")
Map<String, Object> executionContext =
        (Map<String, Object>) message.get("executionContext");

logger.info(
        "Starting analyzeCode() for fileId={}, model={}",
        fileId,
        model
);

analyzerService.analyzeCode(file, model, executionContext);

logger.info(
        "Finished analyzeCode() for fileId={}",
        fileId
); 

            logger.info("Successfully processed analysis for fileId: {}", fileId);
            publishEvent(fileId, "COMPLETED");
        } catch (Exception e) {
            logger.error(
                    "FULL STACKTRACE FOR FILE ID {}",
                    fileId,
                    e
            );
            throw new org.springframework.amqp.AmqpRejectAndDontRequeueException(e);
        } finally {

            redisTemplate.delete(lockKey);
        }
    }

    private void publishEvent(Long fileId, String status) {
        try {
            String payload = objectMapper.writeValueAsString(Map.of("fileId", fileId, "status", status));
            redisTemplate.convertAndSend(com.student.demo.config.RedisPubSubConfig.ANALYSIS_EVENTS_TOPIC, payload);
        } catch (Exception e) {
            logger.error("Failed to publish Redis event", e);
        }
    }

    private void markAsFailed(Long fileId, String error) {
        metricsRepository.findByCodeFileId(fileId).ifPresent(metrics -> {
            metrics.setStatus(AnalysisStatus.FAILED);
            metrics.setFailureReason(error);
            metricsRepository.save(metrics);
            publishEvent(fileId, "FAILED");
        });
    }
}
