package com.apoapsys.astronomy.particleplayground;

import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;

public class ParticlePlayground {

	public static void main(String[] args) {

		final LeapFrogSimulator simulator = new LeapFrogSimulator();
		simulator.setCheckingForCollisions(false);
		simulator.addSimulationForceProvider(new NewtonianGravityForceProviderImpl());

		try {
			SolarSystemCreator.create(simulator, false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		for (Particle particle : simulator.getParticles()) {
			if (particle.body.getName().equals("Sample Spacecraft")) {
				System.err.println(particle.body.getName() + ": " + particle.position.length() + ", " + particle.velocity.length());
			}
			if (particle.body.getName().equals("Earth")) {
				System.err.println(particle.body.getName() + ": " + particle.position.length() + ", " + particle.velocity.length());
			}
		}
		
		SimulationThread simThread = new SimulationThread(simulator);
		simThread.start();

		PlaygroundFrame frame = new PlaygroundFrame(simulator, simThread);
		frame.setVisible(true);

	}

}
