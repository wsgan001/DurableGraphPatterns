package system;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the System's Configurations
 * 
 * @author ksemer
 */
public final class Config {
	// ====================================================

	// max ranking identifier
	public final static int MAX_RANKING = 1;

	// halfway ranking identifier
	public final static int HALFWAY_RANKING = 2;

	// zero ranking identifier
	public final static int ZERO_RANKING = 3;

	// ====================================================

	// enable load of serialized graph and index object
	public static boolean LOAD_OBJECT;

	// store all objects in a file
	public static boolean STORE_OBJECT;

	// path of file that stores all objects
	public static String PATH_OBJECT;

	// path of graph dataset
	public static String PATH_DATASET;

	// path of labels
	public static String PATH_LABELS;

	// output directory path
	public static String PATH_OUTPUT;

	// path with all input queries
	public static String PATH_QUERY;

	// dataset direction
	public static boolean ISDIRECTED;

	// for matches with contiguous duration
	public static boolean CONTIGUOUS_MATCHES;

	public static boolean RUN_RANDOM;

	// Run topk algorithm
	public static boolean RUN_TOPK_QUERIES;

	// Run durable algorithm
	public static boolean RUN_DURABLE_QUERIES;

	// maximum interval of graph lifespan
	public static int MAXIMUM_INTERVAL;

	// # of labels in the dataset
	public static int SIZE_OF_LABELS;

	public static int RANDOM_ITERATIONS;

	// show the memory info of structures
	public static boolean SHOW_MEMORY;

	// for zero ranking
	public static boolean ZERO_RANKING_ENABLED;

	// for binary/halfway ranking execution
	public static boolean HALFWAY_RANKING_ENABLED;

	// for max ranking execution
	public static boolean MAX_RANKING_ENABLED;

	// maximum matches that we want to get for debug purpose in DurableQueries
	public static int MAX_MATCHES;

	// time limit in seconds for algorithm's execution
	public static int TIME_LIMIT;

	// k matches for Durable Topk Queries
	public static int K;

	// candidates percent for halfway strategy
	public static float CP;

	// nodes' labels change over time
	public static boolean LABEL_CHANGE;

	// enable TiNLa
	public static boolean TINLA_ENABLED;

	// enable CTiNLa
	public static boolean CTINLA_ENABLED;

	// enable TiPLa
	public static boolean TIPLA_ENABLED;

	// depth for TiPLa
	public static int TIPLA_MAX_DEPTH;

	// radius for TiNLa
	public static int TINLA_R;

	// radius for CTiNLa
	public static int CTINLA_R;

	private static final Logger _log = Logger.getLogger(Config.class.getName());

	// ====================================================

