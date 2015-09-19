package apps.rokuan.com.calliope_helper.fragment.store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import apps.rokuan.com.calliope_helper.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by LEBEAU Christophe on 19/09/2015.
 */
public class SignInFragment extends Fragment {
    @Bind(R.id.fragment_signin_login) protected EditText userLoginView;
    @Bind(R.id.fragment_signin_password) protected EditText userPasswordView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_signin, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.fragment_signin_connect)
    public void signIn(){
        String login = userLoginView.getText().toString();
        String password = userPasswordView.getText().toString();

        if(login.isEmpty()){
            return;
        }

        if(password.isEmpty()){
            return;
        }

        // TODO: acceder a la page POST pour se connecter
    }
}
