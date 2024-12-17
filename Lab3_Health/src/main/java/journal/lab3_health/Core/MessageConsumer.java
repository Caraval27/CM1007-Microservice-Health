package journal.lab3_health.Core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private HapiService hapiService;

    @KafkaListener(topics = "request-general-practitioner-topic", groupId = "health-service-group")
    public void receiveGeneralPractitionerRequest(String id) {
        String generalPractitioner = hapiService.getGeneralPractitionerByIdentifier(id);
        kafkaTemplate.send("receive-general-practitioner-topic", generalPractitioner);
    }
}