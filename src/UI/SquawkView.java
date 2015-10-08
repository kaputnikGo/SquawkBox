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
	private String appName = "SQUAWK BOX - prototype";
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
	
	// UI collections
	protected List<Text> formTextFields;
	protected List<Label> formFieldLabels;
	protected Map<String, String> FORM_FIELDS;
	protected List<Button> buttonList;

	protected Text debugText;

	protected Combo comboTemplates;
	protected Combo componentTemplates;
	protected Composite rowComp2;
	protected Composite rowComp3;
	protected Composite rightPanel;
	protected StackLayout stackLayout;	
	
	public SquawkView() {	
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
		squawk.installDB();				
		updateConsole("Database checks complete.");

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
	}
	
	public void updateFormFieldsDisplay() {
		int fieldNum = FORM_FIELDS.size();
		if (fieldNum > currentFieldsNum) {			
			if (allocateNewFields(fieldNum)) {
				// nothing
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
	
	public void updateSingleFormField(String tag, String content) {
		updateConsole("Updating formField at tag: " + tag);
		for (String key : FORM_FIELDS.keySet()) {
			if (key.toString().equals(tag)) {
				// update its content with the changed
				FORM_FIELDS.put(key, content);
				updateFormFieldsDisplay();				
			}		
		}
	}
	
	public void clearFormFields() {
		for (int i = 0; i < formTextFields.size(); i++) {
			formTextFields.get(i).setText("");
			formTextFields.get(i).setEnabled(true);
		}
	}
	
	public void enableAddComponent(boolean enable) {
		if (enable) 
			buttonList.get(19).setEnabled(true);
		else 
			buttonList.get(19).setEnabled(false);
	}
	
	public void lockInterface() {
		// has an array now, yo.
		// allow only one selection, is a live edit, commit then its gone
		// lock everything except the selection Text field and updateSelector buttonList.get(9) -(b16)
		// leave override lock enabled always
		
// TODO
// add call to lock/unlock FORM_FIELDS except selection
		for (int i = 0; i < buttonList.size(); i++) {
			if (squawkBrowser.isLocked()) {
				// disable all controls except the update selector
				buttonList.get(i).setEnabled(false);
				if (i == 8) {
					buttonList.get(8).setText("UNLOCK");
					buttonList.get(8).setEnabled(true);
				}
				if (i == 9) {
					buttonList.get(9).setEnabled(true);
				}
				if (i == 10) {
					buttonList.get(10).setEnabled(true);
				}
			}
			else {
				// enable all controls except the update selector
				buttonList.get(i).setEnabled(true);
				if (i == 8) {
					buttonList.get(8).setText("LOCK");					
				}
				if (i == 9) {
					buttonList.get(9).setEnabled(false);
				}
				if (i == 10) {
					buttonList.get(10).setEnabled(false);
				}
			}
			// always
			
		}
		squawkBrowser.setLock(!squawkBrowser.isLocked());
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
			updateConsole("Initialise browser.");
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
			updateConsole("Browser is not null.");
		}
	}
	
	void userButtonPressed(Event event) {
		// find out which button was pressed
		Button button = ((Button) event.widget);
		userWebcode = button.getText();
		comboTemplates.setItems(squawk.getTemplateList(userWebcode));
		updateConsole("Template webcode selected: " + userWebcode);
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
		int i = 0;
		for (String key : FORM_FIELDS.keySet()) {
			FORM_FIELDS.put(key, formTextFields.get(i).getText());
			i++;
		}
		return true;
	}
	
	
	private boolean allocateNewFields(int requestedFieldsNum) {
		if (requestedFieldsNum < 1) return false;
		int numNewFields = 0;
		
		numNewFields = requestedFieldsNum - currentFieldsNum;
		if (numNewFields < 1) return false;
		for (int i = 0; i < numNewFields;) { 						
			formFieldLabels.add(TemplateView.getFormFieldLabel(this, "empty", false));
			formTextFields.add(TemplateView.getFormFieldText(this, "empty", false));
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
		
		int size = FORM_FIELDS.size();
		if (size < currentFieldsNum) {
			formFieldLabels.get(size).setText(labelName);
			formFieldLabels.get(size).setVisible(true);
			formTextFields.get(size).setText(selection);
			formTextFields.get(size).setEnabled(true);
			formTextFields.get(size).setVisible(true);
		}		
		else {
			formFieldLabels.add(TemplateView.getFormFieldLabel(this, labelName, true));
			formTextFields.add(TemplateView.getFormFieldText(this, selection, true));
			scrollComp.layout(true, true);
			currentFieldsNum++;		
		}		
		FORM_FIELDS.put(labelName, selection);
	}
	
	
	private void removeSelectionField() {
		for (String key : FORM_FIELDS.keySet()) {
			if (key.toString().equals(USER_TAG)) {
				FORM_FIELDS.remove(key);
				//currentFieldsNum--;
				//updateFormFieldsDisplay();
			}			
		}		
	}
	
	private void updateSelection() {
		// get the selection text and update it
		// assume is last field... oh dear - key the key USER_TAG?
		squawkBrowser.updateSelector(formTextFields.get(formTextFields.size()-1).getText());
		// then
		lockInterface();
		removeSelectionField();
	}
	
	private void cancelSelection() {
		// stop it all, now	
		
		// then
		lockInterface();
		removeSelectionField();
	}
	
/************************************************************
* 
* 		// interface methods 
* 
************************************************************/		
	void addButtonListeners() {
		// array ordering is flunky here...
		// init state for the buttons as well.
		// dump to squawkBrowser button
		buttonList.get(0).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		//squawkBrowser.dumpToBrowser(Utilities.debugDumpTableDB(squawk.getDefaultDB(), squawk.getComponentTable()));
	    		squawkBrowser.dumpToBrowser(squawkBrowser.getBrowserHtml());
	    	}
	    });
		// add site specific wrapper to device in browser
		buttonList.get(1).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		//squawkBrowser.openEmailTemplate();
	    		squawkBrowser.refreshBrowser();
	    		
	    	}
	    });
		buttonList.get(2).addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		openBrowser();
	    	}
	    });
		// open file button
		buttonList.get(3).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.openLocalDirFile();
	    	}
	    });	
		// re-process the current device with new vars
		buttonList.get(4).addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {
	    		if (preProcessFormFields())
	    			squawkBrowser.processBrowser();
	    	}
	    });
		// export the finished device as a text file
		buttonList.get(5).addListener(SWT.Selection,  new Listener() {
	    	public void handleEvent(Event event) {	    		
	    		// send default save name string to browser
	    		squawkBrowser.exportBrowser("test_save");
	    	}
	    });
	    	    
		// toggle grid lines of blue
		buttonList.get(6).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		squawkBrowser.toggleGrid();
	    	}
	    });
		// user selected html for editing
		buttonList.get(7).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {    		
	    		// get user string from browser,
	    		// add it to a new unique form field	
	    		lockInterface();
	    		addSelectionField(new String(USER_TAG), squawkBrowser.userSelection);
	    	}
	    });
		// user editing lock override
		buttonList.get(8).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		updateConsole("Lock override called");
	    		lockInterface();
	    	}
	    });
		// update user selection text
		buttonList.get(9).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		updateSelection();
	    		if (preProcessFormFields())
	    			squawkBrowser.processBrowser();
	    	}
	    });
		// cancel user selection text
		buttonList.get(10).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {
	    		cancelSelection();
	    		if (preProcessFormFields())
	    			squawkBrowser.processBrowser();
	    	}
	    });
		buttonList.get(10).setEnabled(false);
		// webcodes button listeners added in ControlView

		// clear browser browser to allow creating component template	
		buttonList.get(18).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {    		
	    		squawkBrowser.startComponents();
	    		//viewInflate.switchViewType(VIEW_COMPONENT);
	    	}
	    });
		// add selected component to browser
		buttonList.get(19).addListener(SWT.Selection, new Listener() {
	    	public void handleEvent(Event e) {    		
	    		squawkBrowser.addComponentToPage(userComponentName);
	    		updateFormFieldsDisplay();
	    	}
	    });
		buttonList.get(19).setEnabled(false);
	}
		
	private void addTextListeners() {
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
	
	private void addComboElements() {	
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
