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

import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.particleplayground.uicomponents.NumberField;
import com.apoapsys.astronomy.particleplayground.uicomponents.NumberField.ValueChangedListener;

public class EmitterPropertiesPanel extends AbstractOptionPanel {
	
	private ParticleEmitter particleEmitter;
	
	private NumberField locationX;
	private NumberField locationY;
	private NumberField locationZ;
	
	private NumberField facingX;
	private NumberField facingY;
	private NumberField facingZ;
	
	private NumberField velocity;
	private NumberField mass;
	private NumberField radius;
	
	private JPanel color;
	
	public EmitterPropertiesPanel(final ParticleEmitter particleEmitter) {
		this.particleEmitter = particleEmitter;
		
		setLayout(new GridLayout(7, 1));
		
		this.setBorder(BorderFactory.createTitledBorder("Particle Emitter"));
		
		ValueChangedListener valueChangedListener = new ValueChangedListener() {

			@Override
			public void onValueChanged(double newValue) {
				updateEmitterFromComponents();
			}
			
		};
		
		add(createLabeledPanel("Location:", 
				locationX = new NumberField(particleEmitter.getLocation().x),
				locationY = new NumberField(particleEmitter.getLocation().y),
				locationZ = new NumberField(particleEmitter.getLocation().z)));
		
		locationX.addValueChangedListener(valueChangedListener);
		locationY.addValueChangedListener(valueChangedListener);
		locationZ.addValueChangedListener(valueChangedListener);
		
		
		add(createLabeledPanel("Facing:", 
				facingX = new NumberField(particleEmitter.getFacing().x),
				facingY = new NumberField(particleEmitter.getFacing().y),
				facingZ = new NumberField(particleEmitter.getFacing().z)));
		
		facingX.addValueChangedListener(valueChangedListener);
		facingY.addValueChangedListener(valueChangedListener);
		facingZ.addValueChangedListener(valueChangedListener);
		
		add(createLabeledPanel("Velocity:", velocity = new NumberField(particleEmitter.getVelocity())));
		add(createLabeledPanel("Mass:", mass = new NumberField(particleEmitter.getMass())));
		add(createLabeledPanel("Radius:", radius = new NumberField(particleEmitter.getRadius())));
		
		velocity.addValueChangedListener(valueChangedListener);
		mass.addValueChangedListener(valueChangedListener);
		radius.addValueChangedListener(valueChangedListener);
		
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