	/**
	 * Check for config correctness
	 * 
	 * @throws Exception
	 */
	public static void loadConfigs() throws Exception {

		final String SETTINGS_FILE = "./config/settings.properties";

		try {
			Properties Settings = new Properties();
			InputStream is = new FileInputStream(new File(SETTINGS_FILE));
			Settings.load(is);
			is.close();

			PATH_OBJECT = Settings.getProperty("ObjectPath", "");
			PATH_DATASET = Settings.getProperty("DataPath", "");
			PATH_LABELS = Settings.getProperty("LabelPath", "");
			PATH_OUTPUT = Settings.getProperty("OutputPath", "");
			PATH_QUERY = Settings.getProperty("QueryPath", "");

			ISDIRECTED = Boolean.parseBoolean(Settings.getProperty("Directed", "false"));
			LOAD_OBJECT = Boolean.parseBoolean(Settings.getProperty("LoadObject", "false"));
			STORE_OBJECT = Boolean.parseBoolean(Settings.getProperty("StoreObject", "false"));

			RUN_RANDOM = Boolean.parseBoolean(Settings.getProperty("Random", "false"));
			RUN_TOPK_QUERIES = Boolean.parseBoolean(Settings.getProperty("TopkQueries", "false"));
			RUN_DURABLE_QUERIES = Boolean.parseBoolean(Settings.getProperty("DurableQueries", "false"));
			CONTIGUOUS_MATCHES = Boolean.parseBoolean(Settings.getProperty("ContiguousMatches", "false"));

			MAX_RANKING_ENABLED = Boolean.parseBoolean(Settings.getProperty("MaxRanking", "false"));
			ZERO_RANKING_ENABLED = Boolean.parseBoolean(Settings.getProperty("ZeroRanking", "false"));
			HALFWAY_RANKING_ENABLED = Boolean.parseBoolean(Settings.getProperty("HalfwayRanking", "false"));

			SHOW_MEMORY = Boolean.parseBoolean(Settings.getProperty("ShowMemory", "false"));
			TINLA_ENABLED = Boolean.parseBoolean(Settings.getProperty("TiNLa", "false"));
			CTINLA_ENABLED = Boolean.parseBoolean(Settings.getProperty("CTiNLa", "false"));
			TIPLA_ENABLED = Boolean.parseBoolean(Settings.getProperty("TiPLa", "false"));
			LABEL_CHANGE = Boolean.parseBoolean(Settings.getProperty("LabelChange", "false"));

			K = Integer.parseInt(Settings.getProperty("k", "1"));
			CP = Float.parseFloat(Settings.getProperty("cp", "0.1"));
			TINLA_R = Integer.parseInt(Settings.getProperty("TiNLa_r", "1"));
			CTINLA_R = Integer.parseInt(Settings.getProperty("CTiNLa_r", "1"));
			MAX_MATCHES = Integer.parseInt(Settings.getProperty("MaxMatches", "-1"));
			TIME_LIMIT = Integer.parseInt(Settings.getProperty("TimeLimit", "3600"));
			TIPLA_MAX_DEPTH = Integer.parseInt(Settings.getProperty("TiPLa_depth", "2"));
			MAXIMUM_INTERVAL = Integer.parseInt(Settings.getProperty("MaximumInterval", "-1"));
			RANDOM_ITERATIONS = Integer.parseInt(Settings.getProperty("RandomIterations", "5"));

			boolean stop = false;

			// if max matches restriction is disabled then set algorithm to
			// retrieve everything
			if (MAX_MATCHES == -1)
				MAX_MATCHES = Integer.MAX_VALUE;
			else
				_log.log(Level.WARNING, "Limit to number of matches is enabled", new Exception());

			if (PATH_DATASET.isEmpty()) {
				_log.log(Level.SEVERE, "dataset path is empty." + ". Abborted.", new Exception());
				stop = true;
			} else if (PATH_LABELS.isEmpty()) {
				_log.log(Level.SEVERE, "label path is empty." + ". Abborted.", new Exception());
				stop = true;
			} else if (PATH_OUTPUT.isEmpty()) {
				_log.log(Level.SEVERE, "output path is empty." + ". Abborted.", new Exception());
				stop = true;
			} else if (PATH_QUERY.isEmpty()) {
				_log.log(Level.SEVERE, "query path is empty." + ". Abborted.", new Exception());
				stop = true;
			} else if (MAXIMUM_INTERVAL == -1) {
				_log.log(Level.SEVERE, "Interval is empty or wrong configured." + ". Abborted.", new Exception());
				stop = true;
			} else if ((LOAD_OBJECT || STORE_OBJECT) && PATH_OBJECT.isEmpty()) {
				_log.log(Level.SEVERE, "object path is empty while load/store object is true", new Exception());
				stop = true;
			} else if (TINLA_ENABLED && TINLA_R == 0) {
				_log.log(Level.SEVERE, "TiNLa settings are wrong", new Exception());
				stop = true;
			} else if (TIPLA_ENABLED && TIPLA_MAX_DEPTH == 0) {
				_log.log(Level.SEVERE, "TiPLa settings are wrong", new Exception());
				stop = true;
			} else if ((TIPLA_ENABLED && (TINLA_ENABLED || CTINLA_ENABLED)) || (TINLA_ENABLED && CTINLA_ENABLED)) {
				_log.log(Level.SEVERE, "Only one index must be enabled", new Exception());
				stop = true;
			}

			if (stop)
				System.exit(0);
		} catch (

		Exception e) {
			_log.log(Level.SEVERE, "Failed to Load " + SETTINGS_FILE + " File.", e);
			System.exit(0);
		}
	}
}