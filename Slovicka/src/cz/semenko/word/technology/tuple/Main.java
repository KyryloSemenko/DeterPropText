package cz.semenko.word.technology.tuple;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Kyrylo Semenko
 *
 */
public class Main {
	public static void main(String[] args) {
		Map<Tuple, Long> statistics = new TreeMap<>();
		
		InvariantsTokenizer invariantsTokenizer = new InvariantsTokenizer();
		List<Tuple> tuples = invariantsTokenizer.tuples("abrakadabra");
		
		for (Tuple tuple : tuples) {
			Long num = statistics.get(tuple);
			if (num == null) {
				statistics.put(tuple, 1L);
			} else {
				statistics.put(tuple, num + 1L);
			}
		}
		
		System.out.println(statistics);
		
		Map<Tuple, Long> neighbours = new TreeMap<>();
		
		for (Tuple tuple : statistics.keySet()) {
			if (tuple.getSrc().equals("br") || tuple.getTgt().equals("br")) {
				neighbours.put(tuple, statistics.get(tuple));
			}
		}
		System.out.println("\r\nneighbours: " + neighbours);
		
		Map<Tuple, Long> similar = new TreeMap<>();
		for (Tuple neighbour : neighbours.keySet()) {
			if (neighbour.getSrc().equals("br")) {
				String tgt = neighbour.getTgt();
				for (Tuple tuple : statistics.keySet()) {
					if (tuple.getTgt().equals(tgt) && !tuple.getSrc().equals("br")) {
						System.out.println(tuple.getSrc() + " " + statistics.get(tuple));
						similar.put(tuple, statistics.get(tuple));
					}
				}
			} else {
				String src = neighbour.getSrc();
				for (Tuple tuple : statistics.keySet()) {
					if (tuple.getSrc().equals(src) && !tuple.getTgt().equals("br")) {
						System.out.println(tuple.getTgt() + " " + statistics.get(tuple));
						similar.put(tuple, statistics.get(tuple));
					}
				}
			}
		}
		System.out.println("\r\nsimilar: " + similar);
		
	}

}
