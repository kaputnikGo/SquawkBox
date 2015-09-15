package UI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ViewInflate {
	private SquawkView squawkView;
	
	private String label1aString = "Title:";
	private String label2aString = "Preheader:";
	private String label3aString = "Paragraph1:";
	private String label3bString = "Sign off:";
	private String label3cString = "Footer:";
	private String label4aString = "Banner url:";
	private String label12String = "Date";
	private String label13String = "Heading";
	private String label14String = "Author url";
	private String label15String = "Intro";

	private String button5String = "process";
	private String button6String = "EXPORT";
	private String button7String = "browser";
	private String button8String = "local open";
	private String button9String = "dump";
	private String button10String = "email";
	private String button11String = "grid";
	
	private String label16String = "Component List:";
	private String button12String = "add component";
	private String button13String = "clear browser";
	
	public void initView(final SquawkView squawkView) {
		this.squawkView = squawkView;
		initLeftPanel();
		initRightPanel();
	}
	
	private void initLeftPanel() {
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
	
	private void initRightPanel() {
		// form input area, needs to be worked		
		Composite gridComp = new Composite(squawkView.shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridComp.setLayout(gridLayout);
		
		Label label1a = new Label(gridComp, SWT.NONE);
		label1a.setText(label1aString);
		squawkView.text1 = new Text(gridComp, SWT.BORDER);
		
		Label label2a = new Label(gridComp, SWT.NONE);
		label2a.setText(label2aString);
		squawkView.text2 = new Text(gridComp, SWT.BORDER);		
		//banner url		
		Label label4a = new Label(gridComp, SWT.NONE);
		label4a.setText(label4aString);
		squawkView.text4 = new Text(gridComp, SWT.BORDER);
		//date
		Label label12 = new Label(gridComp, SWT.NONE);
		label12.setText(label12String);
		squawkView.text12 = new Text(gridComp, SWT.BORDER);
		//heading
		Label label13 = new Label(gridComp, SWT.NONE);
		label13.setText(label13String);
		squawkView.text13 = new Text(gridComp, SWT.BORDER);
		// author avatar url
		Label label14 = new Label(gridComp, SWT.NONE);
		label14.setText(label14String);
		squawkView.text14 = new Text(gridComp, SWT.BORDER);
		// intro (para)
		Label label15 = new Label(gridComp, SWT.NONE);
		label15.setText(label15String);
		squawkView.text15 = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		Label label3a = new Label(gridComp, SWT.NONE);
		label3a.setText(label3aString);
		squawkView.text3a = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		Label label3b = new Label(gridComp, SWT.NONE);
		label3b.setText(label3bString);
		squawkView.text3b = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		Label label3c = new Label(gridComp, SWT.NONE);
		label3c.setText(label3cString);
		squawkView.text3c = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		GridData data = new GridData();
		data.widthHint = 60;
		label1a.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label2a.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label3a.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label3b.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label3c.setLayoutData(data);		
		
		data = new GridData();
		data.widthHint = 60;
		label4a.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label12.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label13.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label14.setLayoutData(data);
		
		data = new GridData();
		data.widthHint = 60;
		label15.setLayoutData(data);
		
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 20;
		squawkView.text1.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 40;
		squawkView.text2.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		squawkView.text3a.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		squawkView.text3b.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		squawkView.text3c.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		squawkView.text15.setLayoutData(data2);
				
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		squawkView.text4.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		squawkView.text12.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		squawkView.text13.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		squawkView.text14.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		squawkView.text15.setLayoutData(data2);

		FormData formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(gridComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(gridComp, 10, SWT.BOTTOM);
		gridComp.setLayoutData(formGrid);
	}
}