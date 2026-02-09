package au.com.xtramile.mpiapi.util;

public class MPIUtil {

    public static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    public static String normalizePhone(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("[^0-9]", "");
    }

    public static String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase();
    }

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

}
