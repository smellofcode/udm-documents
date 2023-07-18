/*
MIT License

Copyright (c) 2023 smellofcode

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.udm.documents.folders.adapter.out.db.jdbi;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import javax.sql.DataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.enums.EnumStrategy;
import org.jdbi.v3.core.enums.Enums;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

@SpringBootApplication(scanBasePackages = TestSpringBootConfiguration.BASE_PACKAGE)
public class TestSpringBootConfiguration {
    public static final String CLASS_PATH_MODULE_NAME = "folders";
    public static final String BASE_PACKAGE = "com.udm.documents." + CLASS_PATH_MODULE_NAME;

    @Bean
    @DependsOn("liquibase")
    public Jdbi jdbi() {
        final var jdbi = Jdbi.create(dataSource()).installPlugin(new SqlObjectPlugin());
        jdbi.configure(Enums.class, enums -> enums.setEnumStrategy(EnumStrategy.BY_NAME));
        return jdbi;
    }

    @Bean
    @LiquibaseDataSource
    public DataSource dataSource() {
        final var config = new HikariConfig();
        config.setMaximumPoolSize(2);
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(
                "jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS testschema;");
        config.setConnectionInitSql("SET SCHEMA testschema");
        return new TransactionAwareDataSourceProxy(new HikariDataSource(config));
    }

    @Bean
    public SpringLiquibase liquibase() {
        final var config = new SpringLiquibase();
        config.setDataSource(dataSource());
        config.setChangeLog("classpath:db/changelog/" + CLASS_PATH_MODULE_NAME + "/root.yaml");
        final var parameters = new HashMap<String, String>();
        parameters.put("db-schema-name", "testschema");
        config.setChangeLogParameters(parameters);
        return config;
    }
}
