package apps.rokuan.com.calliope_helper.fragment.store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import apps.rokuan.com.calliope_helper.R;
import apps.rokuan.com.calliope_helper.api.CalliopeStoreAPI;
import apps.rokuan.com.calliope_helper.api.OperationResult;
import apps.rokuan.com.calliope_helper.api.User;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;

/**
 * Created by LEBEAU Christophe on 19/09/2015.
 */
public class SignUpFragment extends Fragment {
    @Bind(R.id.fragment_signup_name) protected EditText accountNameView;
    @Bind(R.id.fragment_signup_login) protected EditText accountLoginView;
    @Bind(R.id.fragment_signup_email) protected EditText accountEmailView;
    @Bind(R.id.fragment_signup_password) protected EditText accountPasswordView;
    @Bind(R.id.fragment_signup_confirm_password) protected EditText accountPasswordConfirmView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_signup, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.fragment_signup_submit)
    public void submitAccount(){
        String name = accountNameView.getText().toString();
        String login = accountLoginView.getText().toString();
        String email = accountEmailView.getText().toString();
        String password = accountPasswordView.getText().toString();
        String password2 = accountPasswordConfirmView.getText().toString();

        if(name.trim().isEmpty()){
            return;
        }

        if(login.trim().isEmpty()){
            return;
        }

        if(email.trim().isEmpty()){
            return;
        }

        // TODO: mettre une taille minimale et un format pour le mot de passe

        if(password.isEmpty()){
            return;
        }

        if(password2.isEmpty()){
            return;
        }

        if(!password.equals(password2)){
            return;
        }

        User user = new User(name, email, login, password);

        CalliopeStoreAPI.getInstance().getService().createAccount(user).enqueue(new Callback<OperationResult>() {
            @Override
            public void onResponse(Response<OperationResult> response) {
                // TODO: savoir si le compte a ete cree
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
