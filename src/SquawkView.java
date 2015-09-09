
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


// view and controller...

public class SquawkView {
	private static final int SHELL_HEIGHT = 600;
	private static final int SHELL_WIDTH = 1100;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser squawkBrowser;
	
	//layout structure elements
	private Display display;
	private Shell shell;
	
	// var strings
	private String appName = "SQUAWK BOX.";

	private String label1aString = "Title:";
	private String label2aString = "Preheader:";
	private String label3aString = "Paragraph1:";
	private String label3bString = "Sign off:";
	private String label3cString = "Footer:";
	private String label4aString = "Banner url:";

	private String button5String = "process";
	private String button6String = "EXPORT";
	private String button7String = "browser";
	private String button8String = "local open";
	private String button9String = "dump";
	private String button10String = "email";
	
	private String userWebcode = "";
	private String userTemplateName = "";
	
	private Label label1a;
	private Label label2a;
	private Label label3a;
	private Label label3b;
	private Label label3c;
	private Label label4a;
	
	private Text text1;
	private Text text2;
	private Text text3a;
	private Text text3b;
	private Text text3c;
	private Text text4;
	
	private Composite rowComp1;
	private Composite rowComp2;
	private Composite rowComp3;
	private Button[] webcodeButtons;
	
	private Button b5;
	private Button b6;
	private Button b7;
	private Button b8;
	private Button b9;
	private Button b10;
	private Text debugText;

	
	SquawkView() {
		// constructor
		// init the display window		
		display = new Display();		
		shell = new Shell(display);
		shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
		shell.setMinimumSize(SHELL_WIDTH, SHELL_HEIGHT);
		shell.setText(appName);
		shell.setLayout(new FormLayout());

		icon = new Image(display, getClass().getClassLoader().getResourceAsStream("resources/plug_icon.png"));
	    shell.setImage(icon); 

		initLayout();
		squawk = new SqlTalk(this);
		squawkBrowser = new SquawkBrowser(this);
		updateConsole("Browser created.");
		initBrowser();
		
	    shell.addListener(SWT.Close, new Listener() {
	        public void handleEvent(Event event) {
	          int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
	          MessageBox messageBox = new MessageBox(shell, style);
	          messageBox.setText("Information");
	          messageBox.setMessage("Close the SquawkBox?");
	          event.doit = messageBox.open() == SWT.YES;
	        }
	    });
	}
	
/************************************************************
* 
* 		// public methods 
* 
************************************************************/		
	public void run() {				
		shell.open();
		updateConsole("Squawk says install...");
		squawk.installDB();		
		updateConsole("Squawk says finished.");
		
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch()) {
				display.sleep();
			}
		}
		// gc
		display.dispose();
	}
	
	public Display getDisplay() {
		return display;
	}
	
	public void browserClosed() {
		squawkBrowser = null;
	}
	
	public void updateFormFields() {
		// hopefully called by browser, after it has
		// loaded a template file
		updateConsole("update form fields called.");
		
		text1.setText(squawkBrowser.userTitle);
		text2.setText(squawkBrowser.userPreheader);
		text3a.setText(squawkBrowser.userPara1);
		text3b.setText(squawkBrowser.userSignoff);
		text3c.setText(squawkBrowser.userFooter);
		text4.setText(squawkBrowser.userBannerLogo);
	}
	
	public void updateConsole(final String message) {
		if (Utilities.checkString(message)) {
			// TODO
			shell.open(); // needed?
			debugText.append(message + "\n");
		}
		else {
			
		}
	}
		
/************************************************************
* 
* 		// control methods 
* 
************************************************************/	
	private void initBrowser() {
		// first get the resource html file		
		if (squawkBrowser.initBrowser()) {
			URL initFile = getClass().getClassLoader().getResource("resources/index.html");
			if (initFile == null) {
				updateConsole("Init browser file not found. ");
				return;
			}
			squawkBrowser.openInitPage(initFile.toString());
			updateConsole("openResourcePage called.");
		}
	}
	
	private void openBrowser() {
		// mainly to allow user to re-open if closed
		if (squawkBrowser == null) {
			squawkBrowser = new SquawkBrowser(this);
			updateConsole("Browser created.");
			initBrowser();
		}
		else {
			updateConsole("browser not null.");
		}
	}
	
	private void userButtonPressed(Event event) {
		// find out which button was pressed
		Button button = ((Button) event.widget);
		userWebcode = button.getText();
		updateConsole("Button press: " + userWebcode);
	}
	
