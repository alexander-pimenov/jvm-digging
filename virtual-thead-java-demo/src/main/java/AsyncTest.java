import shop.*;

import java.util.concurrent.CompletableFuture;

public class AsyncTest {
    public static void main(String[] args) {
        Repo repo = new Repo();

        CartService cartService = new CartService();
        UserService userService = new UserService();
        EmailService emailService = new EmailService();
        PaymentService paymentService = new PaymentService();

        for (int i = 0; i < 1000; i++) {
            CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> {
                User user = userService.findUserByName("Fuller Gonzalez");
                return user;
            }).thenApply(user -> {
                if (!repo.contains(user)) {
                    repo.save(user);
                }
                return user;
            }).thenApply(user -> {
                CompletableFuture<Cart> CartFuture = CompletableFuture.supplyAsync(() -> {
                    Cart cart = cartService.loadCartFor(user);
                    return cart;
                }).thenApply(cart -> {
                    Integer total = cart.items().stream()
                            .mapToInt(item -> item.getCost())
                            .sum();
                    CompletableFuture<Integer> transactionFuture = CompletableFuture.supplyAsync(() -> {
                        Integer transactionId = paymentService.pay(user, total);
                        return transactionId;
                    }).thenApply(transactionId -> {
                                emailService.send(user, cart, transactionId);
                                return transactionId;
                            }
                    );
                    return cart;
                });
                return user;
            });
        }

    }
}
