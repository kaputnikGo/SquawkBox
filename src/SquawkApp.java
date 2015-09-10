
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
	
	// allow common templates to be included in a drop down
	// method to create new templates to be added to drop down
	// method to allow editing of existing templates (versioning)
	
	// div id == someName to be replaced with span id = someName for user editable content
	// build simple version first, then add component drop-in later
		
	// email version - list components, drag and drop with text edit
	// divide template into discrete components
	// create list of them
	// allow select and place of components in new template file
	
	// implement iterating over a template looking for id="templateParaN" where N = int(1 to n)
	
	/*
	 * need a template_tags db table to list them and save them
	 * 
	 * change name of bannerLogo to templateLogo - eediot!
	 * 
	 * new template div id:
	 * 	<span id="templateDate">
	 * 	<span id="templateHeading">
	 * 	<img id="templateAuthorAvatar"...>
	 * 	<span id="templateIntro">
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
		// start up window in browser to display a resource file. - DONE
		// reopen browser if user closes it. - DONE
		// add button to add site wrapper to device for viewing pleasure.
		// allow a given template file to load into browser and get parsed into the squawkBox view form fields
		
		// get sqltalk into several diff classes.
	
}
