package controller;

import db.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Customer;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class ManageCustomersFormController {
    public TableView tblAllCustomers;
    public TableColumn colId;
    public TableColumn colTitle;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colCity;
    public TableColumn colProvince;
    public TableColumn colPostalCode;
    public TextField txtCustId;
    public TextField txtCustName;
    public TextField txtCustTitle;
    public TextField txtCustAddress;


    public void searchAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(txtCustId.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Enter Customer Id").show();
        }else {
            PreparedStatement stm = DbConnection.getInstance()
                    .getConnection().prepareStatement("SELECT * FROM Customer WHERE CustId=?");
            stm.setObject(1, txtCustId.getText());
            ResultSet set = stm.executeQuery();
            if (set.next()) {
                txtCustTitle.setText(set.getString(2));
                txtCustName.setText(set.getString(3));
                txtCustAddress.setText(set.getString(4));
            } else {
                new Alert(Alert.AlertType.WARNING,"Empty Result Set").show();
            }
        }
    }

    private   void loadAllCustomers() throws SQLException, ClassNotFoundException {
        ObservableList<Customer> customerObservableList =FXCollections.observableArrayList();
        PreparedStatement stm = DbConnection.getInstance().getConnection().prepareStatement("SELECT * FROM Customer");
        ResultSet set = stm.executeQuery();
        while (set.next()){
            customerObservableList.add(new Customer(
                    set.getString(1),
                    set.getString(2),
                    set.getString(3),
                    set.getString(4),
                    set.getString(5),
                    set.getString(6),
                    set.getString(7)
            ));
        }
        tblAllCustomers.setItems(customerObservableList);
    }

    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("custId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("custTitle"));
        colName.setCellValueFactory(new PropertyValueFactory<>("custName"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        colProvince.setCellValueFactory(new PropertyValueFactory<>("province"));
        colPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        try {
            loadAllCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void deleteCustomerAction(ActionEvent actionEvent) {
        ButtonType yes =new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
        ButtonType no =new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do You want to Delete this Customer",yes,no);
        alert.setTitle("Confirmation Alert");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.orElse(no)==yes) {
            try {
                if (deleteCustomer(txtCustId.getText())) {
                    new Alert(Alert.AlertType.INFORMATION, "Deleted").show();
                    txtCustId.setText("");
                    txtCustAddress.setText("");
                    txtCustName.setText("");
                    txtCustTitle.setText("");
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
                loadAllCustomers();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {

        }

    }

    public boolean deleteCustomer(String id) throws SQLException, ClassNotFoundException {
        if (DbConnection.getInstance().getConnection().prepareStatement("DELETE FROM Customer WHERE CustId='"+id+"'").executeUpdate()>0){
            return true;
        }else{
            return false;
        }
    }
}
