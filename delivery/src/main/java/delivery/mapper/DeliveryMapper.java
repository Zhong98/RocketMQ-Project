package delivery.mapper;

import delivery.entity.Delivery;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DeliveryMapper {
    int createDelivery(Delivery delivery);
}
