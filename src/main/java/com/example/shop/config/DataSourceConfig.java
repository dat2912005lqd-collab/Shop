package com.example.shop.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        String rawUrl = firstNonNull(
                env.getProperty("JDBC_DATABASE_URL"),
                env.getProperty("SPRING_DATASOURCE_URL"),
                env.getProperty("DATABASE_URL"),
                env.getProperty("spring.datasource.url")
        );

        String username = firstNonNull(
                env.getProperty("DB_USERNAME"),
                env.getProperty("SPRING_DATASOURCE_USERNAME"),
                env.getProperty("DATABASE_USERNAME"),
                env.getProperty("spring.datasource.username")
        );

        String password = firstNonNull(
                env.getProperty("DB_PASSWORD"),
                env.getProperty("SPRING_DATASOURCE_PASSWORD"),
                env.getProperty("DATABASE_PASSWORD"),
                env.getProperty("spring.datasource.password")
        );

        JdbcConfig jdbcConfig = normalizeJdbcUrl(rawUrl);

        if (username == null || username.isBlank()) {
            username = jdbcConfig.username;
        }
        if (password == null) {
            password = jdbcConfig.password;
        }

        String dialect = jdbcConfig.url.startsWith("jdbc:postgresql:")
                ? "org.hibernate.dialect.PostgreSQLDialect"
                : "org.hibernate.dialect.H2Dialect";
        System.setProperty("spring.jpa.database-platform", dialect);
        System.setProperty("spring.jpa.properties.hibernate.dialect", dialect);
        System.setProperty("hibernate.dialect", dialect);
        System.setProperty("jakarta.persistence.jdbc.url", jdbcConfig.url);

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(jdbcConfig.url);
        dataSource.setUsername(username == null ? "sa" : username);
        dataSource.setPassword(password == null ? "" : password);

        if (jdbcConfig.url.startsWith("jdbc:postgresql:")) {
            dataSource.setDriverClassName("org.postgresql.Driver");
        }

        return dataSource;
    }

    private static JdbcConfig normalizeJdbcUrl(String rawUrl) {
        if (rawUrl == null || rawUrl.isBlank()) {
            return new JdbcConfig("jdbc:h2:mem:shopdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        }

        String url = rawUrl.trim();
        if (url.startsWith("postgres://") || url.startsWith("postgresql://")) {
            try {
                URI uri = new URI(url);
                String userInfo = uri.getUserInfo();
                String username = null;
                String password = null;
                if (userInfo != null) {
                    int colonIndex = userInfo.indexOf(":");
                    if (colonIndex >= 0) {
                        username = userInfo.substring(0, colonIndex);
                        password = userInfo.substring(colonIndex + 1);
                    } else {
                        username = userInfo;
                    }
                }
                String host = uri.getHost();
                int port = uri.getPort();
                String database = uri.getPath();
                if (database == null || database.isBlank()) {
                    throw new IllegalStateException("DATABASE_URL must include a database name");
                }
                String jdbcUrl = "jdbc:postgresql://" + host + (port > 0 ? ":" + port : "") + database;
                return new JdbcConfig(jdbcUrl, username, password);
            } catch (URISyntaxException ex) {
                throw new IllegalStateException("Invalid DATABASE_URL value: " + rawUrl, ex);
            }
        }

        if (!url.startsWith("jdbc:")) {
            throw new IllegalStateException("Invalid database URL format. Use a JDBC URL or postgres:// URL.");
        }

        return new JdbcConfig(url, null, null);
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

    private static class JdbcConfig {
        final String url;
        final String username;
        final String password;

        JdbcConfig(String url, String username, String password) {
            this.url = url;
            this.username = username;
            this.password = password;
        }
    }
}
