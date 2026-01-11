package site.praytogether.pray_together.domain.base;

public final class EmailMaskingUtil {

  private EmailMaskingUtil() {
  }

  public static String mask(String email) {
    if (email == null || !email.contains("@")) {
      return "***";
    }

    String[] parts = email.split("@");
    String local = parts[0];
    String domain = parts[1];

    if (local.length() <= 2) {
      return "**@" + domain;
    }

    return local.substring(0, 2) + "***@" + domain;
  }
}
