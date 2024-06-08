package com.zwy.appformysql.Controller;
import com.zwy.appformysql.Config.ConfigurationReader;
import com.zwy.appformysql.Helper.DatabaseHelper;


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
//    private TableView ResultTable;
//    private TableView<String> ResultTable;
    private void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @FXML
    private void onSelectButtonClick() throws SQLException {
        System.out.println("点击了查询" + HotelCode.getText() + "d" + HotelName.getText());


        DatabaseHelper f = new DatabaseHelper( "10.242.117.34", "portal_pms" ,"root",  "QRs0s9SZi7mk5dm4");

        ResultSet resultSet = f.executeQuery("select hotel_group_id, id, code, sta, audit, descript, descript_en, descript_short from hotel limit 10;");


        ResultTable.getColumns().clear();
        ResultTable.getItems().clear();

        TableView<List<String>> tableView = new TableView<>();

        setResultTable tableManager = new setResultTable(tableView);
        ResultTable.getSelectionModel().setCellSelectionEnabled(true);
        // 设置表格列
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
    // 添加行号列
        TableColumn<List<String>, Integer> indexColumn = new TableColumn<>("#");
        indexColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(ResultTable.getItems().indexOf(cellData.getValue()) + 1));
        ResultTable.getColumns().add(0, indexColumn); // 将行号列添加到第一个位置

        // 动态生成表格列
        for (int i = 1; i <= columnCount; i++) {
            final int columnIndex = i;
            TableColumn<List<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex - 1)));
            ResultTable.getColumns().add(column);

        }

        // 更新表格数据

        ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                row.add(resultSet.getString(i));
            }
            ResultTable.getItems().add(row);
        }

        tableManager.updateTableView(data);
        System.out.println(data);




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
    private void onEutexSqlClick(){
        System.out.println("点击了执行语句");
    }
    @FXML
    private void onExportExcelClick(){
        System.out.println("点击了导出Excel");
    }

    @FXML
    private void initialize() {
        ConfigurationReader readerGroup = new ConfigurationReader("F:/App/AppForMysql/group.ini");
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
                ConfigurationReader readerServer = new ConfigurationReader("F:/App/AppForMysql/" + selectedGroup + ".ini");
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


    public class setResultTable {
        @FXML
        public TableView ResultTable;

        public setResultTable(TableView<List<String>> resultTable) {
            this.ResultTable = resultTable;
        }

        // 设置表格列

        public void setTableColumns(List<String> columnNames) {
            for (String columnName : columnNames) {
                final int finalColumnIndex = ResultTable.getColumns().size(); // 使用size()作为索引
                TableColumn<List<String>, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(cellData -> {
                    List<String> rowData = cellData.getValue();
                    if (rowData != null && rowData.size() > finalColumnIndex) {
                        return new SimpleStringProperty(rowData.get(finalColumnIndex));
                    } else {
                        return new SimpleStringProperty("");
                    }
                });
                column.setPrefWidth(100); // 设置列宽以便看到内容
                ResultTable.getColumns().add(column);
            }

            // 假设你有一个ObservableList<List<String>>的数据源
            // resultTable.setItems(yourDataSource);
        }



        // 更新表格数据
        public void updateTableView(ObservableList<ObservableList<String>> data) {
            ResultTable.getItems().addAll(data);
        }
    }


}