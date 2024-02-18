package vip.openpark.reactor;

import reactor.core.publisher.Mono;

/**
 * @author anthony
 * @version 2024/2/17 12:30
 */
public class MonoQuickStartApplication {
	public static void main(String[] args) {
		// 创建一个 Mono 对象
		Mono<String> mono = Mono.just("Hello World");
		mono.subscribe(System.out::println);
	}
}