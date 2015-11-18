package cz.semenko.word;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import cz.semenko.word.dao.DBViewer;
import cz.semenko.word.persistent.Associations;
import cz.semenko.word.persistent.Cell;
import cz.semenko.word.sleep.MemoryCleaner;

/**
 * Singleton. Access to application configuration.
 *
 * @author Kyrylo Semenko
 */
public class Config {
	/** Business name of application */
	private String application_name = null;
	
	/** Place where database lives */
	private String application_databaseHome;
	
	/** Kolik pismen bude nazcteno ze souboru do masivu. Je to jako vizualni pamet. */
	private int dataProvider_numCharsReadsFromInput = 0;
	
	/** Velikost tabulky Tables v cache FastMemory */
	private int fastMemory_tablesTableSize = 0;
	
	/** Velikost tabulky Cell v cache FastMemory */
	private int fastMemory_tablesCellSize = 0;
	
	/** Velikost tabulky Associations v cache FastMemory */
	private int fastMemory_tablesAssociationsSize = 0;

	/** Maximalni velikost aware - delka vektoru myslenek */
	private int knowledge_knowledgeSize = 0;

	/**
	 * Jestli jsou tri objekty a b c, a existuji associace ab a bc, pritom c je objekt s vetsim cost nez b,<br>
	 * potom bude prednostne pouzita ab, kdyz parameter je nastaven na true,<br>
	 * nebo bc kdyz parameter je nastaven na false
	 */
	private boolean knowledge_decideToRelateCellsByHigherCellType = false;

	/**
	 * Jestli jsou tri objekty za sebou (a, b, c), a existuji asociace ab, bc,<br>
	 * potom budou spojovany objekty dle velikosti hodnot asociaci.<br>
	 * true - budou spojovany objekty s vyssi hodnotou asociaci<br>
	 * false - budou spojovany objekty s nizsi hodnotou asociaci
	 */
	private boolean knowledge_decideToRelateCellsByHigherAssocCost = false;
	
	/**
	 * Relate only cells with {@link Associations#getCost()} equals or higher then this parameter.<br>
	 * This parameter is accepted together with {@link Config#knowledge_decideToRelateCellsByHigherAssocCost} parameter only
	 */
	private int knowledge_minAssocCostToRelate = 0;
	
	/** A new {@link Cell} will be created, when its parents has {@link Cell#getType()} less or equal to this parameter. */
	private int cellsCreationDecider_createNewCellsToAllPairsDepth = 0;
	
	/**
	 * Zde zavadim neco jako Hluboke vyhledavani a Melke vyhledavani.<br>
	 * <ul>
	 * 	<li>Melke - hledat jen ve FastMemory.
	 * 		<ul>
	 * 			<li>Nehledat kdyz ve FastMemory nalezena aspon jedna Association.</li>
	 * 			<li>Kdyz aspon jedna chybi:
	 * 				<ul>
	 * 					<li>dohledat associations u vsech prvku,</li>
	 * 					<li>nebo dohledat associations jen u chybejicich prvku</li>
	 * 				</ul>
	 * 			</li>
	 *		</ul>
	 * 	</li>
	 * 	<li>Hluboke - dohledat vzdy v SlowMemory.</li>
	 * </ul>
	 **/
	private boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory = false;
	
	/**
	 * Když je false, hledat jen v cache. Jinak hledat i v DB.
	 * Jestli {@link Config#fastMemory_alwaysSearchToAssociationsDeepInTheMemory} je true, parametr nemá žádný význam.
	 * Viz. {@link Config#fastMemory_alwaysSearchToAssociationsDeepInTheMemory} */
	private boolean fastMemory_searchToAssociationsAtAllElements = false;
	
	/** Zda maji byt spojovany jen objekty stejneho typu. */
	private boolean knowledge_relateOnlyCellsOfTheSameTypes = false;
	
	/** Ukladat do souboru pospojovane objekty behem cteni nebo ne */
	private boolean knowledge_saveThoughtsToFile = false;
	
	/** Cesta a nazev souboru pro ulozeni idecek spojenzch objektu behem cteni vstupniho souboru */
	private String thoughtsSaver_filePathToSaveThoughts;

	/**
	 * While sleeping (see {@link MemoryCleaner}) will be remove {@link Associations} that has {@link Associations#getCost()} less than this parameter.<br>
	 * Disconnected {@link Cell} objects will by removed too.
	 */
	private int memoryCleaner_lowestCostForLeaving;

	/**
	 * Length of a text saved to database when a new {@link Cell} creates.<br>
	 * Text that longer then this parameter is ignored.<br>
	 * This value must not be greater then {@link Cell#getSrc()} table column size
	 */
	private int dbViewer_maxTextLengthToSave;
	
