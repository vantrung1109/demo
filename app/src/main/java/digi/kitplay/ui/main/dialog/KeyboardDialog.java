package digi.kitplay.ui.main.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableChar;
import androidx.databinding.ObservableField;
import androidx.fragment.app.DialogFragment;

import digi.kitplay.R;
import digi.kitplay.databinding.LayoutKeyboardBinding;

import lombok.Setter;
import timber.log.Timber;

public class KeyboardDialog extends DialogFragment implements View.OnClickListener {

    private String hint;
    private String submitText;
    public KeyboardDialog(String hint, String submitText){
        this.hint = hint;
        this.submitText = submitText;
    }
    public KeyboardDialog(){}

    private boolean lock = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id._login:
                listener.login(code.get());
                dismiss();
                break;
            case R.id._zero:
                code.set(code.get()+"0");
                break;
            case R.id._one:
                code.set(code.get()+"1");
                break;
            case R.id._two:
                code.set(code.get()+"2");
                break;
            case R.id._three:
                code.set(code.get()+"3");
                break;
            case R.id._four:
                code.set(code.get()+"4");
                break;
            case R.id._five:
                code.set(code.get()+"5");
                break;
            case R.id._six:
                code.set(code.get()+"6");
                break;
            case R.id._seven:
                code.set(code.get()+"7");
                break;
            case R.id._eight:
                code.set(code.get()+"8");
                break;
            case R.id._nine:
                code.set(code.get()+"9");
                break;
            case R.id.backspace:
                int l = code.get().length() - 1;
                if (l < 0 ){
                    break;
                }
                code.set(code.get().substring(0, l > 0 ? l : 0));
                break;
            default:
                break;
        }
    }

    public interface KeyboardListener{
        void login(String code);
    }
    @Setter
    private KeyboardListener listener;
    private final ObservableField<String> code = new ObservableField<>("");
    public final ObservableField<Boolean> isVisibility = new ObservableField<>(false);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LayoutKeyboardBinding layoutKeyboardBinding = LayoutKeyboardBinding.inflate(inflater);
        layoutKeyboardBinding.setCode(code);
        if (hint!=null && submitText!=null){
            layoutKeyboardBinding.setHint(hint);
            layoutKeyboardBinding.setSubmitText(submitText);
        }
        layoutKeyboardBinding.setFragment(this);
        layoutKeyboardBinding.executePendingBindings();
        isVisibility.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                int currentCursor = layoutKeyboardBinding.edtPw.getSelectionStart();
                if(!isVisibility.get()){

                    layoutKeyboardBinding.edtPw.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    layoutKeyboardBinding.edtPw.setSelection(currentCursor);
                }else {
                    layoutKeyboardBinding.edtPw.setTransformationMethod(null);
                    layoutKeyboardBinding.edtPw.setSelection(currentCursor);
                }
            }
        });
        return layoutKeyboardBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        lock = false;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        if (listener == null) {
            try {
                // Instantiate the mListener so we can send events to the host
                listener = (KeyboardListener) context;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(context
                        + " must implement KeyboardListener");
            }
        }

    }

    public void setIsVisibilityPassword(){
        isVisibility.set(Boolean.FALSE.equals(isVisibility.get()));
    }

}
