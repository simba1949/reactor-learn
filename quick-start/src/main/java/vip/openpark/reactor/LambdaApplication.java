package vip.openpark.reactor;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * <a href="https://projectreactor.io/docs/core/release/reference/#_simple_ways_to_create_a_flux_or_mono_and_subscribe_to_it">Lambda Support</a>
 *
 * @author anthony
 * @version 2024/2/17 15:28
 */
@Slf4j
public class LambdaApplication {
	public static void main(String[] args) {
		Flux<String> flux =
			Flux
				.range(1, 10)
				.map(ele -> {
					if (9 == ele) {
						throw new RuntimeException("error");
					}
					return "MAP" + ele;
				});
		
		flux.subscribe(System.out::println, error -> log.error("error", error));
		flux.subscribe();
	}
}