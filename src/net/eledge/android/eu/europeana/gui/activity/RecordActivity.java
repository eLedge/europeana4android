package net.eledge.android.eu.europeana.gui.activity;

import net.eledge.android.eu.europeana.EuropeanaApplication;
import net.eledge.android.eu.europeana.R;
import net.eledge.android.eu.europeana.gui.adaptor.ResultAdaptor;
import net.eledge.android.eu.europeana.gui.dialog.AboutDialog;
import net.eledge.android.eu.europeana.search.RecordController;
import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.toolkit.StringUtils;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ShareActionProvider;

public class RecordActivity extends FragmentActivity {
	
	// Controller
	private SearchController searchController = SearchController.instance;
	private RecordController recordController = RecordController.instance;
	
	// Share button
	private ShareActionProvider mShareActionProvider;
	
	// NavigationDrawer
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mResultsList;
	private ResultAdaptor mResultAdaptor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		
		mResultsList = (ListView) findViewById(R.id.drawer_items);
		mResultAdaptor = new ResultAdaptor((EuropeanaApplication) getApplication(), this, searchController.getSearchItems());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_activity_record);
		if (mDrawerLayout != null) {
			mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer,
					R.string.drawer_facets_open, R.string.drawer_facets_close) {
				public void onDrawerClosed(View view) {
					// getActionBar().setTitle(mTitle);
					invalidateOptionsMenu();
				}

				public void onDrawerOpened(View drawerView) {
					// getActionBar().setTitle(mDrawerTitle);
					invalidateOptionsMenu();
				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);
		}		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.record, menu);

		MenuItem share = menu.findItem(R.id.action_share);
		// Fetch and store ShareActionProvider
		mShareActionProvider = (ShareActionProvider) share.getActionProvider();
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(createShareIntent());
		}
		
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mDrawerLayout != null) {
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(mResultsList);
			menu.findItem(R.id.action_share).setVisible(!drawerOpen);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if ((mDrawerLayout != null) && mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_about:
			try {
				Dialog dialog = new AboutDialog(this, getPackageManager().getPackageInfo(getPackageName(), 0));
				dialog.show();
			} catch (NameNotFoundException e) {}
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		if (mDrawerLayout != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (mDrawerLayout != null) {
			mDrawerToggle.syncState();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		String id = null;
		if (intent != null) {
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				id = intent.getDataString();
				if (!TextUtils.isEmpty(id)) {
					if (StringUtils.contains(id, "europeana.eu/")) {
						Uri uri = Uri.parse(id);
						// TODO
					}
				}
			}
		}
	}
	
	private Intent createShareIntent() {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT, recordController.getPortalUrl());
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this search on Europeana.eu!");
		return shareIntent;
	}

}
