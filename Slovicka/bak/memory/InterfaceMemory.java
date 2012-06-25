package cz.semenko.word.model.memory;


public interface InterfaceMemory {
	
	/** Vyhleda objekty pro vstupni znaky. Jestli objekt neexistuje, vytvori ho **/
	public Long[] getObjects(char[] inputChars) throws Exception;
	//public static Memory getInstance();

}
