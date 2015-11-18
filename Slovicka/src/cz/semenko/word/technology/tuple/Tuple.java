package cz.semenko.word.technology.tuple;

import org.apache.commons.lang.builder.CompareToBuilder;

/**
 * DTO object. Represents pair of two strings. First string followed by second string. 
 * @author Kyrylo Semenko
 *
 */
public class Tuple implements Comparable<Tuple> {
	/** First token in pair */
	private String src;
	
	/** Second token in pair */
	private String tgt;
	
	/** Empty constructor */
	public Tuple() {}
	
	/** Constructor */
	public Tuple(String src, String tgt) {
		setSrc(src);
		setTgt(tgt);
	}
	
	@Override
	public String toString() {
		return src + ":" + tgt;
	}

	@Override
	public int compareTo(Tuple o) {
		if (o == null) {
			return 1;
		}
		return new CompareToBuilder()
			.append(this.getSrc(), o.getSrc())
			.append(this.getTgt(), o.getTgt())
			.toComparison();
	}
	
// getters and setters //
	
	/**
	 * See {@link Tuple#src}
	 */
	public String getSrc() {
		return src;
	}

	/**
	 * See {@link Tuple#src}
	 */
	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * See {@link Tuple#tgt}
	 */
	public String getTgt() {
		return tgt;
	}

	/**
	 * See {@link Tuple#tgt}
	 */
	public void setTgt(String tgt) {
		this.tgt = tgt;
	}

}
