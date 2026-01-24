package au.com.xtramile.mpiapi.util;

public class CommonUtil {

    public static String normalizeName(String name) {
        return name == null ? "" : name.trim().toUpperCase();
    }

    public static String normalizePhone(String phone) {
        return phone == null ? "" : phone.replaceAll("\\s+", "");
    }

    public static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

}
