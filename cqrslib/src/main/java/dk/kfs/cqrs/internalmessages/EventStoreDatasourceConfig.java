package dk.kfs.cqrs.internalmessages;

import com.zaxxer.hikari.HikariDataSource;
import dk.kfs.cqrs.internalmessages.events.model.EventStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "eventstoreEntityManager",
        transactionManagerRef = "eventstoreTransactionManager",
        basePackages = {"dk.kfs.cqrs.internalmessages.events.model"})
@PropertySource({"classpath:persistence-multiple-db-boot.properties"})
public class EventStoreDatasourceConfig {
    @Autowired
    Environment env;
    @Bean("eventstoreProps")
    @ConfigurationProperties(prefix = "eventstore.datasource")
    DataSourceProperties getDatabaseProps() {
        return new DataSourceProperties();
    }

    @Bean("eventstoreDatasource")
    public DataSource readDataSource(@Qualifier("eventstoreProps") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean("eventstoreTransactionManager")
    PlatformTransactionManager eventstoreTransactionManager(@Qualifier("eventstoreEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }

    @Bean("eventstoreEntityManager")
    LocalContainerEntityManagerFactoryBean eventstoreEntityManager(@Qualifier("eventstoreDatasource") DataSource ds) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(false);
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setPackagesToScan(EventStoreRepository.class.getPackage().getName());
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect",env.getProperty("eventstore.hibernate.dialect"));
        properties.setProperty("hibernate.hbm2ddl.auto",env.getProperty("eventstore.hibernate.hbm2ddl.auto"));
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }

}
