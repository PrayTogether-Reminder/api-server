package site.praytogether.pray_together.domain.prayers.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.prayers.service.PrayerContentService;
import site.praytogether.pray_together.domain.prayers.service.PrayerTitleService;

@Service
@RequiredArgsConstructor
public class PrayerApplicationService {
  private final PrayerTitleService titleService;
  private final PrayerContentService contentService;
}
