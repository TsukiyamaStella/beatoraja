package bms.player.beatoraja.skin;

import java.io.*;
import java.util.*;

import bms.player.beatoraja.MainState;
import bms.player.beatoraja.skin.LR2SkinHeader.CustomFile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * LR2のスキン定義用csvファイルのローダー
 * 
 * @author exch
 */
public abstract class LR2SkinCSVLoader extends LR2SkinLoader {

	List<Texture> imagelist = new ArrayList<Texture>();

	/**
	 * スキンの元サイズのwidth
	 */
	public final float srcw;
	/**
	 * スキンの元サイズのheight
	 */
	public final float srch;
	/**
	 * 描画サイズのwidth
	 */
	public final float dstw;
	/**
	 * 描画サイズのheight
	 */
	public final float dsth;

	private Skin skin;

	private MainState state;

	public LR2SkinCSVLoader(final float srcw, final float srch, final float dstw, final float dsth) {
		this.srcw = srcw;
		this.srch = srch;
		this.dstw = dstw;
		this.dsth = dsth;

		addCommandWord(new CommandWord("STARTINPUT") {
			@Override
			public void execute(String[] str) {
				skin.setInput(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("SCENETIME") {
			@Override
			public void execute(String[] str) {
				skin.setScene(Integer.parseInt(str[1]));
			}
		});
		addCommandWord(new CommandWord("FADEOUT") {
			@Override
			public void execute(String[] str) {
				skin.setFadeout(Integer.parseInt(str[1]));
			}
		});

		addCommandWord(new CommandWord("INCLUDE") {
			@Override
			public void execute(String[] str) {
				String imagepath = str[1].replace("LR2files\\Theme", "skin").replace("\\", "/");
				File imagefile = new File(imagepath);
				for(String key : filemap.keySet()) {
					if(imagepath.startsWith(key)) {
						String foot = imagepath.substring(key.length());
						imagefile = new File(imagepath.substring(0, imagepath.lastIndexOf('*'))
								+ filemap.get(key) + foot);
						System.out.println(imagefile.getPath());
						imagepath = "";
						break;
					}
				}
				if (imagepath.contains("*")) {
					String ext = imagepath.substring(imagepath.lastIndexOf("*") + 1);
					File imagedir = new File(imagepath.substring(0, imagepath.lastIndexOf('/')));
					if (imagedir.exists() && imagedir.isDirectory()) {
						List<File> l = new ArrayList();
						for (File subfile : imagedir.listFiles()) {
							if (subfile.getPath().toLowerCase().endsWith(ext)) {
								l.add(subfile);
							}
						}
						if (l.size() > 0) {
							imagefile = l.get((int) (Math.random() * l.size()));
						}
					}
				}
				if (imagefile.exists()) {
					try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(imagefile),
							"MS932"));) {
						while ((line = br.readLine()) != null) {
							processLine(line, state);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("IMAGE") {
			@Override
			public void execute(String[] str) {
				String imagepath = str[1].replace("LR2files\\Theme", "skin").replace("\\", "/");
				File imagefile = new File(imagepath);
				for(String key : filemap.keySet()) {
					if(imagepath.startsWith(key)) {
						String foot = imagepath.substring(key.length());
						imagefile = new File(imagepath.substring(0, imagepath.lastIndexOf('*'))
								+ filemap.get(key) + foot);
						System.out.println(imagefile.getPath());
						imagepath = "";
						break;
					}
				}
				if (imagepath.contains("*")) {
					String ext = imagepath.substring(imagepath.lastIndexOf("*") + 1);
					File imagedir = new File(imagepath.substring(0, imagepath.lastIndexOf('/')));
					if (imagedir.exists() && imagedir.isDirectory()) {
						List<File> l = new ArrayList();
						for (File subfile : imagedir.listFiles()) {
							if (subfile.getPath().toLowerCase().endsWith(ext)) {
								l.add(subfile);
							}
						}
						if (l.size() > 0) {
							imagefile = l.get((int) (Math.random() * l.size()));
						}
					}
				}
				if (imagefile.exists()) {
					try {
						imagelist.add(new Texture(Gdx.files.internal(imagefile.getPath())));
					} catch (GdxRuntimeException e) {
						imagelist.add(null);
						e.printStackTrace();
					}
				} else {
					imagelist.add(null);
				}
				// System.out
				// .println("Image Loaded - " + (imagelist.size() -
				// 1) + " : " + imagefile.getPath());
			}
		});

		addCommandWord(new CommandWord("SRC_IMAGE") {
			@Override
			public void execute(String[] str) {
				try {
					part = null;
					int gr = Integer.parseInt(str[2]);
					if (gr >= 100) {
						part = new SkinImage(gr);
						// System.out.println("add reference image : "
						// + gr);
					} else {
						int[] values = parseInt(str);
						TextureRegion[] images = getSourceImage(values);
						if(images != null) {
							part = new SkinImage(images, values[9]);
							part.setTimer(values[10]);
							// System.out.println("Object Added - " +
							// (part.getTiming()));							
						}
					}
					if (part != null) {
						skin.add(part);
					} else {
						System.out.println("NO_DESTINATION : " + line);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});

		addCommandWord(new CommandWord("DST_IMAGE") {
			@Override
			public void execute(String[] str) {
				if (part != null) {
					try {
						int[] values = parseInt(str);
						if (values[5] < 0) {
							values[3] += values[5];
							values[5] = -values[5];
						}
						if (values[6] < 0) {
							values[4] += values[6];
							values[6] = -values[6];
						}
						part.setDestination(values[2], values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth
								/ srch, values[5] * dstw / srcw, values[6] * dsth / srch, values[7], values[8],
								values[9], values[10], values[11], values[12], values[13], values[14], values[15],
								values[16], values[17], values[18], values[19], values[20]);
						if(values[21] != 0) {
							System.out.println("set scratch image : " + values[21]);
							part.setScratch(values[21]);
						}
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_NUMBER") {
			@Override
			public void execute(String[] str) {
				num = null;
				try {
					int[] values = parseInt(str);
					int divx = values[7];
					if (divx <= 0) {
						divx = 1;
					}
					int divy = values[8];
					if (divy <= 0) {
						divy = 1;
					}

					if(divx * divy >= 10) {
						TextureRegion[] images = getSourceImage(values);
						if(images != null) {
							if (images.length % 24 == 0) {
								TextureRegion[] pn = new TextureRegion[12];
								TextureRegion[] mn = new TextureRegion[12];

								for (int i = 0; i < 12; i++) {
									pn[i] = images[i];
									mn[i] = images[i + 12];
								}
								num = new SkinNumber(pn, mn, values[9], values[13] + 1, 0, values[11]);
								num.setAlign(values[12]);
							} else {
								int d = images.length % 10 == 0 ? 10 :11;
								
								TextureRegion[][] nimages = new TextureRegion[divx * divy / d][d];
								for (int i = 0; i < d; i++) {
									for (int j = 0; j < divx * divy / d; j++) {
										nimages[j][i] = images[j * d + i];
									}
								}
								
								num = new SkinNumber(nimages, values[9], values[13], d > 10 ? 2 : 0, values[11]);
								num.setAlign(values[12]);
							}
							num.setTimer(values[10]);
							skin.add(num);
							// System.out.println("Number Added - " +
							// (num.getId()));						
						}						
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
		addCommandWord(new CommandWord("DST_NUMBER") {
			@Override
			public void execute(String[] str) {
				if (num != null) {
					try {
						int[] values = parseInt(str);
						num.setDestination(values[2], values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth
								/ srch, values[5] * dstw / srcw, values[6] * dsth / srch, values[7], values[8],
								values[9], values[10], values[11], values[12], values[13], values[14], values[15],
								values[16], values[17], values[18], values[19], values[20]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_TEXT") {
			@Override
			public void execute(String[] str) {
				text = null;
				int gr = Integer.parseInt(str[2]);
				try {
					text = new SkinText("skin/VL-Gothic-Regular.ttf", 0, 40, 2);
					int[] values = parseInt(str);
					text.setReferenceID(values[3]);
					text.setAlign(values[4]);
					int edit = values[5];
					int panel = values[6];
					skin.add(text);
					// System.out.println("Text Added - " +
					// (values[3]));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
		addCommandWord(new CommandWord("DST_TEXT") {
			@Override
			public void execute(String[] str) {
				if (text != null) {
					try {
						int[] values = parseInt(str);
						text.setDestination(values[2], values[3] * dstw / srcw, dsth - values[4] * dsth / srch,
								values[5] * dstw / srcw, values[6] * dsth / srch, values[7], values[8], values[9],
								values[10], values[11], values[12], values[13], values[14], values[15], values[16],
								values[17], values[18], values[19], values[20]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_SLIDER") {
			@Override
			public void execute(String[] str) {
				slider = null;
				try {
					int[] values = parseInt(str);
					TextureRegion[] images = getSourceImage(values);
					if (images != null) {
						slider = new SkinSlider(images, values[9], values[11], (int) (values[12] * (values[11] == 1
								|| values[11] == 3 ? (dstw / srcw) : (dsth / srch))), values[13]);
						slider.setTimer(values[10]);
						slider.setChangable(values[14] == 0);
						skin.add(slider);
						// System.out.println("Object Added - " +
						// (part.getTiming()));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
		addCommandWord(new CommandWord("DST_SLIDER") {
			@Override
			public void execute(String[] str) {
				if (slider != null) {
					try {
						int[] values = parseInt(str);
						slider.setDestination(values[2], values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth
								/ srch, values[5] * dstw / srcw, values[6] * dsth / srch, values[7], values[8],
								values[9], values[10], values[11], values[12], values[13], values[14], values[15],
								values[16], values[17], values[18], values[19], values[20]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

		addCommandWord(new CommandWord("SRC_BARGRAPH") {
			@Override
			public void execute(String[] str) {
				bar = null;
				try {
					int[] values = parseInt(str);
					int gr = values[2];
					if (gr >= 100) {
						bar = new SkinGraph(gr);
						bar.setTimer(values[10]);
						bar.setReferenceID(values[11] + 1000);
						bar.setDirection(values[12]);
					} else {
						TextureRegion[] images = getSourceImage(values);
						if (images != null) {
							bar = new SkinGraph(images, values[9]);
							bar.setTimer(values[10]);
							bar.setReferenceID(values[11] + 1000);
							bar.setDirection(values[12]);
							// System.out.println("Object Added - " +
							// (part.getTiming()));
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if (bar != null) {
					skin.add(bar);
				}
			}
		});
		addCommandWord(new CommandWord("DST_BARGRAPH") {
			@Override
			public void execute(String[] str) {
				if (bar != null) {
					try {
						int[] values = parseInt(str);
						if (bar.getDirection() == 1) {
							values[4] += values[6];
							values[6] = -values[6];
						}
						bar.setDestination(values[2], values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth
								/ srch, values[5] * dstw / srcw, values[6] * dsth / srch, values[7], values[8],
								values[9], values[10], values[11], values[12], values[13], values[14], values[15],
								values[16], values[17], values[18], values[19], values[20]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});
		addCommandWord(new CommandWord("SRC_BUTTON") {
			@Override
			public void execute(String[] str) {
				button = null;
				int gr = Integer.parseInt(str[2]);
				if (gr < imagelist.size() && imagelist.get(gr) != null) {
					try {
						int[] values = parseInt(str);
						int x = values[3];
						int y = values[4];
						int w = values[5];
						if (w == -1) {
							w = imagelist.get(gr).getWidth();
						}
						int h = values[6];
						if (h == -1) {
							h = imagelist.get(gr).getHeight();
						}
						int divx = values[7];
						if (divx <= 0) {
							divx = 1;
						}
						int divy = values[8];
						if (divy <= 0) {
							divy = 1;
						}
						TextureRegion[][] images = new TextureRegion[divx * divy][];
						for (int i = 0; i < divx; i++) {
							for (int j = 0; j < divy; j++) {
								images[divx * j + i] = new TextureRegion[] { new TextureRegion(imagelist.get(gr), x + w
										/ divx * i, y + h / divy * j, w / divx, h / divy) };
							}
						}
						button = new SkinImage(images, values[9]);
						button.setTimer(values[10]);
						button.setReferenceID(values[11] + 1000);
						skin.add(button);
						// System.out.println("Object Added - " +
						// (part.getTiming()));
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});
		addCommandWord(new CommandWord("DST_BUTTON") {
			@Override
			public void execute(String[] str) {
				if (button != null) {
					try {
						int[] values = parseInt(str);
						button.setDestination(values[2], values[3] * dstw / srcw, dsth - (values[4] + values[6]) * dsth
								/ srch, values[5] * dstw / srcw, values[6] * dsth / srch, values[7], values[8],
								values[9], values[10], values[11], values[12], values[13], values[14], values[15],
								values[16], values[17], values[18], values[19], values[20]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	protected void loadSkin(Skin skin, File f, MainState state) throws IOException {
		this.loadSkin(skin, f, state, new int[0]);
	}

	protected void loadSkin(Skin skin, File f, MainState state, int[] option) throws IOException {
		this.loadSkin0(skin, f, state, option, new HashMap());
	}

	private Map<String, String> filemap = new HashMap();

	protected void loadSkin(Skin skin, File f, MainState state, LR2SkinHeader header, int[] option,
			Map<String, Object> property) throws IOException {
		this.skin = skin;
		this.state = state;
		for (String key : property.keySet()) {
			if (property.get(key) != null) {
				if (property.get(key) instanceof Integer) {
					op.add((Integer) property.get(key));
				}
				if (property.get(key) instanceof String) {
					for (CustomFile file : header.getCustomFiles()) {
						if (file.name.equals(key)) {
							filemap.put(file.path, (String) property.get(key));
							break;
						}
					}
				}
			}
		}
		for (int i : option) {
			op.add(i);
		}
		option = new int[op.size()];
		for (int i = 0; i < op.size(); i++) {
			option[i] = op.get(i);
		}
		this.loadSkin0(skin, f, state, option, filemap);
	}

	SkinImage part = null;
	SkinImage button = null;
	SkinGraph bar = null;
	SkinSlider slider = null;
	SkinNumber num = null;
	SkinText text = null;
	String line = null;

	protected void loadSkin0(Skin skin, File f, MainState state, int[] option, Map<String, String> filemap)
			throws IOException {
		for (int i : option) {
			op.add(i);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "MS932"));

		while ((line = br.readLine()) != null) {
			processLine(line, state);
		}
		br.close();

		int[] soption = new int[op.size()];
		for (int i = 0; i < op.size(); i++) {
			soption[i] = op.get(i);
		}
		skin.setOption(soption);

		for (SkinObject obj : skin.getAllSkinObjects()) {
			if (obj instanceof SkinImage && obj.getAllDestination().length == 0) {
				skin.removeSkinObject(obj);
				System.out.println("NO_DESTINATION : " + obj);
			}
		}
	}

	protected int[] parseInt(String[] s) {
		int[] result = new int[22];
		for (int i = 1; i < s.length; i++) {
			try {
				result[i] = Integer.parseInt(s[i].replace('!', '-').replaceAll(" ", ""));
			} catch (Exception e) {

			}
		}
		return result;
	}

	protected TextureRegion[] getSourceImage(int[] values) {
		if (values[2] < imagelist.size() && imagelist.get(values[2]) != null) {
			return getSourceImage(imagelist.get(values[2]), values[3], values[4], values[5], values[6], values[7],
					values[8]);
		}
		System.out.println("failed to load image : " + line);
		return null;
	}

	protected TextureRegion[] getSourceImage(Texture image, int x, int y, int w, int h, int divx, int divy) {
		if (w == -1) {
			w = image.getWidth();
		}
		if (h == -1) {
			h = image.getHeight();
		}
		if (divx <= 0) {
			divx = 1;
		}
		if (divy <= 0) {
			divy = 1;
		}
		TextureRegion[] images = new TextureRegion[divx * divy];
		for (int i = 0; i < divx; i++) {
			for (int j = 0; j < divy; j++) {
				images[divx * j + i] = new TextureRegion(image, x + w / divx * i, y + h / divy * j, w / divx, h / divy);
			}
		}
		return images;
	}
}
