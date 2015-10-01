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
	
	// squawkView to load view for rightPanel depending on mode (templates or components)
	
	// what happens when there are more form fields that default number - have an error message. can create more dynamically?
	// have increased default number to 24, added scrolling composite - but still need to figure on maximum number
	//		and dynamic allocation of new resources, 
	//		ie. create a very large pool,	switch "views" of chunks of 12 fields

	// need a component class with a name and its content?
	
	// need a template_tags db table to list them and save them
	
	// insert new component within list of those already present...

}
