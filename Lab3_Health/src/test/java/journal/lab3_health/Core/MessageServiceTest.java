package journal.lab3_health.Core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/*@SpringBootTest
@ActiveProfiles("test")
class MessageServiceTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private HealthService healthService;

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
        if (!"null".equals(senderId)) {
            when(healthService.getGeneralPractitionerByIdentifier(senderId)).thenReturn(generalPractitioner);
        }

        messageService.processGeneralPractitionerRequest("null".equals(senderId) ? null : senderId);

        if (senderId == null || senderId.trim().isEmpty() || generalPractitioner == null || generalPractitioner.trim().isEmpty()) {
            verifyNoInteractions(kafkaTemplate);
        } else {
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq("response-general-practitioner-topic"), captor.capture());
            assertEquals(generalPractitioner, captor.getValue());
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
        if (!"null".equals(identifier)) {
            when(healthService.getPatientOrPractitionerNameByIdentifier(identifier)).thenReturn(name);
        }

        messageService.processNameRequest("null".equals(identifier) ? null : identifier);

        if (identifier == null || identifier.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            verifyNoInteractions(kafkaTemplate);
        } else {
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            verify(kafkaTemplate).send(eq("response-name-topic"), captor.capture());
            assertEquals(name, captor.getValue());
        }
    }
}*/