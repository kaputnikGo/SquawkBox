import UI.SquawkView;


public class SquawkApp {

	public static void main(String[] args) {
        //interface
		SquawkView squawkView = new SquawkView();
        squawkView.run();	     
	}
	
	/*****************************/
	// project setting character 
	// encodings needs to be 
	//		UTF-8 !!!!!!!
	/*****************************/
	
	/*
	 * NOTES
	 * has a embeded db - https://bitbucket.org/xerial/sqlite-jdbc
	 * source folder in c://workspace/JAVAesque/
	 */	
	
	 //TODO
	// method to create new templates to be added to drop down
	// method to allow editing of existing templates (versioning)
	
	// need a class diagram...
	
	// ability to edit the template/component directly, bypassing any template IDs (create new IDs as well)
	//		eg. changing an image or a table colour that sits outside any template spans, changing font/sizes etc.
	//		need to guard the layout code...
	// can select text from html browser, need to be able to edit and put it back again...
	// get start+end index of selected string from html string, replace with new user edits. 
	//	need to lock the template during this so no other edits are made, index count will be out.
	
	// better way of creating/editing bullet lists, nested ones mainly (number of bullets is not changeable presently
	
	// squawkView to load view for rightPanel depending on mode (templates or components)
	
	// dynamic allocation of form fields achieved.
	// need to work out way of dealing with dupes of span ids... multiple versions of same component
	// need a component class with a name and its content?
	// component class can be responsible for its own span ids and form fields that are linked.
	
	// noob component class Basic.java - for inheritance
	// possibly need an edit/new component window to allowaccess to all component html directly
	
	// need a template_tags db table to list them and save them
	
	// insert new component within list of those already present...
	// can only be possible when all components are reduced again to smallest discrete size
	
	// may need a bridge to javascript from java for some of the more functional things needing to be done
	

}
