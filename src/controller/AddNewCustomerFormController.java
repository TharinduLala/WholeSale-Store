package controller;

import db.DbConnection;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddNewCustomerFormController {
    public TextField txtId;
    public TextField txtName;
    public TextField txtTitle;
    public TextField txtAddress;
    public TextField txtCity;
    public TextField txtProvince;
    public TextField txtPostalCode;
    public AnchorPane addCostomerFormContext;

    public void addNewCustomerAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(txtId.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Enter Customer Details").show();
        }else{
            Customer customer=new Customer(
                    txtId.getText(),
                    txtTitle.getText(),
                    txtName.getText(),
                    txtAddress.getText(),
                    txtCity.getText(),
                    txtProvince.getText(),
                    txtPostalCode.getText()
            );
            if (alreadyExists(txtId.getText())) {
                new Alert(Alert.AlertType.WARNING, "Customer Already Added..").show();
            } else {
                if (saveCustomer(customer)) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Saved..");
                    alert.show();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again..").show();
                }
            }
        }
    }
    
    private boolean saveCustomer(Customer c) throws SQLException, ClassNotFoundException {
        Connection con= DbConnection.getInstance().getConnection();
        String query="INSERT INTO Customer VALUES(?,?,?,?,?,?,?)";
        PreparedStatement stm=con.prepareStatement(query);
        stm.setObject(1,c.getCustId());
        stm.setObject(2,c.getCustTitle());
        stm.setObject(3,c.getCustName());
        stm.setObject(4,c.getAddress());
        stm.setObject(5,c.getCity());
        stm.setObject(6,c.getProvince());
        stm.setObject(7,c.getPostalCode());
        return stm.executeUpdate()>0;
    }

    private boolean alreadyExists(String id) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance()
                .getConnection().prepareStatement("SELECT * FROM Customer WHERE CustId=?");
        stm.setObject(1,id);
        ResultSet rst = stm.executeQuery();
        if (rst.next()) {
            return true;
        } else {
            return false;
        }
    }


}