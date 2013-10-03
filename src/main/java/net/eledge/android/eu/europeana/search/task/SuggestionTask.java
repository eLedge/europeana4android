package net.eledge.android.eu.europeana.search.task;

import android.app.Activity;
import android.os.AsyncTask;

import net.eledge.android.eu.europeana.search.SearchController;
import net.eledge.android.eu.europeana.search.model.Suggestions;
import net.eledge.android.eu.europeana.search.model.suggestion.Item;
import net.eledge.android.eu.europeana.tools.UriHelper;
import net.eledge.android.toolkit.async.ListenerNotifier;
import net.eledge.android.toolkit.async.listener.TaskListener;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class SuggestionTask extends AsyncTask<String, Void, Item[]> {
	
	private SearchController searchController = SearchController._instance;
	
	private TaskListener<Item[]> listener;
	
	private String term;
	
	public SuggestionTask(TaskListener<Item[]> listener) {
		this.listener = listener;
	}
	
	@Override
	protected Item[] doInBackground(String... params) {
		if (StringUtils.isBlank(params[0])) {
			return null;
		}
        term = params[0];
        String url = UriHelper.getSuggestionUrl(term, searchController.suggestionPagesize);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate.getForObject(url, Suggestions.class).items;
	}
	
	@Override
	protected void onPostExecute(Item[] suggestions) {
		searchController.cacheSuggestions(term, suggestions);
		if (listener instanceof Activity) {
			((Activity)listener).runOnUiThread(new ListenerNotifier<Item[]>(listener, suggestions));
		} else {
			listener.onTaskFinished(suggestions);
		}
	}

}