import org.eclipse.swt.SWT;
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
	private static final int SHELL_HEIGHT = 480;
	private static final int SHELL_WIDTH = 640;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser browser;
	
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
	private String label1String = "Enter the title";
	private String label2String = "Enter the byline (optional)";
	private String label3String = "Enter paragraph text";
	private String label4String = "URL for image (optional)";
	private String label5String = "Press OK when finished";

	private String label1aString = "Title:";
	private String label2aString = "Byline:";
	private String label3aString = "Paragraph:";
	private String label4aString = "image url:";

	private String button1String = "this one";
	private String button2String = "no 2";
	private String button3String = "no 3";
	private String button4String = "no 4";
	private String debugTitle = "Console:";

/*
	private String ordString = "This is no ordinary sentence.";
	private String specString = "Fire, Exclaimation mark";
	private String warnString = "Perhaps don't press this";
	private String resultString = "idiot";
*/
	
	//gui elements
	private Label label0;
	private Label label1;
	private Label label1a;
	private Label label2;
	private Label label2a;
	private Label label3;
	private Label label3a;
	private Label label4;
	private Label label4a;
	private Label label5;
	private Label label6;
	
	private Text text1;
	private Text text2;
	private Text text3;
	private Text text4;
	
	private Button b1;
	private Button b2;
	private Button b3;
	private Button b4;
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
		browser = new SquawkBrowser(this);
		updateConsole("Browser created.");
		if (browser.initBrowser()) {
			//test'
			updateConsole("init browser, open localhost...");
			browser.openWebpage("http://127.0.0.1/");
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
	          messageBox.setMessage("Close the View?");
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
		label6.setText("Fire, exclamation mark.");	
		
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
		text3 = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
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
		label4a.setLayoutData(data);
		
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		text1.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 40;
		text2.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		data2.heightHint = 100;
		text3.setLayoutData(data2);
		data2 = new GridData(GridData.FILL_HORIZONTAL);
		text4.setLayoutData(data2);
		
		formGrid = new FormData();
		formGrid.top = new FormAttachment(0, 10);
		formGrid.left = new FormAttachment(fillComp, 10);
		formGrid.right = new FormAttachment(100, -10);
		formGrid.bottom = new FormAttachment(fillComp, 10, SWT.BOTTOM);
		gridComp.setLayoutData(formGrid);
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

	/*
	private void tempStore() {
//		event listeners	
		b1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				label6.setText(resultString);
				// start a timer then clear at end
				System.out.println("The button was pressed!");
			}
		});	
	}
	*/
	
}
