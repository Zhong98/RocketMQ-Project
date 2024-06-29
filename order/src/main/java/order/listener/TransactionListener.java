package order.listener;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import order.entity.Order;
import order.entity.Product;
import order.entity.User;
import order.mapper.OrderMapper;
import order.mapper.ProductMapper;
import order.mapper.UserMapper;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RocketMQTransactionListener
public class TransactionListener implements RocketMQLocalTransactionListener {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserMapper userMapper;

    Gson gson = new Gson();

    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        String str = new String((byte[]) message.getPayload());
        Order order = gson.fromJson(str ,Order.class);
        try {
            User user = userMapper.getUserByName(order.getUsername());
            Product product = productMapper.selectProductById(order.getProductId());

            int price = product.getPrice();
            int totalPrice = price * order.getAmount();

            orderMapper.createOrder(order);
            updateBalance(user, totalPrice);
            updateProductStock(product, order.getAmount());

            return RocketMQLocalTransactionState.COMMIT;
        }catch (Exception e){
            log.error(e.getMessage());
            return RocketMQLocalTransactionState.ROLLBACK;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        Order order = gson.fromJson((String) message.getPayload(),Order.class);
        Order orderById = orderMapper.getOrderById(order.getId());
        if (orderById == null) {
            return RocketMQLocalTransactionState.ROLLBACK;
        }
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Transactional
    public void updateBalance(User user, int totalPrice) {
        try {
            userMapper.updateBalance(user.getUsername(), user.getBalance() - totalPrice);
        } catch (Exception e) {
            System.out.println(2);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void updateProductStock(Product product, int amount) {
        try {
            productMapper.updateProductStock(product.getId(), product.getStock() - amount);
        } catch (Exception e) {
            System.out.println(3);
            throw new RuntimeException(e);
        }
    }
}
