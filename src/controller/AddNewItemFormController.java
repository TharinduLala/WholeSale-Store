package controller;

import db.DbConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddNewItemFormController {
    public AnchorPane newItemFormContext;
    public TextField txtItemCode;
    public TextField txtPackSize;
    public TextField txtDescription;
    public TextField txtQtyOnhand;
    public TextField txtUniPrice;

    public void addNewItemAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if (isEmpty()){
            new Alert(Alert.AlertType.WARNING,"Please Input All Details").show();
        }else {
            Item item=new Item(
                    txtItemCode.getText(),
                    txtDescription.getText(),
                    txtPackSize.getText(),
                    Integer.parseInt(txtQtyOnhand.getText()),
                    Double.parseDouble(txtUniPrice.getText())
            );
            if (alreadyExists(txtItemCode.getText())){
                new Alert(Alert.AlertType.WARNING, "Item Already Added..").show();
            }else {
                if (new ItemController().addNewItem(item)) {
                    new Alert(Alert.AlertType.INFORMATION, "Saved..").show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again..").show();
                }
            }
        }
    }

    private boolean isEmpty(){
        if (
                txtItemCode.getText().equals("")||
                        txtDescription.getText().equals("")||
                        txtPackSize.getText().equals("")||
                        txtQtyOnhand.getText().equals("")||
                        txtUniPrice.getText().equals("")
        ) {
            return true;
        }else {
            return false;
        }
    }

    private boolean alreadyExists(String itemCode) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance()
                .getConnection().prepareStatement("SELECT * FROM Item WHERE ItemCode=?");
        stm.setObject(1,itemCode);
        ResultSet rst = stm.executeQuery();
        if (rst.next()) {
            return true;
        } else {
            return false;
        }
    }
}
