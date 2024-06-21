package com.github.rin.javaauto;

import java.util.UUID;

public interface Identifiable {
    UUID id = UUID.randomUUID();
    UUID getId();
}
