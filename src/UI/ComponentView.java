package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class ComponentView {
	
	private static String label0bString = "Component";
	
	public static void loadView(final SquawkView squawkView) {
		Composite gridComp = new Composite(squawkView.shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridComp.setLayout(gridLayout);
		
		Label label0b = new Label(gridComp, SWT.NONE);
		label0b.setText(label0bString);
		GridData data = new GridData();
		data.widthHint = 60;
		label0b.setLayoutData(data);
		
		FormData formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(gridComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(gridComp, 10, SWT.BOTTOM);
		gridComp.setLayoutData(formGrid);	
	}
}