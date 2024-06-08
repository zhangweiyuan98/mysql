package com.zwy.appformysql.Helper;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class DatabaseHelper {
    private Connection connection;
    private static final Logger logger = Logger.getLogger(DatabaseHelper.class.getName());

    public DatabaseHelper(String host, String databaseName ,String username, String password) {
        try {
            String url = "jdbc:mysql://" + host + "/" + databaseName;
            this.connection = DriverManager.getConnection(url, username, password);
            String currentDate = LocalDate.now().toString();

            // 创建日志文件并配置日志处理器
            FileHandler fileHandler = new FileHandler(currentDate+".log", true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            // 处理数据库连接异常，并记录日志
            logger.severe("数据库连接异常: " + e.getMessage());
        }
    }

    // 执行查询，并返回结果集
    public ResultSet executeQuery(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        logger.info("执行查询SQL语句: " + sql);
        return statement.executeQuery(sql);
    }

    // 执行更新操作，返回受影响的行数
    public int executeUpdate(String sql) throws SQLException {
        Statement statement = connection.createStatement();
        logger.info("执行更新SQL语句: " + sql);
        return statement.executeUpdate(sql);
    }

    // 执行插入操作，返回是否插入成功
    public boolean executeInsert(String sql) {
        try {
            Statement statement = connection.createStatement();
            logger.info("执行插入SQL语句: " + sql);
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            logger.severe("执行插入SQL语句失败: " + e.getMessage());
            return false;
        }
    }

    // 执行删除操作，返回是否删除成功
    public boolean executeDelete(String sql) {
        try {
            Statement statement = connection.createStatement();
            logger.info("执行删除SQL语句: " + sql);
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            logger.severe("执行删除SQL语句失败: " + e.getMessage());
            return false;
        }
    }

    // 创建表，返回是否创建成功
    public boolean createTable(String sql) {
        try {
            Statement statement = connection.createStatement();
            logger.info("创建表: " + sql);
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            logger.severe("创建表失败: " + e.getMessage());
            return false;
        }
    }

    // 修改表结构，返回是否修改成功
    public boolean alterTable(String sql) {
        try {
            Statement statement = connection.createStatement();
            logger.info("修改表结构: " + sql);
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            logger.severe("修改表结构失败: " + e.getMessage());
            return false;
        }
    }

    // 其他数据库操作方法...

    // 关闭数据库连接
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("数据库连接已关闭");
            } catch (SQLException e) {
                logger.severe("关闭数据库连接异常: " + e.getMessage());
            }
        }
    }
}
