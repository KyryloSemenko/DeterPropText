package cz.semenko.word;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * Singleton. Access to program configuration.
 *
 * @author k
 * @version $Id: $Id
 */
public class Config {
	private String application_name = null;
	private String dbCon_dbURL = null;
	private String dbCon_derbyJarServerStart = null;
	private String dbCon_derbyJarServerStop = null;
	private int dataProvider_numCharsReadsFromInput = 0;
	private int fastMemory_tablesTableSize = 0;
	private int fastMemory_tablesCellsSize = 0;
	private int fastMemory_tablesAssociationsSize = 0;
	private int knowledge_cellsCreationDepth = 0;
	private int knowledge_knowledgeSize = 0;
	private boolean knowledge_decideToRelateByObjectTypeOrAssocCost = false;
	private boolean knowledge_decideToRelateCellsByHigherCellType = false;
	private boolean knowledge_decideToRelateCellsByHigherAssocCost = false;
	private boolean cellsCreationDecider_createNewCellsToAllPairs = false;
	private int cellsCreationDecider_createNewCellsToAllPairsDepth = 0;
	private boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory = false;
	private boolean fastMemory_searchToAssociationsAtAllElements = false;
	private boolean knowledge_relateOnlyCellsOfSameTypes = false;
	private boolean knowledge_saveThoughtsToFile = false;
	private String thoughtsSaver_filePathToSaveThoughts;
	private int memoryCleaner_lowestCostForLeaving;
	
	private static XMLConfiguration conf;
	/** Constant <code>logger</code> */
	public static Logger logger = Logger.getRootLogger();
	
