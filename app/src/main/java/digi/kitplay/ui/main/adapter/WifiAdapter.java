package digi.kitplay.ui.main.adapter;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import digi.kitplay.R;
import digi.kitplay.databinding.LayoutWifiItemBinding;
import lombok.Setter;


public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.WifiAdapterViewHolder> {

    @Setter
    List<ScanResult> wifiList;
    ConnectWifi callback;

    public interface ConnectWifi {
        /**
         * type = true -> require password
         * <br>
         *  type = false -> not require password
         * */
        void connect(String ssid,boolean type);
    }

    public WifiAdapter(@NonNull List<ScanResult> wifiList, @NonNull ConnectWifi callback ){
        this.wifiList = wifiList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public WifiAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutWifiItemBinding layout= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.layout_wifi_item, parent, false);
        return new WifiAdapterViewHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiAdapterViewHolder holder, int position) {
        if (position == 0){
            holder.layout.root.requestFocus();
        }
        holder.layout.setName(wifiList.get(position).SSID);
        holder.layout.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.connect(holder.layout.getName(),true);
            }
        });
        holder.layout.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public static class WifiAdapterViewHolder extends RecyclerView.ViewHolder {

        LayoutWifiItemBinding layout;

        public WifiAdapterViewHolder(LayoutWifiItemBinding layout) {
            super(layout.getRoot());
            this.layout = layout;
        }
    }
}

