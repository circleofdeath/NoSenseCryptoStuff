package deltaalpha.crypto;

import deltaalpha.crypto.jwglgl.App;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SuppressWarnings("All")
@SpringBootApplication
public class CryptoApplication {
	public static final String SECURITY_KEY = "1"; // change it if you need

	public static void main(String[] args) {
		Mono<Void> springApp = Mono.fromRunnable(() -> SpringApplication.run(CryptoApplication.class, args))
				.subscribeOn(Schedulers.boundedElastic()).then();

		Mono<Void> otherApp = Mono.fromRunnable(() -> App.main(args))
				.subscribeOn(Schedulers.boundedElastic()).then();

		Flux.merge(springApp, otherApp)
				.doOnTerminate(() -> System.out.println("Both applications have started"))
				.subscribe();

		while(true) {}
	}
}