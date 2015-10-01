package UI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import MAIN.Utilities;

public class TemplateView {
	
	public static void loadView(SquawkView squawkView) {
		squawkView.scrollComp = new ScrolledComposite(squawkView.shell, SWT.V_SCROLL);		
		squawkView.scrollComp.setExpandHorizontal(true);
		squawkView.scrollComp.setExpandVertical(true);
		squawkView.scrollComp.setMinWidth((Utilities.SHELL_WIDTH / 2) - Utilities.SHELL_PADDING);
		squawkView.scrollComp.setMinHeight(Utilities.SHELL_HEIGHT - Utilities.SHELL_PADDING);
		
		squawkView.gridComp = new Composite(squawkView.scrollComp, SWT.NONE);
		squawkView.scrollComp.setContent(squawkView.gridComp);
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		squawkView.gridComp.setLayout(gridLayout);		
		/*
		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		squawkView.gridComp.setLayout(fillLayout);
		*/

		Map.Entry<String, String> entry;
		Label label;
		Text text;
		squawkView.formTextFields = new ArrayList<Text>(squawkView.FORM_FIELDS.size());
		squawkView.formFieldLabels = new ArrayList<Label>(squawkView.FORM_FIELDS.size());
		
		Iterator<Entry<String, String>> entries = squawkView.FORM_FIELDS.entrySet().iterator();
		while (entries.hasNext()) {
			entry = (Map.Entry<String, String>)entries.next();
			
			label = getFormFieldLabel(squawkView);
			label.setText(entry.getKey());
			squawkView.formFieldLabels.add(label);

			text = getFormFieldText(squawkView);
			text.setText(entry.getValue());
			text.setEnabled(true);
			squawkView.formTextFields.add(text);
		}
		
		FormData formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(squawkView.gridComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(squawkView.gridComp, 10, SWT.BOTTOM);
		squawkView.gridComp.setLayoutData(formGrid);	
	}
	
	public static Text getFormFieldText(SquawkView squawkView) {
		Text text = new Text(squawkView.gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData data2;
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = Utilities.FORM_FIELD_HEIGHT;
		data2.minimumHeight = Utilities.FORM_FIELD_HEIGHT;
		text.setLayoutData(data2);
		return text;
	}
	
	public static Label getFormFieldLabel(final SquawkView squawkView) {
		Label label = new Label(squawkView.gridComp, SWT.NONE);
		GridData data;
		data = new GridData();
		data.widthHint = Utilities.FORM_LABEL_WIDTH;
		label.setLayoutData(data);
		return label;
	}
}