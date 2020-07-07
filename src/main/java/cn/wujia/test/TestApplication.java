package cn.wujia.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//@SpringBootApplication
public class TestApplication {
    static volatile int a = 0;

    public static void main(String[] args) throws IOException {

        //  SpringApplication.run(TestApplication.class, args);
        int corePoolSize = 2;//核心线程池大小
        int maximumPoolSize = 4;//最大线程池大小
        long keepAliveTime = 10;//线程最大空闲时间
        TimeUnit unit = TimeUnit.SECONDS;//时间单位
        //任务队列，被添加到线程池中，但尚未被执行的任务；它一般分为直接提交队列、有界任务队列、无界任务队列、优先任务队列几种
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10);
        //线程工厂，用于创建线程，一般用默认即可
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        //Executors.defaultThreadFactory();
        //拒绝策略；当任务太多来不及处理时，如何拒绝任务
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        //new MyIgnorePolicy();
        //new NameTreadFactory();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                workQueue, threadFactory, handler);
        executor.prestartAllCoreThreads(); // 预启动所有核心线程
        //executor.prestartAllCoreThreads()
        for (int i = 1; i <= 10; i++) {
            MyTask task = new MyTask(String.valueOf(i));
            executor.execute(task);
        }
        System.in.read();//阻塞主线程
    }

    static class MyTask implements Runnable {
        private String name;
        Lock lock = new ReentrantLock();

        public MyTask(String name) {
            this.name = name;
        }

        @Override
        public synchronized void run() {
            System.out.println(this.toString() + " is running!");
            try {
                Thread.sleep(1000); //让任务执行慢点
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a++;
            System.out.println(a + " is running!");
        }

        @Override
        public String toString() {
            return "MyTask [name=" + name + "]";
        }
    }
}