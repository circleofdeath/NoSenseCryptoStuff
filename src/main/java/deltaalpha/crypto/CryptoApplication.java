package deltaalpha.crypto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CryptoApplication {
	public static final String SECURITY_KEY = "1234"; // change it if need

	public static void main(String[] args) {
		SpringApplication.run(CryptoApplication.class, args);
	}
}