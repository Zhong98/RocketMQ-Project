package order.service.impl;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import order.entity.Order;
import order.entity.Product;
import order.entity.User;
import order.mapper.ProductMapper;
import order.mapper.UserMapper;
import order.service.PayService;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    RocketMQTemplate rocketMQTemplate;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserMapper userMapper;

    List<Product> products = new ArrayList<>();
    Gson gson = new Gson();

    @Override
    @Retryable
    public String pay(String username, int productId, int amount) {
        if (products.isEmpty()) {
            products = productMapper.selectAllProduct();
        }

        Product product = null;
        try {
            product = products.get(productId - 1);
        } catch (IndexOutOfBoundsException e) {
            return "该商品不存在";
        }

        int price = product.getPrice();
        int totalPrice = price * amount;

        User user = userMapper.getUserByName(username);
        if (user.getBalance() < totalPrice) {
            return "You don't have enough money";
        } else if (amount > product.getStock()) {
            return "Sorry, we don't have enough stock to meet your demand";
        }

        //创建订单对象
        Order order = new Order();
        order.setId(100_000_000 + new Random().nextInt(900_000_000));
        order.setUsername(username);
        order.setTel(user.getTel());
        order.setAddress(user.getAddress());
        order.setProductId(productId);
        order.setAmount(amount);
        order.setTotalPrice(totalPrice);
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));

        Message<String> message = MessageBuilder.withPayload(gson.toJson(order)).build();
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction("order", message, null);
        LocalTransactionState localTransactionState = sendResult.getLocalTransactionState();
        log.info(localTransactionState.toString());

        return "OK";
    }
}
