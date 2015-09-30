import UI.SquawkView;


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
	// method to create new templates to be added to drop down
	// method to allow editing of existing templates (versioning)
	
	// make the template editable !!
	// sort out the weirdo formatting of html to java to html
	
	/*****************************/
	// project setting character 
	// encodings needs to be UTF-8 !!!!!!!
	/*****************************/
	
	// trial jsoup for html parsing in java library
	// may need to drop swt browser all together. get jsoup to be the squawkbrowser 
	//		that displays in just a simple shell
	// or combine the form fields and the browser into a JEditorPane of the jsoup doc
	
	
	// need a class diagram...
	
	// squawkView to load view for rightPanel depending on mode (templates or components)
	
	// what happens when there are more form fields that default number - have an error message. can create more dynamically?	
	/*
	 * problematic dynamic creation of new formfields for composite. 
	 * 
	 * alts: create a very large pool, 
	 * switch "views" so fieldnum is broken up into chunks of 12 fields.  
	 * 
	 */

	// integrate component selection parsing with the new template parsing methods
	
	// need a component class with a name and its content?

	// squawkBrowser reload causes the doctype to change to something swt browser or IE sets
	// need to ensure the character encoding is added in the head.html
	
	// need a template_tags db table to list them and save them

}