	/** How many rows will be clean up during one loop of cleaning tables Cells and Associations from empty rows. */
	private int dbViewer_numRowsForCleanupRotation;
	
	/** Number of IDs to return from {@link DBViewer#getAvailableCellsIdList(Long)} when new {@link Cell} objects are creating */
	private int dbViewer_numberOfAvailableCellsIdToReturn;
	
	/** Number of IDs to return from {@link DBViewer#getAvailableAssociationsIdList(Long)} when new {@link Associations} objects are creating */
	private int dbViewer_numberOfAvailableAssociationsIdToReturn;
	
	/**
	 * Když je <b>true</b>, aplikovat pravidla spojení sousedních cell na základě konkurence. Když je <b>false</b>, spojovat všechny sousední cells<br>
	 * If <b>true</b>, decide which cells has to be merged based on competition rules. If <b>false</b>, always merge neighboring cells.
	 */
	private boolean thoughtUnionDecider_competitionAllowed;
	
	private static XMLConfiguration conf;
	
	/** Constant <code>logger</code> */
	public static Logger logger = Logger.getRootLogger();
	
	/**
	 * <p>Constructor for Config.</p>
	 */
	public Config() {
		try {
			conf = new XMLConfiguration("config.xml");
			// Fill out private variables
			application_name = conf.getString("application.name");
			setApplication_databaseHome(conf.getString("application.databaseHome"));
			dataProvider_numCharsReadsFromInput = conf.getInt("dataProvider.numCharsReadsFromInput");
			fastMemory_tablesTableSize = conf.getInt("fastMemory.tablesTableSize");
			fastMemory_tablesCellSize = conf.getInt("fastMemory.tablesCellsSize");
			if(dataProvider_numCharsReadsFromInput > fastMemory_tablesCellSize) {
				String msg = "Configuration parameter dataProvider.numCharsReadsFromInput must not be greater then fastMemory.tablesCellsSize";
				System.out.println(msg);
				throw new ConfigurationException(msg);
			}
			setFastMemory_tablesAssociationsSize(conf.getInt("fastMemory.tablesAssociationsSize"));
			setKnowledge_knowledgeSize(conf.getInt("knowledge.knowledgeSize"));
			setKnowledge_decideToRelateCellsByHigherAssocCost(conf.getBoolean("knowledge.decideToRelateCellsByHigherAssocCost"));
			setKnowledge_minAssocCostToRelate(conf.getInt("knowledge.minAssocCostToRelate"));
			setKnowledge_decideToRelateCellsByHigherCellType(conf.getBoolean("knowledge.decideToRelateCellsByHigherCellType"));
			setKnowledge_relateOnlyCellsOfTheSameTypes(conf.getBoolean("knowledge.relateOnlyCellsOfTheSameTypes"));
			setCellsCreationDecider_createNewCellsToAllPairsDepth(conf.getInt("cellsCreationDecider.createNewCellsToAllPairsDepth"));
			setFastMemory_alwaysSearchToAssociationsDeepInTheMemory(conf.getBoolean("fastMemory.alwaysSearchToAssociationsDeepInTheMemory"));
			setFastMemory_searchToAssociationsAtAllElements(conf.getBoolean("fastMemory.searchToAssociationsAtAllElements"));
			setKnowledge_saveThoughtsToFile(conf.getBoolean("knowledge.saveThoughtsToFile"));
			setThoughtsSaver_filePathToSaveThoughts(conf.getString("thoughtsSaver.filePathToSaveThoughts"));
			setMemoryCleaner_lowestCostForLeaving(conf.getInt("memoryCleaner.lowestCostForLeaving"));
			setDbViewer_maxTextLengthToSave(conf.getInt("dbViewer.maxTextLengthToSave"));
			setDbViewer_numRowsForCleanupRotation(conf.getInt("dbViewer.numRowsForCleanupRotation"));
			setDbViewer_numberOfAvailableCellsIdToReturn(conf.getInt("dbViewer.numberOfAvailableCellsIdToReturn"));
			setDbViewer_numberOfAvailableAssociationsIdToReturn(conf.getInt("dbViewer.numberOfAvailableAssociationsIdToReturn"));
			setThoughtUnionDecider_competitionAllowed(conf.getBoolean("thoughtUnionDecider.competitionAllowed"));
		} catch (ConfigurationException e) {
			logger.error(e.getMessage(), e);
			System.out.println(e.getMessage());
			System.exit(1);
		}
	}
	
	/** Application business name */
	public String getApplication_name() {
		return application_name;
	}

