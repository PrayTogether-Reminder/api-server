package site.praytogether.pray_together.domain.prayers.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.praytogether.pray_together.domain.prayers.respository.PrayerTitleRepository;

@Service
@RequiredArgsConstructor
public class PrayerTitleService {
  private final PrayerTitleRepository titleRepository;
}
