package vip.openpark.reactor.operate;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anthony
 * @since 2024/2/17 21:37
 */
@Slf4j
public class OperateApplication {
	public static void main(String[] args) throws InterruptedException {
		// filter();
		// map();
		// flatMap();
		// concat();
		// concatWith();
		// concatMap();
		// switchMap();
		// transform();
		// transformDeferred();
		// defaultIfEmpty();
		// switchIfEmpty();
		// merge();
		// mergeSequential();
		// zip();
		// zipWith();
		
		Thread.sleep(10000);
	}
	
	public static void filter() {
		Flux.range(1, 10)
			// 过滤偶数
			.filter(i -> i % 2 == 0)
			.log()
			.subscribe();
	}
	
	public static void map() {
		Flux.range(1, 10)
			// 转换为字符串
			.map(i -> "MAP-" + i)
			.log()
			.subscribe();
	}
	
	public static void flatMap() {
		Flux.just("Li Bai", "Du Fu")
			// flatMap 将 Flux<集合<元素>> 转换为 Flux<元素>，流的扁平化
			.flatMap(val -> {
				String[] split = val.split(" ");
				return Flux.fromArray(split);
			})
			.log()
			.subscribe();
	}
	
	public static void concat() {
		Flux.concat(Flux.just("Li Bai", "Du Fu"), Flux.just("Gao Shi", "Xin Qi Ji"))
			.log()
			.subscribe();
	}
	
	public static void concatWith() {
		Flux.just("Li Bai", "Du Fu")
			.concatWith(Flux.just("Gao Shi", "Xin Qi Ji"))
			.log()
			.subscribe();
	}
	
	public static void concatMap() {
		Flux.range(1, 2)
			// concatMap 连接流
			.concatMap(i -> Flux.just("concatMap-" + i, "newEle"))
			.log()
			.subscribe();
	}
	
	public static void switchMap() {
		Flux.range(1, 2)
			// switchMap 会根据流中的元素，切换不同的流
			.switchMap(i -> {
				if (i % 2 == 0) {
					return Flux.just("switchMap-1", "switchMap-3");
				} else {
					return Flux.just("switchMap-2", "switchMap-4");
				}
			})
			.log()
			.subscribe();
	}
	
	/**
	 * transform 无 deferred，不会共享外部变量，无状态转换
	 * transformDeferred 有 deferred，会共享外部变量，有状态转换
	 */
	public static void transform() {
		AtomicInteger atomic = new AtomicInteger(0);
		// transform 下，订阅者不会共享外部变量
		Flux<String> flux = Flux.just("a", "b", "c")
			.transform(fluxVal -> {
				if (atomic.getAndIncrement() == 0) {
					// 说明是第一次调用，将元素转为大写
					return fluxVal.map(String::toUpperCase);
				} else {
					// 说明不是第一次调用，不处理
					return fluxVal;
				}
			});
		
		flux.subscribe(ele -> log.info("第一次调用：{}", ele));
		flux.subscribe(ele -> log.info("第二次调用：{}", ele));
	}
	
	public static void transformDeferred() {
		AtomicInteger atomic = new AtomicInteger(0);
		// transformDeferred 下，订阅者会共享外部变量
		Flux<String> flux = Flux.just("a", "b", "c")
			.transformDeferred(fluxVal -> {
				if (atomic.getAndIncrement() == 0) {
					// 说明是第一次调用，将元素转为大写
					return fluxVal.map(String::toUpperCase);
				} else {
					// 说明不是第一次调用，不处理
					return fluxVal;
				}
			});
		
		flux.subscribe(ele -> log.info("第一次调用：{}", ele));
		flux.subscribe(ele -> log.info("第二次调用：{}", ele));
	}
	
	public static void defaultIfEmpty() {
		Flux.just()
			// 【静态兜底数据】如果发布者元素为null，则使用默认值
			.defaultIfEmpty("default")
			.log()
			.subscribe();
	}
	
	public static void switchIfEmpty() {
		Flux.empty()
			// 空转换，调用动态的兜底方法
			.switchIfEmpty(Flux.just("switchIfEmpty"))
			.log()
			.subscribe();
	}
	
	public static void merge() {
		// merge 将多个流合并为一个流，按照发布者的发布顺序，依次输出元素
		Flux.merge(
				Flux.just("a1", "a2", "a3").delayElements(Duration.ofSeconds(1)),
				Flux.just("b1", "b2", "b3").delayElements(Duration.ofMillis(1200)),
				Flux.just("c1", "c2", "c3").delayElements(Duration.ofMillis(1300))
			)
			.log()
			.subscribe();
	}
	
	public static void mergeSequential() {
		// merge 将多个流合并为一个流，按照第一个元素发布顺序，依次输出该流中所有元素，然后依次输出第二个流中所有元素，依次类推
		Flux.mergeSequential(
				Flux.just("a1", "a2", "a3").delayElements(Duration.ofSeconds(1)),
				Flux.just("b1", "b2", "b3").delayElements(Duration.ofMillis(1200)),
				Flux.just("c1", "c2", "c3").delayElements(Duration.ofMillis(1300))
			)
			.log()
			.subscribe();
	}
	
	public static void zip() {
		Flux.zip(
				Flux.just("a1", "a2"),
				Flux.just("b1", "b2", "b3"),
				Flux.just("c1", "c2", "c3", "c4"),
				Flux.just("d1", "d2", "d3", "d4", "d5")
			)
			.doOnDiscard(String.class, s -> log.info("Discard: {}", s))
			.log()
			.subscribe();
	}
	
	public static void zipWith() {
		// 最多支持8个流压缩
		Flux.just(1, 2, 3)
			// zipWith 将此Flux压缩到另一个Publisher源中，也就是说等待两者都发出一个元素并将这些元素组合成Tuple2。
			// 无法组合的元素将直接被丢弃
			.zipWith(Flux.just("a", "b", "c", "d"))
			.doOnDiscard(String.class, s -> log.info("Discard: {}", s))
			.log()
			.subscribe();
	}
}