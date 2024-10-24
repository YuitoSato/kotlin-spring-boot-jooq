package com.example.kotlinspringbootjooq.infrastructure

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.RenderNameStyle;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
class JooqConfig(
    private val dataSource: DataSource
) {

    @Bean
    fun transactionManager(): PlatformTransactionManager {
        return DataSourceTransactionManager(dataSource);
    }

    @Bean
    fun jooqConfiguration(): DefaultConfiguration {
        val jooqConfiguration = DefaultConfiguration();

        // トランザクションプロバイダーをSpringのトランザクションマネージャに設定
        jooqConfiguration.set(SpringTransactionProvider(transactionManager()));

        // DataSourceをTransactionAwareDataSourceProxyでラップして設定
        jooqConfiguration.set(TransactionAwareDataSourceProxy(dataSource));

        // SQLの名前スタイル設定
        val settings = Settings().withRenderNameStyle(RenderNameStyle.AS_IS);
        jooqConfiguration.setSQLDialect(SQLDialect.POSTGRES);
        jooqConfiguration.setSettings(settings);

        return jooqConfiguration;
    }

    @Bean
    fun dslContext(): DSLContext {
        return DefaultDSLContext(jooqConfiguration());
    }
}
