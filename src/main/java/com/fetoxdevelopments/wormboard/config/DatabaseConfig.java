package com.fetoxdevelopments.wormboard.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories("com.fetoxdevelopments.wormboard.repository")
@ComponentScan({"com.fetoxdevelopments.wormboard.app", "com.fetoxdevelopments.wormboard.controller", "com.fetoxdevelopments.wormboard.worker",
                "com.fetoxdevelopments.wormboard.status"})
public class DatabaseConfig
{
  @Bean
  public DataSource dataSource()
  {
    return DataSourceBuilder.create().username("postgres").password("bollox").url("jdbc:postgresql://localhost:5432/staticdump")
      .driverClassName("org.postgresql.Driver").build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, JpaVendorAdapter jpaVendorAdapter)
  {
    LocalContainerEntityManagerFactoryBean lef = new LocalContainerEntityManagerFactoryBean();
    lef.setDataSource(dataSource);
    lef.setJpaVendorAdapter(jpaVendorAdapter);
    lef.setPackagesToScan("com.fetoxdevelopments.wormboard.domain");
    return lef;
  }

  @Bean
  public JpaVendorAdapter jpaVendorAdapter()
  {
    HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setShowSql(true);
    hibernateJpaVendorAdapter.setGenerateDdl(false);
    hibernateJpaVendorAdapter.setDatabase(Database.POSTGRESQL);
    return hibernateJpaVendorAdapter;
  }
}
