package com.harsh.studentmgmt.util;

import com.harsh.studentmgmt.config.DatabaseConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public final class SchemaInitializer {
    private SchemaInitializer() {
    }

    public static void initialize() {
        String schemaSql = loadSchemaSql();
        try (Connection connection = DatabaseConfig.getConnection();
             Statement statement = connection.createStatement()) {
            Arrays.stream(schemaSql.split(";"))
                    .map(String::trim)
                    .filter(sql -> !sql.isBlank())
                    .forEach(sql -> {
                        try {
                            statement.execute(sql);
                        } catch (SQLException e) {
                            throw new RuntimeException("Failed to execute SQL: " + sql, e);
                        }
                    });
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize schema", e);
        }
    }

    private static String loadSchemaSql() {
        try (InputStream input = SchemaInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (input == null) {
                throw new IllegalStateException("schema.sql not found in resources");
            }
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append('\n');
                }
            }
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read schema.sql", e);
        }
    }
}