	/**
	 * <p>Constructor for Config.</p>
	 */
	public Config() {
		try {
			conf = new XMLConfiguration("config.xml");
			// Naplnit privatni promenne
			application_name = conf.getString("application.name");
			dbCon_dbURL = conf.getString("dbCon.dbURL");
			dbCon_derbyJarServerStart = conf.getString("dbCon.derbyJarServerStart");
			dbCon_derbyJarServerStop = conf.getString("dbCon.derbyJarServerStop");
			dataProvider_numCharsReadsFromInput = conf.getInt("dataProvider.numCharsReadsFromInput");
			fastMemory_tablesTableSize = conf.getInt("fastMemory.tablesTableSize");
			fastMemory_tablesCellsSize = conf.getInt("fastMemory.tablesCellsSize");
			if(dataProvider_numCharsReadsFromInput > fastMemory_tablesCellsSize) {
				String msg = "Configuration parameter dataProvider.numCharsReadsFromInput must not be greater then fastMemory.tablesCellsSize";
				System.out.println(msg);
				throw new ConfigurationException(msg);
			}
			setFastMemory_tablesAssociationsSize(conf.getInt("fastMemory.tablesAssociationsSize"));
			setKnowledge_cellsCreationDepth(conf.getInt("knowledge.cellsCreationDepth"));
			setKnowledge_knowledgeSize(conf.getInt("knowledge.knowledgeSize"));
			setKnowledge_decideToRelateByObjectTypeOrAssocCost(conf.getBoolean("knowledge.decideToRelateByObjectTypeOrAssocCost"));
			setKnowledge_decideToRelateCellsByHigherAssocCost(conf.getBoolean("knowledge.decideToRelateCellsByHigherAssocCost"));
			setKnowledge_decideToRelateCellsByHigherObjectType(conf.getBoolean("knowledge.decideToRelateCellsByHigherObjectType"));
			setKnowledge_relateOnlyCellsOfSameTypes(conf.getBoolean("knowledge.relateOnlyCellsOfSameTypes"));
			setCellsCreationDecider_createNewCellsToAllPairs(conf.getBoolean("cellsCreationDecider.createNewCellsToAllPairs"));
			setCellsCreationDecider_createNewCellsToAllPairsDepth(conf.getInt("cellsCreationDecider.createNewCellsToAllPairsDepth"));
			setFastMemory_alwaysSearchToAssociationsDeepInTheMemory(conf.getBoolean("fastMemory.alwaysSearchToAssociationsDeepInTheMemory"));
			setFastMemory_searchToAssociationsAtAllElements(conf.getBoolean("fastMemory.searchToAssociationsAtAllElements"));
			setKnowledge_saveThoughtsToFile(conf.getBoolean("knowledge.saveThoughtsToFile"));
			setThoughtsSaver_filePathToSaveThoughts(conf.getString("thoughtsSaver.filePathToSaveThoughts"));
			setMemoryCleaner_lowestCostForLeaving(conf.getInt("memoryCleaner.lowestCostForLeaving"));
		} catch (ConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/** Application business name */
	public String getApplication_name() {
		return application_name;
	}

	/**
	 * <p>Getter for the field <code>dataProvider_numCharsReadsFromInput</code>.</p>
	 *
	 * @return a int.
	 */
	public int getDataProvider_numCharsReadsFromInput() {
		return dataProvider_numCharsReadsFromInput;
	}

	/**
	 * <p>Setter for the field <code>dataProvider_numCharsReadsFromInput</code>.</p>
	 *
	 * @param dataProviderNumCharsReadsFromInput a int.
	 */
	public void setDataProvider_numCharsReadsFromInput(
			int dataProviderNumCharsReadsFromInput) {
		dataProvider_numCharsReadsFromInput = dataProviderNumCharsReadsFromInput;
	}

	/**
	 * <p>Getter for the field <code>fastMemory_tablesTableSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesTableSize() {
		return fastMemory_tablesTableSize;
	}

	/**
	 * <p>Setter for the field <code>fastMemory_tablesTableSize</code>.</p>
	 *
	 * @param fastMemoryTablesTableSize a int.
	 */
	public void setFastMemory_tablesTableSize(int fastMemoryTablesTableSize) {
		fastMemory_tablesTableSize = fastMemoryTablesTableSize;
	}

	/**
	 * <p>Getter for the field <code>fastMemory_tablesCellsSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesCellsSize() {
		return fastMemory_tablesCellsSize;
	}

	/**
	 * <p>Setter for the field <code>fastMemory_tablesCellsSize</code>.</p>
	 *
	 * @param fastMemoryTablesCellsSize a int.
	 */
	public void setFastMemory_tablesCellsSize(
			int fastMemoryTablesCellsSize) {
		fastMemory_tablesCellsSize = fastMemoryTablesCellsSize;
	}

	/**
	 * <p>Getter for the field <code>dbCon_dbURL</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDbCon_dbURL() {
		return dbCon_dbURL;
	}

	/**
	 * <p>Setter for the field <code>dbCon_dbURL</code>.</p>
	 *
	 * @param dbConDbURL a {@link java.lang.String} object.
	 */
	public void setDbCon_dbURL(String dbConDbURL) {
		dbCon_dbURL = dbConDbURL;
	}

	/**
	 * <p>Getter for the field <code>dbCon_derbyJarServerStart</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDbCon_derbyJarServerStart() {
		return dbCon_derbyJarServerStart;
	}

	/**
	 * <p>Setter for the field <code>dbCon_derbyJarServerStart</code>.</p>
	 *
	 * @param dbConDerbyJarServerStart a {@link java.lang.String} object.
	 */
	public void setDbCon_derbyJarServerStart(String dbConDerbyJarServerStart) {
		dbCon_derbyJarServerStart = dbConDerbyJarServerStart;
	}

	/**
	 * <p>Getter for the field <code>dbCon_derbyJarServerStop</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDbCon_derbyJarServerStop() {
		return dbCon_derbyJarServerStop;
	}

	/**
	 * <p>Setter for the field <code>dbCon_derbyJarServerStop</code>.</p>
	 *
	 * @param dbConDerbyJarServerStop a {@link java.lang.String} object.
	 */
	public void setDbCon_derbyJarServerStop(String dbConDerbyJarServerStop) {
		dbCon_derbyJarServerStop = dbConDerbyJarServerStop;
	}

	/**
	 * <p>Setter for the field <code>fastMemory_tablesAssociationsSize</code>.</p>
	 *
	 * @param fastMemory_tablesAssociationsSize a int.
	 */
	public void setFastMemory_tablesAssociationsSize(
			int fastMemory_tablesAssociationsSize) {
		this.fastMemory_tablesAssociationsSize = fastMemory_tablesAssociationsSize;
	}

	/**
	 * <p>Getter for the field <code>fastMemory_tablesAssociationsSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesAssociationsSize() {
		return fastMemory_tablesAssociationsSize;
	}

	/**
	 * <p>Getter for the field <code>knowledge_cellsCreationDepth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getKnowledge_cellsCreationDepth() {
		return knowledge_cellsCreationDepth;
	}

	/**
	 * <p>Setter for the field <code>knowledge_cellsCreationDepth</code>.</p>
	 *
	 * @param knowledgeCellsCreationDepth a int.
	 */
	public void setKnowledge_cellsCreationDepth(int knowledgeCellsCreationDepth) {
		knowledge_cellsCreationDepth = knowledgeCellsCreationDepth;
	}

	/**
	 * <p>Getter for the field <code>knowledge_knowledgeSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getKnowledge_knowledgeSize() {
		return knowledge_knowledgeSize;
	}

	/**
	 * <p>Setter for the field <code>knowledge_knowledgeSize</code>.</p>
	 *
	 * @param knowledgeKnowledgeSize a int.
	 */
	public void setKnowledge_knowledgeSize(int knowledgeKnowledgeSize) {
		knowledge_knowledgeSize = knowledgeKnowledgeSize;
	}

	/**
	 * <p>isKnowledge_decideToRelateByObjectTypeOrAssocCost.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateByObjectTypeOrAssocCost() {
		return knowledge_decideToRelateByObjectTypeOrAssocCost;
	}

	/**
	 * <p>Setter for the field <code>knowledge_decideToRelateByObjectTypeOrAssocCost</code>.</p>
	 *
	 * @param knowledge_decideToRelateByObjectTypeOrAssocCost a boolean.
	 */
	public void setKnowledge_decideToRelateByObjectTypeOrAssocCost(
			boolean knowledge_decideToRelateByObjectTypeOrAssocCost) {
		this.knowledge_decideToRelateByObjectTypeOrAssocCost = knowledge_decideToRelateByObjectTypeOrAssocCost;
	}

	/**
	 * <p>isKnowledge_decideToRelateCellsByHigherObjectType.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateCellsByHigherObjectType() {
		return knowledge_decideToRelateCellsByHigherCellType;
	}

	/**
	 * <p>Setter for the field <code>knowledge_decideToRelateCellsByHigherCellType</code>.</p>
	 *
	 * @param knowledge_decideToRelateCellsByHigherCellType a boolean.
	 */
	public void setKnowledge_decideToRelateCellsByHigherObjectType(
			boolean knowledge_decideToRelateCellsByHigherObjectType) {
		this.knowledge_decideToRelateCellsByHigherCellType = knowledge_decideToRelateCellsByHigherObjectType;
	}

	/**
	 * <p>isKnowledge_decideToRelateCellsByHigherAssocCost.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateCellsByHigherAssocCost() {
		return knowledge_decideToRelateCellsByHigherAssocCost;
	}

	/**
	 * <p>Setter for the field <code>knowledge_decideToRelateCellsByHigherAssocCost</code>.</p>
	 *
	 * @param knowledge_decideToRelateCellsByHigherAssocCost a boolean.
	 */
	public void setKnowledge_decideToRelateCellsByHigherAssocCost(
			boolean knowledge_decideToRelateCellsByHigherAssocCost) {
		this.knowledge_decideToRelateCellsByHigherAssocCost = knowledge_decideToRelateCellsByHigherAssocCost;
	}

	/**
	 * <p>isCellsCreationDecider_createNewCellsToAllPairs.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isCellsCreationDecider_createNewCellsToAllPairs() {
		return cellsCreationDecider_createNewCellsToAllPairs;
	}

	/**
	 * <p>Setter for the field <code>cellsCreationDecider_createNewCellsToAllPairs</code>.</p>
	 *
	 * @param cellsCreationDecider_createNewCellsToAllPairs a boolean.
	 */
	public void setCellsCreationDecider_createNewCellsToAllPairs(
			boolean cellsCreationDecider_createNewCellsToAllPairs) {
		this.cellsCreationDecider_createNewCellsToAllPairs = cellsCreationDecider_createNewCellsToAllPairs;
	}

	/**
	 * <p>Setter for the field <code>cellsCreationDecider_createNewCellsToAllPairsDepth</code>.</p>
	 *
	 * @param cellsCreationDecider_createNewCellsToAllPairsDepth a int.
	 */
	public void setCellsCreationDecider_createNewCellsToAllPairsDepth(
			int cellsCreationDecider_createNewCellsToAllPairsDepth) {
		this.cellsCreationDecider_createNewCellsToAllPairsDepth = cellsCreationDecider_createNewCellsToAllPairsDepth;
	}

	/**
	 * <p>Getter for the field <code>cellsCreationDecider_createNewCellsToAllPairsDepth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getCellsCreationDecider_createNewCellsToAllPairsDepth() {
		return cellsCreationDecider_createNewCellsToAllPairsDepth;
	}

	/**
	 * <p>isFastMemory_alwaysSearchToAssociationsDeepInTheMemory.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isFastMemory_alwaysSearchToAssociationsDeepInTheMemory() {
		return fastMemory_alwaysSearchToAssociationsDeepInTheMemory;
	}
	
	/**
	 * <p>Setter for the field <code>fastMemory_alwaysSearchToAssociationsDeepInTheMemory</code>.</p>
	 *
	 * @param fastMemory_alwaysSearchToAssociationsDeepInTheMemory a boolean.
	 */
	public void setFastMemory_alwaysSearchToAssociationsDeepInTheMemory(
			boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory) {
		this.fastMemory_alwaysSearchToAssociationsDeepInTheMemory = fastMemory_alwaysSearchToAssociationsDeepInTheMemory;
	}

	/**
	 * <p>isFastMemory_searchToAssociationsAtAllElements.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isFastMemory_searchToAssociationsAtAllElements() {
		return fastMemory_searchToAssociationsAtAllElements;
	}

	/**
	 * <p>Setter for the field <code>fastMemory_searchToAssociationsAtAllElements</code>.</p>
	 *
	 * @param fastMemory_searchToAssociationsAtAllElements a boolean.
	 */
	public void setFastMemory_searchToAssociationsAtAllElements(
			boolean fastMemory_searchToAssociationsAtAllElements) {
		this.fastMemory_searchToAssociationsAtAllElements = fastMemory_searchToAssociationsAtAllElements;
	}

	/**
	 * <p>isKnowledge_relateOnlyCellsOfSameTypes.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_relateOnlyCellsOfSameTypes() {
		return knowledge_relateOnlyCellsOfSameTypes;
	}
	
	/**
	 * <p>Setter for the field <code>knowledge_relateOnlyCellsOfSameTypes</code>.</p>
	 *
	 * @param knowledge_relateOnlyCellsOfSameTypes a boolean.
	 * @throws org.apache.commons.configuration.ConfigurationException if any.
	 */
	public void setKnowledge_relateOnlyCellsOfSameTypes (
			boolean knowledge_relateOnlyCellsOfSameTypes) throws ConfigurationException {
		if (isKnowledge_decideToRelateByObjectTypeOrAssocCost() == true & knowledge_relateOnlyCellsOfSameTypes == true) {
			throw new ConfigurationException(
					"Nemuze byt soucasne knowledge.decideToRelateByObjectTypeOrAssocCost() == " +
					"true & knowledge.relateOnlyCellsOfSameTypes == true"
			);
		}
		this.knowledge_relateOnlyCellsOfSameTypes = knowledge_relateOnlyCellsOfSameTypes;
	}

	/**
	 * <p>isKnowledge_saveThoughtsToFile.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_saveThoughtsToFile() {
		return knowledge_saveThoughtsToFile;
	}
	/**
	 * <p>Setter for the field <code>knowledge_saveThoughtsToFile</code>.</p>
	 *
	 * @param knowledge_saveThoughtsToFile a boolean.
	 */
	public void setKnowledge_saveThoughtsToFile(boolean knowledge_saveThoughtsToFile) {
		this.knowledge_saveThoughtsToFile  = knowledge_saveThoughtsToFile;
	}

	/**
	 * <p>Getter for the field <code>thoughtsSaver_filePathToSaveThoughts</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getThoughtsSaver_filePathToSaveThoughts() {
		return thoughtsSaver_filePathToSaveThoughts;
	}
	/**
	 * <p>Setter for the field <code>thoughtsSaver_filePathToSaveThoughts</code>.</p>
	 *
	 * @param param a {@link java.lang.String} object.
	 */
	public void setThoughtsSaver_filePathToSaveThoughts(String param) {
		this.thoughtsSaver_filePathToSaveThoughts = param.trim();
	}

	/**
	 * <p>Getter for the field <code>memoryCleaner_lowestCostForLeaving</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMemoryCleaner_lowestCostForLeaving() {
		return memoryCleaner_lowestCostForLeaving;
	}
	/**
	 * <p>Setter for the field <code>memoryCleaner_lowestCostForLeaving</code>.</p>
	 *
	 * @param memoryCleaner_lowestCostForLeaving a int.
	 */
	public void setMemoryCleaner_lowestCostForLeaving(int memoryCleaner_lowestCostForLeaving) {
		this.memoryCleaner_lowestCostForLeaving = memoryCleaner_lowestCostForLeaving;
	}
}
