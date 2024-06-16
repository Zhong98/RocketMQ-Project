package delivery.consumer;

import com.google.gson.Gson;
import delivery.entity.Delivery;
import delivery.entity.Order;
import delivery.mapper.DeliveryMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Random;

@Slf4j
@Component
@RocketMQMessageListener(topic = "order", consumerGroup = "delivery-group")
public class DeliveryConsumer implements RocketMQListener<String>{
        @Autowired
        DeliveryMapper deliveryMapper;

        Gson gson = new Gson();

        @Override
        public void onMessage(String message) {
            Order order = gson.fromJson(message, Order.class);
            Delivery delivery = new Delivery();
            delivery.setId(100_000_000 + new Random().nextInt(900_000_000));
            delivery.setUsername(order.getUsername());
            delivery.setProductId(order.getProductId());
            delivery.setAmount(order.getAmount());
            delivery.setAddress(order.getAddress());
            delivery.setTel(order.getTel());
            delivery.setCreateTime(new Timestamp(System.currentTimeMillis()));
            try {
                deliveryMapper.createDelivery(delivery);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
}