/************************************************************
* 
* 		// interface methods 
* 
************************************************************/	
	
	private void initLayout() {
		initLeftPanel();
		initRightPanel();
	}	
	
	private void initLeftPanel() {
		// controls
		FillLayout fillLayout = new FillLayout();
		fillLayout.marginHeight = 5;
		fillLayout.marginWidth = 5;
		shell.setLayout( fillLayout );

		Composite outer = new Composite(shell, SWT.BORDER);
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
		rowComp1 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout1 = new RowLayout();
		rowLayout1.pack = false;
		rowComp1.setLayout(rowLayout1); 
		
		rowComp2 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout2 = new RowLayout();
		rowLayout2.pack = false;
		rowComp2.setLayout(rowLayout2);
		
		rowComp3 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout3 = new RowLayout();
		rowLayout3.pack = false;
		rowComp3.setLayout(rowLayout3);
		
		
		b9 = new Button(rowComp1, SWT.PUSH);
		b9.setText(button9String);
		b10 = new Button(rowComp1, SWT.PUSH);
	    b10.setText(button10String);
		b7 = new Button(rowComp1, SWT.PUSH);
		b7.setText(button7String);
		b8 = new Button(rowComp1, SWT.PUSH);
		b8.setText(button8String);
		
		inflateButtons();
		
		b5 = new Button(rowComp3, SWT.PUSH);
		b5.setText(button5String);
		b6 = new Button(rowComp3, SWT.PUSH);
		b6.setText(button6String);
  
		addButtonListeners();
		
		// device list
		Combo comboTemplates = new Combo(rowComp2, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener selectionListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);
	    		
	    		// this will load the template for a given webcode
	    		userTemplateName = combo.getText();
	    		URL templateFile = getClass().getClassLoader().getResource("templates/EMAIL/" + userWebcode + "/" + userTemplateName + ".html");
	    		
	    		if (templateFile == null) {
	    			updateConsole("Template file not found for userWebcode " + userWebcode);
	    			return;
	    		}
	    		else {
	    			squawkBrowser.openTemplatePage(templateFile.toString());
	    		}
	    	}
	    };
	    comboTemplates.addSelectionListener(selectionListener);
	    comboTemplates.setItems(Utilities.TEMPLATE_LIST);
	    comboTemplates.select(0);
	    
		// debugging console
		debugText = new Text(innerLow, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		debugText.setText("console waiting...\n");
	}
	
	private void initRightPanel() {
		// form input area, needs to be worked		
		Composite gridComp = new Composite(shell, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridComp.setLayout(gridLayout);
		
		label1a = new Label(gridComp, SWT.NONE);
		label1a.setText(label1aString);
		text1 = new Text(gridComp, SWT.BORDER);
		
		label2a = new Label(gridComp, SWT.NONE);
		label2a.setText(label2aString);
		text2 = new Text(gridComp, SWT.BORDER);
		
		label3a = new Label(gridComp, SWT.NONE);
		label3a.setText(label3aString);
		text3a = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		label3b = new Label(gridComp, SWT.NONE);
		label3b.setText(label3bString);
		text3b = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		
		label3c = new Label(gridComp, SWT.NONE);
		label3c.setText(label3cString);
		text3c = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
				
		label4a = new Label(gridComp, SWT.NONE);
		label4a.setText(label4aString);
		text4 = new Text(gridComp, SWT.BORDER);
		
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
		
		GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 20;
		text1.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 40;
		text2.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		text3a.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		text3b.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 120;
		text3c.setLayoutData(data2);
				
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		text4.setLayoutData(data2);

		FormData formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(gridComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(gridComp, 10, SWT.BOTTOM);
		gridComp.setLayoutData(formGrid);
		
		addTextListeners();
	}
			
	private void inflateButtons() {
		// generate the number of buttons needed based upon Utilities.WEBCODE_LIST length.
		int numButtons = Utilities.WEBCODE_LIST.length;
		if (numButtons <= 0) numButtons = 1;		
		webcodeButtons = new Button[numButtons];		
		for (int i = 0; i < numButtons; i++) {
			webcodeButtons[i] = new Button(rowComp2, SWT.PUSH);
			webcodeButtons[i].setText(Utilities.WEBCODE_LIST[i]);
			webcodeButtons[i].addListener(SWT.Selection,  new Listener() {
				@Override
				public void handleEvent(Event event) {
					userButtonPressed(event);
				}
				
			});
		}		
	}
	
	private void addButtonListeners() {
		//TODO	    
	    b5.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// re-process the current device with new vars
	    		squawkBrowser.processBrowser();
	    	}
	    });
	    b6.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// export the finished device as a text file
	    		squawkBrowser.exportBrowser("test_device");
	    		
	    		// here tell sqltalk to save a record of the device	    		
	    		//squawk.saveDeviceRecord(userWebcode, userDevice, squawkBrowser.getDeviceHtml());
	    	}
	    });
	    b7.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// 
	    		openBrowser();
	    	}
	    });
		// open file button
	    b8.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.openLocalDirFile();
	    	}
	    });
		// dump to squawkBrowser button
	    b9.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.dumpToBrowser(Utilities.debugDumpTableDB(squawk.getDefaultDB(), squawk.getTemplatesTable()));
	    	}
	    });
		// add site specific wrapper to device in browser
	    b10.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.openEmailTemplate();
	    	}
	    });
	}
		
	private void addTextListeners() {
		// these listen to the text boxes and update the browser vars
		text1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userTitle = text1.getText();
			}
		});
		text2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userPreheader = text2.getText();
			}
		});
		
		// need to break this down into the 3 paras on the Viewer.
		text3a.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userPara1 = text3a.getText();
			}
		});
		text3b.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userSignoff = text3b.getText();
			}
		});
		text3c.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userFooter = text3c.getText();
			}
		});
		// report url
		text4.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userBannerLogo = text4.getText();
			}
		});
		
	}
}
