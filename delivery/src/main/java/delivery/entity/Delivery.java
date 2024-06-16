package delivery.entity;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class Delivery {
    private int id;
    private String username;
    private int productId;
    private int amount;
    private String address;
    private String tel;
    private Timestamp createTime;
}
