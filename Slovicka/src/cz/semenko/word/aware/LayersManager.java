package cz.semenko.word.aware;

import java.util.Arrays;
import java.util.Vector;

import cz.semenko.word.persistent.Cell;

/**
 * Manager of {@link Layers} object.
 */
public class LayersManager {
	/** Container of {@link Cell} IDs. See {@link Layers} */
	private Layers layers;
	/** Layers starts from layer 0, that is IDs of {@link cz.semenko.word.persistent.Cell} objects that has been read in one hit from source (for example file),
	 * see {@link cz.semenko.word.Config#dataProvider_numCharsReadsFromInput}<br> */
	private int currentLayerLevel;
	/** Positions of elements in layer which has to be united to a new {@link cz.semenko.word.persistent.Cell} in the next layer */
	private Vector<Integer> positionsForUnion = new Vector<Integer>();
	/** Current {@link cz.semenko.word.aware.LayersManager#positionsForUnion} */
	private int currentPositionForUnion = 0;
	

	/**
	 * <p>Empty constructor for LayersManager.</p>
	 */
	public LayersManager() {}
	
	/**
	 * Adds empty layer to the end of the {@link Layers} object
	 */
	public void addLayer() {
		if (currentPositionForUnion == 0) {
			currentPositionForUnion = 1;
		} else {
			currentPositionForUnion = currentPositionForUnion * 2;
		}
		positionsForUnion.add(currentPositionForUnion);
		Vector<Long> layer = new Vector<Long>();
		layers.add(layer);
		currentLayerLevel++;
	}
	
	/**
	 * <p>getFirstLayer.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getFirstLayer() {
		currentLayerLevel = 0;
		return layers.get(currentLayerLevel);
	}
	
	/**
	 * <p>Getter for the field <code>currentPositionForUnion</code>.</p>
	 *
	 * @return a int.
	 */
	public int getCurrentPositionForUnion() {
		return positionsForUnion.get(currentLayerLevel);
	}
	
	/**
	 * <p>Getter for the field <code>currentLayerLevel</code>.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getCurrentLayer() {
		if (currentLayerLevel == -1) {
			return null;
		}
		return layers.get(currentLayerLevel);
	}
	
	/**
	 * <p>getNextLayer.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getNextLayer() {
		if (currentLayerLevel >= layers.size()-1) {
			return null;
		}
		currentLayerLevel++;
		return layers.get(currentLayerLevel);
	}
	
	/**
	 * <p>getPreviousLayer.</p>
	 *
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> getPreviousLayer() {
		if (currentLayerLevel <= 0) {
			return null;
		}
		currentLayerLevel--;
		return layers.get(currentLayerLevel);
	}
	
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
	 * Finds out, if last layer has nonzero IDs abreast (next to each other).
	 * @return a boolean true, if two IDs lies next to each other.
	 */
	public boolean lastLayerHasPairs() {
		boolean hasPair = false;
		int numberOfNotNulIds = 0;
		Vector<Long> lastLayer = layers.lastElement();
		System.out.println(layers);
		int positionForUnion = positionsForUnion.lastElement();
		for (int i = 0; i < lastLayer.size(); i = i + positionForUnion) {
			Long nextId = lastLayer.get(i);
			if (nextId != null) {
				numberOfNotNulIds++;
			} else {
				numberOfNotNulIds--;
			}
			if (numberOfNotNulIds > 1) {
				return true;
			}
			if (numberOfNotNulIds < 0) {
				numberOfNotNulIds = 0;
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
		layers.set(currentLayerLevel, superiorLayer);
	}

	/**
	 * <p>removeLastLayer.</p>
	 */
	public void removeLastLayer() {
		layers.remove(layers.size()-1);
		currentLayerLevel--;
		currentPositionForUnion = currentPositionForUnion/2;
		positionsForUnion.remove(positionsForUnion.size()-1);
	}

	/**
	 * <p>Size of layers</p>
	 *
	 * @return a int.
	 */
	public int size() {
		return layers.size();
	}

	/**
	 * Get layer from position
	 *
	 * @param i a int.
	 * @return a {@link java.util.Vector} object.
	 */
	public Vector<Long> get(int i) {
		currentLayerLevel = i;
		currentPositionForUnion = positionsForUnion.get(i);
		return layers.get(i);
	}

	/**
	 * Finds out non zero IDs lies in lowest layers of the {@link Layers} object
	 * @return array of {@link Cell} IDs.
	 */
	public Long[] getBottomCells() {
		Vector<Long> result = new Vector<Long>();
		Vector<Long> firstLayer = layers.get(0);
		for (int i = 0; i < firstLayer.size(); i++) {
			int firstLayerCellPosition = i;
			Long topObjectId = getLastObjectId(firstLayerCellPosition);
			if (topObjectId == null) {
				result.add(firstLayer.get(i));
				continue;
			}
			result.add(topObjectId);
			i = getLastObjectPos(firstLayerCellPosition) - 1;
		}
		Long[] arr = new Long[result.size()];
		arr = result.toArray(arr);
		return arr;
	}

	/**
	 * Finds out position of child ID in lowest layer
	 * @param parentIdPosition
	 * @return int position of lowest child ID
	 */
	private int getLastObjectPos(int parentIdPosition) {
		int result = parentIdPosition;
		for (int i = 1; i < layers.size(); i++) {
			Vector<Long> nextLayer = get(i);
			int currentPositionForUnion = getCurrentPositionForUnion();
			if (parentIdPosition >= nextLayer.size()) {
				return result;
			}
			Long id = nextLayer.get(parentIdPosition);
			if (id != null) { // bottom (child) id exists, lets go to find in next layer
				result = parentIdPosition + currentPositionForUnion;
				continue;
			} else { // bottom (child) id does not exists, this is last non zero id
				return result;
			}
		}
		return result;
	}

	/**
	 * Finds out id of a {@link Cell} that lies lowest in all levels for {@link Cell} from first layer.
	 * @param firstLayerCellPosition position of {@link Cell} in first layer.
	 * @return id of {@link Cell} related to {@link Cell} from parameter.
	 */
	private Long getLastObjectId(int firstLayerCellPosition) {
		Long result = null;
		for (int i = 1; i < layers.size(); i++) {
			Vector<Long> nextLayer = get(i);
			if (firstLayerCellPosition >= nextLayer.size()) {
				return result;
			}
			Long id = nextLayer.get(firstLayerCellPosition);
			if (id != null) { // bottom (child) id exists, lets go to find in next layer
				result = id;
				continue;
			} else { // bottom (child) id does not exists, this is last non zero id
				return result;
			}
		}
		return result;
	}

	/**
	 * Sets IDs of {@link cz.semenko.word.persistent.Cell} to first layer.
	 */
	public void setFirstLayer(Long[] inputCells) {
		currentLayerLevel = -1;
		layers = new Layers();
		addLayer();
		layers.get(currentLayerLevel).addAll(Arrays.asList(inputCells));
	}
}
