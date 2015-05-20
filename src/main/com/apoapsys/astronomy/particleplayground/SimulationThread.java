package com.apoapsys.astronomy.particleplayground;

import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;

public class SimulationThread extends Thread {
	private LeapFrogSimulator simulator;
	private boolean stop = false;
	private boolean paused = false;
	private double speed = 1;
	
	public SimulationThread(LeapFrogSimulator simulator) {
		this.simulator = simulator;
	}
	
	public void run() {
		double last = System.currentTimeMillis();
		
		while(!stop) {
			double now = System.currentTimeMillis();
			
			if (!paused) {
				simulator.step((now - last) * speed );
				last = now;
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				last = System.currentTimeMillis();
			}
		}
	}
	
	public void cancel() {
		stop = true;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}
	
	public boolean isPaused() {
		return paused;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	
	
}
