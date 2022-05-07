package com.aghajari.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aghajari.test.model.User;
import com.aghajari.xmlbypass.XmlByPass;
import com.aghajari.xmlbypass.XmlLayout;

/*
@XmlByPassAttr(name = "app:bufferType", format = "string", enums = {
        @XmlByPassAttrEnum(key = "normal", value = "1")
})
*/
@XmlByPass(layouts = {
        @XmlLayout(layout = "activity_main", className = "ActivityMain"),
        @XmlLayout(layout = "Test", className = "SmileView"),
})
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ActivityMain(this));

        ActivityMainViewModel viewModel = new ViewModelProvider(this).get(ActivityMainViewModel.class);
        viewModel.user.setValue(new User("Aghajari"));
        viewModel.myText.setValue("Awesome!");
    }

    public void hello(View view) {
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
    }
}