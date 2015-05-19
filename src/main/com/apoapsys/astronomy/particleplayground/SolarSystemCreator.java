package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.util.List;

import com.apoapsys.astronomy.Constants;
import com.apoapsys.astronomy.bodies.Body;
import com.apoapsys.astronomy.bodies.MajorBodiesLoader;
import com.apoapsys.astronomy.bodies.OrbitingBody;
import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.orbits.EllipticalOrbit;
import com.apoapsys.astronomy.orbits.Ephemeris;
import com.apoapsys.astronomy.orbits.OrbitPosition;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.apoapsys.astronomy.time.DateTime;

public class SolarSystemCreator {
	
	public SolarSystemCreator() {
		
	}
	
	protected static Particle createPreInitialState(OrbitingBody body, DateTime dt) {
		Particle particle = new Particle();
		particle.body = body;
		particle.velocity = new Vector();
		particle.position = (body.getOrbit() != null) ? body.getOrbit().positionAtTime(dt).getPosition() : new Vector();
		particle.position.multiplyScalar(Constants.AU_TO_KM * 1000);
		particle.velocity.multiplyScalar(Constants.AU_TO_KM * 1000);
		return particle;
	}
	
	protected static Particle createInitialState(OrbitingBody body, DateTime dt) {
		
		Particle initialState = createPreInitialState(body, dt);
		
		
		Ephemeris ephemeris = body.getEphemeris();
		ephemeris.meanMotion = 1.0 / ephemeris.period;
		if (ephemeris != null && ephemeris.ascendingNode != null) {
			EllipticalOrbit orbit = new EllipticalOrbit(ephemeris);
			
			OrbitPosition position = orbit.positionAtTime(dt);
			OrbitPosition velocity = orbit.velocityAtTime(dt);
			
			initialState.position = position.getPosition();
			initialState.velocity = velocity.getPosition();
			
			initialState.position.multiplyScalar(Constants.AU_TO_KM * 1000);
			initialState.velocity.multiplyScalar(Constants.AU_TO_KM * 1000);
		}
		
		
		
		return initialState;
	}
	
	
	protected static void createParticle(LeapFrogSimulator sim, OrbitingBody orbitingBody, DateTime dt, Particle parent) {
		if (orbitingBody.getMass() == 0) {
			return;
		}
		
		Particle bodyState = createInitialState(orbitingBody, dt);
		
		if (parent != null) {
			bodyState.position.add(parent.position);
			bodyState.velocity.add(parent.velocity);
		}
		
		bodyState.extendedProperties.put("color", Color.WHITE);
		
		sim.addParticle(bodyState);
		
		for (Body moon : orbitingBody.getOrbitingBodies()) {
			OrbitingBody orbitingMoon = (OrbitingBody) moon;
			createParticle(sim, orbitingMoon, dt, bodyState);
		}
		
	}
	
	public static void create(LeapFrogSimulator sim) throws Exception {
		
		List<Body> bodies = MajorBodiesLoader.load();
		DateTime dt = new DateTime();

		for (Body body : bodies) {
			OrbitingBody orbitingBody = (OrbitingBody) body;
			////if (body.getName().equalsIgnoreCase("sun")) {
			//	continue;
			//}
			createParticle(sim, orbitingBody, dt, null);
			System.err.println("Adding " + orbitingBody.getName());
		}
		
		
	}
	
}
