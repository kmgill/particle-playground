package com.apoapsys.astronomy.particleplayground;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.simulations.nbody.Particle;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.LeapFrogSimulator;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.FPSAnimator;

public class PlaygroundFrame extends JFrame {
	
	private LeapFrogSimulator simulator;
	private ParticleGJPanel simPanel;
	
	private JPanel emitterPropertiesPanel;
	
	public PlaygroundFrame(LeapFrogSimulator simulator) {
		setTitle("Particle Playground");
		setSize(500, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.simulator = simulator;
		
		setLayout(new BorderLayout());
		
		simPanel = new ParticleGJPanel(simulator);
		add(simPanel, BorderLayout.CENTER);
		
		emitterPropertiesPanel = new JPanel();
		emitterPropertiesPanel.setLayout(new GridLayout(3, 1, 3, 3));
		
		JScrollPane scroll = new JScrollPane(emitterPropertiesPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(scroll, BorderLayout.EAST);
		
		JToolBar toolbar = new JToolBar();
		add(toolbar, BorderLayout.NORTH);
		
		JButton btnAddEmitter = new JButton("New Emitter");
		toolbar.add(btnAddEmitter);
		btnAddEmitter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ParticleEmitter emitter = new ParticleEmitter();
				emitter.setLocation(new Vector(0, 0, 60.0));
				emitter.setMass(270000000.0);
				emitter.setVelocity(0.5);
				addParticleEmitter(emitter);
			}
		});
		
		final FPSAnimator animator = new FPSAnimator(simPanel, 60);
		animator.start();
		
	}
	
	public void addParticleEmitter(ParticleEmitter particleEmitter) {
		particleEmitter.addParticleCreateListener(new ParticleCreateListener() {
			@Override
			public void onParticleCreated(Particle particle) {
				simulator.addParticle(particle);
			}
		});
		
		EmitterPropertiesPanel emitterPanel = new EmitterPropertiesPanel(particleEmitter);
		emitterPropertiesPanel.add(emitterPanel);
		simPanel.getEmittersList().add(particleEmitter);
	}
	
	public List<ParticleEmitter> getEmittersList() {
		return simPanel.getEmittersList();
	}
	
	
}
