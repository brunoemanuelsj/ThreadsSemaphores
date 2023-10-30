package main;

import java.util.concurrent.Semaphore;

public class Main {

	private static Semaphore mutex = new Semaphore(1);
	private static Semaphore db = new Semaphore(1);
	static int rc = 0;
	private static final int qtdLeitores = 3;
	private static final int qtdEscritores = 1;

	public static void down(Semaphore sem) throws InterruptedException {
		sem.acquire();
	}

	public static void up(Semaphore sem) {
		sem.release();
	}

	public static void readDataBase() {
		System.out.println("Lendo banco de dados.");
	}

	public static void useDataRead() {
		System.out.println("");
	}

	public static void writeDataBase() throws InterruptedException {
		System.out.println("Escrevendo no banco.");
		Thread.sleep(2000);
	}

	public static void reader() throws InterruptedException {
		while (true) {

			down(mutex);

			rc++;

			if (rc == 1) {
				down(db);
			}
			System.out.println("Quantidade de leitores: " + rc);

			up(mutex);

			readDataBase();

			down(mutex);

			rc--;

			if (rc == 0) {
				up(db);
			}
			System.out.println("Quantidade de leitores: " + rc);
			up(mutex);

			useDataRead();
		}
	}

	public static void writer() throws InterruptedException {
		while (true) {
			down(db);
			writeDataBase();
			up(db);
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] processos = new Thread[qtdLeitores + qtdEscritores];

		for (int i = 0; i < qtdLeitores; i++) {

			processos[i] = new Thread(() -> {
				try {
					reader();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			processos[i].start();
		}

		for (int i = 0; i < qtdEscritores; i++) {

			processos[qtdLeitores + i] = new Thread(() -> {
				try {
					writer();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
			processos[qtdLeitores + i].start();
		}

		for (Thread processo : processos) {
			processo.join();
		}
	}

}
