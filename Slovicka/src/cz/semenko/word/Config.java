package cz.semenko.word;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import cz.semenko.word.aware.Knowledge;
import cz.semenko.word.persistent.Cell;

/**
 * Singleton. Access to program configuration.
 *
 * @author k
 * @version $Id: $Id
 */
public class Config {
	/** Business name of application */
	private String application_name = null;
	/** Kolik pismen bude nazcteno ze souboru do masivu. Je to jako vizualni pamet. TODO: vyzkouset co bude rychlejsi, doprogramovat chovani ktere zajisti nejrychlejsi nacitani */
	private int dataProvider_numCharsReadsFromInput = 0;
	/** Velikost tabulky Tables v cache FastMemory */
	private int fastMemory_tablesTableSize = 0;
	/** Velikost tabulky Cell v cache FastMemory */
	private int fastMemory_tablesCellSize = 0;
	/** Velikost tabulky Associations v cache FastMemory */
	private int fastMemory_tablesAssociationsSize = 0;
	/** While {@link Knowledge} decides to create a new {@link Cell} from two close Cells, it compare these Cells {@link Cell#type} with this configuration property. <br>
	 * If one of Cell has Type higher then configuration parameter, then new Cell will not created. */
	private int knowledge_relateThoughtsUpToCellType = 0;
	/** Maximalni velikost aware - delka vektoru myslenek */
	private int knowledge_knowledgeSize = 0;
	/** Zpusob pro rozhodovani, jak budou spojovany objekty behem cteni. 
		true - Budou spojeny dle velikosti type objektu,
		false - budou spojeny dle velikosti cost associaci */
	private boolean knowledge_decideToRelateByCellTypeOrAssocCost = false;
	/** Jestli jsou tri objekty a b c, a existuji associace ab a bc, pritom
		c je objekt s vetsim cost nez b, potom bude prednostne pouzita ab, kdyz parameter
		je nastaven na true, nebo bc kdyz parameter je nastaven na false */
	private boolean knowledge_decideToRelateCellsByHigherCellType = false;
	/** Jestli jsou tri objekty za sebou (a, b, c), a existuji asociace ab, bc,
		potom budou spojovany objekty dle velikosti hodnot asociaci.
		true - budou spojovany objekty s vyssi hodnotou asociaci
		false - budou spojovany objekty s nizsi hodnotou asociaci */
	private boolean knowledge_decideToRelateCellsByHigherAssocCost = false;
	/** A new {@link Cell} will be created, when its parents has {@link Cell#type} less or equal to this parameter. */
	private int cellsCreationDecider_createNewCellsToAllPairsDepth = 0;
	/** Zde zavadim neco jako Hluboke vyhledavani a Melke vyhledavani.
	* Melke - hledat jen ve FastMemory.
	* 	nehledat kdyz ve FastMemory nalezena aspon jedna Association.
	* 	Kdyz aspon jedna chybi -
	* 		dohledat associations u vsech prvku,
	* 		nebo
	* 		dohledat associations jen u chybejicich prvku.
	* Hluboke - dohledat vzdy v SlowlyMemory.
	**/
	private boolean fastMemory_alwaysSearchToAssociationsDeepInTheMemory = false;
	/** Jestli alwaysSearchToAssociationsDeepInTheMemory je true, nema zadny vyznam. Viz. {@link Config#fastMemory_alwaysSearchToAssociationsDeepInTheMemory} */
	private boolean fastMemory_searchToAssociationsAtAllElements = false;
	/** Zda maji byt spojovany jen objekty stejneho typu. Jestli true, parametr decideToRelateByCellTypeOrAssocCost musi byt false. */
	private boolean knowledge_relateOnlyCellsOfSameTypes = false;
	/** Ukladat do souboru pospojovane objekty behem cteni nebo ne */
	private boolean knowledge_saveThoughtsToFile = false;
	/** Cesta a nazev souboru pro ulozeni idecek spojenzch objektu behem cteni vstupniho souboru */
	private String thoughtsSaver_filePathToSaveThoughts;
	/** Behem vycisteni pameti (spanku) budou odstraneny asociace a jejich objekty, ktere maji 
		nizsi COST */
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
			dataProvider_numCharsReadsFromInput = conf.getInt("dataProvider.numCharsReadsFromInput");
			fastMemory_tablesTableSize = conf.getInt("fastMemory.tablesTableSize");
			fastMemory_tablesCellSize = conf.getInt("fastMemory.tablesCellsSize");
			if(dataProvider_numCharsReadsFromInput > fastMemory_tablesCellSize) {
				String msg = "Configuration parameter dataProvider.numCharsReadsFromInput must not be greater then fastMemory.tablesCellsSize";
				System.out.println(msg);
				throw new ConfigurationException(msg);
			}
			setFastMemory_tablesAssociationsSize(conf.getInt("fastMemory.tablesAssociationsSize"));
			setKnowledge_relateThoughtsUpToCellType(conf.getInt("knowledge.relateThoughtsUpToCellType"));
			setKnowledge_knowledgeSize(conf.getInt("knowledge.knowledgeSize"));
			setKnowledge_decideToRelateByCellTypeOrAssocCost(conf.getBoolean("knowledge.decideToRelateByCellTypeOrAssocCost"));
			setKnowledge_decideToRelateCellsByHigherAssocCost(conf.getBoolean("knowledge.decideToRelateCellsByHigherAssocCost"));
			setKnowledge_decideToRelateCellsByHigherCellType(conf.getBoolean("knowledge.decideToRelateCellsByHigherCellType"));
			setKnowledge_relateOnlyCellsOfSameTypes(conf.getBoolean("knowledge.relateOnlyCellsOfSameTypes"));
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

