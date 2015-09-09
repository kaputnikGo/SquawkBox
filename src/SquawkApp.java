
public class SquawkApp {

	public static void main(String[] args) {
        //interface
		SquawkView squawkView = new SquawkView();
        squawkView.run();	     
	}
	
	/*
	 * NOTES
	 * has a embeded db - https://bitbucket.org/xerial/sqlite-jdbc
	 * source folder in c://workspace/JAVAesque/
	 */	
	
	 //TODO
	
		// need to change from openx devices to email template - destroy
	// allow common templates to be included in a drop down
	// method to create new templates to be added to drop down
	// method to allow editing of existing templates (versioning)
	
	// div id == someName to be replaced with span id = someName for user editable content
	// build simple version first, then add component drop-in later
	
	// allow a new webcode to be created that can look like MM-ENDO, or MM-PROMO etc.
	// could have the button select the main owner site, then the combo drop down loads the template files for it.
	
	// email version - list components, drag and drop with text edit
	// divide template into discrete components
	// create list of them
	// allow select and place of components in new template file
	
	
		// start up window in browser to display a resource file. - DONE
		// reopen browser if user closes it. - DONE
		// add button to add site wrapper to device for viewing pleasure.
		// allow a given template file to load into browser and get parsed into the squawkBox view form fields
		
		// get sqltalk into several diff classes.
	
	
		// enable a listing of devices saved to the DB for selection, display, edit, save, export, etc
		//		list in the browser, provide link to goto/load that sql record.
		
		// enum the sites so that new ones can be added
		// allow for a change in style sheets? currently not really using them, need the site wrapper enabled.
		// local open only trawls a dir getting first html file found - is set to compile a list for selection.
		// external links will invoke local machine browser (SWT.NONE == InternetExplorer)
}
