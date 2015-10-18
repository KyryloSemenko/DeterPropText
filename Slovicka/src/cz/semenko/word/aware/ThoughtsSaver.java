package cz.semenko.word.aware;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import cz.semenko.word.Config;

public class ThoughtsSaver {
	/**
	 * Separator of {@link Thought}s in file
	 */
	private static final String THOUGHT_SEPARATOR = ";";
	private BufferedWriter bw = null;
	
	public ThoughtsSaver(Config config) throws IOException {
		String filePath = config.getThoughtsSaver_filePathToSaveThoughts();
		File fileToSaveThoughts = new File(filePath);
		if (!fileToSaveThoughts.exists()) {
			File folder = new File(fileToSaveThoughts.getParent());
			if (!folder.exists()) {
				folder.mkdirs();
			}
			fileToSaveThoughts.createNewFile();
		}
		bw = new BufferedWriter(new FileWriter(fileToSaveThoughts));
	}

	@Override
	protected void finalize() throws Throwable {
		try {
			if (bw != null) {
				bw.flush();
				bw.close();
			}
	    } finally {
	        super.finalize();
	    }		
	}

	/**
	 * Prida idecko objektu, do souboru. Jako oddelovac pouzije ';'
	 * @param id
	 * @throws IOException 
	 */
	public void saveCellId(Long id) throws IOException {
		bw.append(id + THOUGHT_SEPARATOR);		
	}

	/**
	 * Prida idecka objektu vektoru Thoughts do souboru
	 * @param thoughts
	 * @throws IOException
	 */
	public void saveThoughts(Vector<Thought> thoughts) throws IOException {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < thoughts.size(); i++) {
			buff.append(thoughts.get(i).getActiveCell().getId() + THOUGHT_SEPARATOR);
		}
		bw.append(buff);
		bw.flush();
	}

}
