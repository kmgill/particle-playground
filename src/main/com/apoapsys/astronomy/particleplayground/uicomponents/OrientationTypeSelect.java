package com.apoapsys.astronomy.particleplayground.uicomponents;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataListener;

import com.apoapsys.astronomy.simulations.nbody.leapfrog.ParticlePropulsionForceProviderImpl.OrientationType;

@SuppressWarnings("serial")
public class OrientationTypeSelect extends JComboBox<OrientationType> {
	
	private OrientationTypeComboBoxModel model = new OrientationTypeComboBoxModel();
	
	public OrientationTypeSelect() {
		setModel(model);
	}
	
	class OrientationTypeComboBoxModel implements ComboBoxModel<OrientationType> {
		private OrientationType selectedItem = OrientationType.MANUAL;
		
		private List<ListDataListener> listeners = new ArrayList<>();
		
		@Override
		public int getSize() {
			return OrientationType.values().length;
		}

		@Override
		public OrientationType getElementAt(int index) {
			return OrientationType.values()[index];
		}

		@Override
		public void addListDataListener(ListDataListener l) {
			listeners.add(l);
		}

		@Override
		public void removeListDataListener(ListDataListener l) {
			listeners.remove(l);
		}

		@Override
		public void setSelectedItem(Object anItem) {
			selectedItem = (OrientationType) anItem;
		}

		@Override
		public OrientationType getSelectedItem() {
			return selectedItem;
		}
		
	}
	
}
