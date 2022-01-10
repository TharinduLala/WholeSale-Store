package controller;

import db.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Customer;
import model.Item;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageItemsFormController {
    public AnchorPane manageItemsFormContext;
    public TextField txtPackSize;
    public TextField txtUnitPrice;
    public TextField txtDescription;
    public TextField txtQtyOnHand;
    public TableView<Item> tblItems;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colPackSize;
    public TableColumn colUnitPrice;
    public TableColumn colQtyOnHand;
    public TextField txtSearchItemCode;

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        colQtyOnHand.setCellValueFactory(new PropertyValueFactory<>("qtyOnHand"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("uniitPrice"));
        try {
            loadAllItems();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void confirmEditsAction(ActionEvent actionEvent){
        ButtonType yes =new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
        ButtonType no =new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do You want to Save this changes",yes,no);
        alert.setTitle("Confirmation Alert");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.orElse(no)==yes) {
            Item item = new Item(
                    txtSearchItemCode.getText(),
                    txtDescription.getText(),
                    txtPackSize.getText(),
                    Integer.parseInt(txtQtyOnHand.getText()),
                    Double.parseDouble(txtUnitPrice.getText())
            );
            try {
                if (new ItemController().updateItem(item)) {
                    loadAllItems();
                    new Alert(Alert.AlertType.INFORMATION, "Item Updated......").show();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
                loadAllItems();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                loadAllItems();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {

        }
    }

    public void removeItemAction(ActionEvent actionEvent) {
        ButtonType yes =new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
        ButtonType no =new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do You want to remove this item",yes,no);
        alert.setTitle("Confirmation Alert");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.orElse(no)==yes) {
            try {
                if (new ItemController().removeItem(txtSearchItemCode.getText())) {
                    new Alert(Alert.AlertType.INFORMATION, "Item Removed......").show();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
                loadAllItems();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {

        }
    }

    private void clearFields() {
        txtDescription.setText("");
        txtPackSize.setText("");
        txtQtyOnHand.setText("");
        txtUnitPrice.setText("");
        txtSearchItemCode.setText("");
    }

    public void loadAllItems() throws SQLException, ClassNotFoundException {
        ObservableList<Item> itemObservableList = FXCollections.observableArrayList();
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Item");
        ResultSet set = stm.executeQuery();
        while (set.next()){
            itemObservableList.add(new Item(
                    set.getString(1),
                    set.getString(2),
                    set.getString(3),
                    set.getInt(4),
                    set.getDouble(5)
            ));
        }
        tblItems.setItems(itemObservableList);
    }

    public void searchItemAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(txtSearchItemCode.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Enter Item Code").show();
        }else {
            PreparedStatement stm = DbConnection.getInstance()
                    .getConnection().prepareStatement("SELECT * FROM Item WHERE ItemCode=?");
            stm.setObject(1, txtSearchItemCode.getText());
            ResultSet set = stm.executeQuery();
            if (set.next()) {
                txtDescription.setText(set.getString(2));
                txtPackSize.setText(set.getString(3));
                txtQtyOnHand.setText(set.getString(4));
                txtUnitPrice.setText(set.getString(5));
            } else {
                new Alert(Alert.AlertType.WARNING,"Empty Result Set").show();
            }
        }
    }

}
