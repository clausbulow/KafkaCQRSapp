package dk.ksf.application;

import com.zaxxer.hikari.HikariDataSource;
import dk.ksf.application.matrikel.model.Matrikel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource({"classpath:persistence-multiple-db-boot.properties"})
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "dk.ksf.application",
        entityManagerFactoryRef = "readstoreEntityManager",
        transactionManagerRef = "readstoreTransactionManager")
public class DatasourceConfig {
    @Autowired
    Environment env;

    @Primary
    @Bean("readstoreProps")
    @ConfigurationProperties(prefix = "readstore.datasource")
    DataSourceProperties getDatabaseProps() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean("readstoreDatasource")
    public DataSource readDataSource(@Qualifier("readstoreProps") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }


    @Primary
    @Bean("readstoreTransactionManager")
    PlatformTransactionManager readstoreTransactionManager(@Qualifier("readstoreEntityManager") LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return new JpaTransactionManager(entityManagerFactoryBean.getObject());
    }

    @Primary
    @Bean("readstoreEntityManager")
    LocalContainerEntityManagerFactoryBean readstoreEntityManager(@Qualifier("readstoreDatasource") DataSource ds) {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);


        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(ds);
        factoryBean.setJpaVendorAdapter(vendorAdapter);
        factoryBean.setJpaDialect(new DefaultJpaDialect());
        factoryBean.setPackagesToScan(Matrikel.class.getPackage().getName());
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect",env.getProperty("readstore.hibernate.dialect"));
        properties.setProperty("hibernate.hbm2ddl.auto",env.getProperty("readstore.hibernate.hbm2ddl.auto"));
        factoryBean.setJpaProperties(properties);
        return factoryBean;
    }
}
