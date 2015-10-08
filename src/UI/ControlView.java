package UI;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import MAIN.Utilities;

public class ControlView {
	private static final String[] BUTTON_NAMES = new String[] {
		"dump", "refresh", "browser", "local open",
		"process", "EXPORT", "grid", "selector",
		"LOCK", "update selector", "CANCEL", 
		"start new", "add component"
	};

	public static void loadView(final SquawkView squawkView) {
		// controls
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		squawkView.shell.setLayout( fillLayout );

		Composite outer = new Composite(squawkView.shell, SWT.BORDER);
		outer.setBackground(new Color(null, 0, 0, 0));

		FormLayout formLayout = new FormLayout();
		formLayout.marginHeight = 5;
		formLayout.marginWidth = 5;
		formLayout.spacing = 5;
		outer.setLayout(formLayout);

		Composite innerTop = new Composite(outer, SWT.BORDER);
		innerTop.setLayout(new FillLayout());
		innerTop.setBackground(new Color(null, 183, 183, 183)); // grey

		FormData fData = new FormData();
		fData.top = new FormAttachment(0); // (n) == percentage
		fData.left = new FormAttachment(0);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(30);
		innerTop.setLayoutData(fData);

		Composite innerLow = new Composite(outer, SWT.BORDER);
		innerLow.setLayout(fillLayout);
		innerLow.setBackground(new Color(null, 88, 88, 88)); // dk grey

		fData = new FormData();
		fData.top = new FormAttachment(30);
		fData.left = new FormAttachment(0);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		innerLow.setLayoutData(fData);
		
		// top
		Composite rowComp1 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout1 = new RowLayout();
		rowLayout1.pack = false;
		rowComp1.setLayout(rowLayout1); 
		
		squawkView.rowComp2 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout2 = new RowLayout();
		rowLayout2.pack = false;
		squawkView.rowComp2.setLayout(rowLayout2);
		
		squawkView.rowComp3 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout3 = new RowLayout();
		rowLayout3.pack = false;
		squawkView.rowComp3.setLayout(rowLayout3);
				
		squawkView.buttonList = new ArrayList<Button>(20);
		
		for (int i = 0; i <= 10; i++) {
			squawkView.buttonList.add(getControlButton(rowComp1, BUTTON_NAMES[i]));			
		}
		// add the webcode buttons
		for (int i = 0; i < Utilities.WEBCODE_LIST.length; i++) {
			Button button = getControlButton(squawkView.rowComp2, Utilities.WEBCODE_LIST[i]);
			button.addListener(SWT.Selection,  new Listener() {
				@Override
				public void handleEvent(Event event) {
					squawkView.userButtonPressed(event);
				}
				
			});
			squawkView.buttonList.add(button);
		}
		// rowComp3 reserved for drop down component list
		Label label = new Label(squawkView.rowComp3, SWT.NONE);
		label.setText("Component List:");		
		squawkView.buttonList.add(getControlButton(squawkView.rowComp3, BUTTON_NAMES[11]));		
		squawkView.buttonList.add(getControlButton(squawkView.rowComp3, BUTTON_NAMES[12]));

		// debugging console
	    squawkView.debugText = new Text(innerLow, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	    squawkView.debugText.setText("Console loaded.\n");
		
	}
	
	public static Button getControlButton(Composite comp, String name) {
		Button button = new Button(comp, SWT.PUSH);
		button.setText(name);
		button.setEnabled(true);		
		return button;
	}
}