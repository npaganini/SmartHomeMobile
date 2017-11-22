package grupo4.smarthome;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by estebankramer on 18/11/2017.
 */

public class DeviceArrayAdapter extends ArrayAdapter<Device> {
    public DeviceArrayAdapter(Activity context, Device[] objects) {
        super(context, R.layout.device_list_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeviceViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_list_item, parent, false);
            holder = new DeviceViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.device_image);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.device_name);
            convertView.setTag(holder);
        } else {
            holder = (DeviceViewHolder) convertView.getTag();
        }

        Device device = getItem(position);
        holder.imageView.setImageResource(device.getImage());
        holder.nameTextView.setText(device.getName());

        return convertView;
    }
}
