import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
    private int n; // the number of barbers
    private List<Barber> barbers = new ArrayList<>();
    private boolean shutdown = false;
    private ReentrantLock lock = new ReentrantLock();
    private Condition barbersCond = this.lock.newCondition();
    private Condition customerCond = this.lock.newCondition();
    private int sleepBarberCount = 0;

    public BarberShop(int n) {
        this.n = n;

        // Create n barbers (5 marks)
        for (int i = 0; i < this.n; i++) {
            Barber barber = new Barber();
            this.barbers.add(barber);
            barber.start();
        }
    }

    // Customers' job (10 marks)
    // return true: get hair cut
    public boolean customer_enter() {
        if (this.shutdown == true)
            return false;
        try {
            this.lock.lockInterruptibly();
            try {
                if (this.sleepBarberCount == 0) // All barbers are busy, the customer leaves the shop.
                    return false;
                else {
                    this.sleepBarberCount--;
                    this.barbersCond.signal();  // Wake one sleeping barber up
                    this.customerCond.await();
                }
            } catch (InterruptedException e) {
            } finally {
                this.lock.unlock();
            }
            // get hair cut
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }

    public void shutdown() {
        if (this.shutdown == true)
            return;
        this.shutdown = true;
        for (Barber barber : this.barbers)
            barber.interrupt();
        for (Barber barber : this.barbers) {
            try {
                barber.join();
            } catch (InterruptedException e) {
            }        
        }
    }

    class Barber extends Thread {
        // Barbers' job (10 marks)
        @Override
        public void run() {
            while (BarberShop.this.shutdown == false) {
                try {
                    BarberShop.this.lock.lockInterruptibly();
                    try {
                        BarberShop.this.sleepBarberCount++;
                        BarberShop.this.barbersCond.await(); // sleeping
                        BarberShop.this.customerCond.signal();
                    } catch (InterruptedException e) {
                    } finally {
                        BarberShop.this.lock.unlock();
                    }
                    //
                    // cut customer's hair in 100 ms
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                } catch (InterruptedException e) {
                    return;
                }
            }

        }
    }
}
