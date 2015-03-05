package com.deskwiki.deskwiki.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.deskwiki.deskwiki.R;
import com.deskwiki.deskwiki.model.WikiItem;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * The list adapter used by the scrollable listview when any search result is found
 * Created by dibyenduroy on 3/4/15.
 */
public class WikiSearchListAdapter extends BaseAdapter{
    public static final String COMMON_DATE_FORMAT = "MM/dd/yyyy";

    private final WeakReference<Activity> activity;
    private final LayoutInflater inflater;
    List<WikiItem.QueryItem.SearchItem> wikiItemList;
    SimpleDateFormat dateFormat = new SimpleDateFormat(COMMON_DATE_FORMAT);
    public WikiSearchListAdapter(WeakReference<Activity> activity
            , List<WikiItem.QueryItem.SearchItem> wikiItemList){
        this.activity = activity;
        this.wikiItemList = wikiItemList;
        inflater = (LayoutInflater) activity.get().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return wikiItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return wikiItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Refreshes the listview
     * @param wikiItems
     */
    public void clearAddAllItems(List<WikiItem.QueryItem.SearchItem> wikiItems){
        wikiItemList.clear();
        wikiItemList.addAll(wikiItems);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.wiki_search_item_summary, null);
            MerchantViewHolder vh = new MerchantViewHolder(convertView);
            convertView.setTag(vh);
        }

        final WikiItem.QueryItem.SearchItem wikiItem = wikiItemList.get(position);
        MerchantViewHolder vh = (MerchantViewHolder) convertView.getTag();

        vh.searchItemTitle.setText(wikiItem.title);
        vh.searchItemLastEditedDate.setText(dateFormat.format(wikiItem.timestamp));


        return convertView;
    }

    private class MerchantViewHolder{
        TextView searchItemTitle;
        TextView searchItemLastEditedDate;
        public MerchantViewHolder(View view){
            searchItemTitle = (TextView) view.findViewById(R.id.wiki_search_item_title);
            searchItemLastEditedDate = (TextView) view.findViewById(R.id.wiki_item_last_edited_date);
        }
    }
}
