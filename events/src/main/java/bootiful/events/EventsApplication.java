package bootiful.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@EnableAsync
@SpringBootApplication
public class EventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }

    @EventListener
    void webServerInitialized(WebServerInitializedEvent wse) {
        System.out.println("the web server started on " + wse.getWebServer().getPort());
    }

    @EventListener
    void ready(ApplicationReadyEvent are) {
        System.out.println("application ready event (take 3): " + are.toString());
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> applicationReadyEventApplicationListener() {
        return event -> System.out.println("application ready event (take 2): " + event.getTimeTaken().toString());
    }

    @Component
    static class MyListener implements ApplicationListener<ApplicationReadyEvent> {

        @Override
        public void onApplicationEvent(ApplicationReadyEvent event) {
            System.out.println("application ready event: " + event.getTimeTaken().toString());
        }
    }

    @org.springframework.modulith.events.ApplicationModuleListener
    void onMyCustomEvent(MyCustomEvent myCustomEvent) throws Exception {
        System.out.println(Thread.currentThread().getName() + " got a custom event! " + myCustomEvent.message());
        Thread.sleep(20_000);
        System.out.println("finished!");
    }

    @Bean
    ApplicationListener<ApplicationReadyEvent> publisher(ApplicationEventPublisher publisher) {
        return args -> {
            publisher.publishEvent(new MyCustomEvent("Happy New Year!"));
            System.out.println("hello, world!");
        };
    }
}

record MyCustomEvent(String message) {
}