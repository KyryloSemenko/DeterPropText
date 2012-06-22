package cz.semenko.word.viewer;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.layout.grouplayout.GroupLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.grouplayout.LayoutStyle;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.StyledText;

import cz.semenko.word.viewer.listeners.InputTextKeyListener;

/**
 * SWT user interface
 * @author k
 *
 */
public class ViewerGUI {

	protected Shell shell;
	public static Logger logger = Logger.getRootLogger();
	private static ViewerGUI instance = null;
	private StyledText outputText = null;

	private ViewerGUI() {}

	/**
	 * 
	 * @return Singleton
	 */
	public static ViewerGUI getInstance() {
		if (instance == null) {
			synchronized(ViewerGUI.class) {
				ViewerGUI inst = instance;
				if (inst == null) {
					instance = new ViewerGUI();
				}
			}
		}
		return instance;
	}
	
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ViewerGUI window = ViewerGUI.getInstance();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Slovicka");
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmOpen = new MenuItem(menu_1, SWT.NONE);
		mntmOpen.setText("Open");
		
		MenuItem mntmSaveAs = new MenuItem(menu_1, SWT.NONE);
		mntmSaveAs.setText("Save As...");
		
		MenuItem mntmEdit = new MenuItem(menu, SWT.CASCADE);
		mntmEdit.setText("Edit");
		
		Menu menu_2 = new Menu(mntmEdit);
		mntmEdit.setMenu(menu_2);
		
		MenuItem mntmSelectAll = new MenuItem(menu_2, SWT.NONE);
		mntmSelectAll.setText("Select All");
		
		Group groupInputText = new Group(shell, SWT.NONE);
		groupInputText.setText("inputText");
		
		Group groupOutputText = new Group(shell, SWT.NONE);
		groupOutputText.setText("outputText");
		GroupLayout gl_shell = new GroupLayout(shell);
		gl_shell.setHorizontalGroup(
			gl_shell.createParallelGroup(GroupLayout.TRAILING)
				.add(gl_shell.createSequentialGroup()
					.addContainerGap()
					.add(gl_shell.createParallelGroup(GroupLayout.TRAILING)
						.add(GroupLayout.LEADING, groupOutputText, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
						.add(GroupLayout.LEADING, groupInputText, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_shell.setVerticalGroup(
			gl_shell.createParallelGroup(GroupLayout.LEADING)
				.add(gl_shell.createSequentialGroup()
					.add(groupInputText, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.RELATED)
					.add(groupOutputText, GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		outputText = new StyledText(groupOutputText, SWT.BORDER);
		outputText.setWordWrap(true);
		outputText.setEditable(false);
		GroupLayout gl_groupOutputText = new GroupLayout(groupOutputText);
		gl_groupOutputText.setHorizontalGroup(
			gl_groupOutputText.createParallelGroup(GroupLayout.LEADING)
				.add(gl_groupOutputText.createSequentialGroup()
					.add(outputText, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_groupOutputText.setVerticalGroup(
			gl_groupOutputText.createParallelGroup(GroupLayout.LEADING)
				.add(GroupLayout.TRAILING, gl_groupOutputText.createSequentialGroup()
					.addContainerGap()
					.add(outputText, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupOutputText.setLayout(gl_groupOutputText);
		
		StyledText inputText = new StyledText(groupInputText, SWT.BORDER);
		inputText.setWordWrap(true);
		GroupLayout gl_groupInputText = new GroupLayout(groupInputText);
		gl_groupInputText.setHorizontalGroup(
			gl_groupInputText.createParallelGroup(GroupLayout.LEADING)
				.add(gl_groupInputText.createSequentialGroup()
					.add(inputText, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_groupInputText.setVerticalGroup(
			gl_groupInputText.createParallelGroup(GroupLayout.LEADING)
				.add(GroupLayout.TRAILING, gl_groupInputText.createSequentialGroup()
					.addContainerGap()
					.add(inputText, GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupInputText.setLayout(gl_groupInputText);
		shell.setLayout(gl_shell);

		// Listeners
		inputText.addKeyListener(InputTextKeyListener.getInstance());
	}

	public StyledText getOutputText() {
		return outputText;
	}
	
	
}
