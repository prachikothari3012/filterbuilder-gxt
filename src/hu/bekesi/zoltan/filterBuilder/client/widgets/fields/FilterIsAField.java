/*
 *
 *   Copyright 2011 Zoltan Bekesi
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   NOTICE THE GXT ( Ext-GWT ) LIBRARY IS A GPL v3 LICENCED PRODUCT.
 *   FIND OUT MORE ON:  http://www.sencha.com/license
 *
 *   Author : Zoltan Bekesi<bekesizoltan@gmail.com>
 *
 * */

package hu.bekesi.zoltan.filterBuilder.client.widgets.fields;

import hu.bekesi.zoltan.filterBuilder.client.criteria.SimpleModel;
import hu.bekesi.zoltan.filterBuilder.client.resources.ResourceHelper;
import hu.bekesi.zoltan.filterBuilder.client.widgets.ComboBoxHelper;
import hu.bekesi.zoltan.filterBuilder.client.widgets.I18NSimpleComboBox;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Widget;

public class FilterIsAField<M extends ModelData> extends FilterField {

	private static final long serialVersionUID = 1350088696367002710L;

	private ListStore<M> _store = null;
	private String _comboDisplayField = null;
	private String _comboValueField = null;

	public FilterIsAField() {

	}

//	public FilterIsAField(String valueField_, String name_) {
//		super(valueField_, name_);
//	}

	public FilterIsAField(String valueField_, String name_, ListStore<M> store_, String comboDisplayField_, String comboValueField_) {
		super(valueField_, name_);
		_store = store_;
		_comboDisplayField = comboDisplayField_;
		_comboValueField = comboValueField_;
	}

	@Override
	public List<Widget> getWidgets(final SimpleModel model_) {

		ArrayList<Widget> widgets = new ArrayList<Widget>();

		I18NSimpleComboBox combo = new I18NSimpleComboBox();
		combo.setWidth(135);
		combo.setForceSelection(true);
		combo.setEditable(false);
		combo.setTriggerAction(TriggerAction.ALL);

		combo.add(ResourceHelper.getResources().is_a(), "is-a");
		combo.add(ResourceHelper.getResources().not_is_a(), "not is-a");

		combo.setSimpleValue("is-a");

		if (model_.getOp() == null)
			model_.setOp("is-a");


		combo.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				model_.setOp(se.getSelectedItem().getValue());

			}
		});

		widgets.add(combo);

		if (_store != null) {
			final ComboBox<M> _comboBox = new ComboBox<M>();
			_comboBox.setStore(_store);
			_comboBox.setDisplayField(_comboDisplayField);
			_comboBox.setValueField(_comboValueField);
			_comboBox.select(0);
			_comboBox.setEditable(false);
			_comboBox.setTypeAhead(false);
			_comboBox.setTriggerAction(TriggerAction.ALL);

			if (_store.getLoader() != null) {
				_comboBox.setPageSize(10);
				_comboBox.setMinListWidth(350);
			}
			widgets.add(_comboBox);
			_comboBox.addSelectionChangedListener(new SelectionChangedListener<M>() {

				@Override
				public void selectionChanged(SelectionChangedEvent<M> se) {
					model_.removeData(0);
					model_.addData(_comboBox.getSelection().get(0));
				}
			});


			final DelayedTask _setValueTask = new DelayedTask(new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {

					model_.removeData(0);
					model_.addData(ComboBoxHelper.getComboBoxRealValue(_comboBox));
				}
			});

			_comboBox.addKeyListener(new KeyListener(){

				@Override
				public void componentKeyPress(ComponentEvent event) {
					super.componentKeyPress(event);
					_setValueTask.delay(250);
				}
			});


		} else {

			final TextField<String> tf = new TextField<String>();

			tf.addListener(Events.Change, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					model_.removeData(0);
					model_.addData(be.getValue());

				}
			});
			widgets.add(tf);
		}
		return widgets;

	}

}
