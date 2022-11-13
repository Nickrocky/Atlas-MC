package com.dndcraft.atlas.wrapper;

import com.dndcraft.atlas.agnostic.AgnosticObject;
import com.dndcraft.atlas.agnostic.Config;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.bukkit.configuration.file.FileConfiguration;

@RequiredArgsConstructor
public class BukkitConfig implements AgnosticObject<FileConfiguration>, Config {
	
	@Getter
	@Delegate(types=Config.class)
	private final FileConfiguration handle;
}
