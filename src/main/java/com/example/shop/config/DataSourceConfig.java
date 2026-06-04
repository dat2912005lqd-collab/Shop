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
                env.getProperty("DATABASE_URL")
        );
        String username = firstNonNull(
                env.getProperty("DB_USERNAME"),
                env.getProperty("SPRING_DATASOURCE_USERNAME"),
                env.getProperty("DATABASE_USERNAME"),
                "postgres"
        );
        String password = firstNonNull(
                env.getProperty("DB_PASSWORD"),
                env.getProperty("SPRING_DATASOURCE_PASSWORD"),
                env.getProperty("DATABASE_PASSWORD"),
                "123456"
        );

        if (url == null || url.isBlank()) {
            url = "jdbc:postgresql://localhost:5432/shopdb";
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
        dataSource.setDriverClassName("org.postgresql.Driver");
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
