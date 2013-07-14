package cz.semenko.word.persistent;

import org.apache.log4j.Logger;

/**
 * Objects  class is data transfer object. Represent a connection of two Associations.
 * @author Kyrylo Semenko
 *
 */
public class Objects {
	private Long id;
	private String src;
	private Long type;
	Logger logger = Logger.getLogger(Objects.class);
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Objects)) return false;
		Objects that = (Objects)obj;
		// Pro zrychleni
//		if (this.getId() == that.getId()) {
//			return true;
//		}
		/** Muze dochazet k ruznym chybam. Objects se stejnym ID bude mit jine hodnoty,
		 * nebo Objects s ruznymi ID budou mit stejne hodnoty.
		 * Overim oba pripady, ale nevim jak rychle bude fungovat tato metoda.
		 * TODO overit rychlost metody.
		 * TODO az bude odladen beh, odstranit overovani duplicit pro vetsi rychlost.
		 */
		if (this.getId().equals(that.getId())) {
			if (this.getSrc().compareTo(that.getSrc()) != 0 
					|| this.getType().equals(that.getType()) == false) {
				String error = ("Objecty maji stejne ID, ale ruzny obsah. ID:" + this.getId());
				logger.error(error);
				System.out.println(error);
				System.exit(1);
			}
			return true;
		} else {
			/*if (this.getSrc().compareTo(that.getSrc()) == 0) {
				String error = ("Objecty maji ruzne ID, ale stejny obsah. ID1: " + this.getId()
						+ ", ID2: " + that.getId());
				logger.error(error);
				System.out.println(error);
				System.exit(1);
			}*/
		}
		return false;
	}
	
	public Objects(Long id, String src, Long type) {
		super();
		this.id = id;
		this.src = src;
		this.type = type;
	}
	
	/**
	 * <p>Empty constructor</p>
	 */
	public Objects() {
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Long getType() {
		return type;
	}
	
	public void setType(Long type) {
		this.type = type;
	}
	
	public String toString() {
		return ("src '" + src + "', type " + type + ", id " + id);
	}
}
