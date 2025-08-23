import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    // main method (10 marks)
    public static void main(String[] args) throws InterruptedException {
        int customersNum = 1000;
            
        BarberShop barberShop = new BarberShop(5);
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < customersNum; i++) {
            // create customer threads (5 marks)
            Customer customer = new Customer(barberShop);
            customers.add(customer);            
        }

        for (Customer c : customers)
            c.start();

        for (Customer c : customers)
            c.join();
        
        barberShop.shutdown();
         
        System.out.println("Done.");   
    }
}

class Customer extends Thread {
    private BarberShop barberShop;

    public Customer(BarberShop barberShop) {
        this.barberShop = barberShop;
    }

    @Override
    public void run() {
        Random rd = new Random();
        try {
            Thread.sleep(rd.nextInt(50));
        } catch (InterruptedException e) {
        }
        this.barberShop.customer_enter();         
    }
}