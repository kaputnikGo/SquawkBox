package UI;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
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
	private static final boolean DEBUG = true;
	private static Image icon;
	private SqlTalk squawk;
	private SquawkBrowser squawkBrowser;
	private String appName = "SQUAWK BOX.";
	private int currentFieldsNum = Utilities.DEFAULT_FIELD_MAX;
	
	private static final String errorString = "n/a";
	private static final String USER_TAG = "selection";
	
	public static final int VIEW_TEMPLATE = 0;
	public static final int VIEW_COMPONENT = 1;
	
	//layout structure elements
	protected Display display;
	protected Shell shell;
	protected ViewInflate viewInflate;
	protected ScrolledComposite scrollComp;
	protected Composite gridComp; 
	
	// var strings
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
	protected Button b14;
	protected Button b15;
	protected Button b16;
	protected Text debugText;

	
	public SquawkView() {
		// constructor
		
		// init the display window		
		display = new Display();		
		shell = new Shell(display);
		shell.setSize(Utilities.SHELL_WIDTH, Utilities.SHELL_HEIGHT);
		shell.setMinimumSize(Utilities.SHELL_WIDTH, Utilities.SHELL_HEIGHT);
		shell.setText(appName);
		shell.setLayout(new FormLayout());
		icon = new Image(display, getClass().getClassLoader().getResourceAsStream("resources/plug_icon.png"));
	    shell.setImage(icon); 
	    	    
		//rightPanel = new Composite(shell, SWT.NONE);
		//stackLayout = new StackLayout();
		//rightPanel.setLayout(stackLayout);
	    squawk = new SqlTalk(this);
	    
	    FORM_FIELDS = new LinkedHashMap<String, String>(squawk.initForDisplays());	    

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
			//FORM_FIELDS.putIfAbsent(key, foundContent.get(i));
			FORM_FIELDS.put(key, foundContent.get(i));
			i++;
		}
		
		// recalculate widget display of form fields		
		scrollComp.setMinSize(gridComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        gridComp.layout();
        
		updateConsole("updated form field size: " + FORM_FIELDS.size());
	}
	
	public void updateFormFieldsDisplay() {
		int fieldNum = FORM_FIELDS.size();
		if (fieldNum > currentFieldsNum) {
			updateConsole("form field size " + fieldNum + ", exceeds default allocation of " + currentFieldsNum);			
			if (allocateNewFields(fieldNum)) {
				updateConsole("added new formFields");
			}
			else {
				System.out.println("ERROR in allocate new");
				updateConsole("Error in allocating new form fields.");
				return;
			}			
		}
				
		int i = 0;
		for (String key : FORM_FIELDS.keySet()) {
			// get the value associated with the key
			formTextFields.get(i).setText(FORM_FIELDS.get(key));
			// enable and visible in case we working with default fields still
			formTextFields.get(i).setEnabled(true);
			formTextFields.get(i).setVisible(true);
			// get the key
			formFieldLabels.get(i).setText(key);
			formFieldLabels.get(i).setVisible(true);
			i++;
		}
	}
	
	public void clearFormFields() {
		for (int i = 0; i < formTextFields.size(); i++) {
			formTextFields.get(i).setText("");
			formTextFields.get(i).setEnabled(true);
		}
	}
	
	public void enableAddComponent(boolean enable) {
		if (enable) b12.setEnabled(true);
		else b12.setEnabled(true);
	}
	
	public void lockInterface() {
		// allow only one selection, is a live edit, commit then its gone
		if (squawkBrowser.isLocked()) {
			b12.setEnabled(false);
			b5.setEnabled(false);
			b14.setEnabled(false);
			b15.setText("UNLOCK");
			b16.setEnabled(true);
			squawkBrowser.setLock(false);			
		}
		else {
			b12.setEnabled(true);
			b5.setEnabled(true);
			b14.setEnabled(true);
			b15.setText("LOCK");
			b16.setEnabled(false);
			squawkBrowser.setLock(true);
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
		//TODO - got the id num	
		// this called every char...
		//	
		
		//Text text = ((Text) event.widget);		
		//int id = ((int)event.widget.getData());
		//updateConsole("text field updated id: " + id);
		
		//String what = text.getText();
		//updateConsole("widget text: " + what);
		//updateConsole("formfield: " + formTextFields.get(id).getText());
		// no way
		//formTextFields.get(id).setText(what);
		
		
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
	
	private boolean preProcessFormFields() {
		updateConsole("preprocess formfields now...");
		int i = 0;
		for (String key : FORM_FIELDS.keySet()) {
			FORM_FIELDS.put(key, formTextFields.get(i).getText());
			if (key.toString().equals("title")) {
				updateConsole("title from text field: " + formTextFields.get(i).getText());
			}
			i++;
		}
		return true;
	}
	
	
	private boolean allocateNewFields(int requestedFieldsNum) {
		if (requestedFieldsNum < 1) return false;
		int numNewFields = 0;
		
		numNewFields = requestedFieldsNum - currentFieldsNum;
		if (numNewFields < 1) return false;
		updateConsole("allocate " + numNewFields + " new field(s) for total : " + requestedFieldsNum);
		for (int i = 0; i < numNewFields;) { 						
			formFieldLabels.add(TemplateView.getFormFieldLabel(this, "empty"));
			formTextFields.add(TemplateView.getFormFieldText(this, "empty"));
			i++;
		}
		gridComp.layout(true, true);
		scrollComp.layout(true, true);
		scrollComp.setMinSize(gridComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		currentFieldsNum += numNewFields;
		
		return true;
	}
	
	private void addSelectionField(String labelName, String selection) {
		if (!Utilities.checkString(selection)) return;
		if (!Utilities.checkString(labelName)) labelName = "empty";
		updateConsole("add selection field: " + selection);
		
		// need to know if we changing default field or creating a new one
		int size = FORM_FIELDS.size();
		if (size < currentFieldsNum) {
			formFieldLabels.get(size).setText(labelName);
			formFieldLabels.get(size).setVisible(true);
			formTextFields.get(size).setText(selection);
			formTextFields.get(size).setEnabled(true);
			formTextFields.get(size).setVisible(true);
		}
		else {
			formFieldLabels.add(TemplateView.getFormFieldLabel(this, labelName));
			formTextFields.add(TemplateView.getFormFieldText(this, selection));
			scrollComp.layout(true, true);
			currentFieldsNum++;		
		}
		
		FORM_FIELDS.put(labelName, selection);
	}
	
	//TODO
	private void removeSelectionField() {
		for (String key : FORM_FIELDS.keySet()) {
			if (key.toString().equals(USER_TAG)) {
				updateConsole("found key, removing...");
				FORM_FIELDS.remove(key);
				currentFieldsNum--;
				updateFormFieldsDisplay();
			}			
		}		
	}
	
	//TODO
	private void updateSelection() {
		updateConsole("update selector");
		// get the selection text and update it
		// assume is last field... oh dear - key the key USER_TAG
		squawkBrowser.updateSelector(formTextFields.get(formTextFields.size()-1).getText());
		// then
		removeSelectionField();
	}
	
/************************************************************
* 
* 		// interface methods 
* 
************************************************************/		
	void addButtonListeners() {
		//TODO - an array	    
	    b5.addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		// re-process the current device with new vars
	    		if (preProcessFormFields())
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
	    		//squawkBrowser.dumpToBrowser(Utilities.debugDumpTableDB(squawk.getDefaultDB(), squawk.getComponentTable()));
	    		squawkBrowser.dumpToBrowser(squawkBrowser.getBrowserHtml());
	    	}
	    });
		// add site specific wrapper to device in browser
	    b10.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		//squawkBrowser.openEmailTemplate();
	    		squawkBrowser.refreshBrowser();
	    		
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
		// user selected html for editing
	    b14.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {    		
	    		// get user string from browser,
	    		// add it to a new unique form field	    		
	    		addSelectionField(new String(USER_TAG), squawkBrowser.userSelection);
	    		lockInterface();
	    	}
	    });
		// user editing lock override
	    b15.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		updateConsole("lock override called");
	    		lockInterface();
	    	}
	    });
		// user editing lock override
	    b16.addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		updateSelection();
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
			// give it some form of id
			formTextFields.get(i).setData(i);
		}
	}
	
	void addComboElements() {	
		// full template selector
		comboTemplates = new Combo(rowComp2, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener comboListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);	    		
	    		userTemplateName = combo.getText();
	    		URL templateFile = getClass().getClassLoader().getResource("templates/EMAIL/" + userWebcode + "/" + userTemplateName + ".html");
	    		
	    		if (templateFile == null) {
	    			updateConsole("Template file not found for userWebcode " + userWebcode);
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
	    
	    // component selector
		componentTemplates = new Combo(rowComp3, SWT.DROP_DOWN | SWT.BORDER);
	    SelectionListener componentListener = new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent event) {
	    		Combo combo = ((Combo) event.widget);
	    		userComponentName = combo.getText();
	    		enableAddComponent(true);
	    	}
	    };
	    componentTemplates.addSelectionListener(componentListener);
	    String[] compList = squawk.getComponentList();
	    if (compList == null) {
	    	compList = new String[1];
	    	compList[1] = "not loaded";
	    }
	    componentTemplates.setItems(compList);
	}
}
