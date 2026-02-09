import java.util.*;

public class PatientMatchingService {
    
    // Threshold constants based on healthcare MPI standards
    private static final int AUTO_MATCH_THRESHOLD = 80;
    private static final int REVIEW_THRESHOLD = 50;
    
    // Field weights (total = 100)
    private static final int WEIGHT_NAME = 35;
    private static final int WEIGHT_DOB = 35;
    private static final int WEIGHT_PHONE = 15;
    private static final int WEIGHT_EMAIL = 15;
    
    public static class IncomingPatient {
        String firstName;
        String lastName;
        String dateOfBirth; // Format: YYYY-MM-DD
        String phone;
        String email;
        
        public IncomingPatient(String firstName, String lastName, String dateOfBirth, 
                              String phone, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
            this.phone = phone;
            this.email = email;
        }
    }
    
    public static class ExistingPatient {
        UUID patientId;
        String firstName;
        String lastName;
        String dateOfBirth;
        String phone;
        String email;
        
        public ExistingPatient(UUID patientId, String firstName, String lastName, 
                              String dateOfBirth, String phone, String email) {
            this.patientId = patientId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
            this.phone = phone;
            this.email = email;
        }
    }
    
    public static class MatchResult {
        String decision; // AUTO_MATCH, REVIEW, NO_MATCH
        int confidenceScore;
        UUID matchedPatientId;
        Map<String, Boolean> fieldMatches;
        
