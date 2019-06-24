/**
 * TODO LISENSE
 */
package com.goxr3plus.xr3player.controllers.chromium;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.goxr3plus.xr3player.application.Main;
import com.goxr3plus.xr3player.services.chromium.ChromiumUpdaterService;
import com.goxr3plus.xr3player.utils.general.InfoTool;
import com.goxr3plus.xr3player.utils.general.OSTool;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.teamdev.jxbrowser.chromium.Browser;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;

/**
 * @author GOXR3PLUS
 *
 */
public class WebBrowserController extends StackPane {

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass().getName());

	public static final String VERSION = "Version 3." + Main.APPLICATION_VERSION + ".0";

	public static boolean MOVING_TITLES_ENABLED = true;

	public final ChromiumFullScreenController chromiumFullScreenController = new ChromiumFullScreenController();

	public final ChromiumUpdaterService chromiumUpdaterService = new ChromiumUpdaterService(this);

	protected final Set<String> addBlockSet = new HashSet<>();
	// ------------------------------------------------------------

	@FXML
	private JFXTabPane tabPane;

	@FXML
	private JFXButton youtube;

	@FXML
	private JFXButton soundCloud;

	@FXML
	private JFXButton facebook;

	@FXML
	private JFXButton printerest;

	@FXML
	private JFXButton twitter;

	@FXML
	private JFXButton linkedIn;

	@FXML
	private JFXButton dropBox;

	@FXML
	private JFXButton gmail;

	@FXML
	private JFXButton googleDrive;

	@FXML
	private JFXButton googleMaps;

	@FXML
	private JFXButton addTab;

	// -------------------------------------------------------------

	/**
	 * Constructor
	 */
	public WebBrowserController() {

		// ------------------------------------FXMLLOADER
		// ----------------------------------------
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource(InfoTool.BROWSER_FXMLS + "WebBrowserController.fxml"));
		loader.setController(this);
		loader.setRoot(this);

		try {
			loader.load();
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "", ex);
		}

		// addBlockSet
		addBlockSet.add("fmovies.com");
	}

	/**
	 * Called as soon as .fxml is initialized [[SuppressWarningsSpartan]]
	 */
	@FXML
	private void initialize() {

		// tabPane
		tabPane.getTabs().clear();
		createAndAddNewTab();

		// addTab
		addTab.setOnAction(a -> createAndAddNewTab());

		// Extra Stuff
		youtube.setOnAction(a -> createTabAndSelect("https://www.youtube.com/"));

		soundCloud.setOnAction(a -> createTabAndSelect("https://www.soundcloud.com"));

		facebook.setOnAction(a -> createTabAndSelect("https://www.facebook.com"));

		printerest.setOnAction(a -> createTabAndSelect("https://www.pinterest.com"));

		twitter.setOnAction(a -> createTabAndSelect("https://www.twitter.com"));

		linkedIn.setOnAction(a -> createTabAndSelect("https://www.linkedin.com/"));

		dropBox.setOnAction(a -> createTabAndSelect("https://www.dropbox.com"));

		gmail.setOnAction(a -> createTabAndSelect("https://www.gmail.com"));

		googleDrive.setOnAction(a -> createTabAndSelect("https://www.google.com"));

		googleMaps.setOnAction(a -> createTabAndSelect("https://maps.google.com/"));
	}

	/**
	 * Creates a new Tab and selects it
	 * 
	 * @param url
	 */
	public void createTabAndSelect(String url) {
		tabPane.getSelectionModel().select(createAndAddNewTab(url).getTab());
	}

	/**
	 * Creates a new tab for the web browser ->Directing to a specific web site
	 * [[SuppressWarningsSpartan]]
	 * 
	 * @param webSite
	 */
	public WebBrowserTabController createAndAddNewTab(String... webSite) {

		// Create
		WebBrowserTabController webBrowserTab = createNewTab(webSite);

		// Add the tab
		tabPane.getTabs().add(webBrowserTab.getTab());

		return webBrowserTab;
	}

	/**
	 * Creates a new tab for the web browser ->Directing to a specific web site
	 * [[SuppressWarningsSpartan]]
	 * 
	 * @param webSite
	 */
	public WebBrowserTabController createNewTab(String... webSite) {

		// Create
		Tab tab = new Tab("");
		WebBrowserTabController webBrowserTab = new WebBrowserTabController(this, tab,
				webSite.length == 0 ? null : webSite[0]);
		tab.setOnClosed(c -> {

			// Check the tabs number
			if (tabPane.getTabs().isEmpty())
				createAndAddNewTab();

			// Dispose the browser
			disposeBrowser(webBrowserTab.getBrowser());
		});

		return webBrowserTab;
	}

	/**
	 * Creates a new tab for the browser and adding it to the end of the Tabs
	 * 
	 * @param webSite
	 */
	public void addNewTabOnTheEnd(String webSite) {
		tabPane.getTabs().add(createNewTab(webSite).getTab());
	}

	/**
	 * Disposing Browser instance in the incorrect thread in JavaFX may lead to a
	 * deadlock on the native side. The Browser instances must be disposed on
	 * different threads depending on the operating system. In Linux and macOS the
	 * Browser.dispose() method must be called on the UI thread, While on Windows
	 * Browser must be disposed on the non-UI thread
	 * 
	 * @param browser
	 */
	public void disposeBrowser(Browser browser) {
		switch (OSTool.getOS()) {
		case WINDOWS:
			new Thread(browser::dispose).start();
			break;
		case LINUX:
		case MAC:
			Platform.runLater(browser::dispose);
			break;
		default:
			System.out.println("Can't dispose browser instance!!!");
			break;
		}
	}

	/**
	 * Dispose all the browsers , this method is used before the application exits
	 * :)
	 */
	public void disposeAllBrowsers() {
		try {
			tabPane.getTabs().forEach(tab -> ((WebBrowserTabController) tab.getContent()).getBrowser().dispose());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Closes the tabs to the right of the given Tab
	 * 
	 * @param givenTab
	 */
	public void closeTabsToTheRight(Tab givenTab) {
		// Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;

		// The start
		int start = tabPane.getTabs().indexOf(givenTab);

		// Remove the appropriate items
		tabPane.getTabs().stream()
				// filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) > start)
				// Collect the all to a list
				.collect(Collectors.toList()).forEach(this::removeTab);

	}

	/**
	 * Closes the tabs to the left of the given Tab
	 * 
	 * @param givenTab
	 */
	public void closeTabsToTheLeft(Tab givenTab) {
		// Return if size <= 1
		if (tabPane.getTabs().size() <= 1)
			return;

		// The start
		int start = tabPane.getTabs().indexOf(givenTab);

		// Remove the appropriate items
		tabPane.getTabs().stream()
				// filter
				.filter(tab -> tabPane.getTabs().indexOf(tab) < start)
				// Collect the all to a list
				.collect(Collectors.toList()).forEach(this::removeTab);

	}

	/**
	 * Removes this Tab from the TabPane
	 * 
	 * @param tab
	 */
	public void removeTab(Tab tab) {
		tabPane.getTabs().remove(tab);
		tab.getOnClosed().handle(null);
	}

	/**
	 * @return the tabPane
	 */
	public TabPane getTabPane() {
		return tabPane;
	}

	/**
	 * Sets the moving titles enabled or disabled on all the tabs
	 * 
	 * @param value
	 */
	public void setMovingTitlesEnabled(boolean value) {
		MOVING_TITLES_ENABLED = value;
		tabPane.getTabs().forEach(tab -> ((WebBrowserTabController) tab.getContent()).setMovingTitleEnabled(value));
	}

	/**
	 * Start the chromiumUpdaterService which is actually a background Thread
	 * responsible for looking if tabs have audio , are muted/umuted and generally
	 * many other things
	 * 
	 * @see ChromiumUpdaterService
	 */
	public void startChromiumUpdaterService() {
		chromiumUpdaterService.start();
	}

	/**
	 * In an attempt to block add popups created this Set of blocked websites
	 * 
	 * @return A Set of blocked websites
	 */
	public Set<String> getAddBlockSet() {
		return addBlockSet;
	}

	/**
	 * This is a list holding all the proposed websites for the user
	 */

	// public static final SortedSet<String> WEBSITE_PROPOSALS = new
	// TreeSet<>(Arrays.asList("https://www.104.com.tw", "https://www.11st.co.kr",
	// "https://www.123-reg.co.uk",
	// "https://www.1337x.to", "https://www.163.com", "https://www.1688.com",
	// "https://www.17ok.com", "https://www.1and1.com", "https://www.1and1.fr",
	// "https://www.1und1.de",
	// "https://www.2ch.net", "https://www.360.cn", "https://www.39.net",
	// "https://www.4.cn", "https://www.4chan.org", "https://www.4dsply.com",
	// "https://www.4pda.ru",
	// "https://www.4shared.com", "https://www.51.la", "https://www.51sole.com",
	// "https://www.52pk.com", "https://www.58.com", "https://www.9gag.com",
	// "https://www.a8.net",
	// "https://www.abc.net.au", "https://www.about.com", "https://www.about.me",
	// "https://www.aboutcookies.org", "https://www.abs-cbn.com",
	// "https://www.absoluteclickscom.com", "https://www.academia.edu",
	// "https://www.accuweather.com", "https://www.acquirethisname.com",
	// "https://www.addthis.com",
	// "https://www.addtoany.com", "https://www.adexchangeprediction.com",
	// "https://www.adf.ly", "https://www.admarketplace.net",
	// "https://www.admin.ch",
	// "https://www.adnetworkperformance.com", "https://www.adobe.com",
	// "https://www.adp.com", "https://www.adweek.com", "https://www.afternic.com",
	// "https://www.agoda.com",
	// "https://www.airbnb.com", "https://www.airtel.in",
	// "https://www.albawabhnews.com", "https://www.alexa.com",
	// "https://www.alibaba.com", "https://www.alicdn.com",
	// "https://www.aliexpress.com", "https://www.alipay.com",
	// "https://www.aliyun.com", "https://www.aljazeera.com",
	// "https://www.aljazeera.net",
	// "https://www.allaboutcookies.org", "https://www.allegro.pl",
	// "https://www.allocine.fr", "https://www.allrecipes.com",
	// "https://www.almasryalyoum.com",
	// "https://www.alodokter.com", "https://www.alsbbora.com",
	// "https://www.amazon.ca", "https://www.amazon.cn", "https://www.amazon.co.jp",
	// "https://www.amazon.co.uk",
	// "https://www.amazon.com", "https://www.amazon.de", "https://www.amazon.es",
	// "https://www.amazon.fr", "https://www.amazon.in", "https://www.amazon.it",
	// "https://www.amazonaws.com", "https://www.ameblo.jp",
	// "https://www.americanexpress.com", "https://www.ampxchange.com",
	// "https://www.amzn.to",
	// "https://www.andhrajyothy.com", "https://www.android.com",
	// "https://www.animeflv.net", "https://www.antaranews.com",
	// "https://www.aol.com", "https://www.ap.org",
	// "https://www.apa.org", "https://www.apache.org", "https://www.aparat.com",
	// "https://www.apple-kungfu.com", "https://www.apple.com",
	// "https://www.appledaily.com.tw",
	// "https://www.appspot.com", "https://www.archive.org",
	// "https://www.archives.gov", "https://www.army.mil",
	// "https://www.arstechnica.com", "https://www.as.com",
	// "https://www.asahi.com", "https://www.asana.com", "https://www.ascii.co.uk",
	// "https://www.ask.com", "https://www.ask.fm", "https://www.askcom.me",
	// "https://www.askubuntu.com", "https://www.asos.com", "https://www.asus.com",
	// "https://www.atlassian.net", "https://www.att.com",
	// "https://www.autodesk.com",
	// "https://www.avg.com", "https://www.avito.ru", "https://www.axs.com",
	// "https://www.azlyrics.com", "https://www.azxsd.pro",
	// "https://www.babytree.com",
	// "https://www.badoo.com", "https://www.baidu.com", "https://www.baike.com",
	// "https://www.bandcamp.com", "https://www.banggood.com",
	// "https://www.bankmellat.ir",
	// "https://www.bankofamerica.com", "https://www.banvenez.com",
	// "https://www.barnesandnoble.com", "https://www.battle.net",
	// "https://www.bbb.org", "https://www.bbc.co.uk",
	// "https://www.bbc.com", "https://www.beeg.com", "https://www.behance.net",
	// "https://www.beian.gov.cn", "https://www.berkeley.edu",
	// "https://www.bestbuy.com",
	// "https://www.bestfwdservice.com", "https://www.bet365.com",
	// "https://www.bet9ja.com", "https://www.beytoote.com",
	// "https://www.bhaskar.com",
	// "https://www.bhphotovideo.com", "https://www.bigcartel.com",
	// "https://www.bild.de", "https://www.bilibili.com",
	// "https://www.billdesk.com", "https://www.bing.com",
	// "https://www.bintang.com", "https://www.biobiochile.cl",
	// "https://www.birdieulx.com", "https://www.bit.ly", "https://www.bitauto.com",
	// "https://www.bitbucket.org",
	// "https://www.bitly.com", "https://www.bizjournals.com",
	// "https://www.blackboard.com", "https://www.blastingnews.com",
	// "https://www.bleacherreport.com",
	// "https://www.blog.jp", "https://www.blog.me", "https://www.blogfa.com",
	// "https://www.blogger.com", "https://www.blogspot.ca",
	// "https://www.blogspot.co.uk",
	// "https://www.blogspot.com", "https://www.blogspot.com.br",
	// "https://www.blogspot.com.es", "https://www.blogspot.de",
	// "https://www.blogspot.fr",
	// "https://www.blogspot.in", "https://www.blogspot.jp",
	// "https://www.blogspot.mx", "https://www.bloomberg.com",
	// "https://www.blpmovies.com", "https://www.bls.gov",
	// "https://www.bluehost.com", "https://www.bmj.com",
	// "https://www.bodybuilding.com", "https://www.bola.net",
	// "https://www.bomb01.com", "https://www.bongacams.com",
	// "https://www.booking.com", "https://www.bookmyshow.com",
	// "https://www.boredpanda.com", "https://www.boston.com",
	// "https://www.bostonglobe.com", "https://www.box.com",
	// "https://www.bp.blogspot.com", "https://www.brazzers.com",
	// "https://www.breitbart.com", "https://www.brilio.net",
	// "https://www.bukalapak.com", "https://www.bund.de",
	// "https://www.businessinsider.com", "https://www.businessweek.com",
	// "https://www.businesswire.com", "https://www.buydomains.com",
	// "https://www.buyma.com",
	// "https://www.buzzfeed.com", "https://www.ca.gov",
	// "https://www.cafepress.com", "https://www.caijing.com.cn",
	// "https://www.caixa.gov.br", "https://www.caliente.mx",
	// "https://www.cam.ac.uk", "https://www.cam4.com", "https://www.cambridge.org",
	// "https://www.camdolls.com", "https://www.campaign-archive1.com",
	// "https://www.campaign-archive2.com", "https://www.canva.com",
	// "https://www.capitalone.com", "https://www.cbc.ca",
	// "https://www.cbslocal.com", "https://www.cbsnews.com",
	// "https://www.ccm.net", "https://www.cdc.gov", "https://www.cdiscount.com",
	// "https://www.census.gov", "https://www.cerpen.co.id",
	// "https://www.change.org",
	// "https://www.chaoshi.tmall.com", "https://www.chase.com",
	// "https://www.chaturbate.com", "https://www.chatwork.com",
	// "https://www.chicagotribune.com",
	// "https://www.china.com", "https://www.china.com.cn",
	// "https://www.chinadaily.com.cn", "https://www.chinaso.com",
	// "https://www.chinaz.com", "https://www.chip.de",
	// "https://www.chouftv.ma", "https://www.chron.com", "https://www.ci123.com",
	// "https://www.cisco.com", "https://www.citi.com", "https://www.ck101.com",
	// "https://www.clickbank.net", "https://www.clicksgear.com",
	// "https://www.clipconverter.cc", "https://www.cloudfront.net",
	// "https://www.cmu.edu", "https://www.cnbc.com",
	// "https://www.cnblogs.com", "https://www.cnet.com", "https://www.cnn.com",
	// "https://www.cnzz.com", "https://www.coccoc.com",
	// "https://www.codeonclick.com",
	// "https://www.codepen.io", "https://www.coinbase.com",
	// "https://www.coinmarketcap.com", "https://www.columbia.edu",
	// "https://www.com.com", "https://www.comcast.net",
	// "https://www.comicbook.com", "https://www.commentcamarche.net",
	// "https://www.conservativetribune.com", "https://www.constantcontact.com",
	// "https://www.content1req.com",
	// "https://www.convert2mp3.net", "https://www.cornell.edu",
	// "https://www.corriere.it", "https://www.coursera.org",
	// "https://www.cpanel.com", "https://www.cpanel.net",
	// "https://www.cpm10.com", "https://www.cpmofferconvert.com",
	// "https://www.cqnews.net", "https://www.craigslist.org",
	// "https://www.creativecommons.org",
	// "https://www.cricbuzz.com", "https://www.crunchyroll.com",
	// "https://www.csdn.net", "https://www.ctitv.com.tw", "https://www.dafont.com",
	// "https://www.daikynguyenvn.com", "https://www.dailymail.co.uk",
	// "https://www.dailymotion.com", "https://www.dailypakistan.com.pk",
	// "https://www.dandomain.dk",
	// "https://www.daum.net", "https://www.dcard.tw", "https://www.debian.org",
	// "https://www.delicious.com", "https://www.dell.com", "https://www.delta.com",
	// "https://www.detail.tmall.com", "https://www.detik.com",
	// "https://www.deviantart.com", "https://www.deviantart.net",
	// "https://www.dict.cc",
	// "https://www.dictionary.com", "https://www.digg.com",
	// "https://www.digikala.com", "https://www.dingit.tv", "https://www.diply.com",
	// "https://www.directdomains.com",
	// "https://www.directrev.com", "https://www.discogs.com",
	// "https://www.discordapp.com", "https://www.discover.com",
	// "https://www.discovery.com",
	// "https://www.discuss.com.hk", "https://www.disq.us",
	// "https://www.disqus.com", "https://www.divar.ir", "https://www.dmm.co.jp",
	// "https://www.dmv.org",
	// "https://www.doi.org", "https://www.domainactive.co",
	// "https://www.domainmarket.com", "https://www.domainname.de",
	// "https://www.domainnameshop.com",
	// "https://www.domeneshop.no", "https://www.donga.com", "https://www.dot.gov",
	// "https://www.douban.com", "https://www.doubleclick.net",
	// "https://www.doublepimp.com",
	// "https://www.doublepimpssl.com", "https://www.douyu.com",
	// "https://www.dream.co.id", "https://www.dreamhost.com",
	// "https://www.dribbble.com", "https://www.drive2.ru",
	// "https://www.drom.ru", "https://www.dropbox.com",
	// "https://www.dropboxusercontent.com", "https://www.drtuber.com",
	// "https://www.drudgereport.com",
	// "https://www.drupal.org", "https://www.duckduckgo.com",
	// "https://www.duke.edu", "https://www.duolingo.com",
	// "https://www.e-recht24.de", "https://www.ea.com",
	// "https://www.eastday.com", "https://www.ebay-kleinanzeigen.de",
	// "https://www.ebay.co.uk", "https://www.ebay.com", "https://www.ebay.com.au",
	// "https://www.ebay.de",
	// "https://www.ebay.fr", "https://www.ebay.in", "https://www.ebay.it",
	// "https://www.economist.com", "https://www.ecosia.org", "https://www.ed.gov",
	// "https://www.eepurl.com", "https://www.eksisozluk.com",
	// "https://www.el-nacional.com", "https://www.elbalad.news",
	// "https://www.elegantthemes.com",
	// "https://www.elfagr.org", "https://www.elmundo.es", "https://www.elpais.com",
	// "https://www.elvenar.com", "https://www.emol.com",
	// "https://www.enable-javascript.com",
	// "https://www.engadget.com", "https://www.ensonhaber.com",
	// "https://www.entrepreneur.com", "https://www.epa.gov",
	// "https://www.eskimi.com", "https://www.espn.com",
	// "https://www.espncricinfo.com", "https://www.etsy.com",
	// "https://www.ettoday.net", "https://www.europa.eu",
	// "https://www.eventbrite.co.uk",
	// "https://www.eventbrite.com", "https://www.evernote.com",
	// "https://www.ewebdevelopment.com", "https://www.examiner.com",
	// "https://www.example.com",
	// "https://www.exblog.jp", "https://www.exoclick.com",
	// "https://www.expedia.com", "https://www.express.co.uk",
	// "https://www.eyny.com", "https://www.facebook.com",
	// "https://www.faithtap.com", "https://www.familydoctor.com.cn",
	// "https://www.fanfiction.net", "https://www.fanpage.gr",
	// "https://www.fao.org",
	// "https://www.farsnews.com", "https://www.fastcompany.com",
	// "https://www.fatosdesconhecidos.com.br", "https://www.fb.com",
	// "https://www.fb.me", "https://www.fbcdn.net",
	// "https://www.fbsbx.com", "https://www.fc2.com", "https://www.fda.gov",
	// "https://www.fedex.com", "https://www.feedburner.com",
	// "https://www.feedly.com",
	// "https://www.fidelity.com", "https://www.filehippo.com",
	// "https://www.files.wordpress.com", "https://www.fiverr.com",
	// "https://www.flickr.com",
	// "https://www.flipkart.com", "https://www.flirt4free.com",
	// "https://www.fmovies.to", "https://www.focus.de",
	// "https://www.food.tmall.com", "https://www.forbes.com",
	// "https://www.force.com", "https://www.fortune.com",
	// "https://www.foursquare.com", "https://www.foxnews.com",
	// "https://www.free.fr", "https://www.freejobalert.com",
	// "https://www.freepik.com", "https://www.friv.com",
	// "https://www.fromdoctopdf.com", "https://www.frstlead.com",
	// "https://www.ft.com", "https://www.ftc.gov",
	// "https://www.g2a.com", "https://www.gamefaqs.com",
	// "https://www.gamepedia.com", "https://www.gamer.com.tw",
	// "https://www.gamespot.com",
	// "https://www.gamingwonderland.com", "https://www.gazeta.pl",
	// "https://www.gazetaexpress.com", "https://www.gazzetta.it",
	// "https://www.gearbest.com",
	// "https://www.genius.com", "https://www.geocities.com",
	// "https://www.geocities.jp", "https://www.getpocket.com",
	// "https://www.gfycat.com", "https://www.giphy.com",
	// "https://www.gismeteo.ru", "https://www.github.com", "https://www.github.io",
	// "https://www.givemesport.com", "https://www.gizmodo.com",
	// "https://www.glassdoor.com",
	// "https://www.globo.com", "https://www.gmarket.co.kr", "https://www.gmw.cn",
	// "https://www.gmx.net", "https://www.gnu.org", "https://www.go.com",
	// "https://www.goal.com",
	// "https://www.godaddy.com", "https://www.gofundme.com",
	// "https://www.gogoanime.io", "https://www.gomovies.to",
	// "https://www.gongchang.com", "https://www.goo.gl",
	// "https://www.goo.ne.jp", "https://www.goodreads.com",
	// "https://www.googleusercontent.com", "https://www.googlevideo.com",
	// "https://www.gotporn.com",
	// "https://www.gov.uk", "https://www.gpo.gov", "https://www.grammarly.com",
	// "https://www.gravatar.com", "https://www.grid.id", "https://www.groupon.com",
	// "https://www.gsmarena.com", "https://www.guardian.co.uk",
	// "https://www.gutefrage.net", "https://www.gyazo.com",
	// "https://www.haber7.com", "https://www.hamariweb.com",
	// "https://www.hao123.com", "https://www.harvard.edu",
	// "https://www.hatena.ne.jp", "https://www.hatenablog.com",
	// "https://www.hbr.org", "https://www.hclips.com",
	// "https://www.hdfcbank.com", "https://www.hdzog.com",
	// "https://www.hespress.com", "https://www.hhs.gov", "https://www.hibu.com",
	// "https://www.hilltopads.net",
	// "https://www.hilton.com", "https://www.histats.com", "https://www.hm.com",
	// "https://www.hola.com", "https://www.hollywoodreporter.com",
	// "https://www.home.pl",
	// "https://www.homedepot.com", "https://www.homestead.com",
	// "https://www.hootsuite.com", "https://www.hostgator.com",
	// "https://www.hostnet.nl", "https://www.hotels.com",
	// "https://www.hotmovs.com", "https://www.hotstar.com",
	// "https://www.house.gov", "https://www.houzz.com",
	// "https://www.howtogeek.com", "https://www.hp.com",
	// "https://www.href.li", "https://www.huaban.com", "https://www.huanqiu.com",
	// "https://www.hubspot.com", "https://www.huffingtonpost.com",
	// "https://www.hulu.com",
	// "https://www.humblebundle.com", "https://www.hurriyet.com.tr",
	// "https://www.ibm.com", "https://www.ibtimes.com", "https://www.icann.org",
	// "https://www.icicibank.com",
	// "https://www.icio.us", "https://www.icloud.com", "https://www.idnes.cz",
	// "https://www.ieee.org", "https://www.ign.com", "https://www.ikea.com",
	// "https://www.imdb.com",
	// "https://www.imgur.com", "https://www.imwhite.ru", "https://www.inc.com",
	// "https://www.indeed.com", "https://www.independent.co.uk",
	// "https://www.indianexpress.com",
	// "https://www.indiatimes.com", "https://www.indiegogo.com",
	// "https://www.infusionsoft.com", "https://www.inquirer.net",
	// "https://www.instagram.com",
	// "https://www.instructables.com", "https://www.instructure.com",
	// "https://www.intel.com", "https://www.interia.pl",
	// "https://www.internetdownloadmanager.com",
	// "https://www.intoday.in", "https://www.intuit.com",
	// "https://www.inven.co.kr", "https://www.investing.com",
	// "https://www.investopedia.com", "https://www.ipetgroup.com",
	// "https://www.iqiyi.com", "https://www.iqoption.com",
	// "https://www.irctc.co.in", "https://www.irs.gov", "https://www.issuu.com",
	// "https://www.istockphoto.com",
	// "https://www.itmedia.co.jp", "https://www.iwanttodeliver.com",
	// "https://www.jabong.com", "https://www.japanpost.jp", "https://www.java.com",
	// "https://www.jd.com",
	// "https://www.jeuxvideo.com", "https://www.jianshu.com",
	// "https://www.jiathis.com", "https://www.jimdo.com", "https://www.joomla.org",
	// "https://www.jrj.com.cn",
	// "https://www.jugem.jp", "https://www.junbi-tracker.com",
	// "https://www.justdial.com", "https://www.jw.org", "https://www.k618.cn",
	// "https://www.kakaku.com",
	// "https://www.kapanlagi.com", "https://www.kaskus.co.id",
	// "https://www.kayak.com", "https://www.khanacademy.org",
	// "https://www.kickstarter.com", "https://www.kijiji.ca",
	// "https://www.kinogo.club", "https://www.kinokrad.co",
	// "https://www.kinopoisk.ru", "https://www.kinoprofi.org",
	// "https://www.kissanime.ru", "https://www.kissasian.com",
	// "https://www.kizlarsoruyor.com", "https://www.kompas.com",
	// "https://www.kooora.com", "https://www.kotaku.com",
	// "https://www.labanquepostale.fr",
	// "https://www.ladbible.com", "https://www.lapatilla.com",
	// "https://www.latimes.com", "https://www.launchpage.org",
	// "https://www.lazada.co.id",
	// "https://www.lazada.co.th", "https://www.lazada.com.my",
	// "https://www.lazada.com.ph", "https://www.leagueoflegends.com",
	// "https://www.leboncoin.fr",
	// "https://www.lefigaro.fr", "https://www.lemonde.fr",
	// "https://www.lenovo.com", "https://www.lenta.ru", "https://www.libero.it",
	// "https://www.life.tw",
	// "https://www.lifebuzz.com", "https://www.lifehacker.com",
	// "https://www.lifewire.com", "https://www.liftable.com",
	// "https://www.likemag.com", "https://www.line.me",
	// "https://www.linkedin.com", "https://www.linkshrink.net",
	// "https://www.linksynergy.com", "https://www.liputan6.com",
	// "https://www.list-manage.com",
	// "https://www.list-manage1.com", "https://www.list-manage2.com",
	// "https://www.list.tmall.com", "https://www.live.com",
	// "https://www.liveadexchanger.com",
	// "https://www.livedoor.com", "https://www.livedoor.jp",
	// "https://www.liveinternet.ru", "https://www.livejasmin.com",
	// "https://www.livejournal.com",
	// "https://www.liveleak.com", "https://www.livescore.com",
	// "https://www.livestream.com", "https://www.livestrong.com",
	// "https://www.lk21.me", "https://www.loc.gov",
	// "https://www.loopia.com", "https://www.loopia.se", "https://www.lowes.com",
	// "https://www.ltn.com.tw", "https://www.lun.com",
	// "https://www.macromedia.com",
	// "https://www.macys.com", "https://www.mail.ru", "https://www.mailchimp.com",
	// "https://www.makemytrip.com", "https://www.mama.cn",
	// "https://www.managemy.tel",
	// "https://www.mangafox.me", "https://www.manoramaonline.com",
	// "https://www.mapquest.com", "https://www.marca.com",
	// "https://www.marketwatch.com",
	// "https://www.marriott.com", "https://www.mashable.com",
	// "https://www.mbc.net", "https://www.meb.gov.tr",
	// "https://www.media.tumblr.com", "https://www.mediafire.com",
	// "https://www.mediaoffers.click", "https://www.mediawhirl.net",
	// "https://www.medium.com", "https://www.meetup.com", "https://www.mega.nz",
	// "https://www.mercadolibre.com.ar", "https://www.mercadolibre.com.mx",
	// "https://www.mercadolibre.com.ve", "https://www.mercadolivre.com.br",
	// "https://www.merdeka.com",
	// "https://www.merriam-webster.com", "https://www.messenger.com",
	// "https://www.metropcs.mobi", "https://www.metropoles.com",
	// "https://www.mi.com",
	// "https://www.microsoft.com", "https://www.microsoftonline.com",
	// "https://www.miibeian.gov.cn", "https://www.miitbeian.gov.cn",
	// "https://www.mijndomein.nl",
	// "https://www.milliyet.com.tr", "https://www.mirror.co.uk",
	// "https://www.mit.edu", "https://www.mlb.com",
	// "https://www.mmofreegames.online", "https://www.mobile.de",
	// "https://www.mobile01.com", "https://www.momoshop.com.tw",
	// "https://www.moneycontrol.com", "https://www.motherless.com",
	// "https://www.mozilla.com",
	// "https://www.mozilla.org", "https://www.msn.com", "https://www.mtv.com",
	// "https://www.mundo.com", "https://www.my-hit.org",
	// "https://www.myanimelist.net",
	// "https://www.myfitnesspal.com", "https://www.myfreecams.com",
	// "https://www.mynavi.jp", "https://www.mysagagame.com",
	// "https://www.myshopify.com",
	// "https://www.myspace.com", "https://www.mysql.com",
	// "https://www.mywatchseries.to", "https://www.myway.com",
	// "https://www.naij.com", "https://www.namejet.com",
	// "https://www.nametests.com", "https://www.namnak.com",
	// "https://www.nanoadexchange.com", "https://www.nasa.gov",
	// "https://www.nationalgeographic.com",
	// "https://www.nature.com", "https://www.naukri.com", "https://www.naver.com",
	// "https://www.naver.jp", "https://www.nazwa.pl", "https://www.nba.com",
	// "https://www.nbcnews.com", "https://www.ndtv.com", "https://www.neobux.com",
	// "https://www.netflix.com", "https://www.netscape.com",
	// "https://www.networkadvertising.org", "https://www.networksolutions.com",
	// "https://www.newegg.com", "https://www.news.com.au",
	// "https://www.newstarads.com",
	// "https://www.newsweek.com", "https://www.newtabtv.com",
	// "https://www.newyorker.com", "https://www.nextlnk13.com",
	// "https://www.nginx.org", "https://www.nhk.or.jp",
	// "https://www.nhs.uk", "https://www.nicovideo.jp", "https://www.nielsen.com",
	// "https://www.nifty.com", "https://www.nih.gov", "https://www.nike.com",
	// "https://www.nikkei.com", "https://www.nikkeibp.co.jp",
	// "https://www.ning.com", "https://www.njoyapps.com", "https://www.noaa.gov",
	// "https://www.nocookie.net",
	// "https://www.nordstrom.com", "https://www.norton.com",
	// "https://www.nownews.com", "https://www.npr.org", "https://www.nps.gov",
	// "https://www.nsw.gov.au",
	// "https://www.ntd.tv", "https://www.nur.kz", "https://www.nydailynews.com",
	// "https://www.nymag.com", "https://www.nypost.com", "https://www.nytimes.com",
	// "https://www.nyu.edu", "https://www.ocn.ne.jp", "https://www.odin.com",
	// "https://www.oecd.org", "https://www.oeeee.com", "https://www.office.com",
	// "https://www.office365.com", "https://www.ok.ru", "https://www.okcupid.com",
	// "https://www.okdiario.com", "https://www.okezone.com",
	// "https://www.okta.com",
	// "https://www.olx.com.br", "https://www.olx.in", "https://www.olx.pl",
	// "https://www.olx.ua", "https://www.onclkds.com", "https://www.one.com",
	// "https://www.onedio.com",
	// "https://www.onet.pl", "https://www.onlinesbi.com",
	// "https://www.onlinevideoconverter.com", "https://www.op.gg",
	// "https://www.openload.co",
	// "https://www.openstreetmap.org", "https://www.opensubtitles.org",
	// "https://www.opera.com", "https://www.oracle.com", "https://www.orange.fr",
	// "https://www.oschina.net",
	// "https://www.ouedkniss.com", "https://www.ouo.io",
	// "https://www.outbrain.com", "https://www.ow.ly", "https://www.ox.ac.uk",
	// "https://www.oxfordjournals.org",
	// "https://www.paclitor.com", "https://www.pages.tmall.com",
	// "https://www.pandora.com", "https://www.pantip.com",
	// "https://www.parallels.com", "https://www.patch.com",
	// "https://www.patreon.com", "https://www.paypal.com", "https://www.paytm.com",
	// "https://www.pbs.org", "https://www.pchome.com.tw",
	// "https://www.pcworld.com",
	// "https://www.people.com.cn", "https://www.perfectgirls.net",
	// "https://www.perfecttoolmedia.com", "https://www.performanceadexchange.com",
	// "https://www.phoca.cz",
	// "https://www.photobucket.com", "https://www.php.net",
	// "https://www.phpbb.com", "https://www.pikabu.ru", "https://www.pinimg.com",
	// "https://www.pinterest.com",
	// "https://www.pirateproxy.cc", "https://www.piriform.com",
	// "https://www.pixabay.com", "https://www.pixnet.net",
	// "https://www.piz7ohhujogi.com",
	// "https://www.plarium.com", "https://www.playstation.com",
	// "https://www.plesk.com", "https://www.pof.com", "https://www.politico.com",
	// "https://www.poloniex.com",
	// "https://www.popads.net", "https://www.popcash.net",
	// "https://www.popmyads.com", "https://www.porn.com",
	// "https://www.porn555.com", "https://www.pornhub.com",
	// "https://www.prestoris.com", "https://www.prezi.com",
	// "https://www.primewire.ag", "https://www.princeton.edu",
	// "https://www.prnewswire.com", "https://www.prnt.sc",
	// "https://www.prothom-alo.com", "https://www.providr.com",
	// "https://www.prpops.com", "https://www.prweb.com", "https://www.psu.edu",
	// "https://www.ptt.cc",
	// "https://www.pulseonclick.com", "https://www.python.org",
	// "https://www.qiita.com", "https://www.qingdaonews.com",
	// "https://www.qoolquiz.com", "https://www.qq.com",
	// "https://www.quizlet.com", "https://www.quora.com",
	// "https://www.rakuten.co.jp", "https://www.rambler.ru",
	// "https://www.rapidgator.net", "https://www.rarbg.to",
	// "https://www.rbc.ru", "https://www.re19fla.com",
	// "https://www.reallifecam.com", "https://www.realtor.com",
	// "https://www.redcross.org", "https://www.redd.it",
	// "https://www.reddit.com", "https://www.redfin.com", "https://www.rediff.com",
	// "https://www.redirectvoluum.com", "https://www.rednet.cn",
	// "https://www.redtube.com",
	// "https://www.register.it", "https://www.reimageplus.com",
	// "https://www.repubblica.it", "https://www.researchgate.net",
	// "https://www.reuters.com",
	// "https://www.reverso.net", "https://www.ria.ru", "https://www.roblox.com",
	// "https://www.rollingstone.com", "https://www.rottentomatoes.com",
	// "https://www.rs6.net",
	// "https://www.rt.com", "https://www.rumble.com", "https://www.ruten.com.tw",
	// "https://www.rutracker.org", "https://www.rutube.ru",
	// "https://www.sabah.com.tr",
	// "https://www.sabq.org", "https://www.sagepub.com",
	// "https://www.sahibinden.com", "https://www.sakura.ne.jp",
	// "https://www.salesforce.com", "https://www.salon.com",
	// "https://www.samsung.com", "https://www.sapo.pt",
	// "https://www.sarkariresult.com", "https://www.savefrom.net",
	// "https://www.sberbank.ru",
	// "https://www.sciencedirect.com", "https://www.sciencemag.org",
	// "https://www.scientificamerican.com", "https://www.scribd.com",
	// "https://www.scribol.com",
	// "https://www.searchprivate.org", "https://www.seasonvar.ru",
	// "https://www.seattletimes.com", "https://www.secureserver.net",
	// "https://www.sedo.com",
	// "https://www.seesaa.net", "https://www.senate.gov", "https://www.setn.com",
	// "https://www.sex.com", "https://www.seznam.cz", "https://www.sfgate.com",
	// "https://www.sh.st", "https://www.shaparak.ir", "https://www.shareasale.com",
	// "https://www.sharepoint.com", "https://www.shink.in",
	// "https://www.shinystat.com",
	// "https://www.shop-pro.jp", "https://www.shopify.com",
	// "https://www.shutterstock.com", "https://www.si.edu",
	// "https://www.sina.com.cn", "https://www.sinoptik.ua",
	// "https://www.siteadvisor.com", "https://www.skype.com",
	// "https://www.slack.com", "https://www.slate.com",
	// "https://www.slickdeals.net", "https://www.slideshare.net",
	// "https://www.smh.com.au", "https://www.smugmug.com",
	// "https://www.snapdeal.com", "https://www.so.com", "https://www.softonic.com",
	// "https://www.sogou.com",
	// "https://www.sohu.com", "https://www.solarmoviez.to", "https://www.soso.com",
	// "https://www.soundcloud.com", "https://www.souq.com",
	// "https://www.sourceforge.net",
	// "https://www.southwest.com", "https://www.spankbang.com",
	// "https://www.speedtest.net", "https://www.spiegel.de",
	// "https://www.sportbible.com", "https://www.sporx.com",
	// "https://www.spotify.com", "https://www.spotscenered.info",
	// "https://www.springer.com", "https://www.sputniknews.com",
	// "https://www.squarespace.com",
	// "https://www.stackexchange.com", "https://www.stackoverflow.com",
	// "https://www.stanford.edu", "https://www.statcounter.com",
	// "https://www.state.gov",
	// "https://www.steamcommunity.com", "https://www.steampowered.com",
	// "https://www.stockstar.com", "https://www.storify.com",
	// "https://www.strava.com",
	// "https://www.streamable.com", "https://www.streamcloud.eu",
	// "https://www.studiopress.com", "https://www.stumbleupon.com",
	// "https://www.suara.com",
	// "https://www.subito.it", "https://www.subject.tmall.com",
	// "https://www.subscene.com", "https://www.sumatoad.com",
	// "https://www.suning.com", "https://www.superuser.com",
	// "https://www.surveymonkey.com", "https://www.symantec.com",
	// "https://www.t-online.de", "https://www.t.co", "https://www.tabelog.com",
	// "https://www.taboola.com",
	// "https://www.taleo.net", "https://www.tamilrockers.lv",
	// "https://www.tandfonline.com", "https://www.taobao.com",
	// "https://www.target.com", "https://www.taringa.net",
	// "https://www.teamviewer.com", "https://www.tebyan.net",
	// "https://www.techcrunch.com", "https://www.technorati.com",
	// "https://www.techradar.com", "https://www.ted.com",
	// "https://www.teepr.com", "https://www.telegram.me",
	// "https://www.telegram.org", "https://www.telegraph.co.uk",
	// "https://www.telewebion.com", "https://www.telnames.net",
	// "https://www.terraclicks.com", "https://www.tfetimes.com",
	// "https://www.theatlantic.com", "https://www.thebalance.com",
	// "https://www.thedailybeast.com",
	// "https://www.theepochtimes.com", "https://www.thefreedictionary.com",
	// "https://www.theglobeandmail.com", "https://www.theguardian.com",
	// "https://www.thehill.com",
	// "https://www.thekitchn.com", "https://www.themeforest.net",
	// "https://www.themegrill.com", "https://www.thepennyhoarder.com",
	// "https://www.thepiratebay.org",
	// "https://www.thesaurus.com", "https://www.thesun.co.uk",
	// "https://www.thetimes.co.uk", "https://www.theverge.com",
	// "https://www.thevideo.me",
	// "https://www.thewhizmarketing.com", "https://www.thewhizproducts.com",
	// "https://www.tianya.cn", "https://www.ticketmaster.com",
	// "https://www.time.com",
	// "https://www.timeanddate.com", "https://www.tinyurl.com",
	// "https://www.tistory.com", "https://www.tmall.com", "https://www.today.com",
	// "https://www.tokopedia.com",
	// "https://www.tomshardware.com", "https://www.torrentproject.se",
	// "https://www.torrentz2.eu", "https://www.trackmedia101.com",
	// "https://www.translationbuddy.com",
	// "https://www.trello.com", "https://www.tribunnews.com",
	// "https://www.tripadvisor.co.uk", "https://www.tripadvisor.com",
	// "https://www.tripod.com",
	// "https://www.trulia.com", "https://www.trustpilot.com",
	// "https://www.tube8.com", "https://www.tumblr.com",
	// "https://www.turbobit.net", "https://www.tutorialspoint.com",
	// "https://www.tvbs.com.tw", "https://www.twimg.com", "https://www.twitch.tv",
	// "https://www.twitter.com", "https://www.txxx.com", "https://www.typepad.com",
	// "https://www.uber.com", "https://www.uchicago.edu", "https://www.ucla.edu",
	// "https://www.ucsd.edu", "https://www.udemy.com", "https://www.udn.com",
	// "https://www.uidai.gov.in", "https://www.uk2.net", "https://www.ukr.net",
	// "https://www.ultimate-guitar.com", "https://www.umblr.com",
	// "https://www.umich.edu",
	// "https://www.umn.edu", "https://www.un.org", "https://www.unc.edu",
	// "https://www.unesco.org", "https://www.unity3d.com",
	// "https://www.uol.com.br",
	// "https://www.upenn.edu", "https://www.uploaded.net",
	// "https://www.upornia.com", "https://www.ups.com", "https://www.uptobox.com",
	// "https://www.uptodown.com",
	// "https://www.upwork.com", "https://www.urbandictionary.com",
	// "https://www.urdupoint.com", "https://www.usa.gov",
	// "https://www.usatoday.com", "https://www.usc.edu",
	// "https://www.usda.gov", "https://www.userapi.com", "https://www.usgs.gov",
	// "https://www.usnews.com", "https://www.usps.com", "https://www.ustream.tv",
	// "https://www.utexas.edu", "https://www.uzone.id", "https://www.va.gov",
	// "https://www.variety.com", "https://www.varzesh3.com",
	// "https://www.venturebeat.com",
	// "https://www.verizonwireless.com", "https://www.vetogate.com",
	// "https://www.vice.com", "https://www.videodownloadconverter.com",
	// "https://www.videoyoum7.com",
	// "https://www.vidzi.tv", "https://www.vimeo.com", "https://www.visma.com",
	// "https://www.visualstudio.com", "https://www.viva.co.id",
	// "https://www.vk.com",
	// "https://www.vkontakte.ru", "https://www.vnexpress.net",
	// "https://www.voc.com.cn", "https://www.vox.com", "https://www.voyeurhit.com",
	// "https://www.vporn.com",
	// "https://www.vtv.vn", "https://www.w3.org", "https://www.w3schools.com",
	// "https://www.walmart.com", "https://www.warnerbros.com",
	// "https://www.washington.edu",
	// "https://www.washingtonpost.com", "https://www.washingtontimes.com",
	// "https://www.watchfree.to", "https://www.wattpad.com",
	// "https://www.weather.com",
	// "https://www.web.de", "https://www.webex.com", "https://www.weblio.jp",
	// "https://www.webmd.com", "https://www.webs.com", "https://www.webtretho.com",
	// "https://www.weebly.com", "https://www.weevah2.top", "https://www.weibo.com",
	// "https://www.wellsfargo.com", "https://www.welt.de",
	// "https://www.westernjournalism.com",
	// "https://www.wetransfer.com", "https://www.whatsapp.com",
	// "https://www.whitehouse.gov", "https://www.who.int",
	// "https://www.whoisprivacyprotect.com",
	// "https://www.wikia.com", "https://www.wikihow.com",
	// "https://www.wikimedia.org", "https://www.wikipedia.org",
	// "https://www.wiktionary.org", "https://www.wiley.com",
	// "https://www.windowsphone.com", "https://www.wired.com",
	// "https://www.wisc.edu", "https://www.wish.com", "https://www.wittyfeed.com",
	// "https://www.wix.com",
	// "https://www.wixsite.com", "https://www.wordpress.com",
	// "https://www.wordpress.org", "https://www.wordreference.com",
	// "https://www.world.tmall.com",
	// "https://www.worldbank.org", "https://www.wowhead.com", "https://www.wp.com",
	// "https://www.wp.me", "https://www.wp.pl", "https://www.wsj.com",
	// "https://www.wtoip.com",
	// "https://www.wufoo.com", "https://www.wunderground.com",
	// "https://www.wuxiaworld.com", "https://www.wykop.pl",
	// "https://www.x48fly.com", "https://www.xbox.com",
	// "https://www.xda-developers.com", "https://www.xe.com",
	// "https://www.xfinity.com", "https://www.xgames-04.com",
	// "https://www.xhamster.com", "https://www.xing.com",
	// "https://www.xinhuanet.com", "https://www.xiti.com", "https://www.xnxx.com",
	// "https://www.xtube.com", "https://www.xvideos.com", "https://www.y8.com",
	// "https://www.yadi.sk", "https://www.yahoo.co.jp", "https://www.yahoo.com",
	// "https://www.yale.edu", "https://www.yandex.com.tr", "https://www.yandex.kz",
	// "https://www.yandex.ru", "https://www.yandex.ua", "https://www.yaplakal.com",
	// "https://www.yelp.com", "https://www.yenisafak.com", "https://www.yesky.com",
	// "https://www.yjc.ir", "https://www.youboy.com", "https://www.youdao.com",
	// "https://www.youjizz.com", "https://www.youku.com", "https://www.youm7.com",
	// "https://www.youporn.com", "https://www.youronlinechoices.com",
	// "https://www.youth.cn", "https://www.youtube.com", "https://www.ytimg.com",
	// "https://www.yts.ag",
	// "https://www.zdnet.com", "https://www.zendesk.com", "https://www.zhanqi.tv",
	// "https://www.zhihu.com", "https://www.zillow.com", "https://www.zing.vn",
	// "https://www.zippyshare.com", "https://www.zoho.com",
	// "https://www.zomato.com", "https://www.zone-telechargement.ws",
	// "https://www.zoom.us"));
	//
}
