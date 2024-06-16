package order.controller;

import order.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayController {

    @Autowired
    PayService payService;

    @GetMapping("/pay")
    @ResponseBody
    public String pay(String userName, String goodId, String count) {
        String msg;
        if (userName == null || goodId == null || count == null) {
            return "用户名，商品ID和购买数量都不能为空";
        }
        try {
            int productId = Integer.parseInt(goodId);
            int amount = Integer.parseInt(count);
            msg = payService.pay(userName, productId, amount);
        }catch (NumberFormatException e){
            return "请输入正确的数字";
        }
        return msg;
    }
}
