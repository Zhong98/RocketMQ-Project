package order.mapper;

import order.entity.Order;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface OrderMapper {
    int createOrder(Order order);
    Order getOrderById(int id);
}
