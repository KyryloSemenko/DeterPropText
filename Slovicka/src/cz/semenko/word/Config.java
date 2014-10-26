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
	private int fastMemory_tablesObjectsSize = 0;
	private int fastMemory_tablesAssociationsSize = 0;
	private int knowledge_objectsCreationDepth = 0;
	private int knowledge_knowledgeSize = 0;
	private boolean knowledge_decideToRelateByObjectTypeOrAssocCost = false;
	private boolean knowledge_decideToRelateObjectsByHigherObjectType = false;
	private boolean knowledge_decideToRelateObjectsByHigherAssocCost = false;
	private boolean objectsCreationDecider_createNewObjectsToAllPairs = false;
	private int objectsCreationDecider_createNewObjectsToAllPairsDepth = 0;
	private boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory = false;
	private boolean fastMemory_searchToAssociationsAtAllElements = false;
	private boolean knowledge_relateOnlyObjectsOfSameTypes = false;
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
			fastMemory_tablesObjectsSize = conf.getInt("fastMemory.tablesObjectsSize");
			if(dataProvider_numCharsReadsFromInput > fastMemory_tablesObjectsSize) {
				String msg = "Configuration parameter dataProvider.numCharsReadsFromInput must not be greater then fastMemory.tablesObjectsSize";
				System.out.println(msg);
				throw new ConfigurationException(msg);
			}
			setFastMemory_tablesAssociationsSize(conf.getInt("fastMemory.tablesAssociationsSize"));
			setKnowledge_objectsCreationDepth(conf.getInt("knowledge.objectsCreationDepth"));
			setKnowledge_knowledgeSize(conf.getInt("knowledge.knowledgeSize"));
			setKnowledge_decideToRelateByObjectTypeOrAssocCost(conf.getBoolean("knowledge.decideToRelateByObjectTypeOrAssocCost"));
			setKnowledge_decideToRelateObjectsByHigherAssocCost(conf.getBoolean("knowledge.decideToRelateObjectsByHigherAssocCost"));
			setKnowledge_decideToRelateObjectsByHigherObjectType(conf.getBoolean("knowledge.decideToRelateObjectsByHigherObjectType"));
			setKnowledge_relateOnlyObjectsOfSameTypes(conf.getBoolean("knowledge.relateOnlyObjectsOfSameTypes"));
			setObjectsCreationDecider_createNewObjectsToAllPairs(conf.getBoolean("objectsCreationDecider.createNewObjectsToAllPairs"));
			setObjectsCreationDecider_createNewObjectsToAllPairsDepth(conf.getInt("objectsCreationDecider.createNewObjectsToAllPairsDepth"));
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
	 * <p>Getter for the field <code>fastMemory_tablesObjectsSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesObjectsSize() {
		return fastMemory_tablesObjectsSize;
	}

	/**
	 * <p>Setter for the field <code>fastMemory_tablesObjectsSize</code>.</p>
	 *
	 * @param fastMemoryTablesObjectsSize a int.
	 */
	public void setFastMemory_tablesObjectsSize(
			int fastMemoryTablesObjectsSize) {
		fastMemory_tablesObjectsSize = fastMemoryTablesObjectsSize;
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
	 * <p>Getter for the field <code>knowledge_objectsCreationDepth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getKnowledge_objectsCreationDepth() {
		return knowledge_objectsCreationDepth;
	}

	/**
	 * <p>Setter for the field <code>knowledge_objectsCreationDepth</code>.</p>
	 *
	 * @param knowledgeObjectsCreationDepth a int.
	 */
	public void setKnowledge_objectsCreationDepth(int knowledgeObjectsCreationDepth) {
		knowledge_objectsCreationDepth = knowledgeObjectsCreationDepth;
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
	 * <p>isKnowledge_decideToRelateObjectsByHigherObjectType.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateObjectsByHigherObjectType() {
		return knowledge_decideToRelateObjectsByHigherObjectType;
	}

	/**
	 * <p>Setter for the field <code>knowledge_decideToRelateObjectsByHigherObjectType</code>.</p>
	 *
	 * @param knowledge_decideToRelateObjectsByHigherObjectType a boolean.
	 */
	public void setKnowledge_decideToRelateObjectsByHigherObjectType(
			boolean knowledge_decideToRelateObjectsByHigherObjectType) {
		this.knowledge_decideToRelateObjectsByHigherObjectType = knowledge_decideToRelateObjectsByHigherObjectType;
	}

	/**
	 * <p>isKnowledge_decideToRelateObjectsByHigherAssocCost.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateObjectsByHigherAssocCost() {
		return knowledge_decideToRelateObjectsByHigherAssocCost;
	}

	/**
	 * <p>Setter for the field <code>knowledge_decideToRelateObjectsByHigherAssocCost</code>.</p>
	 *
	 * @param knowledge_decideToRelateObjectsByHigherAssocCost a boolean.
	 */
	public void setKnowledge_decideToRelateObjectsByHigherAssocCost(
			boolean knowledge_decideToRelateObjectsByHigherAssocCost) {
		this.knowledge_decideToRelateObjectsByHigherAssocCost = knowledge_decideToRelateObjectsByHigherAssocCost;
	}

	/**
	 * <p>isObjectsCreationDecider_createNewObjectsToAllPairs.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isObjectsCreationDecider_createNewObjectsToAllPairs() {
		return objectsCreationDecider_createNewObjectsToAllPairs;
	}

	/**
	 * <p>Setter for the field <code>objectsCreationDecider_createNewObjectsToAllPairs</code>.</p>
	 *
	 * @param objectsCreationDecider_createNewObjectsToAllPairs a boolean.
	 */
	public void setObjectsCreationDecider_createNewObjectsToAllPairs(
			boolean objectsCreationDecider_createNewObjectsToAllPairs) {
		this.objectsCreationDecider_createNewObjectsToAllPairs = objectsCreationDecider_createNewObjectsToAllPairs;
	}

	/**
	 * <p>Setter for the field <code>objectsCreationDecider_createNewObjectsToAllPairsDepth</code>.</p>
	 *
	 * @param objectsCreationDecider_createNewObjectsToAllPairsDepth a int.
	 */
	public void setObjectsCreationDecider_createNewObjectsToAllPairsDepth(
			int objectsCreationDecider_createNewObjectsToAllPairsDepth) {
		this.objectsCreationDecider_createNewObjectsToAllPairsDepth = objectsCreationDecider_createNewObjectsToAllPairsDepth;
	}

	/**
	 * <p>Getter for the field <code>objectsCreationDecider_createNewObjectsToAllPairsDepth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getObjectsCreationDecider_createNewObjectsToAllPairsDepth() {
		return objectsCreationDecider_createNewObjectsToAllPairsDepth;
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
	 * <p>isKnowledge_relateOnlyObjectsOfSameTypes.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_relateOnlyObjectsOfSameTypes() {
		return knowledge_relateOnlyObjectsOfSameTypes;
	}
	
	/**
	 * <p>Setter for the field <code>knowledge_relateOnlyObjectsOfSameTypes</code>.</p>
	 *
	 * @param knowledge_relateOnlyObjectsOfSameTypes a boolean.
	 * @throws org.apache.commons.configuration.ConfigurationException if any.
	 */
	public void setKnowledge_relateOnlyObjectsOfSameTypes (
			boolean knowledge_relateOnlyObjectsOfSameTypes) throws ConfigurationException {
		if (isKnowledge_decideToRelateByObjectTypeOrAssocCost() == true & knowledge_relateOnlyObjectsOfSameTypes == true) {
			throw new ConfigurationException(
					"Nemuze byt soucasne knowledge.decideToRelateByObjectTypeOrAssocCost() == " +
					"true & knowledge.relateOnlyObjectsOfSameTypes == true"
			);
		}
		this.knowledge_relateOnlyObjectsOfSameTypes = knowledge_relateOnlyObjectsOfSameTypes;
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
