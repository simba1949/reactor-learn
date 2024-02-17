package vip.openpark.reactor.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @author anthony
 * @since 2024/2/17 20:47
 */
@Slf4j
public class HandlerApplication {
	public static void main(String[] args) {
		Flux
			.range(1, 5)
			// handle 可以用来生成一个每个源元素的任意值，可能跳过一些元素
			.handle((val, sink) -> {
				log.info("获取的值为{}", val);
				if (val % 2 == 0) {
					sink.next("奇数-" + val);
					return;
				}
				sink.next("偶数-" + val);
			})
			.subscribe(val -> log.info("消费的值为{}", val));
	}
}