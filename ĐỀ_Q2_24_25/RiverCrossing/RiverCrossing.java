import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RiverCrossing {
	private Lock lock = new ReentrantLock();
	private Condition womenCond = this.lock.newCondition();
	private Condition menCond = this.lock.newCondition();
	private int womenCount = 0;
	private int menCount = 0;

	public RiverCrossing() {

	}

	public void womanArrives() {
		try {
			this.lock.lockInterruptibly();
			try {
				this.womenCount++;
				if (this.womenCount == 4) { // 4 women, 0 men : Safe (3 marks)
					this.womenCond.signal();
					this.womenCond.signal();
					this.womenCond.signal();
					this.womenCount -= 4;
				} else if (this.womenCount == 2 && this.menCount >= 2) { // 2 women, 2 men : safe (3 marks)
					this.womenCond.signal();
					this.menCond.signal();
					this.menCond.signal();
					this.womenCount -= 2;
					this.menCount -= 2;
				} else // 3 marks
					this.womenCond.await();
			} finally {
				this.lock.unlock();
			}
		} catch (InterruptedException e) {
		}
	}

	public void manArrives() {
		try {
			this.lock.lockInterruptibly();
			try {
				this.menCount++;
				if (this.menCount == 4) { // 4 men, 0 women : Safe (3 marks)
					this.menCond.signal();
					this.menCond.signal();
					this.menCond.signal();
					this.menCount -= 4;
				} else if (this.menCount == 2 && this.womenCount >= 2) { // 2 men, 2 women : safe (3 marks)
					this.menCond.signal();
					this.womenCond.signal();
					this.womenCond.signal();
					this.womenCount -= 2;
					this.menCount -= 2;
				} else // 3 marks
					this.menCond.await();
			} finally {
				this.lock.unlock();
			}
		} catch (InterruptedException e) {
		}
	}
}
