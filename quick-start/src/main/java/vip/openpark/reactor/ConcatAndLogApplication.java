package vip.openpark.reactor;

import reactor.core.publisher.Flux;

/**
 * 流的连接和日志记录
 *
 * @author anthony
 * @version 2024/2/17 14:45
 */
public class ConcatAndLogApplication {
	public static void main(String[] args) {
		Flux.concat(Flux.just(1, 2, 3), Flux.just(4, 5, 6))
			.log() // 响应式打印日志，只是对上游的操作进行打印
			.filter(i -> i % 2 == 0)
			.log()
			.map(i -> i * 2)
			.log()
			.subscribe(System.out::println);
	}
}