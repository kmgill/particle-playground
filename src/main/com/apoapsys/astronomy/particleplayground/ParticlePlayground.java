package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.util.List;

import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.Collision;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;

public class ParticlePlayground {
	
	public static void main(String[] args) {
		
		final LeapFrogSimulator simulator = new LeapFrogSimulator();
		simulator.setCheckingForCollisions(true);
		simulator.addSimulationForceProvider(new NewtonianGravityForceProviderImpl());
		
		ParticleEmitter emitter0 = new ParticleEmitter();
		emitter0.setColor(Color.YELLOW);
		emitter0.setLocation(new Vector(0, 0, 0));
		emitter0.setFacing(new Vector(0, 0, 0));
		emitter0.setMass(270000000000.0);
		emitter0.setVelocity(0.0);
		emitter0.setRadius(4.0);
		simulator.addParticle(emitter0.createParticle());
		
		/*
		ParticleEmitter emitter0 = new ParticleEmitter();
		emitter0.setLocation(new Vector(0, 0, 8));
		emitter0.setFacing(new Vector(-1, 0, -.1));
		emitter0.setMass(270000000000.0);
		emitter0.setVelocity(1.0);
		simulator.addParticle(emitter0.createParticle());
		
		ParticleEmitter emitter1 = new ParticleEmitter();
		emitter1.setLocation(new Vector(0, 0, -8));
		emitter1.setFacing(new Vector(1, 0, .1));
		emitter1.setMass(270000000000.0);
		emitter1.setVelocity(1.0);
		simulator.addParticle(emitter1.createParticle());

		final ParticleEmitter emitter2 = new ParticleEmitter();
		emitter2.setLocation(new Vector(0, 0, 0));
		emitter2.setFacing(new Vector(1, 0, 0));
		emitter2.setMass(270000000.0);
		emitter2.setVelocity(.1);
		*/
		
		Thread simThread = new Thread() {

			@Override
			public void run() {
				
				double last = System.currentTimeMillis();
				while(true) {
					double now = System.currentTimeMillis();
					
					List<Collision> collisions = simulator.step((now - last) / 10.0);
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
					
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		simThread.start();
		
		
		
		
		PlaygroundFrame frame = new PlaygroundFrame(simulator);
		//frame.addParticleEmitter(emitter0);
		//frame.addParticleEmitter(emitter1);
		//frame.addParticleEmitter(emitter2);
		frame.setVisible(true);
		
		
		
		
		
	}
	
}
