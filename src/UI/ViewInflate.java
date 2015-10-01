package UI;

public class ViewInflate {
	private SquawkView squawkView;
		
	public void initView(final SquawkView squawkView, int viewType) {
		this.squawkView = squawkView;
		
		ControlView.loadView(squawkView);		
		TemplateView.loadView(squawkView);
		//switchViewType(viewType);
	}
	
	public void switchViewType(int newViewType) {
		// need to dispose of existing panel?
		squawkView.updateConsole("viewType switch to: " + newViewType);
		
		if (newViewType == SquawkView.VIEW_TEMPLATE) {
			//stackLayout.topControl = gridComp;
		}
		else {
			//stackLayout.topControl = gridComp2;
		}
        //rightPanel.layout();
	}
}