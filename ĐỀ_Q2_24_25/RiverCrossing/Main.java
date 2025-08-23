import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	public static void main(String[] args) throws InterruptedException { // 4 marks
		int N = 1000;	// 1000 women, 1000 men
		RiverCrossing rc = new RiverCrossing();

		List<Thread> threads = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			Thread t = new WomanThread(rc);
			threads.add(t);
			t = new ManThread(rc);
			threads.add(t);
		}		
		
		for (Thread t :  threads)
			t.start();
		
		for (Thread t :  threads)
			t.join();

		System.out.println("Done");
	}
}

class WomanThread extends Thread {   // 4 marks
	private RiverCrossing rc;
	
	public WomanThread(RiverCrossing rc) {
		this.rc = rc;
	}

	@Override
	public void run() {
		Random rd = new Random();
		try {
			Thread.sleep(rd.nextInt(10));
		} catch (InterruptedException e) {
		}
		this.rc.womanArrives();			
	}
}

class ManThread extends Thread {   // 4 marks
	private RiverCrossing rc;
	
	public ManThread(RiverCrossing rc) {
		this.rc = rc;
	}

	@Override
	public void run() {
		Random rd = new Random();
		try {
			Thread.sleep(rd.nextInt(10));
		} catch (InterruptedException e) {
		}
		this.rc.manArrives();
	}
}
