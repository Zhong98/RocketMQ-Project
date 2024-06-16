package order.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private int id;
    private String username;
    private String address;
    private int balance;
    private String tel;
}
