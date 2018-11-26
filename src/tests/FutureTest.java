package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<Integer> f;

    @Before
    public void setUp() throws Exception {
        f = new Future<>();
    }

    @Test
    public void get() {
        f.resolve(5);
        assertTrue(f.get() == 5);
    }

    @Test
    public void isDone() {
        assertFalse(f.isDone());
        f.resolve(5);
        assertTrue(f.isDone());
    }

    @Test
    public void get1() {
        Integer a = f.get(5, TimeUnit.SECONDS);
        try {
            Thread.currentThread().wait(5005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertTrue(a == null);
        Future<Integer> g = new Future<>();
        Integer b = g.get(5, TimeUnit.SECONDS);
        g.resolve(5);
        assertTrue(b == 5);
    }

}
