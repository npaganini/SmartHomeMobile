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

public class ActionArrayAdapter extends ArrayAdapter<Action> {
    public ActionArrayAdapter(Activity context, Action[] objects) {
        super(context, R.layout.combos_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.combo_list_item, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Action action = getItem(position);
        holder.nameTextView.setText("Hola");

        return convertView;
    }
}