package order.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Delivery {
    private int id;
    private int userId;
    private String productList;
    private String address;
    private String createTime;
}
