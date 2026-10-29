package app.mywargame;

import org.springframework.boot.SpringApplication;

public class TestMyWargameAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(MyWargameAppApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
