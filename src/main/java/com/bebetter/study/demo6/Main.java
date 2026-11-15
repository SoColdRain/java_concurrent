package com.bebetter.study.demo6;

import java.math.BigInteger;
import java.util.List;

public class Main {

    static void main() throws InterruptedException {
        System.out.println(aSecondOfPrimes());
    }

    private static List<BigInteger> aSecondOfPrimes() throws InterruptedException {
        PrimeGenerator primeGenerator = new PrimeGenerator();
        new Thread(primeGenerator).start();
        try {
            Thread.sleep(1000);
        } finally {
            primeGenerator.cancel();
        }
        return primeGenerator.get();
    }
}
