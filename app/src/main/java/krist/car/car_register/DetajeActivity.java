package krist.car.car_register;




import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import krist.car.MainActivity;
import krist.car.profile_info.Models.CarModel;
import krist.car.R;
import krist.car.utils.Helpers;

public class DetajeActivity extends AppCompatActivity  implements View.OnFocusChangeListener{


    private AutoCompleteTextView acModeli, acMarka, acNgjyra;
    private TextView zgjidhFoto;
    private EditText  txttarga;
    private Button btnNgarko;
    private ProgressBar mProgresBar;
    private Uri filePath;
    private Toolbar toolbar;
    private final int PICK_IMAGE_REQUEST = 1;
    private CarRegisterViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detaje);


        acMarka = findViewById(R.id.emailRegister);
        acModeli =  findViewById(R.id.passRegister);
        acNgjyra = findViewById(R.id.cardIdNumberRegister);
        zgjidhFoto = findViewById(R.id.btn_foto_personale_register);
        txttarga = findViewById(R.id.phoneRegister);
        btnNgarko = findViewById(R.id.buttonRegister);
        mProgresBar = findViewById(R.id.progres_detajet_activity);
        mProgresBar.setVisibility(View.INVISIBLE);
        toolbar = findViewById(R.id.toolbarRegjMakine);

        viewHideSoftKeyBoard();
        setupAdaptersForFields();
        setupListeners();
        setupRegisterCarViewModel();
    }

    private void setupRegisterCarViewModel() {
        viewModel = new ViewModelProvider(this).get(CarRegisterViewModel.class);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(view -> finish());
        zgjidhFoto.setOnClickListener(view -> chooseImage());
        btnNgarko.setOnClickListener(view -> attemptCarRegister());
    }

    private void setupAdaptersForFields() {
        String[] modeliArray = getResources().getStringArray(R.array.modelet_makina);
        String[] markaArray = getResources().getStringArray(R.array.marka_makina);
        String[] ngjyraArray = getResources().getStringArray(R.array.ngjyrat_makina);

        ArrayAdapter<String> adapterMarka = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, markaArray);
        ArrayAdapter<String> adapterModeli = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, modeliArray);
        ArrayAdapter<String> adapterNgjyra = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ngjyraArray);

        acMarka.setAdapter(adapterMarka);
        acModeli.setAdapter(adapterModeli);
        acNgjyra.setAdapter(adapterNgjyra);
    }

   private void attemptCarRegister() {
       String make = acMarka.getText().toString().trim();
       String carModel = acModeli.getText().toString().trim();
       String plate = txttarga.getText().toString().trim().toUpperCase();
       String color = acNgjyra.getText().toString().trim();

       CarModel model = new CarModel("", make, carModel, plate, color, "");

       if(!validateFields(model)){
           return;
       }

       uploadCarImg(model);
   }

   private void uploadCarImg(CarModel model) {
        viewModel.uploadImg(filePath, Helpers.getFileExtension(this, filePath));
        viewModel.isImgUploadedSuccessfully().observe(this, imgRef -> {
            if(!imgRef.isEmpty()) {
                model.setCarImgRef(imgRef);
                registerCarDetails(model);
            }
        });
   }

   private void registerCarDetails(CarModel model) {
       viewModel.registerCarNewWay(model);
       viewModel.isCarRegisterSuccessfully().observe(this, isSuccess -> {
            if(isSuccess) {
                Helpers.goToActivity(this, MainActivity.class);
                Helpers.showToastMessage(this, "Regjistrim i suksesshem");
            }else {
                Helpers.showToastMessage(this, "Pati nje problem ne regjistrim, provo perseri");
            }
        });
   }

   private Boolean validateFields(CarModel model) {
        if(model.getCarMarks().isEmpty()) {
            acMarka.setError("Zgjidhni marken");
            return false;
        }else if(model.getCarModel().isEmpty()) {
            acModeli.setError("Zgjidhni modelin");
            return false;
        }else if(model.getCarColor().isEmpty()){
            acNgjyra.setError("Zgjidhni ngjyren");
            return false;
        }else if(model.getCarPlate().isEmpty()) {
            txttarga.setError("Plotesoni targen");
            return false;
        }else if(filePath == null) {
            Helpers.showToastMessage(this, "Mungon foto e makines");
            return false;
        }else {
            return true;
        }
   }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            filePath = data.getData();
        }
    }

    private void hideKeyboard(View view){
        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    @Override
    public void onFocusChange(View view, boolean b) {
        if(!view.hasFocus()){
            hideKeyboard(view);
        }
    }

    private void viewHideSoftKeyBoard(){
        acMarka.setOnFocusChangeListener(this);
        acModeli.setOnFocusChangeListener(this);
        acNgjyra.setOnFocusChangeListener(this);
        txttarga.setOnFocusChangeListener(this);
    }
}