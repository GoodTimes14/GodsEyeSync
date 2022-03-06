package it.gsync.common.data.types;

import it.gsync.common.dependencies.Dependency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum StorageType {

    MONGO(new Dependency(
            "org.mongodb",
            "mongo-java-driver",
            "3.12.8",
            "org.mongodb.MongoClient")
    ),
    MYSQL(new Dependency(
            "com.zaxxer",
            "HikariCP",
            "4.0.3",
            "com.zaxxer.hikari.HikariDataSource"),
            new Dependency(
                    "mysql",
                    "mysql-connector-java",
                    "8.0.24",
                    "com.mysql.jdbc.Connection"
            ),
            new Dependency(
                    "org.slf4j",
                    "slf4j-api",
                    "1.7.30",
                    "org.slf4j.LoggerFactory")
    )
    ,H2(new Dependency("com.h2database",
            "h2",
            "2.1.210",
            "org.h2.Driver")
    );

    private Dependency[] dependencies;

    StorageType(Dependency... dependencies) {
        this.dependencies = dependencies;
    }

}
