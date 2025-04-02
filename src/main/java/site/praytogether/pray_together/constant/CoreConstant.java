package site.praytogether.pray_together.constant;

public class CoreConstant {
  private static final int RDBMS_CHAR_LEN_BYTE = 3;

  public static class MemberConstant {
    public static final int EMAIL_MAX_LEN = 50 * RDBMS_CHAR_LEN_BYTE;
    public static final int NAME_MAX_LEN = 20 * RDBMS_CHAR_LEN_BYTE;
    public static final int PASSWORD_MAX_LEN = 20 * RDBMS_CHAR_LEN_BYTE;
  }

  public static class RoomConstant {
    public static final int NAME_MAX_LEN = 50 * RDBMS_CHAR_LEN_BYTE;
    public static final int DESCRIPTION_MAX_LEN = 255 * RDBMS_CHAR_LEN_BYTE;
  }

  public static class MemberRoomConstant {
    public static final int ROLE_MAX_LEN = 10 * RDBMS_CHAR_LEN_BYTE;
    public static final String DEFAULT_INFINITE_SCROLL_ORDER_BY = "time";
    public static final String DEFAULT_INFINITE_SCROLL_AFTER = "0";
    public static final String DEFAULT_INFINITE_SCROLL_DIR = "desc";
    public static final int ROOMS_INFINITE_SCROLL_SIZE = 10;
  }

  public static class JwtConstant {
    public static final String ACCESS_TYPE = "access";
    public static final String REFRESH_TYPE = "refresh";
    public static final String HTTP_HEADER_AUTHORIZATION = "Authorization";
    public static final String HTTP_HEADER_AUTH_BEARER = "Bearer ";
  }
}
