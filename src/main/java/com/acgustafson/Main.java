package com.acgustafson;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Main {
    static String BROKER1_HOSTNAME = "localhost:61617";
    static String BROKER2_HOSTNAME = "localhost:61618";
    static int numOfProducers = Integer.parseInt(System.getProperty("producers", "3000"));
    static int numOfConsumers = Integer.parseInt(System.getProperty("consumers", "12"));
    public static void main(String[] args) throws InterruptedException {
        String todo = System.getProperty("run", "all");

        switch (todo) {
            case "all" -> {
                createEmbeddedBrokers();
                createProducers();
                createConsumers();
                createStatsConsumer();
            }
            case "broker" -> createEmbeddedBrokers();
            case "producer" -> createProducers();
            case "consumer" -> createConsumers();
            case "stats" -> createStatsConsumer();
            default -> {
                createEmbeddedBrokers();
                createProducers();
                createConsumers();
                createStatsConsumer();
            }
        }
    }

    private static void createEmbeddedBrokers() throws InterruptedException {
        System.out.println("creating 2 embedded brokers");
        new Server("broker1.xml").start();
        new Server("broker2.xml").start();
        Thread.sleep(5000); // let the brokers finish starting
    }

    private static void createStatsConsumer() {
        // Create a wildcard consumer to print some stats for us
        System.out.println("creating stats consumer");
        Client statsConsumer = new Client("StatsConsumer1", true, BROKER1_HOSTNAME, true);
        statsConsumer.start();
    }

    private static void createProducers() {
        // Create our producers
        System.out.println("creating producers");
        for (int i=0; i<numOfProducers; i++) {
            new Client(String.valueOf(i), false, getBrokerName(i), false).start();
            System.out.println("created producer " + i);
        }
    }

    private static void createConsumers() {
        // Create a few wild card consumers
        System.out.println("creating consumers");
        ThreadPoolExecutor consumerThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        for (int i=0; i<numOfConsumers; i++) {
            new Client(String.valueOf(i), true, getBrokerName(i) ,false).start();
        }
    }

    private static String getBrokerName(int i) {
        // Load balancing between the two brokers
        if(i % 2 == 0) {
            return BROKER1_HOSTNAME;
        }
        return BROKER2_HOSTNAME;
    }
}