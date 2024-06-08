package com.zwy.appformysql.Controller;
import com.zwy.appformysql.Config.ConfigurationReader;
import com.zwy.appformysql.Helper.DatabaseHelper;


import com.zwy.appformysql.mapper.ResultTableManager;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableView;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class mainController {

    @FXML
    private TableView<List<String>> ResultTable;
    @FXML
    private Label welcomeText;
    @FXML
    private TextField HotelCode;
    @FXML
    private TextField HotelName;
    @FXML
    private TextField HotelId;
    @FXML
    private TextField BrandCode;
    @FXML
    private TextField Limit;
    @FXML
    private TextField Databasename;
    @FXML
    private TextField TableName;
    @FXML
    private Button SelectButton;
    @FXML
    private ToggleButton EnableButton;
    @FXML
    private CheckBox GroupCheck;
    @FXML
    private Button ChoiceExcel;
    @FXML
    private ChoiceBox ExcelSheet;
    @FXML
    private Button Import;
    @FXML
    private ChoiceBox<String> PCID;
    @FXML
    private ChoiceBox<String> ServerName;
    @FXML
    private  Button EutexSql;
    @FXML
    private  Button ExportExcel;
    @FXML
    private  TextArea SqlText;
    @FXML
    private TableView<List<String>> resultTable;
    @FXML
    private void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @FXML
    private void onSelectButtonClick() throws SQLException {

        // 假设你有一个Map来存储字段和它们的值
        Map<String, String> conditions = new HashMap<>();
        conditions.put("CODE", HotelCode.getText().trim());
        conditions.put("descript", HotelName.getText().trim());
        conditions.put("id", HotelId.getText().trim());
        conditions.put("brand_code", BrandCode.getText().trim());
        // 过滤掉空值
        List<String> nonEmptyConditions = conditions.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .map(entry -> entry.getKey() + " = '" + entry.getValue() + "'")
                .collect(Collectors.toList());

        // 构建查询语句
        String sql = "SELECT * FROM hotel WHERE " +
                String.join(" AND ", nonEmptyConditions);

        // 如果nonEmptyConditions为空，则可能需要处理这种情况
        if (nonEmptyConditions.isEmpty()) {
            // 没有添加任何条件，处理这种情况...
            sql = "SELECT * FROM hotel limit "+Limit.getText(); // 或者抛出异常、返回空结果等
        }
        StringBuilder SqlConcat = new StringBuilder(sql);
        SqlConcat.append(" ORDER BY id ;");

        ConfigurationReader Server = new ConfigurationReader("AppForMysql/group.ini");
        Set<String> sectionNames = Server.getSectionNames();
        String pcidValue = PCID.getValue();
        if ("所有".equals(pcidValue)) {
            // 如果PCID的值为"所有"，则遍历所有节
            for (String sectionName : sectionNames) {
                // 假设每个节都有相同的数据库配置键（host, password, database, user）
                String host = Server.getValue(sectionName, "host");
                String password = Server.getValue(sectionName, "password");
                String database = Server.getValue(sectionName, "database");
                String user = Server.getValue(sectionName, "user");

                // 执行数据库查询等操作...
                Exsql(SqlConcat, host, password, database, user);

            }
        }else {

                String host = Server.getValue(PCID.getValue(),"host");
        //        String port = Server.getValue(PCID.getValue(),"port");
                String password = Server.getValue(PCID.getValue(),"password");
                String database = Server.getValue(PCID.getValue(),"database");
                String user = Server.getValue(PCID.getValue(),"user");
                Exsql(SqlConcat, host, password, database, user);


        }


    }




    @FXML
    private void onGroupCheckClick(){
        System.out.println("打开了导入数据");
    }
    @FXML
    private void onChoiceExcelClick(){
        System.out.println("选择了Exce;");
    }
    @FXML
    private void onEutexSqlClick() throws SQLException {
        String sql = SqlText.getText();
        System.out.println(sql);
        String pcidValue = PCID.getValue();
        ConfigurationReader Server = new ConfigurationReader("AppForMysql/"+pcidValue+".ini");
        Set<String> sectionNames = Server.getSectionNames();
        String server = ServerName.getValue();
        StringBuilder SqlConcat = new StringBuilder(sql);

        SqlConcat.append(" ");

        if ("所有".equals(server)) {
            // 如果PCID的值为"所有"，则遍历所有节
            for (String sectionName : sectionNames) {
                // 假设每个节都有相同的数据库配置键（host, password, database, user）
                String host = Server.getValue(sectionName, "host");
                String password = Server.getValue(sectionName, "password");
                String database = Server.getValue(sectionName, "database");
                String user = Server.getValue(sectionName, "user");

                // 执行数据库查询等操作...
//                Exsql(SqlConcat, host, password, database, user);
                for (String s : Arrays.asList(host, password, database, user)) {
                    System.out.println(s);
                }

            }
        }else {

            String host = Server.getValue(server,"host");
        //String port = Server.getValue(PCID.getValue(),"port");
            String password = Server.getValue(server,"password");
            String database = Server.getValue(server,"database");
            String user = Server.getValue(server,"user");
            Exsql(SqlConcat, host, password, database, user);
            for (String s : Arrays.asList(host, password, database, user)) {
                System.out.println(s);
            }

        }
    }
    @FXML
    private void onExportExcelClick(){
        System.out.println("点击了导出Excel");
    }

    @FXML
    private void initialize() {
        ConfigurationReader readerGroup = new ConfigurationReader("AppForMysql/group.ini");
        ObservableList<String> sectionNames = FXCollections.observableArrayList(readerGroup.getSectionNames());
        PCID.setItems(sectionNames);
        PCID.getItems().add("所有");
        // 设置第一个下拉菜单的默认值
        if (!sectionNames.isEmpty()) {
            PCID.setValue(sectionNames.get(0));
        }

    }


    @FXML
    private void  ChoicePCID(){
        // 获取所选的配置文件名称
        String selectedGroup = PCID.getValue();
        if (selectedGroup.equals("所有")){
            ServerName.setDisable(true);
            ServerName.setValue("所有");
        }else { ServerName.setDisable(false);

            // 根据选择的配置文件名称获取对应的配置项
            try {
                ConfigurationReader readerServer = new ConfigurationReader("AppForMysql/"+selectedGroup + ".ini");
                ObservableList<String> configOption = FXCollections.observableArrayList(readerServer.getSectionNames());
                // 清空第二个下拉菜单的选项
                ServerName.setItems(configOption);
                ServerName.getItems().add("所有");

                // 设置第二个下拉菜单的默认值（这里选择第一个选项）
                if (!configOption.isEmpty()) {
                    ServerName.setValue(configOption.get(0));
                } }
            catch (Exception e) {
                // 将异常信息记录到日志中
                System Logger = null;
                System.Logger logger = Logger.getLogger(getClass().getName());
                logger.log(System.Logger.Level.ERROR,"Error loading configuration for group: " + selectedGroup, e);
            }

        };

    }

    @FXML
    private void onEnableButtonClick() {

       if(EnableButton.isSelected()) {
           EnableButton.setText("关闭");
           setImportOn(false);

       } else {
           EnableButton.setText("启用");
           setImportOn(true);
       }
    }
    @FXML
    private void  setImportOn(boolean flag ){
        Databasename.setDisable(flag );
        TableName.setDisable(flag );
        ChoiceExcel.setDisable(flag );
        ExcelSheet.setDisable(flag );
        Import.setDisable(flag );
        GroupCheck.setDisable(flag );

    }
    private void Exsql(StringBuilder sqlConcat, String host, String password, String database, String user) throws SQLException {
        DatabaseHelper server = new DatabaseHelper(host, database, user, password);

        ResultSet result = server.executeQuery(String.valueOf(sqlConcat));

        ResultSetMetaData metaData = result.getMetaData();
        List<String> columnNames = new ArrayList<>();
        ResultTableManager manager = new ResultTableManager(ResultTable);
        manager.setTableColumns(metaData, columnNames);
        manager.updateTableItems(result);
        ResultTable.setEditable(true);
    }




}