	/**
	 * <p>Getter for the field {@link Config#dataProvider_numCharsReadsFromInput}.</p>
	 *
	 * @return a int.
	 */
	public int getDataProvider_numCharsReadsFromInput() {
		return dataProvider_numCharsReadsFromInput;
	}

	/**
	 * <p>Setter for the field {@link Config#dataProvider_numCharsReadsFromInput}.</p>
	 *
	 * @param dataProviderNumCharsReadsFromInput a int.
	 */
	public void setDataProvider_numCharsReadsFromInput(
			int dataProviderNumCharsReadsFromInput) {
		dataProvider_numCharsReadsFromInput = dataProviderNumCharsReadsFromInput;
	}

	/**
	 * <p>Getter for the field {@link Config#fastMemory_tablesTableSize}.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesTableSize() {
		return fastMemory_tablesTableSize;
	}

	/**
	 * <p>Setter for the field {@link Config#fastMemory_tablesTableSize}.</p>
	 *
	 * @param fastMemoryTablesTableSize a int.
	 */
	public void setFastMemory_tablesTableSize(int fastMemoryTablesTableSize) {
		fastMemory_tablesTableSize = fastMemoryTablesTableSize;
	}

	/**
	 * <p>Getter for the field {@link Config#fastMemory_tablesCellSize}.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesCellSize() {
		return fastMemory_tablesCellSize;
	}

	/**
	 * <p>Setter for the field {@link Config#fastMemory_tablesCellSize}.</p>
	 *
	 * @param fastMemoryTablesCellSize a int.
	 */
	public void setFastMemory_tablesCellSize(
			int fastMemoryTablesCellSize) {
		fastMemory_tablesCellSize = fastMemoryTablesCellSize;
	}


	/**
	 * <p>Setter for the field {@link Config#fastMemory_tablesAssociationsSize}.</p>
	 *
	 * @param fastMemory_tablesAssociationsSize a int.
	 */
	public void setFastMemory_tablesAssociationsSize(
			int fastMemory_tablesAssociationsSize) {
		this.fastMemory_tablesAssociationsSize = fastMemory_tablesAssociationsSize;
	}

	/**
	 * <p>Getter for the field {@link Config#fastMemory_tablesAssociationsSize}.</p>
	 *
	 * @return a int.
	 */
	public int getFastMemory_tablesAssociationsSize() {
		return fastMemory_tablesAssociationsSize;
	}

//	/**
//	 * <p>Getter for the field {@link Config#knowledge_relateThoughtsUpToCellType}.</p>
//	 *
//	 * @return a int.
//	 */
//	public int getKnowledge_relateThoughtsUpToCellType() {
//		return knowledge_relateThoughtsUpToCellType;
//	}
//
//	/**
//	 * <p>Setter for the field {@link Config#knowledge_relateThoughtsUpToCellType} .</p>
//	 *
//	 * @param relateThoughtsUpToCellType a int.
//	 */
//	public void setKnowledge_relateThoughtsUpToCellType(int relateThoughtsUpToCellType) {
//		knowledge_relateThoughtsUpToCellType = relateThoughtsUpToCellType;
//	}

	/**
	 * <p>Getter for the field {@link Config#knowledge_knowledgeSize}.</p>
	 *
	 * @return a int.
	 */
	public int getKnowledge_knowledgeSize() {
		return knowledge_knowledgeSize;
	}

	/**
	 * <p>Setter for the field {@link Config#knowledge_knowledgeSize}.</p>
	 *
	 * @param knowledgeKnowledgeSize a int.
	 */
	public void setKnowledge_knowledgeSize(int knowledgeKnowledgeSize) {
		knowledge_knowledgeSize = knowledgeKnowledgeSize;
	}

	/**
	 * <p>See {@link Config#knowledge_decideToRelateCellsByHigherCellType}.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateCellsByHigherCellType() {
		return knowledge_decideToRelateCellsByHigherCellType;
	}

	/**
	 * <p>Setter for the field {@link Config#knowledge_decideToRelateCellsByHigherCellType}.</p>
	 *
	 * @param knowledge_decideToRelateCellsByHigherCellType a boolean.
	 */
	public void setKnowledge_decideToRelateCellsByHigherCellType(
			boolean knowledge_decideToRelateCellsByHigherCellType) {
		this.knowledge_decideToRelateCellsByHigherCellType = knowledge_decideToRelateCellsByHigherCellType;
	}

