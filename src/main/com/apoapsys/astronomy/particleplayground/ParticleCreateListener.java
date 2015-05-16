package com.apoapsys.astronomy.particleplayground;

import com.apoapsys.astronomy.simulations.nbody.Particle;

public interface ParticleCreateListener {
	public void onParticleCreated(Particle particle);
}
