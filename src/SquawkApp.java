
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
