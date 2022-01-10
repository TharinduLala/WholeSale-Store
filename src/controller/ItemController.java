package controller;

import db.DbConnection;
import model.Customer;
import model.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemController {

    public List<String> getAllItemIds() throws SQLException, ClassNotFoundException {
        ResultSet rst = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM Item").executeQuery();
        List<String>ids=new ArrayList<>();
        while (rst.next()){
            ids.add(rst.getString(1));
        }
        return ids;
    }

    public Item getItem(String id) throws SQLException, ClassNotFoundException {
        ResultSet rst = DbConnection.getInstance().getConnection().
                prepareStatement("SELECT * FROM Item WHERE ItemCode='" + id + "'").executeQuery();
        if(rst.next()){
            return new Item(
                    rst.getString(1),
                    rst.getString(2),
                    rst.getString(3),
                    rst.getInt(4),
                    rst.getDouble(5)
            );
        }else {
            return null;
        }
    }

    public boolean addNewItem(Item i) throws SQLException, ClassNotFoundException {
        Connection con= DbConnection.getInstance().getConnection();
        String query="INSERT INTO Item VALUES(?,?,?,?,?)";
        PreparedStatement stm=con.prepareStatement(query);
        stm.setObject(1,i.getItemCode());
        stm.setObject(2,i.getDescription());
        stm.setObject(3,i.getPackSize());
        stm.setObject(4,i.getQtyOnHand());
        stm.setObject(5,i.getUniitPrice());
        return stm.executeUpdate()>0;
    }

    public boolean removeItem(String itemCode) throws SQLException, ClassNotFoundException {
        if (DbConnection.getInstance().getConnection().prepareStatement("DELETE FROM Item WHERE ItemCode='"+itemCode+"'").executeUpdate()>0){
            return true;
        }else{
            return false;
        }
    }
    
    public boolean updateItem(Item item) throws SQLException, ClassNotFoundException {
        PreparedStatement stm = DbConnection.getInstance().getConnection().
                prepareStatement("UPDATE Item SET Description=?,PackSize=?,QtyOnHand=?,UnitPrice=? WHERE ItemCode=?");
        stm.setObject(1,item.getDescription());
        stm.setObject(2,item.getPackSize());
        stm.setObject(3,item.getQtyOnHand());
        stm.setObject(4,item.getUniitPrice());
        stm.setObject(5,item.getItemCode());
        return stm.executeUpdate()>0;
    }

}
