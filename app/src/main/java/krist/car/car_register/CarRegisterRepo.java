package krist.car.car_register;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import krist.car.api.ApiModule;
import krist.car.models.RegisterCarModel;
import krist.car.profile_info.Models.CarModel;

public class CarRegisterRepo {
    private ApiModule api;

    public CarRegisterRepo() {
        api = ApiModule.getInstance();
    }

    public MutableLiveData<Boolean> registerCarDetails(RegisterCarModel model) {
        final MutableLiveData<Boolean> isCarRegisterSuccessfully = new MutableLiveData<>();

        String userId = api.getUserUId();

        api.getDatebaseReferenceToThisEndPoint("user_cars").child(userId + "/cars").setValue(model)
                .addOnCompleteListener(task -> {
                    isCarRegisterSuccessfully.setValue(task.isComplete());
                });
        return isCarRegisterSuccessfully;
    }

    public MutableLiveData<String> uploadPhoto(Uri imgFilePath, String fileExtension) {
        final MutableLiveData<String> imgUri = new MutableLiveData<>();
        StorageReference reference = api.getFirebaseStorageToThisEndPoint("uploads").child(System.currentTimeMillis() + "." + fileExtension);

        reference.putFile(imgFilePath).continueWithTask( task -> {
            if(!task.isComplete()) {
                throw Objects.requireNonNull(task.getException());
            }else {
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener( task -> {
            String imageUri = task.getResult().toString();
            imgUri.setValue(imageUri);
        });
        return imgUri;
    }

    public MutableLiveData<Boolean> addCar(CarModel carModel) {
        final MutableLiveData<Boolean> isCarRegisterSuccessfully = new MutableLiveData<>();

        String userId = api.getUserUId();

        DatabaseReference ref = api.getDatebaseReferenceToThisEndPoint("user_cars").child(userId).child("cars").push();

        carModel.setCarKey(ref.getKey());

        ref.setValue(carModel).addOnCompleteListener(task -> {
            isCarRegisterSuccessfully.setValue(task.isSuccessful());
        });
        return isCarRegisterSuccessfully;
    }
}
