module com.zwy.appformysql {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.zwy.appformysql to javafx.fxml;
    exports com.zwy.appformysql;
    exports com.zwy.appformysql.Controller;
    opens com.zwy.appformysql.Controller to javafx.fxml;
    exports com.zwy.appformysql.Config;
    opens com.zwy.appformysql.Config to javafx.fxml;

}