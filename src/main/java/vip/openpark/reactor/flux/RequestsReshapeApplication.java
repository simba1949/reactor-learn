package vip.openpark.reactor.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * @author anthony
 * @version 2024/2/17 16:34
 */
@Slf4j
public class RequestsReshapeApplication {
	public static void main(String[] args) {
		limitRage();
	}
	
	/**
	 * limitRate
	 */
	public static void limitRage() {
		Flux.range(1, 1000)
			// 限流触发，看上游是怎么限流获取数据的
			.log()
			// 第一次抓取100个元素，如果 75% 元素已经被处理完成，则继续抓取新的 75% 元素
			.limitRate(100)
			.subscribe();
	}
	
	/**
	 * buffer
	 */
	public static void buffer() {
		Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
			// 每3个元素为一组，消费者每次最大消费3个元素，消费者收到的是一个数组
			.buffer(3)
			// 类型：class java.util.ArrayList
			.subscribe(ele -> log.info("类型：{}，值：{}", ele.getClass(), ele));
	}
}