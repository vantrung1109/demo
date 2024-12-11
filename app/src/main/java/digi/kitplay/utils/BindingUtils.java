package digi.kitplay.utils;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import digi.kitplay.R;
import digi.kitplay.constant.Constants;

public class BindingUtils {

    @BindingAdapter({"order_state","order_locked_background","order_priority"})
    public static void orderState(View view, Integer status, Boolean isLocked, Integer orderPriority){
        if(isLocked != null && isLocked){
            view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
            return;
        }
        switch (status){
            case Constants.ORDER_STATE_WAITING:
            case Constants.ORDER_STATE_WORKING:
                if(orderPriority == 1){
                    view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.red));
                }else {
                    view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
                }
                break;
            case Constants.ORDER_STATE_DONE:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
                break;
            default:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
                break;
        }
    }

    @BindingAdapter({"note_background_by_state", "note_locked_background", "note_priority"})
    public static void backgroundColorOrderNote(View view, Integer status, Boolean isLocked, Integer orderPriority){
        if(isLocked != null && isLocked){
            view.setBackground(ContextCompat.getDrawable(view.getContext(),R.color.white));
            return;
        }
        switch (status){
            case Constants.ORDER_FOOD_STATUS_WAITING:
            case Constants.ORDER_FOOD_STATUS_WORKING:
                if(orderPriority == 1){
                    view.setBackground(ContextCompat.getDrawable(view.getContext(),R.color.red));
                }else {
                    view.setBackground(ContextCompat.getDrawable(view.getContext(),R.color.white));
                }
                break;
            case Constants.ORDER_FOOD_STATUS_DONE:
                view.setBackground(ContextCompat.getDrawable(view.getContext(),R.color.white));
            default:
                view.setBackground(ContextCompat.getDrawable(view.getContext(),R.color.white));
                break;
        }
    }

    @BindingAdapter({"state_view_background","state_view_locked_background"})
    public static void stateViewBackground(View view, Integer status, Boolean isLocked){
        if(isLocked != null && isLocked){
            view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.state_waiting_color));
            return;
        }
        if (status == null) {
            view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
            return;
        }
        switch (status){
            case Constants.ORDER_FOOD_STATUS_WAITING:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.yellow));
                break;
            case Constants.ORDER_FOOD_STATUS_DONE:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.app_light_color));
                break;
            case Constants.ORDER_FOOD_STATUS_WORKING:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.app_light_color));
                break;
            default:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.white));
                break;
        }
    }

    @BindingAdapter({"printer_total_time", "printer_status", "printer_timeRemaining"})
    public static void stateViewBackground(View view, long time, Integer status, Long timeRemaining){
        if (status == null) {
            view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.state_done_color));
            return;
        }
        switch (status){
            case Constants.ORDER_FOOD_STATUS_WORKING:
                if(timeRemaining >= time / 2){
                    view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.state_done_color));
                }else if(timeRemaining < time / 2 && timeRemaining >= time / 4){
                    view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.yellow));
                }else if(timeRemaining < time / 4){
                    view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.red));
                }
                break;
            case Constants.ORDER_FOOD_STATUS_DONE:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.green_color));
                break;
            case Constants.ORDER_FOOD_STATUS_WAITING:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.gray_color));
                break;
            default:
                view.setBackgroundTintList(ContextCompat.getColorStateList(view.getContext(), R.color.state_done_color));
                break;
        }
    }

    @BindingAdapter({"imageBase64"})
    public static void setImageBase64(ImageView imageView, Bitmap bitmap) {
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else {
            imageView.setImageResource(R.drawable.drink);
        }
    }

    @BindingAdapter("layout_constraintDimensionRatio")
    public static void setLayoutConstraintDimensionRatio(View view, String ratio) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        if (params != null && ratio != null) {
            params.dimensionRatio = ratio;
            view.setLayoutParams(params);
        }
    }

    @BindingAdapter({"imageOrderType"})
    public static void setImageOrderType(ImageView imageView, Integer orderType) {
        if(orderType == null){
            imageView.setImageResource(R.drawable.take_away);
            return;
        }
        switch (orderType){
            case Constants.ORDER_TYPE_DINE_IN:
                imageView.setImageResource(R.drawable.dine_in);
                break;
            case Constants.ORDER_TYPE_DELIVERY:
                imageView.setImageResource(R.drawable.devivery);
                break;
            case Constants.ORDER_TYPE_TAKE_OUT:
            default:
                imageView.setImageResource(R.drawable.take_away);
                break;
        }
    }


}
