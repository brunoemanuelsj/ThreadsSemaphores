package Main;

import java.util.concurrent.Semaphore;

public class Main {
	private static final int N = 5;
	private static final int THINKING = 0;
	private static final int HUNGRY = 1;
	private static final int EATING = 2;
	private static int[] state = new int[N];
	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore[] s = new Semaphore[N];

	public static int LEFT(int i) {
		return (i + N - 1) % N;
	}

	public static int RIGHT(int i) {
		return (i + 1) % N;
	}

	public static void eat(int i) throws InterruptedException {
		System.out.println("Filósofo " + i + " está comendo.");
		Thread.sleep(3000);
	}

	public static void think(int i) throws InterruptedException {
		System.out.println("Filósofo " + i + " está pensando.");
		Thread.sleep(5000);
	}

	public static void down(Semaphore sem) throws InterruptedException {
		sem.acquire();
	}

	public static void up(Semaphore sem) {
		sem.release();
	}

	public static void test(int i) throws InterruptedException {
		if (state[i] == HUNGRY && state[LEFT(i)] != EATING && state[RIGHT(i)] != EATING) {
			state[i] = EATING;
			eat(i);
			up(s[i]);
		}
	}

	public static void takeForks(int i) throws InterruptedException {
		System.out.println("Filósofo " + i + " pegando garfos.");
		down(mutex);
		state[i] = HUNGRY;
		test(i);
		up(mutex);
		down(s[i]);
	}

	public static void putForks(int i) throws InterruptedException {
		System.out.println("Filósofo " + i + " guardando garfos.");
		down(mutex);
		state[i] = THINKING;
		test(LEFT(i));
		test(RIGHT(i));
		up(mutex);
	}

	public static void philosopher(int i) throws InterruptedException {
		while (true) {
			think(i);
			takeForks(i);
			putForks(i);
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < N; i++) {
			s[i] = new Semaphore(0);
		}

		Thread[] philosophers = new Thread[N];
		for (int i = 0; i < N; i++) {

			final int philosopherId = i;

			philosophers[i] = new Thread(() -> {

				try {
					philosopher(philosopherId);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			});
			philosophers[i].start();
		}

		for (Thread philosopher : philosophers) {
			try {
				philosopher.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
