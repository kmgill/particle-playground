package com.apoapsys.astronomy.particleplayground;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.apoapsys.astronomy.math.MathExt;
import com.apoapsys.astronomy.math.Vector;
import com.apoapsys.astronomy.particleplayground.uicomponents.NumberField;
import com.apoapsys.astronomy.particleplayground.uicomponents.NumberField.ValueChangedListener;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.ParticlePropulsionForceProviderImpl;

//ParticlePropulsionForceProvider
public class PropulsionForcePanel extends AbstractOptionPanel {
	
	private final ParticlePropulsionForceProviderImpl provider;
	
	private NumberField facingX;
	private NumberField facingY;
	private NumberField facingZ;
	
	private JCheckBox chkEnabled;
	private JSlider sldrThrottleLevel;
	private NumberField txtThrust;
	
	public PropulsionForcePanel(ParticlePropulsionForceProviderImpl provider) {
		this.provider = provider;
		
		setLayout(new GridLayout(4, 1));
		
		this.setBorder(BorderFactory.createTitledBorder("Particle Propulsion"));
		
		ChangeListener changeListener = new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				updateProviderFromComponents();
			}
			
		};
		
		ValueChangedListener valueChangedListener = new ValueChangedListener() {

			@Override
			public void onValueChanged(double newValue) {
				updateProviderFromComponents();
			}
			
		};
		
		add(createPanel(chkEnabled = new JCheckBox("Enabled")));
		chkEnabled.setSelected(provider.isEnabled());
		chkEnabled.addChangeListener(changeListener);
		
		add(createLabeledPanel("Facing:", 
				facingX = new NumberField(provider.getFacing().x),
				facingY = new NumberField(provider.getFacing().y),
				facingZ = new NumberField(provider.getFacing().z)));
		
		facingX.addValueChangedListener(valueChangedListener);
		facingY.addValueChangedListener(valueChangedListener);
		facingZ.addValueChangedListener(valueChangedListener);
		
		add(createLabeledPanel("Throttle:",
				sldrThrottleLevel = new JSlider(0, 100)
				));
		sldrThrottleLevel.setValue((int) (MathExt.round(provider.getThrottleLevel() * 100))); 
		sldrThrottleLevel.addChangeListener(changeListener);
		
		
		add(createLabeledPanel("Engine Thrust:",
			txtThrust = new NumberField(provider.getThrust())));
		txtThrust.addValueChangedListener(valueChangedListener);
		txtThrust.setToolTipText("Currently implemented as basic acceleration in m/s");
	}
	
	protected void updateProviderFromComponents() {
		provider.setEnabled(chkEnabled.isSelected());
		provider.setFacing(new Vector(facingX.getNumberValue(), facingY.getNumberValue(), facingZ.getNumberValue()));
		provider.setThrottleLevel(sldrThrottleLevel.getValue() / 100.0);
		provider.setThrust(txtThrust.getNumberValue());
	}
	
}
