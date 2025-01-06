package journal.lab3_health.Core;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MessageServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private HealthService healthService;

    @Mock
    private JwtDecoder jwtDecoder;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @ParameterizedTest
    @CsvSource({
            "12345, Dr. John Doe",   // Case 1: Valid senderId and general practitioner
            "'', ",                  // Case 2: Empty senderId
            "null, ",                // Case 3: Null senderId
            "12345, null"            // Case 4: Valid senderId but no practitioner found
    })
    void testProcessGeneralPractitionerRequest(String senderId, String generalPractitioner) {
        String tokenString = "mockToken";
        String authorizationHeader = "Bearer " + tokenString;

        when(jwtDecoder.decode(tokenString)).thenReturn(mock(Jwt.class));

        if (!"null".equals(senderId)) {
            when(healthService.getGeneralPractitionerByIdentifier(senderId)).thenReturn(generalPractitioner);
        }

        messageService.processGeneralPractitionerRequest(
                "null".equals(senderId) ? null : senderId,
                authorizationHeader
        );

        if (senderId == null || senderId.trim().isEmpty() || generalPractitioner == null || generalPractitioner.trim().isEmpty()) {
            verifyNoInteractions(kafkaTemplate);
        } else {
            ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
            verify(kafkaTemplate).send(recordCaptor.capture());

            ProducerRecord<String, String> capturedRecord = recordCaptor.getValue();
            assertEquals("response-general-practitioner-topic", capturedRecord.topic());
            assertEquals(generalPractitioner, capturedRecord.value());
            assertEquals(authorizationHeader, new String(capturedRecord.headers().lastHeader("Authorization").value()));
        }
    }

    @ParameterizedTest
    @CsvSource({
            "67890, Jane Doe",   // Case 1: Valid identifier and name
            "'', ",              // Case 2: Empty identifier
            "null, ",            // Case 3: Null identifier
            "67890, null"        // Case 4: Valid identifier but no name found
    })
    void testProcessNameRequest(String identifier, String name) {
        String tokenString = "mockToken";
        String authorizationHeader = "Bearer " + tokenString;

        when(jwtDecoder.decode(tokenString)).thenReturn(mock(Jwt.class));

        if (!"null".equals(identifier)) {
            when(healthService.getPatientOrPractitionerNameByIdentifier(identifier)).thenReturn(name);
        }

        messageService.processNameRequest(
                "null".equals(identifier) ? null : identifier,
                authorizationHeader
        );

        if (identifier == null || identifier.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            verifyNoInteractions(kafkaTemplate);
        } else {
            ArgumentCaptor<ProducerRecord<String, String>> recordCaptor = ArgumentCaptor.forClass(ProducerRecord.class);
            verify(kafkaTemplate).send(recordCaptor.capture());

            ProducerRecord<String, String> capturedRecord = recordCaptor.getValue();
            assertEquals("response-name-topic", capturedRecord.topic());
            assertEquals(name, capturedRecord.value());
            assertEquals(authorizationHeader, new String(capturedRecord.headers().lastHeader("Authorization").value()));
        }
    }
}