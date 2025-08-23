

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
 * 
Question 3 (40 marks)
In a barbershop there are barbers and barber chairs. There are no waiting chairs in the barbershop. 
If there are no customers to be served, the barbers go to sleep. 
If a customer enters the barbershop and all barber chairs are occupied, then the customer leaves the shop. 
If a customer enters the barbershop and there is a barber who is asleep, the customer wakes up the barber and get the haircut. 
When the barber finishes cutting a customer's hair, he goes to sleep.

Write a Java program to coordinate the barbers and the customers (using ReentrantLock and Condition classes/interfaces).
a. Write Java programs for the barbers' function (15 marks)
- Barber job implementation (10 marks)
-Threads for n barbers (5 marks)
b. Implement the customers' function (15 marks)
- Customer job implementation (10 marks)
- Threads for customers (5 marks)
c. Write a main method to test your programs (10 marks)
 */

public class Main {
    static Random rd = new Random();
    public static void main(String[] args) throws InterruptedException {
        int size = 5;
        Shop shop = new Shop();
        ArrayList<Customer> customerThread = new ArrayList<>();
        ArrayList<Barber> barberThread = new ArrayList<>();
        for (int i = 1; i<=size; i++){
            barberThread.add(new Barber(i, shop));
            barberThread.get(i-1).start();
        }
        int ith = 1;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime <= 3000){
            customerThread.add(new Customer(ith, shop));
            Thread.sleep(rd.nextInt(20));
            customerThread.get(ith-1).start();
            ith++;
        }

        for (Customer c: customerThread){
            c.interrupt();
        }

        for (Barber b: barberThread){
            b.interrupt();
        }
        

        for (Barber b: barberThread){
            b.join();
        }
        for (Customer c: customerThread){
            c.join();
        }
        System.out.println("Done");
    }

}
class Shop {
    public ReentrantLock mainLock = new ReentrantLock();
    public Condition available = mainLock.newCondition();
    public LinkedList<Barber> freeBarber = new LinkedList<>();

    public Shop(){
    }
}

class Customer extends Thread {
    private int id;
    private Shop shop;
    private ReentrantLock shopLock;

    public Customer(int id, Shop shop){
        this.id = id;
        this.shop = shop;
        this.shopLock = this.shop.mainLock;
    }

    public int getNameID(){
        return id;
    }
       
    @Override
    public void run(){
        
        try{
            this.shopLock.lockInterruptibly();
            try {
                if (!this.shop.freeBarber.isEmpty()){
                    Barber freeBar = this.shop.freeBarber.removeFirst();
                    freeBar.getCustomer(this);
                    freeBar.available.signal();
                }
                else System.out.println("The customer " + id + " is leaving");
            }finally{
                this.shopLock.unlock();
            }
        } catch (Exception e){

        }
    }

}

class Barber extends Thread{
    Random rd = new Random();
    private int name;
    private ReentrantLock shopLock;
    public final Condition available ;
    private Customer customer = null;
    private Shop shop;
    
    public Barber(int name, Shop shop){
        this.shop = shop;
        this.name = name;
        this.shopLock = this.shop.mainLock;
        this.available = this.shopLock.newCondition();
    }

    public void getCustomer(Customer cust){
        this.customer = cust;
    }

    public int getname(){
        return name;
    }

    public void working() throws InterruptedException{
        
        
    }

    @Override
    public void run(){
        while (!this.isInterrupted()){
            try {
                shopLock.lockInterruptibly();
                try {
                    while (this.customer == null){
                        System.out.println("Barber " + name + " is sleeping...");
                        if (!this.shop.freeBarber.contains(this)) {
                            this.shop.freeBarber.add(this);
                        }
                        available.await();
                    }
                    // System.out.println("Barber " + name + " finishs cutting hair for " + this.customer.getNameID());
                    shopLock.unlock();
                    System.out.println("Customer " + this.customer.getNameID() + " is being served by barber " + this.name);
                    Thread.sleep(rd.nextInt(100));
                    this.customer = null;
                } finally {
                    if (shopLock.isHeldByCurrentThread())
                        shopLock.unlock();
                }
            } catch (InterruptedException ex) {
                break;
            }
        }
    }
}