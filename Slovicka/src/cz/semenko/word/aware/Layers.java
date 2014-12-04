package cz.semenko.word.aware;

import java.util.List;
import java.util.Vector;

import cz.semenko.word.persistent.Cell;

/**
 * Container that represents a tree of related {@link cz.semenko.word.persistent.Cell} objects.<br>
 * Zero level of Layers contains {@link cz.semenko.word.persistent.Cell} IDs that has been read from source in one hit,
 * see {@link cz.semenko.word.Config#dataProvider_numCharsReadsFromInput}<br><br>
 * 
 * Positions of Cells to relation: <br>
 * Level 0: 1-2, 2-3, 3-4, 4-5, 5-6, 6-7, 7-8, 8-9... Step is 0.<br>
 * Level 1: 1-3, 2-4, 3-5, 4-6, 5-7, 6-8, 7-9, 8-10... Step is 1. Else it does'nt make a sense. Always skip one cell (one step).<br>
 * In this case pairs 1-3, 3-5, 5-7, 7-9 belongs to Mutation 0 <br>
 * And according pairs 2-4, 4-6, 6-8, 8-10 belongs to Mutation 1 <br>
 * Level 2: 1-5, 2-6 and so on. Always skip three cells (three steps).<br>
 * Level 3: 1-9, 2-10, 3-11. Always skip seven cells (seven steps).<br>
 * For example of reading ABCDEFGHIJKLMNOPQR from source:
 * <pre>{@code

*************************************************************************************************************
Level 0 Mutation 0
         A    B    C    D    E    F    G    H    I    J    K    L    M    N    O    P    Q    R
          \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  /
           AB   BC   CD   DE   EF   FG   GH   HI   IJ   JK   KL   LM   MN   NO   OP   PQ   QR
*************************************************************************************************************
Level 1 Mutation 0
         AB  BC  CD  DE  EF  FG  GH  HI  IJ  JK  KL  LM  MN  NO  OP  PQ  QR
           \    /  \    /  \    /  \    /  \    /  \    /  \    /  \    /
            ABCD    CDEF    EFGH    GHIJ    IJKL    KLMN    MNOP    OPQR
Level 1 Mutation 1
         AB  BC  CD  DE  EF  FG  GH  HI  IJ  JK  KL  LM  MN  NO  OP  PQ  QR
               \    /  \    /  \    /  \    /  \    /  \    /  \    /
                BCDE    DEFG    FGHI    HIJK    JKLM    LMNO    NOPQ
*************************************************************************************************************
Level 2 Mutation 0
         ABCD BCDE CDEF DEFG EFGH FGHI GHIJ HIJK IJKL JKLM KLMN LMNO MNOP NOPQ OPQR
             \              /    \              /    \              /
                 ABCDEFGH            EFGHIJKL            IJKLMNOP
Level 2 Mutation 1
         ABCD BCDE CDEF DEFG EFGH FGHI GHIJ HIJK IJKL JKLM KLMN LMNO MNOP NOPQ OPQR
                 \                /  \                /  \                /
                      BCDEFGHI            FGHIJKLM             JKLMNOPQ
Level 2 Mutation 2
         ABCD BCDE CDEF DEFG EFGH FGHI GHIJ HIJK IJKL JKLM KLMN LMNO MNOP NOPQ OPQR
                      \                /  \                /  \                 /
                           CDEFGHIJ            GHIJKLMN             KLMNOPQR
Level 2 Mutation 3
         ABCD BCDE CDEF DEFG EFGH FGHI GHIJ HIJK IJKL JKLM KLMN LMNO MNOP NOPQ OPQR
                           \                /  \                /
                                 DEFGHIJK           HIJKLMNO
*************************************************************************************************************
Level 3 Mutation 0
         ABCDEFGH BCDEFGHI CDEFGHIJ DEFGHIJK EFGHIJKL FGHIJKLM GHIJKLMN HIJKLMNO IJKLMNOP JKLMNOPQ KLMNOPQR
                \                                                                /
                                        ABCDEFGHIJKLMNO
Level 3 Mutation 1
         ABCDEFGH BCDEFGHI CDEFGHIJ DEFGHIJK EFGHIJKL FGHIJKLM GHIJKLMN HIJKLMNO IJKLMNOP JKLMNOPQ KLMNOPQR
                         \                                                                /
                                                BCDEFGHIJKLMNOPQ
Level 3 Mutation 2
         ABCDEFGH BCDEFGHI CDEFGHIJ DEFGHIJK EFGHIJKL FGHIJKLM GHIJKLMN HIJKLMNO IJKLMNOP JKLMNOPQ KLMNOPQR
                                  \                                                                /
                                                        CDEFGHIJKLMNOPQR


 *************************************************************************************************************

Or another view on the same data 

**************************************************************************************************************
Level 0 Mutation 0
         A    B    C    D    E    F    G    H    I    J    K    L    M    N    O    P    Q    R
          \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  / \  /
           AB   BC   CD   DE   EF   FG   GH   HI   IJ   JK   KL   LM   MN   NO   OP   PQ   QR
**************************************************************************************************************
Level 1 Mutation 0
         AB      CD      EF      GH      IJ      KL      MN      OP      QR
           \    /  \    /  \    /  \    /  \    /  \    /  \    /  \    /
            ABCD    CDEF    EFGH    GHIJ    IJKL    KLMN    MNOP    OPQR
Level 1 Mutation 1
         BC      DE      FG      HI      JK      LM      NO      PQ    
           \    /  \    /  \    /  \    /  \    /  \    /  \    /
            BCDE    DEFG    FGHI    HIJK    JKLM    LMNO    NOPQ
**************************************************************************************************************
Level 2 Mutation 0
         ABCD          EFGH          IJKL          MNOP          
             \        /    \        /    \        /
              ABCDEFGH      EFGHIJKL      IJKLMNOP
Level 2 Mutation 1
		 BCDE          FGHI          JKLM          NOPQ     
		     \        /    \        /    \        /
		      BCDEFGHI      FGHIJKLM      JKLMNOPQ
Level 2 Mutation 2
	     CDEF          GHIJ          KLMN          OPQR
	         \        /    \        /    \        /
	          CDEFGHIJ      GHIJKLMN      KLMNOPQR
Level 2 Mutation 3
         DEFG          HIJK          LMNO                
             \        /    \        /
              DEFGHIJK      HIJKLMNO
**************************************************************************************************************
Level 3 Mutation 0
         ABCDEFGH                  IJKLMNOP
                 \                /
                  ABCDEFGHIJKLMNOP
Level 3 Mutation 1
         BCDEFGHI                  JKLMNOPQ 
                 \                /
                  BCDEFGHIJKLMNOPQ
Level 3 Mutation 2
         CDEFGHIJ                  KLMNOPQR
                 \                /
                  CDEFGHIJKLMNOPQR


               0 - step in the level 0 (skip 0 position)
 (0 * 2) + 1 = 1 - step in the level 1 (skip 1 position)
 (1 * 2) + 1 = 3 - step in the level 2 (skip 3 positions)
 (3 * 2) + 1 = 7 - step in the level 3 (skip 7 positions)
 (7 * 2) + 1 = 15 - step in the level 4 (skip 15 positions)
 (15 * 2) + 1 = 31 - step in the level 5 (skip 31 positions)
 and so on according to the pattern (x * 2) + 1 = y where 'x' is the step from previous level, and 'y' is the step in the current level
 * }</pre>
 * <br>
 * 
 * @author Kyrylo Semenko
 *
 */
