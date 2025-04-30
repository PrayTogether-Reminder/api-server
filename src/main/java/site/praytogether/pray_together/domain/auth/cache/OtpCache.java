package site.praytogether.pray_together.domain.auth.cache;

public interface OtpCache {
  void put(String key, String value);

  String delete(String key);

  String get(String key);
}
