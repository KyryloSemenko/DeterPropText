package cz.semenko.word.persistent;

import java.util.Map;
import java.util.TreeMap;

import cz.semenko.word.Config;
import cz.semenko.word.technology.memory.slowly.SlowlyMemory;

/** Tables table **/
public class Tables {
	private Long id;
	private String connString;
	
	public Tables() {
	}

	public Tables(Long id, String connString) {
		super();
		this.id = id;
		this.connString = connString;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#getId()
	 */
	public Long getId() {
		return id;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#setId(java.lang.Long)
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#getConnString()
	 */
	public String getConnString() {
		return connString;
	}

	/* (non-Javadoc)
	 * @see cz.semenko.word.persistent.Inter#setConnString(java.lang.String)
	 */
	public void setConnString(String connString) {
		this.connString = connString;
	}

}

