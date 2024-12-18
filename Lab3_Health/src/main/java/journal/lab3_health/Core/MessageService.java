package journal.lab3_health.Core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private HapiService hapiService;

    @KafkaListener(topics = "request-general-practitioner-topic", groupId = "health-service-group")
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

        kafkaTemplate.send("response-general-practitioner-topic", generalPractitioner);
    }

    @KafkaListener(topics = "request-name-topic", groupId = "health-service-group")
    public void processNameRequest(@Payload(required = false)String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            System.err.println("Received empty payload. Ignoring message.");
            return;
        }

        String name = hapiService.getPatientOrPractitionerNameByIdentifier(identifier);

        if (name == null || name.trim().isEmpty()) {
            System.err.println("No name found. Ignoring message.");
            return;
        }

        kafkaTemplate.send("response-name-topic", name);
    }
}