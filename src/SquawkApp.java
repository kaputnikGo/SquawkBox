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
	
	// method to trawl a template folder for file names.
	
	// squawkBrowser reload causes the doctype to change to something swt browser or IE sets
		
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
	 */	
}
