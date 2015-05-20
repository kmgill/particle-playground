package com.apoapsys.astronomy.particleplayground;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;

import com.apoapsys.astronomy.simulations.nbody.leapfrog.NewtonianGravityForceProviderImpl;

public class NewtonianForcePanel extends AbstractOptionPanel {
	
	private NewtonianGravityForceProviderImpl forceProvider;
	
	private JTextField txtMultiplier;
	private JTextField txtBrownian;
	private JTextField txtDrag;
	private JTextField txtDamping;
	
	public NewtonianForcePanel(NewtonianGravityForceProviderImpl forceProvider) {
		this.forceProvider = forceProvider;
		
		this.setBorder(BorderFactory.createTitledBorder("Newtonian"));
		
		txtMultiplier = new JTextField(""+forceProvider.getMultiplier());
		txtBrownian = new JTextField(""+forceProvider.getBrownian());
		txtDrag = new JTextField(""+forceProvider.getDrag());
		txtDamping = new JTextField(""+forceProvider.getDamp());
		
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
