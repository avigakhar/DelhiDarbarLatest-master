package com.app.delhidarbar.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.app.delhidarbar.R;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;

public abstract class GenericSpinnerSingleItemAdapter<T> extends ArrayAdapter<T> {
    private List<T> items;
    private int layoutRes;
    private int dropDownLayoutRes;
    private LayoutInflater layoutInflater;
    private static final String TAG = GenericSpinnerSingleItemAdapter.class.getSimpleName();

    public GenericSpinnerSingleItemAdapter(Context context) {
        this(context, R.layout.spinner_layout, R.layout.simple_spinner_dropdown_item, new ArrayList<>());
    }

    public GenericSpinnerSingleItemAdapter(Context context, List<T> items) {
        this(context, R.layout.spinner_layout, R.layout.simple_spinner_dropdown_item, items);
    }

    public GenericSpinnerSingleItemAdapter(Context context, int resource, int dropDownResource, List<T> items) {
        super(context, resource, items);

        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutRes = resource;
        this.items = items;
        this.dropDownLayoutRes = dropDownResource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getItemView(position, convertView, parent, false);
    }

    @Override
    public T getItem(int position) {
        if (items == null)
            return null;

        if (position < 0 || position >= items.size())
            return null;

        return items.get(position);
    }

    @Override
    public int getCount() {
        if (items == null) return 0;

        return items.size();
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return getItemView(position, convertView, parent, true);
    }

    private View getItemView(int position, View convertView, ViewGroup parent, boolean isDropdown) {
        ItemViewHolder itemViewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(layoutRes, parent, false);
            itemViewHolder = new ItemViewHolder(convertView);
            convertView.setTag(itemViewHolder);

        } else {
            itemViewHolder = (ItemViewHolder) convertView.getTag();
        }

        itemViewHolder.applyData(position, isDropdown);
        return convertView;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void clear() {
        if (items == null) {
            items = new ArrayList<>();
        }

        items.clear();
        notifyDataSetChanged();
    }

    public abstract String getTextFor(T t);

    public int getTextColorRes(boolean isDropdown) {
        return -1;
    }

    class ItemViewHolder {
        View itemLayout;
        TextView textView;

        ItemViewHolder(View itemLayout) {
            this.itemLayout = itemLayout;
            this.textView = itemLayout.findViewById(android.R.id.text1);
        }

        private void applyData(int position, boolean isDropdown) {
            T t = getItem(position);

            if (t != null) {
                itemLayout.setVisibility(View.VISIBLE);
                textView.setText(getTextFor(t));

                if (getTextColorRes(isDropdown) != -1) {
                    textView.setTextColor(getTextColorRes(isDropdown));
                }
            } else {
                itemLayout.setVisibility(View.GONE);
            }
        }
    }
}
