package vip.openpark.reactor.operate;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Sinks;

import java.io.IOException;

/**
 * @author anthony
 * @version 2024/2/18 10:39
 */
@Slf4j
public class SinksApplication {
	public static void main(String[] args) throws IOException, InterruptedException {
		// unicast();
		// multicast();
		// replay();
		
		System.in.read();
	}
	
	public static void concept() {
		Sinks.one(); // 单值发送器
		Sinks.many(); // 多值发送器
		
		Sinks.many().replay(); // 重放
	}
	
	/**
	 * 1. 一个发送器只能有一个订阅者
	 */
	public static void unicast() throws InterruptedException {
		Sinks.Many<Object> many = Sinks.many().unicast().onBackpressureBuffer();
		
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				many.tryEmitNext(i);
			}
		}).start();
		
		// 第一个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v1 = {}", v))).start();
		// 防止先订阅
		Thread.sleep(1000);
		// 第二个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v2 = {}", v))).start();
	}
	
	/**
	 * 1. 多个订阅者
	 */
	public static void multicast() {
		Sinks.Many<Object> many = Sinks.many().multicast().onBackpressureBuffer();
		
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				many.tryEmitNext(i);
				try {
					// 这里防止先订阅者订阅号消费过快，导致后来的订阅者还没有来得及订阅就被丢弃
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		// 第一个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v1 = {}", v))).start();
		// 第二个订阅
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v2 = {}", v))).start();
	}
	
	/**
	 * 1. 重放
	 */
	public static void replay() {
		Sinks.Many<Object> many = Sinks.many().replay().limit(3);
		
		// 发布者每秒发布一个值
		new Thread(() -> {
			for (int i = 0; i < 100; i++) {
				many.tryEmitNext(i);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}).start();
		
		// 第一个订阅者
		new Thread(() -> many.asFlux().subscribe(v -> log.info("v1 = {}", v))).start();
		
		// 第二个订阅五秒后订阅
		new Thread(() -> {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			many.asFlux().subscribe(v -> log.info("v2 = {}", v));
		}).start();
	}
}