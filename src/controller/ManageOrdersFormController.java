package controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import db.DbConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Item;
import model.ItemDetails;
import model.Order;
import view.tm.CartTm;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ManageOrdersFormController {
    public TableView tblShowOrder;
    public TableColumn colItemCode;
    public TableColumn colDescription;
    public TableColumn colQty;
    public TableColumn colUnitPrice;
    public TableColumn colTotal;
    public TableColumn colDiscount;
    public TableColumn colNetTotal;
    public Label lblOrderId;
    public Label lblCustId;
    public JFXComboBox <String>cmbSelectItemCode;
    public JFXTextField txtDescription;
    public JFXTextField txtPackSize;
    public Label lblUnitPrice;
    public Label lblQtyOnHand;
    public TextField txtInputQty;
    public TextField txtInputDiscount;
    public Label lblNetTotal;
    public JFXComboBox<String> cmbSelectId;
    ObservableList<CartTm>cartTmObList=FXCollections.observableArrayList();
    int cartRowForRemove=-1;


    public void clearRowAction(ActionEvent actionEvent) {
        if (cartRowForRemove==-1){
            new Alert(Alert.AlertType.WARNING,"Please Select a Row").show();
        }else {
            cartTmObList.remove(cartRowForRemove);
            calculateCost();
            tblShowOrder.refresh();
        }
    }

    public void confirmEditAction(ActionEvent actionEvent) {
        ButtonType yes =new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
        ButtonType no =new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do You want to save this changes",yes,no);
        alert.setTitle("Confirmation Alert");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.orElse(no)==yes) {
            ArrayList<ItemDetails> items = new ArrayList<>();
            double netCost = 0;
            for (CartTm tempTm : cartTmObList
            ) {
                netCost += tempTm.getNetTotal();
                items.add(new ItemDetails(
                        tempTm.getItemCode(),
                        tempTm.getQty(),
                        tempTm.getDiscount()
                ));
            }

            Order order = new Order(
                    lblOrderId.getText(),
                    lblCustId.getText(),
                    getDate(),
                    netCost,
                    items
            );
            if (new OrderController().updateOrder(order)) {
                new Alert(Alert.AlertType.INFORMATION, "Order Updated...").show();
                clerAfterAdded();
            } else {
                new Alert(Alert.AlertType.WARNING, "Try Again...").show();
            }
        }else {

        }
    }

    public void addToOrderAction(ActionEvent actionEvent) {
        if (txtDescription.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Select Item To Add").show();

        }else if(txtInputQty.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"Please Input Quantity to be purchased").show();
        }else {
            String description=txtDescription.getText();
            int qtyOnHand=Integer.parseInt(lblQtyOnHand.getText());
            double unitPrice=Double.parseDouble(lblUnitPrice.getText());
            int qtyForCustomer=Integer.parseInt(txtInputQty.getText());
            double discount;
            if (txtInputDiscount.getText().equals("")){
                discount=0;
            }else {
                discount=Double.parseDouble(txtInputDiscount.getText());
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
                cartTmObList.add(tm);
            } else {
                CartTm temp = cartTmObList.get(rowNumber);
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
                    cartTmObList.remove(rowNumber);
                    cartTmObList.add(newTm);
                }
            }
            tblShowOrder.setItems(cartTmObList);
            calculateCost();
        }
    }

    public void removeOrderAction(ActionEvent actionEvent){
        ButtonType yes =new ButtonType("Yes",ButtonBar.ButtonData.OK_DONE);
        ButtonType no =new ButtonType("No",ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert=new Alert(Alert.AlertType.CONFIRMATION,"Do You want to romove this order",yes,no);
        alert.setTitle("Confirmation Alert");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.orElse(no)==yes) {
            try {
                if (new OrderController().removeOrder(lblOrderId.getText())) {
                    new Alert(Alert.AlertType.INFORMATION, "Order Removed....").show();
                    loadOrderIds();
                    clerAfterAdded();
                } else {
                    new Alert(Alert.AlertType.WARNING, "Try Again").show();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {

        }
    }

    private void loadOrderIds() throws SQLException, ClassNotFoundException {
        ObservableList<String>idOblist= FXCollections.observableArrayList();
        ResultSet set = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM `Order`").executeQuery();
        while (set.next()){
            idOblist.add(set.getString(1));
        }
        cmbSelectId.setItems(idOblist);
    }

    public void initialize(){
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colNetTotal.setCellValueFactory(new PropertyValueFactory<>("netTotal"));
        try {
            loadOrderIds();
            loadItemIds();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        cmbSelectId.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                setOrderData(newValue);
                setOrderDetailsInTable();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        tblShowOrder.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            cartRowForRemove=(int)newValue;
        });
        cmbSelectItemCode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            try {
                setItemData(newValue);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } );
    }

    private void setOrderData(String orderId) throws SQLException, ClassNotFoundException {
        ResultSet rst = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM `Order` WHERE OrderId = '"+orderId+"'").executeQuery();
        if(rst.next()){
            lblOrderId.setText( rst.getString(1));
             lblCustId.setText(rst.getString(2));
             lblNetTotal.setText(rst.getString(4)+"/=");

        }else {

        }
    }

    private ObservableList<ItemDetails> getOrderDetails(String orderId) throws SQLException, ClassNotFoundException {
        ObservableList<ItemDetails>items=FXCollections.observableArrayList();
        ResultSet rst = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM `Order Detail` WHERE OrderId = '"+orderId+"'").executeQuery();
        while (rst.next()){
            ItemDetails itemDetails = new ItemDetails(
                rst.getString(2),rst.getInt(3),rst.getDouble(4)
            );
            items.add(itemDetails);
        }
        return items;
    }

    private void setOrderDetailsInTable() throws SQLException, ClassNotFoundException {
        ObservableList<ItemDetails> itemList = getOrderDetails(lblOrderId.getText());
        for (int i = 0; i <itemList.size() ; i++) {
            if (itemList.get(i)==null){

            }else {
                String tempItemCode=itemList.get(i).getItemCode();
                Item tempItem=new ItemController().getItem(tempItemCode);
                String decsription=tempItem.getDescription();
                int qty=itemList.get(i).getQty();
                double unitPrice=tempItem.getUniitPrice();
                double total=qty*unitPrice;
                double discount=itemList.get(i).getDiscount();
                double netTotal=total-(total*discount/100);
                CartTm temp=new CartTm(
                        tempItemCode,
                        decsription,
                        qty,
                        unitPrice,
                        total,
                        discount,
                        netTotal
                );
                cartTmObList.add(temp);
            }
        }
        tblShowOrder.setItems(cartTmObList);

    }

    private String getDate(){
        Date date = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        return f.format(date);
    }

    private void loadItemIds() throws SQLException, ClassNotFoundException {
        List<String>itemIds=new ItemController().getAllItemIds();
        cmbSelectItemCode.getItems().addAll(itemIds);
    }

    private void setItemData(String itemCode ) throws SQLException, ClassNotFoundException {
        Item i1=new ItemController().getItem(itemCode);
        if (i1==null){

        }else {
            txtDescription.setText(i1.getDescription());
            txtPackSize.setText(i1.getPackSize());
            lblQtyOnHand.setText(String.valueOf(i1.getQtyOnHand()));
            lblUnitPrice.setText(String.valueOf(i1.getUniitPrice()));
        }
    }

    private int isExists(CartTm tm){
        for (int i = 0; i <cartTmObList.size() ; i++) {
            if(tm.getItemCode().equals(cartTmObList.get(i).getItemCode())){
                return i;
            }
        }
        return -1;
    }

    void calculateCost(){
        double total=0;
        for (CartTm tm:cartTmObList
        ) {
            total+=tm.getNetTotal();
        }
        lblNetTotal.setText(total+"/=");
    }

    private void clerAfterAdded(){
        lblCustId.setText("");
        lblOrderId.setText("");
        cmbSelectItemCode.setValue("");
        cmbSelectId.setValue("");
        txtPackSize.setText("");
        txtDescription.setText("");
        txtInputQty.setText("");
        txtInputDiscount.setText("");
        lblUnitPrice.setText("");
        lblQtyOnHand.setText("");
        lblNetTotal.setText("");
        tblShowOrder.setItems(null);
        cartTmObList.clear();
    }
}
