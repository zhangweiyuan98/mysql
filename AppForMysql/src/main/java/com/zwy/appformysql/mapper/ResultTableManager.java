package com.zwy.appformysql.mapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

public class ResultTableManager {
    private TableView<List<String>> resultTable;

    public ResultTableManager(TableView<List<String>> resultTable) {
        this.resultTable = resultTable;
    }

    public void setTableColumns(ResultSetMetaData metaData, List<String> columnNames) throws SQLException {
        resultTable.getColumns().clear();

        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            TableColumn<List<String>, String> column = new TableColumn<>(metaData.getColumnName(i));

            final int finalI = i;
            column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(finalI - 1)));
            resultTable.getColumns().add(column);


        }

    }

    public void updateTableItems(ResultSet resultSet) throws SQLException {
        resultTable.getItems().clear();
        while (resultSet.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                row.add(resultSet.getString(i));
            }
            resultTable.getItems().add(row);
        }
    }

    ContextMenu contextMenu = new ContextMenu();
    MenuItem copyItem = new MenuItem("Copy");



}