public class Layers {
	private static final String LINKER_TO_LEFT = "/";
	private static final String LINKER_TO_RIGHT = "\\";
	private static final String ONE_GAP = " ";
	private static final String GAP_OF_NULL_VALUE = "    ";

	/** Container of {@link Cell} IDs*/
	private Vector<Vector<Long>> layers = new Vector<Vector<Long>>();
	
	/**
	 * Add layer at the end.
	 */
	public void add(Vector<Long> layer) {
		layers.add(layer);
	}

	/**
	 * Get layer from position
	 * @param layerPosition position of layer. Layer on the top position has index 0.
	 */
	public Vector<Long> get(int layerPosition) {
		return layers.get(layerPosition);
	}

	/**
	 * Returns number of layers.
	 */
	public int size() {
		return layers.size();
	}

	/**
	 * Returns last layer.
	 */
	public Vector<Long> lastElement() {
		return layers.lastElement();
	}

	/**
	 * Replaces the element at the specified position in this Vector with the specified element.
	 * @param index  of the element to replace
	 * @param layerDataToPast data to be stored at the specified position
	 */
	public void set(int index, Vector<Long> layerDataToPast) {
		layers.set(index, layerDataToPast);
	}

	/**
	 * Removes the element at the specified position in this Vector.<br>
	 * Shifts any subsequent levels to upward.
	 * @param index the index of the element to be removed
	 */
	public void remove(int index) {
		layers.remove(index);
		
	}

