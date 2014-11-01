package cz.semenko.word.aware;

import java.util.Vector;

/**
 * Reprezentace vzniku variantu moznych vzruseni, ktere prolinaji ruznymy vrstvamy.
 * Jako pomucku predstavim si vrstvy. V nulove vrstve jsou
 * zastoupene jednotlive znaky. Z vrstvy znaku hledame spojeni pro
 * kazdy par objektu.
 * Z prvni vrstvy hledame spojeni jen pro
 * objekty pres jeden - v sachovem poradi. Jinak to nedava smysl. Vzdy vynechame jeden objekt.
 * Z druhe vrstvy hledame spojeni pro 1-5, 2-6 a tak dale. Vzdy vynechame tri objekty.
 * Z treti vrstvy hledame spojeni pro 1-9, 2-10, 3-11. Vzdy vynechame sedum objektu.
 * Vrstva znaku: 1, 2, 3, 4, 5, 6, 7, 8, 9, 1a, 2a, 3a, 4a, 5a, 6a, 7a, 8a, 9a.
 * Prvni vrstva: 12, 23, 34, 45, 56, 67, 78, 89, 91a, 1a2a, 2a3a, 3a4a, 4a5a, 5a6a, 6a7a, 7a8a, 8a9a.
 * Druha vrstva: 1234, 2345, 3456, 4567, 5678, 6789, 7891a, 891a2a, 91a2a3a, 1a2a3a4a, 2a3a4a5a, 3a4a5a6a, 4a5a6a7a, 5a6a7a8a, 6a7a8a9a.
 * Treti vrstva: 12345678, 23456789, 34567891a, 4567891a2a, 567891a2a3a, 67891a2a3a4a, 7891a2a3a4a5a, 891a2a3a4a5a6a, 91a2a3a4a5a6a7a, 1a2a3a4a5a6a7a8a, 2a3a4a5a6a7a8a9a.
 * Ctvrta vrstva: 1234567891a2a3a4a5a6a7a, 234567891a2a3a4a5a6a7a8a, 34567891a2a3a4a5a6a7a8a9a.
 * (1 * 2) + 1 = 3 - druha vrstva
 * (3 * 2) + 1 = 7 - treti vrstva
 * (7 * 2) + 1 = 15 - ctvrta vrstva
 * (15 * 2) + 1 = 31 - pata vrstva a tak dale
 * (x * 2) + 1 = y
 * Pozor, objekt neni pripraven pro vicevlaknove pouziti.
 *
 * @author k
 * @version $Id: $Id
 */
public class Layers {
	/** Prave aktivni vrstva a konstanta */
	private int currentLayer = -1;
	/** Konstanty pro kazdou vrstvu, ktere urcuji, kolikate elementy ve vrstve podlehaji spojovani */
	private Vector<Integer> constants = new Vector<Integer>();
	/** Vektor vektoru se samotnymi ID objektu */
	private Vector<Vector<Long>> layers = new Vector<Vector<Long>>();
	/** Pravidlo, ktere urcuje jak se meni konstanty od vrstvy k vrstve. */
	private String rule = "x*2";
	/** Vychozi konstanta */
	private int currentConstant = 0;

	/**
	 * <p>Constructor for Layers.</p>
	 */
	public Layers() {
		;
	}
	
	/**
	 * <p>Getter for the field <code>rule</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRule() {
		return rule;
	}
	
	/**
	 * Prida prazdnou vrstvu na konec vektoru vrstev
	 */
	public void addLayer() {
		// Definujeme konstantu pro tuto vrstvu
		if (currentConstant == 0) {
			currentConstant = 1;
		} else {
			currentConstant = currentConstant * 2;
		}
		constants.add(currentConstant);
		// Pridame vrstvu
		Vector<Long> layer = new Vector<Long>();
		layers.add(layer);
		currentLayer++;
	}
	
	/**
	 * <p>getFirstLayer.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getFirstLayer() {
		currentLayer = 0;
		return layers.get(currentLayer);
	}
	
	/**
	 * <p>Getter for the field <code>currentConstant</code>.</p>
	 *
	 * @return a int.
	 */
	public int getCurrentConstant() {
		return constants.get(currentLayer);
	}
	
