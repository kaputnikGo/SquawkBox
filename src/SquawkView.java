
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
	private static final int SHELL_WIDTH = 720;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser squawkBrowser;
	
	//layout structure elements
	private Display display;
	private Shell shell;
	
	// var strings
	private String appName = "SQUAWK BOX.";

	private String label1aString = "Heading:";
	private String label2aString = "Byline:";
	private String label3aString = "Paragraph1:";
	private String label3bString = "Paragraph2:";
	private String label3cString = "Paragraph3:";
	private String label4aString = "Cover url:";

	private String button1String = "DR";
	private String button2String = "ESKY";
	private String button3String = "ILA";
	private String button4String = "MM";
	private String button5String = "process";
	private String button6String = "EXPORT";
	private String button7String = "browser";
	private String button8String = "local open";
	private String button9String = "dump";
	private String button10String = "wrapper";
	
	private static final String[] DEVICE_LIST = new String[] {"EOA", "IF", "PU", "SF"};
	private String userDevice = "EOA"; // default set
	private String userWebcode = "DR"; // default set 
	
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
	
	private Button b1;
	private Button b2;
	private Button b3;
	private Button b4;
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
		updateConsole("squawk says install...");
		squawk.installDB();		
		updateConsole("squawk says finished.");
		
		// debug new method for getting resource/template files
		//Utilities.dumpTemplate("DR", "EOA");
		
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
		innerTop.setLayout(new GridLayout());
		innerTop.setBackground(new Color(null, 183, 183, 183)); // grey

		FormData fData = new FormData();
		fData.top = new FormAttachment(0); // (n) == percentage
		fData.left = new FormAttachment(0);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(22);
		innerTop.setLayoutData(fData);

		Composite innerLow = new Composite(outer, SWT.BORDER);
		innerLow.setLayout(fillLayout);
		innerLow.setBackground(new Color(null, 88, 88, 88)); // dk grey

		fData = new FormData();
		fData.top = new FormAttachment(22);
		fData.left = new FormAttachment(0);
		fData.right = new FormAttachment(100);
		fData.bottom = new FormAttachment(100);
		innerLow.setLayoutData(fData);
		
		// top
		Composite rowComp1 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout1 = new RowLayout();
		rowLayout1.pack = false;
		rowComp1.setLayout(rowLayout1); 
		
		Composite rowComp2 = new Composite(innerTop, SWT.NONE);
		RowLayout rowLayout2 = new RowLayout();
		rowLayout2.pack = false;
		rowComp2.setLayout(rowLayout2);
		
		Composite rowComp3 = new Composite(innerTop, SWT.NONE);
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
		
	    // top
		b1 = new Button(rowComp2, SWT.PUSH);
		b1.setText(button1String);
		b2 = new Button(rowComp2, SWT.PUSH);
		b2.setText(button2String);
		b3 = new Button(rowComp2, SWT.PUSH);
		b3.setText(button3String);
		b4 = new Button(rowComp2, SWT.PUSH);
		b4.setText(button4String);
		
		b5 = new Button(rowComp3, SWT.PUSH);
		b5.setText(button5String);
		b6 = new Button(rowComp3, SWT.PUSH);
		b6.setText(button6String);
		
		addButtonListeners();
	    
		// device list
		Combo comboDevices = new Combo(rowComp2, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener selectionListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);
	    		userDevice = combo.getText();
	    		URL deviceFile = getClass().getClassLoader().getResource("templates/" + userWebcode + "/template_" + userDevice + ".html");
	    		if (deviceFile == null) 
	    			updateConsole("device file " + userDevice + " not found for site code " + userWebcode);	    		
	    		squawkBrowser.openResourcePage(deviceFile.toString());
	    	}
	    };
	    comboDevices.addSelectionListener(selectionListener);
	    comboDevices.setItems(DEVICE_LIST);
	    comboDevices.select(0);
	    
	    // lower
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
		data2.heightHint = 40;
		text1.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 20;
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
	
	private void initBrowser() {
		// first get the resource html file		
		if (squawkBrowser.initBrowser()) {
			URL initFile = getClass().getClassLoader().getResource("resources/index.html");
			if (initFile == null) {
				updateConsole("Init browser file not found. ");
				return;
			}
			squawkBrowser.openResourcePage(initFile.toString());
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
	
	private void addButtonListeners() {
		//TODO
		// check if browser exists first...
		
	    b1.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		b1.setFocus();
	    		URL drFile = getClass().getClassLoader().getResource("templates/DR/template_" + userDevice + ".html");
	    		if (drFile == null) 
	    			updateConsole("DR device file not found: " + userDevice);	    		
	    		squawkBrowser.openResourcePage(drFile.toString());
	    		userWebcode = "DR";
	    		drFile = null;
	    	}
	    });
	    b2.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		b2.setFocus();
	    		URL eskyFile = getClass().getClassLoader().getResource("templates/ESKY/template_" + userDevice + ".html");
	    		if (eskyFile == null) 
	    			updateConsole("ESKY device file not found: " + userDevice);	    		
	    		squawkBrowser.openResourcePage(eskyFile.toString());
	    		userWebcode = "ESKY";
	    		eskyFile = null;
	    	}
	    });
	    b3.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		b3.setFocus();
	    		URL ilaFile = getClass().getClassLoader().getResource("templates/ILA/template_" + userDevice + ".html");
	    		if (ilaFile == null) 
	    			updateConsole("ILA device file not found: " + userDevice);	    		
	    		squawkBrowser.openResourcePage(ilaFile.toString());
	    		userWebcode = "ILA";
	    		ilaFile = null;
	    	}
	    });
	    b4.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		b4.setFocus();
	    		URL mmFile = getClass().getClassLoader().getResource("templates/MM/template_" + userDevice + ".html");
	    		if (mmFile == null) 
	    			updateConsole("MM device file not found: " + userDevice);	    		
	    		squawkBrowser.openResourcePage(mmFile.toString());
	    		userWebcode = "MM";
	    		mmFile = null;
	    	}
	    });
	    
	    b5.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// re-process the current device with new vars
	    		squawkBrowser.processBrowser();
	    	}
	    });
	    b6.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// export the finished device as a text file
	    		squawkBrowser.exportDevice("test_device");
	    		// here tell sqltalk to save a record of the device
	    		squawk.saveDeviceRecord(userWebcode, userDevice, squawkBrowser.getDeviceHtml());
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
	    		squawkBrowser.dumpToBrowser(Utilities.debugAdminDB(squawk.getAdminTable()));
	    	}
	    });
		// add site specific wrapper to device in browser
	    b10.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.addWrapper();
	    	}
	    });
	}
	
	private void addTextListeners() {
		text1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userHeading = text1.getText();
			}
		});
		text2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userByline = text2.getText();
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
				squawkBrowser.userPara2 = text3b.getText();
			}
		});
		text3c.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userPara3 = text3c.getText();
			}
		});
		// report url
		text4.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userReportUrl = text4.getText();
			}
		});
		
	}
}
