package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;

import com.apoapsys.astronomy.math.MathExt;
import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.math.Vectors;
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

		//try {
		//	Thread.sleep(5000);
		//} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
/*
		for (Particle particle : simulator.getParticles()) {
			if (particle.body.getName().equals("Sample Spacecraft")) {
				System.err.println(particle.body.getName() + ": " + particle.position.length() + ", " + particle.velocity.length());
			}
			if (particle.body.getName().equals("Earth")) {
				System.err.println(particle.body.getName() + ": " + particle.position.length() + ", " + particle.velocity.length());
			}
		}
*/
		PlaygroundFrame frame = new PlaygroundFrame(simulator, simThread);
		frame.setVisible(true);

	}

	public static Particle createSimpleSelfPropelledSpacecraft(Particle origin) {
		ParticleEmitter emitter = new ParticleEmitter();
		emitter.setMass(27000);
		
		Vector location = origin.position.clone().add(new Vector(-(6371 + 200000) * 1000 , 0, (6371 + 200000) * 1000 ));
		location.rotate(MathExt.radians(1), Vectors.Y_AXIS);
		emitter.setLocation(location);
		
		Vector facing = origin.velocity.clone().normalize();
		facing.rotate(MathExt.radians(1), Vectors.Y_AXIS);
		
		emitter.setFacing(facing);
		emitter.setVelocity(origin.velocity.length());
		emitter.setRadius(15);
		emitter.setColor(Color.GREEN);

		Particle spacecraft = emitter.createParticle();
		spacecraft.body.setName("Sample Spacecraft");

		ParticlePropulsionForceProviderImpl propulsion = new ParticlePropulsionForceProviderImpl();
		propulsion.setEnabled(false);
		propulsion.setFacing(facing.clone().inverse());
		propulsion.setThrottleLevel(1.0); // 100% of capacity
		propulsion.setThrust(1.0);
		spacecraft.ownForces.add(propulsion);

		return spacecraft;

	}

}
