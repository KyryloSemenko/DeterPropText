package cz.semenko.word.viewer.listeners;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;

import cz.semenko.word.viewer.Model;
import cz.semenko.word.viewer.ViewerGUI;


public class InputTextKeyListener extends KeyAdapter {
	private static InputTextKeyListener instance = null;
	public static Logger logger = Logger.getRootLogger();
	
	private InputTextKeyListener() {}

	public static InputTextKeyListener getInstance() {
		if (instance == null) {
			synchronized(InputTextKeyListener.class) {
				InputTextKeyListener inst = instance;
				if (inst == null) {
					instance = new InputTextKeyListener();
				}
			}
		}
		return instance;
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
			StyledText outputText = ViewerGUI.getInstance().getOutputText();
			try {
				String text = Model.getInstance().getSourceToObjectsId(inputText);
				outputText.append(text + System.getProperty("line.separator"));
				outputText.setTopIndex(outputText.getLineCount());
			} catch (Exception ex) {
				logger.error(ex.getMessage(), ex);
				ex.printStackTrace();
			}
		}
	}
}