	/**
	 * <p>See {@link Config#knowledge_decideToRelateCellsByHigherAssocCost}.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateCellsByHigherAssocCost() {
		return knowledge_decideToRelateCellsByHigherAssocCost;
	}

	/**
	 * <p>Setter for the field {@link Config#knowledge_decideToRelateCellsByHigherAssocCost}.</p>
	 *
	 * @param knowledge_decideToRelateCellsByHigherAssocCost a boolean.
	 */
	public void setKnowledge_decideToRelateCellsByHigherAssocCost(
			boolean knowledge_decideToRelateCellsByHigherAssocCost) {
		this.knowledge_decideToRelateCellsByHigherAssocCost = knowledge_decideToRelateCellsByHigherAssocCost;
	}

	/**
	 * @return the {@link int}<br>
	 * See {@link Config#knowledge_minAssocCostToRelate}
	 */
	public int getKnowledge_minAssocCostToRelate() {
		return knowledge_minAssocCostToRelate;
	}

	/**
	 * @param knowledge_minAssocCostToRelate the {@link int} to set<br>
	 * See {@link Config#knowledge_minAssocCostToRelate}
	 */
	public void setKnowledge_minAssocCostToRelate(int knowledge_minAssocCostToRelate) {
		this.knowledge_minAssocCostToRelate = knowledge_minAssocCostToRelate;
	}

	/**
	 * <p>Setter for the field {@link Config#cellsCreationDecider_createNewCellsToAllPairsDepth}.</p>
	 *
	 * @param cellsCreationDecider_createNewCellsToAllPairsDepth a int.
	 */
	public void setCellsCreationDecider_createNewCellsToAllPairsDepth(
			int cellsCreationDecider_createNewCellsToAllPairsDepth) {
		this.cellsCreationDecider_createNewCellsToAllPairsDepth = cellsCreationDecider_createNewCellsToAllPairsDepth;
	}

	/**
	 * <p>Getter for the field {@link Config#cellsCreationDecider_createNewCellsToAllPairsDepth}.</p>
	 *
	 * @return a int.
	 */
	public int getCellsCreationDecider_createNewCellsToAllPairsDepth() {
		return cellsCreationDecider_createNewCellsToAllPairsDepth;
	}

	/**
	 * <p>See {@link Config#isFastMemory_alwaysSearchToAssociationsDeepInTheMemory}</p>
	 *
	 * @return a boolean.
	 */
	public boolean isFastMemory_alwaysSearchToAssociationsDeepInTheMemory() {
		return fastMemory_alwaysSearchToAssociationsDeepInTheMemory;
	}
	
	/**
	 * <p>Setter for the field {@link Config#fastMemory_alwaysSearchToAssociationsDeepInTheMemory}.</p>
	 *
	 * @param fastMemory_alwaysSearchToAssociationsDeepInTheMemory a boolean.
	 */
	public void setFastMemory_alwaysSearchToAssociationsDeepInTheMemory(
			boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory) {
		this.fastMemory_alwaysSearchToAssociationsDeepInTheMemory = fastMemory_alwaysSearchToAssociationsDeepInTheMemory;
	}

	/**
	 * <p>See {@link Config#fastMemory_searchToAssociationsAtAllElements}.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isFastMemory_searchToAssociationsAtAllElements() {
		return fastMemory_searchToAssociationsAtAllElements;
	}

	/**
	 * <p>Setter for the field {@link Config#fastMemory_searchToAssociationsAtAllElements}.</p>
	 */
	public void setFastMemory_searchToAssociationsAtAllElements(
			boolean fastMemory_searchToAssociationsAtAllElements) {
		this.fastMemory_searchToAssociationsAtAllElements = fastMemory_searchToAssociationsAtAllElements;
	}

	/**
	 * <p>See {@link Config#knowledge_relateOnlyCellsOfTheSameTypes}.</p>
	 */
	public boolean isKnowledge_relateOnlyCellsOfTheSameTypes() {
		return knowledge_relateOnlyCellsOfTheSameTypes;
	}
	
	/**
	 * <p>Setter for the field {@link Config#knowledge_relateOnlyCellsOfTheSameTypes}.</p>
	 */
	public void setKnowledge_relateOnlyCellsOfTheSameTypes (
			boolean knowledge_relateOnlyCellsOfSameTypes) throws ConfigurationException {
		this.knowledge_relateOnlyCellsOfTheSameTypes = knowledge_relateOnlyCellsOfSameTypes;
	}

	/**
	 * <p>See {@link Config#knowledge_saveThoughtsToFile}.</p>
	 */
	public boolean isKnowledge_saveThoughtsToFile() {
		return knowledge_saveThoughtsToFile;
	}
	/**
	 * <p>Setter for the field {@link Config#knowledge_saveThoughtsToFile}.</p>
	 */
	public void setKnowledge_saveThoughtsToFile(boolean knowledge_saveThoughtsToFile) {
		this.knowledge_saveThoughtsToFile  = knowledge_saveThoughtsToFile;
	}

