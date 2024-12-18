package journal.lab3_health.Core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestKafka {
    private static final String REQUEST_TOPIC = "request-topic";
    private static final String RESPONSE_TOPIC = "response-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private HapiService hapiService;

    @KafkaListener(topics = REQUEST_TOPIC, groupId = "health-service-group")
    public void processRequest(String requestMessage) {
        String response = hapiService.getGeneralPractitionerByIdentifier(requestMessage);
        kafkaTemplate.send(RESPONSE_TOPIC, response);
    }
}
