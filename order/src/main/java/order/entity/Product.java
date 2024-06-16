package order.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Product {
    private int id;
    private int price;
    private int stock;
}
