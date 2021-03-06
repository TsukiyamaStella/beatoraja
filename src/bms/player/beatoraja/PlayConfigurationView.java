package bms.player.beatoraja;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import bms.player.beatoraja.Config.SkinConfig;
import bms.player.beatoraja.TableData.CourseData;
import bms.player.beatoraja.TableData.TrophyData;
import bms.player.beatoraja.skin.LR2SkinHeader;
import bms.player.beatoraja.skin.LR2SkinHeader.CustomFile;
import bms.player.beatoraja.skin.LR2SkinHeader.CustomOption;
import bms.player.beatoraja.skin.LR2SkinHeaderLoader;
import bms.player.beatoraja.song.SQLiteSongDatabaseAccessor;
import bms.player.beatoraja.song.SongData;
import bms.player.beatoraja.song.SongDatabaseAccessor;
import bms.table.Course;
import bms.table.Course.Trophy;
import bms.table.DifficultyTable;
import bms.table.DifficultyTableElement;
import bms.table.DifficultyTableParser;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.sun.jndi.toolkit.url.Uri;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Callback;

/**
 * Beatorajaの設定ダイアログ
 * 
 * @author exch
 */
public class PlayConfigurationView implements Initializable {

	@FXML
	private ComboBox<Integer> resolution;

	/**
	 * ハイスピード
	 */
	@FXML
	private Spinner<Double> hispeed;

	@FXML
	private GridPane lr2configuration;
	@FXML
	private ComboBox<Integer> fixhispeed;
	@FXML
	private Spinner<Integer> gvalue;
	@FXML
	private Spinner<Integer> inputduration;
	@FXML
	private CheckBox fullscreen;
	@FXML
	private CheckBox vsync;
	@FXML
	private ComboBox<Integer> bgaop;
	@FXML
	private CheckBox use2p;

	@FXML
	private ListView<String> bmsroot;
	@FXML
	private TextField url;
	@FXML
	private ListView<String> tableurl;

	@FXML
	private ComboBox<Integer> scoreop;
	@FXML
	private ComboBox<Integer> gaugeop;
	@FXML
	private ComboBox<Integer> lntype;
	@FXML
	private ComboBox<String> configBox;
	@FXML
	private CheckBox enableLanecover;
	@FXML
	private Spinner<Double> lanecover;
	@FXML
	private CheckBox enableLift;
	@FXML
	private Spinner<Double> lift;

	@FXML
	private TextField bgmpath;
	@FXML
	private TextField soundpath;

	@FXML
	private Spinner<Integer> judgetiming;
	@FXML
	private CheckBox constant;
	@FXML
	private CheckBox bpmguide;
	@FXML
	private CheckBox legacy;

	@FXML
	private Spinner<Integer> maxfps;
	@FXML
	private Spinner<Integer> audiobuffer;
	@FXML
	private Spinner<Integer> audiosim;
	@FXML
	private ComboBox<Integer> judgealgorithm;

	private Config config;;
	@FXML
	private CheckBox folderlamp;
	@FXML
	private ComboBox<Integer> judgedetail;

	private SkinConfigurationView skinview;
	@FXML
	private ComboBox<Integer> skincategory;
	@FXML
	private ComboBox<LR2SkinHeader> skin;
	@FXML
	private ScrollPane skinconfig;

	private static final String[] SCOREOP = { "OFF", "MIRROR", "RANDOM", "R-RANDOM", "S-RANDOM", "SPIRAL", "H-RANDOM",
			"ALL-SCR", "RANDOM-EX", "S-RANDOM-EX" };

	private static final String[] GAUGEOP = { "ASSIST EASY", "EASY", "NORMAL", "HARD", "EX-HARD", "HAZARD" };

	private static final String[] LNTYPE = { "LONG NOTE", "CHARGE NOTE", "HELL CHARGE NOTE" };

	private static final String[] BGAOP = { "ON", "AUTOPLAY ", "OFF" };

	private static final String[] FIXHISPEEDOP = { "OFF", "START BPM", "MAX BPM", "MAIN BPM", "MIN BPM" };

	private static final String[] JUDGEALGORITHM = { "LR2風", "本家風", "最下ノーツ最優先" };

	private static final String[] JUDGEDETAIL = { "なし", "FAST/SLOW", "±ms" };

	private static final String[] RESOLUTION = { "SD (640 x 480)", "HD (1280 x 720)", "FULL HD (1920 x 1080)",
			"ULTRA HD (3940 x 2160)" };

