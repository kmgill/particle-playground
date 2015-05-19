package com.apoapsys.astronomy.particleplayground;

import java.util.List;

import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.Collision;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;

public class ParticlePlayground {
	
	public static void main(String[] args) {
		
		final LeapFrogSimulator simulator = new LeapFrogSimulator();
		simulator.setCheckingForCollisions(false);
		simulator.addSimulationForceProvider(new NewtonianGravityForceProviderImpl());

		try {
			SolarSystemCreator.create(simulator);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Thread simThread = new Thread() {

			@Override
			public void run() {
				
				double last = System.currentTimeMillis();
				while(true) {
					double now = System.currentTimeMillis();
					
					List<Collision> collisions = simulator.step((now - last));
					last = now;
					
					if (collisions != null) {
						for (Collision collision : collisions) {
							Particle particle0 = collision.particle0;
							Particle particle1 = collision.particle1;
							
							if (particle0 == null || particle1 == null) {
								continue;
							}
							
							System.err.println("Collision of " + particle0.body.getName() + " and " + particle1.body.getName());
							
							
						}
					}
					
					
					//try {
						//Thread.sleep(10);
				//	} catch (InterruptedException e) {
					//	e.printStackTrace();
					//}
				}
			}
		};
		simThread.start();
		
		
		
		
		PlaygroundFrame frame = new PlaygroundFrame(simulator);
		frame.setVisible(true);
		
		
		
		
		
	}
	
}
