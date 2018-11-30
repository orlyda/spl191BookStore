
package java;

import bgu.spl.mics.Future;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


public class FutureTest {
    private Future<Integer> f,g;

    @Before
    public void setUp()  {
        f = new Future<>();
        g = new Future<>();
    }

    @Test
    public void get() {
        f.resolve(5);
        assertTrue(f.get() == 5);
        Integer a =0;
        Thread test1 = new Thread(new ThreadForTestGet(a,g));
        Thread test2 = new Thread(new ThreadForTestResolve(g));
        test1.start();
        test2.start();
        try {
            test1.join();
        } catch (InterruptedException ignored) {}
    }
    private void endAssert(Integer a){
        assertTrue(a==5);
    }
    @Test
    public void isDone() {
        assertFalse(f.isDone());
        f.resolve(5);
        assertTrue(f.isDone());
    }

    @Test
    public void get1() {
        Integer a = f.get(1, TimeUnit.SECONDS);
        try {
            Thread.currentThread().sleep(1005);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNull(a);
    }
    @Test
    public void get2(){
        Integer a = new Integer(0);
        Thread test1 = new Thread(new ThreadForTestGet1(a,f));
        Thread test2 = new Thread(new ThreadForTestResolve(f));
        try {
            test1.join();
        } catch (InterruptedException ignored) {}
    }
    protected class ThreadForTestGet implements Runnable{
        Integer a = null;
        Future<Integer> f;
        ThreadForTestGet(Integer a, Future f){
            this.a = a;
            this.f = f;
        }
        @Override
        public void run() {
            this.a=f.get();
            endAssert(a);
        }}
    protected class ThreadForTestGet1 implements Runnable{
        private Integer a = null;
        private Future<Integer> f;
        ThreadForTestGet1(Integer a, Future f){
            this.a = a;
            this.f = f;
        }
        @Override
        public void run() {
            this.a=f.get(2,TimeUnit.SECONDS);
            endAssert(a);
        }}
    protected class ThreadForTestResolve implements Runnable{
        private Future<Integer> f;
        ThreadForTestResolve(Future<Integer> f){
            this.f=f;
        }
        @Override
        public void run() {
            f.resolve(5);
        }}

}
