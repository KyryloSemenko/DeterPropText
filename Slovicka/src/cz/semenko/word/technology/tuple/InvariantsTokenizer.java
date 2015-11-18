package cz.semenko.word.technology.tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Find out all pairs of unique invariants.<br>
 * For example word <i>one</i> has next invariants: [n:e, o:n, o:ne, on:e]<br>
 * And word <i>abcde</i> has next invariants: [d:e, c:d, c:de, cd:e, b:c, b:cd, b:cde, bc:d, bc:de, bcd:e, a:b, a:bc, a:bcd, a:bcde, ab:c, ab:cd, ab:cde, abc:d, abc:de, abcd:e]
 * @author Kyrylo Semenko
 */
public class InvariantsTokenizer {
	public List<Tuple> tuples(String text) {
//		System.out.println(text + ":" + text.length());
		
		List<Tuple> tuples = new ArrayList<Tuple>();
		
		for (int i = text.length() - 2; i >= 0; i--) {
			int srcStartPos = i;
			
			int maxSrcLength = text.length() - srcStartPos - 1;
			for (int srcLength = 1; srcLength <= maxSrcLength; srcLength++) {
				int maxTgtLength = text.length() - srcStartPos - srcLength;
				for (int tgtLength = 1; tgtLength <= maxTgtLength; tgtLength++) {
					String src = text.substring(srcStartPos, srcStartPos + srcLength);
					int tgtStartPos = srcStartPos + src.length();
					String tgt = text.substring(tgtStartPos, tgtStartPos + tgtLength);
					
					Tuple tuple = new Tuple();
					tuple.setSrc(src);
					tuple.setTgt(tgt);
					tuples.add(tuple);
				}
			}
		}
//		System.out.println(tuples.size());
//		System.out.println(tuples);
		return tuples;
	}
}


