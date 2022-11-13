package com.dndcraft.atlas.io.sql;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode @Accessors(fluent=true)
@FieldDefaults(level= AccessLevel.PRIVATE)
public final class MySQLDetails {
    private final String HOSTNAME;
    private final String USERNAME;
    private final String PASSWORD;
    private final String DATABASE;
    private final String PORT;
}
