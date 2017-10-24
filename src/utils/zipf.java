package utils;

import java.util.Random;

public class zipf {
	private Random rnd = new Random(System.currentTimeMillis());
	private int size;
	private double skew;
	private double bottom = 0;

	public zipf(int size, double skew) {
		this.size = size;
		this.skew = skew;

		for (int i = 1; i < size; i++) {
			this.bottom += (1 / Math.pow(i, this.skew));
		}
	}

	// the next() method returns an random rank id.
	// The frequency of returned rank ids are follows Zipf distribution.
	public int next() {
		int rank;
		double friquency = 0;
		double dice;

		rank = rnd.nextInt(size);
		friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
		dice = rnd.nextDouble();

		while (!(dice < friquency)) {
			rank = rnd.nextInt(size);
			friquency = (1.0d / Math.pow(rank, this.skew)) / this.bottom;
			dice = rnd.nextDouble();
		}

		return rank;
	}

	// This method returns a probability that the given rank occurs.
	public double getProbability(int rank) {
		return (1.0d / Math.pow(rank, this.skew)) / this.bottom;
	}

	public static void main(String[] args) {


		zipf zipf = new zipf(10, 1);
		for (int i = 1; i <= 10; i++)
			System.out.println(i + " " + zipf.getProbability(i));
	}
}