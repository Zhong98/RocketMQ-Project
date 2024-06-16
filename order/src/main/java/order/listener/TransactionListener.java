package order.listener;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import order.entity.Order;
import order.mapper.OrderMapper;
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

    Gson gson = new Gson();

    @Override
    @Transactional
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        String str = new String((byte[]) message.getPayload());
        Order order = gson.fromJson(str ,Order.class);
        try {
            orderMapper.createOrder(order);
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
}
