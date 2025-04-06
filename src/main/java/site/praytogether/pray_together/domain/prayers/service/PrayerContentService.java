package site.praytogether.pray_together.domain.prayers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.prayers.respository.PrayerContentRepository;

@Service
@RequiredArgsConstructor
public class PrayerContentService {
  private final PrayerContentRepository contentRepository;
}