        public MatchResult(String decision, int confidenceScore, UUID matchedPatientId,
                          Map<String, Boolean> fieldMatches) {
            this.decision = decision;
            this.confidenceScore = confidenceScore;
            this.matchedPatientId = matchedPatientId;
            this.fieldMatches = fieldMatches;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Decision: %s | Score: %d | PatientID: %s | Matches: %s",
                decision, confidenceScore, matchedPatientId, fieldMatches
            );
        }
    }
    
    /**
     * Main matching algorithm that compares incoming patient with existing patients
     * Returns the best match result
     */
    public MatchResult findMatch(IncomingPatient incoming, List<ExistingPatient> existingPatients) {
        MatchResult bestMatch = null;
        int highestScore = 0;
        
        // Compare incoming patient with each existing patient
        for (ExistingPatient existing : existingPatients) {
            MatchResult result = comparePatients(incoming, existing);
            
            if (result.confidenceScore > highestScore) {
                highestScore = result.confidenceScore;
                bestMatch = result;
            }
        }
        
        // If no existing patient scored above REVIEW threshold, it's a new patient
        if (bestMatch == null || bestMatch.confidenceScore < REVIEW_THRESHOLD) {
            return new MatchResult("NO_MATCH", bestMatch != null ? bestMatch.confidenceScore : 0, 
                                  null, new HashMap<>());
        }
        
        return bestMatch;
    }
    
    /**
     * Compares two patient records and returns match result with confidence score
     */
    private MatchResult comparePatients(IncomingPatient incoming, ExistingPatient existing) {
        Map<String, Boolean> fieldMatches = new HashMap<>();
        int totalScore = 0;
        int matchedFieldCount = 0;
        
        // 1. Compare Name (35 points) - using fuzzy matching for typos
        boolean nameMatch = compareName(
            normalize(incoming.firstName), normalize(incoming.lastName),
            normalize(existing.firstName), normalize(existing.lastName)
        );
        fieldMatches.put("name", nameMatch);
        if (nameMatch) {
            totalScore += WEIGHT_NAME;
            matchedFieldCount++;
        }
        
        // 2. Compare Date of Birth (35 points) - exact match required
        boolean dobMatch = compareDateOfBirth(
            normalize(incoming.dateOfBirth),
            normalize(existing.dateOfBirth)
        );
        fieldMatches.put("dob", dobMatch);
        if (dobMatch) {
            totalScore += WEIGHT_DOB;
            matchedFieldCount++;
        }
        
        // 3. Compare Phone (15 points) - normalize to digits only
        boolean phoneMatch = comparePhone(
            normalizePhone(incoming.phone),
            normalizePhone(existing.phone)
        );
        fieldMatches.put("phone", phoneMatch);
        if (phoneMatch) {
            totalScore += WEIGHT_PHONE;
            matchedFieldCount++;
        }
        
        // 4. Compare Email (15 points) - lowercase and trim
        boolean emailMatch = compareEmail(
            normalizeEmail(incoming.email),
            normalizeEmail(existing.email)
        );
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
        if (totalScore >= AUTO_MATCH_THRESHOLD) {
            decision = "AUTO_MATCH";
        } else if (totalScore >= REVIEW_THRESHOLD) {
            decision = "REVIEW";
        } else {
            decision = "NO_MATCH";
        }
        
        return new MatchResult(decision, totalScore, existing.patientId, fieldMatches);
    }
    
    /**
     * Compare names using Levenshtein distance for typo tolerance
     * Returns true if names are similar enough
     */
    private boolean compareName(String firstName1, String lastName1, 
                               String firstName2, String lastName2) {
        if (isEmpty(firstName1) || isEmpty(lastName1) || 
            isEmpty(firstName2) || isEmpty(lastName2)) {
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
        if (isEmpty(dob1) || isEmpty(dob2)) {
            return false;
        }
        return dob1.equals(dob2);
    }
    
    private boolean comparePhone(String phone1, String phone2) {
        if (isEmpty(phone1) || isEmpty(phone2)) {
            return false;
        }
        // After normalization, phone should be digits only
        return phone1.equals(phone2);
    }
    
    private boolean compareEmail(String email1, String email2) {
        if (isEmpty(email1) || isEmpty(email2)) {
            return false;
        }
        return email1.equals(email2);
    }
    
    // ========== Normalization Methods ==========
    
    private String normalize(String value) {
        if (value == null) return "";
        return value.trim().toLowerCase();
    }
    
    private String normalizePhone(String phone) {
        if (phone == null) return "";
        // Remove all non-digit characters: spaces, dashes, parentheses, etc.
        return phone.replaceAll("[^0-9]", "");
    }
    
    private String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }
    
    private boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
    
    /**
     * Calculates edit distance between two strings
     */
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
    
    // ========== Sample Test Cases ==========
    
    public static void main(String[] args) {
        PatientMatchingService service = new PatientMatchingService();
        
        // Existing patients in database
        List<ExistingPatient> existingPatients = Arrays.asList(
            new ExistingPatient(
                UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
                "John", "Smith", "1985-06-15", 
                "08123456789", "john.smith@email.com"
            ),
            new ExistingPatient(
                UUID.fromString("f9e8d7c6-b5a4-3210-9876-543210fedcba"),
                "Jane", "Doe", "1990-03-22",
                "08198765432", "jane.doe@email.com"
            )
        );
        
        System.out.println("=== TEST CASE 1: AUTO_MATCH (Same patient with typo) ===");
        IncomingPatient test1 = new IncomingPatient(
            "Jon", "Smith", "1985-06-15",      // Typo in first name: Jon vs John
            "0812-345-6789", "john.smith@email.com"  // Same phone (different format)
        );
        MatchResult result1 = service.findMatch(test1, existingPatients);
        System.out.println(result1);
        System.out.println();
        
        System.out.println("=== TEST CASE 2: REVIEW (Partial match - uncertain) ===");
        IncomingPatient test2 = new IncomingPatient(
            "John", "Smith", "1985-06-15",    // Name and DOB match
            "08199999999", "different@email.com"  // Different phone and email
        );
        MatchResult result2 = service.findMatch(test2, existingPatients);
        System.out.println(result2);
        System.out.println();
        
        System.out.println("=== TEST CASE 3: NO_MATCH (Different patient) ===");
        IncomingPatient test3 = new IncomingPatient(
            "Michael", "Johnson", "1995-12-01",
            "08155555555", "michael.j@email.com"
        );
        MatchResult result3 = service.findMatch(test3, existingPatients);
        System.out.println(result3);
    }
}
