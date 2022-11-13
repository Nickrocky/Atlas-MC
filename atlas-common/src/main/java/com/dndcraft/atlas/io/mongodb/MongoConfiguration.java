package com.dndcraft.atlas.io.mongodb;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class MongoConfiguration {
    private final String username, database, password, ipAddress, port;
}
