package com.apoapsys.astronomy.particleplayground;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import com.apoapsys.astronomy.particleplayground.uicomponents.NumberField;
import com.apoapsys.astronomy.particleplayground.uicomponents.NumberField.ValueChangedListener;
import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;

public class NewtonianForcePanel extends AbstractOptionPanel {
	
	private NewtonianGravityForceProviderImpl forceProvider;
	
	private NumberField txtMultiplier;
	private NumberField txtBrownian;
	private NumberField txtDrag;
	private NumberField txtDamping;
	
	public NewtonianForcePanel(NewtonianGravityForceProviderImpl forceProvider) {
		this.forceProvider = forceProvider;
		
		this.setBorder(BorderFactory.createTitledBorder("Newtonian"));
		
		ValueChangedListener valueChangedListener = new ValueChangedListener() {

			@Override
			public void onValueChanged(double newValue) {
				updateForceFromComponents();
			}
			
		};
		
		txtMultiplier = new NumberField(forceProvider.getMultiplier());
		txtBrownian = new NumberField(forceProvider.getBrownian());
		txtDrag = new NumberField(forceProvider.getDrag());
		txtDamping = new NumberField(forceProvider.getDamp());
		
		txtMultiplier.addValueChangedListener(valueChangedListener);
		txtBrownian.addValueChangedListener(valueChangedListener);
		txtDrag.addValueChangedListener(valueChangedListener);
		txtDamping.addValueChangedListener(valueChangedListener);
		
		setLayout(new GridLayout(5, 1));

		add(createLabeledPanel("Multiplier:", txtMultiplier));
		add(createLabeledPanel("Brownian:", txtBrownian));
		add(createLabeledPanel("Drag:", txtDrag));
		add(createLabeledPanel("Damping:", txtDamping));
		
		JButton btnUpdate = new JButton("Apply");
		add(createPanel(btnUpdate));
		btnUpdate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateForceFromComponents();
			}
			
		});
	}
	
	private void updateForceFromComponents() {
		forceProvider.setMultiplier(getValueFromComponent(txtMultiplier));
		forceProvider.setBrownian(getValueFromComponent(txtBrownian));
		forceProvider.setDrag(getValueFromComponent(txtDrag));
		forceProvider.setDamp(getValueFromComponent(txtDamping));
	}
	
}
