package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Sample1 {

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(100);

        Sum sum = new Sum();

        List<Callable<String>> tasks = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            tasks.add(()-> {
                for (int j = 0; j< 10000; j++) {
                    sum.addOne();
                }
                return "finish";
            });
        }
        try {
            service.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("The count value : " + sum.getCount());
        service.shutdown();

    }

    private static class Sum {
        /*
        This means that asking compiler to put this value in memory rather than in CPU cache which makes
        it can be access by other thread in memory.
         */

        private volatile int count = 0;

        /**
         * The sychronized means that 'this' instance will be luck during this method process.
         * if you remove sychronized the result will be wrong.
         */

        public synchronized void addOne() {
            this.count++;
        }

        /**
         * This method doesn't need to sychronized since the primitive type int is read and write atomic.
         * @return count THe value of sum.
         */
        public int getCount() {
            return count;
        }

    }

}
