package it.gsync.common.data.impl;

import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.gsync.common.data.DataConnector;
import it.gsync.common.data.types.ConnectionDetails;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HikariConnector extends DataConnector {

    private HikariDataSource dataSource;

    public HikariConnector(Logger logger, ConnectionDetails details) {
        super(logger, details);
    }


    @Override
    public void connect() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:mysql://" + getDetails().getHost() + ":" + getDetails().getPort() + "/" + getDetails().getDatabase());
        hikariConfig.setUsername(getDetails().getUsername());
        if(getDetails().isAuth()) {
            hikariConfig.setPassword(getDetails().getPassword());
        }
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setConnectionTimeout(30000);
        hikariConfig.setIdleTimeout(12000);
        hikariConfig.setLeakDetectionThreshold(30000);
        hikariConfig.addDataSourceProperty( "cachePrepStmts" , "true" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSize" , "250" );
        hikariConfig.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource = new HikariDataSource(hikariConfig);
        createTables();
    }


    public void createTables() {
        try(Connection connection = dataSource.getConnection()) {
            connection
                    .createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS punishments (id INT NOT NULL primary key AUTO_INCREMENT,uuid VARCHAR(64),playerName VARCHAR(64),server VARCHAR(64),checkType VARCHAR(64),punishType VARCHAR(64),date VARCHAR(64));");
            connection
                    .createStatement()
                    .executeUpdate("CREATE TABLE IF NOT EXISTS flags (id int NOT NULL primary key AUTO_INCREMENT,uuid VARCHAR(64),playerName VARCHAR(64),server VARCHAR(64),detection VARCHAR(64),checkType VARCHAR(64),violations INT,ping INT,tps DOUBLE,timestamp BIGINT);");

        } catch (SQLException ex) {
            getLogger().log(Level.SEVERE,"Error while connecting to database: ",ex);
        }
    }

    @Override
    public <T> T fetchlastObject(Class<T> type, String field, Object value) {
        PreparedStatement findStatement = null;
        T obj = null;
        try(Connection connection = dataSource.getConnection()) {
            if(type.isAssignableFrom(Flag.class)) {
                findStatement = connection.prepareStatement("SELECT * FROM flags WHERE " + field + " = ? ORDER BY id DESC");
                findStatement.setObject(1,value);
                ResultSet resultSet = findStatement.executeQuery();
                if(resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String playerName = resultSet.getString("playerName");
                    String server = resultSet.getString("server");
                    String detection = resultSet.getString("detection");
                    String checkType = resultSet.getString("checkType");
                    int violations = resultSet.getInt("violations");
                    int ping = resultSet.getInt("ping");
                    double tps = resultSet.getInt("tps");
                    long timestamp = resultSet.getLong("timestamp");
                    obj = (T) new Flag(uuid,playerName,server,detection,checkType,violations,ping,tps,timestamp);
                }
            } else if(type.isAssignableFrom(Punish.class)) {
                findStatement = connection.prepareStatement("SELECT * FROM punishments WHERE " + field + " = ? ORDER BY id DESC");
                findStatement.setObject(1,value);
                ResultSet resultSet = findStatement.executeQuery();
                if(resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String playerName = resultSet.getString("playerName");
                    String server = resultSet.getString("server");
                    String checkType = resultSet.getString("checkType");
                    String punishType = resultSet.getString("punishType");
                    String date = resultSet.getString("date");
                    obj = (T) new Punish(uuid,playerName,server,checkType,punishType,date);
                }
            } else {
                throw new IllegalArgumentException("Type not found");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        } finally {
            if(findStatement != null) {
                try {
                    findStatement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return obj;
    }


    @Override
    public <T> List<T> fetchObjects(Class<T> type, String field, Object value) {
        PreparedStatement findStatement = null;
        List<T> obj = Lists.newArrayList();
        try(Connection connection = dataSource.getConnection()) {
            if(type.isAssignableFrom(Flag.class)) {
                findStatement = connection.prepareStatement("SELECT * FROM flags WHERE " + field + " = ?");
                findStatement.setObject(1,value);
                ResultSet resultSet = findStatement.executeQuery();
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String playerName = resultSet.getString("playerName");
                    String server = resultSet.getString("server");
                    String detection = resultSet.getString("detection");
                    String checkType = resultSet.getString("checkType");
                    int violations = resultSet.getInt("violations");
                    int ping = resultSet.getInt("ping");
                    double tps = resultSet.getInt("tps");
                    long timestamp = resultSet.getLong("timestamp");
                    Flag flag = new Flag(uuid,playerName,server,detection,checkType,violations,ping,tps,timestamp);
                    obj.add((T) flag);
                }
            } else if(type.isAssignableFrom(Punish.class)) {
                findStatement = connection.prepareStatement("SELECT * FROM punishments WHERE " + field + " = ?");
                findStatement.setObject(1,value);
                ResultSet resultSet = findStatement.executeQuery();
                while (resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String playerName = resultSet.getString("playerName");
                    String server = resultSet.getString("server");
                    String checkType = resultSet.getString("checkType");
                    String punishType = resultSet.getString("punishType");
                    String date = resultSet.getString("date");
                    Punish punish = new Punish(uuid,playerName,server,checkType,punishType,date);
                    obj.add((T) punish);
                }
            } else {
                throw new IllegalArgumentException("Type not found");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return obj;
        } finally {
            if(findStatement != null) {
                try {
                    findStatement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        return obj;
    }

    @Override
    public void saveObject(Object object) {
        PreparedStatement insertStatement = null;
        try(Connection connection = dataSource.getConnection()) {
            if(object instanceof Flag) {
                Flag flag = (Flag) object;
                insertStatement = connection.prepareStatement("INSERT INTO flags (uuid,playerName,server,detection,checkType,violations,ping,tps,timestamp) VALUES (?,?,?,?,?,?,?,?,?);");
                insertStatement.setString(1,flag.getUuid().toString());
                insertStatement.setString(2,flag.getPlayerName());
                insertStatement.setString(3,flag.getServer());
                insertStatement.setString(4,flag.getDetection());
                insertStatement.setString(5,flag.getCheckType());
                insertStatement.setInt(6,flag.getVl());
                insertStatement.setInt(7,flag.getPing());
                insertStatement.setDouble(8,flag.getTps());
                insertStatement.setLong(9,flag.getTimestamp());
                insertStatement.executeUpdate();
            } else if(object instanceof Punish) {
                Punish punish = (Punish) object;
                insertStatement = connection.prepareStatement("INSERT INTO punishments (uuid,playerName,server,checkType,punishType,date) VALUES (?,?,?,?,?,?);");
                insertStatement.setString(1,punish.getUuid().toString());
                insertStatement.setString(2,punish.getPlayerName());
                insertStatement.setString(3,punish.getServer());
                insertStatement.setString(4,punish.getCheckType());
                insertStatement.setString(5,punish.getPunishType());
                insertStatement.setString(6,punish.getDate());
                insertStatement.executeUpdate();
            } else {
                throw new IllegalArgumentException("Type not found");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            if(insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        }
    }
}