	private static final String[] SKIN_CATEGORY = { "7KEYS", "5KEYS", "14KEYS", "10KEYS", "9KEYS", "MUSIC SELECT",
			"DECIDE", "RESULT", "KEY CONFIG", "SKIN SELECT", "SOUND SET", "THEME", "7KEYS BATTLE", "5KEYS BATTLE",
			"9KEYS BATTLE", "COURSE RESULT" };

	private MainController.BMSInformationLoader loader;

	public void initialize(URL arg0, ResourceBundle arg1) {
		lr2configuration.setHgap(25);
		lr2configuration.setVgap(4);

		resolution.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(RESOLUTION);
			}
		});
		resolution.setButtonCell(new OptionListCell(RESOLUTION));
		resolution.getItems().setAll(0, 1, 2);
		scoreop.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(SCOREOP);
			}
		});
		scoreop.setButtonCell(new OptionListCell(SCOREOP));
		scoreop.getItems().setAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		gaugeop.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(GAUGEOP);
			}
		});
		gaugeop.setButtonCell(new OptionListCell(GAUGEOP));
		gaugeop.getItems().setAll(0, 1, 2, 3, 4, 5);
		bgaop.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(BGAOP);
			}
		});
		bgaop.setButtonCell(new OptionListCell(BGAOP));
		bgaop.getItems().setAll(0, 1, 2);
		fixhispeed.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(FIXHISPEEDOP);
			}
		});
		fixhispeed.setButtonCell(new OptionListCell(FIXHISPEEDOP));
		fixhispeed.getItems().setAll(0, 1, 2, 3, 4);
		lntype.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(LNTYPE);
			}
		});
		lntype.setButtonCell(new OptionListCell(LNTYPE));
		lntype.getItems().setAll(0, 1, 2);
		judgealgorithm.setButtonCell(new OptionListCell(JUDGEALGORITHM));
		judgealgorithm.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(JUDGEALGORITHM);
			}
		});
		judgealgorithm.getItems().setAll(0, 1, 2);
		judgedetail.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(JUDGEDETAIL);
			}
		});
		judgedetail.setButtonCell(new OptionListCell(JUDGEDETAIL));
		judgedetail.getItems().setAll(0, 1, 2);

		skincategory.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			public ListCell<Integer> call(ListView<Integer> param) {
				return new OptionListCell(SKIN_CATEGORY);
			}
		});
		skincategory.setButtonCell(new OptionListCell(SKIN_CATEGORY));
		// skincategory.getItems().setAll(0, 1,
		// 2,3,4,5,6,7,8,9,10,11,12,13,14,15);
		skincategory.getItems().setAll(0, 1, 2, 3, 4, 6, 7);

		skin.setCellFactory(new Callback<ListView<LR2SkinHeader>, ListCell<LR2SkinHeader>>() {
			public ListCell<LR2SkinHeader> call(ListView<LR2SkinHeader> param) {
				return new SkinListCell();
			}
		});
		skin.setButtonCell(new SkinListCell());

	}

	public void setBMSInformationLoader(MainController.BMSInformationLoader loader) {
		this.loader = loader;
	}

	/**
	 * ダイアログの項目を更新する
	 */
	public void update(Config config) {
		this.config = config;
		resolution.setValue(config.getResolution());
		fullscreen.setSelected(config.isFullscreen());
		vsync.setSelected(config.isVsync());
		use2p.setSelected(config.isUse2pside());
		bgaop.setValue(config.getBga());
		scoreop.getSelectionModel().select(config.getRandom());
		gaugeop.getSelectionModel().select(config.getGauge());
		lntype.getSelectionModel().select(config.getLnmode());

		fixhispeed.setValue(config.getFixhispeed());
		hispeed.getValueFactory().setValue((double) config.getMode7().getHispeed());
		gvalue.getValueFactory().setValue(config.getMode7().getDuration());
		enableLanecover.setSelected(config.getMode7().isEnablelanecover());
		lanecover.getValueFactory().setValue((double) config.getMode7().getLanecover());
		enableLift.setSelected(config.getMode7().isEnablelift());
		lift.getValueFactory().setValue((double) config.getMode7().getLift());
		judgetiming.getValueFactory().setValue(config.getJudgetiming());

		bgmpath.setText(config.getBgmpath());
		soundpath.setText(config.getSoundpath());

		bmsroot.getItems().setAll(config.getBmsroot());
		tableurl.getItems().setAll(config.getTableURL());

		constant.setSelected(config.isConstant());
		bpmguide.setSelected(config.isBpmguide());
		legacy.setSelected(config.isLegacynote());

		maxfps.getValueFactory().setValue(config.getMaxFramePerSecond());
		audiobuffer.getValueFactory().setValue(config.getAudioDeviceBufferSize());
		audiosim.getValueFactory().setValue(config.getAudioDeviceSimultaneousSources());

		judgealgorithm.setValue(config.getJudgeAlgorithm());

		folderlamp.setSelected(config.isFolderlamp());
		judgedetail.setValue(config.getJudgedetail());

		inputduration.getValueFactory().setValue(config.getInputduration());

		skinview = new SkinConfigurationView();

		skincategory.setValue(0);
		updateSkinCategory();
	}

	/**
	 * ダイアログの項目をconfig.xmlに反映する
	 */
	public void commit() {
		config.setResolution(resolution.getValue());
		config.setFullscreen(fullscreen.isSelected());
		config.setVsync(vsync.isSelected());
		config.setUse2pside(use2p.isSelected());
		config.setBga(bgaop.getValue());
		config.setRandom(scoreop.getValue());
		config.setGauge(gaugeop.getValue());
		config.setLnmode(lntype.getValue());
		config.getMode7().setHispeed(hispeed.getValue().floatValue());
		config.setFixhispeed(fixhispeed.getValue());
		config.getMode7().setDuration(gvalue.getValue());
		config.getMode7().setEnablelanecover(enableLanecover.isSelected());
		config.getMode7().setLanecover(lanecover.getValue().floatValue());
		config.getMode7().setEnablelift(enableLift.isSelected());
		config.getMode7().setLift(lift.getValue().floatValue());
		config.setJudgetiming(judgetiming.getValue());

		config.setBgmpath(bgmpath.getText());
		config.setSoundpath(soundpath.getText());

		config.setBmsroot(bmsroot.getItems().toArray(new String[0]));
		config.setTableURL(tableurl.getItems().toArray(new String[0]));

		config.setConstant(constant.isSelected());
		config.setBpmguide(bpmguide.isSelected());
		config.setLegacynote(legacy.isSelected());

		config.setMaxFramePerSecond(maxfps.getValue());
		config.setAudioDeviceBufferSize(audiobuffer.getValue());
		config.setAudioDeviceSimultaneousSources(audiosim.getValue());

		config.setJudgeAlgorithm(judgealgorithm.getValue());

		config.setFolderlamp(folderlamp.isSelected());
		config.setJudgedetail(judgedetail.getValue());

		config.setInputduration(inputduration.getValue());

		updateSkinCategory();

		Json json = new Json();
		json.setOutputType(OutputType.json);
		try (FileWriter fw = new FileWriter("config.json")) {
			fw.write(json.prettyPrint(config));
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addSongPath() {
		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("楽曲のルートフォルダを選択してください");
		File dir = chooser.showDialog(null);
		if (dir != null) {
			bmsroot.getItems().add(dir.getPath());
		}
	}

	public void removeSongPath() {
		bmsroot.getItems().removeAll(bmsroot.getSelectionModel().getSelectedItems());
	}

	public void addTableURL() {
		String s = url.getText();
		if (s.startsWith("http") && !tableurl.getItems().contains(s)) {
			tableurl.getItems().add(url.getText());
		}
	}

	public void removeTableURL() {
		tableurl.getItems().removeAll(tableurl.getSelectionModel().getSelectedItems());
	}

	private int mode = -1;

	public void updateSkinCategory() {
		if (skinview.getSelectedHeader() != null) {
			LR2SkinHeader header = skinview.getSelectedHeader();
			SkinConfig skin = new SkinConfig();
			skin.setPath(header.getPath().toString());
			skin.setProperty(skinview.getProperty());
			config.getSkin()[header.getMode()] = skin;
		} else if (mode != -1) {
			config.getSkin()[mode] = null;
		}

		skin.getItems().clear();
		skin.getItems().add(null);
		skin.getItems().addAll(skinview.getSkinHeader(skincategory.getValue()));
		mode = skincategory.getValue();
		if (config.getSkin()[skincategory.getValue()] != null) {
			SkinConfig skinconf = config.getSkin()[skincategory.getValue()];
			if (skinconf != null) {
				for (LR2SkinHeader header : skin.getItems()) {
					if (header != null && header.getPath().equals(Paths.get(skinconf.getPath()))) {
						skin.setValue(header);
						skinconfig.setContent(skinview.create(skin.getValue(), skinconf.getProperty()));
						break;
					}
				}
			} else {
				skin.getSelectionModel().select(0);
			}
		}
	}

	public void updateSkin() {
		skinconfig.setContent(skinview.create(skin.getValue(), null));
	}

	public void start() {
		commit();
		loader.hide();
		MainController.play(null, 0, true);
	}

	public void loadAllBMS() {
		loadBMS(true);
	}

	public void loadDiffBMS() {
		loadBMS(false);
	}

	/**
	 * BMSを読み込み、楽曲データベースを更新する
	 * 
	 * @param updateAll
	 *            falseの場合は追加削除分のみを更新する
	 */
	public void loadBMS(boolean updateAll) {
		commit();
		try {
			Class.forName("org.sqlite.JDBC");
			SongDatabaseAccessor songdb = new SQLiteSongDatabaseAccessor(Paths.get("songdata.db").toString(),
					config.getBmsroot());
			Logger.getGlobal().info("song.db更新開始");
			songdb.updateSongDatas(null, updateAll);
			Logger.getGlobal().info("song.db更新完了");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static final String[] CONSTRAINT = { "null", "grade", "grade_mirror", "grade_random", "no_speed",
			"no_good", "no_great" };

	public void loadTable() {
		commit();
		try {
			Files.createDirectories(Paths.get("table"));
		} catch (IOException e) {
		}

		try (DirectoryStream<Path> paths = Files.newDirectoryStream(Paths.get("table"))) {
			for (Path p : paths) {
				Files.deleteIfExists(p);
			}
		} catch (IOException e) {

		}

		TableDataAccessor tda = new TableDataAccessor();
		tda.updateTableData(config.getTableURL());
	}

	public void importScoreDataFromLR2() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().setAll(
				new ExtensionFilter("Lunatic Rave 2 Score Database File", "*.db"));
		chooser.setTitle("LRのスコアデータベースを選択してください");
		File dir = chooser.showOpenDialog(null);
		if (dir == null) {
			return;
		}

		final int[] clears = { 0, 1, 4, 5, 6, 8, 9 };
		try {
			Class.forName("org.sqlite.JDBC");
			PlayDataAccessor playdata = new PlayDataAccessor("playerscore");
			SongDatabaseAccessor songdb = new SQLiteSongDatabaseAccessor(Paths.get("songdata.db").toString(),
					config.getBmsroot());
			String player = "playerscore";
			ScoreDatabaseAccessor scoredb = new ScoreDatabaseAccessor(new File(".").getAbsoluteFile().getParent(), "/", "/");
			scoredb.createTable(player);

			try (Connection con = DriverManager.getConnection("jdbc:sqlite:" + dir.getPath())) {
				QueryRunner qr = new QueryRunner();
				MapListHandler rh = new MapListHandler();
				List<Map<String, Object>> scores = qr.query(con, "SELECT * FROM score", rh);

				List<IRScoreData> result = new ArrayList<IRScoreData>();
				for (Map<String, Object> score : scores) {
					final String md5 = (String) score.get("hash");
					SongData[] song = songdb.getSongDatas(new String[] { md5 });
					if (song.length > 0) {
						IRScoreData sd = new IRScoreData();
						sd.setEpg((int) score.get("perfect"));
						sd.setEgr((int) score.get("great"));
						sd.setEgd((int) score.get("good"));
						sd.setEbd((int) score.get("bad"));
						sd.setEpr((int) score.get("poor"));
						sd.setMinbp((int) score.get("minbp"));
						sd.setClear(clears[(int) score.get("clear")]);
						sd.setPlaycount((int) score.get("playcount"));
						sd.setClearcount((int) score.get("clearcount"));
						sd.setNotes(song[0].getNotes());
						sd.setSha256(song[0].getSha256());
						IRScoreData oldsd = scoredb.getScoreData(player, sd.getSha256(), 0);
						if(oldsd == null || oldsd.getClear() <= sd.getClear()) {
							result.add(sd);
						}
					}
				}
				scoredb.setScoreData(player, result.toArray(new IRScoreData[result.size()]));
			} catch (Exception e) {
				Logger.getGlobal().severe("スコア移行時の例外:" + e.getMessage());
			}
		} catch (ClassNotFoundException e1) {
		}

	}

	public void exit() {
		commit();
		Platform.exit();
		System.exit(0);
	}

	class OptionListCell extends ListCell<Integer> {

		private final String[] strings;

		public OptionListCell(String[] strings) {
			this.strings = strings;
		}

		@Override
		protected void updateItem(Integer arg0, boolean arg1) {
			super.updateItem(arg0, arg1);
			if (arg0 != null) {
				setText(strings[arg0]);
			}
		}
	}

	class SkinListCell extends ListCell<LR2SkinHeader> {

		@Override
		protected void updateItem(LR2SkinHeader arg0, boolean arg1) {
			super.updateItem(arg0, arg1);
			if (arg0 != null) {
				setText(arg0.getName());
			} else {
				setText("");
			}
		}
	}

}

class SkinConfigurationView {

	private List<LR2SkinHeader> lr2skinheader = new ArrayList<LR2SkinHeader>();

	private LR2SkinHeader selected = null;
	private Map<CustomOption, ComboBox<String>> optionbox = new HashMap<CustomOption, ComboBox<String>>();
	private Map<CustomFile, ComboBox<String>> filebox = new HashMap<CustomFile, ComboBox<String>>();

	public SkinConfigurationView() {
		List<Path> lr2skinpaths = new ArrayList<Path>();
		scan(Paths.get("skin"), lr2skinpaths);
		for (Path path : lr2skinpaths) {
			LR2SkinHeaderLoader loader = new LR2SkinHeaderLoader();
			try {
				LR2SkinHeader header = loader.loadSkin(path, null);
				System.out.println(path.toString() + " : " + header.getName() + " - " + header.getMode());
				lr2skinheader.add(header);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public VBox create(LR2SkinHeader header, Map<String, Object> property) {
		selected = header;
		if (header == null) {
			return null;
		}
		VBox main = new VBox();
		optionbox.clear();
		for (CustomOption option : header.getCustomOptions()) {
			HBox hbox = new HBox();
			ComboBox<String> combo = new ComboBox<String>();
			combo.getItems().setAll(option.contents);
			if (property != null) {
				int i = (int) property.get(option.name);
				combo.getSelectionModel().select(i - option.option);
			} else {
				combo.getSelectionModel().select(0);
			}
			hbox.getChildren().addAll(new Label(option.name), combo);
			optionbox.put(option, combo);
			main.getChildren().add(hbox);
		}
		filebox.clear();
		for (CustomFile file : header.getCustomFiles()) {
			String name = file.path.substring(file.path.lastIndexOf('/') + 1);
			final Path dirpath = Paths.get(file.path.substring(0, file.path.lastIndexOf('/')));
			if (!Files.exists(dirpath)) {
				continue;
			}
			try (DirectoryStream<Path> paths = Files.newDirectoryStream(dirpath,
					"{" + name.toLowerCase() + "," + name.toUpperCase() + "}")) {
				HBox hbox = new HBox();
				ComboBox<String> combo = new ComboBox<String>();
				for (Path p : paths) {
					combo.getItems().add(p.getFileName().toString());
				}
				if (property != null) {
					String s = (String) property.get(file.name);
					combo.setValue(s);
				} else {
					combo.getSelectionModel().select(0);
				}
				hbox.getChildren().addAll(new Label(file.name), combo);
				filebox.put(file, combo);
				main.getChildren().add(hbox);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return main;
	}

	private void scan(Path p, final List<Path> paths) {
		if (Files.isDirectory(p)) {
			try (Stream<Path> sub = Files.list(p)) {
				sub.forEach(new Consumer<Path>() {
					@Override
					public void accept(Path t) {
						scan(t, paths);
					}
				});
			} catch (IOException e) {
			}
		} else if (p.getFileName().toString().toLowerCase().endsWith(".lr2skin")) {
			paths.add(p);
		}
	}

	public LR2SkinHeader getSelectedHeader() {
		return selected;
	}

	public Map<String, Object> getProperty() {
		Map<String, Object> result = new HashMap();
		for (CustomOption option : selected.getCustomOptions()) {
			if (optionbox.get(option) != null) {
				int index = optionbox.get(option).getSelectionModel().getSelectedIndex();
				result.put(option.name, index + option.option);
			}
		}
		for (CustomFile file : selected.getCustomFiles()) {
			if (filebox.get(file) != null) {
				result.put(file.name, filebox.get(file).getValue());
			}
		}
		return result;
	}

	public LR2SkinHeader[] getSkinHeader(int mode) {
		List<LR2SkinHeader> result = new ArrayList();
		for (LR2SkinHeader header : lr2skinheader) {
			if (header.getMode() == mode) {
				result.add(header);
			}
		}
		return result.toArray(new LR2SkinHeader[result.size()]);
	}
}