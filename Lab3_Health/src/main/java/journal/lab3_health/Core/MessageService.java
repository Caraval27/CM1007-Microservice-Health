package journal.lab3_health.Core;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private HealthService healthService;

    @Autowired
    private JwtDecoder jwtDecoder;

    @KafkaListener(topics = "request-general-practitioner-topic", groupId = "health-service-group")
    public void processGeneralPractitionerRequest(@Payload String senderId, @Header("Authorization") String authorizationHeader) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            return;
        }
        String tokenString = authorizationHeader.substring(7);
        jwtDecoder.decode(tokenString);

        String generalPractitioner = healthService.getGeneralPractitionerByIdentifier(senderId);

        ProducerRecord<String, String> record = new ProducerRecord<>("response-general-practitioner-topic", generalPractitioner);
        record.headers().add("Authorization", ("Bearer " + tokenString).getBytes());
        kafkaTemplate.send(record);
    }

    @KafkaListener(topics = "request-name-topic", groupId = "health-service-group")
    public void processNameRequest(@Payload String identifier, @Header("Authorization") String authorizationHeader) {
        if (!authorizationHeader.startsWith("Bearer ")) {
            return;
        }
        String tokenString = authorizationHeader.substring(7);
        jwtDecoder.decode(tokenString);

        String name = healthService.getPatientOrPractitionerNameByIdentifier(identifier);

        ProducerRecord<String, String> record = new ProducerRecord<>("response-name-topic", name);
        record.headers().add("Authorization", ("Bearer " + tokenString).getBytes());

        kafkaTemplate.send(record);
    }
}