package cz.semenko.word.viewer.listeners;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import cz.semenko.word.viewer.Model;
import cz.semenko.word.viewer.ViewerGUI;


public class InputTextKeyListener extends KeyAdapter {
	public static Logger logger = Logger.getRootLogger();
	// Model dodava Spring FW
	private Model model;
	// viewerGUI dodava Spring FW
	private ViewerGUI viewerGUI;
	
	/**
	 * @param model the model to set
	 */
	public void setModel(Model model) {
		this.model = model;
	}

	public InputTextKeyListener() {}
	
	/**
	 * @param viewerGUI the viewerGUI to set
	 */
	public void setViewerGUI(ViewerGUI viewerGUI) {
		this.viewerGUI = viewerGUI;
	}

	/**
	 * Spusti getSourceToObjectId
	 * TODO dodelat dalsi moznosti, napriklad getTargetObjects...
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		// CTRL+A
		if (e.character == 0x01 & e.keyCode == 97) {
			StyledText text = (StyledText)(e.getSource());
			text.selectAll();
		}
		// ENTER
		if (e.character == '\r' || e.character == '\n') {
			StyledText inputText = (StyledText)e.getSource();
			StyledText outputText = viewerGUI.getOutputText();
			try {
				String text = model.getSourceToObjectsId(inputText);
				outputText.append(text + System.getProperty("line.separator"));
				outputText.setTopIndex(outputText.getLineCount());
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				ex.printStackTrace();
			}
		}
	}
}
