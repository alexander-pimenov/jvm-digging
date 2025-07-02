import shop.*;

public class LineTest {
    public static void main(String[] args) {
        Repo repo = new Repo();
        CartService cartService = new CartService();
        UserService userService = new UserService();
        EmailService emailService = new EmailService();
        PaymentService paymentService = new PaymentService();

        User user = userService.findUserByName("Fuller Gonzalez");

        if(!repo.contains(user)){
            repo.save(user);
        }
        Cart cart = cartService.loadCartFor(user);

        Integer total = cart.items().stream()
                .mapToInt(item -> item.getCost() )
                .sum();
        Integer transactionId = paymentService.pay(user,total);
        emailService.send(user,cart,transactionId);



        System.out.println(total.toString() +" transactionId:"+ transactionId);
    }
}
