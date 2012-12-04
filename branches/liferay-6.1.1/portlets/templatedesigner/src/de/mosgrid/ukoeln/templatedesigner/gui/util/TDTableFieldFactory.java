package de.mosgrid.ukoeln.templatedesigner.gui.util;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

import de.mosgrid.msml.util.IDictionary;
import de.mosgrid.ukoeln.templatedesigner.document.AbstractPropertyBean;
import de.mosgrid.ukoeln.templatedesigner.document.IPropertyBean;
import de.mosgrid.ukoeln.templatedesigner.document.TDTemplateJobDocument;
import de.mosgrid.ukoeln.templatedesigner.helper.StringH;

public abstract class TDTableFieldFactory extends DocumentTableFieldFactory {

	private static final long serialVersionUID = -3423225174749768501L;

	public TDTableFieldFactory(TDTemplateJobDocument doc) {
		super(doc);
	}

	@Override
	public Field createField(final Container container, final Object itemId, final Object propertyId,
			final Component uiContext) {
		IPropertyBean b = (IPropertyBean) itemId;
		if (propertyId.equals(AbstractPropertyBean.EDIT_PROPID)) {
			CheckBox chk = new CheckBox();
			chk.setImmediate(true);
			chk.setEnabled(!StringH.isNullOrEmpty(b.getRef()));
			return chk;
		}
		
		if (propertyId.equals(AbstractPropertyBean.DICT_PROPID)) {
			BeanItemContainer<IDictionary> datasource = getInternalDictionaryDataSource(getDoc());
			if (datasource == null)
				return null;

			ComboBox cmb = new ComboBox();
			cmb.setContainerDataSource(datasource);
			cmb.setNullSelectionAllowed(false);
			cmb.setImmediate(true);
			cmb.setItemCaptionMode(ComboBox.ITEM_CAPTION_MODE_EXPLICIT);
			for (int i = 0; i < datasource.size(); i++) {
				IDictionary dict = datasource.getIdByIndex(i);
				cmb.setItemCaption(dict, dict.getDictPrefix());
			}

			if (datasource.size() > 0) {
				cmb.setValue(datasource.getIdByIndex(0));
			}
			return cmb;
		}

		if (propertyId.equals(AbstractPropertyBean.REF_PROPID)) {
			ComboBox cmb = new ComboBox();
			cmb.setContainerDataSource(getInternalDictRefDataSource(getDoc(), b.getDict()));
			cmb.setNullSelectionAllowed(false);
			cmb.setImmediate(true);
			return cmb;
		}

		if (propertyId.equals(AbstractPropertyBean.VAL_PROPID)) {
			TextField txt = new TextField();
			txt.setEnabled(!StringH.isNullOrEmpty(b.getRef()));
			txt.setImmediate(true);
			return txt;
		}

		if (propertyId.equals(AbstractPropertyBean.INDEX_PROPID)) {
			return null;
		}

		return new TextField();
	}

	protected abstract BeanItemContainer<IDictionary> getInternalDictionaryDataSource(TDTemplateJobDocument doc);

	protected abstract BeanItemContainer<String> getInternalDictRefDataSource(TDTemplateJobDocument doc, IDictionary iDictionary);
}
