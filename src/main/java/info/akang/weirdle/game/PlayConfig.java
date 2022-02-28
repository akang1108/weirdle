package info.akang.weirdle.game;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayConfig {
    public static final PlayConfig DEFAULT = PlayConfig.builder()
            .sessionTimeToLiveSeconds(15 * 60)
            .sessionEvictionPeriodSeconds(60)
            .maxGuesses(8)
            .build();

    private final long sessionTimeToLiveSeconds;
    private final long sessionEvictionPeriodSeconds;
    private final int maxGuesses;
}