	/**
	 * <p>Getter for the field {@link Config#thoughtsSaver_filePathToSaveThoughts}.</p>
	 */
	public String getThoughtsSaver_filePathToSaveThoughts() {
		return thoughtsSaver_filePathToSaveThoughts;
	}
	/**
	 * <p>Setter for the field {@link Config#thoughtsSaver_filePathToSaveThoughts}.</p>
	 */
	public void setThoughtsSaver_filePathToSaveThoughts(String param) {
		this.thoughtsSaver_filePathToSaveThoughts = param.trim();
	}

	/**
	 * <p>Getter for the field {@link Config#memoryCleaner_lowestCostForLeaving}.</p>
	 */
	public int getMemoryCleaner_lowestCostForLeaving() {
		return memoryCleaner_lowestCostForLeaving;
	}
	/**
	 * <p>Setter for the field {@link Config#memoryCleaner_lowestCostForLeaving}.</p>
	 *
	 * @param memoryCleaner_lowestCostForLeaving a int.
	 */
	public void setMemoryCleaner_lowestCostForLeaving(int memoryCleaner_lowestCostForLeaving) {
		this.memoryCleaner_lowestCostForLeaving = memoryCleaner_lowestCostForLeaving;
	}
	
	/** See {@link Config#application_databaseHome} */
	public String getApplication_databaseHome() {
		return application_databaseHome;
	}

	/** See {@link Config#application_databaseHome} */
	public void setApplication_databaseHome(String application_databaseHome) {
		this.application_databaseHome = application_databaseHome;
	}

	/** See {@link Config#dbViewer_maxTextLengthToSave} */
	public int getDbViewer_maxTextLengthToSave() {
		return dbViewer_maxTextLengthToSave;
	}

	/** See {@link Config#dbViewer_maxTextLengthToSave} */
	public void setDbViewer_maxTextLengthToSave(int dbViewer_maxTextLengthToSave) {
		this.dbViewer_maxTextLengthToSave = dbViewer_maxTextLengthToSave;
	}

	/** See {@link Config#dbViewer_numRowsForCleanupRotation} */
	public int getDbViewer_numRowsForCleanupRotation() {
		return dbViewer_numRowsForCleanupRotation;
	}

	/** See {@link Config#dbViewer_numRowsForCleanupRotation} */
	public void setDbViewer_numRowsForCleanupRotation(
			int dbViewer_numRowsForCleanupRotation) {
		this.dbViewer_numRowsForCleanupRotation = dbViewer_numRowsForCleanupRotation;
	}

	/**
	 * See {@link Config#dbViewer_numberOfAvailableCellsIdToReturn}
	 */
	public int getDbViewer_numberOfAvailableCellsIdToReturn() {
		return dbViewer_numberOfAvailableCellsIdToReturn;
	}

	/**
	 * See {@link Config#dbViewer_numberOfAvailableCellsIdToReturn}
	 */
	public void setDbViewer_numberOfAvailableCellsIdToReturn(
			int dbViewer_numberOfAvailableCellsIdToReturn) {
		this.dbViewer_numberOfAvailableCellsIdToReturn = dbViewer_numberOfAvailableCellsIdToReturn;
	}

	/**
	 * See {@link Config#dbViewer_numberOfAvailableAssociationsIdToReturn}
	 */
	public int getDbViewer_numberOfAvailableAssociationsIdToReturn() {
		return dbViewer_numberOfAvailableAssociationsIdToReturn;
	}

	/**
	 * See {@link Config#dbViewer_numberOfAvailableAssociationsIdToReturn}
	 */
	public void setDbViewer_numberOfAvailableAssociationsIdToReturn(
			int dbViewer_numberOfAvailableAssociationsIdToReturn) {
		this.dbViewer_numberOfAvailableAssociationsIdToReturn = dbViewer_numberOfAvailableAssociationsIdToReturn;
	}

	/**
	 * See {@link Config#thoughtUnionDecider_competitionAllowed}
	 */
	public boolean isThoughtUnionDecider_competitionAllowed() {
		return thoughtUnionDecider_competitionAllowed;
	}

	/**
	 * See {@link Config#thoughtUnionDecider_competitionAllowed}
	 */
	public void setThoughtUnionDecider_competitionAllowed(boolean thoughtUnionDecider_competitionAllowed) {
		this.thoughtUnionDecider_competitionAllowed = thoughtUnionDecider_competitionAllowed;
	}
}
