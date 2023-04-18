package com.ts.visualranks.configuration.implementation;

import com.ts.visualranks.configuration.ReloadableConfig;
import com.ts.visualranks.database.DatabaseType;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Description;
import net.dzikoysk.cdn.source.Resource;
import net.dzikoysk.cdn.source.Source;

import java.io.File;

public class PluginConfiguration implements ReloadableConfig {

    @Description({ " ", "# Database section" })
    public DatabaseSection databaseSection = new DatabaseSection();

    @Contextual
    public static class DatabaseSection {
        public DatabaseType databaseType = DatabaseType.MYSQL;

        public String hostname = "localhost";
        public String database = "visualrank";
        public String username = "root";
        public String password = "";
        public int port = 3306;
    }

    @Override
    public Resource resource(File folder) {
        return Source.of(folder, "config.yml");
    }

}