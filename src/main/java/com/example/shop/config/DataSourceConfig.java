package com.example.shop.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String url = firstNonNull(
                env.getProperty("JDBC_DATABASE_URL"),
                env.getProperty("SPRING_DATASOURCE_URL"),
                env.getProperty("DATABASE_URL"),
                env.getProperty("spring.datasource.url")
        );
        String username = firstNonNull(
                env.getProperty("DB_USERNAME"),
                env.getProperty("SPRING_DATASOURCE_USERNAME"),
                env.getProperty("DATABASE_USERNAME"),
                env.getProperty("spring.datasource.username"),
                "sa"
        );
        String password = firstNonNull(
                env.getProperty("DB_PASSWORD"),
                env.getProperty("SPRING_DATASOURCE_PASSWORD"),
                env.getProperty("DATABASE_PASSWORD"),
                env.getProperty("spring.datasource.password"),
                ""
        );

        if (url == null || url.isBlank()) {
            throw new IllegalStateException(
                    "No database URL configured. Set JDBC_DATABASE_URL, SPRING_DATASOURCE_URL, DATABASE_URL or spring.datasource.url"
            );
        }

        if (!url.startsWith("jdbc:")) {
            if (url.startsWith("postgresql://")) {
                url = "jdbc:" + url;
            } else if (url.startsWith("postgres://")) {
                url = "jdbc:postgresql://" + url.substring("postgres://".length());
            }
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    private static String firstNonNull(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
