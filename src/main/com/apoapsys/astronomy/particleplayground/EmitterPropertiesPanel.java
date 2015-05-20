package com.apoapsys.astronomy.particleplayground;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.apoapsys.astronomy.math.Vector;

public class EmitterPropertiesPanel extends AbstractOptionPanel {
	
	private ParticleEmitter particleEmitter;
	
	private JTextField locationX;
	private JTextField locationY;
	private JTextField locationZ;
	
	private JTextField facingX;
	private JTextField facingY;
	private JTextField facingZ;
	
	private JTextField velocity;
	private JTextField mass;
	private JTextField radius;
	
	private JPanel color;
	
	public EmitterPropertiesPanel(final ParticleEmitter particleEmitter) {
		this.particleEmitter = particleEmitter;
		
		setLayout(new GridLayout(7, 1));
		
		this.setBorder(BorderFactory.createTitledBorder("Particle Emitter"));

		add(createLabeledPanel("Location:", 
				locationX = new JTextField(""+particleEmitter.getLocation().x),
				locationY = new JTextField(""+particleEmitter.getLocation().y),
				locationZ = new JTextField(""+particleEmitter.getLocation().z)));
		
		add(createLabeledPanel("Facing:", 
				facingX = new JTextField(""+particleEmitter.getFacing().x),
				facingY = new JTextField(""+particleEmitter.getFacing().y),
				facingZ = new JTextField(""+particleEmitter.getFacing().z)));
		
		add(createLabeledPanel("Velocity:", velocity = new JTextField(""+particleEmitter.getVelocity())));
		add(createLabeledPanel("Mass:", mass = new JTextField(""+particleEmitter.getMass())));
		add(createLabeledPanel("Radius:", radius = new JTextField(""+particleEmitter.getRadius())));
		
		
		color = new JPanel();
		color.setBackground(particleEmitter.getColor());
		color.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Color newColor = JColorChooser.showDialog(
						EmitterPropertiesPanel.this,
	                     "Choose Particle Color",
	                     color.getBackground());
				color.setBackground(newColor);
				updateEmitterFromComponents();
			}
			
		});
		add(createLabeledPanel("Color:", color));

		
		JButton btnCreate = new JButton("Add Particle");
		btnCreate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				particleEmitter.createParticle();
			}
		});
		
		JButton btnUpdate = new JButton("Update Emitter");
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateEmitterFromComponents();
			}
		});
		add(createPanel(btnCreate, btnUpdate));
		
		
		particleEmitter.addPropertyListener(new EmitterPropertiesListener() {

			@Override
			public void onEmitterPropertiesModified(ParticleEmitter emitter) {
				updateComponentsFromEmitter();
			}
			
		});
	}
	
	
	public void updateComponentsFromEmitter() {
		locationX.setText(""+particleEmitter.getLocation().x);
		locationY.setText(""+particleEmitter.getLocation().y);
		locationZ.setText(""+particleEmitter.getLocation().z);
		
		facingX.setText(""+particleEmitter.getFacing().x);
		facingY.setText(""+particleEmitter.getFacing().y);
		facingZ.setText(""+particleEmitter.getFacing().z);
		
		velocity.setText(""+particleEmitter.getVelocity());
		mass.setText(""+particleEmitter.getMass());
		radius.setText(""+particleEmitter.getRadius());
		
		color.setBackground(particleEmitter.getColor());
		
		
		normalizeFacingValues();
	}
	
	public void normalizeFacingValues() {
		
		Vector facing = new Vector(getValueFromComponent(facingX), getValueFromComponent(facingY), getValueFromComponent(facingZ)).getNormalized();
		facingX.setText(""+facing.x);
		facingY.setText(""+facing.y);
		facingZ.setText(""+facing.z);
	}
	
	public void updateEmitterFromComponents() {
		normalizeFacingValues();
		particleEmitter.setSuppressPropertyChangeEvents(true);
		particleEmitter.setLocation(new Vector(getValueFromComponent(locationX), getValueFromComponent(locationY), getValueFromComponent(locationZ)));
		particleEmitter.setFacing(new Vector(getValueFromComponent(facingX), getValueFromComponent(facingY), getValueFromComponent(facingZ)));
		
		particleEmitter.setVelocity(getValueFromComponent(velocity));
		particleEmitter.setMass(getValueFromComponent(mass));
		particleEmitter.setRadius(getValueFromComponent(radius));
		particleEmitter.setSuppressPropertyChangeEvents(false);
		particleEmitter.setColor(color.getBackground());
		
	}
	
}
