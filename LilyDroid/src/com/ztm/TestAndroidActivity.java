package com.ztm;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.NameValuePair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sonyericsson.zoom.ImageTextButton;
import com.ztm.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.text.ClipboardManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

public class TestAndroidActivity extends Activity implements OnTouchListener,
		OnGestureListener {

	private GestureDetector mGestureDetector;
	// 控件

	private TextView textView;

	private ListView listView;

	private Button btnLink;

	// 全局变量

	private List<String> LinkAdr;

	private String data;

	private List<TopicInfo> top10TopicList;

	private String topicUrl;

	private String newUrl;

	private String huifuUrl;

	// 1 表示从10大跳转过去的，2表示从讨论区跳转过去的，3表示从各区热点跳过去
	int curStatus = 0;

	List<TopicInfo> areaTopic;

	int areaNowTopic = 0;

	boolean isWifi = false;

	private int nowPos;

	String urlString = "";

	String curAreaName = "";

	String curTopicId = "";

	String isPic;
	String signColor;
	String isRem = "false";
	boolean isTouch;
	boolean isIP;
	boolean isMoreFast;
	String loginId = "";
	String loginPwd = "";

	String androidmanufacturer;
	String androidmodel;

	String backWords = "";//
	boolean isBackWord = true;

	int runningTasks = 0;

	private ProgressDialog progressDialog = null;

	SharedPreferences sharedPreferences;

	List<String> areaNamList;

	String[] fastReList;

	Drawable drawableFav;

	Drawable drawableDis;

	boolean isLogin = false;

	String nowLoginId = "";

	int txtFonts;

	int backAlpha;

	Spanned topicData;

	int scrollY = 0;

	boolean topicWithImg = false;

	HashMap<String, String> bbsAll;

	HashMap<String, Integer> smilyAll;

	ArrayAdapter<String> bbsAlladapter;

	String bbsURL = "http://bbs.nju.edu.cn/";

	String loginURL = "http://bbs.nju.edu.cn/bbslogin?type=2";

	String loginoutURL = "http://bbs.nju.edu.cn/bbslogout";

	String synUrl = "http://bbs.nju.edu.cn/bbsleft";
	
	String forumUrl ="http://bbs.nju.edu.cn/cache/t_forum.js";
	
	String recbrdUrl ="http://bbs.nju.edu.cn/cache/t_recbrd.js";

	String bbsTop10String = "http://bbs.nju.edu.cn/bbstop10";

	String mailURL = "http://bbs.nju.edu.cn/bbsmail";

	HashMap<String, Integer> fbAll = new HashMap<String, Integer>();
	
	HashMap<String, String> fbNameAll = new HashMap<String, String>();
	
	String bbsHotString = "http://bbs.nju.edu.cn/bbstopall";
	List<Map<String, Object>> parentList = null;
	List<List<Map<String, Object>>> allChildList = null;
	ImageSpan hotTopicSpan;
	ImageSpan mailSpan;
	Drawable xianDraw;
	int sWidth = 480;
	int sLength = 800;
	int sdensity;
	
	ForegroundColorSpan listColorSpan;
	List<TopicInfo> hotList;
	AbsoluteSizeSpan absoluteSizeSpan;
	List<TopicInfo> mailList = null;
	
	// String TMStr;
	// TODO:定义全局变量
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this);

		Resources res = getResources();
		String color = res.getString(R.string.listColor);
		listColorSpan = new ForegroundColorSpan(Color.parseColor(color));
		absoluteSizeSpan = new AbsoluteSizeSpan(13, true);

		Drawable drawable = res.getDrawable(R.drawable.bkcolor);

		Drawable d = getResources().getDrawable(R.drawable.hottopcic);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

		hotTopicSpan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);

		d = getResources().getDrawable(R.drawable.unread);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());

		mailSpan = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
		// TMStr = "㊣";

		xianDraw = res.getDrawable(R.drawable.xian);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		sWidth = metric.widthPixels - 30; // 屏幕宽度（像素）
		sLength = metric.heightPixels - 40; // 屏幕宽度（像素）
		sdensity =(int) metric.density;

		this.getWindow().setBackgroundDrawable(drawable);
		bbsAll = BBSAll.getBBSAll();

		smilyAll = BBSAll.getSmilyAll();
		String[] bbsAllArray = StringUtil.getArray(bbsAll);
		bbsAlladapter = new ArrayAdapter<String>(TestAndroidActivity.this,
				android.R.layout.simple_dropdown_item_1line, bbsAllArray);
		initPhoneState();
		initAllParams();
		initfbAll();
		StringUtil.initAll();

		chaToLogin();
	}

	private void InitMain() {
		chaToMain();
		getUrlHtml(bbsTop10String, Const.MSGWHAT);
	}

	private void initfbAll() {
		fbAll.put("fb1", R.drawable.fb1);
		fbAll.put("fb2", R.drawable.fb2);
		fbAll.put("fb3", R.drawable.fb3);
		fbAll.put("fb4", R.drawable.fb4);
		fbAll.put("fb5", R.drawable.fb5);
		fbAll.put("fb6", R.drawable.fb6);
		fbAll.put("fb7", R.drawable.fb7);
		fbAll.put("fb8", R.drawable.fb8);
		fbAll.put("fb9", R.drawable.fb9);
		fbAll.put("fb10", R.drawable.fb10);
		fbAll.put("fb11", R.drawable.fb11);
		fbAll.put("fb12", R.drawable.fb12);
		
		
		
		fbNameAll.put("1", "※本站系统※");
		fbNameAll.put("2", "※南京大学※");
		fbNameAll.put("3", "※乡情校谊※");
		fbNameAll.put("4", "※电脑技术※");
		fbNameAll.put("5", "※学术科学※");
		fbNameAll.put("6", "※文化艺术※");
		fbNameAll.put("7", "※体育娱乐※");
		fbNameAll.put("8", "※感性休闲※");
		fbNameAll.put("9", "※新闻信息※");
		fbNameAll.put("10", "※百合广角※");
		fbNameAll.put("11", "※校务信箱※");
		fbNameAll.put("12", "※社团群体※");
		fbNameAll.put("13", "※冷门讨论区※");
		

	}

	private void initPhoneState() {

		try {

			Class<android.os.Build> build_class = android.os.Build.class;

			// 取得牌子

			java.lang.reflect.Field manu_field = build_class
					.getField("MANUFACTURER");

			androidmanufacturer = (String) manu_field
					.get(new android.os.Build());

			// 取得型號

			java.lang.reflect.Field field2 = build_class.getField("MODEL");

			androidmodel = (String) field2.get(new android.os.Build());

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void chaToLogin() {
		setContentView(R.layout.login);
		setTitle("小百合");
		curStatus = 0;
		Button btnlog = (Button) findViewById(R.id.btn_login);
		btnlog.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				EditText textName = (EditText) findViewById(R.id.textName);
				EditText textPwd = (EditText) findViewById(R.id.textPwd);
				CheckBox cb = (CheckBox) findViewById(R.id.cb_rem);
				if (cb.isChecked()) {
					isRem = "true";
				} else {
					isRem = "false";
				}
				loginId = textName.getText().toString();
				loginPwd = textPwd.getText().toString();
				String url = loginURL + "&id=" + loginId + "&pw=" + loginPwd;
				getUrlHtml(url, Const.MSGLOGIN);
			}

		});
		Button btnno = (Button) findViewById(R.id.btn_nolog);
		btnno.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				isLogin = false;
				InitMain();
			}

		});
		if (isRem.equals("true")) {
			EditText text = (EditText) findViewById(R.id.textName);
			text.setText(loginId);
			text = (EditText) findViewById(R.id.textPwd);
			text.setText(loginPwd);
			CheckBox cb = (CheckBox) findViewById(R.id.cb_rem);
			cb.setChecked(true);
		}
	}

	private void getLikeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				TestAndroidActivity.this);

		builder.setTitle("选择版面：");
		if (areaNamList == null || areaNamList.size() < 1) {

			return;
		}
		String[] a = new String[areaNamList.size()];
		int i = 0;
		for (String areName : areaNamList) {
			a[i] = areName;
			i++;
		}

		builder.setSingleChoiceItems(a, 0,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {

						String areaText = areaNamList.get(i);
						urlString = getResources().getString(R.string.areaStr)
								+ areaText;
						curAreaName = "" + areaText;
						dialoginterface.dismiss();
						getUrlHtml(urlString, Const.MSGAREA);

					}
				});

		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {

			}
		});
		builder.create().show();
	}

	/**
	 * 跳转到主界面
	 */
	private void chaToMain() {
		setContentView(R.layout.main);

		curStatus = 0;
		setTitle("全站十大");
		// 注意界面控件的初始化的位置,不要放在setContentView()前面
		listView = (ListView) findViewById(R.id.topicList);
		setIndexBtns(1);

	}

	private void setIndexBtns(int i) {
		btnLink = (Button) findViewById(R.id.btn_link);

		btnLink.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// 可以打开一个新线程来读取，加入滚动条等
				chaToMain();
				getUrlHtml(bbsTop10String, Const.MSGWHAT);
			}

		});

		if (i != 2) {
			Button btnArea = (Button) findViewById(R.id.btn_all);

			btnArea.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					// 可以打开一个新线程来读取，加入滚动条等
					if (parentList == null || parentList.size() < 2) {
						String url = "http://bbs.nju.edu.cn/bbstopb10";
						getUrlHtml(url, Const.TOP20BOARD);
					} else
						chaToAreaToGo();

				}

			});
		}

		if (i != 3) {
			Button btnLike = (Button) findViewById(R.id.btn_like);

			btnLike.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if (isLogin)
						getUrlHtml(mailURL, Const.MSGMAIL);
					else {
						displayMsg("你还没登陆呐~");
					}

				}

			});
		}

		if (i != 4) {
			Button btnSet = (Button) findViewById(R.id.btn_set);

			btnSet.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {

					getUrlHtml(bbsHotString, Const.MSGHOT);
				}

			});
		}
	}

	// 菜单项
	final private int menuSettings = Menu.FIRST;
	final private int menuLogout = Menu.FIRST + 2;
	final private int menuSyn = Menu.FIRST + 1;
	final private int menuReport = Menu.FIRST + 3;
	final private int menuJump = Menu.FIRST + 4;
	private static final int REQ_SYSTEM_SETTINGS = 0;

	// 创建菜单
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 建立菜单
		menu.add(Menu.NONE, menuSettings, 2, "设置").setIcon(R.drawable.set);
		menu.add(Menu.NONE, menuSyn, 2, "同步收藏").setIcon(R.drawable.syn);
		menu.add(Menu.NONE, menuJump, 2, "快速跳转").setIcon(R.drawable.fast);
		menu.add(Menu.NONE, menuReport, 2, "意见反馈").setIcon(R.drawable.info);
		menu.add(Menu.NONE, menuLogout, 2, "注销").setIcon(R.drawable.key);
		return super.onCreateOptionsMenu(menu);
	}

	// 菜单选择事件处理
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case menuSettings:
			// 转到Settings设置界面
			startActivityForResult(new Intent(this, Settings.class),
					REQ_SYSTEM_SETTINGS);
			break;
		case menuLogout:
			// 转到登录界面
			getUrlHtml(loginoutURL, 123);
			isLogin = false;
			nowLoginId = null;
			chaToLogin();
			break;

		case menuJump:
			getLikeDialog();
			break;
		case menuSyn:
			// 转到登录界面
			if (isLogin)
				getUrlHtml(synUrl, Const.MSGSYN);
			else {
				displayMsg("你还没登陆呐~");
			}
			break;
		case menuReport:

			if (isLogin) {

				String cont = "\n\n\n我的机型：" + androidmodel + "\n屏幕宽高比："
						+ (sWidth + 30) + "x" + (sLength + 40);

				beginMail("tiztm", "Android版小百合意见反馈", cont, null);
			} else {
				displayMsg("你还没登陆呐~");
			}
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// Settings设置界面返回的结果
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		myParams();
	}

	private void beginMail(String to, String title, String cont, String action) {
		final String thisAction = action;
		LayoutInflater factory = LayoutInflater.from(TestAndroidActivity.this);
		final View acdlgView = factory.inflate(R.layout.maildlg, null);
		Builder altDlg = new AlertDialog.Builder(TestAndroidActivity.this)
				.setTitle("发送信件").setView(acdlgView).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								EditText titleEdit = (EditText) acdlgView
										.findViewById(R.id.edt_cont);
								String cont = StringUtil.getStrBetter(titleEdit
										.getText().toString());

								titleEdit = (EditText) acdlgView
										.findViewById(R.id.edt_to);
								String to = titleEdit.getText().toString();

								titleEdit = (EditText) acdlgView
										.findViewById(R.id.edt_title);
								String title = titleEdit.getText().toString();

								sendMail(to, title, cont, thisAction);
							}
						});

		altDlg.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialoginterface, int i) {

			}
		});

		AlertDialog dlg = altDlg.create();
		EditText titleEdit;
		if (to != null) {
			titleEdit = (EditText) acdlgView.findViewById(R.id.edt_to);
			titleEdit.setText(to);
		}
		if (title != null) {
			titleEdit = (EditText) acdlgView.findViewById(R.id.edt_title);
			titleEdit.setText(title);
		}
		if (cont != null) {
			titleEdit = (EditText) acdlgView.findViewById(R.id.edt_cont);
			titleEdit.setText(cont);
		}
		if (sLength < 300) {
			titleEdit = (EditText) acdlgView.findViewById(R.id.edt_cont);

			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) titleEdit
					.getLayoutParams(); // 取控件mGrid当前的布局参数
			linearParams.height = 70;// 当控件的高强制设成75象素

			titleEdit.setLayoutParams(linearParams);

		}

		dlg.show();
	}

	/**
	 * 捕获按键事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			retBtn();
			return true;

		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	
	private void retBtn()
	{
		if (curStatus == 1) {
			chaToMain();
			if (top10TopicList != null) {
				convtTopics();
			}
		} else if (curStatus == 2) {
			chaToArea(null);
		} else if (curStatus == 3) {
			chaToHot(null);
		} else if (curStatus == 4) {
			chaToAreaToGo();
		} else if (curStatus == 5) {
			chaToMailBox(null);
		}

		else if (curStatus == 0) {
			exitPro();
		}
	}
	private void initAllParams() {
		sharedPreferences = getSharedPreferences("LilyDroid",
				Context.MODE_PRIVATE);
		String name = sharedPreferences.getString("areaName", "");
		areaNamList = new ArrayList<String>();
		isRem = sharedPreferences.getString("isRem", "false");
		loginId = sharedPreferences.getString("loginId", "");
		loginPwd = sharedPreferences.getString("loginPwd", "");

		myParams();
		WifiManager mWiFiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		if (mWiFiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			isWifi = true;
		}

		if (name == null || name.length() < 1)
			return;

		String[] split = name.split(",");
		for (String string : split) {
			areaNamList.add(string);
		}
	}

	private void myParams() {
		SharedPreferences sp = getSharedPreferences("com.ztm_preferences",
				Context.MODE_PRIVATE);
		isPic = sp.getString("picDS", "1");
		isTouch = sp.getBoolean("isTouch", true);
		isBackWord = sp.getBoolean("isBackWord", true);
		backWords = sp
				.getString("backWords", "发送自 我的小百合Android客户端 by ${model}");
		isIP = sp.getBoolean("isIP", false);
		signColor = sp.getString("signColor", "[1;32m");

		backAlpha = sp.getInt("backAlpha", 20);

		backAlpha = (int) ((int) (100 - backAlpha) * 2.55);
		// backWords = backWords.replaceAll(TMStr, "");
		backWords = backWords.replaceAll("\\$\\{model\\}", androidmodel)
				.replaceAll("\\$\\{manufa\\}", androidmanufacturer);
		isMoreFast = sp.getBoolean("isMoreMoreFast", false);
		txtFonts = sp.getInt("txtFonts", 18);

		String fastRe = sp.getString("fastRe", "沙发");
		if (fastRe.length() < 1) {
			fastReList = null;
			return;
		}

		fastReList = fastRe.split("##");

	}

	private void displayMsg(String msg) {
		Toast.makeText(TestAndroidActivity.this, msg, Toast.LENGTH_SHORT)
				.show();
	}

	/**
	 * TODO:消息控制器 消息控制器，用来更新界面，因为在普通线程是无法用来更新界面的
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			runningTasks--;

			if (msg.what != Const.MSGPSTNEW && data.equals("error")) {
				displayMsg("你的网络貌似有点小问题~");

			} else {
				switch (msg.what) {
				case Const.MSGWHAT:
					// 设置显示文本
					// 处理解析data数据
					top10TopicList = StringUtil.getTop10Topic(data);
					convtTopics();
					break;
				case Const.MSGTOPIC:
					// 设置显示文本

					chaToTopic(topicData);
					break;
				case Const.MSGTOPICNEXT:

					textView = (TextView) findViewById(R.id.label);
					ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
					sv.scrollTo(0, 0);
					textView.setText(getURLChanged(topicData));

					break;
				case Const.MSGTOPICREFREASH:
					textView = (TextView) findViewById(R.id.label);

					if (textView != null)
						textView.setText(getURLChanged(topicData));
					break;
				case Const.MSGAREA:
					chaToArea(data);
					break;

				case Const.MSGAREAPAGES:
					areaPages(data);
					break;

				case Const.MSGLOGIN:
					checkLogin(data);
					break;
				case Const.MSGAUTOLOGIN:
					checkAutoLogin(data);
					break;
				case Const.MSGPST:
					checkForm(data);
					break;
				case Const.MSGPSTNEW:
					// 发文可能会失败，注意保留文章
					checkRst(data);
					break;

				case Const.MSGVIEWUSER:
					getUserData(data);
					break;

				case Const.MSGSYN:
					checkSyn(data);
					break;
				case Const.MSGSYNFIRST:
					checkSyn(data);
					InitMain();
					break;

				case Const.MSGHOT:
					bbsHot();
					chaToHot("New");
					break;

				case Const.TOP20BOARD:
					convtTOP20Area(data);
					getUrlHtml(forumUrl, Const.MSGFORUM);
					break;
				case Const.MSGFORUM:
					convtForum(data);
					getUrlHtml(recbrdUrl, Const.MSGRECBRD);
					break;
				case Const.MSGRECBRD:
					convtRecbrd(data);
					initAllAreas();
					chaToAreaToGo();
					break;
					

				case Const.MSGMAIL:
					getMailCont();
					chaToMailBox("");
					break;

				case Const.MSGMAILTOPIC:
					chaToMailTopic();
					break;
				case Const.MSGREMAIL:
					checkMailForm(data);
					break;
				default:
					break;

				}
			}
			if (runningTasks < 1) {
				runningTasks = 0;
				progressDialog.dismiss();
			}

		}

		

	};

	/**
	 * 邮件界面翻页
	 */
	private void goToMailPage(int pageNo) {
		int startPage = areaNowTopic + pageNo;
		if (startPage < 0) {
			startPage = 0;
		}
		getUrlHtml(mailURL + "?start=" + startPage, Const.MSGMAIL);

	}

	private void chaToMailTopic() {
		char s = 10;
		String backS = s + "";

		data = data.replaceAll(backS, "<br>");
		Document doc = Jsoup.parse(data);
		Elements scs = doc.getElementsByTag("textarea");

		if (scs.size() != 1) {
			Toast.makeText(TestAndroidActivity.this, "获取邮件失败",
					Toast.LENGTH_SHORT).show();
		} else {
			Element textArea = scs.get(0);
			String infoView = "<br>" + textArea.text();

			String withSmile = StringUtil.addSmileySpans(infoView);
			setContentView(R.layout.mailtopic);

			textView = (TextView) findViewById(R.id.label);
			textView.setText(Html.fromHtml(withSmile));
			textView.setTextSize(txtFonts);
			textView.getBackground().setAlpha(backAlpha);

			Elements as = doc.getElementsByTag("a");
			for (Element element : as) {
				if (element.text().equals("回信")) {
					String ss = (element.getElementsByTag("a")).get(0).attr(
							"href");
					int lastIndexOf = ss.lastIndexOf("Re:");

					String sFir = ss.substring(0, lastIndexOf);
					String sSec = ss.substring(lastIndexOf);

					try {
						sSec = URLEncoder.encode(sSec, "GB2312");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					final String attr = bbsURL + sFir + sSec;

					Button btnPre = (Button) findViewById(R.id.btn_huifu);
					btnPre.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							getUrlHtml(attr, Const.MSGREMAIL);
						}
					});

				}
			}

		}

	}

	private void chaToMailBox(String place) {

		setTitle("我的邮箱");
		setContentView(R.layout.mailbox);
		curStatus = 1;
		LinkAdr = new ArrayList<String>();

		listView = (ListView) findViewById(R.id.topicList);

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : mailList) {

			Map<String, Object> map = new HashMap<String, Object>();

			String title = topicInfo.getTitle();
			SpannableString ss = null;

			if (topicInfo.getMark().length() > 0) {

				if (topicInfo.getMark().equals("unread")) {

					ss = new SpannableString("[sm]" + title);
					ss.setSpan(mailSpan, 0, 4,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					map.put("topicm", "");
					
				} else {
					map.put("topicm", "[" + topicInfo.getMark() + "] ");
				}
			} else {
				map.put("topicm", "");
			}
			map.put("topicau", topicInfo.getNums() + " 寄信人:"
					+ topicInfo.getAuthor());
			if (ss == null)
				ss = new SpannableString(title);
			map.put("topictitle", ss);
			map.put("topicother", topicInfo.getPubDate());
			list.add(map);
			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {
			MyListAdapter adapter = new MyListAdapter(this, list,
					R.layout.vlist, new String[] { "topictitle", "topicau",
							"topicother","topicm" }, new int[] { R.id.topictitle,
							R.id.topicau, R.id.topicother , R.id.topicm});
			listView.setAdapter(adapter);
			// 添加点击
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null)
						return;

					// huifuUrl = topicUrl.replace("bbstcon?", "bbspst?");
					curStatus = 5;

					scrollY = listView.getFirstVisiblePosition() + 1;

					getUrlHtml(topicUrl, Const.MSGMAILTOPIC);

				}
			});
		}

		Button btnPre = (Button) findViewById(R.id.btn_pre);
		btnPre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goToMailPage(-20);
			}
		});

		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goToMailPage(20);
			}
		});

		Button btnMail = (Button) findViewById(R.id.btn_mail);
		btnMail.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				beginMail(null, null, null, null);
			}
		});

		if (place != null) {
			listView.setSelection(mailList.size() - 1);
		} else {
			listView.setSelection(scrollY);
		}

	}

	private void getMailCont() {
		
		
		
		
		
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		int getTopicNo = 0;
		mailList = new ArrayList<TopicInfo>();
		if (tds.size() < 7) {
			
			if (data.contains("您尚未登录")) {
				displayMsg("您尚未登录");
			}
			else
			{
			Toast.makeText(TestAndroidActivity.this, "你的邮箱是空的",
					Toast.LENGTH_SHORT).show();
			}
		} else {
			int i = 6;
			while (i < tds.size()) {

				String no = tds.get(i).text();
				int thisPos = 0;
				try {
					thisPos = Integer.parseInt(no);
				} catch (Exception e) {
					return;
				}

				if (getTopicNo == 0) {
					areaNowTopic = thisPos;
					getTopicNo = 1;
				}

				TopicInfo ti = new TopicInfo();
				ti.setNums(no);
				// 设置title
				ti.setTitle(tds.get(i + 5).text());
				ti.setLink((tds.get(i + 5).getElementsByTag("a")).get(0).attr(
						"href"));

				String date = DateUtil.formatDateToStrNoWeek(DateUtil
						.getDatefromStrNoWeek(tds.get(i + 4).text()));
				if (date == null || date.equals("null"))
					ti.setPubDate(tds.get(i + 4).text());
				else
					ti.setPubDate(date);

				ti.setAuthor(tds.get(i + 3).text());
				ti.setMark(tds.get(i + 2).text());
				Elements element = (tds.get(i + 2).getElementsByTag("img"));
				if (element != null && element.size() > 0) {
					ti.setMark("unread");
				}
				mailList.add(ti);
				i += 6;
			}
		}
		// return tiList;

	}

	private void bbsHot() {
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		int curPos = 0;
		if (!tds.get(0).text().equals("")) {
			displayMsg("各区热点格式有变化，无法解析！");
			return;
		}
		hotList = new ArrayList<TopicInfo>();
		;
		for (int i = 0; i < tds.size(); i++) {
			Element element = tds.get(i);
			String text = element.text();
			if (text.equals("")) {
				// img
				Elements elementsByTag = element.getElementsByTag("img");
				if (elementsByTag.size() < 1)
					continue;
				curPos++;
				TopicInfo ti = new TopicInfo();
				ti.setTitle("fb" + curPos);
				hotList.add(ti);

				continue;
			}
			TopicInfo ti = new TopicInfo();
			hotList.add(ti);
			ti.setLink((element.getElementsByTag("a")).get(0).attr("href"));// 设置title
			int lastIndexOf = text.lastIndexOf('[');
			ti.setTitle("○ " + text.substring(1, lastIndexOf - 1));// 设置title
			ti.setArea(text.substring(lastIndexOf, text.length()));
		}
		hotList.size();
	}

	// 同步收藏夹
	private void checkSyn(String data) {
		Document doc = Jsoup.parse(data);

		Elements as = doc.getElementsByTag("a");
		int ll = areaNamList.size();
		for (Element aTag : as) {
			String href = aTag.attr("href");
			if (href.contains("board?board=")) {
				String tempAreaName = aTag.text().trim().toLowerCase();

				tempAreaName = tempAreaName.replaceFirst(tempAreaName
						.substring(0, 1), tempAreaName.substring(0, 1)
						.toUpperCase());

				if (areaNamList.contains(tempAreaName)) {
					continue;
				}
				areaNamList.add(tempAreaName);

			}
		}
		if (ll < areaNamList.size()) {
			int ii = areaNamList.size() - ll;
			storeAreaName();
			// initAllAreas();
			displayMsg("同步WEB收藏夹成功!更新" + ii + "个已收藏版面");
		} else {
			displayMsg("你的本地收藏夹已经是最新的了！");
		}

	}

	private void convtTOP20Area(String areaData) {
		Document doc = Jsoup.parse(data);

		Elements tds = doc.getElementsByTag("td");

		top20List = new ArrayList<String>();
		if (tds.size() < 12) {
			Toast.makeText(TestAndroidActivity.this, "获取热门讨论区失败",
					Toast.LENGTH_SHORT).show();
		} else {
			int i = 6;
			while (i < tds.size()) {
				
				// ti.setNums(tds.get(i+4).text());
				top20List.add(tds.get(i + 1).text() + " ("
						+ tds.get(i + 2).text() + ")");
				i += 6;
			}
		}
		// return null;

	}
	
	
	private void convtRecbrd(String data) {
		// TODO Auto-generated method stub
		String[] split = data.split("\\{brd:");
		recbrdList = new ArrayList<String>();
		if(split.length<2)
		{
			displayMsg("获取推荐讨论区失败");
		}
		else
		{
			int i = 1;
			while(i<split.length)
			{
				//'JobExpress',bm:'SYSOP yika1985',title:'就业特快 '},
				String[] split2 = split[i].split("'");
				if(split2.length>5)
				{
					recbrdList.add(split2[1]+" ("+split2[5]+")");
				}
				i++;
			}
			
		}
	}
	
	

	private void convtForum(String data) {
		// TODO Auto-generated method stub
		String[] split = data.split("\\{d:");
		forumList = new ArrayList<String>();
		if(split.length<2)
		{
			displayMsg("获取分类精彩讨论区失败");
		}
		else
		{
			int i = 1;
			while(i<split.length)
			{
				String nowStr = split[i];
				String[] split2 = nowStr.split("'");
				forumList.add(fbNameAll.get(i+""));
				int j=1;
				while(j<split2.length-3)
				{
					forumList.add(split2[j]+" ("+split2[j+2]+")");
					j+=4;
				}
				i+=1;
			}
		}
	}
	

	/**
	 * 获取用户信息
	 * 
	 * @param data
	 */
	private void getUserData(String data) {
		char s = 10;
		String backS = s + "";

		data = data.replaceAll(backS, "<br>");
		Document doc = Jsoup.parse(data);

		Elements scs = doc.getElementsByTag("textarea");

		if (scs.size() != 1) {
			Toast.makeText(TestAndroidActivity.this, "获取用户信息失败",
					Toast.LENGTH_SHORT).show();
		} else {
			Element textArea = scs.get(0);
			final String infoView = textArea.text();

			String withSmile = StringUtil.addSmileySpans(infoView);
			LayoutInflater factory = LayoutInflater
					.from(TestAndroidActivity.this);
			final View info = factory.inflate(R.layout.infodlg, null);
			Builder dlg = new AlertDialog.Builder(TestAndroidActivity.this)
					.setTitle("用户信息查询").setView(info).setNegativeButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
								}
							});

			if (isLogin) {
				dlg.setPositiveButton("写信",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String to = infoView.substring(0, infoView
										.indexOf(" ("));
								beginMail(to, null, null, null);

							}
						});

			}

			AlertDialog ad = dlg.create();
			// 发彩照功能
			textView = (TextView) info.findViewById(R.id.tvInfo);
			ScrollView sv = (ScrollView) info.findViewById(R.id.svInfo);
			sv.scrollTo(0, 0);
			textView.setText(Html.fromHtml(withSmile));

			dlg.show();

		}

	}

	/**
	 * 检查发文结果
	 */
	private void checkRst(String data) {

		if (data.contains("信件已寄给")) {
			displayMsg("发送信件成功！");
		} else if (data.contains("错误的收信人帐号")) {
			displayMsg("错误的收信人帐号! ");
		} else if (data.contains("http-equiv='Refresh'")) {

			if (reid.equals("0")) {
				// 发新文章完成
				getUrlHtml(urlString, Const.MSGAREAPAGES);
			} else {
				// 回复完成
				getUrlHtml(topicUrl + "&start=" + nowPos,
						Const.MSGTOPICREFREASH);
			}
			displayMsg("发文成功！");
		} else if (data.contains("修改文章成功")) {

			// 修改完成
			getUrlHtml(topicUrl + "&start=" + nowPos, Const.MSGTOPICREFREASH);

			displayMsg("修改成功！");

		} else if (data.contains("返回本讨论区")) {

			// 回复完成
			getUrlHtml(topicUrl + "&start=" + nowPos, Const.MSGTOPICREFREASH);

			displayMsg("删除成功！");

		}

		else {
			if (data.contains("两次发文间隔过密")) {
				displayMsg("两次发文间隔过密, 请休息几秒后再试! ");
			} else {
				displayMsg("发送失败~发文内容保存在剪贴板上");
			}

			ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

			clipboard.setText(cont);
		}

	}

	/**
	 * 检测是否登录成功
	 * 
	 * @param data
	 */
	private void checkLogin(String data) {

		Document doc = Jsoup.parse(data);

		Elements scs = doc.getElementsByTag("script");

		if (scs.size() == 3) {
			String element = scs.get(1).toString();

			setCookies(element.substring(27, element.length() - 12));

			Toast.makeText(TestAndroidActivity.this, "登录成功！",
					Toast.LENGTH_SHORT).show();
			isLogin = true;
			nowLoginId = loginId;

			Editor editor = sharedPreferences.edit();// 获取编辑器
			editor.putString("isRem", isRem);
			if (isRem.equals("true")) {
				editor.putString("loginId", loginId);
				editor.putString("loginPwd", loginPwd);
			} else {
				editor.putString("loginId", "");
				editor.putString("loginPwd", "");
			}
			editor.commit();

			// 如果收藏夹为空，则去服务器端取得
			if (areaNamList.size() < 1) {
				getUrlHtml(synUrl, Const.MSGSYNFIRST);
			} else {
				InitMain();
			}
			// progressDialog.dismiss();

		} else if (scs.size() == 1) {
			if (data.contains("密码错误") || data.contains("错误的使用者帐号")) {
				Toast.makeText(TestAndroidActivity.this, "用户名或密码错！",
						Toast.LENGTH_SHORT).show();
			} else if (data.contains("此帐号本日login次数过多")) {
				Toast.makeText(TestAndroidActivity.this, "此帐号本日login次数过多！",
						Toast.LENGTH_SHORT).show();
			}

			else {
				Toast.makeText(TestAndroidActivity.this, "登陆失败！",
						Toast.LENGTH_SHORT).show();
			}

			isLogin = false;

		}
		return;
	}

	/**
	 * 检测是否！自动！登录成功
	 * 
	 * @param data
	 */
	private void checkAutoLogin(String data) {
		Document doc = Jsoup.parse(data);
		Elements scs = doc.getElementsByTag("script");
		if (scs.size() == 3) {
			String element = scs.get(1).toString();

			setCookies(element.substring(27, element.length() - 12));

			Toast.makeText(TestAndroidActivity.this, "登录成功！",
					Toast.LENGTH_SHORT).show();
			isLogin = true;
			nowLoginId = loginId;
		} else if (scs.size() == 1) {
			if (data.contains("密码错误") || data.contains("错误的使用者帐号")) {
				Toast.makeText(TestAndroidActivity.this, "用户名或密码错！",
						Toast.LENGTH_SHORT).show();
			} else if (data.contains("此帐号本日login次数过多")) {
				Toast.makeText(TestAndroidActivity.this, "此帐号本日login次数过多！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TestAndroidActivity.this, "登陆失败！",
						Toast.LENGTH_SHORT).show();
			}
			isLogin = false;

		}
		return;
	}

	protected void checkMailForm(String formData) {
		Document doc = Jsoup.parse(formData);
		Elements ins = doc.getElementsByTag("input");
		// progressDialog.dismiss();
		if (ins.size() != 12) {

			// 登录失败，要求重新登录
			if (formData.contains("匆匆过客")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TestAndroidActivity.this);
				builder.setMessage("匆匆过客不能写信~重新登录?").setCancelable(false)
						.setPositiveButton("登录",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										if (isRem.equals("true")) {
											// 自动登录的话，自动登录
											String url = loginURL + "&id="
													+ loginId + "&pw="
													+ loginPwd;
											getUrlHtml(url, Const.MSGAUTOLOGIN);
										} else {
											chaToLogin();
										}
									}
								}).setNegativeButton("算了",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				Toast.makeText(TestAndroidActivity.this, "由于未知错误发文失败",
						Toast.LENGTH_SHORT).show();
			}

		} else {
			String action = ins.get(0).attr("value");
			String title = ins.get(1).attr("value");
			String userId = ins.get(2).attr("value");
			String recont = "";
			try {
				recont = formData
						.substring(
								formData
										.indexOf("<textarea name=text id=text rows=20 cols=80 wrap=physicle>") + 58,
								formData.indexOf("</textarea>"));
			} catch (Exception e) {

			}
			beginMail(userId, title, recont, action);

		}
	}

	String pid;
	String reid;
	String cont;

	/**
	 * 获取发文的窗口
	 * 
	 * @param formData
	 */
	protected void checkForm(String formData) {
		Document doc = Jsoup.parse(formData);
		Elements ins = doc.getElementsByTag("input");
		// progressDialog.dismiss();
		if (ins.size() != 12) {

			if (ins.size() != 5) {
				// 登录失败，要求重新登录
				if (formData.contains("匆匆过客")) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							TestAndroidActivity.this);
					builder.setMessage("你还没登陆呢~重新登录?").setCancelable(false)
							.setPositiveButton("登录",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											if (isRem.equals("true")) {
												// 自动登录的话，自动登录
												String url = loginURL + "&id="
														+ loginId + "&pw="
														+ loginPwd;
												getUrlHtml(url,
														Const.MSGAUTOLOGIN);
											} else {
												chaToLogin();
											}

										}
									}).setNegativeButton("算了",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				} else if (formData.contains("您无权在此讨论区")) {
					Toast.makeText(TestAndroidActivity.this, "您无权在此讨论区发文",
							Toast.LENGTH_SHORT).show();
				} else if (formData.contains("本文不可回复")) {
					Toast.makeText(TestAndroidActivity.this, "本文不可回复",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(TestAndroidActivity.this, "由于未知错误发文失败",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				final String type = ins.get(0).attr("value");
				final String board = ins.get(1).attr("value");
				final String file = ins.get(2).attr("value");
				String recont = formData
						.substring(
								formData
										.indexOf("<textarea name=text rows=20 cols=80 wrap=physicle>") + 50,
								formData.indexOf("</textarea>"));

				LayoutInflater factory = LayoutInflater
						.from(TestAndroidActivity.this);
				final View acdlgView = factory.inflate(R.layout.editdlg, null);
				Builder altDlg = new AlertDialog.Builder(
						TestAndroidActivity.this).setTitle("修改文章").setView(
						acdlgView).setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								EditText titleEdit = (EditText) acdlgView
										.findViewById(R.id.edt_cont);
								String cont = StringUtil.getStrBetter(titleEdit
										.getText().toString());

								sendEdit(cont, type, board, file);
							}

						});

				altDlg.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {

							}
						});

				AlertDialog dlg = altDlg.create();
				// EditText titleEdit = (EditText) acdlgView
				// .findViewById(R.id.edt_title);

				EditText titleEdit = (EditText) acdlgView
						.findViewById(R.id.edt_cont);
				titleEdit.setText(recont);

				dlg.show();

			}

		} else {

			String title = ins.get(0).attr("value");
			pid = ins.get(1).attr("value");
			reid = ins.get(2).attr("value");
			String recont = "";
			try {
				recont = formData
						.substring(
								formData
										.indexOf("<textarea name=text rows=20 cols=80 wrap=physicle>") + 50,
								formData.indexOf("</textarea>"));
			} catch (Exception e) {

			}
			final String extraRecont = recont;
			LayoutInflater factory = LayoutInflater
					.from(TestAndroidActivity.this);
			final View acdlgView = factory.inflate(R.layout.acdlg, null);
			Builder altDlg = new AlertDialog.Builder(TestAndroidActivity.this)
					.setTitle("发文").setView(acdlgView).setPositiveButton("发表",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									EditText titleEdit = (EditText) acdlgView
											.findViewById(R.id.edt_title);
									String title = titleEdit.getText()
											.toString();
									titleEdit = (EditText) acdlgView
											.findViewById(R.id.edt_cont);
									cont = StringUtil.getStrBetter(titleEdit
											.getText().toString());
									// 引用原文
									CheckBox cb = (CheckBox) acdlgView
											.findViewById(R.id.cb_recont);
									if (cb.isChecked() && extraRecont != null
											&& extraRecont.length() > 1) {
										cont += " " + extraRecont.substring(2);
									}
									sendTopic(title, cont);
								}

							});

			if (extraRecont.length() < 4) {
				altDlg.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {

							}
						});
			} else {
				altDlg.setNegativeButton("快速回复",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								if (fastReList.length < 1)
									return;
								if (isMoreFast) {
									EditText titleEdit = (EditText) acdlgView
											.findViewById(R.id.edt_title);
									String title = titleEdit.getText()
											.toString();
									String reText = fastReList[0];
									sendTopic(title, reText);
									return;
								}
								AlertDialog.Builder builder = new AlertDialog.Builder(
										TestAndroidActivity.this);

								builder.setTitle("选择要使用的快捷回复：");

								builder.setSingleChoiceItems(fastReList, 0,
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialoginterface,
													int i) {

												EditText titleEdit = (EditText) acdlgView
														.findViewById(R.id.edt_title);
												String title = titleEdit
														.getText().toString();
												String reText = fastReList[i]
														+ "\n";
												dialoginterface.dismiss();
												sendTopic(title, reText);
											}
										});

								builder.setPositiveButton("取消",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialoginterface,
													int i) {

											}
										});
								builder.create().show();

							}
						});
			}

			AlertDialog dlg = altDlg.create();
			EditText titleEdit = (EditText) acdlgView
					.findViewById(R.id.edt_title);
			CheckBox cb = (CheckBox) acdlgView.findViewById(R.id.cb_recont);

			if (extraRecont.length() < 4) {

				cb.setChecked(false);
				cb.setVisibility(CheckBox.INVISIBLE);
			} else {
				cb.setChecked(true);
			}
			titleEdit.setText(title);

			if (sLength < 300) {
				titleEdit = (EditText) acdlgView.findViewById(R.id.edt_cont);

				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) titleEdit
						.getLayoutParams(); // 取控件mGrid当前的布局参数
				linearParams.height = 90;// 当控件的高强制设成75象素

				titleEdit.setLayoutParams(linearParams);

			}

			dlg.show();

		}
	}

	private void sendEdit(String cont, String type, String board, String file) {

		String url = "http://bbs.nju.edu.cn/bbsedit?board=" + board + "&file="
				+ file + "&type=" + type;

		// +"&text="+;

		NameValuePair[] newVp = { new NameValuePair("text", cont) };

		nvpCont = newVp;

		getUrlHtml(url, Const.MSGPSTNEW);

	}

	private void sendMail(String to, String title, String cont, String action) {
		// cont = StringUtil.getStrBetter(cont);
		// 手机签名
		if (isBackWord && backWords != null && backWords.length() > 0) {
			cont += "\n-\n" + signColor + backWords + "[m\n";
		}

		try {
			title = URLEncoder.encode(title, "GB2312");
			String url = "http://bbs.nju.edu.cn/bbssndmail?pid=0" + "&title="
					+ title + "&userid=" + to + "&signature=1";
			if (action != null) {
				//
				url += "&action=" + action;
			}

			// +"&text="+;

			NameValuePair[] newVp = { new NameValuePair("text", cont) };

			nvpCont = newVp;

			getUrlHtml(url, Const.MSGPSTNEW);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

	}

	private void sendTopic(String title, String cont) {
		// 手机签名
		if (isBackWord && backWords != null && backWords.length() > 0) {
			cont += "\n-\n" + signColor + backWords + "[m\n";
		}

		try {
			title = URLEncoder.encode(title, "GB2312"); // new
			// String((title.replace(" ",
			// "%20")).getBytes("UTF-8"),"gb2312");
			String url = "http://bbs.nju.edu.cn/bbssnd?board=" + curAreaName
					+ "&title=" + title + "&pid=" + pid + "&reid=" + reid
					+ "&signature=1";
			// +"&text="+;

			NameValuePair[] newVp = { new NameValuePair("text", cont) };

			nvpCont = newVp;

			getUrlHtml(url, Const.MSGPSTNEW);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void setCookies(String cookStr) {

		char[] charArray = cookStr.toCharArray();
		int i = 0;
		int sp1 = 0;
		int sp2 = 0;
		for (char c : charArray) {
			if (sp1 == 0 && !Character.isDigit(c)) {
				sp1 = i;

			} else if (c == '+') {
				sp2 = i;
				break;
			}
			i++;
		}
		String NUM = (Integer.parseInt(cookStr.substring(0, sp1)) + 2) + "";
		String id = cookStr.substring(sp1 + 1, sp2);
		String KEY = (Integer.parseInt(cookStr.substring(sp2 + 1)) - 2) + "";
		// saveMyCookie( NUM, id , KEY);
		NetTraffic.setMyCookie(NUM, id, KEY);
	}

	/**
	 * 将由数据转化为ListView可读的形式 供10大使用
	 */
	private void convtTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : top10TopicList) {

			Map<String, Object> map = new HashMap<String, Object>();

			String title = topicInfo.getTitle();
			

			map.put("topictitle", title);

			map.put("topicau", "作者:" + topicInfo.getAuthor());

			map.put("topicother", "信区:" + topicInfo.getArea());

			list.add(map);

			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {

			SimpleAdapter adapter = new SimpleAdapter(this, list,
					R.layout.vlist, new String[] { "topictitle", "topicau",
							"topicother" }, new int[] { R.id.topictitle,
							R.id.topicau, R.id.topicother });
			listView.setAdapter(adapter);
			// 添加点击
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null)
						return;
					huifuUrl = topicUrl.replace("bbstcon?", "bbspst?");
					curStatus = 1;
					nowPos = 0;
					getUrlHtml(topicUrl, Const.MSGTOPIC);

				}
			});
		}
	}

	/**
	 * 将由HTML页面转出的数据转化为ListView可读的形式 供讨论区使用
	 */
	private void convtAreaTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : areaTopic) {

			Map<String, Object> map = new HashMap<String, Object>();

			String title = topicInfo.getTitle();
			int begin = title.length();
			title += "  (" + topicInfo.getHot() + ")";
			String[] split = topicInfo.getHot().split("/");
			SpannableString sp = null;

			if (split.length == 2) {
				int re = Integer.parseInt(split[0]);
				int watch = Integer.parseInt(split[1]);
				if (re > 9 || watch > 500) {
					sp = new SpannableString(title + "[sm]");
					sp.setSpan(hotTopicSpan, title.length(),
							title.length() + 4,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
			if (sp == null) {
				sp = new SpannableString(title);
			}
			sp.setSpan(listColorSpan, begin, title.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			sp.setSpan(absoluteSizeSpan, begin, title.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			map.put("topictitle", sp);
			String place = "";
			if (topicInfo.getNums() == null || topicInfo.getNums().equals("")) {
				place = "";
			} else {
				place = topicInfo.getNums();
			}

			if (topicInfo.getMark().length() > 0) {
				map.put("topicm", "[" + topicInfo.getMark() + "] ");
			} else {
				map.put("topicm", "");

			}
			map.put("topicau", place + " 作者:" + topicInfo.getAuthor());
			map.put("topicother", topicInfo.getPubDate());

			list.add(map);

			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {

			MyListAdapter adapter = new MyListAdapter(this, list,
					R.layout.vlist, new String[] { "topictitle", "topicau",
							"topicother","topicm" }, new int[] { R.id.topictitle,
							R.id.topicau, R.id.topicother, R.id.topicm  });
			listView.setAdapter(adapter);
			// 添加点击
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null)
						return;

					huifuUrl = topicUrl.replace("bbstcon?", "bbspst?");
					curStatus = 2;
					nowPos = 0;
					scrollY = listView.getFirstVisiblePosition() + 1;

					getUrlHtml(topicUrl, Const.MSGTOPIC);

				}
			});
		}
	}

	/**
	 * 将由HTML页面转出的数据转化为ListView可读的形式 供各区热点使用
	 */
	private void convtHotTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : hotList) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (topicInfo.getArea() == null || topicInfo.getArea().length() < 1) {
				map.put("img", fbAll.get(topicInfo.getTitle()));
			} else {

				String title = topicInfo.getTitle();

				map.put("topictitle", title);

				map.put("topicau", topicInfo.getArea());

			}
			list.add(map);

			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {

			MyImageListAdapter adapter = new MyImageListAdapter(this, list,
					R.layout.hotlist, new String[] { "topictitle", "img",
							"topicau" }, new int[] { R.id.topictitle,
							R.id.itemImage, R.id.topicau });
			listView.setAdapter(adapter);
			// 添加点击
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null || topicUrl.length() < 27)
						return;

					huifuUrl = topicUrl.replace("bbstcon?", "bbspst?");
					curStatus = 3;
					nowPos = 0;
					scrollY = listView.getFirstVisiblePosition();
					getUrlHtml(topicUrl, Const.MSGTOPIC);

				}
			});
		}
	}
	int lastPar = -1;
	int lastChd = -1;
	private void chaToAreaToGo() {

		setTitle("跳转讨论区");

		curStatus = 1;
		setContentView(R.layout.gotoarea);

		AutoCompleteTextView secondPwd = (AutoCompleteTextView) findViewById(R.id.area_edit);
		if (secondPwd.getAdapter() == null) {
			secondPwd.setAdapter(bbsAlladapter);
			secondPwd.setThreshold(1);
		}
		Button btnBack = (Button) findViewById(R.id.btn_go);
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText secondPwd = (EditText) findViewById(R.id.area_edit);
				String inputPwd = secondPwd.getText().toString();
				getToAreaWithName(inputPwd);
			}
		});
		if (parentList == null || parentList.size() < 2)
			initAllAreas();

		android.widget.SimpleExpandableListAdapter adapter = new android.widget.SimpleExpandableListAdapter(
				this, parentList, R.layout.explistparent,
				new String[] { "TITLE" }, new int[] { android.R.id.text1 },
				allChildList, R.layout.explistchild, new String[] { "TITLE" },
				new int[] { android.R.id.text1 });
		// create child's OnChildClickListener
		android.widget.ExpandableListView listView = (android.widget.ExpandableListView) findViewById(R.id.area_view);

		// Adapter set
		listView.setAdapter(adapter);
		listView.setOnChildClickListener(new android.widget.ExpandableListView.OnChildClickListener() {

					public boolean onChildClick(
							android.widget.ExpandableListView parent, View v,
							int groupPosition, int childPosition, long id) {
						
						//parent.get
						
						Map<String, Object> childMap = allChildList.get(
								groupPosition).get(childPosition);
						String name = (String) childMap.get("TITLE");
						if (name == null || name.length() < 1||name.contains("※"))
							return false;
						int indexOf = name.indexOf('(');
						if (indexOf > 0) {
							name = name.substring(0, indexOf);
						}
						getToAreaWithName(name);
						lastPar = groupPosition;
						lastChd=childPosition;
						return false;
					}
				});
		setIndexBtns(2);
		
		if(lastPar>-1)
		{
			
			listView.expandGroup(lastPar);
			
			
			listView.setSelectedChild(lastPar, lastChd, true);
			lastPar = -1;
			lastChd = -1;
		}

	}

	private void getToAreaWithName(String name) {
		if (name == null || name.length() < 1)
			return;
		name = name.trim();
		String areaText = bbsAll.get(name);
		areaText = areaText == null ? name : areaText;
		areaText = areaText.toLowerCase();
		areaText = areaText.replaceFirst(areaText.substring(0, 1), areaText
				.substring(0, 1).toUpperCase());
		urlString = getResources().getString(R.string.areaStr) + areaText;
		curAreaName = "" + areaText;
		getUrlHtml(urlString, Const.MSGAREA);
	}

	List<String> top20List;
	List<String> forumList;
	List<String> recbrdList;

	private void initAllAreas() {
		parentList = new ArrayList<Map<String, Object>>();
		allChildList = new ArrayList<List<Map<String, Object>>>();

		Map<String, Object> parentData = new HashMap<String, Object>();
		parentData.put("TITLE", "我的收藏");
		parentList.add(parentData);

		List<Map<String, Object>> childList = new ArrayList<Map<String, Object>>();

		for (String s : areaNamList) {
			Map<String, Object> childData = new HashMap<String, Object>();
			childData.put("TITLE", s);
			childList.add(childData);
		}
		allChildList.add(childList);

		if (top20List != null) {
			parentData = new HashMap<String, Object>();
			parentData.put("TITLE", "今日热门");
			parentList.add(parentData);

			// convtTOP20Area(data);

			childList = new ArrayList<Map<String, Object>>();
			for (String topicInfo : top20List) {
				Map<String, Object> childData = new HashMap<String, Object>();
				childData.put("TITLE",topicInfo);
				childList.add(childData);
			}
			allChildList.add(childList);
		}
		
		if (forumList != null) {
			parentData = new HashMap<String, Object>();
			parentData.put("TITLE", "分类精彩");
			parentList.add(parentData);

			// convtTOP20Area(data);

			childList = new ArrayList<Map<String, Object>>();
			for (String topicInfo : forumList) {
				Map<String, Object> childData = new HashMap<String, Object>();
				childData.put("TITLE",topicInfo);
				childList.add(childData);
			}
			allChildList.add(childList);
		}
		
		if (recbrdList != null) {
			parentData = new HashMap<String, Object>();
			parentData.put("TITLE", "首页推荐");
			parentList.add(parentData);

			// convtTOP20Area(data);

			childList = new ArrayList<Map<String, Object>>();
			for (String topicInfo : recbrdList) {
				Map<String, Object> childData = new HashMap<String, Object>();
				childData.put("TITLE",topicInfo);
				childList.add(childData);
			}
			allChildList.add(childList);
		}

		

	}

	boolean isNext = true;
	boolean isPrev = true;

	private void chaToHot(String HotData) {

		setTitle("各区热点");

		curStatus = 1;
		setContentView(R.layout.hot);

		listView = (ListView) findViewById(R.id.topicList);
		convtHotTopics();
		if (HotData == null) {
			listView.setSelection(scrollY);
		}
		setIndexBtns(4);

	}

	/**
	 * 跳转到讨论区界面
	 * 
	 * @param AreaData
	 */
	private void chaToArea(String AreaData) {

		if (AreaData != null && AreaData.contains("错误! 错误的讨论区")) {
			Toast.makeText(TestAndroidActivity.this, "该讨论区不存在！",
					Toast.LENGTH_SHORT).show();
			return;
		}

		setContentView(R.layout.area);
		curStatus = 4;
		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getUrlHtml(newUrl, Const.MSGPST);

			}
		});
		Button btnPre = (Button) findViewById(R.id.btn_pre);
		btnPre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goToPage(-21);
			}
		});

		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				goToPage(21);
			}
		});

		setTitle("当前讨论区：" + curAreaName);

		ImageTextButton btnLike = (ImageTextButton) findViewById(R.id.btn_like);

		if (areaNamList.contains(curAreaName)) {
			// btnLike.setBackgroundDrawable(drawableDis);
			btnLike.setText("退 订");
			btnLike.setIcon(R.drawable.fav);
		} else {
			// btnLike.setBackgroundDrawable(drawableFav);
			btnLike.setText("收 藏");
			btnLike.setIcon(R.drawable.unfav);
			
		}

		btnLike.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageTextButton btnLike = (ImageTextButton) findViewById(R.id.btn_like);
				if (areaNamList.contains(curAreaName)) {
					areaNamList.remove(curAreaName);
					// btnLike.setBackgroundDrawable(drawableFav);
					btnLike.setText("收 藏");
					btnLike.setIcon(R.drawable.unfav);
					

				} else {
					areaNamList.add(curAreaName);
					// btnLike.setBackgroundDrawable(drawableDis);
					btnLike.setText("退 订");
					btnLike.setIcon(R.drawable.fav);
				}
				storeAreaName();
			}

		});

		if (AreaData != null) {
			newUrl = "http://bbs.nju.edu.cn/bbspst?board=" + curAreaName;
			areaTopic = getAreaTopic(AreaData);
		}
		listView = (ListView) findViewById(R.id.topicList);

		convtAreaTopics();
		if (AreaData == null) {
			// listView.requestFocusFromTouch();

			listView.setSelection(scrollY);
			//listView.getSelectedView().
			//listView.p
		} else {

			listView.setSelection(areaTopic.size() - 1);

		}

	}

	private void storeAreaName() {
		String areaName = "";
		for (String name : areaNamList) {
			areaName += name + ",";
		}
		if (areaName.length() > 1) {
			areaName = areaName.substring(0, areaName.length() - 1);
		}
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("areaName", areaName);
		editor.commit();

		initAllAreas();
	}

	/**
	 * 讨论区界面翻页
	 * 
	 * @param AreaData
	 */
	private void goToPage(int pageNo) {
		int startPage = areaNowTopic + pageNo;
		if (startPage < 0) {
			startPage = 0;
		}

		getUrlHtml(urlString + "&start=" + startPage, Const.MSGAREAPAGES);

	}

	private void areaPages(String AreaData) {
		areaTopic = getAreaTopic(AreaData);
		listView = (ListView) findViewById(R.id.topicList);
		convtAreaTopics();
		listView.setSelection(areaTopic.size() - 1);
	}

	/**
	 * 解析获取的页面 处理讨论区的话题列表
	 * 
	 * @param data
	 * @return
	 */
	private List<TopicInfo> getAreaTopic(String data) {
		List<TopicInfo> tiList = new ArrayList<TopicInfo>();
		Document doc = Jsoup.parse(data);
		Elements tds = doc.getElementsByTag("td");
		int curPos = 0;
		int getTopicNo = 0;
		while (curPos < tds.size()) {
			if (curPos != 0) {
				TopicInfo ti = new TopicInfo();
				ti.setLink((tds.get(curPos + 4).getElementsByTag("a")).get(0)
						.attr("href"));// 设置title
				ti.setTitle(tds.get(curPos + 4).text());// 设置title

				String date = DateUtil.formatDateToStrNoWeek(DateUtil
						.getDatefromStrNoWeek(tds.get(curPos + 3).text()));
				if (date == null || date.equals("null"))

					ti.setPubDate(tds.get(curPos + 3).text());
				else
					ti.setPubDate(date);
				ti.setAuthor(tds.get(curPos + 2).text());
				ti.setMark(tds.get(curPos + 1).text());
				ti.setHot(tds.get(curPos + 5).text());
				String notext = tds.get(curPos).text();
				ti.setNums(notext);
				tiList.add(ti);
				if (getTopicNo == 0) {

					if (notext != "" && Character.isDigit(notext.charAt(0))) {
						areaNowTopic = Integer.parseInt(notext);
						getTopicNo = 1;
					}
				}

			}

			curPos += 6;
		}

		return tiList;
	}

	class MyURLSpan extends ClickableSpan {

		private String mUrl;
		private boolean underline = false;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setUnderlineText(underline);// 一般链下面都有一条线，挺恶心， 该方法可以去掉那条线
			// ds.setColor(Color.rgb(0, 0, 237));// 改变链接的颜色设置
		}

		@Override
		public void onClick(View widget) {
			// 此处写你的处理逻辑
			// System.out.println("123123");
			// processHyperLinkClick(text); //点击超链接时调用
			if (mUrl.toLowerCase().startsWith("http:")
					&& (mUrl.toLowerCase().endsWith(".jpg")
							|| mUrl.toLowerCase().endsWith(".png")
							|| mUrl.toLowerCase().endsWith(".jpeg") || mUrl
							.toLowerCase().endsWith(".gif"))) {
				Intent intent = new Intent(TestAndroidActivity.this,
						ImageActivity.class);
				intent.putExtra("mUrl", mUrl);

				startActivity(intent);
			} else if (mUrl.contains("bbsqry?userid")) {
				// 查看用户
				getUrlHtml(mUrl, Const.MSGVIEWUSER);
			} else if (mUrl.contains("bbspst?board")) {
				// 回复
				getUrlHtml(mUrl, Const.MSGPST);
			}

			else if (mUrl.contains("bbsedit?board")) {
				// 修改
				getUrlHtml(mUrl, Const.MSGPST);
			}

			else if (mUrl.contains("bbsdel?board")) {
				// 删除
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TestAndroidActivity.this);

				builder.setTitle("提示").setMessage("确定删除本文？").setPositiveButton(
						"确定", new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								getUrlHtml(mUrl, Const.MSGPSTNEW);
							}

						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();

			}

		}
	}

	public SpannableStringBuilder getURLChanged(Spanned topicData) {
		URLSpan[] spans = topicData.getSpans(0, topicData.length(),
				URLSpan.class);
		SpannableStringBuilder style = new SpannableStringBuilder(topicData);
		for (URLSpan url : spans) {
			style.removeSpan(url);
			MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
			style.setSpan(myURLSpan, topicData.getSpanStart(url), topicData
					.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		}
		return style;
	}

	/**
	 * 跳转到某个话题界面
	 * 
	 * @param AreaData
	 */
	private void chaToTopic(Spanned topicData) {

		setContentView(R.layout.topic);

		SpannableStringBuilder urlChanged = getURLChanged(topicData);

		textView = (TextView) findViewById(R.id.label);
		textView.setText(urlChanged);
		textView.setTextSize(txtFonts);
		textView.setMovementMethod(LinkMovementMethod.getInstance());

		textView.getBackground().setAlpha(backAlpha);
		if (isTouch) {
			textView.setOnTouchListener(this);
			textView.setFocusable(true);
			textView.setLongClickable(true);
		}

		Button btnBack = (Button) findViewById(R.id.btn_back);
		btnBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				//
				// if (curStatus == 1) {
				// chaToMain();
				// if (top10TopicList != null) {
				// convtTopics();
				// }
				// } else if (curStatus == 2) {
				// chaToArea(null);
				// }
				// else if (curStatus == 3) {
				// chaToHot(null);
				// }

				urlString = "http://bbs.nju.edu.cn/bbstdoc?board="
						+ curAreaName;
				getUrlHtml(urlString, Const.MSGAREA);

			}
		});

		Button btnHuifu = (Button) findViewById(R.id.btn_huifu);
		btnHuifu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				getUrlHtml(huifuUrl, Const.MSGPST);
			}
		});

		Button btnPre = (Button) findViewById(R.id.btn_pre);
		btnPre.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (nowPos < 1) {
					Toast.makeText(TestAndroidActivity.this, "当前为第一页！",
							Toast.LENGTH_SHORT).show();
					return;

				}
				nowPos = nowPos - 30;
				getUrlHtml(topicUrl + "&start=" + nowPos, Const.MSGTOPICNEXT);

			}
		});

		Button btnNext = (Button) findViewById(R.id.btn_next);
		btnNext.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (isNext) {
					nowPos = nowPos + 30;
					getUrlHtml(topicUrl + "&start=" + nowPos,
							Const.MSGTOPICNEXT);
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							TestAndroidActivity.this);
					builder.setMessage("已是最后一页，是否刷新当前页?").setCancelable(false)
							.setPositiveButton("刷新",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											getUrlHtml(topicUrl + "&start="
													+ nowPos,
													Const.MSGTOPICREFREASH);
										}
									}).setNegativeButton("算了",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();
				}

			}
		});

	}

	private String dataUrl = "";
	private int datamsg = -1;
	NameValuePair[] nvpCont = null;
	Thread imageTrd;

	private void getUrlHtml(String url, int msg) {
		if (msg == 123 || progressDialog == null || !progressDialog.isShowing()) {
			progressDialog = ProgressDialog.show(TestAndroidActivity.this,
					"请稍等...", "抓取网页信息中...", true);
		}
		runningTasks++;

		dataUrl = url;
		datamsg = msg;
		new Thread() {

			@Override
			public void run() {
				// 需要花时间计算的方法
				try {
					if (nvpCont == null) {
						data = NetTraffic.getHtmlContent(dataUrl);
					} else {
						data = NetTraffic.postHtmlContent(dataUrl, nvpCont);
						nvpCont = null;
					}
					// Thread.sleep(5000);
				} catch (Exception e) {
					data = "error";
				}

				if (datamsg == Const.MSGTOPIC || datamsg == Const.MSGTOPICNEXT
						|| datamsg == Const.MSGTOPICREFREASH) {

					if (imageTrd != null && imageTrd.isAlive()) {
						imageTrd.setName("NoUse");
					}
					topicWithImg = false;
					final String topicDataInfo = StringUtil.getTopicInfo(data,
							nowPos, isIP, isWifi, isPic, nowLoginId);
					if (topicDataInfo != null) {
						isNext = StringUtil.isNext;
						if (StringUtil.curAreaName != null
								&& !StringUtil.curAreaName.equals("byztm"))
							curAreaName = StringUtil.curAreaName;
						topicWithImg = StringUtil.topicWithImg;

						topicData = Html.fromHtml(topicDataInfo,
								new Html.ImageGetter() {
									public Drawable getDrawable(String source) {

										Drawable drawable = null;
										if ("xian".equals(source)) {
											drawable = xianDraw;
											drawable.setBounds(0, 0, sWidth, 2);
										} else if (source.startsWith("[")) {
											try {
												drawable = fetchDrawable(source);
											} catch (Exception e) {
												return null;
											}
											if (drawable == null)
												return null;
											int iw = drawable
													.getIntrinsicWidth();
											drawable
													.setBounds(
															0,
															0,
															iw,
															drawable
																	.getIntrinsicHeight());
										}
										return drawable;

									}
								}, null);
						if (topicWithImg) {

							imageTrd = new Thread(topicDataInfo) {

								@Override
								public void interrupt() {
									this.stop();
								}

								@Override
								public void run() {
									// 需要花时间计算的方法
									topicData = Html.fromHtml(topicDataInfo,
											new Html.ImageGetter() {

												public Drawable getDrawable(
														String source) {

													Drawable drawable = null;
													if ("xian".equals(source)) {
														drawable = xianDraw;
														drawable.setBounds(0,
																0, sWidth, 2);
													}

													else if (source
															.startsWith("http")
															|| source
																	.startsWith("[")) {
														try {
															drawable = fetchDrawable(source);
														} catch (Exception e) {
															return null;
														}
														if (drawable == null)
															return null;
														int iw = drawable
																.getIntrinsicWidth();
														drawable
																.setBounds(
																		0,
																		0,
																		iw,
																		drawable
																				.getIntrinsicHeight());
													}
													return drawable;

												}
											}, null);
									if (this.getName() != null
											&& !this.getName().equals("NoUse")) {
										sendMsg(Const.MSGTOPICREFREASH);
									}

								}
							};
							imageTrd.start();
						}
					}

				}
				sendMsg(datamsg);
			}
		}.start();

	}

	HashMap<String, SoftReference<Drawable>> drawableMap = new HashMap<String, SoftReference<Drawable>>();

	public Drawable fetchDrawable(String source) {
		SoftReference<Drawable> drawableRef = drawableMap.get(source);
		if (drawableRef != null) {
			Drawable drawable = drawableRef.get();
			if (drawable != null)
				return drawable;

			drawableMap.remove(source);
		}
		Drawable drawable = null;
		if (source.startsWith("[")) {
			Resources res = getResources();
			Integer i = smilyAll.get(source);
			if (i != null) {
				drawable = res.getDrawable(i);
			} else
				return null;
		} else if (source.startsWith("http")) {
			drawable = zoomDrawable(source);
		} else {
			return null;
		}

		drawableRef = new SoftReference<Drawable>(drawable);
		drawableMap.put(source, drawableRef);

		return drawable;

	}

	/**
	 * 根据图片网络地址获取图片的byte[]类型数据
	 * 
	 * @param urlPath
	 *            图片网络地址
	 * @return 图片数据
	 */
	public byte[] getImageFromURL(String urlPath) {
		byte[] data = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			// conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			is = conn.getInputStream();
			if (conn.getResponseCode() == 200) {
				data = readInputStream(is);
			} else {
				data = null;
				return data;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 读取InputStream数据，转为byte[]数据类型
	 * 
	 * @param is
	 *            InputStream数据
	 * @return 返回byte[]数据
	 */
	public byte[] readInputStream(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = baos.toByteArray();
		try {
			is.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 根据网络图片地址集批量获取网络图片
	 * 
	 * @param urlPath
	 *            网络图片地址数组
	 * @return 返回Bitmap数据类型的数组
	 */
	public Drawable zoomDrawable(String urlPath) {

		Bitmap bitmaps;

		byte[] imageByte = getImageFromURL(urlPath.trim());

		// 以下是把图片转化为缩略图再加载
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
				imageByte.length, options);

		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		//options.inDensity = sdensity;

		int widthRatio = (int) Math.ceil(options.outWidth * 1.0 / sWidth);
		int heightRatio = (int) Math.ceil(options.outHeight * 1.0 / sLength);
		if (widthRatio > 1 || heightRatio > 1) {
			if (widthRatio > heightRatio) {
				options.inSampleSize = widthRatio;
			} else {
				options.inSampleSize = heightRatio;
			}
		}
		if (sWidth < 260)
			options.inSampleSize = options.inSampleSize * 2;
		
		bitmaps = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length,
				options);
	
		if(options.inSampleSize>1)
		return new BitmapDrawable(null, bitmaps);
		else
			return new BitmapDrawable(this.getResources(), bitmaps);

	}

	private void exitPro() {
		new AlertDialog.Builder(TestAndroidActivity.this).setTitle("提示")
				.setMessage("确定退出吗？").setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								getUrlHtml(loginoutURL, 123);

								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {

									e.printStackTrace();
								}
								android.os.Process
										.killProcess(android.os.Process.myPid());
							}

						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).show();
	}

	private void sendMsg(int meg) {
		Message msg = new Message();
		msg.what = meg;
		handler.sendMessage(msg);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

		getUrlHtml(loginoutURL, 123);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		System.gc();

		System.exit(0);

	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		return mGestureDetector.onTouchEvent(arg1);
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE) {

			getUrlHtml(huifuUrl, Const.MSGPST);

		} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE) {
			retBtn();
		}

		return false;
	}

	public void onLongPress(MotionEvent arg0) {

	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onShowPress(MotionEvent arg0) {

	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
		float x = arg0.getRawX();
		float y = arg0.getRawY();
		// 点击上翻和点击下翻
		if (y > sv.getHeight() - sLength / 6 && x > (sWidth * 3 / 4)) {
			sv.scrollBy(0, sv.getHeight() - 20);
			return true;
		}
		if (y < sLength / 3 && x > (sWidth * 3 / 4)) {
			sv.scrollBy(0, 20 - sv.getHeight());
			return true;
		}
		return false;
	}

}