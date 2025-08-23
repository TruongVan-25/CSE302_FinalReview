
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/*
Question 3 (30 points)
A particular river crossing is shared by both women and men. A boat is used to cross the river, but it only seats four people, 
and must always carry a full load. In order to guarantee the safety, you cannot put three women and one man in the same boat; 
similarly, you cannot put three men in the same boat as a woman. All other combinations are safe. 
Two procedures are needed, womanArrives and manArrives, called by a woman or a man when he/she arrives at the river bank. 
The procedures arrange the arriving women and men into safe boatloads. Women and men arrive at the river bank, they must wait until a boat is full.

1. Implement two methods womanArrives and manArrives that arrange the arriving women and men into safe boatloads. (18 marks)
2. Write a main method to test these methods. (12 marks)
 */
public class Main {
    public static void main(String[] args) throws InterruptedException{
        Random rd = new Random();
        int size  = 4;
        Boat river = new Boat(size);
        ArrayList<Woman> womanThread = new ArrayList<>();
        ArrayList<Man> manThread = new ArrayList<>();

        for (int i  = 0; i<50; i++){
            int choose = rd.nextInt(2);
            if (choose == 1){
                Man m = new Man(i, river);
                manThread.add(m);
                Thread.sleep(rd.nextInt(100));
                m.start();
            }
            else{
                Woman wm = new Woman(i, river);
                womanThread.add(wm);
                Thread.sleep(rd.nextInt(100));
                wm.start();
            }
        }


        for (Woman wm: womanThread) wm.interrupt();
        for (Man m: manThread) m.interrupt();

        for (Woman wm: womanThread) wm.join();
        for (Man m: manThread) m.join();

        System.out.println("Done");
    }
}

class Woman extends Thread {
    private Boat river;
    public int name;
    public Woman(int name, Boat river){
        this.name = name;
        this.river = river;
    }

    @Override
    public void run(){
        try {
            this.river.womanArrives(name);
        } catch (InterruptedException e) {
        }
    }
}

class Man extends  Thread{
    private Boat river;
    public int name;
    public Man(int name, Boat river){
        this.name = name;
        this.river = river;
    }

    @Override
    public void run(){
        try {
            this.river.manArrives(name);
        } catch (InterruptedException e) {
        }
    }
}

class Boat extends  Thread{
    Random rd = new Random();
    public ReentrantLock lock = new ReentrantLock();
    private Condition manWait = lock.newCondition();
    private Condition womanWait = lock.newCondition();
    private int countMan = 0;
    private int countWaitingMan = 0;
    private int countWoman = 0;
    private int countWaitingWoman = 0;
    private int sizeBoat  = 4;

    public Boat(int size){
        this.sizeBoat = 4;
    }

    public void manArrives(int name) throws InterruptedException{
        this.lock.lockInterruptibly();
        try {

            while ( (countMan == 2 && countWoman == 1) || (countWoman == 3 && countMan == 0)){
                System.out.println("Man " + name + " dang await");
                countWaitingMan++;
                exit();
                manWait.await();
            }
            countMan ++;
            System.out.println("Man " + name + " arrives:    countMan : " + countMan + " --- countWoman: " + countWoman + " -- freeMan: " + countWaitingMan + " --  freeWoman: " + countWaitingWoman) ;

            exit();
        } finally {
            this.lock.unlock();
        }
    }
    public void womanArrives(int name) throws InterruptedException{
        this.lock.lockInterruptibly();
        try {

            while ( (countWoman == 2 && countMan == 1) || (countWoman == 0 && countMan == 3)){
                System.out.println("Woman " + name + "dang await");
                countWaitingWoman ++;
                exit();
                womanWait.await();
            }
            countWoman ++;
            System.out.println("Woman " + name + " arrives:     countMan: " + countMan + " --- countWoman: " + countWoman + " -- freeMan: " + countWaitingMan + " --  freeWoman: " + countWaitingWoman);
            exit();
        } finally {
            this.lock.unlock();
        }
    }

    public void exit() throws InterruptedException {
        this.lock.lockInterruptibly();
        try{
            // Nếu số nam nữ đều đủ
            if ((countMan + countWoman) == sizeBoat){
                System.out.println("DANG CHO KHACH QUA SONG");
                Thread.sleep(rd.nextInt(100));
                countMan = 0;
                countWoman = 0;
                womanWait.signalAll();
                manWait.signalAll();
            }
            // Nếu số nam và số nam đang chờ đã đủ 1 thuyền thì cho đi trước
            else if (countMan + countWaitingMan == sizeBoat){
                System.out.println("DANG CHO KHACH QUA SONG");
                Thread.sleep(rd.nextInt(100));
                countMan = 0;
                countWaitingMan = 0;
                womanWait.signalAll();
                manWait.signalAll();
            }
            // Nếu số nữ và số nữ đang chờ đã đủ 1 thuyền thì cho đi trước
            else if (countWoman + countWaitingWoman == sizeBoat){
                System.out.println("DANG CHO KHACH QUA SONG");
                Thread.sleep(rd.nextInt(100));
                countWoman = 0;
                countWaitingWoman = 0;
                womanWait.signalAll();
                manWait.signalAll();
            }
            
            
        }finally{
            this.lock.unlock();
        }
    }
}
