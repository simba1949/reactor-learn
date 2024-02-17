package vip.openpark.reactor.flux;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

/**
 * 编程方式创建序列
 * 同步环境使用：generate
 * 多线程环境使用：create
 *
 * @author anthony
 * @version 2024/2/17 17:02
 */
@Slf4j
public class CreateSequenceApplication {
	public static void main(String[] args) {
		// generate();
		create();
	}
	
	/**
	 * Sink 接收端
	 * Source 数据源
	 */
	public static void generate() {
		Flux.generate(
				// 初始状态
				() -> 1,
				(state, sink) -> {
					if (state <= 10) {
						// 数据生成
						sink.next(state);
					} else {
						// 数据生成完成
						sink.complete();
					}
					
					if (state == 5) {
						// 发生错误
						sink.error(new RuntimeException("发生错误"));
					}
					
					// 返回下一个状态
					return ++state;
				})
			.log()
			.doOnError(error -> log.error("Flux发生了异常", error))
			.subscribe(System.out::println);
	}
	
	/**
	 * create
	 */
	public static void create() {
		// 创建一个无限的序列
		Flux.create(emitter -> {
			for (int i = 0; i < 10; i++) {
				emitter.next("emitter" + i);
			}
		}).subscribe(System.out::println);
	}
}