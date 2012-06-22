package cz.semenko.word;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * Singleton. Access to program configuration.
 * @author k
 *
 */
public class Config {
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
	private String dbviewer_dbViewerClassName;
	
	private static Config instance;
	private static XMLConfiguration conf;
	public static Logger logger = Logger.getRootLogger();
	
	private Config() {
		try {
			conf = new XMLConfiguration("config.xml");
			// Naplnit privatni promenne
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
			setDBViewerClassName(conf.getString("dbviewer.dbViewerClassName"));
		} catch (ConfigurationException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Create or find out instance of this class
	 * @return Config object
	 */
	public static Config getInstance() {
		if (instance == null) {
			synchronized(Config.class) {
				Config inst = instance;
				if (inst == null) {
					instance = new Config();
					// conf je vytvoren
				}
			}
		}
		return instance;
	}

	public int getDataProvider_numCharsReadsFromInput() {
		return dataProvider_numCharsReadsFromInput;
	}

	public void setDataProvider_numCharsReadsFromInput(
			int dataProviderNumCharsReadsFromInput) {
		dataProvider_numCharsReadsFromInput = dataProviderNumCharsReadsFromInput;
	}

	public int getFastMemory_tablesTableSize() {
		return fastMemory_tablesTableSize;
	}

	public void setFastMemory_tablesTableSize(int fastMemoryTablesTableSize) {
		fastMemory_tablesTableSize = fastMemoryTablesTableSize;
	}

	public int getFastMemory_tablesObjectsSize() {
		return fastMemory_tablesObjectsSize;
	}

	public void setFastMemory_tablesObjectsSize(
			int fastMemoryTablesObjectsSize) {
		fastMemory_tablesObjectsSize = fastMemoryTablesObjectsSize;
	}

	public String getDbCon_dbURL() {
		return dbCon_dbURL;
	}

	public void setDbCon_dbURL(String dbConDbURL) {
		dbCon_dbURL = dbConDbURL;
	}

	public String getDbCon_derbyJarServerStart() {
		return dbCon_derbyJarServerStart;
	}

	public void setDbCon_derbyJarServerStart(String dbConDerbyJarServerStart) {
		dbCon_derbyJarServerStart = dbConDerbyJarServerStart;
	}

	public String getDbCon_derbyJarServerStop() {
		return dbCon_derbyJarServerStop;
	}

	public void setDbCon_derbyJarServerStop(String dbConDerbyJarServerStop) {
		dbCon_derbyJarServerStop = dbConDerbyJarServerStop;
	}

	public void setFastMemory_tablesAssociationsSize(
			int fastMemory_tablesAssociationsSize) {
		this.fastMemory_tablesAssociationsSize = fastMemory_tablesAssociationsSize;
	}

	public int getFastMemory_tablesAssociationsSize() {
		return fastMemory_tablesAssociationsSize;
	}

	public int getKnowledge_objectsCreationDepth() {
		return knowledge_objectsCreationDepth;
	}

	public void setKnowledge_objectsCreationDepth(int knowledgeObjectsCreationDepth) {
		knowledge_objectsCreationDepth = knowledgeObjectsCreationDepth;
	}

	public int getKnowledge_knowledgeSize() {
		return knowledge_knowledgeSize;
	}

	public void setKnowledge_knowledgeSize(int knowledgeKnowledgeSize) {
		knowledge_knowledgeSize = knowledgeKnowledgeSize;
	}

	public boolean isKnowledge_decideToRelateByObjectTypeOrAssocCost() {
		return knowledge_decideToRelateByObjectTypeOrAssocCost;
	}

	public void setKnowledge_decideToRelateByObjectTypeOrAssocCost(
			boolean knowledge_decideToRelateByObjectTypeOrAssocCost) {
		this.knowledge_decideToRelateByObjectTypeOrAssocCost = knowledge_decideToRelateByObjectTypeOrAssocCost;
	}

	public boolean isKnowledge_decideToRelateObjectsByHigherObjectType() {
		return knowledge_decideToRelateObjectsByHigherObjectType;
	}

	public void setKnowledge_decideToRelateObjectsByHigherObjectType(
			boolean knowledge_decideToRelateObjectsByHigherObjectType) {
		this.knowledge_decideToRelateObjectsByHigherObjectType = knowledge_decideToRelateObjectsByHigherObjectType;
	}

	public boolean isKnowledge_decideToRelateObjectsByHigherAssocCost() {
		return knowledge_decideToRelateObjectsByHigherAssocCost;
	}

	public void setKnowledge_decideToRelateObjectsByHigherAssocCost(
			boolean knowledge_decideToRelateObjectsByHigherAssocCost) {
		this.knowledge_decideToRelateObjectsByHigherAssocCost = knowledge_decideToRelateObjectsByHigherAssocCost;
	}

	public boolean isObjectsCreationDecider_createNewObjectsToAllPairs() {
		return objectsCreationDecider_createNewObjectsToAllPairs;
	}

	public void setObjectsCreationDecider_createNewObjectsToAllPairs(
			boolean objectsCreationDecider_createNewObjectsToAllPairs) {
		this.objectsCreationDecider_createNewObjectsToAllPairs = objectsCreationDecider_createNewObjectsToAllPairs;
	}

	public void setObjectsCreationDecider_createNewObjectsToAllPairsDepth(
			int objectsCreationDecider_createNewObjectsToAllPairsDepth) {
		this.objectsCreationDecider_createNewObjectsToAllPairsDepth = objectsCreationDecider_createNewObjectsToAllPairsDepth;
	}

	public int getObjectsCreationDecider_createNewObjectsToAllPairsDepth() {
		return objectsCreationDecider_createNewObjectsToAllPairsDepth;
	}

	public boolean isFastMemory_alwaysSearchToAssociationsDeepInTheMemory() {
		return fastMemory_alwaysSearchToAssociationsDeepInTheMemory;
	}
	
	public void setFastMemory_alwaysSearchToAssociationsDeepInTheMemory(
			boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory) {
		this.fastMemory_alwaysSearchToAssociationsDeepInTheMemory = fastMemory_alwaysSearchToAssociationsDeepInTheMemory;
	}

	public boolean isFastMemory_searchToAssociationsAtAllElements() {
		return fastMemory_searchToAssociationsAtAllElements;
	}

	public void setFastMemory_searchToAssociationsAtAllElements(
			boolean fastMemory_searchToAssociationsAtAllElements) {
		this.fastMemory_searchToAssociationsAtAllElements = fastMemory_searchToAssociationsAtAllElements;
	}

	public boolean isKnowledge_relateOnlyObjectsOfSameTypes() {
		return knowledge_relateOnlyObjectsOfSameTypes;
	}
	
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

	public boolean isKnowledge_saveThoughtsToFile() {
		return knowledge_saveThoughtsToFile;
	}
	public void setKnowledge_saveThoughtsToFile(boolean knowledge_saveThoughtsToFile) {
		this.knowledge_saveThoughtsToFile  = knowledge_saveThoughtsToFile;
	}

	public String getThoughtsSaver_filePathToSaveThoughts() {
		return thoughtsSaver_filePathToSaveThoughts;
	}
	public void setThoughtsSaver_filePathToSaveThoughts(String param) {
		this.thoughtsSaver_filePathToSaveThoughts = param.trim();
	}

	public int getMemoryCleaner_lowestCostForLeaving() {
		return memoryCleaner_lowestCostForLeaving;
	}
	public void setMemoryCleaner_lowestCostForLeaving(int memoryCleaner_lowestCostForLeaving) {
		this.memoryCleaner_lowestCostForLeaving = memoryCleaner_lowestCostForLeaving;
	}

	public String getDBViewerClassName() {
		return dbviewer_dbViewerClassName;
	}
	public void setDBViewerClassName(String param) {
		this.dbviewer_dbViewerClassName = param;
	}
}
