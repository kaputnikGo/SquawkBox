package UI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import MAIN.Utilities;

public class TemplateView {
	
	public static void loadView(final SquawkView squawkView) {
		squawkView.gridComp = new Composite(squawkView.shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		squawkView.gridComp.setLayout(gridLayout);

		Map.Entry<String, String> entry;
		GridData data;
		GridData data2;
		Label label;
		Text text;
		squawkView.formTextFields = new ArrayList<Text>(squawkView.FORM_FIELDS.size());
		squawkView.formFieldLabels = new ArrayList<Label>(squawkView.FORM_FIELDS.size());
		
		Iterator<Entry<String, String>> entries = squawkView.FORM_FIELDS.entrySet().iterator();
		while (entries.hasNext()) {
			entry = (Map.Entry<String, String>)entries.next();
			
			label = new Label(squawkView.gridComp, SWT.NONE);
			label.setText(entry.getKey());
			data = new GridData();
			data.widthHint = Utilities.FORM_LABEL_WIDTH;
			label.setLayoutData(data);
			squawkView.formFieldLabels.add(label);

			text = new Text(squawkView.gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			text.setText(entry.getValue());
			text.setEnabled(true);
			data2 = new GridData(GridData.FILL_HORIZONTAL);
			data2.heightHint = Utilities.FORM_FIELD_HEIGHT;
			// width will be remainder of gridComp after labels
			text.setLayoutData(data2);
			squawkView.formTextFields.add(text);
		}
		
		FormData formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(squawkView.gridComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(squawkView.gridComp, 10, SWT.BOTTOM);
		squawkView.gridComp.setLayoutData(formGrid);	
	}
}