
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
public class Main_Better {
    public static void main(String[] args) throws InterruptedException{
        Random rd = new Random();
        int size  = 4;
        Boat boat = new Boat(size);
        ArrayList<Woman> womanThread = new ArrayList<>();
        ArrayList<Man> manThread = new ArrayList<>();

        for (int i  = 0; i<50; i++){
            int choose = rd.nextInt(2);
            
            if (choose == 1){
                Man m = new Man(i, i%2, boat);
                manThread.add(m);
                Thread.sleep(rd.nextInt(100));
                if (i==0) boat.currentDirection = i%2;
                m.start();
            }
            else{
                Woman wm = new Woman(i, i%2, boat);
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
    private Boat boat;
    public int direction;
    public int name;
    public Woman(int name, int direction, Boat boat){
        this.name = name;
        this.direction = direction;
        this.boat = boat;
    }

    @Override
    public void run(){
        try {
            this.boat.womanArrives(this);
        } catch (InterruptedException e) {
        }
    }
}

class Man extends  Thread{
    private Boat boat;
    public int direction;
    public int name;
    public Man(int name, int direction, Boat boat){
        this.name = name;
        this.direction = direction;
        this.boat = boat;
    }

    @Override
    public void run(){
        try {
            this.boat.manArrives(this);
        } catch (InterruptedException e) {
        }
    }
}

class Boat extends  Thread{
    Random rd = new Random();
    public ReentrantLock lock = new ReentrantLock();

    // chờ hướng từ 1 qua 0
    private Condition manWait0 = lock.newCondition();
    private Condition womanWait0 = lock.newCondition();

    // chờ hướng từ 0 qua 1
    private Condition manWait1 = lock.newCondition();
    private Condition womanWait1 = lock.newCondition();

    // hướng hiện tại của thuyền
    public int currentDirection;

    // số người đang chờ đi từ 1 qua 0
    private int countMan0 = 0;
    private int countWoman0 = 0;

    // số người đang chờ đi từ 0 qua 1
    private int countMan1 = 0;
    private int countWoman1 = 0;

    private int sizeBoat  = 4;

    public Boat(int size){
        this.sizeBoat = 4;
    }

    public void manArrives(Man man) throws InterruptedException{
        this.lock.lockInterruptibly();
        try {
            if (man.direction == 0){
                while ( (countMan0 == 2 && countWoman0 == 1) // Nếu đã có 2 man va 1 woman thì k thể có thêm 1 man
                || (countWoman0 == 3 && countMan0 == 0)){ // Nếu đã có 3 woman thì k thể có thêm 1 man
                    System.out.println("Man " + man.name + " dang await");
                    manWait0.await();
                }
                countMan0 ++;
                System.out.println("Man " + man.name + " arrives:    countMan0 : " + countMan0 + " --- countWoman0: " + countWoman0  + " -- HUONG " + man.direction + " --  CURRENT DIRECTION  " + currentDirection) ;
                exit();
            }
            else {
                while ( (countMan1 == 2 && countWoman1 == 1) // Nếu đã có 2 man va 1 woman thì k thể có thêm 1 man
                || (countWoman1 == 3 && countMan1 == 0)){ // Nếu đã có 3 woman thì k thể có thêm 1 man
                    System.out.println("Man " + man.name + " dang await");
                    manWait1.await();
                }
                countMan1 ++;
                System.out.println("Man " + man.name + " arrives:    countMan1 : " + countMan1 + " --- countWoman1: " + countWoman1  + " -- HUONG " + man.direction + " --  CURRENT DIRECTION  " + currentDirection) ;
                exit();
            }
        } finally {
            this.lock.unlock();
        }
    }
    public void womanArrives(Woman woman) throws InterruptedException{
        this.lock.lockInterruptibly();
        try {
            if (woman.direction == 0){
                while ( (countWoman0 == 2 && countMan0 == 1) ||  // Nếu đã có 2 woman và 1 man thì k thể có thêm 1 woman
                        (countWoman0 == 0 && countMan0 == 3)){ // Nếu đã có 3 man thì k thể có thêm 1 woman
                    System.out.println("Woman " + woman.name + "dang await");
                    womanWait0.await();
                }
                countWoman0 ++;
                System.out.println("Woman " + woman.name + " arrives:     countMan0: " + countMan0 + " --- countWoman0: " + countWoman0 + " -- HUONG " + woman.direction + " -- CURRENT DIRECTION " + currentDirection);
                exit();
            }else{
                while ( (countWoman1 == 2 && countMan1 == 1) || // Nếu đã có 2 woman và 1 man thì k thể có thêm 1 woman
                        (countWoman1 == 0 && countMan1 == 3)){ // Nếu đã có 3 man thì k thể có thêm 1 woman
                    System.out.println("Woman " + woman.name + "dang await");
                    womanWait1.await();
                }
                countWoman1 ++;
                System.out.println("Woman " + woman.name + " arrives:     countMan1: " + countMan1 + " --- countWoman1: " + countWoman1 + " -- HUONG " + woman.direction + " -- CURRENT DIRECTION " + currentDirection);
                exit();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void exit() throws InterruptedException {
        if (currentDirection == 0){
            // Nếu số nam nữ đều đủ
            if ((countMan0 + countWoman0) >= sizeBoat){
                System.out.println("DANG CHO KHACH QUA SONG HUONG TU 1 QUA 0");
                Thread.sleep(rd.nextInt(100));
                countMan0 = 0;
                countWoman0 = 0;
                womanWait1.signalAll(); // đánh thức hướng còn lại
                manWait1.signalAll(); 
                currentDirection = 1;
            }
        }
        else{
            if ((countMan1 + countWoman1) >= sizeBoat){
                System.out.println("DANG CHO KHACH QUA SONG HUONG TU 0 QUA 1");
                Thread.sleep(rd.nextInt(100));
                countMan1 = 0;
                countWoman1 = 0;
                womanWait0.signalAll(); // đánh thức hướng còn lại
                manWait0.signalAll();
                currentDirection = 0;
            }
        }
    }
}
