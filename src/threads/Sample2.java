package threads;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Sample2 {

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(100);

        List<Callable<String>> tasks = new ArrayList<>();

        Sum sum = new Sum();

        for(int i = 0; i<100; i++) {
            tasks.add(()-> {
                for (int j = 0;j< 10000;j++)
                sum.addUser();
                return "success";
            });
        }

        try {
            service.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("finish");
        System.out.println(sum.getUsers());
        service.shutdown();

    }


    private static class Sum {
        private volatile Users users = new Users();
        /**
         * ReetrantLock can make the reader processing isolate each other, and the
         * processing locked only when reader lock called. So the efficiency will be enhance.
         */
        private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        private ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();
        private ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();

        public void addUser() {
            try {
                writeLock.lock();
                users.addUser();
                users.addCurrency();
            }finally {
                /**
                 * must unlock in finally to prevent dead lock
                 */
                writeLock.unlock();
            }
        }

        public Users getUsers() {
            try {
                readLock.lock();
                return  this.users.clone();
            }finally {
                /**
                 * must unlock in finally to prevent dead lock
                 */
                readLock.unlock();
            }
        }
    }

    public static class Users implements Cloneable{
        private int userNumber = 0;
        private long currency = 0;

        private  Users() {}

        public int getUserNumber() {
            return userNumber;
        }

        /**
         * Let this class can only be modify in its outer class.
         */
        private void addUser() {
            this.userNumber++;
        }

        public long getCurrency() {
            return currency;
        }

        /**
         * Let this class can only be modify in its outer class.
         */
        private void addCurrency() {
            this.currency = this.currency + 30;
        }

        /**
         * You need to create a clone object to prevent others to modify the real value.
         * @return
         */

        @Override
        public Users clone() {
            Users users = null;
            try {
                users = (Users)super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            return users;
        }

        @Override
        public String toString() {
            return "userNumber : " + userNumber + " currency : " + currency;
        }
    }
}
