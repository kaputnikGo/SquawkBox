
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class SquawkView {
	private static final int SHELL_HEIGHT = 580;
	private static final int SHELL_WIDTH = 640;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser squawkBrowser; // confusing as its not the actual browser
	
	//layout structure elements
	private Display display;
	private Shell shell;
	private Composite fillComp;
	private FormData formFill;
	private Composite rowComp;
	private RowLayout rowLayout;
	private FormData formRow;
	private Composite gridComp;
	private GridLayout gridLayout;
	private GridData data;
	private GridData data2;
	private FormData formGrid;
	
	// var strings until something better...
	private String appName = "SQUAWK BOX.";
	private String label0String = "INSTRUCTIONS:";
	private String label1String = "Enter the title <templateHeading>";
	private String label2String = "Enter the byline <templateByline>";
	private String label3String = "Enter paragraph text <templatePara1-3>";
	private String label4String = "URL for image <templateCover>";
	private String label5String = "Press OK when finished";
	private String label6String = "Site specific device templates";

	private String label1aString = "Title:";
	private String label2aString = "Byline:";
	private String label3aString = "Paragraph1:";
	private String label3bString = "Paragraph2:";
	private String label3cString = "Paragraph3:";
	private String label4aString = "image url:";

	private String button1String = "MM";
	private String button2String = "DR";
	private String button3String = "ESKY";
	private String button4String = "ILA";
	private String button5String = "process";
	private String button6String = "export";
	private String debugTitle = "Console:";
	
	//gui elements
	private Label label0;
	private Label label1;
	private Label label1a;
	private Label label2;
	private Label label2a;
	private Label label3;
	private Label label3a;
	private Label label3b;
	private Label label3c;
	private Label label4;
	private Label label4a;
	private Label label5;
	private Label label6;
	
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
	private Label debugLabel;
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
		if (squawkBrowser.initBrowser()) {
			//test'
			updateConsole("init browser, opening localhost index...");
			squawkBrowser.openWebpage("http://127.0.0.1/");
			updateConsole("openWebpage called.");
		}
		else {
			// no browser...
		}
		
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
	
	public void updateConsole(String message) {
		if (Utilities.checkString(message)) {
			shell.open();
			debugText.append(message + "\n");
			gridComp.layout();
		}
		else {
			
		}
	}
	
/************************************************************
* 
* 		// internal methods 
* 
************************************************************/	
	
	private void initLayout() {
		initLeftPanel();
		initRightPanel();
		initBottomPanel();
	}
	
	private void initLeftPanel() {
		// instructions and a big space...
		fillComp = new Composite(shell, SWT.BORDER);
		fillComp.setLayout(new FillLayout(SWT.VERTICAL));
					
		label0 = new Label(fillComp, SWT.NONE);
		label0.setText(label0String);		
		label1 = new Label(fillComp, SWT.NONE);
		label1.setText(label1String);
		label2 = new Label(fillComp, SWT.NONE);
		label2.setText(label2String);
		label3 = new Label(fillComp, SWT.NONE);
		label3.setText(label3String);
		label4 = new Label(fillComp, SWT.NONE);
		label4.setText(label4String);
		label5 = new Label(fillComp, SWT.NONE);
		label5.setText(label5String);
		label6 = new Label(fillComp, SWT.NONE);
		label6.setText(label6String);	
		
		formFill = new FormData();
		formFill.top = new FormAttachment(0, 10);
		formFill.left = new FormAttachment(0, 10);
		formFill.bottom = new FormAttachment(75, 0);
		formFill.right = new FormAttachment(40, 0);
		fillComp.setLayoutData(formFill);
	}
	
	private void initRightPanel() {
		// form input area, needs to be worked		
		gridComp = new Composite(shell, SWT.NONE);
		gridLayout = new GridLayout();
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
		
		data = new GridData();
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
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		text1.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 20;
		text2.setLayoutData(data2);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 60;
		text3a.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 60;
		text3b.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 60;
		text3c.setLayoutData(data2);
		
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		text4.setLayoutData(data2);
		
		formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(fillComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(fillComp, 10, SWT.BOTTOM);
		gridComp.setLayoutData(formGrid);
		
		text1.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userByline = text1.getText();
			}
		});
		text2.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				squawkBrowser.userHeading = text2.getText();
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
	
	private void initBottomPanel() {
		// currently a row of buttons
		rowComp = new Composite(shell, SWT.NONE);
		rowLayout = new RowLayout();
		rowLayout.pack = false;
		rowComp.setLayout(rowLayout);
		
		b1 = new Button(rowComp, SWT.PUSH);
		b1.setText(button1String);
		b2 = new Button(rowComp, SWT.PUSH);
		b2.setText(button2String);
		b3 = new Button(rowComp, SWT.PUSH);
		b3.setText(button3String);
		b4 = new Button(rowComp, SWT.PUSH);
		b4.setText(button4String);
		b5 = new Button(rowComp, SWT.PUSH);
		b5.setText(button5String);
		b6 = new Button(rowComp, SWT.PUSH);
		b6.setText(button6String);
		
	    b1.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		//open a set url at first
	    		String userString = "http://127.0.0.1/SquawkBin/MM/";
	    		squawkBrowser.openWebpage(userString);
	    	}
	    });
	    b2.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		//open a set url at first
	    		String userString = "http://127.0.0.1/SquawkBin/DR/";
	    		squawkBrowser.openWebpage(userString);
	    	}
	    });
	    b3.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		//open a set url at first
	    		String userString = "http://127.0.0.1/SquawkBin/ESKY/";
	    		squawkBrowser.openWebpage(userString);
	    	}
	    });
	    b4.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		//open a set url at first
	    		String userString = "http://127.0.0.1/SquawkBin/ILA/";
	    		squawkBrowser.openWebpage(userString);
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
	    		// be nice if you could zip the lot in a folder
	    		// and the smtp - or invoke email with attachment
	    		// button labeled "send to vince"
	    		squawkBrowser.exportDevice();
	    	}
	    });
		
		// debugging console
		debugLabel = new Label(gridComp, SWT.NONE);
		debugLabel.setText(debugTitle);
		debugText = new Text(gridComp, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		debugText.setText("waiting...\n");
		data = new GridData();
		data.widthHint = 60;
		debugLabel.setLayoutData(data);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.heightHint = 100;
		debugText.setLayoutData(data);
		
		formRow = new FormData();
		formRow.top = new FormAttachment(fillComp, 10);
		formRow.left = new FormAttachment(0, 10);
		formRow.bottom = new FormAttachment(100, -10);
		formRow.right = new FormAttachment(100, -10);
		rowComp.setLayoutData(formRow);
	}
}
