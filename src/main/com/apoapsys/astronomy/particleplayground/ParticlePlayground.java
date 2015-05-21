package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;

import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.ParticlePropulsionForceProviderImpl;

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

		// Create a stupid self-propelled spacecraft that only accelerates in a
		// single direction...
		for (Particle particle : simulator.getParticles()) {
			if (particle.body.getName().equals("Earth")) {
				Particle spacecraft = createSimpleSelfPropelledSpacecraft(particle);
				simulator.addParticle(spacecraft);
			}
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

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
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

		PlaygroundFrame frame = new PlaygroundFrame(simulator, simThread);
		frame.setVisible(true);

	}

	public static Particle createSimpleSelfPropelledSpacecraft(Particle origin) {
		ParticleEmitter emitter = new ParticleEmitter();
		emitter.setMass(27000);
		emitter.setLocation(origin.position.clone().add(new Vector(-(6371 + 400) * 1000 , 0, -(6371 + 400) * 1000 )));
		emitter.setFacing(origin.velocity.clone().normalize().inverse());
		emitter.setVelocity(origin.velocity.length());
		emitter.setRadius(15);
		emitter.setColor(Color.GREEN);

		Particle spacecraft = emitter.createParticle();
		spacecraft.body.setName("Sample Spacecraft");

		ParticlePropulsionForceProviderImpl propulsion = new ParticlePropulsionForceProviderImpl();
		propulsion.setEnabled(false);
		propulsion.setFacing(origin.velocity.clone().normalize());
		propulsion.setThrottleLevel(1.0); // 100% of capacity
		propulsion.setThrust(1.0);
		spacecraft.ownForces.add(propulsion);

		return spacecraft;

	}

}
