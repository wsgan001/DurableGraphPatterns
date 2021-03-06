package graph.pattern;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import system.Config;

/**
 * Class of pattern node
 * 
 * @author ksemer
 */
public class PatternNode implements Serializable {

	// =================================================================

	private static final long serialVersionUID = 1L;
	private int id;
	private int label;
	private List<PatternNode> adjacency;
	private List<Set<Integer>> labelAdjacency;
	private List<Map<Integer, Integer>> labelAdjacency_C;

	// =================================================================

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param label
	 */
	public PatternNode(int id, int label) {

		this.id = id;
		this.label = label;
		this.adjacency = new ArrayList<PatternNode>();
		initializeNeighborIndexes();
	}

	/**
	 * Initialize TiNLa & CTiNLa structures
	 */
	public void initializeNeighborIndexes() {
		if (Config.TINLA_ENABLED) {

			this.labelAdjacency = new ArrayList<>();

			for (int i = 0; i < Config.TINLA_R; i++)
				this.labelAdjacency.add(i, new HashSet<Integer>());

		} else if (Config.CTINLA_ENABLED) {

			this.labelAdjacency_C = new ArrayList<>();

			for (int i = 0; i < Config.CTINLA_R; i++)
				this.labelAdjacency_C.add(i, new HashMap<>());
		}
	}

	/**
	 * Add edge this->trg
	 * 
	 * @param trg
	 */
	public void addEdge(PatternNode trg) {
		adjacency.add(trg);
	}

	/**
	 * Return id
	 * 
	 * @return
	 */
	public int getID() {
		return id;
	}

	/**
	 * Return node's label
	 * 
	 * @return
	 */
	public int getLabel() {
		return label;
	}

	/**
	 * Get adjacency
	 * 
	 * @return
	 */
	public List<PatternNode> getAdjacency() {
		return adjacency;
	}

	/**
	 * Get TiNLa
	 * 
	 * @return
	 */
	public Set<Integer> getTiNLa(int r) {
		return labelAdjacency.get(r);
	}

	/**
	 * Get CTiNLa
	 * 
	 * @param r
	 * @return
	 */
	public Map<Integer, Integer> getCTiNLa(int r) {
		return labelAdjacency_C.get(r);
	}
}