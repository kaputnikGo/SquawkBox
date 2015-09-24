package UI;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
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

import MAIN.SqlTalk;
import MAIN.Utilities;


// view and controller...

public class SquawkView {
	private static final int SHELL_WIDTH = 1200;
	private static final int SHELL_HEIGHT = 1000;
	private static final boolean DEBUG = true;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser squawkBrowser;
	
	private static final String errorString = "n/a";
	
	public static final int VIEW_TEMPLATE = 0;
	public static final int VIEW_COMPONENT = 1;
	
	//layout structure elements
	protected Display display;
	protected Shell shell;
	protected ViewInflate viewInflate;
	protected Composite gridComp; 
	
	// var strings
	private String appName = "SQUAWK BOX.";
	
	protected String userWebcode = "";
	protected String userTemplateName = "";
	protected String userComponentName = "";
	
	// get them from the squawk
	protected List<Text> formTextFields;
	protected List<Label> formFieldLabels;
	protected Map<String, String> FORM_FIELDS;

	protected Button[] webcodeButtons;
	protected Combo comboTemplates;
	protected Combo componentTemplates;
	protected Composite rowComp2;
	protected Composite rowComp3;
	protected Composite rightPanel;
	protected StackLayout stackLayout;	
	
	protected Button b5;
	protected Button b6;
	protected Button b7;
	protected Button b8;
	protected Button b9;
	protected Button b10;
	protected Button b11;
	protected Button b12;
	protected Button b13;
	protected Text debugText;

	
	public SquawkView() {
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
	    	    
		//rightPanel = new Composite(shell, SWT.NONE);
		//stackLayout = new StackLayout();
		//rightPanel.setLayout(stackLayout);
	    squawk = new SqlTalk(this);
	    
	    // this may need to be a LinkedHashMap to preserve insertion order.
	    FORM_FIELDS = new LinkedHashMap<String, String>(squawk.initForDisplays());	    
	    
	    // populate the formField ArrayLists with the values from FORM_FIELDS
	    
	    viewInflate = new ViewInflate();
		viewInflate.initView(this, VIEW_TEMPLATE);
		updateConsole("formfield size: " + FORM_FIELDS.size());
		squawk.installDB();				
		updateConsole("Squawk says finished DB.");

		addButtonListeners();		
		addComboElements();
		addTextListeners();
		
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
		
	public Shell getShell() {
		if (shell != null) 
			return shell;
		else 
			return null;		
	}
	
	public void updateFormFieldMap(List<String> foundTags, List<String> foundContent) {
		// squawkbrowser has parsed the html content and found
		// valid tags to display to the user - not the tag contents.
		if (foundTags == null || foundTags.isEmpty()) return;
		if (foundContent == null || foundContent.isEmpty()) return;

		FORM_FIELDS.clear();
		resetFormFieldsDisplay();
		
		int i = 0;
		for (String key : foundTags) {	
			FORM_FIELDS.putIfAbsent(key, foundContent.get(i));
			i++;
		}
		updateConsole("updated form field size: " + FORM_FIELDS.size());
	}
	
	public void updateFormFieldsDisplay() {
		// fields are not the labels of div ids... :)
		// this is a bit loopy between here and browser	
		// TODO
		// here is a problem if number of fields found exceeds number 
		// created by default in squawk
		int fieldNum = FORM_FIELDS.size();
		
		if (fieldNum > Utilities.DEFAULT_FIELD_MAX) {
			updateConsole("ERROR: form field size " + fieldNum + ", exceeds default allocation " + Utilities.DEFAULT_FIELD_MAX);
			// for each one
			System.out.println("ERROR: formField num request exceeds default allocation.");
			return;
			/*
			if (allocateNewFields(fieldNum - Utilities.DEFAULT_FIELD_MAX)) {
				updateConsole("added new formFields");
			}
			else {
				System.out.println("ERROR in allocate new");
			}
			*/
		}
				
		int i = 0;
		for (String key : FORM_FIELDS.keySet()) {
			// get the value associated with the key
			formTextFields.get(i).setText(FORM_FIELDS.get(key));
			formTextFields.get(i).setEnabled(true);
			formTextFields.get(i).setVisible(true);
			// get the key
			formFieldLabels.get(i).setText(key);
			formFieldLabels.get(i).setVisible(true);
			i++;
		}
		// for remainder of formfields not used, can:
		formTextFields.get(i).setVisible(false);
	}
	
	public void clearFormFields() {
		for (int i = 0; i < formTextFields.size(); i++) {
			formTextFields.get(i).setText("");
			formTextFields.get(i).setEnabled(true);
		}
	}
	
	public void disableFormFields() {
		// called due to template parse error
		// no editable span divs found
		for (int i = 0; i < formTextFields.size(); i++) {
			formTextFields.get(i).setText(errorString);
			formTextFields.get(i).setEnabled(false);
		}
		updateConsole("Not an editable template.");
	}
	
	public void updateConsole(final String message) {		
		if (DEBUG) {		
			if (Utilities.checkString(message)) {
				debugText.append(message + "\n");
			}
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
	
	void userButtonPressed(Event event) {
		// find out which button was pressed
		Button button = ((Button) event.widget);
		userWebcode = button.getText();
		comboTemplates.setItems(squawk.getTemplateList(userWebcode));
		updateConsole("Button press: " + userWebcode);
	}
	

	void updateTextField(ModifyEvent event) {
		//TODO - which one is it..?
		//Text text = ((Text) event.widget);	
		//squawkBrowser.userTitle = text.getText();
		// generic load them all regardless...
	}
	
	private void resetFormFieldsDisplay() {
		// changed the template etc, so clear ALL the form field texts and labels
		for (int i = 0; i < formFieldLabels.size(); i++) {
			formFieldLabels.get(i).setText("empty " + i);
			formFieldLabels.get(i).setVisible(false);
			formTextFields.get(i).setText("empty field");	
			formTextFields.get(i).setVisible(false);
		}
	}
	
	private boolean allocateNewFields(int numFields) {
		// template/component page has found more tags than default allocation
		// add another numFields number here.
		System.out.println("allocate new, total fields: " + (formFieldLabels.size() + numFields));

		Label label;
		Text text;
		GridData data;
		GridData data2;
		
		shell.open();
		shell.layout(true);
		gridComp.layout(true);
		//Control[] children = gridComp.getChildren();

		for (int i = 0; i < formFieldLabels.size() + numFields; i++) { 			
			label = new Label(gridComp, SWT.NONE);
			label.setText("empty");
			data = new GridData();
			data.widthHint = Utilities.FORM_LABEL_WIDTH;
			label.setLayoutData(data);
			formFieldLabels.add(label);

			text = new Text(gridComp, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
			text.setText("empty field");
			text.setEnabled(true);
			data2 = new GridData(GridData.FILL_HORIZONTAL);
			data2.heightHint = Utilities.FORM_FIELD_HEIGHT;
			// width will be remainder of gridComp after labels
			text.setLayoutData(data2);
			text.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					updateTextField(event);
				}
			});	
			formTextFields.add(text);
		}
		gridComp.pack();
		shell.pack();
		return true;
	}
	
/************************************************************
* 
* 		// interface methods 
* 
************************************************************/		
	void addButtonListeners() {
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
	    		// send default save name string to browser
	    		squawkBrowser.exportBrowser("test_save");
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
	    		squawkBrowser.dumpToBrowser(Utilities.debugDumpTableDB(squawk.getDefaultDB(), squawk.getComponentTable()));
	    	}
	    });
		// add site specific wrapper to device in browser
	    b10.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.openEmailTemplate();
	    	}
	    });
		// toggle grid lines of blue
	    b11.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.toggleGrid();
	    	}
	    });
		// add selected component to browser
	    b12.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {    		
	    		squawkBrowser.addComponentToPage(userComponentName);
	    		updateFormFieldsDisplay();
	    	}
	    });
		// clear browser browser to allow creating component template
	    b13.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {    		
	    		squawkBrowser.startComponents();
	    		viewInflate.switchViewType(VIEW_COMPONENT);
	    	}
	    });
	}
	
	void addWebcodeButtons() {
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
		
	void addTextListeners() {
		// these listen to the text boxes and update the browser vars
		for (int i = 0; i < FORM_FIELDS.size(); i++) {
			formTextFields.get(i).addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent event) {
					updateTextField(event);
				}
			});			
		}
	}
	
	void addComboElements() {	
		comboTemplates = new Combo(rowComp2, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener comboListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);
	    		
	    		// this will load the template for a given webcode
	    		userTemplateName = combo.getText();
	    		URL templateFile = getClass().getClassLoader().getResource("templates/EMAIL/" + userWebcode + "/" + userTemplateName + ".html");
	    		
	    		if (templateFile == null) {
	    			updateConsole("Template file not found for userWebcode " + userWebcode);
	    			//TODO
	    			// reset the form and the browser window
	    			disableFormFields();
	    			squawkBrowser.setBrowserText("Template file " + userTemplateName + " not found for userWebcode " + userWebcode);
	    			return;
	    		}
	    		else {
	    			squawkBrowser.openTemplatePage(templateFile.toString());
	    		}
	    	}
	    };
	    comboTemplates.addSelectionListener(comboListener);
	    comboTemplates.setItems(Utilities.TEMPLATE_LIST);
	    comboTemplates.select(0);
	    
	    //TODO
	    // component selector
		componentTemplates = new Combo(rowComp3, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener componentListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);
	    		userComponentName = combo.getText();
	    	}
	    };
	    componentTemplates.addSelectionListener(componentListener);
	    // need a string[] of the component names.
	    // this can only take place after db is ready...
	    String[] compList = squawk.getComponentList();
	    if (compList == null) {
	    	compList = new String[1];
	    	compList[1] = "not loaded";
	    }
	    componentTemplates.setItems(compList);
	    //componentTemplates.select(0);
	}
}
