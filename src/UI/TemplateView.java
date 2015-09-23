package UI;

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

public class TemplateView {
	
	private static int formFieldHeight = 60;
	private static int formLabelWidth = 120;
	
	public static void loadView(final SquawkView squawkView) {
		Composite gridComp = new Composite(squawkView.shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridComp.setLayout(gridLayout);

		GridData data;
		GridData data2;
		squawkView.formTextFields = new Text[squawkView.FORM_FIELDS.size()];
		
		Iterator<Entry<String, String>> entries = squawkView.FORM_FIELDS.entrySet().iterator();
		int i = 0;
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>)entries.next();
			
			Label label = new Label(gridComp, SWT.NONE);
			label.setText(entry.getKey());
			data = new GridData();
			data.widthHint = formLabelWidth;
			label.setLayoutData(data);
			
			squawkView.formTextFields[i] = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			squawkView.formTextFields[i].setText(entry.getValue());
			squawkView.formTextFields[i].setEnabled(true);
			data2 = new GridData(GridData.FILL_HORIZONTAL);
			data2.heightHint = formFieldHeight;
			// width will be remainder of gridComp after labels
			squawkView.formTextFields[i].setLayoutData(data2);
			i++;
		}
		
		FormData formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(gridComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(gridComp, 10, SWT.BOTTOM);
		gridComp.setLayoutData(formGrid);	
	}
}