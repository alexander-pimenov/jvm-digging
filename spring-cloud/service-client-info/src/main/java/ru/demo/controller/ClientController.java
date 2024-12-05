package ru.demo.controller;

import ru.demo.model.ClientData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class ClientController {
    private static final Logger logger = LoggerFactory.getLogger(ClientController.class);

    //curl -v http://localhost:8083/additional-info?name="testClient"

    //docker run -d --rm -p 9411:9411 openzipkin/zipkin
    //http://localhost:9411

    @GetMapping(value = "/additional-info")
    public ClientData info(@RequestParam(name = "name") String name) throws InterruptedException {
        logger.info("request. name:{}", name);
        doJob();
        return new ClientData(String.format("additional ClientInfo name:%s", name));
    }

    private void doJob() throws InterruptedException {
        Thread.sleep(Duration.ofSeconds(1));
    }
}