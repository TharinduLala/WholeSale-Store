package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DbConnection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Item;
import model.ItemDetails;
import model.Order;
import view.tm.CartTm;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CashierDashBoardFormController  {

    public Label lblDate;
    public Label lblTime;
    public Label lblUserName;
    public Label lblDisplayUniPrice;
    public Label lblDisplayQtyOnHand;
    public Label lblDisplayTotal;
    public Label lblOrderId;
    public JFXComboBox <String>cmbSelectItemCode;
    public JFXTextField txtDisplayCustomerName;
    public JFXTextField txtDisplayCustomerTitle;
    public JFXTextField txtDisplayDiscription;
    public JFXTextField txtDislpayPackSize;
    public TextField txtbuyQty;
    public TextField txtDiscount;
    public TableView<CartTm> tblCart;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colTotal;
    public TableColumn colDiscount;
    public TableColumn colNetTotal;
    public AnchorPane cashierDashDoardContext;
    public JFXTextField txtSearchCustomer;
    ObservableList<CartTm>cartTmObservableList=FXCollections.observableArrayList();
    int cartRowForRemove=-1;


    public void initialize() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colNetTotal.setCellValueFactory(new PropertyValueFactory<>("netTotal"));
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        lblUserName.setText("User-001");
        setDateTime();
        setOrderId();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            loadItemIds();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        cmbSelectItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            try {
                setItemData(newValue);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } );
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        tblCart.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            cartRowForRemove=(int)newValue;
        });
    }

    public void addToCartAction(ActionEvent actionEvent) {
        if (txtDisplayDiscription.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Select Item To Add").show();

        }else if(txtbuyQty.getText().equals("")||Integer.parseInt(txtbuyQty.getText())==0){
            new Alert(Alert.AlertType.WARNING,"Please Input Quantity to be purchased").show();
        }else {
            String description=txtDisplayDiscription.getText();
            int qtyOnHand=Integer.parseInt(lblDisplayQtyOnHand.getText());
            double unitPrice=Double.parseDouble(lblDisplayUniPrice.getText());
            int qtyForCustomer=Integer.parseInt(txtbuyQty.getText());
            double discount;
            if (txtDiscount.getText().equals("")){
                discount=0;
            }else {
                discount=Double.parseDouble(txtDiscount.getText());
            }
            double total=qtyForCustomer*unitPrice;
            double netTotal=total-((total*discount)/100);
            if (qtyOnHand<qtyForCustomer){
                new Alert(Alert.AlertType.WARNING,"Not Enough Qty In Store").show();
                return;
            }
            CartTm tm = new CartTm(
                    cmbSelectItemCode.getValue(),
                    description,
                    qtyForCustomer,
                    unitPrice,
                    total,
                    discount,
                    netTotal
            );
            int rowNumber = isExists(tm);
            if (isExists(tm) == -1) {
                cartTmObservableList.add(tm);
            } else {
                CartTm temp = cartTmObservableList.get(rowNumber);
                CartTm newTm = new CartTm(
                        temp.getItemCode(),
                        temp.getDescription(),
                        temp.getQty() + qtyForCustomer,
                        unitPrice,
                        total + temp.getTotal(),
                        discount,
                        (total + temp.getTotal()) - ((total + temp.getTotal()) * discount / 100)
                );
                if(newTm.getQty()>qtyOnHand){
                    new Alert(Alert.AlertType.WARNING,"Not Enough Qty In Store").show();
                    return;
                }else {
                    cartTmObservableList.remove(rowNumber);
                    cartTmObservableList.add(newTm);
                }
            }
            tblCart.setItems(cartTmObservableList);
            calculateCost();
        }
    }

    public void clearCartRowAction(ActionEvent actionEvent) {
        if (cartRowForRemove==-1){
            new Alert(Alert.AlertType.WARNING,"Please Select a Row").show();
        }else {
            cartTmObservableList.remove(cartRowForRemove);
            calculateCost();
            tblCart.refresh();
        }
    }

    public void proceedAction(ActionEvent actionEvent) {
        ArrayList<ItemDetails>items=new ArrayList<>();
        double netCost=0;
        for (CartTm tempTm:cartTmObservableList
             ) {
            netCost+=tempTm.getNetTotal();
            items.add(new ItemDetails(
                    tempTm.getItemCode(),
                    tempTm.getQty(),
                    tempTm.getDiscount()
            ));
        }

        Order order=new Order(
                lblOrderId.getText(),
                txtSearchCustomer.getText(),
                lblDate.getText(),
                netCost,
                items
        );
        if (new OrderController().placeOrder(order)){
            new Alert(Alert.AlertType.INFORMATION,"Order Added...").show();
            clerAfterAdded();
            setOrderId();
        }else{
            new Alert(Alert.AlertType.WARNING,"Try Again...").show();
        }
    }

    public void searchCustIdAction(ActionEvent actionEvent) throws SQLException, ClassNotFoundException {
        if(txtSearchCustomer.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Enter Customer Id").show();
        }else {
            PreparedStatement stm = DbConnection.getInstance()
                    .getConnection().prepareStatement("SELECT * FROM Customer WHERE CustId=?");
            stm.setObject(1, txtSearchCustomer.getText());
            ResultSet set = stm.executeQuery();
            if (set.next()) {
                txtDisplayCustomerTitle.setText(set.getString(2));
                txtDisplayCustomerName.setText(set.getString(3));
            } else {
                new Alert(Alert.AlertType.WARNING,"Empty Result Set").show();
            }
        }
    }

    private void setDateTime(){
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(f.format(date));

        Timeline time = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh : mm a");
            String t=simpleDateFormat.format(new Date());
            lblTime.setText(t);
        }),
                new KeyFrame(Duration.seconds(1))
        );
        time.setCycleCount(Animation.INDEFINITE);
        time.play();
    }

    private void loadItemIds() throws SQLException, ClassNotFoundException {
        List<String>itemIds=new ItemController().getAllItemIds();
        cmbSelectItemCode.getItems().addAll(itemIds);
    }

    private void setItemData(String itemCode ) throws SQLException, ClassNotFoundException {
        Item i1=new ItemController().getItem(itemCode);
        if (i1==null){

        }else {
            txtDisplayDiscription.setText(i1.getDescription());
            txtDislpayPackSize.setText(i1.getPackSize());
            lblDisplayQtyOnHand.setText(String.valueOf(i1.getQtyOnHand()));
            lblDisplayUniPrice.setText(String.valueOf(i1.getUniitPrice()));
        }
    }

    private int isExists(CartTm tm){
        for (int i = 0; i <cartTmObservableList.size() ; i++) {
            if(tm.getItemCode().equals(cartTmObservableList.get(i).getItemCode())){
                return i;
            }
        }
        return -1;
    }

    void calculateCost(){
        double total=0;
        for (CartTm tm:cartTmObservableList
             ) {
            total+=tm.getNetTotal();
        }
        lblDisplayTotal.setText(total+"/=");
    }

    private void setOrderId(){
        try {
            lblOrderId.setText(new OrderController().getOrderId());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openAddCustomerAction(ActionEvent actionEvent) throws IOException {
        Stage stage=new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/AddNewCustomerForm.fxml"))));
        stage.show();
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public void openManageCustomerAction(ActionEvent actionEvent) throws IOException {
        Stage stage =new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/ManageCustomersForm.fxml"))));
        stage.show();
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    public void openManageOrderAction(ActionEvent actionEvent) throws IOException {
        Stage stage=new Stage();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/ManageOrdersForm.fxml"))));
        stage.show();
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
    }

    private void clerAfterAdded(){
        txtSearchCustomer.setText("");
        txtDisplayCustomerTitle.setText("");
        txtDisplayCustomerName.setText("");
        cmbSelectItemCode.setValue("");
        txtDislpayPackSize.setText("");
        txtDisplayDiscription.setText("");
        txtbuyQty.setText("");
        txtDiscount.setText("");
        lblDisplayUniPrice.setText("");
        lblDisplayQtyOnHand.setText("");
        lblDisplayTotal.setText("");
        tblCart.setItems(null);

    }

    public void logOutAction(MouseEvent mouseEvent) throws IOException {
        Stage window = (Stage) cashierDashDoardContext.getScene().getWindow();
        window.setScene(new Scene(FXMLLoader.load(getClass().getResource("../view/LogInForm.fxml"))));
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        window.setX((primScreenBounds.getWidth() - window.getWidth()) / 2);
        window.setY((primScreenBounds.getHeight() - window.getHeight()) / 2);
    }
}
