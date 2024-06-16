package delivery.entity;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class Order {
    private int id;
    private String username;
    private String tel;
    private String address;
    private int productId;
    private int amount;
    private int totalPrice;
    private Timestamp createTime;
}