	@Override
	public String toString() {
		if (layers.size() < 2) {
			return interspaceOfOneGap();
		}
		StringBuilder result = new StringBuilder();
		// For every level
		for (int level = 0; level < layers.size() - 1; level++) {
			int step = mersenne(level);
			// For every mutation
			int mutationsNumber = calculateMutationsNumber(layers.get(level).size(), step);
			for (int mutationNum = 0; mutationNum < mutationsNumber; mutationNum++) {
				List<Long> parentLayer = layers.get(level);
				List<Long> childLayer = layers.get(level + 1);
				StringBuilder firstRow = new StringBuilder();
				StringBuilder secondRow = new StringBuilder();
				StringBuilder thirdRow = new StringBuilder();
				// Calculate positions of linkers relatively to parentLayer
				List<Integer> linkerPositions = new Vector<Integer>();
				for (int nextParentPosition = mutationNum; nextParentPosition < parentLayer.size(); nextParentPosition++) {
					linkerPositions.add(nextParentPosition);
					nextParentPosition += step;
				}
				for (int nextParentPosition = mutationNum; nextParentPosition < parentLayer.size(); nextParentPosition++) {
					if (!linkerPositions.contains(nextParentPosition)) {
						continue;
					}
					firstRow.append(parentLayer.get(nextParentPosition));
					if (childLayer.size() > nextParentPosition) {
						secondRow.append(getBlank(parentLayer.get(nextParentPosition)));
						thirdRow.append(getBlank(parentLayer.get(nextParentPosition)));
						
						firstRow.append(ONE_GAP);
						secondRow.append(LINKER_TO_RIGHT);
						thirdRow.append(ONE_GAP);
						
						firstRow.append(getBlank(childLayer.get(nextParentPosition)));
						secondRow.append(getBlank(childLayer.get(nextParentPosition)));
						thirdRow.append(childLayer.get(nextParentPosition));
						
						firstRow.append(ONE_GAP);
						secondRow.append(LINKER_TO_LEFT);
						thirdRow.append(ONE_GAP);
					}
				}
				result.append("Level: " + level + ", Mutation: " + mutationNum);
				result.append("\r\n");
				result.append(firstRow);
				result.append("\r\n");
				result.append(secondRow);
				result.append("\r\n");
				result.append(thirdRow);
				result.append("\r\n");
			}
			result.append("***********************************************************************\r\n");
		}
		return result.toString();
	}
	
	/**
	 * Calculates a number of mutations for layer<br>
	 * For example<br>
	 * size = 18, step = 1, result = 2<br>
	 * size = 15, step = 3, result = 4<br>
	 * size = 11, step = 7, result = 3<br>
	 * size = 14, step = 7, result = 6<br>
	 * @param size of the layer
	 * @param step how many IDs will be skip in conjunction process for each mutation
	 */
	private int calculateMutationsNumber(int size, int step) {
		if ((step + 1) * 2 <= size) {
			return step + 1;
		}
		return (size % (step + 2)) + 1;
	}

	/**
	 * Returns empty String with number of gaps equals to length of the toString(nextId)
	 */
	private Object getBlank(Long nextId) {
		if (nextId == null) {
			return GAP_OF_NULL_VALUE;
		}
		return nextId.toString().replaceAll(".", ONE_GAP);
	}

	/** Calculates Mersenne number<br> 
	 * for level 0 is 0 <br>
	 * for level 1 is 1 <br>
	 * for level 2 is 3 <br>
	 * for level 3 is 7 <br>
	 * for level 4 is 15 <br>
	 **/
	private int mersenne(int level) {
		if (level == 0) {
			return 0;
		}
	    int result = 0;
	    for (int i = 0; i < level; i++) {
	    	result = result * 2 + 1;
	    }
	    return result;
	}
	
	/** 
	 * If layers is empty, returns "[]"<br>
	 * Else interspace first level's IDs of one gap. 
	 */
	private String interspaceOfOneGap() {
		if (layers.size() == 0) {
			return "[]";
		}
		StringBuilder result = new StringBuilder();
		for (Long nextId : layers.get(0)) {
			result.append(nextId);
			result.append(ONE_GAP);
		}
		return result.toString();
	}
}