package order.mapper;

import order.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User getUserByName(String username);
    int updateBalance(String username, int newBalance);
}
