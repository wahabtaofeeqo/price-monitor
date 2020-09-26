package com.taocoder.pricemonitor.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.taocoder.pricemonitor.models.ResponseInfo;
import com.taocoder.pricemonitor.models.User;

public class AccountViewModel extends ViewModel {
    private MutableLiveData<ResponseInfo<User>> loginResult = new MutableLiveData<>();
    private MutableLiveData<ResponseInfo<User>> registerResult = new MutableLiveData<>();

    private FirebaseFirestore firestore;

    public AccountViewModel() {
        firestore = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ResponseInfo<User>> getLoginResult() {
        return loginResult;
    }

    public MutableLiveData<ResponseInfo<User>> getRegisterResult() {
        return registerResult;
    }

    public void login(final String username, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    getUser(id);
                }
                else {
                    loginResult.setValue(new ResponseInfo<User>(true, "Login failed"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginResult.setValue(new ResponseInfo<User>(true, e.getMessage()));
            }
        });
    }

    public void createAccount(final String name, final String email, String password, final String type) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Save user details not users collection
                    User user = new User();
                    user.setId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    user.setName(name);
                    user.setEmail(email);
                    user.setType(type);
                    user.setStatus(1);
                    saveUser(user);
                }
                else {
                    registerResult.setValue(new ResponseInfo<User>(true, "Registration failed"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                registerResult.setValue(new ResponseInfo<User>(true, e.getMessage()));
            }
        });
    }

    private void saveUser(User user) {
        CollectionReference users = firestore.collection("users");
        users.document().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    registerResult.setValue(new ResponseInfo<User>(false));
                }
                else {
                    registerResult.setValue(new ResponseInfo<User>(true, "Could not save user Data. Try again."));
                }
            }
        });
    }

    private void getUser(String id) {
        CollectionReference users = firestore.collection("users");
        users.whereEqualTo("id", id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                   if (task.getResult().size() > 0) {
                       for (DocumentSnapshot snapshot : task.getResult()) {
                           User user = snapshot.toObject(User.class);
                           if (user != null) {
                               loginResult.setValue(new ResponseInfo<User>(false, user));
                           }
                       }
                   }
                   else {
                       loginResult.setValue(new ResponseInfo<User>(true, "Login failed"));
                   }
                }
                else {
                    loginResult.setValue(new ResponseInfo<User>(true, "Login not successful"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loginResult.setValue(new ResponseInfo<User>(true, e.getMessage()));
            }
        });
    }
}