	/**
	 * <p>Getter for the field <code>currentLayer</code>.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getCurrentLayer() {
		if (currentLayer == -1) {
			return null;
		}
		return layers.get(currentLayer);
	}
	
	/**
	 * <p>getNextLayer.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getNextLayer() {
		if (currentLayer >= layers.size()-1) {
			return null;
		}
		currentLayer++;
		return layers.get(currentLayer);
	}
	
	/**
	 * <p>getPreviousLayer.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getPreviousLayer() {
		if (currentLayer <= 0) {
			return null;
		}
		currentLayer--;
		return layers.get(currentLayer);
	}
	
	/**
	 * <p>addId.</p>
	 *
	 * @param id a {@link java.lang.Long} object.
	 */
	public void addId(Long id) {
		if (currentLayer == -1) {
			addLayer();
		}
		layers.get(currentLayer).add(id);
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < layers.size(); i++) {
			Vector<Long> nextLayer = layers.get(i);
			for (int k = 0; k < nextLayer.size(); k++) {
				buff.append(nextLayer.get(k) + "\t");
			}
			buff.delete(buff.length()-2, buff.length());
			buff.append("\r\n");
		}
		return buff.toString();
	}
	/**
	 * Zjisti, zda posledni vrstva ma elementy vhodne pro vyhledavani vyssich asociaci.
	 *
	 * @return a boolean.
	 */
	public boolean hasLastLayerPairs() {
		boolean hasPair = false;
		int util = 0;
		Vector<Long> lastLayer = layers.lastElement();
		int constant = constants.lastElement();
		for (int i = 0; i < lastLayer.size(); i = i + constant) {
			Long ass = lastLayer.get(i);
			if (ass != null) {
				util++;
			} else {
				util--;
			}
			if (util > 1) {
				return true;
			}
			if (util < 0) {
				util = 0;
			}
		}
		return hasPair;
	}

	/**
	 * <p>setLastLayer.</p>
	 *
	 * @param superiorLayer a {@link java.util.Vector} object.
	 */
	public void setLastLayer(Vector<Long> superiorLayer) {
		addLayer();
		layers.set(currentLayer, superiorLayer);
	}

	/**
	 * <p>removeLastLayer.</p>
	 */
	public void removeLastLayer() {
		layers.remove(layers.size()-1);
		currentLayer--;
		currentConstant = currentConstant/2;
		constants.remove(constants.size()-1);
	}

	/**
	 * <p>size.</p>
	 *
	 * @return a int.
	 */
	public int size() {
		return layers.size();
	}

	/**
	 * <p>get.</p>
	 *
	 * @param i a int.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> get(int i) {
		currentLayer = i;
		currentConstant = constants.get(i);
		return layers.get(i);
	}

	/**
	 * Sestavi nejkratsi moznou posloupnost IDecek objektu.
	 * Zacina zespodu. Pro prvni object najde spicku. Zjisti kolik
	 * objektu z prvni rady ma vynechat a pokracuje od dalsiho.
	 *
	 * @return array ID objektu.
	 */
	public Long[] getHighlyCells() {
		Vector<Long> result = new Vector<Long>();
		//removeLastLayer(); // Byla to chyba
		Vector<Long> firstLayer = layers.get(0);
		for (int i = 0; i < firstLayer.size(); i++) {
			int bottomObjectPos = i;
			Long topObjectId = getTopObjectId(bottomObjectPos);
			if (topObjectId == null) {
				result.add(firstLayer.get(i));
				continue;
			}
			result.add(topObjectId);
			i = getLastObjectPos(bottomObjectPos) - 1;
		}
		Long[] arr = new Long[result.size()];
		arr = result.toArray(arr);
		return arr;
	}

	/**
	 * Nalezne pozici prvniho nasledujiciho objektu v layeru, ktery
	 * ma spojeni s objektem v parametru.
	 * @param bottomObjectPos
	 * @return int - pozice objektu.
	 */
	private int getLastObjectPos(int bottomObjectPos) {
		int result = bottomObjectPos;
		for (int i = 1; i < layers.size(); i++) {
			Vector<Long> nextLayer = get(i);
			int currentConstant = getCurrentConstant();
			if (bottomObjectPos >= nextLayer.size()) {
				return result;
			}
			Long id = nextLayer.get(bottomObjectPos);
			if (id != null) { // nadrazeny objekt existuje, zkoumame dalsi layer
				result = bottomObjectPos + currentConstant;
				continue;
			} else { // nadrazeny objekt neexistuje
				return result;
			}
		}
		return result;
	}

	/**
	 * Nalezne nejvyssi object pro objekt z parametru.
	 * @param bottomObjectPos
	 * @return Long
	 */
	private Long getTopObjectId(int bottomObjectPos) {
		Long result = null;
		for (int i = 1; i < layers.size(); i++) {
			Vector<Long> nextLayer = get(i);
			//int currentConstant = getCurrentConstant();
			//int nextLayerObjectPos = bottomObjectPos;
			if (bottomObjectPos >= nextLayer.size()) {
				return result;
			}
			Long id = nextLayer.get(bottomObjectPos);
			if (id != null) { // nadrazeny objekt existuje
				//bottomObjectPos = nextLayerObjectPos;
				result = id;
				continue;
			} else { // nadrazeny objekt neexistuje
				return result;
			}
		}
		return result;
	}
}
