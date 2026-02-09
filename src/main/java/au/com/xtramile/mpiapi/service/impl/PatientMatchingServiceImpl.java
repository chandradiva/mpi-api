package au.com.xtramile.mpiapi.service.impl;

import au.com.xtramile.mpiapi.dto.MatchResultDto;
import au.com.xtramile.mpiapi.dto.request.PatientRequest;
import au.com.xtramile.mpiapi.model.Patient;
import au.com.xtramile.mpiapi.service.PatientMatchingService;
import au.com.xtramile.mpiapi.util.MPIUtil;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PatientMatchingServiceImpl implements PatientMatchingService {

    private static final int AUTO_MATCH_THRESHOLD = 80;
    private static final int REVIEW_THRESHOLD = 50;

    private static final int WEIGHT_NAME = 35;
    private static final int WEIGHT_DOB = 35;
    private static final int WEIGHT_PHONE = 15;
    private static final int WEIGHT_EMAIL = 15;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public MatchResultDto comparePatients(PatientRequest incoming, Patient existing) {
        Map<String, Boolean> fieldMatches = new HashMap<>();
        int totalScore = 0;
        int matchedFieldCount = 0;

        // 1. Compare Name (35 points) - using fuzzy matching for typos
        boolean nameMatch = compareName(
                MPIUtil.normalize(incoming.firstName()), MPIUtil.normalize(incoming.lastName()),
                MPIUtil.normalize(existing.getFirstName()), MPIUtil.normalize(existing.getLastName()));
        fieldMatches.put("name", nameMatch);
        if (nameMatch) {
            totalScore += WEIGHT_NAME;
            matchedFieldCount++;
        }

        // 2. Compare Date of Birth (35 points) - exact match required
        String dobStr = existing.getDob() != null ? existing.getDob().format(formatter) : "";
        boolean dobMatch = compareDateOfBirth(
                MPIUtil.normalize(incoming.dob()),
                MPIUtil.normalize(dobStr));
        fieldMatches.put("dob", dobMatch);
        if (dobMatch) {
            totalScore += WEIGHT_DOB;
            matchedFieldCount++;
        }

        // 3. Compare Phone (15 points) - normalize to digits only
        boolean phoneMatch = comparePhone(
                MPIUtil.normalizePhone(incoming.phoneNo()),
                MPIUtil.normalizePhone(existing.getPhoneNo()));
        fieldMatches.put("phone", phoneMatch);
        if (phoneMatch) {
            totalScore += WEIGHT_PHONE;
            matchedFieldCount++;
        }

        // 4. Compare Email (15 points) - lowercase and trim
        boolean emailMatch = compareEmail(
                MPIUtil.normalizeEmail(incoming.email()),
                MPIUtil.normalizeEmail(existing.getEmail()));
        fieldMatches.put("email", emailMatch);
        if (emailMatch) {
            totalScore += WEIGHT_EMAIL;
            matchedFieldCount++;
        }

        // Requirement: Minimum 2 out of 4 fields must match
        if (matchedFieldCount < 2) {
            totalScore = 0; // Force NO_MATCH if less than 2 fields match
        }

        // Determine decision based on score
        String decision;
        if (totalScore >= AUTO_MATCH_THRESHOLD) decision = "AUTO_MATCH";
        else if (totalScore >= REVIEW_THRESHOLD) decision = "REVIEW";
        else decision = "NO_MATCH";

        return new MatchResultDto(decision, totalScore, existing.getId(), fieldMatches);
    }

    @Override
    public MatchResultDto findMatch(PatientRequest request, List<Patient> existingPatients) {
        MatchResultDto bestMatch = null;
        int highestScore = 0;

        // Compare incoming patient with each existing patient
        for (Patient existing : existingPatients) {
            MatchResultDto result = comparePatients(request, existing);
            if (result.getConfidenceScore() > highestScore) {
                highestScore = result.getConfidenceScore();
                bestMatch = result;
            }
        }

        // If no existing patient scored above REVIEW threshold, it's a new patient
        if (bestMatch == null || bestMatch.getConfidenceScore() < REVIEW_THRESHOLD) {
            return MatchResultDto.builder()
                    .decision("NO_MATCH")
                    .confidenceScore(bestMatch != null ? bestMatch.getConfidenceScore() : 0)
                    .matchedPatientId(null)
                    .fieldMatches(new HashMap<>())
                    .build();
        }

        return bestMatch;
    }

    private boolean compareName(
            String firstName1,
            String lastName1,
            String firstName2,
            String lastName2
    ) {
        if (MPIUtil.isEmpty(firstName1) || MPIUtil.isEmpty(lastName1) || MPIUtil.isEmpty(firstName2) || MPIUtil.isEmpty(lastName2)) {
            return false;
        }

        // Exact match on last name and first name
        if (lastName1.equals(lastName2) && firstName1.equals(firstName2)) {
            return true;
        }

        // Fuzzy match: allow 1-2 character difference for typos
        int lastNameDistance = levenshteinDistance(lastName1, lastName2);
        int firstNameDistance = levenshteinDistance(firstName1, firstName2);

        // Allow max 1 char difference in last name, 2 in first name
        return lastNameDistance <= 1 && firstNameDistance <= 2;
    }

    private boolean compareDateOfBirth(String dob1, String dob2) {
        if (MPIUtil.isEmpty(dob1) || MPIUtil.isEmpty(dob2)) return false;

        return dob1.equals(dob2);
    }

    private boolean comparePhone(String phone1, String phone2) {
        if (MPIUtil.isEmpty(phone1) || MPIUtil.isEmpty(phone2)) return false;

        return phone1.equals(phone2);
    }

    private boolean compareEmail(String email1, String email2) {
        if (MPIUtil.isEmpty(email1) || MPIUtil.isEmpty(email2)) return false;

        return email1.equals(email2);
    }

    private int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }

        return dp[len1][len2];
    }

}
