package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import com.apoapsys.astronomy.bodies.OrbitingBody;
import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.simulations.nbody.Particle;

public class ParticleEmitter {
	private Vector location = new Vector(0, 0, 0);
	private Vector facing = new Vector(1, 0, 0);
	private double mass = 1.0;
	private double velocity = 1.0;
	private double radius = 1.0;
	
	private static int numParticlesCreated = 0;
	
	private Color color = Color.LIGHT_GRAY;
	
	private List<ParticleCreateListener> createListeners = new LinkedList<>();
	private List<EmitterPropertiesListener> propertyListeners = new LinkedList<>();
	
	private boolean suppressPropertyChangeEvents = false;
	
	public ParticleEmitter() {
		
	}
	
	public void addPropertyListener(EmitterPropertiesListener l) {
		propertyListeners.add(l);
	}
	
	public boolean removePropertyListener(EmitterPropertiesListener l) {
		return propertyListeners.remove(l);
	}
	
	private void firePropertyListeners() {
		if (suppressPropertyChangeEvents) {
			return;
		}
		
		for (EmitterPropertiesListener l : propertyListeners) {
			l.onEmitterPropertiesModified(this);
		}
	}
	
	public void addParticleCreateListener(ParticleCreateListener l) {
		createListeners.add(l);
	}
	
	public boolean removeParticleCreateListener(ParticleCreateListener l) {
		return createListeners.remove(l);
	}
	
	private void fireCreateListeners(Particle particle) {
		for (ParticleCreateListener l : createListeners) {
			l.onParticleCreated(particle);
		}
	}
	
	public Particle createParticle() {
		OrbitingBody body = new OrbitingBody();
		body.setId((++numParticlesCreated));
		body.setName("Particle #" + numParticlesCreated);
		body.setMass(mass);
		body.setRadius(radius);
		
		Vector velocityV = facing.clone().multiplyScalar(velocity);
		
		Particle particle = new Particle(body, location.clone(), velocityV);
		
		particle.extendedProperties.put("color", color);
		fireCreateListeners(particle);
		
		return particle;
	}
	
	public Vector getLocation() {
		return location;
	}

	public void setLocation(Vector location) {
		this.location = location;
		firePropertyListeners();
	}

	public Vector getFacing() {
		return facing;
	}

	public void setFacing(Vector facing) {
		this.facing = facing;
		firePropertyListeners();
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
		firePropertyListeners();
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
		firePropertyListeners();
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
		firePropertyListeners();
	}
	
	public void setColor(Color c) {
		this.color = c;
		firePropertyListeners();
	}
	
	public Color getColor() {
		return color;
	}
	
	public boolean isSuppressPropertyChangeEvents() {
		return suppressPropertyChangeEvents;
	}

	public void setSuppressPropertyChangeEvents(boolean suppressPropertyChangeEvents) {
		this.suppressPropertyChangeEvents = suppressPropertyChangeEvents;
	}
	
	
	
}
