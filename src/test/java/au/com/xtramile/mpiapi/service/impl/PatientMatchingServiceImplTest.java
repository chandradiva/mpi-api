package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.dto.MatchResultDto;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatientMatchingServiceImplTest {

    private PatientMatchingServiceImpl svc;

    @BeforeEach
    void setUp() {
        svc = new PatientMatchingServiceImpl();
    }

    @Test
    void comparePatients_allFieldsMatch_shouldAutoMatch() {
        Patient existing = Patient.builder()
                .id(UUID.randomUUID())
                .firstName("Fredilla")
                .lastName("Diva")
                .dob(LocalDate.of(1991, 5, 17))
                .phoneNo("08121010101")
                .email("diva@gmail.com")
                .build();

        PatientRequest incoming = new PatientRequest(
                UUID.randomUUID(),
                "Fredilla",
                "Diva",
                "17/05/1991",
                "MALE",
                "08121010101",
                "diva@gmail.com",
                null, null, null, null, null,
                null, null, null
        );

        MatchResultDto result = svc.comparePatients(incoming, existing);
        assertEquals("AUTO_MATCH", result.getDecision());
        assertEquals(100, result.getConfidenceScore());
        assertTrue(result.getFieldMatches().getOrDefault("name", false));
        assertTrue(result.getFieldMatches().getOrDefault("dob", false));
        assertTrue(result.getFieldMatches().getOrDefault("phone", false));
        assertTrue(result.getFieldMatches().getOrDefault("email", false));
    }

    @Test
    void comparePatients_nameAndDobMatch_shouldReview() {
        Patient existing = Patient.builder()
                .id(UUID.randomUUID())
                .firstName("Fredilla")
                .lastName("Diva")
                .dob(LocalDate.of(1991, 5, 17))
                .phoneNo("08121234567")
                .email("other@example.com")
                .build();

        PatientRequest incoming = new PatientRequest(
                UUID.randomUUID(),
                "Fredilla",
                "Diva",
                "17/05/1991",
                "MALE",
                "0000000000",
                "nope@example.com",
                null, null, null, null, null,
                null, null, null
        );

        MatchResultDto result = svc.comparePatients(incoming, existing);
        assertEquals("REVIEW", result.getDecision());
        // 35 (name) + 35 (dob) = 70
        assertEquals(70, result.getConfidenceScore());
    }

    @Test
    void comparePatients_oneFieldMatch_shouldNoMatch() {
        Patient existing = Patient.builder()
                .id(UUID.randomUUID())
                .firstName("Alice")
                .lastName("Smith")
                .dob(LocalDate.of(1980, 1, 1))
                .phoneNo("08120000000")
                .email("alice@example.com")
                .build();

        PatientRequest incoming = new PatientRequest(
                UUID.randomUUID(),
                "Alice",
                "Different",
                "01/01/1970",
                "FEMALE",
                "0000000000",
                "nomatch@example.com",
                null, null, null, null, null,
                null, null, null
        );

        MatchResultDto result = svc.comparePatients(incoming, existing);
        // Only first name matches (not sufficient)
        assertEquals("NO_MATCH", result.getDecision());
        assertEquals(0, result.getConfidenceScore());
    }

    @Test
    void comparePatients_phoneNormalization_shouldMatchPhone() {
        Patient existing = Patient.builder()
                .id(UUID.randomUUID())
                .firstName("Bob")
                .lastName("Jones")
                .dob(LocalDate.of(1990, 6, 15))
                .phoneNo("08121010101")
                .email("bob@example.com")
                .build();

        PatientRequest incoming = new PatientRequest(
                UUID.randomUUID(),
                "Bob",
                "Jones",
                "15/06/1990",
                "MALE",
                "0812-101-0101",
                "bob@example.com",
                null, null, null, null, null,
                null, null, null
        );

        MatchResultDto result = svc.comparePatients(incoming, existing);
        // name, dob, phone, email -> AUTO_MATCH
        assertEquals("AUTO_MATCH", result.getDecision());
    }

}
