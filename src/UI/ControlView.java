package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ControlView {
	
	private static String button5String = "process";
	private static String button6String = "EXPORT";
	private static String button7String = "browser";
	private static String button8String = "local open";
	private static String button9String = "dump";
	private static String button10String = "refresh";
	private static String button11String = "grid";
	
	private static String label16String = "Component List:";
	private static String button12String = "add component";
	private static String button13String = "start";
	
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
		
		
		squawkView.b9 = new Button(rowComp1, SWT.PUSH);
		squawkView.b9.setText(button9String);
		squawkView.b10 = new Button(rowComp1, SWT.PUSH);
		squawkView.b10.setText(button10String);
		squawkView.b7 = new Button(rowComp1, SWT.PUSH);
		squawkView.b7.setText(button7String);
		squawkView.b8 = new Button(rowComp1, SWT.PUSH);
		squawkView.b8.setText(button8String);
		
		squawkView.addWebcodeButtons();
		
		squawkView.b5 = new Button(rowComp1, SWT.PUSH);
		squawkView.b5.setText(button5String);
		squawkView.b6 = new Button(rowComp1, SWT.PUSH);
		squawkView.b6.setText(button6String);
		squawkView.b11 = new Button(rowComp1, SWT.PUSH);
		squawkView.b11.setText(button11String);
  
		// rowComp3 reserved for drop down component list
		Label label16 = new Label(squawkView.rowComp3, SWT.NONE);
		label16.setText(label16String);
		squawkView.b13 = new Button(squawkView.rowComp3, SWT.PUSH);
		squawkView.b13.setText(button13String);
		squawkView.b12 = new Button(squawkView.rowComp3, SWT.PUSH);
		squawkView.b12.setText(button12String);

		// debugging console
	    squawkView.debugText = new Text(innerLow, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
	    squawkView.debugText.setText("console waiting...\n");
		
	}
	
}