	/**
	 * <p>Getter for the field {@link Config#knowledge_relateThoughtsUpToCellType}.</p>
	 *
	 * @return a int.
	 */
	public int getKnowledge_relateThoughtsUpToCellType() {
		return knowledge_relateThoughtsUpToCellType;
	}

	/**
	 * <p>Setter for the field {@link Config#knowledge_relateThoughtsUpToCellType} .</p>
	 *
	 * @param relateThoughtsUpToCellType a int.
	 */
	public void setKnowledge_relateThoughtsUpToCellType(int relateThoughtsUpToCellType) {
		knowledge_relateThoughtsUpToCellType = relateThoughtsUpToCellType;
	}

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
	 * <p>See {@link Config#knowledge_decideToRelateByCellTypeOrAssocCost}.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_decideToRelateByCellTypeOrAssocCost() {
		return knowledge_decideToRelateByCellTypeOrAssocCost;
	}

	/**
	 * <p>Setter for the field {@link Config#knowledge_decideToRelateByCellTypeOrAssocCost}.</p>
	 *
	 * @param knowledge_decideToRelateByCellTypeOrAssocCost a boolean.
	 */
	public void setKnowledge_decideToRelateByCellTypeOrAssocCost(
			boolean knowledge_decideToRelateByCellTypeOrAssocCost) {
		this.knowledge_decideToRelateByCellTypeOrAssocCost = knowledge_decideToRelateByCellTypeOrAssocCost;
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
	 *
	 * @param fastMemory_searchToAssociationsAtAllElements a boolean.
	 */
	public void setFastMemory_searchToAssociationsAtAllElements(
			boolean fastMemory_searchToAssociationsAtAllElements) {
		this.fastMemory_searchToAssociationsAtAllElements = fastMemory_searchToAssociationsAtAllElements;
	}

	/**
	 * <p>See {@link Config#knowledge_relateOnlyCellsOfSameTypes}.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_relateOnlyCellsOfSameTypes() {
		return knowledge_relateOnlyCellsOfSameTypes;
	}
	
	/**
	 * <p>Setter for the field {@link Config#knowledge_relateOnlyCellsOfSameTypes}.</p>
	 *
	 * @param knowledge_relateOnlyCellsOfSameTypes a boolean.
	 * @throws org.apache.commons.configuration.ConfigurationException if any.
	 */
	public void setKnowledge_relateOnlyCellsOfSameTypes (
			boolean knowledge_relateOnlyCellsOfSameTypes) throws ConfigurationException {
		if (isKnowledge_decideToRelateByCellTypeOrAssocCost() == true & knowledge_relateOnlyCellsOfSameTypes == true) {
			throw new ConfigurationException(
					"Nemuze byt soucasne knowledge.decideToRelateByCellTypeOrAssocCost() == " +
					"true & knowledge.relateOnlyCellsOfSameTypes == true"
			);
		}
		this.knowledge_relateOnlyCellsOfSameTypes = knowledge_relateOnlyCellsOfSameTypes;
	}

	/**
	 * <p>See {@link Config#knowledge_saveThoughtsToFile}.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isKnowledge_saveThoughtsToFile() {
		return knowledge_saveThoughtsToFile;
	}
	/**
	 * <p>Setter for the field {@link Config#knowledge_saveThoughtsToFile}.</p>
	 *
	 * @param knowledge_saveThoughtsToFile a boolean.
	 */
	public void setKnowledge_saveThoughtsToFile(boolean knowledge_saveThoughtsToFile) {
		this.knowledge_saveThoughtsToFile  = knowledge_saveThoughtsToFile;
	}

	/**
	 * <p>Getter for the field {@link Config#thoughtsSaver_filePathToSaveThoughts}.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getThoughtsSaver_filePathToSaveThoughts() {
		return thoughtsSaver_filePathToSaveThoughts;
	}
	/**
	 * <p>Setter for the field {@link Config#thoughtsSaver_filePathToSaveThoughts}.</p>
	 *
	 * @param param a {@link java.lang.String} object.
	 */
	public void setThoughtsSaver_filePathToSaveThoughts(String param) {
		this.thoughtsSaver_filePathToSaveThoughts = param.trim();
	}

	/**
	 * <p>Getter for the field {@link Config#memoryCleaner_lowestCostForLeaving}.</p>
	 *
	 * @return a int.
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
}
