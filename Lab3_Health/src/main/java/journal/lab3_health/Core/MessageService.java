package journal.lab3_health.Core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private static final String REQUEST_TOPIC = "request-general-practitioner-topic";
    private static final String RESPONSE_TOPIC = "response-general-practitioner-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private HapiService hapiService;

    @KafkaListener(topics = REQUEST_TOPIC, groupId = "health-service-group")
    public void processGeneralPractitionerRequest(@Payload(required = false)String senderId) {
        if (senderId == null || senderId.trim().isEmpty()) {
            System.err.println("Received empty payload. Ignoring message.");
            return;
        }

        String generalPractitioner = hapiService.getGeneralPractitionerByIdentifier(senderId);

        if (generalPractitioner == null || generalPractitioner.trim().isEmpty()) {
            System.err.println("No practitioner found. Ignoring message.");
            return;
        }

        kafkaTemplate.send(RESPONSE_TOPIC, generalPractitioner);
    }
}