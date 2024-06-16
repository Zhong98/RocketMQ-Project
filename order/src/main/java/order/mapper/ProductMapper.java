package order.mapper;

import order.entity.Product;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> selectAllProduct();
    int updateProductStock(int productId, int newStock);
}
