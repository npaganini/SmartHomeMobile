package grupo4.smarthome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by Esteban on 11/21/2017.
 */

public class CombosArrayAdapter extends ArrayAdapter<Combo> {
    public CombosArrayAdapter(Activity context, Combo[] objects) {
        super(context, R.layout.combos_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.combos_list_item, parent, false);
            holder = new ViewHolder();
            holder.nameTextView = (TextView) convertView.findViewById(R.id.combos_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Combo combo = getItem(position);
        //holder.nameTextView.setText(combo.getName());

        return convertView;
    }
}