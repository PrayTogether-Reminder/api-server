package site.praytogether.pray_together.domain.auth.cache;

public interface RefreshTokenCache {
  void save(String key, String value);

  String delete(String key);

  String get(String key);

  boolean isExist(String key);
}
