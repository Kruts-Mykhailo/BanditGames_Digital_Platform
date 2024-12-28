package be.kdg.integration5.statisticscontext.domain;

import java.util.Objects;
import java.util.UUID;

public record SessionId(UUID uuid) {
    public SessionId {
        Objects.requireNonNull(uuid);
    }
}
